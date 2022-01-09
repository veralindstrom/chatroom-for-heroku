package com.example.springboottutorial.id1212.entities.chat;

import javax.persistence.*;

@Entity
@Table(name = "chatroom")
public class Chatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer chatroomId;
    private String name;
    private int userCount;
    private boolean status;

    public Integer getId() {
        return chatroomId;
    }

    public void setId(Integer chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getUserCount(){
        return userCount;
    }

    public void addUserCount(int userCount){
        this.userCount += userCount;
    }

    public boolean getStatus(){
        return status;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

}
