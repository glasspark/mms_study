package com.study.mms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.CreateStudyGroupDTO;
import com.study.mms.dto.ReponseJoinDTO;
import com.study.mms.dto.ScheduleDTO;
import com.study.mms.dto.StudyBoardDTO;
import com.study.mms.dto.StudyGroupMemberDTO;
import com.study.mms.dto.UploadedFileDTO;
import com.study.mms.model.GroupSchedule;
import com.study.mms.model.StudyBoard;
import com.study.mms.model.StudyGroup;
import com.study.mms.model.StudyGroupJoinRequest;
import com.study.mms.model.StudyGroupMember;
import com.study.mms.model.UploadedFile;
import com.study.mms.model.User;
import com.study.mms.repository.GroupScheduleRepository;
import com.study.mms.repository.StudyBoardRepository;
import com.study.mms.repository.StudyGroupJoinRequestRepository;
import com.study.mms.repository.StudyGroupMemberRepository;
import com.study.mms.repository.StudyGroupRepository;
import com.study.mms.repository.UploadedFileRepository;
import com.study.mms.repository.UserRepository;
import com.study.mms.util.ImageUploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyGroupDetailService {

	private final UserRepository userRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final StudyGroupMemberRepository studyGroupMemberRepository;
	private final GroupScheduleRepository groupScheduleRepository;
	private final StudyGroupJoinRequestRepository joinRequestRepository;
	private final UploadedFileRepository uploadedFileRepository;
	private final StudyBoardRepository studyBoardRepository;

	@Transactional
	public Map<String, Object> test(Authentication authentication, CreateStudyGroupDTO groupDTO) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}

		return returnMap;
	}

	// 스케줄 등록
	@Transactional
	public Map<String, Object> addSchedule(PrincipalDetail principalDetail, ScheduleDTO scheduleDTO) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// 스터디 그룹 정보 가져오기
			StudyGroup studyGroup = studyGroupRepository.findById(scheduleDTO.getGroupId())
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			// PrincipalDetail을 통해 현재 로그인된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			GroupSchedule schedule = new GroupSchedule();
			schedule.setStartDate(scheduleDTO.getStartDate());
			schedule.setEndDate(scheduleDTO.getEndDate());
			schedule.setTitle(scheduleDTO.getTitle());
			schedule.setStudyGroup(studyGroup);

			groupScheduleRepository.save(schedule);

			// DTO에 데이터 담아서 반환 (단일 객체)
			ScheduleDTO scheduleDTOResult = ScheduleDTO.builder().id(schedule.getId())
					.groupId(schedule.getStudyGroup().getId()) // 그룹 ID 매핑
					.title(schedule.getTitle()) // 제목 매핑
					.startDate(schedule.getStartDate()) // 시작 날짜 매핑
					.endDate(schedule.getEndDate()) // 종료 날짜 매핑
					.build();

			returnMap.put("status", "success");
			returnMap.put("message", "저장되었습니다.");
			returnMap.put("data", scheduleDTOResult); // 단일 객체로 반환
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}

		return returnMap;

	}

