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
@Table(name = "uploaded_file")
public class UploadedFile {

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
	
	@Column(nullable = false, name = "title")
	@Comment("제목")
	private String title;

	@Column(nullable = false, name = "file_name")
	@Comment("파일이름")
	private String fileName;

	@Column(nullable = false, name = "file_path")
	@Comment("파일경로")
	private String filePath;

	@ManyToOne
	@JoinColumn(name = "study_group_id")
	private StudyGroup studyGroup; // 스터디 그룹과 매핑
	
	@Column(nullable = false, name = "user_id")
	@Comment("유저아이디")
	private Integer userId;


}
