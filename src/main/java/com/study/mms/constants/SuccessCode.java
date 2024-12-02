package com.study.mms.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SuccessCode {
	// 200 요청 성공
	DATA_CREATED(201, "저장되었습니다.");
	
	private final int status;
	private final String message;
}
