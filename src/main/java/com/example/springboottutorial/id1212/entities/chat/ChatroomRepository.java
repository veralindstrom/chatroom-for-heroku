package com.example.springboottutorial.id1212.entities.chat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ChatroomRepository extends CrudRepository<Chatroom, String> {
    Chatroom findChatRoomByChatroomId(Integer chatroomId);
    Chatroom findChatRoomByName(String name);

    @Query("select c.chatroomId from Chatroom c")
    ArrayList<Integer> getAllId();

    @Query("select c from Chatroom c WHERE c.status = true")
    ArrayList<Chatroom>  getAllPublicId();

    @Query("select c.status from Chatroom c WHERE c.chatroomId = ?1")
    Boolean getStatusByChatroomId(Integer chatroomId);
}

