package com.example.id1212.ChatroomApp.entities.bridges.ChatroomCategory;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ChatroomCategoryRepository extends CrudRepository<ChatroomCategory, Integer> {
    ChatroomCategory findChatroomCategoryByCategoryIdAndChatroomId(Integer categoryId, Integer chatroomId);
    ArrayList<ChatroomCategory> findChatroomCategoriesByCategoryId(Integer categoryId);
    ArrayList<ChatroomCategory> findChatroomCategoriesByChatroomId(Integer chatroomId);

}