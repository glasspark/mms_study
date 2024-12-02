$(document).ready(function() {

	getFileList(); //공유 파일 리스트 
	getMemberList();//멤버들의 리스트
	getApplicationLists(); //일정 리스트
	getBoardList(); //게시판 리스트 가져오기

	// 작성하기 버튼 클릭 시 페이지 이동 이벤트
	$('#createPostBtn').on('click', function() {
		let num = $('#groupId').val(); // groupId 값 가져오기
		window.location.href = `/mystudy/detail/board/${num}`;
	});

	//사이드바
	const defaultSection = localStorage.getItem('activeSection') || 'study';
	$(`#sidebar .nav-link[data-target="${defaultSection}"]`).addClass('active');
	$(`#${defaultSection}`).removeClass('d-none');

	$('#sidebar .nav-link').on('click', function(event) {
		event.preventDefault();

		// Remove active class from all nav links
		$('#sidebar .nav-link').removeClass('active');

		// Add active class to the clicked nav link
		$(this).addClass('active');

		// Hide all content sections
		$('.content-section').addClass('d-none');

		// Show the targeted content section
		const target = $(this).data('target');
		$('#' + target).removeClass('d-none');

		//target =>  study members schedule resources board application delete

		// Store the active section in localStorage
		localStorage.setItem('activeSection', target);

		if (target === 'members') {
			getMemberList();//멤버들의 리스트
		}

		if (target === 'resources') {
			getFileList(); //공유 파일 리스트 
		}

		if (target === 'board') {
			getBoardList();
		}

		// If the schedule section is clicked, adjust the calendar size
		if (target === 'schedule') {
			calendar.updateSize(); // 크기 재계산
			getApplicationLists(); //일정 리스트

		}

	});

	//스터디 그룹 삭제 
	$('#deleteStudyBtn').click(function() {
		let num = $('#groupId').val();
		$.ajax({
			url: '/api/group/detail/study?groupId=' + num,
			type: 'DELETE',
			success: function(response) {
				if (response.status === 'success') {
					alert(response.message);
					window.location.href = '/mystudy';
				} else {
					alert(response.message);
				}
			},
			error: function() {
				alert('오류가 발생했습니다. 다시 시도해주세요.');
			}
		});
	});

	//공유 파일 업로드 버튼 클릭 시 파일 업로드 처리
	$('#uploadButton').click(function() {
		let formData = new FormData();
		let title = $('#fileTitle').val();
		let file = $('#groupFile')[0].files[0];
		let num = $('#groupId').val();

		if (!file) {
			alert("파일을 선택해주세요.");
			return;
		}

		formData.append('title', title);
		formData.append('file', file);
		formData.append('groupId', num);

		$.ajax({
			url: '/api/group/detail/upload/file',
			type: 'POST',
			data: formData,
			processData: false,
			contentType: false,
			success: function(response) {
				if (response.status === 'success') {
					alert('파일이 성공적으로 업로드되었습니다.');
					$('#uploadModal').modal('hide'); // 업로드 성공 시 모달 닫기
					getFileList() //공유 파일 리스트 다시 불러오기
				} else {
					alert('파일 업로드에 실패했습니다. 다시 시도해주세요.');
				}
			},
			error: function() {
				alert('오류가 발생했습니다. 다시 시도해주세요.');
			}
		});
	});


	//presentation , fc-scrollgrid-sync-table
	var calendarEl = $('#calendar')[0];  // jQuery로 요소를 선택하고 DOM 요소를 가져옵니다.
	var calendar = new FullCalendar.Calendar(calendarEl, {
		//	contentHeight: 450,
		initialView: 'dayGridMonth',
		headerToolbar: {
			left: 'prev,next today',
			center: 'title',
			right: 'dayGridMonth'
		},
		selectable: true,
		//events: '/api/events', // 서버에서 기존 이벤트 가져오기
		select: function(info) {
			const title = prompt('새로운 일정을 입력하세요:');
			if (title) {
				let newEvent = { // newEvent 객체 선언 및 초기화
					title: title,
					start: info.startStr,
					end: info.endStr
				};
				// 서버에 이벤트 저장
				saveEventToServer(newEvent, function(savedEvent) {
					calendar.addEvent(savedEvent); // 서버 저장 후 캘린더 
				});
			}
			//	calendar.unselect(); // 선택 해제
		},
		datesSet: function(info) { // 날짜가 설정될 때마다 (뷰가 바뀔 때마다) 실행
			//datesSet :  날짜가 설정될 때마다 (뷰가 바뀔 때마다) 실행
			/*let currentYear = info.start.getFullYear();
			let currentMonth = info.start.getMonth() + 1;  // 월은 0부터 시작하므로 +1*/
			getLEventListsToServer(calendar)
			//console.log(info.startStr);
		},
		eventChange: function(info) {
			// 이벤트 수정 처리
			var updatedEvent = {
				id: info.event.id,
				title: info.event.title,
				start: info.event.start.toISOString(),
				end: info.event.end ? info.event.end.toISOString() : null
			};
			updateEventOnServer(updatedEvent);
		},
		eventClick: function(info) {
			// 일정 클릭 시 삭제 확인
			if (confirm('정말 이 일정을 삭제하시겠습니까?')) {
				// 서버에 삭제 요청 보내기
				deleteEventFromServer(info.event.id, function(success) {
					if (success) {
						// 삭제가 성공적이면 UI에서 일정 제거
						info.event.remove();
						alert('일정이 삭제되었습니다.');
					} else {
						alert('일정 삭제에 실패했습니다.');
					}
				});
			}
		}
	});
	calendar.render(); // 캘린더 렌더링
	getLEventListsToServer(calendar);

	//서버에서 등록된 일정 가져오기 
	function getLEventListsToServer(calendar) {
		let num = $('#groupId').val();

		// 현재 보이는 캘린더의 날짜 가져오기
		let currentDate = calendar.getDate();

		// Date 객체에서 년도와 월 가져오기
		let currentYear = currentDate.getFullYear();
		let currentMonth = currentDate.getMonth() + 1; // 월은 0부터 시작하므로 +1을 해줘야 실제 월이 됨


		$.ajax({
			url: '/api/group/detail/get/schedule', // 서버 저장 엔드포인트
			type: 'GET',
			contentType: 'application/json',
			data: {
				groupId: num,
				month: currentMonth,
				year: currentYear
			},
			success: function(response) {
				//		console.log(response);
				let schedules = response.data;
				clearCalendarEvents(calendar);// 모든 기존 이벤트 삭제

				setEventDatas(schedules);
			},
			error: function() {
				alert('Failed to save the event.');
			}
		});

	}

	// 기존 이벤트 삭제 함수
	function clearCalendarEvents(calendar) {
		calendar.getEvents().forEach(event => event.remove()); // 모든 기존 이벤트 삭제
	}


	//데이터 넣기 
	function setEventDatas(schedules) {
		schedules.forEach(function(schedule) {
			calendar.addEvent({
				id: schedule.id,
				title: schedule.title,
				start: schedule.startDate,
				end: schedule.endDate,
				groupId: schedule.groupId
			});
		});
	}


	//저장
	function saveEventToServer(eventData, callback) {

		let num = $('#groupId').val(); // groupId 값 가져오기
		// eventData에 groupId 추가
		eventData.groupId = num;

		$.ajax({
			url: '/api/group/detail/add/schedule', // 서버 저장 엔드포인트
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(eventData),
			success: function(response) {
				if (response.status === "success") {
					let savedEvent = response.data;

					// 서버에서 받은 데이터를 이용해 캘린더에 새 일정 추가
					if (callback) callback({
						id: savedEvent.id,
						title: savedEvent.title,
						start: savedEvent.startDate,
						end: savedEvent.endDate
					});
				}
			},
			error: function() {
				alert('Failed to save the event.');
			}
		});
	}


	//수정
	/*	function updateEventOnServer(eventData) {
			$.ajax({
				url: '/api/events/' + eventData.id, // 수정할 이벤트 ID
				type: 'PUT',
				contentType: 'application/json',
				data: JSON.stringify(eventData),
				success: function() {
					alert('Event updated successfully.');
				},
				error: function() {
					alert('Failed to update the event.');
				}
			});
		}
	*/
	//삭제
	function deleteEventFromServer(eventId, callback) {
		let num = $('#groupId').val(); // groupId 값 가져오기
		// eventData에 groupId 추가
		$.ajax({
			url: '/api/group/detail/delete/schedule', // 삭제할 이벤트 ID
			type: 'POST',
			data: {
				groupId: num,
				scheduleId: eventId
			},
			success: function(response) {
				console.log(response);
				if (response.status === "success") {
					callback(true);
				} else {
					alert('삭제 실패: ' + response.message);
					callback(false);
				}
			},
			error: function() {
				alert('일정 삭제에 실패했습니다.');
				callback(false);
			}
		});
	}
});


