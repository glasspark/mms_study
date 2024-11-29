package com.study.mms.repository;

import java.util.List;
import java.util.Optional;

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
	
//	Optional<Users> findByUsername(String username);

//	Optional<Users> findByEmail(String username);
}
