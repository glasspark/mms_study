package com.study.mms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.model.Todo;
import com.study.mms.model.User;

public interface TodoRepository extends JpaRepository<Todo, Integer> {

	List<Todo> findByUser(User user);

}
