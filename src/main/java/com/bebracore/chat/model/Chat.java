package com.bebracore.chat.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bebracore.cabinet.model.User;

@Document
public class Chat {
	@Id
	private String id;
	private List<String> userIds;
	private List<String> messageIds;
	private String productId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void addUserId(String userId) {
		if (userIds == null) {
			userIds = new ArrayList<>();
		}
		userIds.add(userId);
	}

	public List<String> getMessageIds() {
		return messageIds;
	}

	public void addMessageId(String messageId) {
		if (messageIds == null) {
			messageIds = new ArrayList<>();
		}
		messageIds.add(messageId);
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

}
