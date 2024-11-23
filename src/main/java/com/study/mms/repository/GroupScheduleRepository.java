package com.study.mms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.mms.model.GroupSchedule;

public interface GroupScheduleRepository extends JpaRepository<GroupSchedule, Integer> {
	@Query(value = "SELECT * FROM group_schedule WHERE YEAR(start_date) = :year AND MONTH(start_date) = :month AND study_group_id = :groupId", nativeQuery = true)
	List<GroupSchedule> findByYearAndMonthNative(@Param("year") int year, @Param("month") int month,
			@Param("groupId") Integer groupId);

}
