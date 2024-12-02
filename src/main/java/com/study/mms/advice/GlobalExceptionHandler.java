package com.study.mms.advice;

//INTERNAL_SERVER_ERROR는 https에도 존재하기 때문에 내가 지정한 예외를 사용하기 위해 경로 지정
import static com.study.mms.constants.ErrorCode.INTERNAL_SERVER_ERROR;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.study.mms.exception.CustomException;
import com.study.mms.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND) // 404 상태 코드 설정
	public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
		model.addAttribute("errorMessage", ex.getMessage());
		return "/error/404"; // error 폴더 내 404.html 템플릿을 반환
	}

	@ExceptionHandler({ CustomException.class })
	public ResponseEntity<Map<String, Object>> handleServerException(CustomException ex) {
		return handleErrorResponse(HttpStatus.valueOf(ex.getErrorCode().getStatus()), ex.getErrorCode().getStatus(),
				ex.getErrorCode().getMessage());
	}

	@ExceptionHandler({ Exception.class })
	protected ResponseEntity<Map<String, Object>> handleServerException(Exception ex) {
		return handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getStatus(),
				INTERNAL_SERVER_ERROR.getMessage());
	}

	//MissingServletRequestParameterException, MissingRequestHeaderException, MethodArgumentNotValidException, NoHandlerFoundException
	//생성 필요


	// Custom Error Response Builder
	private ResponseEntity<Map<String, Object>> handleErrorResponse(HttpStatus status, int errorCode, String message) {
		Map<String, Object> errorDetails = new HashMap<>();
		errorDetails.put("status", errorCode);
		errorDetails.put("message", message);
		errorDetails.put("timestamp", LocalDateTime.now().toString());
		// Logging removed as @Slf4j is not used
		return ResponseEntity.status(status).body(errorDetails);
	}
}
