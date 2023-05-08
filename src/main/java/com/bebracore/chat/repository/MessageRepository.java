package com.bebracore.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bebracore.chat.model.Message;

public interface MessageRepository extends MongoRepository<Message, String>, CustomMessageRepository {
	List<Message> findByChatId(String chatId);
}
