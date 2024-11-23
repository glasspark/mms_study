package com.study.mms.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.CreateFaqDTO;
import com.study.mms.service.AdminQnaService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor // 생성자 의존성 주입
@RequestMapping("/api/admin/question")
public class AdminQnaController {

	private final AdminQnaService adminQnaService;

	@ResponseBody
	@PostMapping("/create")
	@Operation(summary = "자주 묻는 질문 저장  API", description = "자주묻는 질문 저장 API")
	public Map<String, Object> createFaqLists(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@Valid @ModelAttribute CreateFaqDTO createFaqDTO, BindingResult bindingResult) {
		Map<String, Object> response = new HashMap<>();

		// 유효성 검사 오류가 있는 경우
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			response.put("status", "error");
			response.put("errors", errors);
			return response;
		}
		return adminQnaService.createFaqLists(principalDetail, createFaqDTO);
	}

	@ResponseBody
	@GetMapping("/get")
	@Operation(summary = "자주 묻는 질문 리스트  API", description = "자주묻는 질문 리스트 반환 API")
	public Map<String, Object> getFaqLists(@AuthenticationPrincipal PrincipalDetail principalDetail) {
		return adminQnaService.getFaqLists(principalDetail);
	}

	@ResponseBody
	@GetMapping("/get/data")
	@Operation(summary = "자주 묻는 질문 단일 반환  API", description = "자주묻는 질문 단일 정보 반환 API")
	public Map<String, Object> getFaqData(@AuthenticationPrincipal PrincipalDetail principalDetail, Integer faqId) {
		return adminQnaService.getFaqData(principalDetail, faqId);
	}

	@ResponseBody
	@PostMapping("/delete")
	@Operation(summary = "자주 묻는 질문 리스트 삭제  API", description = "자주묻는 질문 리스트 삭제 API")
	public Map<String, Object> deleteFaqLists(@AuthenticationPrincipal PrincipalDetail principalDetail, Integer faqId) {
		return adminQnaService.deleteFaqLists(principalDetail, faqId);
	}

	@ResponseBody
	@PostMapping("/update")
	@Operation(summary = "자주 묻는 질문 저장  API", description = "자주묻는 질문 저장 API")
	public Map<String, Object> updateFaqLists(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@Valid @ModelAttribute CreateFaqDTO createFaqDTO, BindingResult bindingResult) {
		Map<String, Object> response = new HashMap<>();

		// 유효성 검사 오류가 있는 경우
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			response.put("status", "error");
			response.put("errors", errors);
			return response;
		}
		return adminQnaService.updateFaqLists(principalDetail, createFaqDTO);
	}

	@ResponseBody
	@GetMapping("/get/inquiry")
	@Operation(summary = "1:1 문의 리스트 반환 API", description = "1:1 문의 리스트 검색 및 반환 API")
	public Map<String, Object> getFilteredInquiries(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
			@RequestParam(value = "category", defaultValue = "all") String category,
			@RequestParam(value = "status", defaultValue = "all") String status,
			@RequestParam(value = "type", defaultValue = "all") String type,
			@RequestParam(value = "searchQuery", required = false) String searchQuery,
			@RequestParam(value = "page", defaultValue = "1") int page) {

		return adminQnaService.getFilteredInquiries(principalDetail, startDate, endDate, category, status, type,
				searchQuery, page);
	}

	@ResponseBody
	@PostMapping("/add/inquiry/answer")
	@Operation(summary = "1:1 문의 답변 API", description = "1:1 문의 답변 저장 및 반환 API")
	public Map<String, Object> addInquiryAnswer(@AuthenticationPrincipal PrincipalDetail principalDetail, String answer,
			Integer inquiryId) {
		return adminQnaService.addInquiryAnswer(principalDetail, answer, inquiryId);
	}

	@ResponseBody
	@PostMapping("/delete/inquiry")
	@Operation(summary = "1:1 문의 삭제 API", description = "1:1 문의 삭제 API")
	public Map<String, Object> deleteInquiry(@AuthenticationPrincipal PrincipalDetail principalDetail,
			Integer inquiryId) {
		return adminQnaService.deleteInquiry(principalDetail, inquiryId);
	}

}
