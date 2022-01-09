package com.example.springboottutorial.id1212.entities.bridges.ChatroomUser;

import com.example.springboottutorial.id1212.entities.bridges.ChatroomCategory.ChatroomCategoryId;

import javax.persistence.*;

@Entity
@Table(name="chatroom_user")
@IdClass(ChatroomUserId.class)
public class ChatroomUser {
    @Id
    private Integer chatroomId;
    @Id
    private Integer userId;
    @Id
    private Integer roleId;
    private Integer admin;
    private Integer favorite;

    public Integer getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(Integer chatroomId) {
        this.chatroomId = chatroomId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getAdmin(){
        return admin;
    }

    public void setAdmin(Integer admin){
        this.admin = admin;
    }

    public Integer getFavorite(){
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }
}
