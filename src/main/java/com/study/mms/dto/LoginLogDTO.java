package com.study.mms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Getter
@Builder
@Schema(description = "출석체크 리스트 반환 DTO")
public class LoginLogDTO {

 private List<LoginLogDTO.visitorsDTO> visitors;
 private List<LoginLogDTO.visitCountDTO> visitCount;

 @NoArgsConstructor
 @AllArgsConstructor
 @Getter
 @Builder
 @Schema(description = "방문자수 DTO")
 public static class visitorsDTO {
  private String date;
  private Integer count;

 }
 @NoArgsConstructor
 @AllArgsConstructor
 @Getter
 @Builder
 @Schema(description = "방문횟수 DTO")
 public static class visitCountDTO {
  private String date;
  private Integer count;

 }

 
}
