package com.study.mms.dto;

import java.time.LocalDateTime;

import com.study.mms.model.StudyGroupJoinRequest;
import com.study.mms.model.StudyGroupJoinRequest.RequestStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Getter
@Builder
@Schema(description = "스터디그룹 가입신청 리스트 반환 DTO")
public class ReponseJoinDTO {
	private Integer requestId;
	private Integer userId;
	private String username;
	private String nickname;
	private String email;
	private LocalDateTime requestedAt; // 신청 일자
	private Integer studyGroupId;
	private RequestStatus status;

	// 생성자에 필요한 필드만 초기화하도록 작성
	public ReponseJoinDTO(StudyGroupJoinRequest request) {
		this.requestId = request.getId();
		this.userId = request.getUser().getId();
		this.username = request.getUser().getUsername();
		this.nickname = request.getUser().getNickname();
		this.requestedAt = request.getRequestedAt();
		this.studyGroupId = request.getStudyGroup().getId();
		this.status = request.getStatus();
	}

}
