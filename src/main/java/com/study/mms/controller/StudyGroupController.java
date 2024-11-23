package com.study.mms.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.CreateStudyGroupDTO;
import com.study.mms.dto.StudyJoinDTO;
import com.study.mms.service.StudyGroupService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor // 생성자 의존성 주입
@RequestMapping("/api/group")
public class StudyGroupController {

	private final StudyGroupService studyGroupService;

	@ResponseBody
	@PostMapping("/create")
	@Operation(summary = "스터디 그룹 생성 API", description = "스터디 그룹 생성 API")
	public Map<String, Object> createStudyGroup(Authentication authentication,
			@Valid @ModelAttribute CreateStudyGroupDTO groupDTO, BindingResult bindingResult) {
		Map<String, Object> response = new HashMap<>();

		// 유효성 검사 오류가 있는 경우
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			response.put("status", "error");
			response.put("errors", errors);
			return response;
		}
		return studyGroupService.createStudyGroup(authentication, groupDTO);
	}

	@ResponseBody
	@PostMapping("/del/study")
	@Operation(summary = "스터디 그룹 삭제  API", description = "스터디 그룹 삭제 API")
	public Map<String, Object> deleteStudyGroup(Authentication authentication,
			@RequestParam("studyGroupId") Integer num) {
		return studyGroupService.deleteStudyGroup(authentication, num);
	}

	@ResponseBody
	@PostMapping("/req/join")
	@Operation(summary = "스터디 그룹 회원가입 신청 API", description = "회원이 스터디 그룹에 회원가입 신청을 하는 API")
	public Map<String, Object> RequestJoinStudyGroup(Authentication authentication,
			@RequestBody StudyJoinDTO studyJoinDTO) {
		return studyGroupService.RequestJoinStudyGroup(authentication, studyJoinDTO);
	}

//	// 방장이 회원 가입 리스트 반환 받음
//	@ResponseBody
//	@PostMapping("/rep/join/study/datas")
//	@Operation(summary = "스터디 그룹 회원가입 신청 리스트 반환 API", description = "방장이 자신의 스터디그룹에 회원가입 신청한 회원들의 데이터를 반환하는 API")
//	public Map<String, Object> getJoinRequestsForLeader(Authentication authentication,
//			@RequestBody StudyJoinDTO studyJoinDTO) {
//		return studyGroupService.getJoinRequestsForLeader(authentication, studyJoinDTO);
//	}

	// 방장이 회원가입 승인, 거절 처리
//	@ResponseBody
//	@PostMapping("/join/app")
//	@Operation(summary = "스터디 그룹 회원가입 승인 API", description = "방장이 자신의 스터디그룹에 회원가입 신청한 회원승인 API")
//	public Map<String, Object> approveJoinRequest(Authentication authentication,
//			@RequestBody StudyJoinDTO studyJoinDTO) {
//		return studyGroupService.approveJoinRequest(authentication, studyJoinDTO);
//	}

	// 댓글 달기 활성화
	@ResponseBody
	@PostMapping("/study/{studyGroupId}/comments")
	@Operation(summary = "스터디 그룹 게시판 댓글 저장 API", description = "스터디 그룹에 댓글 저장하는 API")
	public Map<String, Object> saveComment(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@PathVariable("studyGroupId") Integer studyGroupId, String comment) {
		return studyGroupService.saveComment(principalDetail, studyGroupId, comment);
	}

	// 댓글 리스트 반환
	@ResponseBody
	@GetMapping("/study/{studyGroupId}/comments")
	@Operation(summary = "스터디 그룹 게시판 댓글 리스트 반환 API", description = "스터디 그룹에 댓글리스트를 반환하는 API")
	public Map<String, Object> getComments(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@PathVariable("studyGroupId") Integer studyGroupId) {
		return studyGroupService.getComments(principalDetail, studyGroupId);
	}

	// 모집종료, 모집중 상태 처리
	@ResponseBody
	@PostMapping("/study/closed")
	@Operation(summary = "스터디 그룹 모집종료, 모집중 상태 변경 API", description = "스터디 그룹에 모집종료, 모집중 상태 변경 API")
	public Map<String, Object> setRecruitmentClosed(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam("studyGroupId") Integer studyGroupId,
			@RequestParam("recruitmentStatus") String recruitmentStatus) {
		return studyGroupService.setRecruitmentClosed(principalDetail, studyGroupId, recruitmentStatus);
	}

	@ResponseBody
	@PostMapping("/update/comment")
	@Operation(summary = "스터디 그룹 댓글 수정 API", description = "스터디 그룹에 작성한 댓글을 수정 API")
	public Map<String, Object> updateComment(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam("commentId") Integer commentId, String comments) {
		return studyGroupService.updateComment(principalDetail, commentId, comments);
	}

	@ResponseBody
	@PostMapping("/delete/comment")
	@Operation(summary = "스터디 그룹 댓글 수정 API", description = "스터디 그룹에 작성한 댓글을 수정 API")
	public Map<String, Object> deleteComment(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam("commentId") Integer commentId) {
		return studyGroupService.deleteComment(principalDetail, commentId);
	}

	@ResponseBody
	@PostMapping("/add/reply")
	@Operation(summary = "스터디 그룹 답글 등록 API", description = "스터디 그룹에 작성한 댓글에 답글을 저장 API")
	public Map<String, Object> saveStudyCommentReply(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam("relpy") String relpy, @RequestParam("commentId") Integer commentId) {
		return studyGroupService.saveStudyCommentReply(principalDetail, relpy, commentId);
	}

	@ResponseBody
	@PostMapping("/update/reply")
	@Operation(summary = "스터디 그룹 답글 수정 API", description = "스터디 그룹에 작성한 댓글에 답글 수정 API")
	public Map<String, Object> updateStudyCommentReply(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam("relpy") String relpy, @RequestParam("replyId") Integer replyId) {
		return studyGroupService.updateStudyCommentReply(principalDetail, relpy, replyId);
	}

	@ResponseBody
	@PostMapping("/delete/reply")
	@Operation(summary = "스터디 그룹 답글 삭제 API", description = "스터디 그룹에 작성한 댓글에 답글 삭제 API")
	public Map<String, Object> deleteStudyCommentReply(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam("replyId") Integer replyId) {
		return studyGroupService.deleteStudyCommentReply(principalDetail, replyId);
	}

}
