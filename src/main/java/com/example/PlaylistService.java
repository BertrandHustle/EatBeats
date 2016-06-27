package com.example;

import com.wrapper.spotify.exceptions.WebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Holds methods for creating/handling Playlists
 */

@Service
public class PlaylistService {

    @Autowired
    SongRepo songRepo;

    @Autowired
    SpotifyService spotifyService;

    public Playlist makePlaylistFromRecipe(Recipe recipe, User user){

        Random random = new Random();

        String category = recipe.getCategory();
        String region = recipe.getRegion();
        String season = recipe.getSeason();

        //adds attributes of recipe to recipe tag hashset (to avoid duplicates)
        recipe.getTags().addAll(Arrays.asList(category, region, season));

        //todo: incorporate algorithm using Spotify parameters (e.g. tempo, mood, etc)

        Playlist playlist = new Playlist();

        //gets all songs which match tag list, returns null if no songs are found

        if (songRepo.findByCategoryAndRegionAndSeason(category, region, season).size() != 0){
            List<Song> songs = songRepo.findByCategoryAndRegionAndSeason(category, region, season);
            //makes playlist from recipe, song, and user

            //generates random range of 5 songs within returned list
            if (songs.size() >= 5){
                int randomInt = random.nextInt(songs.size() - 5);
                playlist.setSongs(songs.subList(randomInt, randomInt + 5));
            } else {
                playlist.setSongs(songs);
            }
            playlist.setUser(user);
            playlist.setRecipe(recipe);
        } else {
            return null;
        }

        return playlist;

    }

    public Playlist makePlaylistFromPlaylistUrl(String url, Recipe recipe, User user) throws IOException, WebApiException {

        String recipeName = recipe.getName();
        String[] splitOnTrackset = url.split(recipeName+":");
        String[] suggestedSongIds = splitOnTrackset[1].split(",");

        ArrayList<Song> songs = new ArrayList<>();

        for (String id : suggestedSongIds){
            Song song = (spotifyService.getSongFromSpotifyId(id));
            songs.add(song);
        }

        Playlist playlist = new Playlist(recipe, songs, user);

        return playlist;

    }

}