//가입 신청 리스트 조회 
function getApplicationLists() {
	let num = $('#groupId').val();

	$.ajax({
		url: '/api/group/detail/get/application', // 삭제할 이벤트 ID
		type: 'GET',
		data: {
			groupId: num
		},
		success: function(response) {
			if (response.status === "success") {
				setApplicationLists(response.data);
			} else {
				alert('삭제 실패: ' + response.message);
			}
		},
		error: function() {
			alert('일정 삭제에 실패했습니다.');
			callback(false);
		}
	});
}

// 가입 신청 리스트 적용 (테이블 형식)
function setApplicationLists(data) {
	// 테이블 컨테이너 참조
	let applicationListContainer = $('#applicationListContainer');
	applicationListContainer.empty(); // 기존 리스트 제거

	// 테이블 구조 생성
	let table = `
        <table class="table table-bordered table-striped">
            <thead>
                <tr>
                    <th>사용자명</th>
                    <th>닉네임</th>
                    <th>신청 일시</th>
                    <th>상태</th>
                    <th>관리</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    `;

	applicationListContainer.append(table);

	// 데이터가 있는 경우 테이블에 항목을 추가합니다.
	if (data && data.length > 0) {
		let tableBody = applicationListContainer.find('tbody');
		data.forEach(item => {
			// requestedAt 날짜 형식을 변환
			let requestedDate = new Date(item.requestedAt);
			let formattedDate = requestedDate.getFullYear() + "-" +
				String(requestedDate.getMonth() + 1).padStart(2, '0') + "-" +
				String(requestedDate.getDate()).padStart(2, '0') + " " +
				String(requestedDate.getHours()).padStart(2, '0') + ":" +
				String(requestedDate.getMinutes()).padStart(2, '0') + ":" +
				String(requestedDate.getSeconds()).padStart(2, '0');

			let row = `
                <tr>
                    <td>${item.username}</td>
                    <td>${item.nickname}</td>
                    <td>${formattedDate}</td>
                    <td>${item.status}</td>
                    <td>
                        <button class="btn btn-primary btn-sm approve-btn" data-request-id="${item.requestId}">승인</button>
                        <button class="btn btn-danger btn-sm reject-btn" data-request-id="${item.requestId}">거절</button>
                    </td>
                </tr>
            `;
			tableBody.append(row);
		});

		// 승인 버튼 및 거절 버튼 클릭 이벤트 핸들러 추가
		$('.approve-btn').click(function() {
			let requestId = $(this).data('request-id');
			processApplication(requestId, 'APPROVED');
		});

		$('.reject-btn').click(function() {
			let requestId = $(this).data('request-id');
			processApplication(requestId, 'REJECTED');
		});
	} else {
		// 데이터가 없는 경우 "신청자가 없습니다" 표시
		applicationListContainer.append('<p>현재 가입 신청자가 없습니다.</p>');
	}
}

