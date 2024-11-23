package com.study.mms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.CreateStudyGroupDTO;
import com.study.mms.dto.ReponseJoinDTO;
import com.study.mms.dto.StudyCommentsDTO;
import com.study.mms.dto.StudyGroupDTO;
import com.study.mms.dto.StudyJoinDTO;
import com.study.mms.exception.ResourceNotFoundException;
import com.study.mms.model.StudyComment;
import com.study.mms.model.StudyGroup;
import com.study.mms.model.StudyGroup.ApprovalStatus;
import com.study.mms.model.StudyGroup.RecruitmentStatus;
import com.study.mms.model.StudyGroupJoinRequest;
import com.study.mms.model.StudyGroupMember;
import com.study.mms.model.StudyReply;
import com.study.mms.model.User;
import com.study.mms.repository.StudyCommentRepository;
import com.study.mms.repository.StudyGroupJoinRequestRepository;
import com.study.mms.repository.StudyGroupMemberRepository;
import com.study.mms.repository.StudyGroupRepository;
import com.study.mms.repository.StudyReplyRepository;
import com.study.mms.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

	private final UserRepository userRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final StudyGroupMemberRepository studyGroupMemberRepository;
	private final StudyGroupJoinRequestRepository joinRequestRepository;
	private final StudyCommentRepository studyCommentRepository;
	private final StudyReplyRepository studyReplyRepository;

	// 스터디 그룹 생성
	@Transactional
	public Map<String, Object> createStudyGroup(Authentication authentication, CreateStudyGroupDTO groupDTO) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// Authentication 객체에서 PrincipalDetail 객체를 가져옴
			PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();

			// 인증된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			// 태그 값에서 마지막에 붙은 쉼표 제거
			String tags = groupDTO.getTag();
			if (tags != null && tags.endsWith(",")) {
				tags = tags.substring(0, tags.length() - 1); // 마지막 쉼표 제거
			}

			User subLeader = null;

			// 부방장이 지정된 경우에만 검색
			if (groupDTO.getSubLeader() != null && !groupDTO.getSubLeader().isEmpty()) {
				subLeader = userRepository.findByUsername(groupDTO.getSubLeader()).orElse(null); // 부방장이 없을 경우 null로 설정
			}

			// 만일 신규 생성이 아니라면
			if (groupDTO.getId() != null) {

				// 스터디 그룹 정보 가져오기
				StudyGroup studyGroup = studyGroupRepository.findById(groupDTO.getId())
						.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

				// 이전과 동일한 이름이라면
				// 스터디 그룹 이름 중복 확인 (이전 이름과 동일한 경우 제외)
				if (!studyGroup.getName().equals(groupDTO.getName())
						&& studyGroupRepository.existsByName(groupDTO.getName())) {
					throw new IllegalArgumentException("같은 이름의 스터디 그룹이 이미 존재합니다.");
				}

				// 기존 스터디 그룹의 필드 업데이트
				studyGroup.setName(groupDTO.getName());
				studyGroup.setDescription(groupDTO.getDescription());
				studyGroup.setLeader(user);
				studyGroup.setSubLeader(subLeader); // 부방장이 없으면 null로 설정
				studyGroup.setMaxMembers(groupDTO.getMaxMembers());
				studyGroup.setStudyTag(tags);

				// 스터디 그룹 저장
				studyGroupRepository.save(studyGroup);

				// 성공 응답 설정
				returnMap.put("status", "success");
				returnMap.put("message", "스터디 그룹이 수정되었습니다.");
				returnMap.put("groupId", studyGroup.getId());

				return returnMap;
			}

			// 스터디 그룹 이름 중복 확인
			if (studyGroupRepository.existsByName(groupDTO.getName())) {
				returnMap.put("status", "error");
				returnMap.put("message", "같은 이름의 스터디 그룹이 이미 존재합니다.");
				return returnMap;
			}

			// 스터디 그룹 생성 (부방장이 없으면 null로 설정)
			StudyGroup studyGroup = StudyGroup.builder().name(groupDTO.getName()).description(groupDTO.getDescription())
					.leader(user).subLeader(subLeader) // 부방장이 없으면 null로 설정
					.maxMembers(groupDTO.getMaxMembers()).status(ApprovalStatus.PENDING)
					.recruitmentStatus(RecruitmentStatus.PENDING_APPROVAL).studyTag(tags).build();

			// 스터디 그룹 저장
			studyGroupRepository.save(studyGroup);

			// 성공 응답 설정
			returnMap.put("status", "success");
			returnMap.put("message", "스터디 그룹이 생성되었습니다.");
			returnMap.put("groupId", studyGroup.getId());

		} catch (Exception e) {
			// 예외 처리
			returnMap.put("status", "error");
			returnMap.put("message", "스터디 그룹 생성 중 오류가 발생했습니다.");
			e.printStackTrace();
		}
		return returnMap;
	}

	// 스터디그룹 삭제
	@Transactional
	public Map<String, Object> deleteStudyGroup(Authentication authentication, Integer num) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// 스터디 그룹 정보 가져오기
			StudyGroup studyGroup = studyGroupRepository.findById(num)
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();

			// 인증된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			// 권한 일치 확인
			if (!studyGroup.getLeader().getId().equals(user.getId())) {
				returnMap.put("status", "error");
				returnMap.put("message", "삭제 권한이 없습니다.");
				return returnMap;
			}

			studyGroupRepository.delete(studyGroup);
			returnMap.put("status", "success");
			returnMap.put("message", "삭제되었습니다.");

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "스터디그룹삭제 중 오류가 발생하였습니다.");
			e.printStackTrace();
		}

		return returnMap;
	};

	// 스터디 그룹 가입 신청(유저가 스터디 그룹에 가입 신청을 함)
	@Transactional
	//@Transactional(rollbackOn = Exception.class)
	public Map<String, Object> RequestJoinStudyGroup(Authentication authentication, StudyJoinDTO studyJoinDTO) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			// Authentication 객체에서 PrincipalDetail 객체를 가져옴
			PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();

			// 인증된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			// 스터디 그룹 정보 가져오기
			StudyGroup studyGroup = studyGroupRepository.findById(studyJoinDTO.getStudyNum())
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			// 이미 가입된 회원인지 확인
			boolean isAlreadyMember = studyGroupMemberRepository.existsByStudyGroupAndUser(studyGroup, user);
			if (isAlreadyMember) {
				throw new IllegalArgumentException("이미 가입된 스터디 그룹입니다.");
			}
			// 신청한 기록이 있는지 확인
			boolean hasPendingRequest = joinRequestRepository.existsByStudyGroupAndUserAndStatus(studyGroup, user,
					StudyGroupJoinRequest.RequestStatus.PENDING);
			if (hasPendingRequest) {
				throw new IllegalArgumentException("이미 가입 신청을 하였습니다.");
			}
			// 신청 가능한 인원인지 확인 필요 (예: 최대 인원 제한 확인)
			if (studyGroup.getMembers().size() >= studyGroup.getMaxMembers()) {
				throw new IllegalArgumentException("스터디 그룹의 최대 가입 인원을 초과했습니다.");
			}

			// 별도의 가입 신청 엔티티를 이용해 신청 정보 저장
			StudyGroupJoinRequest joinRequest = StudyGroupJoinRequest.builder().studyGroup(studyGroup).user(user)
					.status(StudyGroupJoinRequest.RequestStatus.PENDING).build();
			joinRequestRepository.save(joinRequest);

			returnMap.put("status", "success");
			returnMap.put("message", "스터디 그룹 가입 신청이 완료되었습니다.");

		} catch (IllegalArgumentException e) {
			returnMap.put("status", "error");
			returnMap.put("message", e.getMessage());
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "스터디 그룹 가입 신청 중 오류가 발생했습니다.");
		}
		return returnMap;
	}

	// 스터디 그룹 내가 가입한 스터디 리스트 반환
	@Transactional
	public List<StudyGroupDTO> getUserStudyGroup(PrincipalDetail principalDetail) {
		User user = principalDetail.getUser();

		// 승인된 스터디 그룹 목록을 가져옴
//		List<StudyGroup> studyGroups = studyGroupRepository.findByStatusAndMembersUserId(ApprovalStatus.APPROVED,
//				user.getId());

		List<StudyGroup> studyGroups = studyGroupRepository.findApprovedStudyGroupsByUserId(user.getId());

		// StudyGroup 엔티티를 StudyGroupDTO로 변환하여 필요한 정보만 반환
		return studyGroups.stream().map(studyGroup -> {
			StudyGroupMember member = studyGroup.getMembers().stream()
					.filter(m -> m.getUser().getId().equals(user.getId())).findFirst().orElse(null);

			if (member == null) {
				return null;
			}

			return StudyGroupDTO.builder().id(studyGroup.getId()).name(studyGroup.getName())
					.role(member.getRole().name()).build();
		}).filter(dto -> dto != null).collect(Collectors.toList());
	}

	// 스터디 그룹 정보 조회 (번호와 이름으로 조회)
	@Transactional
	public Map<String, Object> getStudyGroupByIdOrName(Integer groupId, String groupName) {
		Map<String, Object> returnMap = new HashMap<>();
		try {
			StudyGroup studyGroup;
			if (groupId != null) {
				studyGroup = studyGroupRepository.findById(groupId)
						.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));
			} else if (groupName != null && !groupName.isEmpty()) {
				studyGroup = studyGroupRepository.findByName(groupName)
						.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));
			} else {
				throw new IllegalArgumentException("유효한 그룹 ID 또는 그룹 이름을 입력해주세요.");
			}
			// 성공 응답 설정
			returnMap.put("status", "success");
			returnMap.put("studyGroup", studyGroup);
		} catch (IllegalArgumentException e) {
			// 예외 처리: 스터디 그룹 정보를 찾을 수 없거나 유효하지 않은 입력
			returnMap.put("status", "error");
			returnMap.put("message", e.getMessage());
		} catch (Exception e) {
			// 일반적인 예외 처리
			returnMap.put("status", "error");
			returnMap.put("message", "스터디 그룹 조회 중 오류가 발생했습니다.");
		}
		return returnMap;
	}

	// 방장이 회원 가입 리스트 반환 studyGroupDetail 에서 사용
