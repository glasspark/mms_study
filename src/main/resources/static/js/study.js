$(document).ready(function() {

	


	//초기화 버튼
	$('#resetButton').on('click', function() {
		$('#groupSearch').val('');
		$('#tagSearch').val('');

		//리스트 처음부터 다시 가져오기
		$.ajax({
			type: 'GET',
			url: '/study', // 서버 엔드포인트 URL
			data: {
				page: 0, // 페이지 번호
				type: $('#type').val(),
				searchText: $('#groupSearch').val() || null, // 빈 문자열이면 null로 설정
				tag: $('#tagSearch').val() || null // 빈 문자열이면 null로 설정
			},
			success: function(response) {
				// 받아온 데이터를 studyContent 영역에 업데이트
				$('#studyContent').html(response);
			},
			error: function() {
				alert('데이터를 가져오는 중 오류가 발생했습니다.');
			}
		});
	});

});