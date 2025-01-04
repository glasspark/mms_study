package com.study.mms.common;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.study.mms.model.User;
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
    //클라이언트에서 서버로 전달되는 요청(Request) 또는 서버에서
// 클라이언트로 전달되는 응답(Response)을 가로채어 특정 작업을 수행
    private final UserRepository userRepository;

    // preHandle 컨트롤러 실행 전
    // postHandle 컨트롤러 실행 후, 뷰 실행 전
    // afterCompletion 뷰 실행 후 (컨트롤러 실행과정에서 예외가 발생한 경우)

    private boolean shouldRedirect(PrincipalDetail principalDetail) {
        return (principalDetail.getNickname() != null && principalDetail.getNickname().contains("not_nickname")) ||
                (principalDetail.getEmail() != null && principalDetail.getEmail().contains("not_email"));
    }


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

        String uri = request.getRequestURI();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 세션에서 socialInput 값 가져오기
        HttpSession session = request.getSession(false); // 세션이 없으면 null 반환
        Boolean socialInput = (session != null) ? (Boolean) session.getAttribute("socialInput") : null;


        if (principal instanceof PrincipalDetail) {
            PrincipalDetail principalDetail = (PrincipalDetail) principal;

            if (socialInput != null && socialInput) {
                return true; // 세션 값이 true라면 요청 계속 처리
            }

            // 이미 /socialSignup 또는 /error로의 요청인 경우 처리 중단
            if (uri.equals("/social-signup") || uri.equals("/error") || uri.equals("/auth/**") || uri.equals("/")) {
                return true; // 추가 작업 없이 요청 진행
            }

            if (shouldRedirect(principalDetail)) {
                response.sendRedirect("/social-signup");
                return false; // 요청 중단
            }
        }

        return true;
    }

}
