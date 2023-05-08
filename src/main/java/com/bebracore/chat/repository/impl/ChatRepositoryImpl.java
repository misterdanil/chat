package com.bebracore.chat.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.bebracore.chat.model.Chat;
import com.bebracore.chat.repository.CustomChatRepository;

@Repository
public class ChatRepositoryImpl implements CustomChatRepository {
	@Autowired
	private MongoOperations mongoOperations;

	@Override
	public List<Chat> findByUserId(String userId, Integer skip, Integer limit) {
		Query query = new Query();

		query.addCriteria(Criteria.where("userIds").is(userId));

		if (skip != null) {
			query.skip(skip);
		}
		if (limit != null) {
			query.limit(limit);
		}

		return mongoOperations.find(query, Chat.class);
	}

}