//스터디 그룹 상세 스케줄 가져오기 
	@Transactional
	public Map<String, Object> getSchedule(PrincipalDetail principalDetail, Integer groupId, int month, int year) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<>();
		try {
			// 월을 가져와서 해당하는 월 기간 조회
			List<GroupSchedule> scheduleList = groupScheduleRepository.findByYearAndMonthNative(year, month, groupId);

			// DTO 에 데이터 담아서 반환
			List<ScheduleDTO> scheduleDTOList = scheduleList.stream()
					.map(schedule -> ScheduleDTO.builder().id(schedule.getId())
							.groupId(schedule.getStudyGroup().getId()) // 그룹 ID 매핑
							.title(schedule.getTitle()) // 제목 매핑
							.startDate(schedule.getStartDate()) // 시작 날짜 매핑
							.endDate(schedule.getEndDate()) // 종료 날짜 매핑
							.build())
					.collect(Collectors.toList());

			returnMap.put("status", "success");
			returnMap.put("message", "조회되었습니다.");
			returnMap.put("data", scheduleDTOList);
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}

		return returnMap;
	}

	// 스터디 그룹 상세 스케줄 삭제
	@Transactional
	public Map<String, Object> deleteSchedule(PrincipalDetail principalDetail, Integer groupId, Integer scheduleId) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<>();
		try {
			Optional<GroupSchedule> optionalSchedule = groupScheduleRepository.findById(scheduleId);
			if (!optionalSchedule.isPresent()) {
				returnMap.put("status", "error");
				returnMap.put("message", "해당하는 스케줄이 없습니다.");
			}

			GroupSchedule userSchedule = optionalSchedule.get();
			groupScheduleRepository.delete(userSchedule);
			returnMap.put("status", "success");
			returnMap.put("message", "삭제되었습니다.");

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}

		return returnMap;
	}

	@Transactional
	public Map<String, Object> getApplicationLists(PrincipalDetail principalDetail, Integer groupId) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<>();
		try {

			User user = principalDetail.getUser();

			// 스터디 그룹 정보 가져오기
			StudyGroup studyGroup = studyGroupRepository.findById(groupId)
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			// 현재 사용자가 방장인지 확인(equals 두 객체가 같은지 비교)
//			if (!studyGroup.getLeader().getId().equals(user.getId())) {
//				throw new IllegalArgumentException("해당 스터디 그룹의 방장이 아닙니다.");
//			}

			// 현재 사용자가 방장 또는 부방장인지 확인
			if (!studyGroup.getLeader().getId().equals(user.getId())
					&& (studyGroup.getSubLeader() == null || !studyGroup.getSubLeader().getId().equals(user.getId()))) {
				throw new AccessDeniedException("권한이 없습니다.");
			}

			// 가입 신청 리스트 가져오기
			List<StudyGroupJoinRequest> joinRequests = joinRequestRepository.findByStudyGroupAndStatus(studyGroup,
					StudyGroupJoinRequest.RequestStatus.PENDING);
			// DTO 리스트로 변환
			List<ReponseJoinDTO> joinRequestDTOs = joinRequests.stream().map(ReponseJoinDTO::new)
					.collect(Collectors.toList());

			// 성공 응답 설정
			returnMap.put("status", "success");
			returnMap.put("data", joinRequestDTOs);
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}

		return returnMap;
	}

	// 스터디 그룹 멤버 승인, 거절 처리
	@Transactional
	public Map<String, Object> applicationJoinRequestProcess(PrincipalDetail principalDetail, Integer groupId,
			Integer requestId, String status) {
		Map<String, Object> returnMap = new HashMap<>();

		// 인증된 사용자 정보 가져오기
		User user = principalDetail.getUser();

		// 스터디 그룹 정보 가져오기
		StudyGroup studyGroup = studyGroupRepository.findById(groupId)
				.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

		// 현재 사용자가 방장인지 확인(equals 두 객체가 같은지 비교)
		if (!studyGroup.getLeader().getId().equals(user.getId())) {
			throw new IllegalArgumentException("해당 스터디 그룹의 방장이 아닙니다.");
		}

		// 가입 요청 조회
		StudyGroupJoinRequest joinRequest = joinRequestRepository.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("해당 가입 신청을 찾을 수 없습니다."));

		if ("APPROVED".equals(status)) {
			// 스터디 그룹 회원 리스트에 추가
			StudyGroupMember newMember = StudyGroupMember.builder().studyGroup(studyGroup).user(joinRequest.getUser())
					.role(StudyGroupMember.StudyRole.MEMBER).build();
			studyGroupMemberRepository.save(newMember);

			// 상태를 승인 상태로 변경
			joinRequest.setStatus(StudyGroupJoinRequest.RequestStatus.APPROVED);
			returnMap.put("status", "success");
			returnMap.put("message", "회원가입 신청이 승인되었습니다.");
		} else {

			// 거절인 경우 상태만 변경
			joinRequest.setStatus(StudyGroupJoinRequest.RequestStatus.REJECTED);
			returnMap.put("status", "success");
			returnMap.put("message", "회원가입 신청이 거절되었습니다.");
		}

		joinRequestRepository.save(joinRequest);

		// 성공 응답 설정
		return returnMap;
	}

	// 공유 파일 업로드
	@Transactional
	public Map<String, Object> uploadGroupFile(PrincipalDetail principalDetail, @RequestParam Integer groupId,
			@RequestParam("file") MultipartFile file, HttpServletRequest req, String title) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// 스터디 그룹 찾기
			StudyGroup studyGroup = studyGroupRepository.findById(groupId)
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			UploadedFile newFile = new UploadedFile();

			// 인증된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			// 새로운 이미지 업로드 및 정보 설정
			String imageUrl = ImageUploader.uploadImage(file, req, "/upload/groupFile/");
			newFile.setFileName(file.getOriginalFilename());
			newFile.setStudyGroup(studyGroup);
			newFile.setFilePath(imageUrl);
			newFile.setUserId(user.getId());
			newFile.setTitle(title);
			uploadedFileRepository.save(newFile);
			returnMap.put("status", "success");
			returnMap.put("message", "파일이 업로드되었습니다.");

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}
		return returnMap;
	}

	// 공유 파일 삭제
	@Transactional
	public Map<String, Object> deleteGroupFile(PrincipalDetail principalDetail, @RequestParam Integer groupId,
			HttpServletRequest req, Integer fileId) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// 스터디 그룹 찾기
			StudyGroup studyGroup = studyGroupRepository.findById(groupId)
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			Optional<UploadedFile> optinalFile = uploadedFileRepository.findById(fileId);
			if (!optinalFile.isPresent()) {
				returnMap.put("status", "error");
				returnMap.put("message", "파일이 존재하지 않습니다.");
			}

			UploadedFile deleteFile = optinalFile.get();
			// 인증된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			// 반장 및 부반장 ID 가져오기
			Integer leaderId = studyGroup.getLeader().getId();
			Integer subLeaderId = studyGroup.getSubLeader() != null ? studyGroup.getSubLeader().getId() : null;

			boolean isRegistrant = deleteFile.getUserId().equals(user.getId());
			boolean canDelete = isRegistrant || user.getId().equals(leaderId) || user.getId().equals(subLeaderId);

			// 삭제 시 유효성 검사 확인
			if (!canDelete) {
				returnMap.put("status", "fail");
				returnMap.put("message", "권한이 없습니다.");
			}

			// 파일 삭제
			ImageUploader.deleteImage(req, deleteFile.getFilePath());

			uploadedFileRepository.delete(deleteFile);
			returnMap.put("status", "success");
			returnMap.put("message", "파일이 삭제되었습니다.");

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}
		return returnMap;
	}

	// 공유 파일 수정
	@Transactional
	public Map<String, Object> updateGroupFile(PrincipalDetail principalDetai, @RequestParam Integer groupId,
			@RequestParam("file") MultipartFile file, HttpServletRequest req, Integer fileId, String title) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// 스터디 그룹 찾기
			StudyGroup studyGroup = studyGroupRepository.findById(groupId)
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			Optional<UploadedFile> optinalFile = uploadedFileRepository.findById(fileId);
			if (!optinalFile.isPresent()) {
				returnMap.put("status", "error");
				returnMap.put("message", "파일이 존재하지 않습니다.");
			}

			// 업데이트 할 파일을 가져 옴
			UploadedFile updateFile = optinalFile.get();

			// 기존에 있던 파일 삭제
			ImageUploader.deleteImage(req, updateFile.getFilePath());

			// 새로운 이미지 업로드 및 정보 설정
			String imageUrl = ImageUploader.uploadImage(file, req, "/upload/groupFile/");
			updateFile.setFileName(file.getOriginalFilename());
			updateFile.setFilePath(imageUrl);
			updateFile.setTitle(title);
			uploadedFileRepository.save(updateFile);
			returnMap.put("status", "success");
			returnMap.put("message", "파일이 성공적으로 업로드되었습니다.");

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}
		return returnMap;
	}

	// 스터디 그룹 공유 파일 리스트 반환
	@Transactional
	public Map<String, Object> getGroupFileList(PrincipalDetail principalDetail, Integer groupId) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<>();
		try {

			// 인증된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			// 그룹 정보 조회
			StudyGroup studyGroup = studyGroupRepository.findById(groupId)
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			// 반장 및 부반장 ID 가져오기
			Integer leaderId = studyGroup.getLeader().getId();
			Integer subLeaderId = studyGroup.getSubLeader() != null ? studyGroup.getSubLeader().getId() : null;

			// DB에서 그룹 ID에 해당하는 파일 리스트 조회
			List<UploadedFile> files = uploadedFileRepository.findByStudyGroupIdOrderByCreatedAtDesc(groupId);
			List<UploadedFileDTO> data = files.stream().map(file -> {
				// 유저 정보 조회
				Optional<User> userOptional = userRepository.findById(file.getUserId());
				String nickname = userOptional.map(User::getNickname).orElse("Unknown User");

				// 현재 사용자와 파일의 등록자 비교 및 삭제 가능 여부 확인
				boolean isRegistrant = file.getUserId().equals(user.getId());
				boolean canDelete = isRegistrant || user.getId().equals(leaderId) || user.getId().equals(subLeaderId);
				// 만약 셋 중(작성자, 반장, 부반장) 중 1명이라면 삭제 가능
				// DTO 생성 및 반환
				return UploadedFileDTO.builder().id(file.getId()).createdAt(file.getCreatedAt()).title(file.getTitle())
						.fileName(file.getFileName()).filePath(file.getFilePath()).nickname(nickname)
						.isRegistrant(canDelete) // 삭제 가능 여부 설정
						.build();
			}).collect(Collectors.toList());

			returnMap.put("status", "success");
			returnMap.put("message", "조회 성공");
			returnMap.put("data", data);

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}
		return returnMap;
	}

	// 스터디 그룹 멤버 리스트 반환
	@Transactional
	public Map<String, Object> getGroupMemberList(PrincipalDetail principalDetail, Integer groupId) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// 그룹 정보 조회
			StudyGroup studyGroup = studyGroupRepository.findById(groupId)
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			// 인증된 사용자 정보 가져오기
			User user = principalDetail.getUser();
			Integer userId = user.getId();
			// 그룹 리더의 ID 가져오기
			Integer leaderId = studyGroup.getLeader().getId();
