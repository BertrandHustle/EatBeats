package com.example;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repo for recipe class
 * Finds recipe by name and all recipes by user
 */

public interface RecipeRepo extends CrudRepository <Recipe, Integer>{
    Recipe findFirstByName(String name);
    List<Recipe> findByUser (User user);

}
