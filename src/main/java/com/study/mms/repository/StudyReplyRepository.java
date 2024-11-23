package com.study.mms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.StudyReply;

public interface StudyReplyRepository extends JpaRepository<StudyReply, Integer> {

	List<StudyReply> findByStudyCommentId(Integer id);
	
	
}
