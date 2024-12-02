package com.study.mms.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

	// Custom Success Response Builder without data
	public static ResponseEntity<Map<String, Object>> buildSuccessResponse(HttpStatus status, String message) {
		Map<String, Object> successDetails = new HashMap<>();
		successDetails.put("status", status.value());
		successDetails.put("message", message);
		return ResponseEntity.status(status).body(successDetails);
	}

	// Custom Success Response Builder with data
	public static ResponseEntity<Map<String, Object>> buildSuccessResponseWithData(HttpStatus status, String message,
			Object data) {
		Map<String, Object> successDetails = new HashMap<>();
		successDetails.put("status", status.value());
		successDetails.put("message", message);
		successDetails.put("data", data);
		return ResponseEntity.status(status).body(successDetails);
	}

}
