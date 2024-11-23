$(document).ready(function() {


	/*	$('.daterange-picker').daterangepicker({
			locale: {
				format: 'YYYY-MM-DD'  // 날짜 형식 설정
			},
			opens: 'right',            // 달력 팝업이 열리는 위치
			autoApply: true,           // 시작일과 종료일 선택 시 자동으로 달력 닫힘
			startDate: moment().startOf('month'), // 기본 시작일 (이달의 첫째 날)
			endDate: moment().endOf('month')      // 기본 종료일 (이달의 마지막 날)
		});
	*/

	$('.daterange-picker').daterangepicker({
		autoUpdateInput: false,
		locale: {
			cancelLabel: 'Clear',
			format: 'YYYY-MM-DD' // 서버로 전송할 형식
		}
	});

	$('.daterange-picker').on('apply.daterangepicker', function(ev, picker) {
		$(this).val(picker.startDate.format('YYYY-MM-DD') + ' - ' + picker.endDate.format('YYYY-MM-DD'));

		// 선택된 날짜 범위를 hidden 필드에 저장
		$('input[name="startDate"]').val(picker.startDate.format('YYYY-MM-DD'));
		$('input[name="endDate"]').val(picker.endDate.format('YYYY-MM-DD'));
	});

	$('.daterange-picker').on('cancel.daterangepicker', function(ev, picker) {
		$(this).val('');
		$('input[name="startDate"]').val('');
		$('input[name="endDate"]').val('');
	});
	
	
	// 승인 버튼 클릭
	$('.approve-btn, .reject-btn').on('click', function() {
		let groupId = $(this).data('id'); // data-id에서 그룹 ID 가져오기
		let status = $(this).data('status'); // data-status에서 상태 가져오기 (approve or reject)
		let button = $(this);

		$.ajax({
			url: '/api/admin/group/approve',
			type: 'POST',
			data: {
				num: groupId,      // 서버에서 받을 변수명에 맞춤
				status: status     // 승인 또는 거절 상태 전송
			},
			success: function(response) {
				alert(response.message);
				// 승인 또는 거절 후 상태 업데이트 및 버튼 숨기기
				let newStatus = (status === 'approve') ? 'APPROVED' : 'REJECTED';
				button.closest('tr').find('.status-cell').text(newStatus); // 상위 tr 요소에서 status-cell 찾기
				button.closest('td').find('.approve-btn, .reject-btn').hide();
			},
			error: function(xhr) {
				alert("처리 중 오류가 발생했습니다.");
			}
		});
	});
});