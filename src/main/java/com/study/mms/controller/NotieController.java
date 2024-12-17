package com.study.mms.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.NoticeDTO;
import com.study.mms.service.AdminNoticeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class NotieController {

	private final AdminNoticeService adminNoticeServicel;

	@ResponseBody
	@GetMapping("/nocite")
	@Operation(summary = "공지사항 리스트 반환", description = "관리자 페이지 공지사항 리스트 반환 API")
	public ResponseEntity<Map<String, Object>> getNoticeList(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam(value = "page", defaultValue = "0") Integer page) {
		return adminNoticeServicel.getNoticeList(principalDetail, page);
	}

	@ResponseBody
	@GetMapping("/nocite/{id}")
	@Operation(summary = "공지사항 단일 데이터 반환", description = "관리자 페이지 공지사항 단일 데이터 반환 API")
	public NoticeDTO getNotice(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@PathVariable("id") Integer noticeId) {
		return adminNoticeServicel.getNotice(principalDetail, noticeId);
	}

}
