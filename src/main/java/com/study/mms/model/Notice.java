package com.study.mms.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
@Table(name = "notice")
public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Integer id;

	@CreatedDate
	@Column(name = "created_at")
	@Comment("생성일")
	private LocalDateTime createdAt;

	@PrePersist
	public void onPrePersist() {
		this.createdAt = LocalDateTime.now().withNano(0); // 댓글 생성일 설정, 소수점 이하 초 제거
	}

	@Column(name = "title", nullable = false)
	@Comment("제목")
	private String title;

	@Column(name = "content", columnDefinition = "TEXT", nullable = false)
	@Comment("내용")
	private String content;

	@Column(name = "img", nullable = true)
	@Comment("이미지")
	private String img;

	@Column(name = "is_pinned", nullable = false)
	@Comment("상단 고정 여부")
	private Boolean isPinned = false; // 상단 고정 여부

	@Column(name = "priority", nullable = true)
	@Comment("우선순위")
	private Integer priority; //상단 고정 우선순위

	@Builder
	public Notice(String title, String content, String img, Boolean isPinned, Integer priority) {
		this.title = title;
		this.content = content;
		this.img = img;
		this.isPinned = isPinned != null ? isPinned : false;
		this.priority = priority;
	}

	// 공지사항 업데이트 메서드
	public void update(String title, String content, String img, Boolean isPinned, Integer priority) {
		this.title = title;
		this.content = content;
		this.img = img;
		this.isPinned = isPinned != null ? isPinned : false;
		this.priority = priority;
	}

}
