$(document).ready(function() {


	// 닉네임 변경 감지(닉네임만 변경하는 경우 처리)
	$('#nickname').on('input', function() {
		$('#saveBtn').prop('disabled', false).attr('data-type', 'nickname');
	});

	// 사용자 지정 이미지 업로드
	$('#changePhotoButton').on('click', function() {
		$('#photoInput').click();
	});

	$('#photoInput').on('change', function(event) {
		const file = event.target.files[0];
		if (file) {
			const reader = new FileReader();
			reader.onload = function(e) {
				$('#userImg').attr('src', e.target.result);
			};
			reader.readAsDataURL(file);
		}

		// Submit 버튼 활성화 및 텍스트 변경
		$('#saveBtn').prop('disabled', false).attr('data-type', 'photo');
	});

	// 랜덤 이미지 목록
	const images = ['/img/cat.png', '/img/defaultImg.png', '/img/dog.png', '/img/panda.png'];
	let previousImage = '';

	$('#randomImg').on('click', function() {
		let randomImage;

		// 이전 이미지와 다른 이미지를 선택할 때까지 반복
		do {
			randomImage = images[Math.floor(Math.random() * images.length)];
		} while (randomImage === previousImage);

		// 선택된 이미지를 저장
		previousImage = randomImage;

		// 이미지 변경 코드 (img 태그의 src 속성 변경, ID가 'userImg'인 태그에 적용)
		$('#userImg').attr('src', randomImage);

		// Submit 버튼 활성화 및 텍스트 변경
		$('#saveBtn').prop('disabled', false).attr('data-type', 'random');
	});

	// 저장 버튼 클릭 시 동작
	$('#saveBtn').on('click', function() {

		//이미지 타입에 따라서 다른 경로로 보낼 것
		const type = $(this).attr('data-type');
		if (type === 'photo') {

			// FormData 객체 생성
			let formData = new FormData();
			let imageFile = $('#photoInput')[0].files[0]; // 이미지 파일 가져오기
			let nickname = $('#nickname').val(); // 닉네임 가져오기

			// FormData에 데이터 추가
			if (imageFile) {
				formData.append('imagePath', imageFile);
			}
			formData.append('nickname', nickname);

			// AJAX 요청
			$.ajax({
				type: 'POST',
				url: '/api/user/save/info',
				data: formData,
				processData: false, // 기본적으로 jQuery는 데이터를 쿼리 문자열로 변환하지만, 이를 방지
				contentType: false, // FormData 전송을 위해 기본 Content-Type 설정 해제
				success: function(response) {
					// 응답 처리
					alert(response.message);
				},
				error: function(error) {
					console.log('데이터 전송 중 오류가 발생했습니다.', error);
					alert('저장에 실패했습니다. 다시 시도해주세요.');
				}
			});

		} else if (type === 'random') {

			// 랜덤 이미지 정보 전송
			$.ajax({
				type: 'POST',
				url: '/api/user/save/random/info',
				data: { imagePath: $('#userImg').attr('src'), nickname: $('#nickname').val() },
				success: function(response) {
					alert(response.message);
				},
				error: function(error) {
					alert(error.message);
				}
			});
			console.log("랜덤 이미지 저장 버튼이 클릭되었습니다.");
		} else if (type === 'nickname') {
			// 닉네임만 변경 시
			$.ajax({
				type: 'POST',
				url: '/api/user/save/nickname/info',
				data: { nickname: $('#nickname').val() },
				success: function(response) {
					alert(response.message);
				},
				error: function(error) {
					console.log('닉네임 변경 중 오류가 발생했습니다.', error);
					alert('닉네임 저장에 실패했습니다. 다시 시도해주세요.');
				}
			});
			console.log("닉네임만 변경하여 저장 버튼이 클릭되었습니다.");
		}
	});


	// ===== 내 정보 - 비밀번호 변경 =====
	// 사용자 지정 이미지 업로드
	$('#changePasswordBtn').on('click', function(event) {
		event.preventDefault();
		let form = $('#pwForm')[0]; // 폼 요소 가져오기

		// 비밀번호 변경
		$.ajax({
			url: '/api/user/info',
			type: 'PATCH', // PATCH 요청
			data: $(form).serialize(),
			success: function(response) {
				if (response.status === "success") {
					alert(response.message);
				}
				alert(response.message);
			},
			error: function(error) {
				console.log('닉네임 변경 중 오류가 발생했습니다.', error);
				alert('닉네임 저장에 실패했습니다. 다시 시도해주세요.');
			}
		});

	});



	// ===== 스터디 ======

	// 내 스터디 목록
	$('#showStudy, #myLists').on('click', function() {
		getStudyLists();
	});

	// 신청한 스터디 
	$('#showApplied').on('click', function() {
	 getAppliedLists()
	});





	// ===== 1:1 문의 관련 API =====

	// 페이지 로드 시 기본 데이터 로드
	loadInquiries(1);

	// 페이징 버튼 클릭 이벤트
	$('#pagination').on('click', '.page-link', function() {
		const page = $(this).data('page');
		if (page >= 1) {
			loadInquiries(page); // 클릭한 페이지로 로드
		}
	});

	// 삭제 버튼 클릭 이벤트 핸들러
	$(document).on('click', '#deleteBtn', function() {
		const inquiryId = $(this).data('id');  // 버튼의 data-id 속성에서 id를 가져옴
		const currentPage = $('#pagination .page-item.active .page-link').data('page'); // 현재 페이지 추적


		// AJAX 요청 예시
		$.ajax({
			type: "POST",
			url: "/api/user/delete/inquiry",  // 수정할 API 엔드포인트
			data: { inquiryId: inquiryId },
			success: function(response) {

				if (response.status === 'success') {
					alert(response.message);
					loadInquiries(currentPage);
				} else {
					alert(response.message);
				}
			},
			error: function(error) {
				console.error("수정 요청 중 오류 발생:", error);
			}
		});
	});
});


