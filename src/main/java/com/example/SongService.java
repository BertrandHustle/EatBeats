package com.example;

import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.Track;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service containing methods for organizing/acting upon Songs
 */


@Service
public class SongService {

    @Autowired
    SongRepo songRepo;

    @Autowired
    SpotifyService spotifyService;


    public List<Song> tagAndSaveSongsFromRecipe(List<Song> songs, Recipe recipe) {

        //retrieves fields from recipe
        String category = recipe.getCategory();
        String season = recipe.getSeason();
        String region = recipe.getRegion();

        //stores tagged songs
        ArrayList<Song> returnSongs = new ArrayList<>();

        //tags each song with recipe fields (if not null) and saves to database
        for (Song song : songs) {
            //todo: handle cases where song not found in spotify (empty spotifyId string)
            //todo: handle duplicates in DB

            //checks if spotify id is empty and if song exists in database

            Song foundSong = new Song();

            try {
                foundSong = songRepo.findByNameIgnoreCase(song.getName());
            } catch (Exception e){
                System.out.println("too many or too few results found!");
            }

            if (!song.getSpotifyId().equals("") &&
                foundSong == null) {
                song.setCategory(category);
                song.setSeason(season);
                song.setRegion(region);
                songRepo.save(song);
                returnSongs.add(song);
            } else {
                //if song already exists in DB, retrieves song and adds to return list
                song = foundSong;
                returnSongs.add(song);
            }
        }

        //returns tagged songs
        return returnSongs;
    }

    //gets a 30sec preview of the song from Spotify (user authentication not needed)
    public String getSongPreviewUrl(Song song) throws IOException, WebApiException {

        Track track = spotifyService.getTrackFromSpotify(song.getName(), song.getArtist());
        String songPreviewUrl = track.getPreviewUrl();
        return songPreviewUrl;

    }

    public Song convertTrackToSong(Track track) throws IOException, WebApiException {

        Song song = new Song(track.getArtists().get(0).getName(), track.getName(), track.getId());
        return song;

    }
}
