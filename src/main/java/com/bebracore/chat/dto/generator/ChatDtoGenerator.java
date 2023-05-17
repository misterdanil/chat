package com.bebracore.chat.dto.generator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.bebracore.cabinet.dto.generator.UserDtoGenerator;
import com.bebracore.cabinet.model.User;
import com.bebracore.cabinet.service.UserService;
import com.bebracore.chat.dto.ChatDto;
import com.bebracore.chat.model.Chat;
import com.bebracore.chat.model.Message;
import com.bebracore.chat.service.MessageService;
import com.bebracore.productswatching.dto.generator.ProductDtoGenerator;
import com.bebracore.productswatching.model.Product;
import com.bebracore.productswatching.service.ProductService;

@Component
public class ChatDtoGenerator {
	@Autowired
	private MessageService messageService;
	@Autowired
	private UserService userService;
	@Autowired
	private MessageDtoGenerator messageDtoGenerator;
	@Autowired
	private ProductService productService;

	public List<ChatDto> createDtos(List<Chat> chats) {
		List<ChatDto> dtos = new ArrayList<>();

		chats.forEach(chat -> {
			ChatDto dto = createDto(chat);
			dtos.add(dto);
		});

		dtos.sort(new Comparator<ChatDto>() {

			@Override
			public int compare(ChatDto o1, ChatDto o2) {
				if(o1.getLastMessage() == null) {
					return -1;
				}
				else if(o2.getLastMessage() == null) {
					return 1;
				}
				return o2.getLastMessage().getCreatedOn().compareTo(o1.getLastMessage().getCreatedOn());
			}

		});

		return dtos;
	}

	public ChatDto createDto(Chat chat) {
		ChatDto dto = new ChatDto();

		String id = chat.getId();
		dto.setId(id);

		Message message = messageService.findLastMessage(id);
		if (message != null) {
			dto.setLastMessage(messageDtoGenerator.createMessageDto(message));
		}
		List<String> userIds = chat.getUserIds();
		String userId = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		for (int i = 0; i < userIds.size(); i++) {
			if (!userIds.get(i).equals((String) authentication.getName())) {
				userId = userIds.get(i);
			}
		}

		User user = userService.findById(userId);
		dto.setUser(UserDtoGenerator.createUserDto(user));

		Product product = productService.getProductById(chat.getProductId());
		dto.setProduct(ProductDtoGenerator.createDto(product));

		return dto;
	}
}
