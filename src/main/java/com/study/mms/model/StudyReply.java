package com.study.mms.model;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
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

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
@Table(name = "study_relpy")
public class StudyReply {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(nullable = false, length = 200)
	@Comment("답글 내용")
	private String content;

	// 글 작성자
	@ManyToOne
	@JoinColumn(name = "user_id")
	@Comment("작성자")
	private User user;

	
	@CreatedDate
	@Column(name = "created_at")
	@Comment("답글 작성일")
	private LocalDateTime createdAt;

	@PrePersist
	public void onPrePersist() {
		this.createdAt = LocalDateTime.now().withNano(0); // 댓글 생성일 설정, 소수점 이하 초 제거
	}

	@ManyToOne
	@JoinColumn(name = "study_comment", nullable = false)
	private StudyComment studyComment;

}
