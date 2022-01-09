package com.example.springboottutorial.id1212.entities.bridges.DBFileUser;

import com.example.springboottutorial.id1212.entities.bridges.DBFileUser.DBFileUser;
import org.springframework.data.repository.CrudRepository;

public interface DBFileUserRepository extends CrudRepository<DBFileUser, Integer> {
    DBFileUser findFileUserByUserId(Integer userId);
    DBFileUser findFileUserByFileId(String fileId);
}