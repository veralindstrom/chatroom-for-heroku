package com.example.springboottutorial.id1212.entities.chat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Date;

public interface MessageRepository extends CrudRepository<Message, Integer> {
    Message findMessageByMessageId(Integer messageId);
    Message findMessageByUserId(Integer userId);
    Message findMessageByDate(Date date);
    Message findMessageByChatroomId(Integer chatroomId);
    ArrayList<Message> findMessagesByChatroomIdAndFileIdIsNotNull(Integer chatroomId);

  /*  Integer findMessageIdMessage

    @Query("select m.messageId from Message m where m.userId = ?1 and m.chatroomId = ?2 and m.date")
    Integer getMessageIdByUserIdChatroomId(Integer userId, Integer chatroomId);*/

    @Query("select m from Message m where m.chatroomId = ?1 order by m.date ASC")
    ArrayList<Message> getAllMessagesInChatroom(Integer chatroomId);

    @Query("select m.userId from Message m where m.messageId = ?1")
    Integer getUserIdByMessageId(Integer messageId);

    @Query("SELECT m.messageId FROM Message m WHERE m.chatroomId = ?1")
    ArrayList<Integer> getAllMessageIdsByChatroomId(Integer chatroomId);
}
