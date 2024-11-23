package com.study.mms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.StudyBoard;

public interface StudyBoardRepository extends JpaRepository<StudyBoard, Integer> {

}
