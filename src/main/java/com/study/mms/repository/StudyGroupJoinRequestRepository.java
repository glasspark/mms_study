package com.study.mms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.StudyGroup;
import com.study.mms.model.StudyGroupJoinRequest;
import com.study.mms.model.StudyGroupJoinRequest.RequestStatus;
import com.study.mms.model.User;

public interface StudyGroupJoinRequestRepository extends JpaRepository<StudyGroupJoinRequest, Integer> {

	boolean existsByStudyGroupAndUserAndStatus(StudyGroup studyGroup, User user, RequestStatus pending);

	List<StudyGroupJoinRequest> findByStudyGroupAndStatus(StudyGroup studyGroup, RequestStatus pending);

	List<StudyGroupJoinRequest> findByUser(User user);

	// groupId와 userId를 이용하여 특정 StudyGroupJoinRequest 조회
	Optional<StudyGroupJoinRequest> findByIdAndUserId(Integer requestId, Integer userId);

}
