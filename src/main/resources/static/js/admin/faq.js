$(document).ready(function() {

	getData();

	// 등록 버튼 클릭 시 FAQ 폼 표시/숨기기
	$('#registerButton').on('click', function() {
		$('#faqForm').toggle();
		$('#saveFaqButton').show();
		$('#updateFaqButton').hide();
	});


	//X 닫기 시 사용
	$('#closeFaqForm').on('click', function() {
		clearErrorMessages();
		$('#faqForm').hide();
	});


	// 폼 제출 시 새로운 FAQ 항목 추가
	$('#faqForm').on('submit', function(e) {
		e.preventDefault(); // 기본 폼 제출 동작을 막음
		// 폼 데이터 가져오기
		const formData = $('#faqForm').serialize(); // 폼 데이터를 URL 인코딩된 문자열로 가져오기

		// AJAX 요청 전송
		$.ajax({
			type: 'POST',
			url: '/api/admin/question/create', // 서버에서 FAQ 생성 처리를 위한 엔드포인트
			data: formData, // 데이터 객체 전송 (application/x-www-form-urlencoded 형식)
			success: function(response) {

				console.log(response);
				// 성공 시 처리할 내용 (예: 알림, 폼 초기화, 리스트 갱신 등)
				if (response.status === "success") {
					alert('FAQ 항목이 성공적으로 추가되었습니다.');
					$('#faqForm')[0].reset(); // 폼 초기화
					$('#faqForm').hide(); // 폼 숨기기

					getData();

				} else {
					clearErrorMessages();
					displayErrorMessages(response.errors);
				}
			},
			error: function(xhr, status, error) {
				// 오류 발생 시 처리할 내용 (예: 오류 메시지 표시)
				alert('FAQ 항목 추가 중 오류가 발생했습니다. 다시 시도해주세요.');
				console.error('오류 메시지:', error);
			}
		});
	});


	// "수정" 버튼 클릭 시 FAQ 수정 처리
	$('#updateFaqButton').on('click', function(e) {
		e.preventDefault();

		const formData = $('#faqForm').serialize();

		$.ajax({
			type: 'POST',  // Change to the appropriate method if it's PUT
			url: `/api/admin/question/update`,  // Endpoint for updating FAQ
			data: formData,
			success: function(response) {
				if (response.status === "success") {
					alert(response.message);
					$('#faqForm')[0].reset();
					$('#faqId').val(null)
					$('#faqForm').hide();

					getData();
				} else {
					clearErrorMessages();
					displayErrorMessages(response.errors);
				}
			},
			error: function(xhr, status, error) {
				alert('FAQ 항목 수정 중 오류가 발생했습니다. 다시 시도해주세요.');
				console.error('오류 메시지:', error);
			}
		});
	});

});



// FAQ 삭제 함수
function deleteFaq(faqId) {
	$.ajax({
		type: 'POST',
		url: `/api/admin/question/delete`,  // FAQ 삭제를 위한 엔드포인트
		data: { faqId: faqId },
		success: function(response) {
			if (response.status === "success") {
				alert(response.message);
				// FAQ 리스트를 다시 가져와서 UI 갱신
				getData();
			} else {
				alert(response.message);
			}
		},
		error: function(xhr, status, error) {
			alert('FAQ 항목 삭제 중 오류가 발생했습니다. 다시 시도해주세요.');
			console.error('오류 메시지:', error);
		}
	});
}

// FAQ 수정 함수 (임시, 수정 UI로 이동 또는 수정 내용을 폼에 반영하는 작업)
function editFaq(faqId) {
	$.ajax({
		type: 'GET',
		url: `/api/admin/question/get/data`,  // FAQ 삭제를 위한 엔드포인트
		data: { faqId: faqId },
		success: function(response) {
			if (response.status === "success") {
				const faq = response.data;

				$('#faqForm').toggle();
				$('#faqId').val(faqId);
				$('#faqCategory').val(faq.category);
				$('#faqTitle').val(faq.title);
				$('#faqContent').val(faq.content);

				/*수정, 저장 버튼 */
				$('#saveFaqButton').hide();
				$('#updateFaqButton').show();

			} else {
				alert(response.message);
			}
		},
		error: function(xhr, status, error) {
			alert('FAQ 항목 삭제 중 오류가 발생했습니다. 다시 시도해주세요.');
			console.error('오류 메시지:', error);
		}
	});
}


