package com.study.mms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.BoardComment;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Integer> {

}
