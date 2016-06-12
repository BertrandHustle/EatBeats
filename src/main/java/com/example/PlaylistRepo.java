package com.example;

import org.springframework.data.repository.CrudRepository;

/**
 * repo for Playlists
 */

public interface PlaylistRepo extends CrudRepository<Playlist, Integer> {

}
