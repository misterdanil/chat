package com.bebracore.chat.dto;

import com.bebracore.cabinet.dto.UserDto;
import com.bebracore.productswatching.dto.ProductDto;

public class ChatDto {
	private String id;
	private String title;
	private UserDto user;
	private MessageDto lastMessage;
	private ProductDto product;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public MessageDto getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(MessageDto lastMessage) {
		this.lastMessage = lastMessage;
	}

	public ProductDto getProduct() {
		return product;
	}

	public void setProduct(ProductDto product) {
		this.product = product;
	}

}
