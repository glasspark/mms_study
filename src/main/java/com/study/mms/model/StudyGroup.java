package com.study.mms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
@Table(name = "study_group")
public class StudyGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "name", nullable = false, unique = true)
	@Comment("스터디그룹 이름")
	private String name;

	@Column(name = "description")
	@Comment("그룹 설명")
	private String description;

	@Column(name = "created_at")
	@Comment("생성일")
	private LocalDateTime createdAt;

	@Column(name = "approval_decision_at")
	@Comment("승인일")
	private LocalDateTime approvalDecisionAt;

	@PrePersist
	public void onPrePersist() {
		this.createdAt = LocalDateTime.now().withNano(0); // 소수점 이하 초 제거
	}

	// 상태가 승인으로 되면 자동으로 시간을 넣음
	@PreUpdate
	public void onPreUpdate() {
		if ((this.status == ApprovalStatus.APPROVED || this.status == ApprovalStatus.REJECTED)
				&& this.approvalDecisionAt == null) {
			this.approvalDecisionAt = LocalDateTime.now().withNano(0); // 승인/거절 결정일 설정, 소수점 이하 초 제거
		}
	}

	@Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
	@Column(name = "status", nullable = false)
	@Comment("승인 상태")
	private ApprovalStatus status = ApprovalStatus.PENDING; // 기본값을 대기로 설정

	@Column(name = "max_members")
	@Comment("최대 인원")
	private Integer maxMembers;

	// 방장
	@ManyToOne
	@JoinColumn(name = "leader_id")
	private User leader;

	// 부방장
	@ManyToOne
	@JoinColumn(name = "sub_leader_id")
	private User subLeader;

	// 팀원들 (고려 : 그룹이 삭제되면?)
	@OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL)
	private List<StudyGroupMember> members;

	public enum ApprovalStatus {
		PENDING, // 대기
		APPROVED, // 승인됨
		REJECTED // 반려됨
	}

	@Column(name = "study_tag")
	@Comment("태그 입력")
	private String studyTag;

	@Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
	@Column(name = "recruitment_status", nullable = false)
	@Comment("승인 상태")
	private RecruitmentStatus recruitmentStatus = RecruitmentStatus.PENDING_APPROVAL; // 기본값을 대기로 설정

	public enum RecruitmentStatus {
		RECRUITING, // 모집중
		RECRUITMENT_CLOSED, // 마감
		PENDING_APPROVAL // 승인대기
	}

	// 스터디 그룹 댓글 관련
	@OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudyGroupJoinRequest> studyGroupJoinRequests;
	
	// 스터디 그룹 댓글 관련
	@OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudyComment> comments;

	// 스터디 그룹 일정
	@OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GroupSchedule> schedules = new ArrayList<>();

	// 공유 파일 이미지 경로
	@OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UploadedFile> files;
	
	//스터디 그룹 게시판
	@OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudyBoard> studyBoards;
}
