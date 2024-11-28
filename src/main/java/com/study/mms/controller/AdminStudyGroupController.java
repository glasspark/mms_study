package com.study.mms.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.study.mms.service.AdminStudyGroupService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor // 생성자 의존성 주입
@RequestMapping("/api/admin/group")
public class AdminStudyGroupController {

	private final AdminStudyGroupService adminStudyGroupService;

//	@ResponseBody
//	@GetMapping("/approve/list")
//	@Operation(summary = "스터디 그룹 생성 승인 리스트 반환 API", description = "관리자 페이지 스터디 그룹 생성 승인 리스트 반환 API")
//	public Map<String, Object> getApproveStudyGroupList(Authentication authentication, @RequestParam int page,
//			@RequestParam String type) {
//		return adminStudyGroupService.getApproveStudyGroupList(authentication, page, type);
//	}

	@ResponseBody
	@PostMapping("/approve")
	@Operation(summary = "스터디 그룹 생성 승인/거절 API", description = "관리자 페이지 스터디 그룹 생성 승인/거절 API")
	public Map<String, Object> processStudyGroupRequest(Authentication authentication, @RequestParam Integer num,
			@RequestParam String status) {
		return adminStudyGroupService.processStudyGroupRequest(authentication, num, status);
	}
	
	
	
	
	

}
