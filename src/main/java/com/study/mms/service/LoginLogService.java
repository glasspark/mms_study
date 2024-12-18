package com.study.mms.service;

import com.study.mms.constants.ErrorCode;
import com.study.mms.constants.SuccessCode;
import com.study.mms.dto.LoginLogDTO;
import com.study.mms.exception.CustomException;
import com.study.mms.model.Notice;
import com.study.mms.model.VisitCountLog;
import com.study.mms.model.VisitorsLog;
import com.study.mms.repository.VisitCountLogRepository;
import com.study.mms.repository.VisitorsLogRepository;
import com.study.mms.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

            VisitorsLog visitorsLog = VisitorsLog.builder().userId(userId).build();
            visitorsLogRepository.save(visitorsLog);
        }
    }

    //방문횟수를 기록
    public void addCount(String userId) {
        VisitCountLog countLog = VisitCountLog.builder().userId(userId).build();
        visitCountLogRepository.save(countLog);
    }


    //방문자수, 방문 횟수 데이터 반환(관리자 페이지 사용)
    public ResponseEntity<Map<String, Object>> getUserLoginLog(Integer year, Integer month) {
        //데이터 값 유효성 확인
        if (year == null || month == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        if (year > LocalDate.now().getYear()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        if (month < 1 || month > 12) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }


// 1. 해당 월의 첫 번째 날
        LocalDate startDate = LocalDate.of(year, month, 1);

        // 2. 해당 월의 마지막 날
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 3. 날짜를 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDateString = startDate.format(formatter);
        String endDateString = endDate.format(formatter);

        // 방문자 수
        List<LoginLogDTO.visitorsDTO> visitors = visitorsLogRepository
                .findDailyLogCountsBetweenDates(startDateString, endDateString)
                .stream()
                .map(data -> LoginLogDTO.visitorsDTO.builder()
                        .date(data[0].toString())
                        .count(((Number) data[1]).intValue())
                        .build())
                .collect(Collectors.toList());

        // 방문 횟수
        List<LoginLogDTO.visitCountDTO> visitCounts = visitCountLogRepository
                .findDailyVisitCountBetweenDates(startDateString, endDateString)
                .stream()
                .map(data -> LoginLogDTO.visitCountDTO.builder()
                        .date(data[0].toString())
                        .count(((Number) data[1]).intValue())
                        .build())
                .collect(Collectors.toList());


        // DTO 생성 및 반환
        LoginLogDTO responseDTO = LoginLogDTO.builder().visitors(visitors).visitCount(visitCounts).build();


        //방문 횟수
        return ResponseUtil.buildSuccessResponseWithData(HttpStatus.OK, SuccessCode.DATA_FETCHED.getMessage(), responseDTO);
    }


}
