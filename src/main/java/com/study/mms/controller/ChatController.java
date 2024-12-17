package com.study.mms.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.mms.dto.ChatMessageDTO;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

	private final SimpMessagingTemplate messagingTemplate;

	public ChatController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@MessageMapping("/send-message") // 클라이언트에서 전송 경로
	public void sendMessage(ChatMessageDTO chatMessage) {
		System.out.println("요청이 왜 안오죠?");
		System.out.println(chatMessage.getSender());
		String destination = "/topic/room/" + chatMessage.getRoomId(); // 동적 경로 생성
		messagingTemplate.convertAndSend(destination, chatMessage); // 메시지 전송
	}

	@MessageMapping("/addUser") // 클라이언트에서 전송 경로
	public void addUser(ChatMessageDTO chatMessage) {
		String destination = "/topic/room/" + chatMessage.getRoomId(); // 동적 경로 생성
		chatMessage.setContent(chatMessage.getSender() + " 님이 참여하였습니다.");
		chatMessage.setType(ChatMessageDTO.MessageType.JOIN);
		messagingTemplate.convertAndSend(destination, chatMessage); // 메시지 전송
	}

}
