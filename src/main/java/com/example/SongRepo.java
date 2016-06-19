package com.example;

import org.springframework.data.repository.CrudRepository;

/**
 * Repo for saving/retrieving Songs
 */

public interface SongRepo extends CrudRepository<Song, Integer>{

}
