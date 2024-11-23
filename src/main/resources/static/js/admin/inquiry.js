
let currentPage = 1; // 현재 페이지를 추적하는 변수

// 검색 조건을 저장할 전역 변수
let searchParams = {
	startDate: null,
	endDate: null,
	category: 'all',
	status: 'all',
	type: 'all',
	searchQuery: ''
};

$(document).ready(function() {

	// 페이지 로드 시 기본 데이터 로드
	loadInquiries(currentPage);

	$('.daterange-picker').daterangepicker({
		autoUpdateInput: false,
		locale: {
			cancelLabel: 'Clear',
			format: 'YYYY-MM-DD' // 서버로 전송할 형식
		}
	});
	// 날짜 범위 선택 적용 시 이벤트
	$('.daterange-picker').on('apply.daterangepicker', function(ev, picker) {
		$(this).val(picker.startDate.format('YYYY-MM-DD') + ' - ' + picker.endDate.format('YYYY-MM-DD'));

		// 선택된 날짜 범위를 hidden 필드와 searchParams에 저장
		$('input[name="startDate"]').val(picker.startDate.format('YYYY-MM-DD'));
		$('input[name="endDate"]').val(picker.endDate.format('YYYY-MM-DD'));

		// searchParams에 반영
		searchParams.startDate = picker.startDate.format('YYYY-MM-DD');
		searchParams.endDate = picker.endDate.format('YYYY-MM-DD');
	});

	// 날짜 범위 선택 취소 시 이벤트
	$('.daterange-picker').on('cancel.daterangepicker', function(ev, picker) {
		$(this).val('');
		$('input[name="startDate"]').val('');
		$('input[name="endDate"]').val('');

		// searchParams에서도 제거
		searchParams.startDate = null;
		searchParams.endDate = null;
	});

	//검색버튼 클릭
	$("#searchBtn").on("click", function(e) {

		e.preventDefault();

		// 검색 조건을 전역 변수에 저장
		searchParams = {
			startDate: $(".daterange-picker").data('startDate') || null,
			endDate: $(".daterange-picker").data('endDate') || null,
			category: $("select[name='category']").val(),
			status: $("select[name='status']").val(),
			type: $("select[name='type']").val(),
			searchQuery: $("input[name='searchQuery']").val()
		};

		loadInquiries(1); // 첫 페이지부터 검색 결과 표시
	});

	// 페이징 버튼 클릭 이벤트
	$('#pagination').on('click', '.page-link', function() {
		const page = $(this).data('page');
		loadInquiries(page); // 현재 검색 조건을 유지한 채로 페이지 이동
	});


	// 수정 버튼 클릭 이벤트 핸들러
	$(document).on('click', '#answerBtn', function() {
		const inquiryId = $(this).data('id');
		const answerText = $(`#answerText${inquiryId}`);
		const answerTextarea = $(`#answerTextarea${inquiryId}`);
		const saveBtn = $(`#saveBtn[data-id="${inquiryId}"]`);
		const cancelBtn = $(`#cancelBtn[data-id="${inquiryId}"]`);
		const deleteBtn = $(`#deleteBtn[data-id="${inquiryId}"]`);

		// textarea에 기존 답변 내용을 설정하고 표시
		answerTextarea.val(answerText.text()).show();
		answerText.hide(); // 기존 답변 텍스트 숨기기
		$(this).hide(); // 답변 버튼 숨기기
		deleteBtn.hide(); // 삭제 버튼 숨기기
		cancelBtn.show(); // 취소 버튼 표시
		saveBtn.show(); // 저장 버튼 표시
	});

	// 취소 버튼 클릭 이벤트 핸들러
	$(document).on('click', '#cancelBtn', function() {
		const inquiryId = $(this).data('id');
		const answerText = $(`#answerText${inquiryId}`);
		const answerTextarea = $(`#answerTextarea${inquiryId}`);
		const saveBtn = $(`#saveBtn[data-id="${inquiryId}"]`);
		const cancelBtn = $(`#cancelBtn[data-id="${inquiryId}"]`);
		const answerBtn = $(`#answerBtn[data-id="${inquiryId}"]`);
		const deleteBtn = $(`#deleteBtn[data-id="${inquiryId}"]`);

		// textarea를 숨기고 원래 답변 텍스트를 복구
		answerTextarea.hide();
		answerText.show();
		saveBtn.hide();
		cancelBtn.hide(); // 취소 버튼 숨기기
		answerBtn.show(); // 답변 버튼 표시
		deleteBtn.show(); // 삭제 버튼 표시
	});

	// 삭제 버튼 클릭 이벤트 핸들러
	$(document).on('click', '#deleteBtn', function() {
		const inquiryId = $(this).data('id');  // 버튼의 data-id 속성에서 id를 가져옴

		// AJAX 요청 예시
		$.ajax({
			type: "POST",
			url: "/api/admin/question/delete/inquiry",  // 수정할 API 엔드포인트
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


	// 삭제 버튼 클릭 이벤트 핸들러
	$(document).on('click', '#saveBtn', function() {
		const inquiryId = $(this).data('id');  // 버튼의 data-id 속성에서 id를 가져옴
		const answerTextarea = $(`#answerTextarea${inquiryId}`);
		const answerContent = answerTextarea.val(); // textarea의 값을 가져옴

		// AJAX 요청 예시
		$.ajax({
			type: "POST",
			url: "/api/admin/question/add/inquiry/answer",  // 수정할 API 엔드포인트
			data: { inquiryId: inquiryId,  answer: answerContent },
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


	// 답변 버튼 클릭 이벤트
	/*	$(document).on('click', '.edit-btn', function() {
			const inquiryId = $(this).data('id');
			const answerText = $(`#answerText${inquiryId}`);
			const answerTextarea = $(`#answerTextarea${inquiryId}`);
			const saveBtn = $(`.save-btn[data-id="${inquiryId}"]`);
	
			// textarea에 기존 답변 내용을 설정하고 표시
			answerTextarea.val(answerText.text()).show();
			answerText.hide(); // 기존 답변 텍스트 숨기기
			saveBtn.show(); // 저장 버튼 표시
		});
	*/

	// AJAX 요청으로 문의 리스트 로드
	function loadInquiries(page) {
		currentPage = page;  // 호출할 때마다 currentPage를 업데이트
		const data = {
			...searchParams, // 저장된 검색 조건을 요청 데이터에 포함
			page: page
		};

		$.ajax({
			type: 'GET',
			url: '/api/admin/question/get/inquiry',
			data: data,
			success: function(response) {
				renderInquiries(response.data); // 데이터 표시
				createPagination(response.totalPages, page); // 페이징
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

			const item = ` <div class="accordion-item">
            <h2 class="accordion-header" id="heading${inquiry.id}">
                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" 
                        data-bs-target="#collapse${inquiry.id}" aria-expanded="false" aria-controls="collapse${inquiry.id}">
                   <span class="me-3 text-white p-1 rounded ${statusClass}">${statusText}</span>    [${inquiry.category}]  ${inquiry.title}
                </button>
            </h2>
            <div id="collapse${inquiry.id}" class="accordion-collapse collapse" 
                 aria-labelledby="heading${inquiry.id}" data-bs-parent="#InquiryAccordion" style="background-color: #f8f9fa;">
                <div class="accordion-body" id="inquiryContent${inquiry.id}">
                    <span>${inquiry.content}</span>
                    
                 <!-- 답변 내용 표시 (답변이 있을 때만 보임) -->
                    <div id="answerSection${inquiry.id}" class="mt-3">
                        <span id="answerText${inquiry.id}" style="display: ${inquiry.answer ? 'block' : 'none'}; background-color: #fff;"" class="p-3 rounded-md">${inquiry.answer || ''}</span>
                        <textarea id="answerTextarea${inquiry.id}" class="form-control mt-2" style="display:none;"></textarea>
                    </div>

                    <!-- 수정 및 삭제 버튼을 하단 오른쪽에 배치 -->
                    <div class="d-flex justify-content-end mt-3">
                        <button type="button" class="btn btn-secondary me-2 edit-btn" data-id="${inquiry.id}" id="answerBtn">답변</button>
                          <button type="button" class="btn btn-secondary delete-btn  me-2" data-id="${inquiry.id}" id="cancelBtn" style="display:none;">취소</button>
                            <button type="button" class="btn btn-secondary  save-btn" data-id="${inquiry.id}" style="display:none;" id="saveBtn">저장</button>
                        <button type="button" class="btn btn-secondary delete-btn" data-id="${inquiry.id}" id="deleteBtn">삭제</button>
                    </div>
                </div>
            </div>
        </div>`;
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



});