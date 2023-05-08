package com.bebracore.chat.dto;

import java.io.InputStream;

import jakarta.validation.constraints.NotBlank;

public class MessageRequest {
	private String text;
	private InputStream voice;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public InputStream getVoice() {
		return voice;
	}

	public void setVoice(InputStream voice) {
		this.voice = voice;
	}
	
	

}
