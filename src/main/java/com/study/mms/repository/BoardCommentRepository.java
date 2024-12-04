package com.study.mms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.BoardComment;
import com.study.mms.model.StudyBoard;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Integer> {

	List<BoardComment> findAllByStudyBoard(StudyBoard studyBoard);

}
