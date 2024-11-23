package com.study.mms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.UploadedFile;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> {

	// 특정 스터디 그룹의 파일 목록을 조회하는 메서드
	List<UploadedFile> findByStudyGroupIdOrderByCreatedAtDesc(Integer studyGroupId);
}
