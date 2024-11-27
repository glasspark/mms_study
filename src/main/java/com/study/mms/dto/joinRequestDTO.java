package com.study.mms.dto;

import java.time.LocalDateTime;

import com.study.mms.model.StudyGroupJoinRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Getter
@Builder
@Schema(description = "스터디 그룹 가입신청 리스트 DTO")
public class joinRequestDTO {

	private Integer id;
	private String name;
	private LocalDateTime requestedAt;
	private String status;

	// StudyGroupJoinRequest 엔티티를 기반으로 DTO 생성하는 빌더 패턴 메서드
	public static joinRequestDTO fromEntity(StudyGroupJoinRequest request) {
		return joinRequestDTO.builder().id(request.getId()).name(request.getStudyGroup().getName()) // 스터디 그룹 이름
				.requestedAt(request.getRequestedAt()).status(request.getStatus().name()) // Enum 값을 문자열로 변환
				.build();
	}

}
