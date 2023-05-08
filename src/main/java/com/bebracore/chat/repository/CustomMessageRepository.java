package com.bebracore.chat.repository;

import java.util.List;

import com.bebracore.chat.model.Message;

public interface CustomMessageRepository {
	List<Message> findByChatId(String chatId, Integer skip, Integer limit);

	Message findLastMessage(String chatId);
}