// AJAX 요청으로 문의 리스트 로드
function loadInquiries(page) {

	$.ajax({
		type: 'GET',
		url: '/api/user/get/inquiries',
		data: { page: page },
		success: function(response) {
			if (response.data.length === 0 && page > 1) {
				// 현재 페이지에 데이터가 없고 페이지 번호가 1보다 크면 이전 페이지로 이동
				page--;
				loadInquiries(page); // 이전 페이지로 다시 요청
			} else {
				renderInquiries(response.data); // 데이터 표시
				createPagination(response.totalPages, page); // 페이징
			}
		},
		error: function(xhr, status, error) {
			console.error("데이터 로드 중 오류 발생:", error);
		}
	});
}

// 문의 리스트 렌더링
function renderInquiries(inquiries) {
	const inquiryContainer = $("#InquiryAccordion");
	inquiryContainer.empty();

	inquiries.forEach((inquiry) => {
		const statusText = inquiry.status === "WAITING" ? "대기중" : "답변완료"; // 상태 변환
		const statusClass = inquiry.status === "WAITING" ? "bg-warning" : "bg-success"; // 상태에 따른 클래스

		const item = `<div class="accordion-item">
				    <h2 class="accordion-header" id="heading${inquiry.id}">
				        <button class="accordion-button collapsed d-flex align-items-start" type="button" data-bs-toggle="collapse" 
				                data-bs-target="#collapse${inquiry.id}" aria-expanded="false" aria-controls="collapse${inquiry.id}">
				            
				            <!-- 상태 아이콘을 같은 줄에 배치하고, 시각적으로 상단에 위치하도록 함 -->
				            <span class="me-3 text-white p-1 rounded ${statusClass}" style="min-width: 80px; text-align: center; align-self: flex-start;">
				                ${statusText}
				            </span>    
				
				            <!-- 제목을 상태 아이콘 오른쪽에 배치 -->
				            <span class="flex-grow-1 align-self-center">[${inquiry.category}] ${inquiry.title}</span>
				        </button>
				    </h2>
				    <div id="collapse${inquiry.id}" class="accordion-collapse collapse" 
				         aria-labelledby="heading${inquiry.id}" data-bs-parent="#InquiryAccordion" style="background-color: #f8f9fa;">
				        <div class="accordion-body text-start" id="inquiryContent${inquiry.id}">
				            <span>${inquiry.content}</span>
				            
				            <!-- 답변 내용 표시 (답변이 있을 때만 보임) -->
				            <div id="answerSection${inquiry.id}" class="mt-3">
				                <span id="answerText${inquiry.id}" style="display: ${inquiry.answer ? 'block' : 'none'}; background-color: #fff;" class="p-3 rounded-md">
				                    ${inquiry.answer || ''}
				                </span>
				                <textarea id="answerTextarea${inquiry.id}" class="form-control mt-2" style="display:none;"></textarea>
				            </div>
				
				            <!-- 수정 및 삭제 버튼을 하단 오른쪽에 배치 -->
				            <div class="d-flex justify-content-end mt-3">
				                <button type="button" class="btn btn-secondary delete-btn" data-id="${inquiry.id}" id="deleteBtn">삭제</button>
				            </div>
				        </div>
				    </div>
				</div>
			`;
		inquiryContainer.append(item);
	});
}


// 페이징 버튼 생성
function createPagination(totalPages, currentPage) {
	const pagination = $('#pagination .pagination');
	pagination.empty();

	const maxButtons = 5; // 한 번에 표시할 페이지 버튼 수
	let startPage = Math.floor((currentPage - 1) / maxButtons) * maxButtons + 1;
	let endPage = Math.min(startPage + maxButtons - 1, totalPages);

	// 이전 버튼 (첫 페이지 그룹일 경우 비활성화)
	const prevDisabled = startPage === 1 ? 'disabled' : '';
	pagination.append(`<li class="page-item ${prevDisabled}"><button class="page-link" data-page="${startPage - 1}">&lt;</button></li>`);

	// 페이지 번호 버튼
	for (let i = startPage; i <= endPage; i++) {
		const isActive = i === currentPage ? 'active' : '';
		pagination.append(`<li class="page-item ${isActive}"><button class="page-link" data-page="${i}">${i}</button></li>`);
	}

	// 다음 버튼 (마지막 페이지 그룹일 경우 비활성화)
	const nextDisabled = endPage === totalPages ? 'disabled' : '';
	pagination.append(`<li class="page-item ${nextDisabled}"><button class="page-link" data-page="${endPage + 1}">&gt;</button></li>`);
}


//가입한 스터디 목록 
function getStudyLists() {

	$.ajax({
		type: 'GET',
		url: '/api/user/study/lists',
		success: function(response) {
			console.log(response);
		},
		error: function(xhr, status, error) {
			console.error("데이터 로드 중 오류 발생:", error);
		}
	});


}



function getAppliedLists() {

	$.ajax({
		type: 'GET',
		url: '/api/user/applied/study',
		success: function(response) {
			console.log(response);
		},
		error: function(xhr, status, error) {
			console.error("데이터 로드 중 오류 발생:", error);
		}
	});

}