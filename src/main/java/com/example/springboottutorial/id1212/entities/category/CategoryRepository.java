package com.example.springboottutorial.id1212.entities.category;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
    Category findCategoryBycategoryId(Integer categoryId);
    Category findCategoryByCategory(String category);
}
