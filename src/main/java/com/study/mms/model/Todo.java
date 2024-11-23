package com.study.mms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "todo")
public class Todo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "description", nullable = false)
	@Comment("해야할 일")
	private String description;

	@Builder.Default // 빌더에서 기본값을 적용하도록 설정
	@Column(name = "completed", nullable = false)
	@Comment("완료 여부")
	private boolean completed = false; // 초기값을 false로 설정

	@ManyToOne
	@JoinColumn(name = "user_id") // user_id 외래키 생성
	private User user;

}
