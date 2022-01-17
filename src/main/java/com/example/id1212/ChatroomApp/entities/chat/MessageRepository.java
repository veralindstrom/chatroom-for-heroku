package com.example.id1212.ChatroomApp.entities.chat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface MessageRepository extends CrudRepository<Message, Integer> {
    Message findMessageByMessageId(Integer messageId);
    ArrayList<Message> findMessagesByChatroomIdAndFileIdIsNotNull(Integer chatroomId);


    @Query("select m from Message m where m.chatroomId = ?1 order by m.date ASC")
    ArrayList<Message> getAllMessagesInChatroom(Integer chatroomId);

    @Query("select m.userId from Message m where m.messageId = ?1")
    Integer getUserIdByMessageId(Integer messageId);

    @Query("SELECT m.messageId FROM Message m WHERE m.chatroomId = ?1")
    ArrayList<Integer> getAllMessageIdsByChatroomId(Integer chatroomId);
}
