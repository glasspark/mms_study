package com.study.mms;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.model.User;
import com.study.mms.repository.UserRepository;
import com.study.mms.repository.VisitCountLogRepository;
import com.study.mms.repository.VisitorsLogRepository;
import com.study.mms.service.LoginLogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.List;


@SpringBootTest
@Rollback(false) // 롤백 방지, 실제 DB에 데이터 유지
class MmsApplicationTests {

    @Mock
    private Authentication authentication;

    @Mock
    private PrincipalDetail principalDetail;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private VisitorsLogRepository visitorsLogRepository; // Mock Repository


    @Mock
    private VisitCountLogRepository visitCountLogRepository;  //방문 횟수를 기록

    @InjectMocks
    private LoginLogService loginLogService; // 테스트 대상 서비스

    private User specificUser;


    //@Mock => Mock 객체를 생성할 때 사용
    //@InjectMocks => Mock 객체를 주입하여 테스트 대상 객체를 생성할 때 사용
    //@Autowired => 의존성 주입

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }




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


    @Test
    public void testRegisterMultipleUsers() {
        // 테스트 데이터
        String password = "password123";
        for (int i = 41; i < 60; i++) {
            String salt = BCrypt.gensalt();
            String encPassword = BCrypt.hashpw(password, salt);

            User user = new User();
            user.setUsername("username" + i);
            user.setEmail("username" + i + "@example.com");
            user.setNickname("username" + i + "_nickname");
            user.setSalt(salt);
            user.setPassword(encPassword);
            user.setRole("ROLE_USER");
            user.setImg_name("default_img");
            user.setImg_path("/img/defaultImg.png");
            user.setSns("DEFAULT");

            // 데이터 저장
            userRepository.save(user);

            System.out.println("데이터 저장 완료");
        }

    }

}
