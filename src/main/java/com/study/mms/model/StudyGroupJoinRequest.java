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
@Table(name = "study_group_join_request")
public class StudyGroupJoinRequest { // 스터디 그룹 회원가입 신청에 관한 상태를 보관하는 엔티티

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "study_group_id", nullable = false)
	private StudyGroup studyGroup;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private RequestStatus status;

	public enum RequestStatus {
		PENDING, // 가입 신청이 대기 중
		APPROVED, // 가입 신청이 승인
		REJECTED // 가입 신청이 거절
	}

	@Column(name = "requested_at")
	@Comment("신청일자")
	private LocalDateTime requestedAt;

	@PrePersist
	public void onPrePersist() {
		this.requestedAt = LocalDateTime.now().withNano(0); // 소수점 이하 초 제거
	}

}
