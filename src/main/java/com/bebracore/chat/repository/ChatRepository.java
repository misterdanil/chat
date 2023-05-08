package com.bebracore.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bebracore.chat.model.Chat;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String>, CustomChatRepository {
	boolean existsByUserIdsAndProductId(List<String> userIds, String productId);
	
	Chat findByUserIdsAndProductId(List<String> userIds, String productId);
}
