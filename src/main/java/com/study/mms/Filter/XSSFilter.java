package com.study.mms.Filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class XSSFilter implements Filter {

    //Filter는 Servlet 스펙에서 제공하는 기능으로,
    // HTTP 요청과 응답을 가로채어(Intercept) 처리하거나 변경할 수 있도록 설계된 인터페이스
    //클라이언트 → [Filter 실행] → 컨트롤러(서블릿) → 응답 처리 → [Filter 실행] → 클라이언트
    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 필터 초기화 시 실행됨 (필요할 경우 사용)
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
        // 필터 종료 시 실행됨 (필요할 경우 사용)
        this.filterConfig = null;
    }

    @Override
    //요청(Request)과 응답(Response)을 가로채고 추가적인 처리를 할 수 있도록 함
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
    }
}
