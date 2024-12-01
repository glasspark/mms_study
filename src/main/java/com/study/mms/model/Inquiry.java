package com.study.mms.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Builder // 빌더 패턴 활성화
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함하는 생성자 생성
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
@Table(name = "inquiry")
public class Inquiry {
	
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
		this.createdAt = LocalDateTime.now().withNano(0); // 소수점 이하 초 제거
	}

	@Column(name = "category", nullable = false)
	@Comment("카테고리")
	private String category;

	@Column(name = "title", nullable = false)
	@Comment("제목")
	private String title;

	@Column(name = "content", columnDefinition = "TEXT", nullable = false)
	@Comment("내용")
	private String content;
	
	@Column(name = "answer", columnDefinition = "TEXT")
	@Comment("답변")
	private String answer;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
   
	  // 문의 상태 (예: 대기중, 답변완료 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status;

    public enum InquiryStatus {
        WAITING,    // 대기중
        ANSWERED    // 답변완료
    }
    
}
