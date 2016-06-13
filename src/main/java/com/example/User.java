package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class for users, will have one-to-many for recipes/favorited playlists
 */

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private int id;

    private String username;
    //todo: make sure passwords are hashed
    private String password;


    public User(String username, String password) throws PasswordHasher.CannotPerformOperationException {
        this.username = username;
        //auto-hashes passwords when User is constructed
        this.password = PasswordHasher.createHash(password);
    }
}
