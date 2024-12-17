package com.study.mms.model;


import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함하는 생성자 생성
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
@Table(name = "visitors_log")
public class VisitorsLog {

    //방문자 수를 기록(중복 허용)
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
    public VisitorsLog(String userId) {
        this.userId = userId;
    }

}
