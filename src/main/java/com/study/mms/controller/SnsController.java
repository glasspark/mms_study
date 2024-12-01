package com.study.mms.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.study.mms.service.UserService;
import com.study.mms.social.KakaoService;
import com.study.mms.util.PrevPage;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SnsController {

	private final KakaoService kakaoService;
	private final UserService userService;

	@RequestMapping("/auth/kakaoLoginPage")
	public String AuthKakaoLoginPage(Model model, Integer step, HttpServletRequest request) {
		if (step == null) {
			request.getSession().setAttribute("step", 0);
		} else {
			request.getSession().setAttribute("step", step);
		}
		String view = kakaoService.kakagoLoginPage(request);

		return "redirect:" + view;
	}

	@RequestMapping("/auth/kakao-login")
	public String AuthKakaoLogin(Model model, String code, HttpServletRequest request) throws Exception {
		return "redirect:" + kakaoService.kakaoOauth(code, request);
	}

	// 조회된 데이터가 없으면
	@RequestMapping("/auth/login-term-of-use/{sns}")
	public String AuthLoginTermOfUse(Model model, Integer prePage, @PathVariable(value = "sns") String sns,
			HttpServletRequest request) throws Exception {
		PrevPage.setPrevPage(request);
		model.addAttribute("location", "login");
		model.addAttribute("sns", sns);
		return "/socialSignup";
	}

}
