package com.example.springboottutorial.id1212.entities.category;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
    Category findCategoryByCategoryId(Integer categoryId);
    Category findCategoryByCategory(String category);

    @Query("SELECT c.category FROM Category c")
    ArrayList<String> getAllCategoryByName();

    @Query("SELECT c.category FROM Category c WHERE c.categoryId = ?1")
    String getCategoryNameById(Integer categoryId);
}
