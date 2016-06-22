package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Holds methods for creating/handling Playlists
 */

@Service
public class PlaylistService {

    @Autowired
    SongRepo songRepo;

    public Playlist makePlaylistFromRecipe(Recipe recipe, User user){

        String category = recipe.getCategory();
        String region = recipe.getRegion();
        String season = recipe.getSeason();

        //adds attributes of recipe to recipe tag hashset (to avoid duplicates)
        recipe.getTags().addAll(Arrays.asList(category, region, season));

        /*
        //adds attributes of recipe to tag list
        ArrayList<String> recipeTags = new ArrayList<>();
        recipeTags.add(recipe.getCategory());
        recipeTags.add(recipe.getRegion());
        recipeTags.add(recipe.getSeason());
        */

        //todo: fix this so it's cleaner
        //todo: make sure this can handle 0 song cases
        //todo: make sure playlists do not exceed 10 songs
        //todo: incorporate algorithm using Spotify parameters (e.g. tempo, mood, etc)
        //todo: fix this so it doesn't retrieve duplicates!

        //gets all songs which match tag list
        List<Song> songs = songRepo.findByCategoryAndRegionAndSeason(category, region, season);

        /*
        List<Song> songsByCategory = songRepo.findByCategory(recipe.getCategory());
        List<Song> songsByRegion = songRepo.findByRegion(recipe.getRegion());
        List<Song> songsBySeason = songRepo.findBySeason(recipe.getSeason());
        */

        //makes playlist from recipe, song, and user
        Playlist playlist = new Playlist(recipe, songs, user);

        return playlist;

    }



}
