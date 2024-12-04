package com.study.mms.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequiredArgsConstructor // 생성자 의존성 주입
@RequestMapping("/admin")
public class AdminNoticeController {

	private final AdminNoticeService adminNoticeServicel;

	@ResponseBody
	@PostMapping("/nocite")
	@Operation(summary = "공지사항 생성, 수정 API", description = "관리자 페이지 공지사항 생성, 수정 API")
	public ResponseEntity<Map<String, Object>> createAndUpdateNotice(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@ModelAttribute NoticeDTO noticeDTO, HttpServletRequest req) {
		return adminNoticeServicel.createAndUpdateNotice(principalDetail, noticeDTO, req);
	}

	@ResponseBody
	@DeleteMapping("/nocite")
	@Operation(summary = "공지사항 삭제", description = "관리자 페이지 공지사항 삭제 API")
	public ResponseEntity<Map<String, Object>> deleteNotice(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam Integer noticeId, HttpServletRequest req) {
		return adminNoticeServicel.deleteNotice(principalDetail, noticeId, req);
	}

	@ResponseBody
	@GetMapping("/nocite")
	@Operation(summary = "공지사항 리스트 반환", description = "관리자 페이지 공지사항 리스트 반환 API")
	public ResponseEntity<Map<String, Object>> getNoticeList(@AuthenticationPrincipal PrincipalDetail principalDetail
		 ) {
		return adminNoticeServicel.getNoticeList(principalDetail);
	}

}
