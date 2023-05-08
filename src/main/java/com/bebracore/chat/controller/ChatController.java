package com.bebracore.chat.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.standard.Media;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bebracore.chat.dto.ChatDto;
import com.bebracore.chat.dto.ChatRequest;
import com.bebracore.chat.dto.MessageDto;
import com.bebracore.chat.dto.generator.ChatDtoGenerator;
import com.bebracore.chat.dto.generator.MessageDtoGenerator;
import com.bebracore.chat.model.Chat;
import com.bebracore.chat.model.Message;
import com.bebracore.chat.service.ChatService;
import com.bebracore.chat.service.MessageService;
import com.bebracore.chat.service.error.ChatExistException;
import com.bebracore.chat.service.error.ChatNotFoundException;
import com.bebracore.chat.service.error.ForbiddenException;
import com.bebracore.chat.service.error.MessageNotFoundException;
import com.bebracore.chat.service.error.UserNotFoundException;
import com.bebracore.webconfig.controller.AbstractController;
import com.bebracore.webconfig.dto.ValidatedResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class ChatController extends AbstractController {
	@Autowired
	private ChatService chatService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private MessageDtoGenerator messageDtoGenerator;
	@Autowired
	private ChatDtoGenerator chatDtoGenerator;
	private Logger logger = LoggerFactory.getLogger(ChatController.class);

	public static class MessageTest {
		private String from;
		private String text;

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

	@MessageMapping("/chat")
	public void send(@Payload MessageTest message) {
		Map<String, Object> mapa = new HashMap<>();
		mapa.put("Content-Type", "application/json");
		messagingTemplate.convertAndSend("/topic/641b74402b87622e0895fbdd/messages",
				"{ \"text\": \"hello, " + message.from + "\"}", mapa);
	}

	@PostMapping(value = "/chat", produces = { MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> createChat(@RequestBody @Valid ChatRequest chatRequest, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		}

		Chat chat;
		try {
			chat = chatService.save(chatRequest);
		} catch (ChatExistException e) {
			return ResponseEntity.status(303).header("Content-Type", MediaType.TEXT_PLAIN_VALUE)
					.body(e.getChat().getId());
		} catch (UserNotFoundException e) {
			result.rejectValue("userId", "chat.user.notFound", e.getMessage());
			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		} catch (ProductNotFoundException e) {
			result.rejectValue("productId", "chat.product.notFound", "Продукта такого не существует");
			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		}

		try {
			return ResponseEntity.created(new URI(HOST + "/chat/" + chat.getId()))
					.header("Content-Type", MediaType.TEXT_PLAIN_VALUE).body(chat.getId());
		} catch (URISyntaxException e) {
			logger.error("Couldn't make uri with host and chat id: " + HOST + " " + chat.getId(), e);
			return ResponseEntity.status(201).build();
		}
	}

	static class Ono {
		MultipartFile file;
		String text;
	}

	@PostMapping(value = "/chat/{id}/message", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ValidatedResponse> sendMessage(@RequestParam(required = false) MultipartFile voice,
			@RequestParam(required = false) String text, @PathVariable String id) {
		BindingResult result = new MapBindingResult(new HashMap<>(), "message");

		if ((text == null || text.isEmpty()) && voice == null) {
			result.reject("message.notNull", "Сообщение должно иметь либо текст, либо голос, либо и то и другое");

			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		}

		Message message;
		try {
			message = messageService.save(voice, text, id);
		} catch (ChatNotFoundException e) {
			result.reject("message.chat.notFound", e.getMessage());
			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		}

		messagingTemplate.convertAndSend("/topic/" + id + "/messages", messageDtoGenerator.createMessageDto(message));

		try {
			return ResponseEntity.created(new URI(HOST + "/chat/" + id + "/message/" + message.getId())).build();
		} catch (URISyntaxException e) {
			logger.error("Couldn't create URI for host and message id: " + HOST + " " + message.getId(), e);
			return ResponseEntity.status(201).build();
		}
	}

	@PostMapping(value = "/chat/{chatId}/message/{messageId}/voice", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<ValidatedResponse> createVoice(HttpServletRequest request, @PathVariable String chatId,
			@PathVariable String messageId) {
		BindingResult result = new MapBindingResult(new HashMap<>(), "message");

		InputStream inputStream;
		try {
			inputStream = request.getInputStream();
		} catch (IOException e) {
			result.rejectValue("voice", "voice.undefined", "Неизвестная ошибка источника");
			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		}

		if (inputStream == null) {
			result.rejectValue("voice", "voice.empty", "Звуковое сообщение не должно быть пустым");
			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		}

		try {
			messageService.addVoice(messageId, inputStream);
		} catch (MessageNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (ForbiddenException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/chat/*/message/{messageId}/voice", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Object> getVoice(@PathVariable String messageId) throws IOException {
		InputStream is = messageService.getVoice(messageId);

		return ResponseEntity.ok(new InputStreamResource(is));
	}

	@GetMapping("/chat/{id}/messages")
	public ResponseEntity<List<MessageDto>> getMessages(@PathVariable String id,
			@RequestParam(required = false) Integer skip, @RequestParam(required = false) Integer limit) {
		List<Message> messages = messageService.findByChatId(id, skip, limit);

		List<MessageDto> dtos = messageDtoGenerator.createMessageDtos(messages);

		return ResponseEntity.ok().body(dtos);
	}

	@GetMapping("/chats")
	public ResponseEntity<List<ChatDto>> getChats(@RequestParam(required = false) Integer skip,
			@RequestParam(required = false) Integer limit) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String userId = (String) authentication.getName();

		List<Chat> chats = chatService.findByUserId(userId, skip, limit);

		List<ChatDto> dtos = chatDtoGenerator.createDtos(chats);

		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/chats/{id}")
	public ResponseEntity<ChatDto> getChat(@PathVariable String id) {
		Chat chat = chatService.findById(id);
		if (chat == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(chatDtoGenerator.createDto(chat));
		}
	}
}
