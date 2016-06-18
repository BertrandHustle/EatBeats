package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;

/**
 * Holds playlists and their rankings
 */

@Entity
public class Playlist {

    @Id
    @GeneratedValue
    private int id;

    //link to spotify playlist (is there a better way to do this?)
    private String spotifyLink;

    //this is the recipe which the playlist belongs to
    private String recipe;

    //this holds the Song objects which comprise the Playlist
    private ArrayList<Song> songs;

    //todo: add many-to-one link to user for saving favorite playlists


    //todo: expand constructor to auto-create spotify link and require recipe

    //default constructor
    public Playlist() {
    }

    public Playlist(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpotifyLink() {
        return spotifyLink;
    }

    public void setSpotifyLink(String spotifyLink) {
        this.spotifyLink = spotifyLink;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
