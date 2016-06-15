package com.example;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.TrackRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.wrapper.spotify.methods.ArtistRequest;


import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

//todo: put copyright text here
//todo: make generate-playlist route
//todo: make single recipe page (edit page)

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

    @Autowired
    RecipeService recipeService;

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

        //retrieves recipe details from select/text forms in html

        //creates new recipe from user input, saves to db
        Recipe recipe = new Recipe(season, name, category, region, description);
        recipe.setUser(user);
        recipeRepo.save(recipe);

        //todo: was this redirecting correctly?
        return "redirect:/";
    }

    //displays recipes w/generate playlist buttons
    @RequestMapping(path = "/my-recipes", method = RequestMethod.GET)
    public String myRecipes(HttpSession session, Model model){

        //todo: make this into a single method in recipe service
        List <Recipe> recipes = recipeService.getUserRecipes(session);

        /*
        //gets current user from session
        User user = userRepo.findFirstByUsername(session.getAttribute("username").toString());
        //gets all of user's recipes
        List<Recipe> recipes = recipeRepo.findByUser(user);
        */

        //add recipes to model and return page
        model.addAttribute("recipes", recipes);
        return "/my-recipes";

    }

    @RequestMapping(path = "/create-playlist", method = RequestMethod.GET)
    public String createRecipe(HttpSession session) throws IOException, WebApiException {

        Api api = Api.DEFAULT_API;

        final TrackSearchRequest searchRequest = api.searchTracks("Blue Moon").market("US").build();
        final Page<Track> trackSearchResult = searchRequest.get();


        TrackRequest request = api.getTrack("0aN8uGH1qlWgleoVw9gxu0").build();
        Track track = request.get();
        System.out.println(track.getName());

        return "";
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
