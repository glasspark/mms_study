package com.study.mms.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.study.mms.dto.StudyGroupDTO;
import com.study.mms.model.StudyGroup;
import com.study.mms.model.StudyGroup.ApprovalStatus;
import com.study.mms.model.StudyGroup.RecruitmentStatus;
import com.study.mms.model.StudyGroupMember;
import com.study.mms.model.User;
import com.study.mms.repository.StudyGroupMemberRepository;
import com.study.mms.repository.StudyGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // 생성자 의존성 주입
public class AdminStudyGroupService {

	private final StudyGroupRepository studyGroupRepository;
	private final StudyGroupMemberRepository studyGroupMemberRepository;

	// 스터디 그룹 승인/거절 처리
	@Transactional
	public Map<String, Object> processStudyGroupRequest(Authentication authentication, Integer groupId, String status) {
		Map<String, Object> returnMap = new HashMap<>();
		try {
			// 주어진 번호로 스터디 그룹 찾기
			StudyGroup studyGroup = studyGroupRepository.findById(groupId)
					.orElseThrow(() -> new IllegalArgumentException("해당 번호의 스터디 그룹을 찾을 수 없습니다."));

			// 승인 처리
			if ("approve".equalsIgnoreCase(status)) {
				studyGroup.setStatus(ApprovalStatus.APPROVED);
				studyGroup.setRecruitmentStatus(RecruitmentStatus.RECRUITING);
				studyGroupRepository.save(studyGroup);

				// 승인 시, 마스터를 그룹 멤버로 등록
				User leader = studyGroup.getLeader();
				StudyGroupMember leaderMember = StudyGroupMember.builder().studyGroup(studyGroup).user(leader)
						.role(StudyGroupMember.StudyRole.LEADER).build();
				studyGroupMemberRepository.save(leaderMember);

				// 부반장이 존재할 경우에만 부 마스터를 그룹 멤버로 등록
				User subLeader = studyGroup.getSubLeader();
				if (subLeader != null) {
					StudyGroupMember subLeaderMember = StudyGroupMember.builder().studyGroup(studyGroup).user(subLeader)
							.role(StudyGroupMember.StudyRole.SUB_LEADER).build();
					studyGroupMemberRepository.save(subLeaderMember);
				}

				returnMap.put("status", "success");
				returnMap.put("message", "스터디 그룹이 승인되었습니다.");
				returnMap.put("type", "APPROVED");
			}
			// 거절 처리
			else if ("reject".equalsIgnoreCase(status)) {
				studyGroup.setStatus(ApprovalStatus.REJECTED);
				studyGroupRepository.save(studyGroup);

				returnMap.put("status", "success");
				returnMap.put("message", "스터디 그룹이 거절되었습니다.");
				returnMap.put("type", "REJECTED");
			}
			// 유효하지 않은 status 값 처리
			else {
				returnMap.put("status", "error");
				returnMap.put("message", "유효하지 않은 요청입니다. 'approve' 또는 'reject' 값을 사용해야 합니다.");
			}

			returnMap.put("groupId", studyGroup.getId());
		} catch (IllegalArgumentException e) {
			// 스터디 그룹을 찾지 못한 경우
			returnMap.put("status", "error");
			returnMap.put("message", e.getMessage());
		} catch (Exception e) {
			// 일반적인 예외 처리
			returnMap.put("status", "error");
			returnMap.put("message", "스터디 그룹 요청 처리 중 오류가 발생했습니다.");
		}
		return returnMap;
	}

	// 승인 요청이 필요한 스터디 그룹 리스트
//	@Transactional
//	public Map<String, Object> getApproveStudyGroupList(Authentication authentication, int page, String type) {
//		Map<String, Object> returnMap = new HashMap<>();
//
//		try {
//			// 페이지 설정 (페이지 번호 및 크기 설정)
//			Pageable pageable = PageRequest.of(page, 10);
//
//			// type에 따라 승인 상태별로 스터디 그룹 리스트 조회
//			Page<StudyGroup> studyGroupsPage;
//			if ("pending".equalsIgnoreCase(type)) {
//				studyGroupsPage = studyGroupRepository.findByStatusOrderByCreatedAtDesc(ApprovalStatus.PENDING,
//						pageable);
//			} else if ("approved".equalsIgnoreCase(type)) {
//				studyGroupsPage = studyGroupRepository.findByStatusOrderByCreatedAtDesc(ApprovalStatus.APPROVED,
//						pageable);
//			} else if ("rejected".equalsIgnoreCase(type)) {
//				studyGroupsPage = studyGroupRepository.findByStatusOrderByCreatedAtDesc(ApprovalStatus.REJECTED,
//						pageable);
//			} else {
//				studyGroupsPage = studyGroupRepository.findAll(pageable);
//			}
//
//			// StudyGroup 엔티티 리스트를 StudyGroupDTO 리스트로 변환
//			List<StudyGroupDTO> approveDTOList = studyGroupsPage.getContent().stream()
//					.map(studyGroup -> StudyGroupDTO.builder().id(studyGroup.getId()).name(studyGroup.getName())
//							.createdAt(studyGroup.getCreatedAt()).status(studyGroup.getStatus()) // Enum 타입 전달
//							.build())
//					.collect(Collectors.toList());
//
//			// 응답 데이터 설정
//			returnMap.put("status", "success");
//			returnMap.put("data", approveDTOList);
//			returnMap.put("currentPage", studyGroupsPage.getNumber());
//			returnMap.put("totalPages", studyGroupsPage.getTotalPages());
//			returnMap.put("totalItems", studyGroupsPage.getTotalElements());
//		} catch (Exception e) {
//			// 예외 처리: 일반적인 예외 발생 시 에러 응답
//			returnMap.put("status", "error");
//			returnMap.put("message", "스터디 그룹 리스트를 가져오는 중 오류가 발생했습니다.");
//		}
//		return returnMap;
//	}

