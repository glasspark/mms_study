package com.study.mms.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.Attendance;
import com.study.mms.model.User;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

	Optional<Attendance> findByUserAndAttendance(User user, LocalDate now);

	List<Attendance> findByUser(User user);

}
