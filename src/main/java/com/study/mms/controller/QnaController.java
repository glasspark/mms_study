package com.study.mms.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.CreateInquiryDTO;
import com.study.mms.service.QnaService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor // 생성자 의존성 주입
@RequestMapping("/api/question")
public class QnaController {

	private final QnaService qnaService;

	@ResponseBody
	@GetMapping("/get")
	@Operation(summary = "자주 묻는 질문 리스트  API", description = "자주묻는 질문 리스트 반환 API")
	public Map<String, Object> getFaqLists(@AuthenticationPrincipal PrincipalDetail principalDetail) {
		return qnaService.getFaqLists(principalDetail);
	}

	@ResponseBody
	@PostMapping("/create")
	@Operation(summary = "1:1 질문 저장  API", description = "1:1  질문 저장 API")
	public Map<String, Object> createInquiry(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@Valid @ModelAttribute CreateInquiryDTO createInquiryDTO, BindingResult bindingResult) {
		Map<String, Object> response = new HashMap<>();

		// 유효성 검사 오류가 있는 경우
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			response.put("status", "error");
			response.put("errors", errors);
			return response;
		}
		return qnaService.addInquiry(principalDetail, createInquiryDTO);
	}

}