//
//			// 현재 멤버가 리더인지 확인 (미리 저장된 leaderId 사용)
//			boolean isLeader = user.getId().equals(leaderId);

			// 사용자가 그룹의 멤버인 경우, 멤버 리스트 가져오기
			List<StudyGroupMember> members = studyGroupMemberRepository.findByStudyGroupId(groupId);

			List<StudyGroupMemberDTO> memberDTOs = members.stream().map(member -> {
				User memberUser = member.getUser();
				// Null 체크
				if (memberUser == null) {
					return null; // 혹은 continue와 같은 방식으로 스킵할 수도 있음
				}
				// 현재 멤버가 리더인지 확인
				boolean isLeader = memberUser.getId().equals(leaderId);

				return StudyGroupMemberDTO.builder().userId(memberUser.getId()).nickname(memberUser.getNickname())
						.imgPath(memberUser.getImg_path()).imgType(memberUser.getImg_type())
						.role(member.getRole().name()).isLeader(isLeader).build();
			}).collect(Collectors.toList());

			// 로그인한 유저가 그룹 리더인지 여부 확인
			boolean isCurrentUserLeader = userId.equals(leaderId);
			returnMap.put("isCurrentUserLeader", isCurrentUserLeader);
			returnMap.put("status", "success");
			returnMap.put("message", "조회 성공");
			returnMap.put("data", memberDTOs);
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}

		return returnMap;
	}

	// 스터디 그룹 멤버 삭제
	@Transactional
	public Map<String, Object> deleteMemberWithdraw(PrincipalDetail principalDetail, Integer groupId, Integer userId) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// 그룹 정보 조회
			StudyGroup studyGroup = studyGroupRepository.findById(groupId)
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			User user = principalDetail.getUser();

			// 현재 유저가 방장인지 아닌지 확인
			if (!studyGroup.getLeader().getId().equals(user.getId())) {
				returnMap.put("status", "error");
				returnMap.put("message", "권한이 없습니다.");
			}

			Optional<StudyGroupMember> optMember = studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId,
					groupId);

			if (!optMember.isPresent()) {
				returnMap.put("status", "error");
				returnMap.put("message", "회원 혹은 스터디 그룹의 정보가 일치하지 않습니다.");
			}

			StudyGroupMember member = optMember.get();

			// 탈퇴시키려는 멤버가 부반장인지 확인 후 부반장이면 studyGroup에 있는 subleader 도 함께 삭제 처리
			if (member.getRole() == StudyGroupMember.StudyRole.SUB_LEADER) {
				studyGroup.setSubLeader(null);
				studyGroupRepository.save(studyGroup);
			}

			studyGroupMemberRepository.delete(member);
			returnMap.put("status", "success");
			returnMap.put("message", "탈퇴되었습니다.");

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}

		return returnMap;
	}

	// 스터디 그룹 상세 게시판 저장
