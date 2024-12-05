package com.study.mms.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor //모은 필드 생성자
@NoArgsConstructor //기본 생성자 
public class PagedResponseDTO<T> {
	private List<T> items; // 데이터 리스트
	private Integer currentPage; // 현재 페이지
	private Integer totalItems; // 전체 아이템 개수
	private Integer totalPages; // 전체 페이지 수
}
