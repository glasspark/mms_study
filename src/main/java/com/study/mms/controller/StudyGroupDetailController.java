package com.study.mms.controller;

import java.time.LocalDate;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.ScheduleDTO;
import com.study.mms.dto.StudyBoardDTO;
import com.study.mms.service.StudyGroupDetailService;
import com.study.mms.util.ImageUploader;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor // 생성자 의존성 주입
@RequestMapping("/api/group/detail")
public class StudyGroupDetailController {
	private final StudyGroupDetailService studyGroupDetailService;

	@ResponseBody
	@PostMapping("/add/schedule")
	@Operation(summary = "스터디 그룹 상세 스케줄 등록 API", description = "스터디그룹 상세페이지에서 스케줄 등록 API")
	public Map<String, Object> addSchedule(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestBody Map<String, Object> payload) {

		// 스케줄러에서는 start, end 로 키 값 해야해서 아래에서 따로 변환 처리
		ScheduleDTO scheduleDTO = ScheduleDTO.builder().title((String) payload.get("title"))
				.startDate(LocalDate.parse((String) payload.get("start")))
				.endDate(LocalDate.parse((String) payload.get("end")))
				.groupId(Integer.parseInt((String) payload.get("groupId"))) // String → Integer 변환
				.build();

		return studyGroupDetailService.addSchedule(principalDetail, scheduleDTO);
	}

	@ResponseBody
	@GetMapping("/get/schedule")
	@Operation(summary = "스터디 그룹 상세 스케줄 가져오기 API", description = "스터디그룹 상세페이지에서 등록된 스케줄 가져오기 API")
	public Map<String, Object> getSchedule(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam Integer groupId, @RequestParam int month, @RequestParam int year) {
		return studyGroupDetailService.getSchedule(principalDetail, groupId, month, year);
	}

	@ResponseBody
	@PostMapping("/delete/schedule")
	@Operation(summary = "스터디 그룹 상세 스케줄 삭제 API", description = "스터디그룹 상세페이지에서 등록된 스케줄 삭제 API")
	public Map<String, Object> deleteSchedule(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam Integer groupId, @RequestParam Integer scheduleId) {
		return studyGroupDetailService.deleteSchedule(principalDetail, groupId, scheduleId);
	}

	@ResponseBody
	@GetMapping("/get/application")
	@Operation(summary = "스터디 그룹 상세 가입신청 리스트  API", description = "스터디그룹 상세페이지에서 가입신청 리스트 가져오기 API")
	public Map<String, Object> getApplicationLists(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam Integer groupId) {
		return studyGroupDetailService.getApplicationLists(principalDetail, groupId);
	}

	@ResponseBody
	@PostMapping("/application/process")
	@Operation(summary = "스터디 그룹 상세 가입신청 리스트  API", description = "스터디그룹 상세페이지에서 가입신청 리스트 가져오기 API")
	public Map<String, Object> applicationJoinRequestProcess(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam Integer groupId, @RequestParam Integer requestId, @RequestParam String status) {
		return studyGroupDetailService.applicationJoinRequestProcess(principalDetail, groupId, requestId, status);
	}

	@ResponseBody
	@PostMapping(path = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "스터디 그룹 공유 파일 업로드  API", description = "스터디그룹 상세페이지에서 공유파일 업로드 API")
	public Map<String, Object> uploadGroupFile(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam Integer groupId, @RequestParam("file") MultipartFile file, HttpServletRequest req,
			@RequestParam String title) {

		return studyGroupDetailService.uploadGroupFile(principalDetail, groupId, file, req, title);
	}

	@ResponseBody
	@PostMapping("/delete/file")
	@Operation(summary = "스터디 그룹 공유 파일 삭제  API", description = "스터디그룹 상세페이지에서 공유파일 삭제 API")
	public Map<String, Object> deleteGroupFile(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam Integer groupId, HttpServletRequest req, @RequestParam Integer fileId) {
		return studyGroupDetailService.deleteGroupFile(principalDetail, groupId, req, fileId);
	}

