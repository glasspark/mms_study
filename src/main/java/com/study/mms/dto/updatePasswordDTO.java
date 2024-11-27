package com.study.mms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Data
@Builder
@Schema(description = "비밀번호 업데이트 DTO")
public class updatePasswordDTO {
	
	@Schema(description = "기존 비밀번호")
	private String password;
	@Schema(description = "신규 비밀번호")
	private String newPassword;
	@Schema(description = "비밀번호 확인")
	private String passwordCheck;

}
