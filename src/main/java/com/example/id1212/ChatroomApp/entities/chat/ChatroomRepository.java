package com.example.id1212.ChatroomApp.entities.chat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ChatroomRepository extends CrudRepository<Chatroom, String> {
    Chatroom findChatRoomByChatroomId(Integer chatroomId);

    @Query("select c from Chatroom c WHERE c.status = true")
    ArrayList<Chatroom>  getAllPublicId();

    @Query("select c.status from Chatroom c WHERE c.chatroomId = ?1")
    Boolean getStatusByChatroomId(Integer chatroomId);
}

