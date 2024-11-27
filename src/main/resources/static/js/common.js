$(document).ready(function() {


	//======== 마이페이지 ========

	//기본적으로 보여질 화면 지정
	showContent('infoContent');
	// 사이드바 이벤트 관련 (클릭 시 보여줄 각 항목의 리스트들)
	$('#showInfo').on('click', function() {
		showContent('infoContent');
	});

	// 사이드바 이벤트 관련 (클릭 시 보여줄 각 항목의 리스트들)
	$('#defaultInfo').on('click', function() {
		showContent('infoContent');
	});
	// 사이드바 이벤트 관련 (클릭 시 보여줄 각 항목의 리스트들)
	$('#showInquiry').on('click', function() {
		showContent('infoInquiryContent');
	});







});