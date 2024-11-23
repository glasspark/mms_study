package com.study.mms.common;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {

	private final UserRepository userRepository;

	// preHandle 컨트롤러 실행 전
	// postHandle 컨트롤러 실행 후, 뷰 실행 전
	// afterCompletion 뷰 실행 후 (컨트롤러 실행과정에서 예외가 발생한 경우)

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException {
		// 인증 객체가 null인지 확인
		if (SecurityContextHolder.getContext().getAuthentication() == null
				|| !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			// 로그인하지 않은 경우 루트 페이지로 리다이렉션
			response.sendRedirect("/");
			return false; // 컨트롤러로 요청이 넘어가지 않음
		}

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal instanceof PrincipalDetail) {
				PrincipalDetail principalDetail = (PrincipalDetail) principal;
				String username = principalDetail.getUsername();
				userRepository.findByUsername(username).ifPresent(user -> request.setAttribute("USER", user));
			} else {
				String username = principal.toString();
				userRepository.findByUsername(username).ifPresent(user -> request.setAttribute("USER", user));
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		if (modelAndView != null && request.getAttribute("USER") != null) {
			Object user = request.getAttribute("USER");
			modelAndView.addObject("USER", request.getAttribute("USER"));

			// 사용자 등급 이미지 경로 추가
//	        if (user instanceof UserInfo) { // User 클래스의 인스턴스인지 확인
//	            String gradePath = userService.getGradeImage(((UserInfo) user).getGrade());
//	            modelAndView.addObject("GRADE_IMAGE_PATH", gradePath);
//	        }
		}
	}

}
