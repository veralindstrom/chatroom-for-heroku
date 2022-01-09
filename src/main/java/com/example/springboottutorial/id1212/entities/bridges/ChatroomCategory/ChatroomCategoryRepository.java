package com.example.springboottutorial.id1212.entities.bridges.ChatroomCategory;

import com.example.springboottutorial.id1212.entities.bridges.ChatroomCategory.ChatroomCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ChatroomCategoryRepository extends CrudRepository<ChatroomCategory, Integer> {
    ChatroomCategory findChatroomCategoryByCategoryIdAndChatroomId(Integer categoryId, Integer chatroomId);
    ArrayList<ChatroomCategory> findChatroomCategoriesByCategoryId(Integer categoryId);
    ArrayList<ChatroomCategory> findChatroomCategoriesByChatroomId(Integer chatroomId);
}