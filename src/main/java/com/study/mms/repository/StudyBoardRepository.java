package com.study.mms.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.StudyBoard;

public interface StudyBoardRepository extends JpaRepository<StudyBoard, Integer> {
	// studyGroupId로 StudyBoard 페이징 조회
	Page<StudyBoard> findByStudyGroupId(Integer studyGroupId, Pageable pageable);

	// studyGroupId와 게시글 ID로 특정 StudyBoard 조회
	Optional<StudyBoard> findByStudyGroupIdAndId(Integer studyGroupId, Integer id);

	// 게시글 ID, 사용자 ID, 그룹 ID를 통해 사용자가 해당 그룹의 게시글 작성자인지 확인하는 메서드
	Optional<StudyBoard> findByIdAndUserIdAndStudyGroupId(Integer boardId, Integer userId, Integer groupId);

}
