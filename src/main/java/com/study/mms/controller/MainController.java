package com.study.mms.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.CreateStudyGroupDTO;
import com.study.mms.dto.StudyBoardDTO;
import com.study.mms.dto.StudyGroupDTO;
import com.study.mms.model.User;
import com.study.mms.service.StudyGroupDetailService;
import com.study.mms.service.StudyGroupService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final StudyGroupService studyGroupService;
	private final StudyGroupDetailService studyGroupDetailService;

	// 메인페이지
	@GetMapping("/")
	public String root() {
		return "index";
	}

	// 로그인 페이지
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	// 로그인 후 홈페이지
	@GetMapping("/home")
	public String homePage() {
		return "home";
	}

	// 회원가입
	@GetMapping("/join")
	public String join() {
		return "join";
	}

	// 아이디, 비밀번호 찾기
	@GetMapping("/help/info/{type}")
	public String helpInfo(@PathVariable("type") String type, Model model) {

		if (type.equals("id")) {
			model.addAttribute("typeName", "아이디");
		} else if (type.equals("pw")) {
			model.addAttribute("typeName", "비밀번호");
		} else {
			return "/index";
		}
		model.addAttribute("type", type);
		return "/helpInfo";
	}

	// 스터디
	@GetMapping("/study")
	public String study(@AuthenticationPrincipal PrincipalDetail principalDetai, Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "type", defaultValue = "all") String type,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "tag", required = false) String tag) {

		if ("null".equals(searchText) || "".equals(searchText)) {
			searchText = null;
		}
		if ("null".equals(tag) || "".equals(tag)) {
			tag = null;
		}

		Page<StudyGroupDTO> studyGroupsPage = studyGroupService.getStudyGroupLists(principalDetai, page, type,
				searchText, tag);

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
		model.addAttribute("searchText", searchText);
		model.addAttribute("tag", tag);

		return "study";
	}

	// 내 스터디
	@GetMapping("/mystudy")
	public String mystudy(@AuthenticationPrincipal PrincipalDetail principalDetai, Model model) {

		List<StudyGroupDTO> getUserGroup = studyGroupService.getUserStudyGroup(principalDetai);
		model.addAttribute("data", getUserGroup);
		return "mystudy";
	}

	// 내 스터디 그룹 상세
	@PreAuthorize("@studyGroupService.isUserMemberOfGroup(#principalDetail, #groupId)")
	@GetMapping("/mystudy/detail/{groupId}")
	public String myStudyDetail(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@PathVariable("groupId") Integer groupId, Model model) {
		// 내가 이 스터디 그룹권한을 확인
		String userRole = studyGroupService.isUserMemberOfRole(principalDetail, groupId);
		model.addAttribute("groupId", groupId);
		model.addAttribute("userRole", userRole);
		// 스터디 그룹에 대한 정보를 반환
		StudyGroupDTO studyGroup = studyGroupService.getStudyGroupDetail(groupId);
		model.addAttribute("studyGroup", studyGroup);
		return "mystudyDetail";
	}

	// 내 스터디 그룹 상세 게시판 글 작성 혹은 수정
	@PreAuthorize("@studyGroupService.isUserMemberOfGroup(#principalDetail, #groupId) && (#boardId == null || @studyGroupService.isUserAuthorOfBoardInGroup( #principalDetail, #groupId,#boardId ))")
	@GetMapping("/mystudy/detail/board/{groupId}")
	public String studyBoard(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@PathVariable("groupId") Integer groupId,
			@RequestParam(value = "boardId", required = false) Integer boardId, Model model) {
		model.addAttribute("groupId", groupId);
		model.addAttribute("boardId", boardId);
		if (boardId != null) {
			StudyBoardDTO boardDetail = studyGroupDetailService.getStudyBoardDetail(principalDetail, groupId, boardId);
			model.addAttribute("boardDetail", boardDetail);
		}
		// 수정 들어가야 합니다.
		return "studyBoard";
	}

	// 내 스터디 그룹 게시판 글 상세보기 페이지
	@PreAuthorize("@studyGroupService.isUserMemberOfGroup(#principalDetail, #groupId)")
	@GetMapping("/mystudy/detail/board/{groupId}/{boardId}")
	public String studyBoardContent(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@PathVariable("groupId") Integer groupId, @PathVariable("boardId") Integer boardId, Model model) {

		StudyBoardDTO boardDetail = studyGroupDetailService.getStudyBoardDetail(principalDetail, groupId, boardId);

		model.addAttribute("groupId", groupId);
		model.addAttribute("boardId", boardId);
		model.addAttribute("boardDetail", boardDetail);

		return "studyBoardDetail";
	}

	// 스터디 그룹 생성 및 수정
	@GetMapping("/create/group")
	public String createGroup(Model model, @RequestParam(value = "num", required = false) Integer num) {

		if (num != null) {
			StudyGroupDTO studyGroup = studyGroupService.getStudyGroupDetail(num);
			model.addAttribute("groupDTO", studyGroup);
		} else {
			model.addAttribute("groupDTO", new CreateStudyGroupDTO());
		}
		return "createGroup";
	}

	// 스터디 그룹 상세페이지
	@GetMapping("/study/{id}")
	public String viewGroupDetail(@PathVariable("id") Integer id, Model model,
			@AuthenticationPrincipal PrincipalDetail principalDetai) {

		StudyGroupDTO studyGroup = studyGroupService.getStudyGroupDetail(id);
		model.addAttribute("data", studyGroup);
		return "studyDetail";
	}

	// 마이페이지
	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal PrincipalDetail principalDetai, Model model) {
		// System.out.println(principalDetail.getUsername());
		// System.out.println(principalDetail.getPrimaryKey());

		// 사용자 정보 가져오기
		User user = principalDetai.getUser();
		model.addAttribute("user", user);
		return "mypage";
	}

	// 자주 묻는 질문
	@GetMapping("/faq")
	public String inquey() {
		return "faq";
	}

	// 1 :1 문의
	@GetMapping("/inquiry")
	public String inquiry() {
		return "inquiry";
	}
}