// 가입 신청 승인 및 거절 처리
function processApplication(requestId, status) {
	let num = $('#groupId').val();

	$.ajax({
		url: '/api/group/detail/application/process',
		type: 'POST',
		data: {
			groupId: num,
			requestId: requestId,
			status: status
		},
		success: function(response) {
			if (response.status === "success") {
				alert(response.message);
				getApplicationLists(); // 처리 후 리스트 다시 조회
			} else {
				alert('처리 실패: ' + response.message);
			}
		},
		error: function() {
			alert('가입 신청 처리에 실패했습니다.');
		}
	});
}


//가입 신청 리스트 조회 
function getFileList() {
	let num = $('#groupId').val();

	$.ajax({
		url: '/api/group/detail/get/file/list',
		type: 'GET',
		data: {
			groupId: num
		},
		success: function(response) {
			if (response.status === "success") {
				setFileList(response.data);
			} else {
				alert(response.message);
			}
		},
		error: function() {
			alert('공유 파일 조회에 실패하였습니다.');
		}
	});
}


//가입 신청 리스트 조회 
function setFileList(data) {

	$('#fileList').empty(); // 기존 리스트 비우기

	// 파일 리스트 생성
	data.forEach(file => {
		// 파일 경로 앞에 /upload 추가
		let filePath = `/upload${file.filePath}`;

		// 날짜 포맷 수정: 'T'를 공백으로 대체하여 읽기 쉽게 변경
		let formattedDate = file.createdAt.replace('T', ' ');

		// 삭제 버튼 추가 (등록자인 경우에만 표시)
		let deleteButton = file.isRegistrant ? `<button class="btn btn-sm btn-danger ms-2" onclick="deleteFile(${file.id})">삭제</button>` : '';

		let listItem = `
            <li class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <strong>${file.title}</strong>
                        <div class="d-flex text-muted">
                            <p class="mb-0 me-3"> ${file.nickname}</p>
                            <p class="mb-0"> ${formattedDate}</p>
                        </div>
                    </div>
                    <div>
                        <a href="${filePath}" class="btn btn-sm btn-primary" download="${file.fileName}">다운로드</a>
                        ${deleteButton} <!-- 삭제 버튼 -->
                    </div>
                </div>
            </li>`;
		$('#fileList').append(listItem);
	});
}

