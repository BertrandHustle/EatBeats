package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        //gets all songs which match tag list
        //todo: add other database searches for region/season etc.
        List<Song> songs = songRepo.findByCategory(recipe.getCategory());

        //makes playlist from recipe, song, and user
        Playlist playlist = new Playlist(recipe, songs, user);

        return playlist;

    }

}
