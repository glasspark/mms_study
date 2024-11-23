package com.study.mms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Getter
@Schema(description = "회원가입 요청을 위한 DTO")
public class UsersJoinDTO {

	@Schema(description = "아이디")
	private String username;
	@Schema(description = "닉네임")
	private String nickname;
	@Schema(description = "이메일")
	private String email;
	@Schema(description = "비밀번호1")
	private String password1;
	@Schema(description = "비밀번호2")
	private String password2;

}
