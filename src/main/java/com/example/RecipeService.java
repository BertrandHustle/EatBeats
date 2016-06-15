package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
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

    public List<Recipe> getUserRecipes (HttpSession session) {
        //gets current user from session
        User user = userRepo.findFirstByUsername(session.getAttribute("username").toString());
        //gets all of user's recipes
        List<Recipe> recipes = recipeRepo.findByUser(user);

        return recipes;
    }
}
