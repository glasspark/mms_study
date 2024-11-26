package com.study.mms.dto;

import java.time.LocalDateTime;

import com.study.mms.model.StudyBoard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Data
@Builder
@Schema(description = "스터디 그룹 게시판 생성 및 수정  DTO")
public class StudyBoardDTO {
	private Integer id;
	private String title;
	private String content;
	private String img;
	private Integer groupId;
	private String nickName;
	private boolean isAuthor; // 작성자 여부
	private LocalDateTime createdAt; // 생성 시간 추가

	public StudyBoardDTO(StudyBoard studyBoard) {
		this.id = studyBoard.getId();
		this.title = studyBoard.getTitle();
		this.content = studyBoard.getContent();
		this.img = studyBoard.getImg();
		this.groupId = studyBoard.getStudyGroup() != null ? studyBoard.getStudyGroup().getId() : null;
		this.nickName = studyBoard.getUser() != null ? studyBoard.getUser().getNickname() : null;
	}
}
