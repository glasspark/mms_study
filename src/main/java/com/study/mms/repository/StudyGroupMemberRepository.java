package com.study.mms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.mms.model.StudyGroup;
import com.study.mms.model.StudyGroupMember;
import com.study.mms.model.User;

public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Integer> {

	// 해당하는 스터디 그룹에 이용자를 조회
	boolean existsByStudyGroupAndUser(StudyGroup studyGroup, User user);

	List<StudyGroupMember> findByUser(User user);

	// 스터디 그룹에서 내 권한 확인
	Optional<StudyGroupMember> findByStudyGroupIdAndUser(Integer studyGroupId, User user);

	// 특정 userId와 studyGroupId에 해당하는 StudyGroupMember 조회
	@Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.user.id = :userId AND sgm.studyGroup.id = :studyGroupId")
	Optional<StudyGroupMember> findByUserIdAndStudyGroupId(@Param("userId") Integer userId,
			@Param("studyGroupId") Integer studyGroupId);

	List<StudyGroupMember> findByStudyGroupId(Integer groupId);

}
