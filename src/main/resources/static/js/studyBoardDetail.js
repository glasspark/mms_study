$(document).ready(function() {

	//수정하기 버튼 클릭
	$('#modifyBtn').on('click', function() {
		let groupId = $('#groupId').val(); // groupId 값 가져오기
		let boardId = $('#boardId').val(); // groupId 값 가져오기
		window.location.href = `/mystudy/detail/board/${groupId}?boardId=${boardId}`;
	});

	//삭제하기 버튼 클릭
	$('#deleteBtn').on('click', function() {
		let groupId = $('#groupId').val(); // groupId 값 가져오기
		let boardId = $('#boardId').val(); // groupId 값 가져오기

		if (confirm("정말 이 게시글을 삭제하시겠습니까?")) {
			$.ajax({
				type: 'DELETE',
				url: `/api/group/detail/board`, // 엔드포인트에 맞춰 URL 설정
				data: {
					groupId: groupId,
					boardId: boardId
				},
				success: function(response) {
					if (response.status === 'success') {
						alert(response.message);
						window.location.href = `/mystudy/detail/${groupId}`;
					} else {
						alert(response.message);
					}
				},
				error: function(error) {
					alert('게시글 삭제 중 오류가 발생했습니다.');
				}
			});
		}
	});

});