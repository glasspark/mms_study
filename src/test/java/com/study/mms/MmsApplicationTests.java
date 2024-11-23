package com.study.mms;

import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.CreateInquiryDTO;
import com.study.mms.dto.CreateStudyGroupDTO;
import com.study.mms.model.User;
import com.study.mms.repository.InquiryRepository;
import com.study.mms.repository.StudyGroupRepository;
import com.study.mms.repository.UserRepository;
import com.study.mms.service.QnaService;
import com.study.mms.service.StudyGroupService;

@SpringBootTest
class MmsApplicationTests {

	@Autowired
	private StudyGroupService studyGroupService;

	@Mock
	private Authentication authentication;

	@Mock
	private PrincipalDetail principalDetail;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private QnaService qnaService;

	@Autowired
	private InquiryRepository inquiryRepository;

	@Mock
	private StudyGroupRepository studyGroupRepository;

	private User specificUser;

	@Test
	void contextLoads() {
	}

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		// 특정 사용자 생성 및 데이터베이스 저장
		specificUser = new User();
		specificUser.setUsername("te8");
		specificUser.setPassword("Qwe123!@#");
		specificUser = userRepository.save(specificUser); // 저장 후 다시 할당하여 영속성 적용

		// Mock 설정
		when(authentication.getPrincipal()).thenReturn(principalDetail);
		when(principalDetail.getUser()).thenReturn(specificUser); // 특정 사용자 설정

	}

	@Test
	public void createStudyGroupsForSpecificUser() {
		for (int i = 31; i <= 80; i++) { // 예: 10개의 데이터 생성
			CreateStudyGroupDTO groupDTO = new CreateStudyGroupDTO();
			groupDTO.setName("StudyGroup " + i);
			groupDTO.setDescription("Description for StudyGroup " + i);
			groupDTO.setMaxMembers(10);

			// 특정 사용자를 기반으로 스터디 그룹 생성
			Map<String, Object> result = studyGroupService.createStudyGroup(authentication, groupDTO);
			System.out.println("Created Group ID for specific user: " + result.get("groupId"));
		}
	}

}
