package com.example;

import com.example.Recipe;
import com.example.User;
import com.example.RecipeRepo;
import com.example.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for Recipe
 */

@Service
public class RecipeService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    RecipeRepo recipeRepo;

    public List<Recipe> getUserRecipes (String username) {
        //gets current user from session
        User user = userRepo.findFirstByUsername(username);
        //gets all of user's recipes
        List<Recipe> recipes = recipeRepo.findByUser(user);

        return recipes;
    }

    public void saveRecipe (User user, String season, String name, String category,
                            String region, String description){

        Recipe recipe = new Recipe(season, name, category, region, description);
        recipe.setUser(user);
        recipeRepo.save(recipe);

    }

}