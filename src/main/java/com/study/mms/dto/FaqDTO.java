package com.study.mms.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Getter
@Builder
@Schema(description = "자주 묻는 질문 DTO")
public class FaqDTO {
	private Integer id;
	private LocalDateTime createdAt;
	private String category;
	private String title;
	private String content;
}
