package com.study.mms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorDTO {
	private int status; // HTTP 상태 코드 (예: 400, 404, 500)
	private String message; // 오류 메시지 (예: "Resource not found")
}