	// 승인 요청이 필요한 스터디 그룹 리스트
	// @Transactional
//	public Page<StudyGroupDTO> getApproveStudyGroupList(int page, String type) {
//		// 페이지 설정 (페이지 번호 및 크기 설정)
//		Pageable pageable = PageRequest.of(page, 10);
//
//		// type에 따라 승인 상태별로 스터디 그룹 리스트 조회
//		Page<StudyGroup> studyGroupsPage;
//		if ("pending".equalsIgnoreCase(type)) {
//			studyGroupsPage = studyGroupRepository.findByStatusOrderByCreatedAtDesc(ApprovalStatus.PENDING, pageable);
//		} else if ("approved".equalsIgnoreCase(type)) {
//			studyGroupsPage = studyGroupRepository.findByStatusOrderByCreatedAtDesc(ApprovalStatus.APPROVED, pageable);
//		} else if ("rejected".equalsIgnoreCase(type)) {
//			studyGroupsPage = studyGroupRepository.findByStatusOrderByCreatedAtDesc(ApprovalStatus.REJECTED, pageable);
//		} else {
//			studyGroupsPage = studyGroupRepository.findAll(pageable);
//		}
//
//		// StudyGroup 엔티티 리스트를 StudyGroupDTO 리스트로 변환하여 반환
//		return studyGroupsPage
//				.map(studyGroup -> StudyGroupDTO.builder().id(studyGroup.getId()).name(studyGroup.getName())
//						.createdAt(studyGroup.getCreatedAt()).status(studyGroup.getStatus()).build());
//	}

//	@Transactional
//	public Page<StudyGroupDTO> getApproveStudyGroupList(int page, String type, String searchQuery,
//			LocalDateTime startDate, LocalDateTime endDate) {
//		Pageable pageable = PageRequest.of(page, 10);
//
//		// type과 searchQuery, 날짜 범위에 따라 스터디 그룹 리스트 조회
//		Page<StudyGroup> studyGroupsPage;
//		if ("pending".equalsIgnoreCase(type)) {
//			studyGroupsPage = studyGroupRepository
//					.findByStatusAndNameContainingIgnoreCaseAndCreatedAtBetweenOrderByCreatedAtDesc(
//							ApprovalStatus.PENDING, searchQuery, startDate, endDate, pageable);
//		} else if ("approved".equalsIgnoreCase(type)) {
//			studyGroupsPage = studyGroupRepository
//					.findByStatusAndNameContainingIgnoreCaseAndCreatedAtBetweenOrderByCreatedAtDesc(
//							ApprovalStatus.APPROVED, searchQuery, startDate, endDate, pageable);
//		} else if ("rejected".equalsIgnoreCase(type)) {
//			studyGroupsPage = studyGroupRepository
//					.findByStatusAndNameContainingIgnoreCaseAndCreatedAtBetweenOrderByCreatedAtDesc(
//							ApprovalStatus.REJECTED, searchQuery, startDate, endDate, pageable);
//		} else {
//			studyGroupsPage = studyGroupRepository.findByNameContainingIgnoreCaseAndCreatedAtBetween(searchQuery,
//					startDate, endDate, pageable);
//		}
//
//		return studyGroupsPage
//				.map(studyGroup -> StudyGroupDTO.builder().id(studyGroup.getId()).name(studyGroup.getName())
//						.createdAt(studyGroup.getCreatedAt()).status(studyGroup.getStatus()).build());
//	}

	@Transactional
	public Page<StudyGroupDTO> getApproveStudyGroupList(int page, String type, String searchQuery,
			LocalDateTime startDate, LocalDateTime endDate) {
		Pageable pageable = PageRequest.of(page, 10);

		// type 값을 ApprovalStatus의 이름으로 변환하거나 null로 처리
		String status = "All".equalsIgnoreCase(type) ? null : type.toUpperCase();

		Page<StudyGroup> studyGroupsPage = studyGroupRepository.findByFilters(status, searchQuery, startDate, endDate,
				pageable);

		return studyGroupsPage
				.map(studyGroup -> StudyGroupDTO.builder().id(studyGroup.getId()).name(studyGroup.getName())
						.createdAt(studyGroup.getCreatedAt()).status(studyGroup.getStatus()).build());
	}

}
