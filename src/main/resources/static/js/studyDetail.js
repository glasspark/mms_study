$(document).ready(function() {

	// 문서 로드 시 모든 댓글 가져오기
	loadComments();


	// 모집완료 버튼 누름
	$("#closed").click(function() {

		let studyGroupId = $("#dropDownLists").attr("data-study");
		let recruitmentStatus = $("#dropDownLists").attr("data-recruitment-status");

		// AJAX 요청
		$.ajax({
			url: `/api/group/study/closed`,
			type: "POST",
			data: {
				studyGroupId: studyGroupId,
				recruitmentStatus: recruitmentStatus
			},
			success: function(response) {
				if (response.status === "success") {
					alert(response.message);
					window.location.href = '/study'
				} else {
					alert(response.message);
				}
			},
			error: function(xhr, status, error) {
				alert("서버 오류가 발생했습니다.");
			}
		});
	});




	// 모집완료 버튼 누름
	$("#delBtn").click(function() {

		let studyGroupId = $("#dropDownLists").attr("data-study");

		// AJAX 요청
		$.ajax({
			url: `/api/group/del/study`,
			type: "POST",
			data: {
				studyGroupId: studyGroupId
			},
			success: function(response) {
				if (response.status === "success") {
					alert(response.message);
					window.location.href = '/study';
				} else {
					alert(response.message);
				}
			},
			error: function(xhr, status, error) {
				alert("서버 오류가 발생했습니다.");
			}
		});
	});


	$("#joinRequestBtn").click(function() {

		const studyId = $(this).attr('data-study');
		$.ajax({
			type: 'POST',
			url: '/api/group/req/join',
			contentType: 'application/json',
			data: JSON.stringify({ studyNum: studyId }),
			success: function(response) {
				
				if (response.status === 'success') {
					alert(response.message);
				} else {
					alert(response.message);
				}
			},
			error: function(xhr, status, error) {
				console.error('오류 발생:', error);
				alert('가입 신청 중 오류가 발생했습니다. 다시 시도해 주세요.');
			}
		});
	});


	$("#registerButton").click(function() {

		// 댓글 입력 필드 값 가져오기
		let commentContent = $("#commentInput").val();
		let studyGroupId = $("#commentInput").attr("data-group");

		// AJAX 요청
		$.ajax({
			url: `/api/group/study/${studyGroupId}/comments`,  // 서버 URL
			type: "POST",
			data: {
				comment: commentContent
			},
			success: function(response) {

				if (response.status === "success") {
					alert("댓글이 저장되었습니다.");
					loadComments();
					/*					$(".comments").empty();
										// 받은 댓글 데이터
										const comments = response.data;
					
										comments.forEach(function(commentData) {
											let commentHtml = setData(commentData);
											$(".comments").append(commentHtml);
											s
										});
					*/
					// 입력 필드 초기화
					$("#commentInput").val("");

				} else {
					alert("오류 발생: " + response.message);
				}
			},
			error: function(xhr, status, error) {
				alert("댓글 저장 중 오류가 발생했습니다.");
			}
		});
	});

	//댓글 삭제하기
	$(document).on('click', '#delComment', function() {


		//확인창 띄우기
		if (confirm("삭제하시겠습니까?")) {
			const commentId = $(this).closest('.card').data('comment-id');

			// AJAX 요청
			$.ajax({
				url: `/api/group/delete/comment`,
				type: "POST",
				data: {
					commentId: commentId
				},
				success: function(response) {
					if (response.status === "success") {
						alert(response.message);
						loadComments();//댓글 다시 불러오기

					} else {
						alert(response.message);
					}
				},
				error: function(xhr, status, error) {
					alert("서버 오류가 발생했습니다.");
				}
			});
		}
	});

	// 수정 버튼 이벤트 핸들러
	$(document).on('click', '#showComment', function() {
		const commentCard = $(this).closest('.card');
		const commentContent = commentCard.find('.comment-content');

		// 기존 텍스트와 textarea, 저장 버튼을 설정
		commentContent.find('.comment-text').hide();
		commentContent.find('.edit-textarea').show();
		commentContent.find('.save-button').show();
	});

	//댓글 수정하기
	$(document).on('click', '#reComment', function() {

		/*	const commentId = $(this).closest('.card').data('comment-id');*/
		const commentCard = $(this).closest('.card');
		const commentId = commentCard.data('comment-id');
		const updatedContent = commentCard.find('.edit-textarea').val();

		// 댓글 유효성 검사 (200자 이하로 제한)
		if (updatedContent.length > 200) {
			alert("댓글은 200자를 초과할 수 없습니다.");
			return;
		}

		// AJAX 요청
		$.ajax({
			url: `/api/group/update/comment`,
			type: "POST",
			data: {
				commentId: commentId,
				comments: updatedContent
			},
			success: function(response) {
				if (response.status === "success") {
					alert(response.message);
					loadComments();//댓글 다시 불러오기
				} else {
					alert(response.message);
				}
			},
			error: function(xhr, status, error) {
				alert("서버 오류가 발생했습니다.");
			}
		});
	});



	// 수정 버튼 이벤트 핸들러
	$(document).on('click', '#showReply', function() {
		const replyActions = $(this).closest('[data-reply-id]'); // 데이터 속성으로 답글 식별
		const replyId = replyActions.data('reply-id'); // 현재 답글 ID 가져오기
		const replyContent = $(`#relpyContent-${replyId}`); // 특정 답글의 내용 영역 찾기

		// 기존 텍스트와 textarea, 저장 버튼을 설정
		replyContent.find('.reply-text').hide();
		replyContent.find('.edit-reply-textarea').show();
		replyContent.find('.save-reply-button').show();
	});

	//답글 수정하기
	$(document).on('click', '#reReply', function() {

		const replyContentWrapper = $(this).closest(`[id^="relpyContent-"]`); // 특정 답글의 내용 영역 찾기
		const replyId = replyContentWrapper.attr('id').split('-')[1]; // replyContent의 ID에서 reply ID를 추출
		const updatedContent = replyContentWrapper.find('.edit-reply-textarea').val(); // 수정된 내용 가져오기

		// 댓글 유효성 검사 (200자 이하로 제한)
		if (updatedContent.length > 200) {
			alert("댓글은 200자를 초과할 수 없습니다.");
			return;
		}

		// AJAX 요청
		$.ajax({
			url: `/api/group/update/reply`,
			type: "POST",
			data: {
				replyId: replyId,
				relpy: updatedContent
			},
			success: function(response) {
				if (response.status === "success") {
					alert(response.message);
					loadComments();//댓글 다시 불러오기
				} else {
					alert(response.message);
				}
			},
			error: function(xhr, status, error) {
				alert("서버 오류가 발생했습니다.");
			}
		});
	});



	// 답글 저장
	$(document).on('click', '#replyBtn', function() {
		const commentListElement = $(this).closest('#commentList');
		const commentId = commentListElement.data('comment-id');


		const replyInput = commentListElement.find('#relpyInput');
		const editedContent = replyInput.val();

		$.ajax({
			type: "POST",
			url: "/api/group/add/reply",
			data: {
				commentId: commentId,
				relpy: editedContent
			},
			success: function(response) {
				if (response.status === "success") {
					replyInput.val('');  //댓글 입력창 초기화
					loadComments();//댓글 다시 불러오기
				} else {
					alert(response.message);
				}
			},
			error: function(error) {
				alert("댓글 저장 중 오류가 발생했습니다.");
			}
		});
	});

	//답글 삭제하기
	$(document).on('click', '#delReply', function() {

		//확인창 띄우기
		if (confirm("삭제하시겠습니까?")) {

			const replyListElement = $(this).closest('[data-reply-id]');
			const replyId = replyListElement.data('reply-id');

			// AJAX 요청
			$.ajax({
				url: `/api/group/delete/reply`,
				type: "POST",
				data: {
					replyId: replyId
				},
				success: function(response) {
					if (response.status === "success") {
						alert(response.message);
						loadComments();//댓글 다시 불러오기

					} else {
						alert(response.message);
					}
				},
				error: function(xhr, status, error) {
					alert("서버 오류가 발생했습니다.");
				}
			});
		}
	});
});