//	@Transactional
//	public Map<String, Object> getJoinRequestsForLeader(Authentication authentication, StudyJoinDTO studyJoinDTO) {
//		Map<String, Object> returnMap = new HashMap<>();
//
//		try {
//			// Authentication 객체에서 PrincipalDetail 객체를 가져옴
//			PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();
//
//			// 인증된 사용자 정보 가져오기
//			User user = principalDetail.getUser();
//
//			// 스터디 그룹 정보 가져오기
//			StudyGroup studyGroup = studyGroupRepository.findById(studyJoinDTO.getStudyNum())
//					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));
//
//			// 현재 사용자가 방장인지 확인(equals 두 객체가 같은지 비교)
//			if (!studyGroup.getLeader().getId().equals(user.getId())) {
//				throw new IllegalArgumentException("해당 스터디 그룹의 방장이 아닙니다.");
//			}
//
//			// 가입 신청 리스트 가져오기
//			List<StudyGroupJoinRequest> joinRequests = joinRequestRepository.findByStudyGroupAndStatus(studyGroup,
//					StudyGroupJoinRequest.RequestStatus.PENDING);
//			// DTO 리스트로 변환
//			List<ReponseJoinDTO> joinRequestDTOs = joinRequests.stream().map(ReponseJoinDTO::new)
//					.collect(Collectors.toList());
//
//			// 성공 응답 설정
//			returnMap.put("status", "success");
//			returnMap.put("joinRequests", joinRequestDTOs);
//		} catch (IllegalArgumentException e) {
//			returnMap.put("status", "error");
//			returnMap.put("message", e.getMessage());
//		} catch (Exception e) {
//			returnMap.put("status", "error");
//			returnMap.put("message", "가입 신청 리스트 조회 중 오류가 발생했습니다.");
//		}
//		return returnMap;
//	}

	// 방장 회원가입 승인 혹은 거절 처리
	// 승인이 되면 승인완료 처리로 상태를 변경하며 해당 그룹에 속해지도록 처리 ( 거절이 없으며 유저 정보 찾는게 없음)
