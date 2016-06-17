package com.example;


import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.RecommendationsRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

//todo: put copyright text here
//todo: make generate-playlist route
//todo: make single recipe page (edit page)
//todo: fix package structure

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

        //todo: handle this with user service
        String username = session.getAttribute("username").toString();
        User user = userRepo.findFirstByUsername(username);

        //creates new recipe from user input, saves to db

        recipeService.saveRecipe(user, season, name, category, region, description);

        //todo: was this redirecting correctly?
        return "redirect:/";
    }

    //displays recipes
    @RequestMapping(path = "/my-recipes", method = RequestMethod.GET)
    public String myRecipes(HttpSession session, Model model){

        //gets username out of session and returns their recipes
        String username = session.getAttribute("username").toString();
        List <Recipe> recipes = recipeService.getUserRecipes(username);

        //add recipes to model and return page
        model.addAttribute("recipes", recipes);
        return "/my-recipes";

    }


    //creates random Spotify playlist based on seed tracks

    //implimentation done by Doug Hughes

    @RequestMapping(path = "/create-playlist", method = RequestMethod.GET)
    public String createRecipe(HttpSession session) throws IOException, WebApiException {

        //builds api object from application id/secret
        final Api api = Api.builder()
                        .clientId("f5b8721c375a43eb801334c0d4329a0d")
                        .clientSecret("e4cf678de40843279f667da0b7dfabae").build();

        /* Create a request object. */
                final ClientCredentialsGrantRequest clientCredentialsGrantRequest = api.clientCredentialsGrant().build();

        /* Use the request object to make the request, either asynchronously (getAsync) or synchronously (get) */
                ClientCredentials clientCredentials = clientCredentialsGrantRequest.get();

        /* Set access token on the Api object so that it's used going forward */
                api.setAccessToken(clientCredentials.getAccessToken());
                final RecommendationsRequest recommendationsRequest = api.getRecommendations()
                        .seedTrack("0CU30zif0WR5h1DI9oGtAF")
                        .build();

                //try/catch block for print test
                try {
                    List<Track> tracks = recommendationsRequest.get();
                    System.out.println("I got " + tracks.size() + " results!");
                } catch (Exception e) {
                    System.out.println("Something went wrong!" + e.getMessage());
                }
                int x = 1;
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
