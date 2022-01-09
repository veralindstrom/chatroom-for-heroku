package com.example.springboottutorial.id1212.entities.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface UserRepository extends CrudRepository<User, String> {
    User findUserByEmailAndPassword(String email,String password);
    User findUserByUserId(Integer id);

    @Query("SELECT u.username FROM User u WHERE u.userId = ?1")
    String getUsername(Integer userId);

    @Query("SELECT u.userId FROM User u WHERE u.username = ?1")
    Integer getUserIdByUsername(String username);

    @Query("SELECT u.userId FROM User u WHERE u.email = ?1")
    Integer getUserIdByEmail(String email);


}
