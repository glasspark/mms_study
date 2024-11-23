package com.study.mms.dto;

import java.time.LocalDate;

import com.study.mms.model.StudyGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Data
@Builder
@Schema(description = "스터디 그룹 스케줄 리스트 DTO")
public class ScheduleDTO {
	private Integer id;
	private Integer groupId;
	private String title;
	private LocalDate startDate;
	private LocalDate endDate;
	private StudyGroup studyGroup;
}