// 파일 삭제 요청 함수
function deleteFile(fileId) {
	if (confirm('정말로 이 파일을 삭제하시겠습니까?')) {

		let num = $('#groupId').val();
		$.ajax({
			url: `/api/group/detail/delete/file`, // 파일 삭제 API 엔드포인트
			type: 'POST',
			data: {
				groupId: num,
				fileId: fileId
			},
			success: function(response) {
				if (response.status === 'success') {
					alert(response.message);
					getFileList(); // 삭제 후 파일 리스트 갱신
				} else {
					alert(response.message);
				}
			},
			error: function() {
				alert('파일 삭제 중 오류가 발생했습니다. 다시 시도해주세요.');
			}
		});
	}
}


//스터디 그룹 멤버 리스트 반환
function getMemberList() {
	let num = $('#groupId').val();

	$.ajax({
		url: '/api/group/detail/get/member/list',
		type: 'GET',
		data: {
			groupId: num
		},
		success: function(response) {
			if (response.status === "success") {
				setMemberList(response);
			} else {
				alert(response.message);
			}
		},
		error: function() {
			alert('공유 파일 조회에 실패하였습니다.');
		}
	});
}

function setMemberList(responseData) {
	$('#memberList').empty(); // 기존 리스트 비우기

	// 로그인한 사용자가 그룹의 리더인지 여부
	let isCurrentUserLeader = responseData.isCurrentUserLeader;

	// 멤버 리스트 생성 (responseData.data는 멤버 리스트 배열)
	if (Array.isArray(responseData.data)) {
		responseData.data.forEach(member => {
			// null 값은 건너뛰기
			if (member === null) {
				return;
			}

			// 이미지 경로 설정
			let imgPath = member.imgType ? `/upload${member.imgPath}` : member.imgPath;

			// 역할 한글로 변환 및 아이콘 설정
			let roleText;
			let roleIcon;
			switch (member.role) {
				case 'LEADER':
					roleText = '방장';
					roleIcon = '<i class="fas fa-crown text-warning"></i>'; // 방장 아이콘 (왕관)
					break;
				case 'SUB_LEADER':
					roleText = '부방장';
					roleIcon = '<i class="fas fa-star text-primary"></i>'; // 부방장 아이콘 (별)
					break;
				default:
					roleText = '멤버';
					roleIcon = ''; // 멤버는 아이콘 없음
			}

			// 탈퇴 버튼 생성 (현재 사용자가 리더인 경우, 본인 외 멤버에 대해서만 표시)
			let withdrawButton = '';
			if (isCurrentUserLeader && !member.isLeader) {
				withdrawButton = `<button class="btn btn-sm btn-danger ms-2" onclick="withdrawMember(${member.userId})">탈퇴</button>`;
			}

			// 멤버 리스트 항목 생성
			let listItem = `
                <li class="list-group-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div class="d-flex align-items-center">
                            <img src="${imgPath}" alt="프로필 이미지" class="rounded-circle me-3" width="40" height="40">
                            <div>
                                <strong>${member.nickname}</strong>
                                <p class="mb-0 text-muted">${roleIcon} ${roleText}</p> <!-- 역할과 아이콘 표시 -->
                            </div>
                        </div>
                        ${withdrawButton} <!-- 탈퇴 버튼 표시 -->
                    </div>
                </li>`;
			$('#memberList').append(listItem);
		});

		// 탈퇴 버튼 클릭 이벤트 추가
		$('.withdraw-button').on('click', function() {
			let userId = $(this).data('user-id');
			withdrawMember(userId);
		});

	} else {
		console.error("문제가 발생하였습니다.");
	}
}


