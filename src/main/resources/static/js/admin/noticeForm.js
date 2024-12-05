$(document).ready(function() {

	// jQuery로 상단 고정 체크박스 이벤트 처리
	$(document).ready(function() {
		$('#isPinned').on('change', function() {
			if ($(this).is(':checked')) {
				$('#priorityField').show(); // 우선순위 입력 필드 표시
			} else {
				$('#priorityField').hide(); // 우선순위 입력 필드 숨김
				$('#pinPriority').val(''); // 값 초기화
			}
		});
	});


	$('#isPinned').on('change', function() {
		if ($(this).is(':checked')) {
			$(this).val('true');
		} else {
			$(this).val('false');
		}
	});

	//취소 버튼 클릭
	$('#cancelBtn').click(function() {
		window.location.href = `/admin/notice`;
	})

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

		formData.forEach((value, key) => {
			console.log(`${key}: ${value}`);
		});
		$.ajax({
			type: "POST",
			contentType: false,
			processData: false,
			enctype: 'multipart/form-data',
			url: `/admin/nocite`,
			data: formData,
			success: function(resp) {
				//	resetForm(); //form 내용 초기화 
				alert(resp.message);
				window.location.href = `/admin/notice`; //공지사항 페이지 이동
			},
			error: function(err) {
				console.error("오류:", err);
				alert('글 등록 중 오류가 발생했습니다. 다시 시도해주세요.');
			}
		});
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

/*	// 폼 초기화 함수
	function resetForm() {
		$('#saveForm')[0].reset();
		$('#postDefaultId').val(''); // hidden input 필드도 초기화
		if (editorWrite) {
			editorWrite.setData(''); // CKEditor 내용 초기화
		}
	}*/

});