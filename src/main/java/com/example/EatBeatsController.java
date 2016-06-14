package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

//todo: put copyright text here
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
    public String root(HttpSession session, Model model){

        //gets username from session (if available) and adds to model
        try {
            String username = session.getAttribute("username").toString();
            String password = session.getAttribute("password").toString();
            if (username != null){
                model.addAttribute("username", username);
                model.addAttribute("password", password);
            }
        } catch (NullPointerException npe) {
            System.out.println("no user found");
        }

        return "home";
    }

    //routes to create-recipe page
    @RequestMapping(path = "/create-recipe", method = RequestMethod.GET)
    public String getCreateRecipe(HttpSession session, Model model){
        return "create-recipe";
    }

    //creates recipe and stores in database
    @RequestMapping(path = "/create-recipe", method = RequestMethod.POST)
    public String postCreateRecipe(HttpSession session, String season, String name,
                                   String category, String region, String description){

        //retrieves current user
        //todo: make more explicit or encapsulate in User method
        User user = userRepo.findFirstByUsername(session.getAttribute("username").toString());

        //creates new recipe from user input, saves to db
        Recipe recipe = new Recipe(season, name, category, region, description);
        recipe.setUser(user);
        recipeRepo.save(recipe);

        return "redirect:/";
    }

    //login route
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, String username, String password, Model model, RedirectAttributes redirectAttributes) throws Exception{

        //init user
        User user = userRepo.findFirstByUsername(username);

        //if user not found, redirect to root
        //todo: put error message here
        if (user == null){
            return "redirect:/";

        //if user/pass incorrect, redirect to home page w/login failed message
        //todo: put login failed message in html
        } else if (!PasswordHasher.verifyPassword(password, user.getPassword())){
            redirectAttributes.addFlashAttribute("loginFailed", " ");
            model.addAttribute("loginFailed", "");
            return "redirect:/";

        //if user/pass correct, redirect to home page and add username to model
        } else if (PasswordHasher.verifyPassword(password, user.getPassword())){
            model.addAttribute("username", user.getUsername());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("password", user.getPassword());
            return "redirect:/";
        }

        return "";
    }

    //logout route
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    //route for creating account and saving to database
    @RequestMapping(path = "/create-account", method = RequestMethod.POST)
    public String postCreateAccount(HttpSession session, String username, String password) throws PasswordHasher.CannotPerformOperationException {

        User user = new User(username, password);

        userRepo.save(user);
        return "redirect:/";

    }

}
