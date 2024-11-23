package com.study.mms.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
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
@Table(name = "Study_comment")
public class StudyComment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "content", nullable = false)
	@Comment("댓글 내용")
	private String content;

	@Column(name = "created_at")
	@Comment("댓글 작성일")
	private LocalDateTime createdAt;

	@PrePersist
	public void onPrePersist() {
		this.createdAt = LocalDateTime.now().withNano(0); // 댓글 생성일 설정, 소수점 이하 초 제거
	}

	// 글 작성자
	@ManyToOne
	@JoinColumn(name = "user_id")
	@Comment("작성자")
	private User user;

	// 스터디 그룹
	@ManyToOne
	@JoinColumn(name = "study_group_id")
	@Comment("스터디 그룹")
	private StudyGroup studyGroup;

	// 스터디 그룹 댓글 관련
	@OneToMany(mappedBy = "studyComment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudyReply> studyReply;

}

