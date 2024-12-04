package com.study.mms.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Getter
@Builder
@Schema(description = "내 스터디 게시판 댓글, 답글 데이터 반환 DTO")
public class BoardCommentWithRepliesDTO {

	@Schema(description = "댓글 ID")
	private Integer commentId;

	@Schema(description = "댓글 내용")
	private String content;

	@Schema(description = "댓글 작성자 닉네임")
	private String author;

	@Schema(description = "댓글 작성자 프로필 이미지")
	private String imgPath;

	@Schema(description = "댓글 작성 시간")
	private LocalDateTime createdAt;

	@Schema(description = "댓글에 대한 답글 리스트")
	private List<ReplyDTO> replies;

	@Schema(description = "댓글 작성 여부")
	private Boolean isAuthor;

	// 답글 DTO 내부 클래스 정의
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Builder
	@Schema(description = "답글 데이터 DTO")
	public static class ReplyDTO {
		@Schema(description = "답글 ID")
		private Integer replyId;

		@Schema(description = "답글 내용")
		private String content;

		@Schema(description = "답글 작성자 닉네임")
		private String author;
		
		@Schema(description = "답글 작성자 프로필 이미지")
		private String imgPath;

		@Schema(description = "답글 작성 시간")
		private LocalDateTime createdAt;

		@Schema(description = "답글 작성 여부")
		private Boolean isAuthor;
	}
}
