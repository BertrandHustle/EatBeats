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
    @Column(length=1500)
    private String spotifyLink;

    //this is the recipe which the playlist belongs to
    @ManyToOne
    private Recipe recipe;

    //this holds the Song objects which comprise the Playlist
    //private ArrayList<String> songSpotifyIds;

    //@ManyToMany (targetEntity=Song.class, mappedBy="playlist", fetch=FetchType.EAGER)
    @ManyToMany
    @JoinTable(name = "playlist_song",
            joinColumns = @JoinColumn(name = "playlist_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "song_id", referencedColumnName = "id"))
    private List<Song> songs = new ArrayList<>();

    //cascading needed because of multiple @ManyToOne annotations to user?
    @ManyToOne //(cascade= CascadeType.ALL)
    @NotNull
    User user;

    //default constructor
    public Playlist() {
    }

    public Playlist(Recipe recipe, List<Song> songs, User user) {
        this.recipe = recipe;
        this.user = user;

        //auto-sets spotify playlist link based on songs passed in
        String joinedIds = joinSongIds(songs);
        String recipeName = recipe.getName();
        this.spotifyLink = "https://embed.spotify.com/?uri=spotify:trackset:"+recipeName+":"+ joinedIds;

        //auto-sets songs in playlist to have tags from recipe
        ArrayList<String> recipeTags = new ArrayList<>();

        recipeTags.add(recipe.getCategory());
        recipeTags.add(recipe.getSeason());
        recipeTags.add(recipe.getRegion());

        for (Song song : songs){
            song.getTags().addAll(recipeTags);
            song.setRegion(recipe.getRegion());
            song.setSeason(recipe.getSeason());
            song.setCategory(recipe.getCategory());
        }
        this.songs.addAll(songs);
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

    public String getFeaturedArtist(){

        //first song in list
        Song firstSong = songs.get(0);
        //gets artist name and returns
        String firstArtistName = firstSong.getArtist();
        return firstArtistName;

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
