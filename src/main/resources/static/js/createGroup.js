//태그 추가 
let tags = []; // 태그 저장 배열

$(document).ready(function() {
	// 엔터 또는 스페이스 키 입력 시 태그 추가
	$('#tags').on('keypress', function(event) {

		if (event.key === 'Enter' || event.key === ' ') {
			event.preventDefault(); // 기본 동작 방지
			const newTag = $(this).val().trim();

			if (newTag && !tags.includes(newTag) && tags.length < 5) {
				tags.push(newTag);

				// 새로운 태그를 UI에 추가
				const tagItem = $('<li></li>').addClass('list-inline-item badge bg-info text-white me-1 p-2 text-center').text(newTag);

				// 태그 삭제 버튼 추가
				const removeBtn = $('<span></span>').addClass('ms-2 text-danger').css('cursor', 'pointer').html('&times;');
				removeBtn.on('click', function() {
					tags = tags.filter(tag => tag !== newTag); // 배열에서 태그 삭제
					tagItem.remove(); // UI에서 태그 제거
					updateTagsField(); // 숨겨진 필드 업데이트
				});
				tagItem.append(removeBtn);
				$('#tagList').append(tagItem); //보여지는 부분 태그 추가
				// 입력 필드 초기화
				$(this).val('');

				//서버에 전송할 태그 값을 input 에 담음
				updateTagsField();

			} else if (tags.length >= 5) {
				alert('태그는 최대 5개까지 추가할 수 있습니다.');
			}
		}
	});


	$('#cancel').on('click', function() {

		window.location.href = '/study'; // 이동할 페이지의 URL로 변경

	});

	$('#register').on('click', function(e) {
		let isValid = true;

		// 스터디 그룹 이름 유효성 검사 (필수, 최대 20자)
		const name = $('#name').val().trim();
		if (name === '' || name.length > 20) {
			alert('스터디 그룹 이름은 필수이며 최대 20자까지 입력할 수 있습니다.');
			isValid = false;
		}

		// 설명 유효성 검사 (최대 200자)
		const description = $('#description').val();
		if (description.length > 200) {
			alert('설명은 최대 200자까지 입력할 수 있습니다.');
			isValid = false;
		}

		// 최대 인원 수 유효성 검사 (1~20)
		const maxMembers = $('#maxMembers').val();
		if (maxMembers < 1 || maxMembers > 20) {
			alert('최대 인원 수는 1명 이상 20명 이하로 설정해야 합니다.');
			isValid = false;
		}

		// 유효하지 않으면 폼 제출 막기
		if (!isValid) {
			e.preventDefault();
		}

		// 폼 데이터 가져오기
		const formData = $('#saveForm').serialize();

		// AJAX 요청으로 서버에 폼 데이터 전송
		$.ajax({
			type: 'POST',
			url: '/api/group/create', // 서버의 엔드포인트 URL로 변경하세요
			data: formData,
			contentType: 'application/x-www-form-urlencoded',
			success: function(response) {
				console.log(response);
				if (response.status === "error") {
					// 오류 메시지 표시
					for (const [field, message] of Object.entries(response.errors)) {
						console.log(`필드: ${field}, 메시지: ${message}`);
						$("#" + field + "-error").text(message).show();
					}
				} else {
					// 성공 처리 (예: 페이지 리다이렉트)
					alert(response.message);
					window.location.href = "/study";
				}
			},
			error: function(error) {
				console.log(error);
				console.error('등록 중 오류가 발생했습니다:', error);
				alert('등록에 실패했습니다. 다시 시도해주세요.');
			}
		});
	});
});


// 숨겨진 필드 업데이트 함수
function updateTagsField() {
	$('#tagsField').val(tags.join(',')); // 태그 배열을 쉼표로 연결하여 숨겨진 필드에 설정
}