$(document).ready(function() {


	//취소 버튼 클릭
	$('#cancelBtn').click(function() {
		let num = $('#groupId').val();
		window.location.href = `/mystudy/detail/${num}`;
	})


	/*function content_setting() {
		let boardId = $('#category_select option:selected').val();
		$.ajax({
			type: "POST",
			url: "/get/board/postForm",
			data: {
				boardId: boardId
			},
			success: function(resp) {
				$('.ck.ck-editor').remove();
				$('#ck-editor').empty();
				$('#ck-editor').html(resp);
				make_ckeditor();
			},
			error: function() {
				alert("에러가 발생하였습니다.");
			}
		})
	}*/

	//작성버튼
	$('#submitBtn').click(function(e) {
		e.preventDefault();
		var formData = new FormData($('#saveForm')[0]);


		var imgList = '';
		// 에디터 데이터 가져오기
		var contentData = editor.getData();


		// 중복된 '/upload/' 경로 제거 (예: '/upload/upload/...')
		var cleanedContent = contentData.replace(/\/upload\/upload\//g, '/upload/');

		var imgRegex = /<img[^>]+src="([^">]+)"/g; // 이미지 태그에서 src 속성 추출
		var match;

		while ((match = imgRegex.exec(cleanedContent)) !== null) {
			// 이미지 경로에서 파일 이름만 추출
			var fullPath = match[1]; // 예: '/upload/temp/B49a6AmVpr테스트강아지.jpg'
			var fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1); // 파일 이름만 추출
			imgList += fileName + ',';
		}

		if (imgList.length > 0) {
			imgList = imgList.substring(0, imgList.length - 1); // 마지막 콤마 제거
		}

		formData.append('img', imgList)					//이미지의 이름만 가져온 값을 넘겨준다. 이미지 이동,  삭제에 쓰인다.
		formData.append('content', editor.getData())		//editor.getData()로 textarea의 내용을 가져올 수 있다.

		$.ajax({
			type: "post",
			contentType: false,
			enctype: 'multipart/form-data',
			processData: false,
			url: "/api/group/detail/save/write",
			data: formData,
			success: function(resp) {
				if (resp.status === "success") {
					alert("등록되었습니다.");
					let num = $('#groupId').val();
					window.location.href = `/mystudy/detail/${num}`;
				} else {
					alert(resp.message);
				}
			}
		})
	})

	//$('#ck-editor').append('${board.content }')

	make_ckeditor();

	function make_ckeditor() {

		$('#ck-editor').append('')
		ClassicEditor
			.create(document.querySelector('#ck-editor'), { //내용을 입력받는 textarea의 아이디나 클래스 입력
				extraPlugins: [MyCustomUploadAdapterPlugin],
				image: {
					toolbar: []
				}
			})
			.then(newEditor => {
				// 크기 높이기
				$('style').append('.ck-content { height: 400px; }');
				editor = newEditor;
			})
			.catch(error => {

				console.error(error);

			});
	}

	class UploadAdapter {
		constructor(loader) {
			this.loader = loader;
		}

		upload() {
			return this
				.loader
				.file
				.then(file => new Promise(((resolve, reject) => {
					this._initRequest();
					this._initListeners(resolve, reject, file);
					this._sendRequest(file);
				})))
		}

		//임시 이미지 저장
		_initRequest() {
			const xhr = this.xhr = new XMLHttpRequest();
			xhr.open('POST', '/api/group/detail/upload/image', true);
			xhr.responseType = 'json';
		}

		_initListeners(resolve, reject) {
			const xhr = this.xhr;
			const loader = this.loader;
			const genericErrorText = '파일을 업로드 할 수 없습니다.'

			xhr.addEventListener('error', () => {
				reject(genericErrorText)
			})
			xhr.addEventListener('abort', () => reject())
			xhr.addEventListener('load', () => {
				const response = xhr.response
				if (!response || response.error) {
					return reject(
						response && response.error
							? response.error.message
							: genericErrorText
					);
				}

				// /upload 경로를 URL 앞에 추가
				const updatedUrl = '/upload' + response.url;

				resolve({
					default: updatedUrl //업로드된 파일 주소
				})

				/*	resolve({
						default: response.url //업로드된 파일 주소
					})*/
			})
		}

		_sendRequest(file) {
			const data = new FormData()
			data.append('boardImg', file)
			this
				.xhr
				.send(data)
		}
	}

	function MyCustomUploadAdapterPlugin(editor) {
		editor
			.plugins
			.get('FileRepository')
			.createUploadAdapter = (loader) => {
				return new UploadAdapter(loader)
			}
	}


});