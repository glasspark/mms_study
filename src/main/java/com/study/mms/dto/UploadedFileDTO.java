package com.study.mms.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Data
@Builder
@Schema(description = "공유 파일 리스트 반환 API")
public class UploadedFileDTO {

	private Integer id;
	private LocalDateTime createdAt;
	private String title;
	private String fileName;
	private String filePath;
	private String nickname;
	private Boolean isRegistrant; //작성자 인지 확인
}
