$(document).ready(function() {

	const pagination = $('#pagination');

	getData(0);

	function getData(page) {
		$.ajax({
			url: `/api/user/nocite?page=${page}`,
			type: 'GET',
			success: function(response) {
				setData(response.data.items);
				setPagination(response.data.currentPage, response.data.totalPages);
			},
			error: function(xhr, status, error) {
				console.error("데이터 로드 중 오류 발생:", error);
			}
		});
	}

	function setData(items) {
		// Clear the accordion container
		const noticeList = document.getElementById('noticeList');
		noticeList.innerHTML = '';

		// Loop through the items and create Bootstrap Accordion items
		items.forEach((item, index) => {

			const pinnedClass = item.isPinned ? 'bg-warning' : 'bg-light'; // 고정게시글 생삭 처리

			const noticeHTML = `
			  <div class="accordion-item ${pinnedClass}">
			    <h2 class="accordion-header d-flex align-items-center justify-content-between" id="heading${index}">
			        <button class="accordion-button collapsed m-2 ${pinnedClass}" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">
			            <i class="bi bi-plus" style="font-size: 2rem; font-weight: bold;"></i>
			            <b>[${item.title}]</b>
			        </button>
			    </h2>
			    <div id="collapse${index}" class="accordion-collapse collapse" aria-labelledby="heading${index}" data-bs-parent="#noticeList">
			        <div class="accordion-body d-flex flex-column">
			            <div>${item.content}</div>
			            <p class="text-end text-muted mt-2">${formatDate(item.createdAt)}</p>
			        </div>
			    </div>
			</div>
                `;
			noticeList.insertAdjacentHTML('beforeend', noticeHTML);
		});
	}

	function formatDate(isoString) {
		const date = new Date(isoString);
		return date.toLocaleString('ko-KR', {
			year: 'numeric',
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit',
		});
	}

	// 페이징 버튼 렌더링
	function setPagination(currentPage, totalPages) {



		pagination.empty(); // 기존 페이징 버튼 초기화

		const maxButtons = 5; // 한 번에 표시할 페이지 버튼 수
		totalPages = Math.max(totalPages, 1); // 최소 1페이지는 보장
		const startPage = Math.floor(currentPage / maxButtons) * maxButtons + 1;
		const endPage = Math.min(startPage + maxButtons - 1, totalPages);

		// 이전 버튼 (첫 페이지 그룹일 경우 비활성화)
		const prevDisabled = currentPage <= 0 ? "disabled" : "";
		pagination.append(`
        <li class="page-item ${prevDisabled}">
            <button class="page-link" data-page="${Math.max(startPage - maxButtons - 1, 0)}" ${prevDisabled ? "disabled" : ""}>&lt;</button>
        </li>
    `);

		// 페이지 번호 버튼
		for (let i = startPage; i <= endPage; i++) {
			const isActive = (i - 1) === currentPage ? "active" : ""; // 현재 페이지와 매칭
			pagination.append(`
            <li class="page-item ${isActive}">
                <button class="page-link" data-page="${i - 1}">${i}</button>
            </li>
        `);
		}

		// 다음 버튼 (마지막 페이지 그룹일 경우 비활성화)
		const nextDisabled = endPage >= totalPages ? "disabled" : "";
		pagination.append(`
        <li class="page-item ${nextDisabled}">
            <button class="page-link" data-page="${Math.min(startPage + maxButtons - 1, totalPages - 1)}" ${nextDisabled ? "disabled" : ""}>&gt;</button>
        </li>
    `);

		// 버튼 클릭 이벤트
		$(".page-link").click(function(e) {
			e.preventDefault();
			const page = parseInt($(this).data("page"));
			if (!isNaN(page) && page >= 0 && page < totalPages) {
				getData(page); // 새로 요청
			}
		});
	}


});