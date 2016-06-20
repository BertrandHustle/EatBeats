package com.example;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * repo for Playlists
 */

public interface PlaylistRepo extends CrudRepository<Playlist, Integer> {
    Playlist findById(int id);
    List<Playlist> findByUser(User user);
    List<Playlist> findByRecipe(Recipe recipe);
}
