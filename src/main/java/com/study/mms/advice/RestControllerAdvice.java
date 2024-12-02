package com.study.mms.advice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

@Target(ElementType.TYPE) // 클래스, 인터페이스, 열거형 등 타입에 적용 가능 하도록
@Retention(RetentionPolicy.RUNTIME) // 런타임 동안에 애너테이션 유지
@ControllerAdvice // 글로벌 예외처리 애너테이션
@ResponseBody
public @interface RestControllerAdvice {

	
	
	
}
