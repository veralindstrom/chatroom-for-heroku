package com.example.id1212.ChatroomApp.DTO;

import com.example.id1212.ChatroomApp.entities.chat.Message;

public class MessageUserDTO {
    private Message message;
    private String username;


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
