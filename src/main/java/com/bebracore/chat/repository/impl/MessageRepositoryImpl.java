package com.bebracore.chat.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.bebracore.chat.model.Message;
import com.bebracore.chat.repository.CustomMessageRepository;

@Repository
public class MessageRepositoryImpl implements CustomMessageRepository {
	@Autowired
	private MongoOperations mongoOperations;

	@Override
	public List<Message> findByChatId(String chatId, Integer skip, Integer limit) {
		Query query = new Query();

		query.addCriteria(Criteria.where("chatId").is(chatId));

		if (skip != null) {
			query.skip(skip);
		}
		if (limit != null) {
			query.limit(limit);
		}

		return mongoOperations.find(query, Message.class);
	}

	@Override
	public Message findLastMessage(String chatId) {
		Query query = new Query();

		query.addCriteria(Criteria.where("chatId").is(chatId)).with(Sort.by(Sort.Direction.DESC, "createdOn"));

		return mongoOperations.findOne(query, Message.class);
	}

}
