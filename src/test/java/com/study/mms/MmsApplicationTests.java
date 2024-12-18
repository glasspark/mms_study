package com.study.mms;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.model.User;
import com.study.mms.model.VisitCountLog;
import com.study.mms.model.VisitorsLog;
import com.study.mms.repository.UserRepository;
import com.study.mms.repository.VisitCountLogRepository;
import com.study.mms.repository.VisitorsLogRepository;
import com.study.mms.service.LoginLogService;
import com.study.mms.service.StudyGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(false) // 롤백 방지, 실제 DB에 데이터 유지
class MmsApplicationTests {

    @Mock
    private Authentication authentication;

    @Mock
    private PrincipalDetail principalDetail;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VisitorsLogRepository visitorsLogRepository; // Mock Repository
    @Mock
    private  VisitCountLogRepository visitCountLogRepository;  //방문 횟수를 기록

    @InjectMocks
    private LoginLogService loginLogService; // 테스트 대상 서비스

    private User specificUser;

    //@Mock => Mock 객체를 생성할 때 사용
    //@InjectMocks => Mock 객체를 주입하여 테스트 대상 객체를 생성할 때 사용
    //@Autowired => 의존성 주입

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testFindDailyLogCountsByYearAndMonth() {
        // Given: 테스트 조건 설정
        Integer year = 2024;
        Integer month = 12;

        // When: Repository 메서드 호출
        List<Object[]> results = visitorsLogRepository.findDailyLogCountsBetweenDates(year, month);


    }

//	@BeforeEach
//	public void setup() { //로그인 설정
//		MockitoAnnotations.openMocks(this);
//
//		// 특정 사용자 생성 및 데이터베이스 저장
//		specificUser = new User();
//		specificUser.setUsername("te8");
//		specificUser.setPassword("1234");
//		specificUser = userRepository.save(specificUser); // 저장 후 다시 할당하여 영속성 적용
//
//		// Mock 설정
//		when(authentication.getPrincipal()).thenReturn(principalDetail);
//		when(principalDetail.getUser()).thenReturn(specificUser); // 특정 사용자 설정
//
//	}




}
