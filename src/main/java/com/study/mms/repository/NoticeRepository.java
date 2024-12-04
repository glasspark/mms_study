package com.study.mms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.study.mms.model.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

	// 특정 순위 이상인 공지사항들의 priority를 +1씩 증가시키는 쿼리
	@Modifying
	@Query("UPDATE Notice n SET n.priority = n.priority + 1 WHERE n.priority >= :priority AND n.isPinned = true")
	void incrementPrioritiesFrom(Integer priority);

	// 특정 우선순위와 상단 고정 여부를 가진 공지사항이 존재하는지 여부를 확인하는 쿼리
	@Query("SELECT COUNT(n) > 0 FROM Notice n WHERE n.priority = :priority AND n.isPinned = true")
	boolean existsByPriorityAndIsPinned(Integer priority);

	// 삭제 시 상단 고정인 공지사항의 순위 이상인 공지사항들의 priority를 -1씩 감소시키는 쿼리
	@Modifying
	@Query("UPDATE Notice n SET n.priority = n.priority - 1 WHERE n.priority > :priority AND n.isPinned = true")
	void decrementPrioritiesAfter(Integer priority);
}
