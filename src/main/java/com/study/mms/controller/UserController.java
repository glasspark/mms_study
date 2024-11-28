package com.study.mms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.StudyGroupDTO;
import com.study.mms.dto.UsersJoinDTO;
import com.study.mms.dto.updatePasswordDTO;
import com.study.mms.service.SendEmail;
import com.study.mms.service.StudyGroupService;
import com.study.mms.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor // 생성자 의존성 주입
@RequestMapping("/api/user")
public class UserController {

	private final UserService userService;
	private final SendEmail sendEmail;
	private final StudyGroupService studyGroupService;

	@ResponseBody
	@PostMapping("/join")
	@Operation(summary = "회원가입 API", description = "회원가입 회원가입 API")
	public Map<String, Object> joinUser(@RequestBody UsersJoinDTO joinDTO, HttpServletRequest req) {
		return userService.joinUsers(joinDTO, req);
	}

	@ResponseBody
	@PostMapping("/send/code")
	@Operation(summary = "회원가입 이메일 인증 코드 전송 API", description = "회원가입 시 이메일에 인증 코드를 전송하는  API")
	public Map<String, Object> sendEmailWithAuthCode(@RequestParam String email, HttpServletRequest req) {
		return sendEmail.sendEmailWithAuthCode(email, req);
	}

	@ResponseBody
	@PostMapping("/check/email")
	@Operation(summary = "회원가입 이메일 인증 API", description = "회원가입 시 이메일을 인증하는  API")
	public Map<String, Object> verifyAuthCode(@RequestParam("key") String key, HttpServletRequest req) {
		return userService.verifyAuthCode(key, req);
	}

	// ================ 마이페이지 ================

	@ResponseBody
	@PostMapping("/save/random/info")
	@Operation(summary = "마이페이지 랜덤 이미지 변경 시 사용자 정보 저장 API", description = "마이페이지에서 랜덤 이미지를 사용하여 개인 정보를 변경한 회원정보 저장 API")
	public Map<String, Object> saveUserDefaultImgAndDatas(@RequestParam("imagePath") String imagePath,
			@RequestParam("nickname") String nickname, @AuthenticationPrincipal PrincipalDetail principalDetail) {
		return userService.saveUserDefaultImgAndDatas(imagePath, nickname, principalDetail);
	}

	@ResponseBody
	@PostMapping("/save/info")
	@Operation(summary = "마이페이지 사용자 지정 이미지 변경 시 사용자 정보 저장 API", description = "마이페이지에서 이미지를 지정하여 변경한 회원정보 저장 API")
	public Map<String, Object> saveUserDatas(@RequestParam("imagePath") MultipartFile imagePath,
			@RequestParam("nickname") String nickname, @AuthenticationPrincipal PrincipalDetail principalDetail,
			HttpServletRequest req) {
		return userService.saveUserDatas(imagePath, nickname, principalDetail, req);
	}

	@ResponseBody
	@PostMapping("/save/nickname/info")
	@Operation(summary = "마이페이지 사용자 지정 이미지 변경 시 사용자 정보 저장 API", description = "마이페이지에서 이미지를 지정하여 변경한 회원정보 저장 API")
	public Map<String, Object> saveUserNickNameDatas(@RequestParam("nickname") String nickname,
			@AuthenticationPrincipal PrincipalDetail principalDetail) {
		return userService.saveUserNickNameDatas(nickname, principalDetail);
	}

//	@ResponseBody
//	@PostMapping("/save/change/password")
//	@Operation(summary = "마이페이지 비밀번호 변경 API", description = "마이페이지 비밀번호 변경  API")
//	public Map<String, Object> changeUserPassword(@RequestBody UsersJoinDTO joinDTO,
//			@AuthenticationPrincipal PrincipalDetail principalDetai) {
//		return userService.changeUserPassword(joinDTO, principalDetai);
//	}

	@ResponseBody
	@PatchMapping("/info")
	@Operation(summary = "마이페이지 비밀번호 변경 API", description = "마이페이지 비밀번호 변경 API")
	public Map<String, Object> passwordChange(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@ModelAttribute updatePasswordDTO passwordDTO, BindingResult bindingResult) {
		return userService.changeUserPassword(passwordDTO, principalDetail);
	}

	// ================ 스터디 ================

