package com.bebracore.chat.repository;

import java.util.List;

import com.bebracore.chat.model.Chat;

public interface CustomChatRepository {
	List<Chat> findByUserId(String userId, Integer skip, Integer limit);
}
