package com.example.id1212.ChatroomApp.entities.bridges.ChatroomUser;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;

public interface ChatroomUserRepository extends CrudRepository<ChatroomUser, Integer> {
    ArrayList<ChatroomUser> findChatroomUsersByUserId(Integer userId);
    ChatroomUser findChatroomUserByUserIdAndChatroomId(Integer userId, Integer chatroomId);

    @Query("SELECT c.userId FROM ChatroomUser c WHERE c.chatroomId = ?1")
    ArrayList<Integer> getAllUserIdsByChatroomId(Integer chatroomId);

    @Query("SELECT c.userId FROM ChatroomUser c WHERE c.chatroomId = ?1 order by c.roleId DESC")
    ArrayList<Integer> getAllUserIdsByChatroomIdDescRoleIdOrder(Integer chatroomId);

    @Query("SELECT c.roleId FROM ChatroomUser c WHERE c.userId = ?1 AND c.chatroomId = ?2")
    Integer getRoleIdByUserIdChatroomId(Integer userId, Integer chatroomId);

    @Query("SELECT c.admin FROM ChatroomUser c WHERE c.userId = ?1 AND c.chatroomId = ?2")
    Integer getAdminStatusByUserIdChatroomId(Integer userId, Integer chatroomId);

    @Query("SELECT c.favorite FROM ChatroomUser c WHERE c.userId = ?1 AND c.chatroomId = ?2")
    Integer getFavoriteStatusByUserIdChatroomId(Integer userId, Integer chatroomId);

    @Query("SELECT c.chatroomId FROM ChatroomUser c WHERE c.favorite = 1 AND c.userId = ?1")
    ArrayList<Integer> getAllChatroomIdsForFavoriteChatroomByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatroomUser c SET c.roleId = ?1 WHERE c.chatroomId = ?2 AND c.userId = ?3")
    void updateChatroomUserWithRoleId(Integer roleId, Integer chatroomId, Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatroomUser c SET c.favorite = ?1 WHERE c.chatroomId = ?2 AND c.userId = ?3")
    void updateChatroomUserWithFavoriteStatus (Integer favorite, Integer chatroomId, Integer userId);
}