//	@Transactional
//	public Map<String, Object> saveStudyBoard(StudyBoardDTO board, HttpServletRequest req) {
//		// TODO Auto-generated method stub
//		Map<String, Object> returnMap = new HashMap<>();
//		try {
//			// 사진이 사용되지 않으면 삭제 되도록
//			if (board.getId() != null && board.getId() != 0) {
//
//				Optional<StudyBoard> findBoard = studyBoardRepository.findById(board.getId());
//				// 해당 게시판이 없다면
//				if (!findBoard.isPresent()) {
//					returnMap.put("status", "error");
//					returnMap.put("message", "오류가 발생하였습니다.");
//					return returnMap;
//				}
//
//				StudyBoard getBoard = findBoard.get();
//				String[] imgList = getBoard.getImg().split(",");
//				if (imgList != null) {
//					String newImg = getBoard.getImg();
//					for (String s : imgList) {
//						if (!newImg.contains(s)) {
//							ImageUploader.deleteImage(req, "/upload/board/" + s);
//						}
//					}
//				}
//
//			}
//			board.setContent(board.getContent().replace("/upload/temp/", "/upload/board/")); // content의 이미지 경로가 임시폴더로
//			String[] imgName = board.getImg().split(",");
//			for (String s : imgName) {
//				System.out.println(s);
//				ImageUploader.moveImage(req, s, "/upload/board/"); // 이미지 이름들을 이용해 임시폴더에서 옮길 폴더로 이미지 이동
//			}
//			
//			studyBoardRepository.save(board);
//			returnMap.put("status", "success");
//			returnMap.put("message", "저장되었습니다.");
//
//		} catch (Exception e) {
//			returnMap.put("status", "error");
//			returnMap.put("message", "오류가 발생했습니다.");
//			e.printStackTrace();
//		}
//
//		return returnMap;
//	}

	// 스터디 그룹 상세 게시판 저장
	@Transactional
	public Map<String, Object> saveStudyBoard(StudyBoardDTO board, HttpServletRequest req) {
		Map<String, Object> returnMap = new HashMap<>();
		try {
			StudyBoard getBoard;

			// 스터디 존재 여부 확인
			StudyGroup studyGroup = studyGroupRepository.findById(board.getGroupId())
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			// 기존 게시글이 존재하는 경우 (업데이트)
			if (board.getId() != null && board.getId() != 0) {
				Optional<StudyBoard> findBoard = studyBoardRepository.findById(board.getId());

				// 해당 게시판이 없다면 오류 처리
				if (!findBoard.isPresent()) {
					returnMap.put("status", "error");
					returnMap.put("message", "오류가 발생하였습니다.");
					return returnMap;
				}

				// 기존 게시글 정보를 가져옴
				getBoard = findBoard.get();

				// 기존 이미지 목록 가져오기
				String[] imgList = getBoard.getImg().split(",");
				if (imgList != null) {
					String newImg = board.getImg(); // 업데이트할 이미지 정보
					for (String s : imgList) {
						// 새로운 이미지 목록에 기존 이미지가 포함되지 않으면 해당 이미지 삭제
						if (!newImg.contains(s)) {
							ImageUploader.deleteImage(req, "/upload/studyBoard/" + s);
						}
					}
				}
			} else {
				// 신규 게시글 생성
				getBoard = new StudyBoard();
			}

			// 게시글의 내용과 이미지 경로 업데이트
			getBoard.setTitle(board.getTitle());
			getBoard.setContent(board.getContent().replace("/upload/temp/", "/upload/studyBoard/")); // 임시 폴더의 이미지 경로
																										// 업데이트
			getBoard.setStudyGroup(studyGroup); // 스터디 그룹 정보 넣기

			// 이미지 경로 수정 (임시 폴더에서 정식 폴더로 이동)
			String[] imgName = board.getImg().split(",");
			for (String s : imgName) {
				ImageUploader.moveImage(req, s, "/upload/studyBoard/"); // 임시 폴더에서 정식 폴더로 이미지 이동 (s는 파일 이름)
			}
			getBoard.setImg(board.getImg());

			// 저장소에 게시글 저장 (신규 생성 또는 업데이트)
			studyBoardRepository.save(getBoard);

			// 성공 메시지 설정
			returnMap.put("status", "success");
			returnMap.put("message", "저장되었습니다.");

		} catch (Exception e) {
			// 오류 처리
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
			e.printStackTrace();
		}

		return returnMap;
	}

}
