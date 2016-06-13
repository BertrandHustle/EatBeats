package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Controller class for EatBeats
 */

@Controller
public class EatBeatsController {

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    PlaylistRepo playlistRepo;

    //root
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String root(HttpSession session, String username){

        if (username == null){
            return "create-account";
        } else {
            return "home";
        }
    }

    //route for creating account and saving to database
    @RequestMapping(path = "/create-account", method = RequestMethod.POST)
    public String postCreateAccount(HttpSession session, String username, String password) throws PasswordHasher.CannotPerformOperationException {

        User user = new User(username, password);

        userRepo.save(user);
        return "redirect/";

    }

}
