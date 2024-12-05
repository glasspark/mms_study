package com.study.mms.dto;

import java.time.LocalDateTime;

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
@Schema(description = " 공지사항 생성, 수정 DTO")
public class NoticeDTO {

	private Integer id;
	private LocalDateTime createdAt;
	private String title;
	private String content;
	private Boolean isPinned;
	private String img;
	private Integer priority; //상단 고정 우선순위

}
