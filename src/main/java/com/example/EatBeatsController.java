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
//todo: fix package structure
//todo: implement search feature for recipes
//todo: allow user to view other users' recipes (via search)
//todo: implement @notNull tags on recipe fields?
//todo: add spotify suggestions as songs in database
//todo: switch edit endpoint to GET, not POST (can use image rather than button)
//todo: make it so it doesn't add recipe if it hits an error
//todo: change spotify song search function to return more results than 3?
//todo: check if user exists at each page


//todo: handle cases where no songs are found for recipe suggestion songs


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
                                   Model model) throws IOException, WebApiException {

        //retrieves current user

        //todo: handle this with user service
        String username = session.getAttribute("username").toString();
        User user = userRepo.findFirstByUsername(username);

        //creates new recipe from user input, saves to db
        Recipe recipe = new Recipe(season, name, category, region, description);
        recipe.setUser(user);

        //checks if any fields in recipe are null or songs list is empty
        //if any fields are null, puts error into flash attribute,
        //else, puts recipe into database successfully
        if (recipe.getRegion() != null &&
            recipe.getName() != null &&
            recipe.getCategory() != null &&
            recipe.getSeason() != null &&
            recipe.getDescription() != null){
            recipeRepo.save(recipe);
        }

        //may want to change this to flash attribute?
        session.setAttribute("recipe", recipe);
        model.addAttribute("recipe", recipe);
        session.setAttribute("user", user);

        return "redirect:/song-suggest";
    }

    //todo: get this to return same page after searching (with new previews)
    @RequestMapping(path = "/song-suggest", method = RequestMethod.GET)
    public String getSongSuggest(HttpSession session, Model model) throws IOException, WebApiException {

        //gets user out of session
        User user = (User) session.getAttribute("user");
        //gets recipe from last page out of session
        Recipe recipe = (Recipe) session.getAttribute("recipe");

        //gets previews for first three songs and adds to model
        ArrayList<Song> suggestedSongs = spotifyService.getListOfSuggestedSongsFromRecipeAndSaveToDatabase(recipe, user);

        Song song1 = suggestedSongs.get(0);
        Song song2 = suggestedSongs.get(1);
        Song song3 = suggestedSongs.get(2);

        //todo: rewrite html to use {{song1.name}}, etc.
        model.addAttribute("song1", song1);
        model.addAttribute("song2", song2);
        model.addAttribute("song3", song3);

        String songPreview1 = songService.getSongPreviewUrl(song1);
        String songPreview2 = songService.getSongPreviewUrl(song2);
        String songPreview3 = songService.getSongPreviewUrl(song3);

        model.addAttribute("songPreview1", songPreview1);
        model.addAttribute("songPreview2", songPreview2);
        model.addAttribute("songPreview3", songPreview3);

        model.addAttribute("songName1", song1.getName());
        model.addAttribute("songName2", song2.getName());
        model.addAttribute("songName3", song3.getName());

        model.addAttribute("songArtist1", song1.getArtist());
        model.addAttribute("songArtist2", song2.getArtist());
        model.addAttribute("songArtist3", song3.getArtist());

        model.addAttribute("recipe", recipe);

        return "song-suggest";

    }

    @RequestMapping(path = "/song-suggest", method = RequestMethod.POST)
    public String postSongSuggest(HttpSession session, String songTitle1, String artist1,
                                  String songTitle2, String artist2, String songTitle3,
                                  String artist3, Model model) throws IOException, WebApiException {

        Song song1 = new Song(artist1, songTitle1);
        Song song2 = new Song(artist2, songTitle2);
        Song song3 = new Song(artist3, songTitle3);

        List<Song> songs = Arrays.asList(song1, song2, song3);
        session.setAttribute("songs", songs);

        String songPreview1 = songService.getSongPreviewUrl(song1);
        String songPreview2 = songService.getSongPreviewUrl(song2);
        String songPreview3 = songService.getSongPreviewUrl(song3);

        model.addAttribute("songPreview1", songPreview1);
        model.addAttribute("songPreview2", songPreview2);
        model.addAttribute("songPreview3", songPreview3);

        model.addAttribute("songName1", songTitle1);
        model.addAttribute("songName2", songTitle2);
        model.addAttribute("songName3", songTitle3);

        return "redirect:/song-suggest";

    }

    //todo: add checkmarks for each song and default to checked
    //todo: move song search off of recipe page to song search page (and handle cases where no songs are suggested)
    //todo: query database when songs are suggested, create playlist from retrieved songs if song suggested is already in database

    @RequestMapping(path = "/song-search", method = RequestMethod.GET)
    public String getSongSearch(HttpSession session, Model model) throws IOException, WebApiException {

        List<Song> songs = (List<Song>) session.getAttribute("songs");
        Recipe recipe = (Recipe) session.getAttribute("recipe");

        //gets song preview urls for each song
        String songPreview1 = songService.getSongPreviewUrl(songs.get(0));
        String songPreview2 = songService.getSongPreviewUrl(songs.get(1));
        String songPreview3 = songService.getSongPreviewUrl(songs.get(2));

        String songName1 = songs.get(0).getName();
        String songName2 = songs.get(1).getName();
        String songName3 = songs.get(2).getName();

        model.addAttribute("songPreview1", songPreview1);
        model.addAttribute("songPreview2", songPreview2);
        model.addAttribute("songPreview3", songPreview3);

        model.addAttribute("songName1", songName1);
        model.addAttribute("songName2", songName2);
        model.addAttribute("songName3", songName3);

        return "song-search";
    }

    //routes for song search previews, re-searching songs and adding recipes

    @RequestMapping(path = "/song-search", method = RequestMethod.POST)
    public String postSongSearch(HttpSession session, String songTitle1, String artist1,
                                  String songTitle2, String artist2, String songTitle3,
                                  String artist3, Model model) throws IOException, WebApiException {

        Recipe recipe = (Recipe) session.getAttribute("recipe");
        List<Song> songs = (List<Song>) session.getAttribute("songs");

        /**
         * 1: make new songs from user input
         * 2: get preview urls from songs
         * 3: add preview urls to model and redirect
         */

        Song song1 = new Song (artist1, songTitle1);
        Song song2 = new Song (artist2, songTitle2);
        Song song3 = new Song (artist3, songTitle3);

        String songPreview1 = songService.getSongPreviewUrl(song1);
        String songPreview2 = songService.getSongPreviewUrl(song2);
        String songPreview3 = songService.getSongPreviewUrl(song3);

        model.addAttribute("songPreview1", songPreview1);
        model.addAttribute("songPreview2", songPreview2);
        model.addAttribute("songPreview3", songPreview3);

        //where should this redirect to?
        return "redirect:/song-search";

    }

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

    //submit recipe and songs
    @RequestMapping(path = "/submit-songs", method = RequestMethod.POST)
    public String submitSongs(HttpSession session){

        List<Song> songs = (List<Song>) session.getAttribute("songs");

        //iterates over all songs in song list and adds to repo if spotifyId isn't an empty string
        //i.e. if the spotify search query was successful
        for (Song song : songs){
            if (!song.getSpotifyId().equals("")){
                songRepo.save(song);
            }
        }

        //todo: add "songs added!" message into model here to display on home page
        return "redirect:/";

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
    public String login(HttpSession session, String username, String password,
                        Model model, RedirectAttributes redirectAttributes) throws Exception{

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
