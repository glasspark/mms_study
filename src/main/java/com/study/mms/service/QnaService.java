package com.study.mms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.CreateInquiryDTO;
import com.study.mms.model.Faq;
import com.study.mms.model.Inquiry;
import com.study.mms.model.User;
import com.study.mms.model.Inquiry.InquiryStatus;
import com.study.mms.repository.FaqRepository;
import com.study.mms.repository.InquiryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QnaService {

	private final FaqRepository faqRepository;
	
	private final InquiryRepository inquiryRepository;

	// 자주 묻는 질문 단일 정보를 가져 옴
	@Transactional
	public Map<String, Object> addInquiry(PrincipalDetail principalDetail, CreateInquiryDTO createInquiryDTO) {

		Map<String, Object> returnMap = new HashMap<>();

		try {

			// PrincipalDetail을 통해 현재 로그인된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			
			Inquiry newInquiry = Inquiry.builder().category(createInquiryDTO.getCategory())
					.content(createInquiryDTO.getContent()).title(createInquiryDTO.getTitle()).user(user)
					.status(InquiryStatus.WAITING).build();

			inquiryRepository.save(newInquiry);

			returnMap.put("status", "success");
			returnMap.put("message", "저장되었습니다.");

		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
		}

		return returnMap;
	}
	
	
	//자주 묻는 질문 리스트 반환
	@Transactional
	public Map<String, Object> getFaqLists(PrincipalDetail principalDetail) {

		Map<String, Object> returnMap = new HashMap<>();

		try {
			List<Faq> faqList = faqRepository.findAll();

			returnMap.put("status", "success");
			returnMap.put("message", "조회되었습니다.");
			returnMap.put("data", faqList);

		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
		}

		return returnMap;
	}

}
