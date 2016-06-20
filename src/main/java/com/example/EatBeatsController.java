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
import java.util.ArrayList;
import java.util.List;

//todo: put copyright text here
//todo: DEPLOY!
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
    PlaylistService playlistService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    SpotifyService spotifyService;

    @Autowired
    SongRepo songRepo;

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
                                   String category, String region, String description,
                                   String songTitle1, String songTitle2, String songTitle3,
                                   String songArtist1, String songArtist2, String songArtist3) throws IOException, WebApiException {

        //retrieves current user

        //todo: handle this with user service
        String username = session.getAttribute("username").toString();
        User user = userRepo.findFirstByUsername(username);

        //creates new recipe from user input, saves to db

        recipeService.saveRecipe(user, season, name, category, region, description);

        String song1Id = spotifyService.searchByTrackName(songTitle1, songArtist1);

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

        return "my-recipes";

    }


    //creates random Spotify playlist based on seed tracks
    @RequestMapping(path = "/make-playlist", method = RequestMethod.GET)
    public String createPlaylist(HttpSession session, String id, Model model) throws IOException, WebApiException {

                String username = session.getAttribute("username").toString();
                User user = userRepo.findFirstByUsername(username);

                int ID = Integer.parseInt(id);
                Recipe recipe = recipeRepo.findById(ID);

                //todo: generate playlist if no tracks found
                Playlist playlist = playlistService.makePlaylistFromRecipe(recipe, user);
                String spotifyPlaylistUrl = playlist.getSpotifyLink();
                model.addAttribute("spotifyPlaylistUrl", spotifyPlaylistUrl);

                return "recipe-playlist";
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
