package com.study.mms.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
@Table(name = "board_relpy")
public class BoardReply {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "content", length = 1500, nullable = false)
	@Size(max = 1500, message = "댓글은 최대 1500자까지 작성할 수 있습니다.")
	@Comment("답글 내용")
	private String content;

	@CreatedDate
	@Column(name = "created_at")
	@Comment("답글 작성일")
	private LocalDateTime createdAt;

	@PrePersist
	public void onPrePersist() {
		this.createdAt = LocalDateTime.now().withNano(0); // 댓글 생성일 설정, 소수점 이하 초 제거
	}

	@ManyToOne
	@JoinColumn(name = "user_id")
	@Comment("작성자")
	private User user;

	@ManyToOne
	@JoinColumn(name = "study_comment_id")
	@Comment("스터디 그룹 게시판 댓글")
	private BoardComment boardComment;

}
