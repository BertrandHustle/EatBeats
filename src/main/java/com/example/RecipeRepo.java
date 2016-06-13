package com.example;

import org.springframework.data.repository.CrudRepository;

/**
 * Repo for recipe class
 */

public interface RecipeRepo extends CrudRepository <Recipe, Integer>{
    Recipe findFirstByName(String name);
}
