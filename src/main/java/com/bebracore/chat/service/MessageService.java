package com.bebracore.chat.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bebracore.chat.model.Message;
import com.bebracore.chat.service.error.ChatNotFoundException;
import com.bebracore.chat.service.error.ForbiddenException;
import com.bebracore.chat.service.error.MessageNotFoundException;

public interface MessageService {
	Message findById(String id);

	List<Message> findByChatId(String chatId, Integer skip, Integer limit);

	Message save(MultipartFile voice, String text, String chatId) throws ChatNotFoundException;

	String addVoice(String messageId, InputStream inputStream) throws MessageNotFoundException, ForbiddenException;

	InputStream getVoice(String messageId);

	Message findLastMessage(String chatId);
}
