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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//todo: put copyright text here
//todo: DEPLOY!
//todo: make single recipe page (edit page)
//todo: fix package structure
//todo: implement search feature for recipes
//todo: allow user to view other users' recipes (via search)
//todo: implement @notNull tags on recipe fields?
//todo: add spotify suggestions as songs in database
//todo: have user confirm that selected songs are correct
//todo: switch edit endpoint to GET, not POST (can use image rather than button)
//todo: make it so it doesn't add recipe if it hits an error


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
    SongService songService;

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
    //todo: change this so it redirects to search-songs
    @RequestMapping(path = "/create-recipe", method = RequestMethod.POST)
    public String postCreateRecipe(HttpSession session, String season, String name,
                                   String category, String region, String description,
                                   String songTitle1, String songTitle2, String songTitle3,
                                   String songArtist1, String songArtist2, String songArtist3,
                                   Model model) throws IOException, WebApiException {

        //retrieves current user

        //todo: handle this with user service
        String username = session.getAttribute("username").toString();
        User user = userRepo.findFirstByUsername(username);

        //creates new recipe from user input, saves to db

        //todo: DON'T ALLOW THIS UNLESS SONGS ARE FOUND
        Recipe recipe = new Recipe(season, name, category, region, description);
        recipe.setUser(user);

        //todo: this should be leaner: move this to service and pass in songs as arguments
        //constructs songs from user input
        Song song1 = spotifyService.getSongFromSpotify(songTitle1, songArtist1);
        Song song2 = spotifyService.getSongFromSpotify(songTitle2, songArtist2);
        Song song3 = spotifyService.getSongFromSpotify(songTitle3, songArtist3);

        //set song attributes by recipe attributes, saves songs to database
        List<Song> songs = Arrays.asList(song1, song2, song3);
        songService.tagAndSaveSongsFromRecipe(songs, recipe);

        if (!songs.isEmpty()){
            recipeRepo.save(recipe);
        }

        /*
        for (Song song : songs){
            if (song.getName() != null && song.getArtist() != null)
            song.setCategory(category);
            song.setSeason(season);
            song.setRegion(region);
            songRepo.save(song);
        }
        */

        model.addAttribute("recipe", recipe);
        model.addAttribute("son")

        //todo: add songs into model and redirect to search-songs
        //todo: add recipe into model so it can be saved on the next route

        return "redirect:/";
    }

    //todo: add edit recipe route
    //todo: add favorite playlists
    //todo: make sure number of songs passed into recommendation request doesn't exceed 10 (and has at least 1)

    //favorite playlists route
    @RequestMapping(path = "/favorite-playlists", method = RequestMethod.GET)
    public String favoritePlaylists(HttpSession session, String id, Model model){

        String username = session.getAttribute("username").toString();
        User user = userRepo.findFirstByUsername(username);

        List<Playlist> favoritePlaylists = user.getFavoritePlaylists();
        model.addAttribute("playlists", favoritePlaylists);

        return "favorite-playlists";

    }

    @RequestMapping(path = "/search-songs", method = RequestMethod.GET)
    public String getSearchSongs(HttpSession session, String songTitle1, String artist1,
                                 String songTitle2, String artist2, String songTitle3,
                                 String artist3){

        return "search-songs";

    }

    //edit recipe route (GET)
    @RequestMapping(path = "/edit-recipe", method = RequestMethod.GET)
    public String getEditRecipe(HttpSession session, String id, Model model){

        Recipe recipe = recipeRepo.findById(Integer.parseInt(id));
        model.addAttribute("recipe", recipe);

        return "edit-recipe";
    }

    @RequestMapping(path = "/edit-recipe", method = RequestMethod.POST)
    public String postEditRecipe(HttpSession session, String id, Model model,
                                 String season, String name, String category,
                                 String region, String description){

        Recipe recipe = recipeRepo.findById(Integer.parseInt(id));
        recipe.setRegion(region);
        recipe.setName(name);
        recipe.setCategory(category);
        recipe.setSeason(season);
        recipe.setDescription(description);

        //checks if any recipe fields are null, saves to db if not
        if (recipe.getRegion() != null &&
            recipe.getName() != null &&
            recipe.getCategory() != null &&
            recipe.getSeason() != null &&
            recipe.getDescription() != null) {
            recipeRepo.save(recipe);
        }

        return "redirect:/my-recipes";

    }

    //delete recipe route
    @RequestMapping(path = "/delete-recipe", method = RequestMethod.GET)
    public String deleteRecipe(HttpSession session, String id){
        recipeRepo.delete(Integer.parseInt(id));
        return "redirect:/my-recipes";
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

                //todo: don't generate playlist if no tracks found
                Playlist playlist = playlistService.makePlaylistFromRecipe(recipe, user);

                //todo: removes duplicates for test purposes, needs to be fixed
                playlist.setSongs(playlist.getSongs().subList(0, 3));

                String spotifyPlaylistUrl = spotifyService.createRecommendationsPlaylistUrlFromPlaylist(playlist, recipe.getName());
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

    @RequestMapping(path = "/create-account", method = RequestMethod.GET)
    public String getCreateAccount(){
        return "create-account";
    }

    //route for creating account and saving to database
    @RequestMapping(path = "/create-account", method = RequestMethod.POST)
    public String postCreateAccount(HttpSession session, String username, String password) throws PasswordHasher.CannotPerformOperationException {

        User user = new User(username, password);

        userRepo.save(user);
        return "redirect:/";

    }

}
