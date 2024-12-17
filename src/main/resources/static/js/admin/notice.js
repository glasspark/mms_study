$(document).ready(function() {

	const noticeList = $("#noticeList");
	const pagination = $("#pagination");

	$(document).ready(function() {
		$('#addNotice').on('click', function() {
			window.location.href = '/admin/notice/form'; // 원하는 URL로 이동
		});
	});

	getData(0);


	function getData(page) {
		$.ajax({
			url: `/admin/nocite?page=${page}`,
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
		noticeList.empty(); // 기존 내용 초기화


		items.forEach(function(item) {
			const listItem = `
            <div class="list-group-item notice-item" data-id="${item.id}">
                <h5>${item.title}</h5>
                <small>작성일: ${new Date(item.createdAt).toLocaleDateString()}</small>
                ${item.isPinned ? '<span class="badge bg-warning">상단 고정</span>' : ""}
            </div>
        `;
			noticeList.append(listItem);
		});

		$(".notice-item").click(function() {
			const id = $(this).data("id");

			// 서버로부터 공지사항의 상세 데이터 가져오기
			$.ajax({
				url: `/admin/nocite/${id}`, // 공지사항 상세 데이터 요청
				type: 'GET',
				success: function(noticeItem) {
					// 모달에 데이터 넣기
					$("#modalTitle").text(noticeItem.title);
					$("#modalContent").html(noticeItem.content);
					$("#modalCreatedAt").text(`작성일: ${new Date(noticeItem.createdAt).toLocaleDateString()}`);
					$("#modalPinned").html(noticeItem.isPinned ? '<span class="badge bg-warning">상단 고정</span>' : "");

					// 수정 버튼에 공지사항 ID 저장
					$(".edit-button").data("id", id);

					// 삭제 버튼에 공지사항 ID 저장
					$(".delete-button").data("id", id);

					// 모달 표시
					$("#noticeModal").modal("show");
				},
				error: function(xhr, status, error) {
					alert("공지사항 데이터를 불러오는 중 오류가 발생했습니다.");
				}
			});
		});

		// 수정 버튼 클릭 이벤트
		$(document).on("click", ".edit-button", function() {
			const noticeId = $(this).data("id");
			window.location.href = `/admin/notice/form?id=${noticeId}`; // 수정 페이지로 이동
		});

		// 삭제 버튼 클릭 이벤트
		$(document).on("click", ".delete-button", function() {
			const noticeId = $(this).data("id");
			if (confirm("공지사항을 삭제하시겠습니까?")) {
				// AJAX로 삭제 요청
				$.ajax({
					url: `/admin/nocite/${noticeId}`,
					type: "DELETE",
					success: function(response) {
						alert("공지사항이 삭제되었습니다.");
						getData(currentPage); // 삭제 후 리스트 갱신
					},
					error: function(xhr, status, error) {
						alert("삭제 중 오류가 발생했습니다.");
					}
				});
			}
		});
	}

	// 페이징 버튼 렌더링
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