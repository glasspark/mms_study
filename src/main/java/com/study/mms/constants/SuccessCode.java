package com.study.mms.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SuccessCode {
	// 200 요청 성공
	DATA_CREATED(201, "저장되었습니다."), DATA_UPDATE(204, "수정되었습니다."), DATA_DELETE(204, "삭제되었습니다."),
	DATA_FETCHED(200, "조회되었습니다.");

	private final int status;
	private final String message;
}
