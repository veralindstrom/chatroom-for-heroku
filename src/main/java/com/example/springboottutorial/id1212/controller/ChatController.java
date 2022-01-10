package com.example.springboottutorial.id1212.controller;

import com.example.springboottutorial.id1212.chat.ChatMessage;
import com.example.springboottutorial.id1212.entities.bridges.DBFileUser.DBFileUser;
import com.example.springboottutorial.id1212.entities.bridges.DBFileUser.DBFileUserRepository;
import com.example.springboottutorial.id1212.entities.file.DBFileRepository;
import com.example.springboottutorial.id1212.entities.user.User;
import com.example.springboottutorial.id1212.entities.user.UserRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.example.springboottutorial.id1212.entities.bridges.ChatroomUser.ChatroomUserRepository;
import com.example.springboottutorial.id1212.entities.bridges.ChatroomUser.ChatroomUser;

import com.example.springboottutorial.id1212.entities.chat.MessageRepository;
import com.example.springboottutorial.id1212.entities.chat.Message;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ChatController {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatroomUserRepository chatroomUserRepository;
    @Autowired
    private DBFileRepository dbFileRepository;
    @Autowired
    private DBFileUserRepository dbFileUserRepository;
    @Autowired
    private UserRepository userRepository;

  /*  public ChatController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.chatroomUserRepository = chatroomUserRepository;
    }*/

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) throws ParseException {
        String messageContent = chatMessage.getContent();
        String date = chatMessage.getDate(); // date is correct
        Integer chatroomId = chatMessage.getChatroomId();
        Integer userId = chatMessage.getUserId(); // if username is not unique
        String fileId = chatMessage.getFileId();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date newDate = formatter.parse(date);

        Message message = new Message();
        message.setMessage(messageContent);
        message.setUserId(userId);
        message.setchatroomId(chatroomId);
        message.setDate(newDate);
        message.setFileId(fileId);
        messageRepository.save(message);

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

   /* @MessageMapping("/chat.favorite")
    @SendTo("/topic/favorite")
    public ChatMessage favoriteChatroom(@Payload ChatMessage chatMessage) {
        Integer chatroomId = chatMessage.getChatroomId();
        Integer userId = chatMessage.getUserId();
        String content = chatMessage.getContent();

        if(content.equals("Favorite")) {
            Integer trueStatus = 1;
            chatroomUserRepository.updateChatroomUserWithFavoriteStatus(trueStatus, chatroomId, userId);
        }
        if(content.equals("RemoveFavorite")) {
            Integer falseStatus = 0;
            chatroomUserRepository.updateChatroomUserWithFavoriteStatus(falseStatus, chatroomId, userId);
        }
        else {
            Integer currentStatus = chatroomUserRepository.getFavoriteStatusByUserIdChatroomId(userId, chatroomId);
            if(currentStatus.equals(1)) {
                chatMessage.setContent("Favorite");
            }
            if (currentStatus.equals(0)) {
                chatMessage.setContent("RemoveFavorite");
            }

        }

        return chatMessage;
    }*/

}
