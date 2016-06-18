package com.example;

import com.google.common.base.Joiner;

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
    private String recipeName;

    //this holds the Song objects which comprise the Playlist
    private ArrayList<Song> songs;

    //todo: add many-to-one link to user for saving favorite playlists

    //default constructor
    public Playlist() {
    }

    public Playlist(String recipeName, ArrayList<Song> songs) {
        this.recipeName = recipeName;
        this.songs = songs;
        //todo: modify so USERNAME is replaced by the user who made the playlist

        //auto-sets spotify playlist link based on songs passed in
        String joinedIds = joinSongIds(songs);
        this.spotifyLink = "https://embed.spotify.com/?uri=spotify:trackset:USERNAME:"+ joinedIds;
    }

    public String joinSongIds (ArrayList<Song> songs){

        //holds song ids before joining
        ArrayList<String> songIds = new ArrayList<>();

        //gets spotify id of each song and adds to arraylist
        for (Song song : songs){
            songIds.add(song.getSpotifyId());
        }

        //joins song ids on comma
        String joinedIds = Joiner.on(",").join(songIds);
        return joinedIds;
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
        return recipeName;
    }

    public void setRecipe(String recipe) {
        this.recipeName = recipe;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
