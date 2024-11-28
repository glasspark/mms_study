package com.study.mms.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.StudyGroupDTO;
import com.study.mms.service.AdminStudyGroupService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor // 생성자 의존성 주입
@RequestMapping("/admin")
public class AdminMainController {

	private final AdminStudyGroupService adminStudyGroupService;

	// 관리자 메인 페이지
	@GetMapping({ "/dashboard", "" })
	public String adminDashBoard(@AuthenticationPrincipal PrincipalDetail principalDetai) {
		return "admin/dashboard";
	}

	// 관리자 스터디 신청 리스트
	@GetMapping("/study")
	public String adminStudyAccessBoard(@AuthenticationPrincipal PrincipalDetail principalDetai,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "type", defaultValue = "All") String type,
			@RequestParam(value = "searchQuery", defaultValue = "") String searchQuery,
			@RequestParam(value = "startDate", required = false) String startDateStr,
			@RequestParam(value = "endDate", required = false) String endDateStr, Model model) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDateTime startDate = (startDateStr != null && !startDateStr.isEmpty())
				? LocalDate.parse(startDateStr, dateFormatter).atStartOfDay()
				: null;

		LocalDateTime endDate = (endDateStr != null && !endDateStr.isEmpty())
				? LocalDate.parse(endDateStr, dateFormatter).atTime(23, 59, 59)
				: null;

		Page<StudyGroupDTO> studyGroupsPage = adminStudyGroupService.getApproveStudyGroupList(page, type, searchQuery,
				startDate, endDate);

		model.addAttribute("data", studyGroupsPage.getContent());
		model.addAttribute("currentPage", studyGroupsPage.getNumber());
		model.addAttribute("totalPages", studyGroupsPage.getTotalPages());
		model.addAttribute("totalItems", studyGroupsPage.getTotalElements());
		model.addAttribute("status", "success");
		model.addAttribute("startPage", (studyGroupsPage.getNumber() / 5) * 5);
		model.addAttribute("endPage",
				Math.min((studyGroupsPage.getNumber() / 5) * 5 + 4, studyGroupsPage.getTotalPages() - 1));
		model.addAttribute("type", type);
		model.addAttribute("previousPage", Math.max((studyGroupsPage.getNumber() / 5) * 5 - 5, 0));
		model.addAttribute("nextPage",
				Math.min((studyGroupsPage.getNumber() / 5) * 5 + 5, studyGroupsPage.getTotalPages() - 1));
		model.addAttribute("size", studyGroupsPage.getSize());
		model.addAttribute("searchQuery", searchQuery);
		model.addAttribute("startDate", startDateStr);
		model.addAttribute("endDate", endDateStr);

		return "admin/study";
	}

	// 자주 묻는 질문 페이지
	@GetMapping("/faq")
	public String adminFaqBoard(@AuthenticationPrincipal PrincipalDetail principalDetai) {
		return "admin/faq";
	}

	// 1:1 문의 페이지
	@GetMapping("/inquiry")
	public String adminInquiryBoard(@AuthenticationPrincipal PrincipalDetail principalDetai) {
		return "admin/inquiry";
	}

	// 관리자 페이지에서 각 스터디 그룹을 관리
	@GetMapping("/studyGroups")
	public String adminStudyGroupsBoard(@AuthenticationPrincipal PrincipalDetail principalDetai) {
		return "admin/studyGroups";
	}

}
