package com.bebracore.chat.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest {
	@NotBlank
	private String userId;
	@NotBlank
	private String productId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

}
