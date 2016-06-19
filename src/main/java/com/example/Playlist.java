package com.example;

import com.google.common.base.Joiner;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne
    private Recipe recipe;

    //this holds the Song objects which comprise the Playlist
    //private ArrayList<String> songSpotifyIds;

    @OneToMany
    private List<Song> songs;

    //cascading needed because of multiple @ManyToOne annotations to user?
    @ManyToOne //(cascade= CascadeType.ALL)
    @NotNull
    User user;

    //default constructor
    public Playlist() {
    }

    public Playlist(Recipe recipe, List<Song> songs, User user) {
        this.recipe = recipe;
        this.songs = songs;
        this.user = user;

        //auto-sets spotify playlist link based on songs passed in
        String joinedIds = joinSongIds(songs);
        String recipeName = recipe.getName();
        this.spotifyLink = "https://embed.spotify.com/?uri=spotify:trackset:"+recipeName+":"+ joinedIds;
    }


    public String joinSongIds (List<Song> songs){

        //holds song ids before joining
        List<String> songIds = new ArrayList<>();

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

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
