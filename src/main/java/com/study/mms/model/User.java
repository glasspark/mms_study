package com.study.mms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "username", nullable = false, unique = true)
	@Comment("아이디")
	private String username;

	@Column(name = "nickname", nullable = false, unique = true)
	@Comment("닉네임")
	private String nickname;

	@Column(name = "email", nullable = false, unique = true)
	@Comment("이메일")
	private String email;

	@Column(name = "password", nullable = false)
	@Comment("패스워드")
	private String password;

	@Column(name = "role", nullable = false)
	@Comment("권한")
	private String role;

	@Column(name = "salt", nullable = false)
	@Comment("salt")
	private String salt;

	@CreatedDate
	@Column(name = "created")
	@Comment("계정 생성")
	private LocalDateTime create;

	@Column(name = "img_name")
	@Comment("이미지 이름")
	private String img_name;

	@Column(name = "img_path")
	@Comment("이미지 경로")
	private String img_path;

	@Column(name = "img_type", nullable = false)
	@Comment("프로필 이미지 타입") // 기본 이미지면 false , 따로 지정한 이미지 이면 true
	private Boolean img_type = false;

	@PrePersist
	public void onPrePersist() {
		this.create = LocalDateTime.now().withNano(0); // 소수점 이하 초 제거
	}

	// Todo 리스트 연결
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Todo> todos = new ArrayList<>();

	// 출석체크 연결
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Attendance> attendance = new ArrayList<>();

	// 방장으로 있는 스터디 그룹
	@OneToMany(mappedBy = "leader")
	private List<StudyGroup> ledStudyGroups;

	// 부반장으로 있는 스터디 그룹
	@OneToMany(mappedBy = "subLeader")
	private List<StudyGroup> subLedStudyGroups;

	// 사용자가 작성한 댓글들
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudyComment> studyComments;

	// 글 작성자
	@ManyToOne
	@JoinColumn(name = "user_id")
	@Comment("작성자")
	private User user;

	// 1 : 1 문의
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Inquiry> Inquiry;

	//아래의 것은 고래해 볼 것(회원 탈퇴하면 전체 사라지는것인지)
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudyGroupMember> studyGroupMemberships;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudyBoard> studyBoards;

}
