$(document).ready(function() {


	//======== 마이페이지 ========

	//기본적으로 보여질 화면 지정
	showContent('infoContent');

	//기본 적으로 보여주는 정보 =>  내 정보 - 기본정보 변경
	$('#showInfo').on('click', function() {
		showContent('infoContent');
	});

	// 내 정보 - 기본정보 변경
	$('#defaultInfo').on('click', function() {
		showContent('infoContent');
	});

	// 내 정보 - 비밀번호 변경
	$('#showPassword').on('click', function() {
		showContent('passwordContent');
	});


	// 사이드바 이벤트 관련 (클릭 시 보여줄 각 항목의 리스트들)
	$('#showInquiry').on('click', function() {
		showContent('infoInquiryContent');
	});

});


// 콘텐츠 표시 함수
function showContent(contentId) {
	// 모든 콘텐츠 섹션 숨기기
	$('.content-section').hide();
	// 선택한 콘텐츠 섹션만 표시
	$('#' + contentId).show();
}
