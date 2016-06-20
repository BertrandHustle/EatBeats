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

        //adds attributes of recipe to tag list
        ArrayList<String> recipeTags = new ArrayList<>();
        recipeTags.add(recipe.getCategory());
        recipeTags.add(recipe.getRegion());
        recipeTags.add(recipe.getSeason());

        //todo: fix this so it's cleaner
        //todo: make sure this can handle 0 song cases
        //todo: make sure playlists do not exceed 10 songs
        //todo: incorporate ranking system
        //todo: incorporate algorithm using Spotify parameters (e.g. tempo, mood, etc)
        //gets all songs which match tag list
        List<Song> songsByCategory = songRepo.findByCategory(recipe.getCategory());
        List<Song> songsByRegion = songRepo.findByRegion(recipe.getRegion());
        List<Song> songsBySeason = songRepo.findBySeason(recipe.getSeason());

        List<Song> songs = songsByCategory;

        //unsure if this identity check will do what I want (may get different instances of
        //songs identical in properties, resulting in duplicates)

        for (Song song : songsByRegion){
            if (!songs.contains(song)){
                songs.add(song);
            }
        }

        for (Song song : songsBySeason){
            if (!songs.contains(song)){
                songs.add(song);
            }
        }

        //makes playlist from recipe, song, and user
        Playlist playlist = new Playlist(recipe, songs, user);

        return playlist;

    }



}