	@ResponseBody
	@PostMapping(path = "/update/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "스터디 그룹 공유 파일 변경  API", description = "스터디그룹 상세페이지에서 공유파일 변경 API")
	public Map<String, Object> updateGroupFile(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam Integer groupId, @RequestParam("file") MultipartFile file, HttpServletRequest req,
			Integer fileId, @RequestParam String title) {
		return studyGroupDetailService.updateGroupFile(principalDetail, groupId, file, req, fileId, title);
	}

	@ResponseBody
	@GetMapping("/get/file/list")
	@Operation(summary = "스터디 그룹 공유 파일 리스트 반환  API", description = "스터디그룹 상세페이지에서 공유파일 리스트를 반환 API")
	public Map<String, Object> getGroupFileList(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam("groupId") Integer groupId) {
		return studyGroupDetailService.getGroupFileList(principalDetail, groupId);
	}

	@ResponseBody
	@GetMapping("/get/member/list")
	@Operation(summary = "스터디 그룹 멤버 리스트 반환 API", description = "스터디그룹 상세페이지에서 멤버들의 리스트를 반환하는 API")
	public Map<String, Object> getGroupMemberList(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam("groupId") Integer groupId) {
		return studyGroupDetailService.getGroupMemberList(principalDetail, groupId);
	}

	@ResponseBody
	@PostMapping("/delete/member/withdraw")
	@Operation(summary = "스터디 그룹 멤버 삭제 API", description = "스터디그룹 상세페이지에서 멤버 삭제 API")
	public Map<String, Object> deleteMemberWithdraw(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam("groupId") Integer groupId, @RequestParam("userId") Integer userId) {
		return studyGroupDetailService.deleteMemberWithdraw(principalDetail, groupId, userId);
	}

	// ====== 게시판 관련 API ======
	// 게시판 임시 이미지 저장
	@ResponseBody
	@PostMapping("/upload/image")
	public void uploadImage(@RequestParam("boardImg") MultipartFile multi, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		JSONObject outData = new JSONObject();
		// 이미지를 업로드하고 경로 반환
		String uploadedFilePath = ImageUploader.uploadImage(multi, req, "/upload/temp/");

		outData.put("uploaded", true);
		outData.put("url", uploadedFilePath);
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().print(outData.toString()); // json("uploaded"=true, "url"=업로드된 이미지 경로) 반환
	}

	@ResponseBody
	@PostMapping("/save/write")
	@Operation(summary = "스터디 그룹 게시판 글 저장 및 수정 API", description = "스터디그룹 상세페이지에서 게시판 작성 글 저장 및 수정 API")
	public Map<String, Object> saveStudyBoard(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@ModelAttribute StudyBoardDTO board, HttpServletRequest req) {
		return studyGroupDetailService.saveStudyBoard(principalDetail, board, req);
	}

	@ResponseBody
	@GetMapping("/board/lists")
	@Operation(summary = "스터디 그룹 게시판 글 리스트 반환 API", description = "스터디그룹 상세페이지에서 게시판 작성 글 리스트 반환 API")
	public Map<String, Object> getStudyBoardLists(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam(defaultValue = "0") Integer page, Integer groupId) {
		return studyGroupDetailService.getStudyBoardLists(principalDetail, page, groupId);
	}

	@ResponseBody
	@DeleteMapping("/board")
	@Operation(summary = "스터디 그룹 게시판 글 저장 및 수정 API", description = "스터디그룹 상세페이지에서 게시판 작성 글 저장 및 수정 API")
	public Map<String, Object> deleteStudyBoardDetail(@AuthenticationPrincipal PrincipalDetail principalDetail,
			@RequestParam Integer groupId, @RequestParam Integer boardId, HttpServletRequest req) {
		return studyGroupDetailService.deleteStudyBoardDetail(principalDetail, groupId, boardId, req);
	}

}
