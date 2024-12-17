package com.study.mms.handler;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.service.LoginLogService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	// 로그인 성공 핸들러

	private final LoginLogService loginLogService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// 사용자의 권한(roles) 가져오기
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		// 사용자 정보 가져오기
		Object principal = authentication.getPrincipal();

		// Custom UserDetails로 캐스팅
		if(principal instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) principal;
			// 여기서 사용자 이름이나 ID 가져오기
			String username = userDetails.getUsername(); // 기본적으로 username을 가져옴
			if (userDetails instanceof PrincipalDetail) { // PrincipalDetail은 커스텀 UserDetails
				String userId = String.valueOf(((PrincipalDetail) userDetails).getUser().getId()); // userId 가져오기

				// ROLE_USER 권한이 있는 경우
				for (GrantedAuthority authority : authorities) {
					String role = authority.getAuthority();

					if ("ROLE_USER".equals(role)) {
						loginLogService.addVisitor(userId); //방문자 수
						loginLogService.addCount(userId); //방문 횟수
					}
				}
			}
		}

		// 로그인 성공 시 Swagger UI 페이지로 리다이렉트
		//response.sendRedirect("/swagger-ui.html");
		response.sendRedirect("/home");
	}

}
