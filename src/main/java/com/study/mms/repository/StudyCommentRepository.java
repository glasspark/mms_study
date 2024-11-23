package com.study.mms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.StudyComment;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Integer> {

	List<StudyComment> findByStudyGroupId(Integer studyGroupId);

	List<StudyComment> findByStudyGroupIdOrderByCreatedAtDescIdAsc(Integer studyGroupId);

}
