package com.study.mms.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	// 400 BAD_REQUEST 잘못된 요청
	INVALID_PARAMETER(400, "파라미터 값을 확인해주세요."),

	// 404 NOT_FOUND 잘못된 리소스 접근
	GROUP_NOT_FOUND(404, "존재하지 않는 스터디 그룹입니다."), USER_NOT_FOUND(404, "이용자를 조회할 수 없습니다."),
	FESTIVAL_NOT_FOUND(404, "존재하지 않는 페스티벌 ID 페스티벌입니다."), SAVED_DISPLAY_NOT_FOUND(404, "저장하지 않은 전시회입니다."),
	SAVED_FAIR_NOT_FOUND(404, "저장하지 않은 박람회입니다."), SAVED_FESTIVAL_NOT_FOUND(404, "저장하지 않은 페스티벌입니다."),
	BOARD_NOT_FOUND(404, "존재하지 않는 게시판 입니다."),
	// 409 CONFLICT 중복된 리소스
	ALREADY_SAVED_DISPLAY(409, "이미 저장한 전시회입니다."), ALREADY_SAVED_FAIR(409, "이미 저장한 박람회입니다."),
	ALREADY_SAVED_FESTIVAL(409, "이미 저장한 페스티벌입니다."),

	// 500 INTERNAL SERVER ERROR
	INTERNAL_SERVER_ERROR(500, "서버 에러입니다.");

	private final int status;
	private final String message;
}
