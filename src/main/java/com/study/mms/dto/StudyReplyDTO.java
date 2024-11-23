package com.study.mms.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyReplyDTO {
	private Integer id;
	private String content;
	private LocalDateTime createdAt;
	private String nickname;
	private String imgPath;
	private Boolean isAuthor; // 현재 사용자가 작성자인지 여부
}