//멤버 탈퇴 처리 
function withdrawMember(userId) {

	let num = $('#groupId').val();

	if (confirm('정말로 이 멤버를 탈퇴시키겠습니까?')) {
		$.ajax({
			url: `/api/group/detail/delete/member/withdraw`, // 탈퇴 API 엔드포인트
			type: 'POST',
			data: {
				groupId: num,
				userId: userId
			},
			success: function(response) {
				if (response.status === 'success') {
					alert(response.message);
					// 멤버 리스트 다시 로드
					getMemberList(); // 멤버 리스트를 갱신하기 위해 다시 로드
				} else {
					alert(response.message);
				}
			},
			error: function() {
				alert('탈퇴 처리에 실패했습니다.');
			}
		});
	}
}

//게시판 리스트 가져오기
function getBoardList(page) {

	// 페이지 번호가 없거나 유효하지 않으면 0으로 설정
	if (page == null || isNaN(page)) {
		page = 0;
	}

	let num = $('#groupId').val();

	$.ajax({
		url: '/api/group/detail/board/lists',
		type: 'GET',
		data: {
			page: page,
			groupId: num
		},
		success: function(response) {
			if (response.status === "success") {
				setBoardList(response);
				renderPagination(response.totalPages, response.number);
			} else {
				alert(response.message);
			}
		},
		error: function() {
			alert('공유 파일 조회에 실패하였습니다.');
		}
	});
}

function setBoardList(response) {

	$("#boardList").empty();

	let data = response.data;

	$.each(data, function(index, item) {
		const listItem = `
            <li class="list-group-item d-flex justify-content-between align-items-center board-item" data-id="${item.id}" data-group-id="${item.groupId}">
                <span>${item.title}</span>
                <small>${item.nickName}</small>
            </li>`;
		$("#boardList").append(listItem);
	});


	// 게시글 클릭 이벤트
	$(".board-item").click(function() { // 중괄호 사용
		const boardId = $(this).data("id"); // 게시글 ID 가져오기
		const groupId = $(this).data("group-id"); // 그룹 ID 가져오기
		window.location.href = `/mystudy/detail/board/${groupId}/${boardId}`; // 상세 페이지로 이동
	});
}

// 페이징 버튼 생성
function renderPagination(totalPages, currentPage) {
	$(".paginationSection").empty(); // 기존 버튼 지움

	// 이전 버튼
	if (currentPage > 0) {
		const prevButton = $(`<button class="btn btn-secondary me-2">이전</button>`);
		prevButton.click(() => getBoardList(currentPage - 1));
		$(".paginationSection").append(prevButton);
	}

	// 페이지 번호 버튼
	for (let i = 0; i < totalPages; i++) {
		const pageButton = $(`<button class="btn ${i === currentPage ? "btn-primary" : "btn-outline-primary"} me-2">${i + 1}</button>`);
		pageButton.click(() => getBoardList(i));
		$(".paginationSection").append(pageButton);
	}

	// 다음 버튼
	if (currentPage < totalPages - 1) {
		const nextButton = $(`<button class="btn btn-secondary">다음</button>`);
		nextButton.click(() => getBoardList(currentPage + 1));
		$(".paginationSection").append(nextButton);
	}
}

