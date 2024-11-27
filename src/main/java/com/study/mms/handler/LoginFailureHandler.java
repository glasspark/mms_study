package com.study.mms.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.bind.annotation.RestController;

import com.study.mms.service.UserService;

@RestController
public class LoginFailureHandler implements AuthenticationFailureHandler {

	//순환 참조로 레퍼지터리 참조해서 사용할 것(유저 서비스 이용 X)

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		System.out.println("실패인가요?");
		System.out.println(request);

		// 로그인 실패 이유 로깅
		System.out.println("로그인 실패: " + exception.getMessage());

		// 여기서 조회를 해서 비밀번호 혹은 아이디 틀린 것으로 찾음
//		String result = userService.LoginFailed(request.getParameter("username"), request.getParameter("password"));
//		request.setAttribute("loginFailed", result);
//		request.getRequestDispatcher("/auth/login?prePage=1").forward(request, response);
	}

//관련된 메서드
//	@Transactional(readOnly = true)
//	public String LoginFailed(String username, String Password) {
//		if (userinfoRepository.findByUsername(username).orElse(null) == null) {
//			return "username";
//		} else if (userinfoRepository.findByUsernameAndPassword(username, Password).orElse(null) == null) {
//			return "password";
//		}
//		return "username";
//	}

}
