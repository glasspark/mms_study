$(document).ready(function() {


	// 폼 제출 시 새로운 FAQ 항목 추가
	$('#faqForm').on('submit', function(e) {
		e.preventDefault(); // 기본 폼 제출 동작을 막음
		// 폼 데이터 가져오기
		const formData = $('#faqForm').serialize(); // 폼 데이터를 URL 인코딩된 문자열로 가져오기

		// AJAX 요청 전송
		$.ajax({
			type: 'POST',
			url: '/api/question/create', // 서버에서 FAQ 생성 처리를 위한 엔드포인트
			data: formData, // 데이터 객체 전송 (application/x-www-form-urlencoded 형식)
			success: function(response) {

				console.log(response);
				// 성공 시 처리할 내용 (예: 알림, 폼 초기화, 리스트 갱신 등)
				if (response.status === "success") {
					alert('1:1 문의가 접수 되었습니다.');
					window.location.href = "/faq";
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



});