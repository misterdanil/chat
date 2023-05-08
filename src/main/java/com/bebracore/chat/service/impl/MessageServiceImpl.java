package com.bebracore.chat.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bebracore.chat.model.Chat;
import com.bebracore.chat.model.Message;
import com.bebracore.chat.repository.MessageRepository;
import com.bebracore.chat.service.ChatService;
import com.bebracore.chat.service.MessageService;
import com.bebracore.chat.service.error.ChatNotFoundException;
import com.bebracore.chat.service.error.ForbiddenException;
import com.bebracore.chat.service.error.MessageNotFoundException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

@Service
public class MessageServiceImpl implements MessageService {
	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private ChatService chatService;
	@Autowired
	private GridFsTemplate gridFsTemplate;
	@Autowired
	private MongoDatabaseFactory mongoDatabaseFactory;
	private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	@Override
	public Message findById(String id) {
		Optional<Message> opt = messageRepository.findById(id);
		if (opt.isEmpty()) {
			return null;
		}
		return opt.get();
	}

	@Override
	public List<Message> findByChatId(String chatId, Integer skip, Integer limit) {
		return messageRepository.findByChatId(chatId, skip, limit);
	}

	@Override
	public Message save(MultipartFile voice, String text, String chatId) throws ChatNotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = (String) authentication.getPrincipal();

		Chat chat = chatService.findById(chatId);
		if (chat == null) {
			throw new ChatNotFoundException("Чата с таким id не существует");
		}

		Message message = new Message();
		message.setText(text);
		message.setChatId(chatId);
		message.setUserId(id);
		message.setCreatedOn(new Date());

		message = messageRepository.save(message);

		if (voice != null) {
			DBObject metaData = new BasicDBObject();
			metaData.put("message", message.getId());

			ObjectId voiceId;
			try {
				voiceId = gridFsTemplate.store(voice.getInputStream(), "voice" + ".mp3", voice.getContentType(),
						metaData);
			} catch (IOException e) {
				logger.error("Couldn't save voice", e);
				return null;
			}
			message.setVoiceId(voiceId.toString());
		}

		message = messageRepository.save(message);

		chat.addMessageId(message.getId());
		chatService.update(chat);

		return message;
	}

	@Override
	public InputStream getVoice(String messageId) {
		GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("metadata.message").is(messageId)));

		try {
			GridFsResource fsr = new GridFsResource(file, getGridFs().openDownloadStream(file.getObjectId()));
			return fsr.getInputStream();
		} catch (IOException e) {
			logger.error("Couldn't get input stream of gridfs file", e);
			return null;
		}

	}

	private GridFSBucket getGridFs() {

		MongoDatabase db = mongoDatabaseFactory.getMongoDatabase();
		return GridFSBuckets.create(db);
	}

	@Override
	public String addVoice(String messageId, InputStream inputStream)
			throws MessageNotFoundException, ForbiddenException {
		Optional<Message> opt = messageRepository.findById(messageId);
		if (opt.isEmpty()) {
			throw new MessageNotFoundException("Сообщения с таким id не существует: " + messageId);
		}

		Message message = opt.get();
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		String userId = (String) authentication.getPrincipal();
//
//		if (!userId.equals(message.getUserId())) {
//			throw new ForbiddenException("Это не ваше сообщение");
//		}

		DBObject metaData = new BasicDBObject();
		metaData.put("type", "voice");
		metaData.put("title", "message: " + messageId);
		String id = gridFsTemplate.store(inputStream, "voice_" + messageId + ".mp3", "audio/mp3", metaData).toString();
		System.out.println(id);
//		message.setVoiceId(id);
		messageRepository.save(message);

		return id;
	}

	@Override
	public Message findLastMessage(String chatId) {
		return messageRepository.findLastMessage(chatId);
	}

}
