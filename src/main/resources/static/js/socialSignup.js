$(document).ready(function() {


	$('#sendCodeBtn').on('click', function() {

		const email = $('#email').val();

		// AJAX 요청
		$.ajax({
			url: '/api/user/send/code', // 서버의 엔드포인트 URL
			type: 'POST',
			data: { email: email }, // JSON 형식으로 데이터를 보냄
			success: function(response) {
				if (response.status === 'success') {
					//인증번호 전송 -> 확인 으로 변경

					// 인증번호 전송 버튼 숨기기, 확인 버튼 보이기
					$('#sendCodeBtn').addClass('hidden');
					$('#checkCodeBtn').removeClass('hidden');


					let timeLeft = response.validTime;
					const countdownDisplay = $('#countdown');

					// 타이머가 이미 있다면 중복되지 않도록 초기화
					if (window.timer) clearInterval(window.timer);
					// 타이머 시작
					window.timer = setInterval(function() {
						const minutes = Math.floor(timeLeft / 60);
						const seconds = timeLeft % 60;

						countdownDisplay.text(`남은 시간: ${minutes}:${seconds < 10 ? '0' : ''}${seconds}`);

						if (timeLeft <= 0) {
							clearInterval(window.timer);
							countdownDisplay.text("시간이 초과되었습니다.");
							// 시간이 초과되면 버튼 상태 다시 변경
							$('#sendCodeBtn').removeClass('hidden'); // 인증번호 전송 버튼 다시 보이기
							$('#checkCodeBtn').addClass('hidden'); // 확인 버튼 숨기기
						}
						timeLeft--; // 남은 시간 감소
					}, 1000); // 1초 간격
				} else if (response.status === 'fail') {
					alert(response.message);
				}
			},
			error: function(xhr, status, error) {
				alert("Failed to submit form.");
				console.error("Error submitting form:", error);
			}
		});
	});


	// 인증번호 전송 후 확인 시 사용 
	$('#checkCodeBtn').on('click', function() {
		const key = $('#emailCheck').val();
		console.log(email);
		$.ajax({
			url: '/api/user/check/email', // 서버의 엔드포인트 URL
			type: 'POST',
			data: { key: key }, // JSON 형식으로 데이터를 보냄
			success: function(response) {
				console.log(response);
				if (response.status === true) {
					//인증이 완료된 이후 후처리
					if (window.timer) clearInterval(window.timer); // 타이머 중단
					$('#countdown').addClass('hidden'); // 타이머 영역 숨기기
					$('#completeCode').removeClass('hidden'); // 인증완료 버튼 보이게
					$('#checkCodeBtn').addClass('hidden'); // 확인 버튼 숨기기
					$('#sendCodeBtn').addClass('hidden'); // 인증번호 전송 버튼 숨기기
				}
			}
		});
	});

	//데이터 전송
	$('#scSubmitButtons').on('click', function() {
		const sns = $(this).data('sns'); // 버튼의 data-sns 값을 가져옴
		switch (sns) {
			case "kakao":
				location.href = '/auth/kakaoLoginPage?step=1';
				break;
			case "naver":
				location.href = '/auth/naverLoginPage?step=1';
				break;
			default:
				alert("잘못된 접근입니다.");
				location.href = '/auth/login';
				return;
		}
	});

});