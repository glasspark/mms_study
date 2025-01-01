$(document).ready(function() {
	// 페이지 로드 시 에러 메시지 숨기기
	$("#errorMessage").hide(); // 초기 화면에서 숨김

	// 입력 필드에 입력이 시작되면 에러 메시지 숨기기
	$("#username, #password").on("input", function() {
		$("#error-message").fadeOut(); // 부드럽게 사라짐
	});


});