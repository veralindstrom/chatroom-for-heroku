package com.example.id1212.ChatroomApp.entities.bridges.DBFileUser;

import org.springframework.data.repository.CrudRepository;

public interface DBFileUserRepository extends CrudRepository<DBFileUser, Integer> {
    DBFileUser findFileUserByUserId(Integer userId);
    DBFileUser findFileUserByFileId(String fileId);
}