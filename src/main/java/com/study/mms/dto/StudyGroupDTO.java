package com.study.mms.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.study.mms.model.StudyGroup.ApprovalStatus;
import com.study.mms.model.StudyGroup.RecruitmentStatus; // 추가
import com.study.mms.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Getter
@Setter
@Builder
@Schema(description = "스터디 그룹 반환 DTO")
public class StudyGroupDTO {
	private Integer id;
	private String name;
	private LocalDateTime createdAt;
	private String status;
	private String recruitmentStatus; //모집중, 마감, 승인 대기 상태 분류
	private String description;
	private String tag;
	private String leader;
	private String leaderUsername;
	private String subLeader;
	private LocalDateTime approvalDecisionAt;
	private int memberCount;
	private Integer maxMembers;
	private List<String> tagList; // 분리된 태그 리스트
	private Integer commentLength;
	private String role; //방장, 부방장, 회원 상태 확인
	


	// Builder 설정을 위한 추가 메서드
	public static class StudyGroupDTOBuilder {
		private static final int MAX_NAME_LENGTH = 160;

		public StudyGroupDTOBuilder description(String description) {
			// 문자열 길이 제한
			this.description = (description != null && description.length() > MAX_NAME_LENGTH)
					? description.substring(0, MAX_NAME_LENGTH) + "..."
					: description;
			return this;
		}

		public StudyGroupDTOBuilder status(ApprovalStatus status) {
			this.status = status != null ? status.name() : null;
			return this;
		}

		public StudyGroupDTOBuilder recruitmentStatus(RecruitmentStatus recruitmentStatus) {
			this.recruitmentStatus = recruitmentStatus != null ? recruitmentStatus.name() : null;
			return this;
		}

		public StudyGroupDTOBuilder leader(User leader) {
			// 리더의 이름 설정 (leader가 null일 수도 있으므로 체크)
			this.leader = (leader != null) ? leader.getNickname() : null;
			this.leaderUsername = (leader != null) ? leader.getUsername() : null;
			return this;
		}

		public StudyGroupDTOBuilder subLeader(User subLeader) {
			// 부리더의 이름 설정 (subLeader가 null일 수도 있으므로 체크)
			this.subLeader = (subLeader != null) ? subLeader.getNickname() : null;
			return this;
		}

		// 멤버의 수 반환
		public StudyGroupDTOBuilder memberCount(int memberCount) {
			this.memberCount = memberCount;
			return this;
		}

		public StudyGroupDTOBuilder tag(String tag) {
			this.tag = tag;
			this.tagList = tag != null ? Arrays.asList(tag.split(",")) : Collections.emptyList();
			return this;
		}
	}

}
