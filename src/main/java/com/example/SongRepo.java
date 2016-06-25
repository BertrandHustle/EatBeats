package com.example;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repo for saving/retrieving Songs
 */

//todo: find a more concise way to make these database searches
public interface SongRepo extends CrudRepository<Song, Integer>{
    Song findByNameIgnoreCaseAndArtistIgnoreCase(String name, String artist);
    List<Song> findByCategoryAndRegionAndSeason(String category, String region, String season);
    Song findByNameIgnoreCase(String name);
}
