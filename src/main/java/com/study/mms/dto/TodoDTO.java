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
@Schema(description = "Todo 리스트 반환 DTO")
public class TodoDTO {

	private Integer id;
	private String description;
	private boolean completed;

}