	@ResponseBody
	@GetMapping("/study/lists")
	@Operation(summary = "마이페이지 가입한 스터디 그룹 리스트 API", description = "마이페이지에서 내가 가입한 스터디 그룹의 리스트 반환 API")
	public Map<String, Object> getJoinStudyLists(@AuthenticationPrincipal PrincipalDetail principalDetail) {
		Map<String, Object> returnMap = new HashMap<>();
		List<StudyGroupDTO> listDto = studyGroupService.getUserStudyGroup(principalDetail);
		returnMap.put("data", listDto);
		return returnMap;
	}

	@ResponseBody
	@DeleteMapping("/study/lists/{groupId}")
	@Operation(summary = "마이페이지 스터디 그룹 탈퇴 API", description = "마이페이지에서 그룹 탈퇴 API")
	public Map<String, Object> studyGroupWithdrawal(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@PathVariable("groupId") Integer groupId) {
		return userService.studyGroupWithdrawal(principalDetail, groupId);
	}

	@ResponseBody
	@GetMapping("/applied/study")
	@Operation(summary = "마이페이지 스터디 그룹 신청 리스트 API", description = "마이페이지에서 내가 신청한 스터디 그룹의 리스트 반환 API")
	public Map<String, Object> getSignUpStudyLists(@AuthenticationPrincipal PrincipalDetail principalDetail) {
		return userService.getSignUpStudyLists(principalDetail);
	}

	@ResponseBody
	@DeleteMapping("/applied/study/{groupId}")
	@Operation(summary = "마이페이지 스터디 그룹 신청 리스트 삭제 API", description = "마이페이지에서 내가 신청한 스터디 그룹의 신청 리스트 삭제 API")
	public Map<String, Object> deleteApplicationDetails(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@PathVariable("groupId") Integer groupId) {
		return userService.deleteApplicationDetails(principalDetail, groupId);
	}

	// ================ 오늘의 할 일 ================

	// 오늘의 할일
	@ResponseBody
	@PostMapping("/add/todo")
	@Operation(summary = "오늘의 할일 리스트 등록 API", description = "마이페이지 할일 등록  API")
	public Map<String, Object> createUserTodoList(Authentication authentication,
			@Parameter(description = "할일 설명") @RequestParam String description) {
		return userService.createUserTodoList(authentication, description);
	}

	@ResponseBody
	@GetMapping("/get/todo")
	@Operation(summary = "오늘의 할일 리스트 반환 API", description = "마이페이지 할일 리스트 반환 API")
	public Map<String, Object> getUserTodoList(Authentication authentication) {
		return userService.getUserTodoList(authentication);
	}

	@ResponseBody
	@PostMapping("/update/todo")
	@Operation(summary = "오늘의 할일 완료 API", description = "마이페이지 할일 완료 처리 API")
	public Map<String, Object> completeUserTodoList(Authentication authentication, @RequestParam Integer todoId) {
		return userService.completeUserTodoList(authentication, todoId);
	}

	@ResponseBody
	@PostMapping("/add/attendance")
	@Operation(summary = "출석체크 등록 API", description = "출석체크 등록 API")
	public Map<String, Object> addUserAttendance(Authentication authentication) {
		return userService.addUserAttendance(authentication);
	}

	@ResponseBody
	@GetMapping("/get/attendance")
	@Operation(summary = "출석체크 리스트 반환 API", description = "유저의 출석체크 리스트 반환 API")
	public Map<String, Object> getUserAttendance(Authentication authentication) {
		return userService.getUserAttendance(authentication);
	}

	// ================ 1:1 문의 ================

	@ResponseBody
	@GetMapping("/get/inquiries")
	@Operation(summary = "1:1 문의 리스트 반환 API", description = "유저의 1: 1 문의 리스트를 반환 API")
	public Map<String, Object> getUserInquiries(@AuthenticationPrincipal PrincipalDetail principalDetail, int page) {
		return userService.getUserInquiries(principalDetail, page);
	}

	@ResponseBody
	@PostMapping("/delete/inquiry")
	@Operation(summary = "1:1 문의 리스트 반환 API", description = "유저의 1: 1 문의 리스트를 반환 API")
	public Map<String, Object> deleteUserInquiry(@AuthenticationPrincipal PrincipalDetail principalDetail,
			Integer inquiryId) {
		return userService.deleteUserInquiry(principalDetail, inquiryId);
	}

}
