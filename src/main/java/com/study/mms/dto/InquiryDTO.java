package com.study.mms.dto;

import java.time.LocalDateTime;

import com.study.mms.model.Inquiry.InquiryStatus;
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
@Schema(description = "1:1 질문 리스트 반환 DTO")
public class InquiryDTO {

	private Integer id;
	private LocalDateTime createdAt;
	private String category;
	private String title;
	private String content;
	private String nickname;
	private String username;
	private Integer userId;
	private String status;
	private String answer;

	public static class InquiryDTOBuilder {

		public InquiryDTOBuilder user(User user) {
			// 리더의 이름 설정 (leader가 null일 수도 있으므로 체크)
			this.username = (user != null) ? user.getUsername() : null;
			this.nickname = (user != null) ? user.getNickname() : null;
			this.userId = (user != null) ? user.getId() : null;
			return this;
		}
		
		public InquiryDTOBuilder inquiryStatus(InquiryStatus  inquiryStatus) {
			this.status = (inquiryStatus != null) ? inquiryStatus.name() : null;
			return this;
		}

	}

}
