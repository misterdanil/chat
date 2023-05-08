package com.bebracore.chat.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bebracore.cabinet.service.UserService;
import com.bebracore.chat.controller.ProductNotFoundException;
import com.bebracore.chat.dto.ChatRequest;
import com.bebracore.chat.model.Chat;
import com.bebracore.chat.repository.ChatRepository;
import com.bebracore.chat.service.ChatService;
import com.bebracore.chat.service.error.ChatExistException;
import com.bebracore.chat.service.error.UserNotFoundException;
import com.bebracore.productswatching.service.ProductService;

@Service
public class ChatServiceImpl implements ChatService {
	@Autowired
	private ChatRepository chatRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private ProductService productService;

	@Override
	public Chat findById(String id) {
		Optional<Chat> chat = chatRepository.findById(id);
		if (chat.isEmpty()) {
			return null;
		}
		return chat.get();
	}

	@Override
	public List<Chat> findByUserId(String userId, Integer skip, Integer limit) {
		return chatRepository.findByUserId(userId, skip, limit);
	}

	@Override
	public Chat findByUserIdsAndProductId(List<String> userIds, String productId) {
		return chatRepository.findByUserIdsAndProductId(userIds, productId);
	}

	@Override
	public boolean existsById(String id) {
		return chatRepository.existsById(id);
	}

	@Override
	public Chat save(ChatRequest chatRequest)
			throws ChatExistException, UserNotFoundException, ProductNotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String id = (String) authentication.getPrincipal();

		String userId = chatRequest.getUserId();
		Chat chat = findByUserIdsAndProductId(Arrays.asList(id, userId), chatRequest.getProductId());
		if (chat != null) {
			throw new ChatExistException("Чат с такими пользователями уже существует", chat);
		}
		if (!userService.existsById(userId)) {
			throw new UserNotFoundException("Пользователь с таким id не найден");
		}
		if (!productService.existsById(chatRequest.getProductId())) {
			throw new ProductNotFoundException("Exception occurred while saving chat. Product with id: "
					+ chatRequest.getProductId() + " doesn't exist");
		}

		chat = new Chat();
		chat.addUserId(id);
		chat.addUserId(userId);
		chat.setProductId(chatRequest.getProductId());

		return chatRepository.save(chat);
	}

	@Override
	public void update(Chat chat) {
		chatRepository.save(chat);
	}

}
