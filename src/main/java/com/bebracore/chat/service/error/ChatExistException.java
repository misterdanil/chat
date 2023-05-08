package com.bebracore.chat.service.error;

import com.bebracore.chat.model.Chat;

public class ChatExistException extends Exception {
	private Chat chat;

	public ChatExistException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChatExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ChatExistException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ChatExistException(String message, Chat chat) {
		super(message);
		this.chat = chat;
		// TODO Auto-generated constructor stub
	}

	public ChatExistException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

}