//	@Transactional
//	public Map<String, Object> approveJoinRequest(Authentication authentication, StudyJoinDTO studyJoinDTO) {
//		Map<String, Object> returnMap = new HashMap<>();
//
//		// Authentication 객체에서 PrincipalDetail 객체를 가져옴
//		PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();
//
//		// 인증된 사용자 정보 가져오기
//		User user = principalDetail.getUser();
//
//		// 스터디 그룹 정보 가져오기
//		StudyGroup studyGroup = studyGroupRepository.findById(studyJoinDTO.getStudyNum())
//				.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));
//
//		// 현재 사용자가 방장인지 확인(equals 두 객체가 같은지 비교)
//		if (!studyGroup.getLeader().getId().equals(user.getId())) {
//			throw new IllegalArgumentException("해당 스터디 그룹의 방장이 아닙니다.");
//		}
//
//		// 가입 요청 조회
//		StudyGroupJoinRequest joinRequest = joinRequestRepository.findById(studyJoinDTO.getRequestId())
//				.orElseThrow(() -> new IllegalArgumentException("해당 가입 신청을 찾을 수 없습니다."));
//
//		// 스터디 그룹 회원 리스트에 추가
//		StudyGroupMember newMember = StudyGroupMember.builder().studyGroup(studyGroup).user(joinRequest.getUser())
//				.role(StudyGroupMember.StudyRole.MEMBER).build();
//		studyGroupMemberRepository.save(newMember);
//
//		// 상태를 승인 상태로 변경
//		joinRequest.setStatus(StudyGroupJoinRequest.RequestStatus.APPROVED);
//		joinRequestRepository.save(joinRequest);
//
//		// 성공 응답 설정
//		returnMap.put("status", "success");
//		returnMap.put("message", "회원가입 신청이 승인되었습니다.");
//		return returnMap;
//	}

	// 홈페이지 스터디 그룹 리스트 반환
	@Transactional
	public Page<StudyGroupDTO> getStudyGroupLists(PrincipalDetail principalDetai, int page, String type,
			String searchText, String tag) {
		// TODO Auto-generated method stub

		Pageable pageable = PageRequest.of(page, 10); // 10개씩 리스트 가져오기

		// 타입별 필터링 처리
		Page<StudyGroup> studyGroupsPage = studyGroupRepository.searchStudyGroups(searchText, type, tag, pageable);

		return studyGroupsPage.map(studyGroup -> StudyGroupDTO.builder().id(studyGroup.getId())
				.name(studyGroup.getName()).createdAt(studyGroup.getCreatedAt()).status(studyGroup.getStatus())
				.recruitmentStatus(studyGroup.getRecruitmentStatus()).description(studyGroup.getDescription())
				.tag(studyGroup.getStudyTag()).leader(studyGroup.getLeader()).subLeader(studyGroup.getSubLeader())
				.approvalDecisionAt(studyGroup.getApprovalDecisionAt())
				.memberCount(studyGroup.getMembers() != null ? studyGroup.getMembers().size() : 0)
				.maxMembers(studyGroup.getMaxMembers()).commentLength(studyGroup.getComments().size()).build());
	}

	// 단일 스터디 그룹 정보 반환
	@Transactional
	public StudyGroupDTO getStudyGroupDetail(Integer id) {
		// ID로 StudyGroup 조회
		Optional<StudyGroup> optionalStudyGroup = studyGroupRepository.findById(id);

		// StudyGroup이 존재하고 승인 상태가 'PENDING_APPROVAL'이 아닌 경우에만 DTO로 변환
		return optionalStudyGroup
				.filter(studyGroup -> studyGroup.getRecruitmentStatus() != RecruitmentStatus.PENDING_APPROVAL)
				.map(studyGroup -> StudyGroupDTO.builder().id(studyGroup.getId()).name(studyGroup.getName())
						.createdAt(studyGroup.getCreatedAt()).status(studyGroup.getStatus())
						.recruitmentStatus(studyGroup.getRecruitmentStatus()).description(studyGroup.getDescription())
						.tag(studyGroup.getStudyTag()).leader(studyGroup.getLeader())
						.subLeader(studyGroup.getSubLeader()).approvalDecisionAt(studyGroup.getApprovalDecisionAt())
						.memberCount(studyGroup.getMembers() != null ? studyGroup.getMembers().size() : 0)
						.maxMembers(studyGroup.getMaxMembers()).build())
				.orElseThrow(() -> new ResourceNotFoundException("해당하는 페이지가 없습니다."));
	}

	// 스터디 그룹 게시판 댓글 저장
	public Map<String, Object> saveComment(PrincipalDetail principalDetail, Integer studyGroupId, String comment) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// 스터디 그룹 정보 가져오기
			StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			// PrincipalDetail을 통해 현재 로그인된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			// 댓글 엔티티 생성
			StudyComment newComment = StudyComment.builder().content(comment).user(user).studyGroup(studyGroup).build();
			// 댓글 저장
			studyCommentRepository.save(newComment);

