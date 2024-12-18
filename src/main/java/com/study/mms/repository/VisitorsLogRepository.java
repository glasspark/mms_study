package com.study.mms.repository;

import com.study.mms.model.VisitorsLog;
import org.hibernate.query.NativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.annotation.Native;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitorsLogRepository extends JpaRepository<VisitorsLog, Integer> {

    //동일한 아이디를 가진 사람이 오늘 log에 저장이 되어져 있는지 확인
    // 당일 userId의 방문 기록 확인
    Optional<VisitorsLog> findByUserIdAndVisitedAtBetween(String userId, LocalDateTime startOfDay, LocalDateTime endOfDay);

//    @Query(value = "SELECT DATE_FORMAT(visited_at, '%Y-%m-%d') AS date, COUNT(*) AS count " +
//            "FROM visitors_log " +
//            "WHERE YEAR(visited_at) = :year AND MONTH(visited_at) = :month " +
//            "GROUP BY DATE_FORMAT(visited_at, '%Y-%m-%d') " +
//            "ORDER BY date", nativeQuery = true)
//    List<Object[]> findDailyLogCountsByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query(value = "WITH RECURSIVE dates AS (" +
            "    SELECT :startDate AS date " +
            "    UNION ALL " +
            "    SELECT DATE_ADD(date, INTERVAL 1 DAY) " +
            "    FROM dates " +
            "    WHERE date < :endDate" +
            ") " +
            "SELECT d.date, IFNULL(COUNT(v.id), 0) AS count " +
            "FROM dates d " +
            "LEFT JOIN visitors_log v " +
            "ON DATE_FORMAT(v.visited_at, '%Y-%m-%d') = d.date " +
            "GROUP BY d.date " +
            "ORDER BY d.date", nativeQuery = true)
    List<Object[]> findDailyLogCountsBetweenDates(@Param("startDate") String startDate,
                                                  @Param("endDate") String endDate);

}
