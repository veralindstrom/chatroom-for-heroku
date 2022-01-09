package com.example.springboottutorial.id1212.DTO;

import com.example.springboottutorial.id1212.entities.chat.Message;

import java.util.ArrayList;

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
