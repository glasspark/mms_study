package com.study.mms.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.mms.model.Inquiry;
import com.study.mms.model.User;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {


	@Query("SELECT i FROM Inquiry i " + "WHERE (:startDate IS NULL OR i.createdAt >= :startDate) "
			+ "AND (:endDate IS NULL OR i.createdAt <= :endDate) "
			+ "AND (:category = 'all' OR i.category = :category) " + "AND (:status = 'all' OR i.status = :status) "
			+ "AND (:searchQuery IS NULL OR :searchQuery = '' OR "
			+ "     ((:searchType = 'title' AND i.title LIKE CONCAT('%', :searchQuery, '%')) OR "
			+ "      (:searchType = 'author' AND i.user.username LIKE CONCAT('%', :searchQuery, '%')))) "
			+ "ORDER BY i.createdAt DESC")
	Page<Inquiry> findInquiries(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			@Param("category") String category, @Param("status") String status, @Param("searchType") String searchType,
			@Param("searchQuery") String searchQuery, Pageable pageable);

	
	 Page<Inquiry> findByUser(User user, Pageable pageable);

}
