$(document).ready(function() {


	getData();

	//자주 묻는 질문 리스트를 가져오는 함수
	function getData() {

		$.ajax({
			type: 'GET',
			url: '/api/question/get',
			success: function(response) {
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
				<h2 class="accordion-header d-flex align-items-center justify-content-between">
				<button class="accordion-button collapsed m-2" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">
				<span><i class="bi bi-plus" style="font-size: 2rem; font-weight: bold;"></i></span><b>[${faq.category}] ${faq.title}</b>
				</button>
				</h2>
				<div id="collapse${index}" class="accordion-collapse collapse" aria-labelledby="heading${index}" data-bs-parent="#faqAccordion">
					<div class="accordion-body ms-2 me-2">
						 ${faq.content} 
					</div>
				</div>
			</div>
        `;
			faqAccordion.append(faqItem);  // 아코디언 아이템을 추가
		});
	}

});