//자주 묻는 질문 리스트를 가져오는 함수
function getData() {

	$.ajax({
		type: 'GET',
		url: '/api/admin/question/get',
		success: function(response) {
			console.log(response);
			// 성공 시 처리할 내용 (예: 알림, 폼 초기화, 리스트 갱신 등)
			if (response.status === "success") {
				setList(response.data);
			} else {
				alert(response.message);
			}
		},
		error: function(xhr, status, error) {
			// 오류 발생 시 처리할 내용 (예: 오류 메시지 표시)
			alert('FAQ 리스트 반환 중 오류가 발생했습니다. 다시 시도해주세요.');
			console.error('오류 메시지:', error);
		}
	});
}


function setList(faqList) {
	const faqAccordion = $('#faqAccordion');
	faqAccordion.empty();  // 기존의 FAQ 리스트를 초기화

	faqList.forEach((faq, index) => {
		const faqItem = `
            <div class="accordion-item">
                <h2 class="accordion-header d-flex align-items-center justify-content-between" id="heading${index}">
                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">
                        <b>[${faq.category}] ${faq.title}</b>  <!-- FAQ 제목 -->
                    </button>
                    <div class="ms-4 mb-2">
                        <button class="btn btn-warning btn-sm edit-faq-button ps-3 pe-3" data-id="${faq.id}">수정</button>  <!-- 수정 버튼 -->
                        <button class="btn btn-danger btn-sm delete-faq-button  ps-3 pe-3" data-id="${faq.id}">삭제</button>  <!-- 삭제 버튼 -->
                    </div>
                </h2>
                <div id="collapse${index}" class="accordion-collapse collapse" aria-labelledby="heading${index}" data-bs-parent="#faqAccordion">
                    <div class="accordion-body">
                        ${faq.content}  <!-- FAQ 내용 -->
                    </div>
                </div>
            </div>
        `;

		faqAccordion.append(faqItem);  // 아코디언 아이템을 추가
	});

	// 삭제 버튼 클릭 이벤트 핸들러 등록
	$('.delete-faq-button').on('click', function() {
		const faqId = $(this).data('id');  // 삭제할 FAQ ID 가져오기
		deleteFaq(faqId);  // 삭제 함수 호출
	});

	// 수정 버튼 클릭 이벤트 핸들러 등록
	$('.edit-faq-button').on('click', function() {
		const faqId = $(this).data('id');  // 수정할 FAQ ID 가져오기
		editFaq(faqId);  // 수정 함수 호출 (수정 폼으로 이동하거나 기존 데이터를 폼에 표시하는 로직)
	});
}



function displayErrorMessages(errors) {
	// 모든 에러 필드를 돌면서 표시
	clearErrorMessages(); // 기존 에러 초기화

	for (const field in errors) {
		const errorMessage = errors[field];
		const errorElementId = `#${field}Error`;  // 예를 들어 'titleError', 'categoryError'
		const inputElementId = `#${field}`;  // 필드 ID와 일치하는 입력 요소 선택

		// 입력 요소에 is-invalid 클래스 추가
		if ($(inputElementId).length) {
			$(inputElementId).addClass('is-invalid'); // 에러 스타일 추가
		}

		// 에러 메시지 요소에 텍스트 추가
		if ($(errorElementId).length) {
			$(errorElementId).text(errorMessage).show(); // 에러 메시지 표시
		}
	}
}

// 폼 에러 초기화 함수
function clearErrorMessages() {
	// 모든 입력 필드에서 is-invalid 클래스 제거 (에러 스타일 초기화)
	$('.is-invalid').each(function() {
		$(this).removeClass('is-invalid');
	});

	// 모든 에러 메시지 요소에서 텍스트 제거
	$('.invalid-feedback').each(function() {
		$(this).text('').hide(); // 에러 메시지를 비우고 숨김
	});
}