//			// 스터디 그룹의 댓글 목록 가져오기
//			List<StudyComment> comments = studyCommentRepository.findByStudyGroupId(studyGroupId);
//
//			// 댓글 목록을 StudyCommentsDTO로 변환
//			List<StudyCommentsDTO> commentsDTO = comments.stream()
//					.map(commentData -> StudyCommentsDTO.builder().id(commentData.getId())
//							.content(commentData.getContent()).createdAt(commentData.getCreatedAt())
//							.userInfo(commentData.getUser())
//							.isAuthor(commentData.getUser().getId().equals(user.getId())).build())
//					.collect(Collectors.toList());

			// 댓글 DTO 생성
//			StudyCommentsDTO commentDTO = StudyCommentsDTO.builder().id(newComment.getId())
//					.content(newComment.getContent()).createdAt(newComment.getCreatedAt())
//					.userInfo(newComment.getUser()) // 빌더에
//					.build();

			// 성공 응답 설정
			returnMap.put("status", "success");
			returnMap.put("message", "저장되었습니다.");
			// returnMap.put("data", commentsDTO);

		} catch (IllegalArgumentException e) {
			// 예외 처리: 스터디 그룹 정보를 찾을 수 없거나 유효하지 않은 입력
			returnMap.put("status", "error");
			returnMap.put("message", e.getMessage());
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "가입 신청 리스트 조회 중 오류가 발생했습니다.");
		}
		return returnMap;
	}

	// 스터디 그룹 게시판 댓글 목록 조회
	public Map<String, Object> getComments(PrincipalDetail principalDetail, Integer studyGroupId) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// PrincipalDetail을 통해 현재 로그인된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			// 스터디 그룹의 댓글 목록 가져오기
			List<StudyComment> comments = studyCommentRepository.findByStudyGroupId(studyGroupId);
			// List<StudyComment> comments =
			// studyCommentRepository.findByStudyGroupIdOrderByCreatedAtDescIdAsc(studyGroupId);

			// 댓글 목록을 StudyCommentsDTO로 변환
