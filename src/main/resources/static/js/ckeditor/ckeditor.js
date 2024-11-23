$(function() {

	/*일반 게시글 등록*/
	$('#saveWriteBtn').click(function() {

		var formData = new FormData($('#saveForm')[0]);

		let editorData = editorWrite.getData();
		var imgList = '';

		var splitData = editorData.split(">");
		for (var i = 0; i < splitData.length; i++) {
			var imgIndex = splitData[i].indexOf("src=");
			if (imgIndex !== -1) {
				var srcStart = splitData[i].indexOf('"', imgIndex) + 1;
				var srcEnd = splitData[i].indexOf('"', srcStart);
				var imgSrc = splitData[i].substring(srcStart, srcEnd);

				// 이미지 경로 중복 여부 확인
				if (!imgList.includes(imgSrc)) {
					imgList += imgSrc + ",";
				}
			}
		}

		imgList = imgList.slice(0, -1); // 마지막 쉼표 제거
		formData.set('img', imgList); // 중복 방지를 위해 set 사용
		formData.set('content', editorData);
		formData.set('primaryKey', postId);

		/*	formData.forEach((value, key) => {
				console.log(key + ": " + value);
			});*/

		console.log(adminBoardType);

		$.ajax({
			type: "POST",
			contentType: false,
			processData: false,
			enctype: 'multipart/form-data',
			url: `/user/${adminBoardType}/board/write`,
			data: formData,
			success: function(resp) {
				if (resp.result === true) {
					alert("등록되었습니다.");
					resetForm(); //form 내용 초기화 
					getList(); //데이터 다시 불러오기 
					//모달 창을 닫아 주기
					$(".admin_write").css("display", "none");
				} else {
					alert(resp.word + "는 금칙어 입니다.\n다시 확인해주세요.");
				}
			},
			error: function(err) {
				console.error("오류:", err);
				alert('글 등록 중 오류가 발생했습니다. 다시 시도해주세요.');
			}
		});
	});

	// 폼 초기화 함수
	function resetForm() {
		$('#saveDefaultForm')[0].reset();
		$('#postDefaultId').val(''); // hidden input 필드도 초기화
		if (editorWrite) {
			editorWrite.setData(''); // CKEditor 내용 초기화
		}
	}

});
