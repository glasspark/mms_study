package com.study.mms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.mms.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    // exists 필드를 기준으로 있다면 true, 없다면 false
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByUsername(String username);

    List<User> findAllByEmail(String email);

    Optional<User> findById(Integer userId);

    @Query(value = "SELECT * FROM user WHERE sns = :sns AND snsId = :snsId", nativeQuery = true)
    Optional<User> findBySnsAndSnsId(@Param("sns") String sns, @Param("snsId") String snsId);


    @Query(nativeQuery = true, value = "SELECT id FROM user WHERE sns = 'KAKAO' ORDER BY id DESC LIMIT 1")
    Optional<String> getKakaoCount();

    @Query(nativeQuery = true, value = "SELECT id FROM user WHERE sns = 'NAVER' ORDER BY id DESC LIMIT 1")
    Optional<String> getNaverCount();

    // 닉네임 필터링 쿼리
    @Query(value = "SELECT * FROM user u " +
            "WHERE u.role != 'ROLE_ADMIN' " +
            "AND u.nickname LIKE %:nickname% " +  // 부분 일치 검색
            "ORDER BY u.id DESC",
            countQuery = "SELECT COUNT(*) FROM user u " +
                    "WHERE u.role != 'ROLE_ADMIN' " +
                    "AND u.nickname LIKE %:nickname%", // 부분 일치 검색
            nativeQuery = true)
    Page<User> findUsersByNickname(@Param("nickname") String nickname, Pageable pageable);

    // 이메일 필터링 쿼리
    @Query(value = "SELECT * FROM user u " +
            "WHERE u.role != 'ROLE_ADMIN' " +
            "AND u.email LIKE %:email% " +  // 부분 일치 검색
            "ORDER BY u.id DESC",
            countQuery = "SELECT COUNT(*) FROM user u " +
                    "WHERE u.role != 'ROLE_ADMIN' " +
                    "AND u.email LIKE %:email%", // 부분 일치 검색
            nativeQuery = true)
    Page<User> findUsersByEmail(@Param("email") String email, Pageable pageable);

    // 기본 쿼리: 조건이 없을 때 ROLE_ADMIN 제외한 모든 사용자 조회
    @Query(value = "SELECT * FROM user u " +
            "WHERE u.role != 'ROLE_ADMIN' " +
            "ORDER BY u.id DESC",
            countQuery = "SELECT COUNT(*) FROM user u " +
                    "WHERE u.role != 'ROLE_ADMIN'",
            nativeQuery = true)
    Page<User> findAllNonAdminUsers(Pageable pageable);

//	Optional<Users> findByUsername(String username);

//	Optional<Users> findByEmail(String username);
}
