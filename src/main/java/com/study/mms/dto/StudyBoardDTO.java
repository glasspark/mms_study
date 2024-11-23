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
@Schema(description = "스터디 그룹 게시판 생성  DTO")
public class StudyBoardDTO {
	private Integer id;
	private String title;
	private String content;
	private String img;
	private Integer groupId;
}
