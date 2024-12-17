package com.study.mms.repository;

import com.study.mms.model.VisitCountLog;
import com.study.mms.model.VisitorsLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VisitCountLogRepository extends JpaRepository<VisitCountLog, Integer> {


}
