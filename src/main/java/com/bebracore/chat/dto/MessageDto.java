package com.bebracore.chat.dto;

import java.io.InputStream;
import java.util.Date;

import com.bebracore.cabinet.dto.UserDto;

public class MessageDto {
	private String id;
	private String text;
	private UserDto user;
	private Date createdOn;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

}
