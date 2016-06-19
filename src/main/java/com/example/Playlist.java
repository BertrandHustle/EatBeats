package com.example;

import com.google.common.base.Joiner;

import javax.persistence.*;
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
    private ArrayList<String> songSpotifyIds;

    //cascading needed because of multiple @ManyToOne annotations to user?
    @ManyToOne(cascade= CascadeType.ALL)
    private User user;

    //default constructor
    public Playlist() {
    }

    public Playlist(String recipeName, ArrayList<String> songSpotifyIds, User user) {
        this.recipeName = recipeName;
        this.songSpotifyIds = songSpotifyIds;
        this.user = user;
        //todo: modify so USERNAME is replaced by the user who made the playlist

        //auto-sets spotify playlist link based on songs passed in
        String joinedIds = Joiner.on(",").join(songSpotifyIds);
        this.spotifyLink = "https://embed.spotify.com/?uri=spotify:trackset:USERNAME:"+ joinedIds;
    }

    /*
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
    */

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

    public ArrayList<String> getSongSpotifyIds() {
        return songSpotifyIds;
    }

    public void setSongSpotifyIds(ArrayList<String> songSpotifyIds) {
        this.songSpotifyIds = songSpotifyIds;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
}
