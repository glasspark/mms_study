package com.study.mms.repository;

import com.study.mms.model.VisitorsLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VisitorsLogRepository extends JpaRepository<VisitorsLog, Integer> {

    //동일한 아이디를 가진 사람이 오늘 log에 저장이 되어져 있는지 확인
    // 당일 userId의 방문 기록 확인
    Optional<VisitorsLog> findByUserIdAndVisitedAtBetween(String userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
