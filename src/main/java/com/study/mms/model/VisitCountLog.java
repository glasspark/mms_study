package com.study.mms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor //기본 생성자 생성
@AllArgsConstructor //모든 필드 포함하는 기본 생성자 생성
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
@Table(name = "visitCount_log")
public class VisitCountLog {
    //방문 횟수를 기록
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Integer id;

    @CreatedDate
    @Column(name = "visited_at")
    @Comment("방문 시간")
    private LocalDateTime visitedAt;

    @PrePersist
    public void onPrePersist() {
        this.visitedAt = LocalDateTime.now().withNano(0); // 댓글 생성일 설정, 소수점 이하 초 제거
    }

    @Column(name = "user_id", nullable = false)
    @Comment("유저아이디")
    private String userId;

    @Builder
    public VisitCountLog(String userId) {
        this.userId = userId;
    }

}