// 모든 댓글을 가져오는 함수
function loadComments() {
	let studyGroupId = $("#commentInput").attr("data-group");
	// AJAX 요청으로 모든 댓글 가져오기
	$.ajax({
		url: `/api/group/study/${studyGroupId}/comments`,  // 서버 URL (GET 요청)
		type: "GET",
		success: function(response) {
			//console.log(response);
			if (response.status === "success") {
				// 기존 댓글 영역을 비우기
				$(".comments").empty();
				// 댓글 리스트를 화면에 추가
				const comments = response.data; // 서버에서 댓글 리스트를 받음

				comments.forEach(function(commentData) {
					let commentHtml = setData(commentData);
					$(".comments").append(commentHtml);
				});

			} else {
				console.log("오류 발생: " + response.message);
			}
		},
		error: function(xhr, status, error) {
			console.error("댓글 가져오기 중 오류가 발생했습니다.");
		}
	});
}


function setData(commentData) {
	let imgPath = commentData.imgPath;

	if (imgPath && imgPath.includes("/upload/profile/")) {
		imgPath = "/upload" + imgPath;
	}

	return `
        <div class="card overflow-hidden rounded-4 border-0 mt-2" data-comment-id="${commentData.id}" id="commentList">
            <div class="container mt-4 pb-4">
                <div class="d-flex align-items-center justify-content-between mb-2">
                    <div class="d-flex align-items-center">
                        <img src="${imgPath}" alt="User Avatar"
                            class="rounded-5 user-avatar"
                            style="width: 40px; height: 40px;">
                        <div class="ms-2">
                            <strong class="nickname">${commentData.nickname}</strong><br>
                            <small class="text-muted created-at">${new Date(commentData.createdAt).toLocaleString()}</small>
                        </div>
                    </div>
                    <p class="trash-icon-area me-3">
                    ${commentData.isAuthor ? '<i class="bi bi-pencil me-2 comment-action cursor-pointer" id="showComment"></i> <i class="bi bi-trash comment-action cursor-pointer" id="delComment"></i>' : ''}
                    </p>
                </div>
                <div class="comment-content" id="commentContent-${commentData.id}">
                    <p class="comment-text">${commentData.content}</p>
                    <textarea class="form-control edit-textarea mt-2" style="display:none;" rows="3">${commentData.content}</textarea>
                    <button class="btn btn-success mt-2 save-button float-end" style="display:none;" id="reComment">저장</button>
                </div>
                <div class="reply-box">
                    <small class="text-muted">댓글  ${commentData.replies ? commentData.replies.length : 0}</small>
                    <div class="input-group mt-2">
                        <input type="text" class="form-control" placeholder="댓글 달기" id="relpyInput">
                        <button class="btn btn-primary" id="replyBtn">등록</button>
                    </div>
                    <div class="reply-list-area">
                        ${commentData.replies ? commentData.replies.map(reply => {
		// reply 이미지 경로 수정
		let replyImgPath = reply.imgPath;
		if (replyImgPath && replyImgPath.includes("/upload/profile/")) {
			replyImgPath = "/upload" + replyImgPath;
		}
		return `
                                <div class="mt-3 ms-2 me-2 ps-2 pe-2 pt-2 d-flex justify-content-between align-items-center">
                                <div class="d-flex align-items-center">
                                    <img src="${replyImgPath}" alt="User Avatar"
                                         class="rounded-5 user-avatar"
                                         style="width: 30px; height: 30px;">
                                            <div class="ms-2">
                                    <p class="mb-0">${reply.nickname}</p>
                                        <small class="text-muted created-at">${new Date(reply.createdAt).toLocaleString()}</small>
                                    </div>
                                         </div>
                                    <div data-reply-id="${reply.id}" id="replyActions">
                                 ${reply.isAuthor ? `<i class="bi bi-pencil me-2 reply-action cursor-pointer" id="showReply"></i> 
                                 <i class="bi bi-trash reply-action cursor-pointer" id="delReply" ></i>` : ''}
                                    </div>
                                </div>
                                <div class="ms-2 me-2  ps-2 pe-2 pt-2 pb-3"  id="relpyContent-${reply.id}">
                                <p class="reply-text" >
                                    ${reply.content}
                             </p>
                             		<textarea class="form-control edit-reply-textarea mt-2" style="display:none;" rows="3">${reply.content}</textarea>
									<button class="btn btn-success mt-2 save-reply-button float-end" style="display:none;" id="reReply">저장</button>
                                </div>
                            `;
	}).join('') : ''}
                    </div>
                </div>
            </div>
        </div>
    `;
}


