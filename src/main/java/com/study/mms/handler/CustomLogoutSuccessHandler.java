package com.study.mms.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		String refererUrl = request.getHeader("Referer");


		// admin 권한인 경우 로그아웃 시 관리자 로그인 페이지 이동
		// 그런데 이전에 머무른 페이지가 관리자 페이지가 아닌경우 일반 페이지 로그아웃 처리
		if (authentication != null) {
			for (GrantedAuthority authority : authentication.getAuthorities()) {
				if (authority.getAuthority().equals("ROLE_ADMIN")) {

					// 관리자 권한인데 경로에 admin이 포함되어 있는지 여부에 따라서 구분
					if (refererUrl != null && refererUrl.contains("/admin/")) {
						response.sendRedirect("/admin/login");
					} else {
						response.sendRedirect("/home");
					}
					return;
				}
			}
		}

		if (refererUrl != null) {
			// response.sendRedirect(refererUrl);
			response.sendRedirect("/"); // 로그아웃은 웰컴 페이지로 이동
		} else {
			response.sendRedirect("/");
		}

	}

}
