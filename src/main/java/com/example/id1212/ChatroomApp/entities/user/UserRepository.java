package com.example.id1212.ChatroomApp.entities.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, String> {
    User findUserByEmail(String email);
    User findUserByUserId(Integer id);

    @Query("SELECT u.username FROM User u WHERE u.userId = ?1")
    String getUsername(Integer userId);

    @Query("SELECT u.userId FROM User u WHERE u.email = ?1")
    Integer getUserIdByEmail(String email);


}
