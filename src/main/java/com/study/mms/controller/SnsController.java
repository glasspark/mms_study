package com.study.mms.controller;

import com.study.mms.service.NaverService;
import com.study.mms.social.KakaoService;
import com.study.mms.util.PrevPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class SnsController {

    private final KakaoService kakaoService;
    private final NaverService naverService;

    // =============== 카카오 소셜 로그인 ===============
    //1. 로그인 화면 보여주기
    @RequestMapping("/auth/kakaoLoginPage") //@RequestParam 으로 step를 받음 (명시적 지정하지 않아서)
    public String AuthKakaoLoginPage(Model model, Integer step, HttpServletRequest request) {

        if (step == null) {
            request.getSession().setAttribute("step", 0);
        } else {
            request.getSession().setAttribute("step", step);
        }
        String view = kakaoService.kakagoLoginPage(request);

        return "redirect:" + view;
    }

    //로그인 화면 후 다음 (리다이렉트URL)
    @RequestMapping("/auth/kakao-login")
    public String AuthKakaoLogin(Model model, String code, HttpServletRequest request) throws Exception {
        return "redirect:" + kakaoService.kakaoOauth(code, request);
    }

    // =============== 네이버 소셜 로그인 ===============
    @RequestMapping("/auth/naverLoginPage") //@RequestParam 으로 step를 받음 (명시적 지정하지 않아서)
    public String AuthNaverLoginPage(Model model, Integer step, HttpServletRequest request) {

        if (step == null) {
            request.getSession().setAttribute("step", 0);
        } else {
            request.getSession().setAttribute("step", step);
        }
        String view = naverService.naverLoginPage(request);
        return "redirect:" + view;
    }

    //로그인 화면 후 다음 (리다이렉트URL)
    @RequestMapping("/auth/naver-login")
    public String AuthNaverLogin(Model model, String code, HttpServletRequest request) throws Exception {
        return "redirect:" + naverService.naverOauth(code, request);
    }


    //공통 페이지 리다이렉트
    //이용자 정보가 없을 때 닉네임, 이메일 설정
    @RequestMapping("/auth/login-term-of-use/{sns}")
    public String AuthLoginTermOfUse(Model model, Integer prePage, @PathVariable(value = "sns") String sns,
                                     HttpServletRequest request) throws Exception {
        PrevPage.setPrevPage(request);

        System.out.println("??");
        System.out.println("sns=" + sns);

        // 요청 처리
        if (sns.equals("naver")) {
            String view = naverService.naverLoginPage(request); // view 값을 type에 할당
            return "redirect:" + view;
        } else if (sns.equals("kakao")) {
            String view = kakaoService.kakagoLoginPage(request); // view 값을 type에 할당
            return "redirect:" + view;
        }

        // 데이터 모델에 추가
        model.addAttribute("location", "login");
        model.addAttribute("sns", sns); // type 값을 뷰로 전달
        return "/";
    }

    @RequestMapping("/auth/login-return/{sns}")
    public String AuthLoginReturn(Model model, HttpServletRequest request, @PathVariable(value = "sns") String sns) {
        System.out.println("리턴 값이 오나요?");
        model.addAttribute("location", "login");
        model.addAttribute("sns", sns); // type 값을 뷰로 전달
        return "/socialSignup";
    }
}
