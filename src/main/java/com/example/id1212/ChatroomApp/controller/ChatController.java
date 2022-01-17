package com.example.id1212.ChatroomApp.controller;

import com.example.id1212.ChatroomApp.chat.ChatMessage;
import com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUser;
import com.example.id1212.ChatroomApp.entities.bridges.DBFileUser.DBFileUserRepository;
import com.example.id1212.ChatroomApp.entities.file.DBFileRepository;
import com.example.id1212.ChatroomApp.entities.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser.ChatroomUserRepository;


import com.example.id1212.ChatroomApp.entities.chat.MessageRepository;
import com.example.id1212.ChatroomApp.entities.chat.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ChatController {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private DBFileUserRepository dbFileUserRepository;


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) throws ParseException {
        String messageContent = chatMessage.getContent();
        String date = chatMessage.getDate(); // date is correct
        Integer chatroomId = chatMessage.getChatroomId();
        Integer userId = chatMessage.getUserId(); // if username is not unique
        String fileId = chatMessage.getFileId();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = formatter.parse(date);

        Message message = new Message();
        message.setMessage(messageContent);
        message.setUserId(userId);
        message.setchatroomId(chatroomId);
        message.setDate(newDate);
        message.setFileId(fileId);
        messageRepository.save(message);

        Integer messageId = message.getId();
        chatMessage.setMessageId(messageId);

        if(fileId != null) {
            DBFileUser dbFileUser = new DBFileUser();
            dbFileUser.setFileId(fileId);
            dbFileUser.setUserId(userId);
            dbFileUserRepository.save(dbFileUser);
        }
        return chatMessage;
    }


    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.leaveUser")
    @SendTo("/topic/public")
    public ChatMessage removeUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().remove("username", chatMessage.getSender());
        return chatMessage;
    }

}
