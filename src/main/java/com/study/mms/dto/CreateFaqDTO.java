package com.study.mms.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Data
@Builder
@Schema(description = "자주 묻는 질문 생성 DTO")
public class CreateFaqDTO {
	private Integer id;
	private LocalDateTime createdAt;
	@NotBlank(message = "카테고리를 입력해 주세요")
	private String category;
	@NotBlank(message = "제목을 입력해 주세요")
	private String title;
	@NotBlank(message = "내용을 입력해 주세요")
	private String content;
}
