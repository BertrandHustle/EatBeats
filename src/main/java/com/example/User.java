package com.example;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for users, will have one-to-many for recipes/favorited playlists
 */

//todo: add field for voted songs

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private int id;

    private String username;
    private String password;

    //may need to fix this from being Eager
    @OneToMany(targetEntity=Playlist.class, mappedBy="user", fetch=FetchType.EAGER)
    private List<Playlist> favoritePlaylists = new ArrayList<>();

    public User(String username, String password) throws PasswordHasher.CannotPerformOperationException {
        this.username = username;
        //auto-hashes passwords when User is constructed
        this.password = PasswordHasher.createHash(password);
    }

    //default constructor to make Spring happy
    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Playlist> getFavoritePlaylists() {
        return favoritePlaylists;
    }

    public void setFavoritePlaylists(List<Playlist> favoritePlaylists) {
        this.favoritePlaylists = favoritePlaylists;
    }
}
