package com.study.mms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Getter
@Builder
@Schema(description = "스터디 그룹 회원가입 신청 및 방장, 관리자 승인 시 사용 DTO")
public class StudyJoinDTO {
	private String name; //스터디 그룹 이름
	private Integer studyNum; // 스터디 그룹의 고유 번호
	private Integer userId;
	private String username;
	private Integer requestId;
}
