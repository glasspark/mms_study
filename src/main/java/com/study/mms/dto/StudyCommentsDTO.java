package com.study.mms.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.study.mms.model.StudyComment;
import com.study.mms.model.StudyReply;
import com.study.mms.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Getter
@Builder
@Schema(description = "스터디 그룹 댓글 리스트 반환 DTO")
public class StudyCommentsDTO {
	private Integer id;
	private String content;
	private LocalDateTime createdAt;
	private String nickname;
	private String imgPath;
	private Boolean isAuthor; // 현재 사용자가 작성자인지 여부
	private List<StudyReplyDTO> replies; // 댓글의 답글 리스트 추가

	public static class StudyCommentsDTOBuilder {

		public StudyCommentsDTOBuilder userInfo(User user) {
			if (user != null) {
				this.nickname = user.getNickname(); // 유저 닉네임 설정
				this.imgPath = user.getImg_path(); // 유저 프로필 이미지 경로 설정
			} else {
				this.nickname = null;
				this.imgPath = null;
			}
			return this;
		}
	}

	// 엔티티를 DTO로 변환하는 static 메서드
	public static StudyCommentsDTO fromEntity(StudyComment studyComment, List<StudyReply> studyReplies,
			User currentUser) {
		
		List<StudyReplyDTO> replyDTOs = studyReplies.stream()
				.map(reply -> StudyReplyDTO.builder().id(reply.getId()).content(reply.getContent())
						.createdAt(reply.getCreatedAt()).nickname(reply.getUser()
								.getNickname()).isAuthor(reply.getUser().getId().equals(currentUser.getId()))
						.imgPath(reply.getUser().getImg_path()).build())
				.collect(Collectors.toList());

		return StudyCommentsDTO.builder().id(studyComment.getId()).content(studyComment.getContent())
				.createdAt(studyComment.getCreatedAt()).nickname(studyComment.getUser().getNickname())
				.imgPath(studyComment.getUser().getImg_path()).isAuthor(studyComment.getUser().getId().equals(currentUser.getId()))
				.replies(replyDTOs).build();
	}

}
