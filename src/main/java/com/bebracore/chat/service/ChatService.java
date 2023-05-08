package com.bebracore.chat.service;

import java.util.List;

import com.bebracore.chat.controller.ProductNotFoundException;
import com.bebracore.chat.dto.ChatRequest;
import com.bebracore.chat.model.Chat;
import com.bebracore.chat.service.error.ChatExistException;
import com.bebracore.chat.service.error.UserNotFoundException;

public interface ChatService {

	Chat findById(String id);

	List<Chat> findByUserId(String userId, Integer skip, Integer limit);
	
	Chat findByUserIdsAndProductId(List<String> userIds, String productId);

	boolean existsById(String id);

	Chat save(ChatRequest chatRequest) throws ChatExistException, UserNotFoundException, ProductNotFoundException;

	void update(Chat chat);
}
