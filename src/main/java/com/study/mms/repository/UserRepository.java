package com.study.mms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	//exists 필드를 기준으로 있다면 true, 없다면 false 
	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	Optional<User> findByUsername(String username);

	List<User> findAllByEmail(String email);

	Optional<User> findById(Integer userId);
	
//	Optional<Users> findByUsername(String username);

//	Optional<Users> findByEmail(String username);
}
