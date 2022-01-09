package com.example.springboottutorial.id1212.entities.user;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "subscription")
public class Subscription implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer subId;
    private Integer userId;
    private Integer chatroomId;
    private Integer categoryId;
    private String mailMessage;

    public Integer getSubId() { return subId; }

    public void setsubId(Integer subId) {
        this.subId = subId;
    }

    public Integer getuserId() {
        return userId;
    }

    public void setuserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getchatroomId() {
        return chatroomId;
    }

    public void setchatroomId(Integer chatroomId) {
        this.chatroomId = chatroomId;
    }

    public Integer getcategoryId() {
        return categoryId;
    }

    public void setcategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getmailMessage() {
        return mailMessage;
    }

    public void setmailMessage(String mailMessage) {
        this.mailMessage = mailMessage;
    }
}
