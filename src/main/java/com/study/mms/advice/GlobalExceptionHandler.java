package com.study.mms.advice;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.study.mms.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND) // 404 상태 코드 설정
	public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
		model.addAttribute("errorMessage", ex.getMessage());
		return "/error/404"; // error 폴더 내 404.html 템플릿을 반환
	}

	
//	@ExceptionHandler(IllegalArgumentException.class)
//	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 상태 코드 설정
//	public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
//		model.addAttribute("errorMessage", ex.getMessage());
//		return "/error/400"; // error 폴더 내 400.html 템플릿을 반환
//	}

}
