package com.study.mms.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.CreateFaqDTO;
import com.study.mms.dto.InquiryDTO;
import com.study.mms.model.Faq;
import com.study.mms.model.Inquiry;
import com.study.mms.model.Inquiry.InquiryStatus;
import com.study.mms.repository.FaqRepository;
import com.study.mms.repository.InquiryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminQnaService {

	private final FaqRepository faqRepository;
	private final InquiryRepository inquiryRepository;

	// 자주 묻는 질문 저장
	@Transactional
	public Map<String, Object> createFaqLists(PrincipalDetail principalDetail, CreateFaqDTO createFaqDTO) {

		Map<String, Object> returnMap = new HashMap<>();

		try {

			Faq newFaq = Faq.builder().category(createFaqDTO.getCategory()).title(createFaqDTO.getTitle())
					.content(createFaqDTO.getContent()).build();

			faqRepository.save(newFaq);

			returnMap.put("status", "success");
			returnMap.put("message", "저장되었습니다.");

		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
		}

		return returnMap;
	}

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

	@Transactional
	public Map<String, Object> deleteFaqLists(PrincipalDetail principalDetail, Integer faqId) {

		Map<String, Object> returnMap = new HashMap<>();

		try {

			Optional<Faq> optinalFaq = faqRepository.findById(faqId);
			if (!optinalFaq.isPresent()) {
				returnMap.put("status", "error");
				returnMap.put("message", "해당하는 질문은 존재하지 않습니다.");
				return returnMap;
			}

			Faq faq = optinalFaq.get();

			faqRepository.delete(faq);

			returnMap.put("status", "success");
			returnMap.put("message", "삭제되었습니다.");

		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
		}

		return returnMap;
	}

	@Transactional
	public Map<String, Object> updateFaqLists(PrincipalDetail principalDetail, CreateFaqDTO createFaqDTO) {

		Map<String, Object> returnMap = new HashMap<>();

		try {

			Optional<Faq> optinalFaq = faqRepository.findById(createFaqDTO.getId());
			if (!optinalFaq.isPresent()) {
				returnMap.put("status", "error");
				returnMap.put("message", "해당하는 질문은 존재하지 않습니다.");
				return returnMap;
			}

			Faq faq = optinalFaq.get();
			faq.setCategory(createFaqDTO.getCategory());
			faq.setTitle(createFaqDTO.getTitle());
			faq.setContent(createFaqDTO.getContent());

			faqRepository.save(faq);

			returnMap.put("status", "success");
			returnMap.put("message", "수정되었습니다.");

		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
		}

		return returnMap;
	}

	// 자주 묻는 질문 단일 정보를 가져 옴
	@Transactional
	public Map<String, Object> getFaqData(PrincipalDetail principalDetail, Integer faqId) {

		Map<String, Object> returnMap = new HashMap<>();

		try {

			Optional<Faq> optinalFaq = faqRepository.findById(faqId);
			if (!optinalFaq.isPresent()) {
				returnMap.put("status", "error");
				returnMap.put("message", "해당하는 질문은 존재하지 않습니다.");
				return returnMap;
			}

			Faq faq = optinalFaq.get();

			returnMap.put("status", "success");
			returnMap.put("message", "조회되었습니다.");
			returnMap.put("data", faq);

		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
		}

		return returnMap;
	}

	// 1:1 문의 리스트 검색 및 반환 API
	@Transactional
	public Map<String, Object> getFilteredInquiries(PrincipalDetail principalDetail, LocalDateTime startDate,
			LocalDateTime endDate, String category, String status, String type, String searchQuery, int page) {
		// TODO Auto-generated method stub

		Map<String, Object> returnMap = new HashMap<>();

		try {
			Pageable pageable = PageRequest.of(page - 1, 8); // 페이지 시작은 0부터 시작하므로 -1 처리

			// Repository에서 필터 조건에 맞는 결과 조회
			Page<Inquiry> inquiries = inquiryRepository.findInquiries(startDate, endDate, category, status, type,
					searchQuery, pageable);

			// Inquiry 엔티티를 InquiryDTO로 변환
			Page<InquiryDTO> inquiryDTOs = inquiries.map(inquiry -> InquiryDTO.builder().id(inquiry.getId())
					.createdAt(inquiry.getCreatedAt()).category(inquiry.getCategory()).title(inquiry.getTitle())
					.content(inquiry.getContent()).user(inquiry.getUser()) // user 필드 설정 (username, nickname, userId)
					.inquiryStatus(inquiry.getStatus()).answer(inquiry.getAnswer()) // status 필드 설정
					.build());

			// 반환할 Map 생성 및 데이터 추가
			returnMap.put("data", inquiryDTOs.getContent()); // 현재 페이지의 데이터 리스트
			returnMap.put("totalPages", inquiries.getTotalPages()); // 총 페이지 수
			returnMap.put("totalElements", inquiries.getTotalElements()); // 총 요소 수
			returnMap.put("currentPage", inquiries.getNumber() + 1); // 현재 페이지 번호 (0부터 시작하므로 +1)
			returnMap.put("pageSize", inquiries.getSize()); // 페이지당 요소 수

		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
		}

		return returnMap;
	}

	// 1:1 문의 답변
	public Map<String, Object> addInquiryAnswer(PrincipalDetail principalDetail, String answer, Integer inquiryId) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<>();

		try {

			Optional<Inquiry> optinalInq = inquiryRepository.findById(inquiryId);

			if (!optinalInq.isPresent()) {
				returnMap.put("status", "error");
				returnMap.put("message", "1:1 문의를 찾을 수 없습니다.");

				return returnMap;
			}
			
			Inquiry inquiry = optinalInq.get();
			inquiry.setAnswer(answer);
			inquiry.setStatus(InquiryStatus.ANSWERED);
			inquiryRepository.save(inquiry);
			
			returnMap.put("status", "success");
			returnMap.put("message", "답변을 저장하였습니다.");
			
		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
		}

		return returnMap;
	}

	// 1:1 문의 삭제
	public Map<String, Object> deleteInquiry(PrincipalDetail principalDetail, Integer inquiryId) {
		// TODO Auto-generated method stub

		Map<String, Object> returnMap = new HashMap<>();

		try {

			Optional<Inquiry> optinalInq = inquiryRepository.findById(inquiryId);

			if (!optinalInq.isPresent()) {
				returnMap.put("status", "error");
				returnMap.put("message", "1:1 문의를 찾을 수 없습니다.");

				return returnMap;
			}
			
			
			inquiryRepository.delete(optinalInq.get());
			returnMap.put("status", "success");
			returnMap.put("message", "삭제되었습니다.");

		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
		}

		return returnMap;
	}

}
