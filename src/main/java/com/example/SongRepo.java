package com.example;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repo for saving/retrieving Songs
 */

//todo: find a more concise way to make these database searches
public interface SongRepo extends CrudRepository<Song, Integer>{
    List<Song> findByCategory(String category);
    List<Song> findByRegion(String region);
    List<Song> findBySeason(String season);
    Song findByName(String name);
}
