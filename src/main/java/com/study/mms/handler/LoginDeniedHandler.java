package com.study.mms.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.util.UrlPathHelper;

public class LoginDeniedHandler implements AccessDeniedHandler {
//권한이 없는데 접근한 경우에 대한 처리

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		// TODO Auto-generated method stub
//
//		// 스프링 시큐리티 로그인때 만든 객체
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//		// 현재 접속 url를 확인
//		UrlPathHelper urlPathHelper = new UrlPathHelper();
//		String originalURL = urlPathHelper.getOriginatingRequestUri(request);
//
//		// 로그아웃 실행
////		new SecurityContextLogoutHandler().logout(request, response,
////				SecurityContextHolder.getContext().getAuthentication());
//
//		// 로직을 짜서 상황에 따라 보내줄 주소를 설정해주면 됨
//		if (originalURL.startsWith("/admin/")) {
//			response.sendRedirect("/admin/login");
//		} else {
//			response.sendRedirect("/login");
//		}
//	}

		
		//여기서 페이지 보내는 처리가 이상한 듯?
		// 권한이 없는 사용자가 보호된 URL에 접근할 경우 403 상태 코드 반환
		// response.sendError(HttpServletResponse.SC_FORBIDDEN, "권한이 없습니다.");
		response.sendRedirect(request.getContextPath() + "/error/403");
	}
}
