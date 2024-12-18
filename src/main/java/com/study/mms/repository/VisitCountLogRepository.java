package com.study.mms.repository;

import com.study.mms.model.VisitCountLog;
import com.study.mms.model.VisitorsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitCountLogRepository extends JpaRepository<VisitCountLog, Integer> {


    @Query(value = "WITH RECURSIVE dates AS (" +
            "    SELECT :startDate AS date " +
            "    UNION ALL " +
            "    SELECT DATE_ADD(date, INTERVAL 1 DAY) " +
            "    FROM dates " +
            "    WHERE date < :endDate" +
            ") " +
            "SELECT d.date, IFNULL(COUNT(v.id), 0) AS count " +
            "FROM dates d " +
            "LEFT JOIN visitCount_log v " +
            "ON DATE_FORMAT(v.visited_at, '%Y-%m-%d') = d.date " +
            "GROUP BY d.date " +
            "ORDER BY d.date", nativeQuery = true)
    List<Object[]> findDailyVisitCountBetweenDates(@Param("startDate") String startDate,
                                                   @Param("endDate") String endDate);


}
