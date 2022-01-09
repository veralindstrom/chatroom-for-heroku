package com.example.springboottutorial.id1212.DTO;

import com.example.springboottutorial.id1212.entities.chat.Message;

public class UserRoleDTO {
    private String username;
    private String role;
    //private Boolean admin;
    //private UserRoleDTO admin;


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

   /* public UserRoleDTO getAdminStatus() {
        return admin;
    }

    public void setAdminStatus(UserRoleDTO admin) {
        this.admin = admin;
    }*/
}