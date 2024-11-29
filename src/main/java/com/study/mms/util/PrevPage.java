package com.study.mms.util;

import javax.servlet.http.HttpServletRequest;

public class PrevPage {
	public static void setPrevPage(HttpServletRequest request) {
		//이전 방문 혹은 현재 페이지의 URL 정보를 세션에 저장(소셜 로그인에 사용)
		String referrer = request.getHeader("Referer");
		String requstURL = request.getRequestURL().toString();
		if (referrer != null) {
			if (!referrer.contains("/auth/login")) {
				request.getSession().setAttribute("prevPage", referrer);
				request.getSession().setAttribute("nowPage", requstURL);
			}
		}
	}
}
