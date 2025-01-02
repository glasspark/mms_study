let currentPage = 1;
$(document).ready(function () {

    getData(1)

    // 페이징 버튼 클릭 이벤트
    $('#pagination').on('click', '.page-link', function() {
        const page = $(this).data('page');
        getData(page); // 현재 검색 조건을 유지한 채로 페이지 이동
    });

    //회원 탈퇴
    $(document).on('click', '.delete-user', function() {
        const id = $(this).data('id');
        $.ajax({
            url: `/api/admin/users/${id}`,
            type: "DELETE",
            success: function (response) {
                getData(currentPage);
            },
            error: function (xhr, status, error) {
                console.log(xhr)
                alert(xhr.responseJSON.message);
            }
        });
    });

    function getData(page) {

        $.ajax({
            url: `/api/admin/users`,
            type: "GET",
            data: {
                page: page,
                type: null,
                content: null
            },
            success: function (response) {
                console.log(response)
                setData(response.data);
                currentPage = response.page.pagination.currentPage;
                createPagination(response.page.pagination.totalPages, response.page.pagination.currentPage) ;
            },
            error: function (xhr, status, error) {
                console.log(xhr)
                alert("서버 오류가 발생했습니다.");
            }
        });
    }

    function setData(data) {

        // 테이블 본문을 선택
        const $tableBody = $("#userTableBody");
        // 기존 테이블 내용을 초기화
        $tableBody.empty();

        // 데이터 반복 처리
        data.forEach(user => {
            // 이미지 경로 설정
            let imgPath = user.img_type ? `/upload${user.img_path}` : user.img_path;

            const $row = $(`
            <tr>
                <td>${user.id}</td>
                <td><img src="${imgPath}" alt="${imgPath}" class="img-thumbnail" style="width: 30px; height: 30px; object-fit: cover;"></td>
                <td>${user.username}</td>
                <td>${user.nickname}</td>
                <td>${user.email}</td>
                <td>${user.sns}</td>
                <td>${new Date(user.create).toLocaleString()}</td>
                <td>
                <button class="btn btn-danger btn-sm delete-user" data-id="${user.id}">탈퇴</button>
                </td>
            </tr>
        `);
            // 테이블 본문에 행 추가
            $tableBody.append($row);
        });

    }


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