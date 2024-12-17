package com.study.mms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration // Spring 컨테이너가 이 클래스를 Bean으로 등록하고 설정 정보를 로드
@EnableWebSocketMessageBroker // Spring WebSocket 메시징 기능을 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {


		// 메세지 흐름 방향
		// 방향 1. 클라이언트 → 서버: 클라이언트가 메시지를 서버로 전송.
		// 방향 2. 서버 → 브로커 → 클라이언트: 서버가 브로커를 통해 메시지를 클라이언트로 전송.
		
		// 서버 → 브로커 → 클라이언트 메시지의 경로를 설정
		/// topic은 메시지를 브로커를 통해 클라이언트로 전달하기 위한 경로의 접두사로 사용

		// 클라이언트가 /api/chat/로 시작하는 경로로 메시지를 보내면, 서버에서 해당 메시지를 처리
		// setApplicationDestinationPrefixes("/api/chat")에 의해
		// /api/chat/send-message는 @MessageMapping("/send-message")로 매핑됩니다.
		// 서버의 sendMessage 메서드가 호출됩니다.
		// 서버로 전송할 때 사용하는 경로의 공통 접두사를 의미 하는 부분
		config.setApplicationDestinationPrefixes("/api/chat");

		config.enableSimpleBroker("/topic");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws") // WebSocket 엔드포인트(연결 시작)
				.setAllowedOriginPatterns("*") // CORS 설정
				.withSockJS(); // SockJS 지원
	}

}

//메시지 브로커 =>서버와 클라이언트 사이에서 메시지를 중계하는 역할
//메시지 수신 => 서버가 메시지를 특정 경로로 전송하면, 메시지 브로커가 이를 받움
//구독 정보 =>  어떤 클라이언트가 어떤 경로를 구독하고 있는지에 대한 정보
