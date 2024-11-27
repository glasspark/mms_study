package com.study.mms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.mms.model.StudyGroup;
import com.study.mms.model.StudyGroup.ApprovalStatus;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Integer> {
	Page<StudyGroup> findByStatusOrderByCreatedAtDesc(ApprovalStatus status, Pageable pageable);

	// 이름으로 스터디 그룹을 찾음
	Optional<StudyGroup> findByName(String name);

	boolean existsByName(String name);

	// 관리자 페이지 검색 시 사용
	@Query(value = """
			SELECT * FROM study_group sg
			WHERE (:status IS NULL OR sg.status = :status)
			  AND (:name IS NULL OR LOWER(sg.name) LIKE LOWER(CONCAT('%', :name, '%')))
			  AND (:startDate IS NULL OR sg.created_at >= :startDate)
			  AND (:endDate IS NULL OR sg.created_at <= :endDate)
			ORDER BY sg.created_at DESC
			""", nativeQuery = true)
	Page<StudyGroup> findByFilters(@Param("status") String status, @Param("name") String name,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

	// 회원 스터디 그룹 조회 시 사용
	@Query(value = "SELECT * FROM study_group sg WHERE sg.status = 'APPROVED' AND (:name IS NULL OR sg.name LIKE %:name%) AND (:status = 'all' OR sg.recruitment_status = :status) AND (:tag IS NULL OR :tag IS NULL OR sg.study_tag LIKE %:tag%) ORDER BY sg.approval_decision_at DESC", nativeQuery = true)
	Page<StudyGroup> searchStudyGroups(@Param("name") String name, @Param("status") String status,
			@Param("tag") String tag, Pageable pageable);

	// 승인된 상태이며 사용자가 가입된 스터디 그룹만 가져오는 메서드
//	List<StudyGroup> findByStatusAndMembersUserId(ApprovalStatus status, Integer userId);

	// 네이티브 쿼리로 승인된 상태이며 사용자가 가입된 스터디 그룹만 가져오는 메서드
	@Query(value = "SELECT sg.* FROM study_group sg " + "JOIN study_group_member sgm ON sg.id = sgm.study_group_id "
			+ "WHERE sg.status = 'APPROVED' AND sgm.user_id = :userId", nativeQuery = true)
	List<StudyGroup> findApprovedStudyGroupsByUserId(@Param("userId") Integer userId);

	// 스터디 그룹에 유저가 있는지 확인하기 위함
	@Query(value = "SELECT COUNT(*) FROM study_group_member sgm "
			+ "WHERE sgm.study_group_id = :groupId AND sgm.user_id = :userId", nativeQuery = true)
	int isUserMemberOfGroup(@Param("userId") Integer userId, @Param("groupId") Integer groupId);

	// 스터디 그룹 방장인지 확인
	@Query("SELECT CASE WHEN COUNT(sg) > 0 THEN true ELSE false END " + "FROM StudyGroup sg "
			+ "WHERE sg.id = :groupId AND sg.leader.id = :userId")
	boolean isUserLeader(@Param("groupId") Integer groupId, @Param("userId") Integer userId);

}
