package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Holds playlists and their rankings
 */

@Entity
public class Playlist {

    @Id
    @GeneratedValue
    private int id;

    //total of favorable rankings given for this playlist by users
    private int totalUpvotes;

    //total of unfavorable rankings given for this playlist by users
    private int totalDownvotes;

    //todo: create method which weights rankings properly

    //link to spotify playlist (is there a better way to do this?)
    private String spotifyLink;

}
