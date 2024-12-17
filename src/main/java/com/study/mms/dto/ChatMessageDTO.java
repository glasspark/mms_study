package com.study.mms.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
	private String roomId; // 채팅방 ID
	private String sender;
	private String content;
	private MessageType type;

	public enum MessageType {
		CHAT, JOIN, LEAVE
	}
}
