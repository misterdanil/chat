package com.bebracore.chat.dto.generator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bebracore.cabinet.dto.generator.UserDtoGenerator;
import com.bebracore.cabinet.service.UserService;
import com.bebracore.chat.dto.MessageDto;
import com.bebracore.chat.model.Message;

@Component
public class MessageDtoGenerator {
	@Autowired
	private UserService userService;

	public MessageDto createMessageDto(Message message) {
		MessageDto dto = new MessageDto();

		dto.setId(message.getId());
		dto.setText(message.getText());
		dto.setUser(UserDtoGenerator.createUserDto(userService.findById(message.getUserId())));
		dto.setCreatedOn(message.getCreatedOn());

		return dto;
	}
	
	public List<MessageDto> createMessageDtos(List<Message> messages) {
		List<MessageDto> dtos = new ArrayList<>();
		messages.forEach(message -> {
			dtos.add(createMessageDto(message));
		});
		
		return dtos;
	}
}
