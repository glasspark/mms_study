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
@Schema(description = "스터디그룹 멤버  DTO")
public class StudyGroupMemberDTO {

	private Integer userId;
	private String nickname;
	private String imgPath;
	private Boolean imgType;
	private String role;
	// 방장 여부 추가 true 이면 멤버 탈퇴 할 수 있는 삭제 부분 보여질 수 있도록 
	private Boolean isLeader; 
}
