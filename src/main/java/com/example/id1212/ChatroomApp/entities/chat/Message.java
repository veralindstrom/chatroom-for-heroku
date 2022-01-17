package com.example.id1212.ChatroomApp.entities.chat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer messageId;
    private String message;
    private Date date;
    private String fileId;
    private Integer userId;
    private Integer chatroomId;

    public Integer getId() {
        return messageId;
    }

    public void setId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public String getFileId(){
        return fileId;
    }

    public void setFileId(String fileId){
        this.fileId = fileId;
    }

    public Integer getUserId(){
        return userId;
    }

    public void setUserId(Integer userId){
        this.userId = userId;
    }

    public Integer getchatroomId(){
        return chatroomId;
    }

    public void setchatroomId(Integer chatroomId){
        this.chatroomId = chatroomId;
    }
}
