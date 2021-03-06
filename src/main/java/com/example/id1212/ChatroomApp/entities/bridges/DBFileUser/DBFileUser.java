package com.example.id1212.ChatroomApp.entities.bridges.DBFileUser;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="user_file")
@IdClass(DBFileUserId.class)
public class DBFileUser implements Serializable {
    @Id
    private Integer userId;
    @Id
    private String fileId;

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}

