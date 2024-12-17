package com.study.mms.service;

import com.study.mms.model.VisitCountLog;
import com.study.mms.model.VisitorsLog;
import com.study.mms.repository.VisitCountLogRepository;
import com.study.mms.repository.VisitorsLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor // 생성자 의존성 주입
public class LoginLogService {

    private final VisitorsLogRepository visitorsLogRepository; //방문자 수를 기록
    private final VisitCountLogRepository visitCountLogRepository;  //방문 횟수를 기록


    //방문자를 기록
    public void addVisitor(String userId) {

        // 오늘의 시작 시간 (00:00:00)과 끝나는 시간 (23:59:59.999999)
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        // Repository를 통해 조회
        Optional<VisitorsLog> visitLog = visitorsLogRepository.findByUserIdAndVisitedAtBetween(userId, startOfDay, endOfDay);


        // 방문자가 기록되어 있지 않는다면 : 저장
        if (visitLog.isEmpty()) {

            VisitorsLog visitorsLog = VisitorsLog.builder()
                    .userId(userId)
                    .build();
            visitorsLogRepository.save(visitorsLog);
        }
    }

    //방문횟수를 기록
    public void addCount(String userId) {
        VisitCountLog countLog = VisitCountLog.builder().userId(userId).build();
        visitCountLogRepository.save(countLog);
    }


}
