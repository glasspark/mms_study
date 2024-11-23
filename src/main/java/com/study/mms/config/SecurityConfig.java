package com.study.mms.config;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.study.mms.auth.PrincipalDetailService;
import com.study.mms.handler.CustomAuthenticationSuccessHandler;
import com.study.mms.handler.CustomLogoutSuccessHandler;
import com.study.mms.handler.LoginDeniedHandler;
import com.study.mms.handler.LoginFailureHandler;

@EnableWebSecurity // 웹 보안기능 활성화 및 초기화
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	private CustomLogoutSuccessHandler customLogoutSuccessHandler;

	@Autowired
	private LoginFailureHandler loginFailureHandler;

	@Autowired
	private PrincipalDetailService principalDetailService;

	@Bean
	public BCryptPasswordEncoder encodePWD() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
		return jdbcTokenRepository;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.authorizeHttpRequests(auth -> auth
						.antMatchers("/api/**", "/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
								"/webjars/**")
						.permitAll() // 이 경로는 인증
						.anyRequest().authenticated())
				.formLogin(login -> login.loginPage("/").loginProcessingUrl("/loginProc")
						.defaultSuccessUrl("/home", false).usernameParameter("username").passwordParameter("password")
						.successHandler(authenticationSuccessHandler).failureHandler(loginFailureHandler).permitAll())
				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // 로그아웃 처리
						.logoutSuccessUrl("/") // 로그아웃 성공 시 이동할 URL
						.invalidateHttpSession(true) // 세션 무효화
						.deleteCookies("JSESSIONID", "remember-me")// 자동 로그인 쿠키 삭제
						.addLogoutHandler((request, response, authentication) -> {
							HttpSession session = request.getSession();
							session.invalidate();
						}).logoutSuccessHandler(customLogoutSuccessHandler).permitAll())
				.rememberMe().key("uniqueAndSecretKey").rememberMeParameter("remember-me")
				.tokenRepository(tokenRepository()).tokenValiditySeconds(15552000) // 쿠키 시간 반년으로 설정
				.userDetailsService(principalDetailService).and().exceptionHandling(
						exceptionHandling -> exceptionHandling.accessDeniedHandler(new LoginDeniedHandler())); // 요청 실패시
		return http.build();
	}

}
