package com.example;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service containing methods for organizing/acting upon Songs
 */


@Service
public class SongService {

    @Autowired
    SongRepo songRepo;

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
            } catch (HibernateException he){
                System.out.println("too many results found!");
            }

            if (//song.getName() != null &&
                //song.getArtist() != null &&
                !song.getSpotifyId().equals("") &&
                foundSong != null)

                song.setCategory(category);
                song.setSeason(season);
                song.setRegion(region);
                songRepo.save(song);
                returnSongs.add(song);
        }

        //returns tagged songs
        return returnSongs;
    }
}