//			List<StudyCommentsDTO> commentDTOs = comments.stream()
//					.map(comment -> StudyCommentsDTO.builder().id(comment.getId()).content(comment.getContent())
//							.createdAt(comment.getCreatedAt()).userInfo(comment.getUser())
//							.isAuthor(comment.getUser().getId().equals(user.getId())).build())
//					.collect(Collectors.toList());

			// 댓글 목록을 StudyCommentsDTO로 변환
			List<StudyCommentsDTO> commentDTOs = comments.stream().map(comment -> {
				// 댓글에 연결된 답글 리스트 가져오기
				List<StudyReply> replies = studyReplyRepository.findByStudyCommentId(comment.getId());

				// 엔티티를 DTO로 변환
				return StudyCommentsDTO.fromEntity(comment, replies, user);
			}).collect(Collectors.toList());

			// 성공 응답 설정
			returnMap.put("status", "success");
			returnMap.put("data", commentDTOs);
		} catch (Exception e) {
			// 예외 처리: 오류가 발생한 경우
			returnMap.put("status", "error");
			returnMap.put("message", "댓글 목록 조회 중 오류가 발생했습니다.");
		}

		return returnMap;
	}

	// 모집종료 처리
	public Map<String, Object> setRecruitmentClosed(PrincipalDetail principalDetail, Integer studyGroupId,
			String recruitmentStatus) {
		// TODO Auto-generated method stub

		Map<String, Object> returnMap = new HashMap<>();

		try {

			// 스터디 그룹 정보 가져오기
			StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
					.orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

			// PrincipalDetail을 통해 현재 로그인된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			RecruitmentStatus status;

			if ("RECRUITMENT_CLOSED".equals(recruitmentStatus)) {
				status = RecruitmentStatus.RECRUITING;
			} else if ("RECRUITING".equals(recruitmentStatus)) {
				status = RecruitmentStatus.RECRUITMENT_CLOSED;
			} else {
				// 유효하지 않은 상태 값에 대해 예외를 던짐
				throw new IllegalArgumentException("유효하지 않는 상태입니다. ");
			}

			studyGroup.setRecruitmentStatus(status);
			studyGroupRepository.save(studyGroup);

			// 성공 응답 설정
			returnMap.put("status", "success");
			returnMap.put("message", "변경되었습니다.");

		} catch (IllegalArgumentException e) {
			// 예외 처리: 스터디 그룹 정보를 찾을 수 없거나 유효하지 않은 입력
			returnMap.put("status", "error");
			returnMap.put("message", e.getMessage());
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
		}
		return returnMap;

	}

	// 댓글 수정하기
	@Transactional
	public Map<String, Object> updateComment(PrincipalDetail principalDetail, Integer commentId, String comments) {

		Map<String, Object> returnMap = new HashMap<>();
		try {

			if (comments.length() > 200) {
				throw new IllegalArgumentException("댓글은 200자를 초과할 수 없습니다.");
			}

			User user = principalDetail.getUser();

			Optional<StudyComment> optionalComment = studyCommentRepository.findById(commentId);

			if (optionalComment.isPresent()) {
				StudyComment comment = optionalComment.get();

				// 작성자와 로그인한 사람이 일치하는지 확인
				if (!comment.getUser().getId().equals(user.getId())) {
					returnMap.put("status", "error");
					returnMap.put("message", "권한이 없습니다.");
					return returnMap;
				}

				comment.setContent(comments);
				studyCommentRepository.save(comment);

				returnMap.put("status", "success");
				returnMap.put("message", "변경되었습니다.");

			} else {
				// 댓글이 존재하지 않는 경우 처리
				returnMap.put("status", "error");
				returnMap.put("message", "댓글을 찾을 수 없습니다.");
			}

		} catch (IllegalArgumentException e) {
			// 유효성 검사 실패나 존재하지 않는 스터디 그룹인 경우 오류 처리
			returnMap.put("status", "error");
			returnMap.put("message", e.getMessage());
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
		}

		return returnMap;
	}

	// 댓글 삭제하기
	@Transactional
	public Map<String, Object> deleteComment(PrincipalDetail principalDetail, Integer commentId) {
		Map<String, Object> returnMap = new HashMap<>();
		User user = principalDetail.getUser();
		try {

			Optional<StudyComment> optionalComment = studyCommentRepository.findById(commentId);

			if (optionalComment.isPresent()) {
				StudyComment comment = optionalComment.get();

				// 작성자와 로그인한 사람이 일치하는지 확인
				if (!comment.getUser().getId().equals(user.getId())) {
					returnMap.put("status", "error");
					returnMap.put("message", "권한이 없습니다.");
					return returnMap;
				}

				studyCommentRepository.delete(comment);

				returnMap.put("status", "success");
				returnMap.put("message", "삭제되었습니다.");

			} else {
				// 댓글이 존재하지 않는 경우 처리
				returnMap.put("status", "error");
				returnMap.put("message", "댓글을 찾을 수 없습니다.");
			}

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생했습니다.");
		}

		return returnMap;
	}

	// 스터디 그룹 답글 저장
	@Transactional
	public Map<String, Object> saveStudyCommentReply(PrincipalDetail principalDetail, String reply, Integer commentId) {
		Map<String, Object> returnMap = new HashMap<>();
		try {

			if (reply == null || reply.trim().isEmpty()) {
				returnMap.put("status", "error");
				returnMap.put("message", "답글 내용을 입력해주세요.");
				return returnMap;
			}

			if (reply.length() > 200) {
				returnMap.put("status", "error");
				returnMap.put("message", "답글은 200자를 초과할 수 없습니다.");
				return returnMap;
			}

			User user = principalDetail.getUser();

			Optional<StudyComment> optionalComment = studyCommentRepository.findById(commentId);
			if (optionalComment.isPresent()) {
				StudyComment studyComment = optionalComment.get();

				StudyReply newRelpy = new StudyReply();
				newRelpy.setUser(user);
				newRelpy.setContent(reply);
				newRelpy.setStudyComment(studyComment);
				studyReplyRepository.save(newRelpy);
				returnMap.put("status", "success");
				returnMap.put("message", "저장되었습니다.");
			} else {
				// 댓글이 존재하지 않는 경우 처리
				returnMap.put("status", "error");
				returnMap.put("message", "댓글을 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
			e.printStackTrace();
		}

		return returnMap;
	}

	// 스터디 그룹 답글 수정
	@Transactional
	public Map<String, Object> updateStudyCommentReply(PrincipalDetail principalDetail, String reply, Integer replyId) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

			if (reply == null || reply.trim().isEmpty()) {
				returnMap.put("status", "error");
				returnMap.put("message", "답글 내용을 입력해주세요.");
				return returnMap;
			}

			if (reply.length() > 200) {
				returnMap.put("status", "error");
				returnMap.put("message", "답글은 200자를 초과할 수 없습니다.");
				return returnMap;
			}

			User user = principalDetail.getUser();

			Optional<StudyReply> optionalReply = studyReplyRepository.findById(replyId);

			if (optionalReply.isPresent()) {

				StudyReply studyReply = optionalReply.get();

				// 여기서 작성자 일치하는지 확인
				if (!studyReply.getUser().getId().equals(user.getId())) {
					returnMap.put("status", "error");
					returnMap.put("message", "권한이 없습니다.");
					return returnMap;

				}
				studyReply.setContent(reply);
				studyReplyRepository.save(studyReply);

				returnMap.put("status", "success");
				returnMap.put("message", "수정되었습니다.");

			} else {
				// 댓글이 존재하지 않는 경우 처리
				returnMap.put("status", "error");
				returnMap.put("message", "답글 찾을 수 없습니다.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
			e.printStackTrace();

		}

		return returnMap;
	}

	// 스터디 그룹 답글 삭제
	@Transactional
	public Map<String, Object> deleteStudyCommentReply(PrincipalDetail principalDetail, Integer replyId) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

			User user = principalDetail.getUser();

			Optional<StudyReply> optionalReply = studyReplyRepository.findById(replyId);

			if (optionalReply.isPresent()) {
				StudyReply studyReply = optionalReply.get();
				// 여기서 작성자 일치하는지 확인
				if (!studyReply.getUser().getId().equals(user.getId())) {
					returnMap.put("status", "error");
					returnMap.put("message", "권한이 없습니다.");
					return returnMap;
				}
				studyReplyRepository.delete(studyReply);

				returnMap.put("status", "success");
				returnMap.put("message", "삭제되었습니다.");
			} else {
				// 댓글이 존재하지 않는 경우 처리
				returnMap.put("status", "error");
				returnMap.put("message", "답글 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
			e.printStackTrace();
		}

		return returnMap;
	}

	// 스터디 그룹 상세페이지 접근 권한 확인
	public boolean isUserMemberOfGroup(PrincipalDetail principalDetail, Integer groupId) {
		// TODO Auto-generated method stub
		User user = principalDetail.getUser();
		return studyGroupRepository.isUserMemberOfGroup(user.getId(), groupId) > 0;
	}

	// 스터디 그룹 권한 확인
	public String isUserMemberOfRole(PrincipalDetail principalDetail, Integer groupId) {
		// TODO Auto-generated method stub
		User user = principalDetail.getUser();

		// 현재 스터디 그룹에서 나의 권한을 확인
		Optional<StudyGroupMember> optionalStudyGroupMember = studyGroupMemberRepository
				.findByStudyGroupIdAndUser(groupId, user);

		// 사용자가 멤버가 아니라면 접근 거부 예외 던짐(권한 처리 추후 수정 필요)
		if (!optionalStudyGroupMember.isPresent()) {
			throw new IllegalArgumentException("사용자가 해당 그룹의 멤버가 아닙니다.");
		}

		// StudyRole을 문자열로 변환하여 반환
		return optionalStudyGroupMember.get().getRole().name();

	}

}
