package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Class for recipes, these will have playlists associated with them as well as properties (e.g. season and type)
 * used to generate said playlists
 */

@Entity
public class Recipe {

    @Id
    @GeneratedValue
    private int id;

    //season the recipe is meant to be eaten during (e.g. fall, winter)
    private String season;
    //name of the recipe
    private String name;
    //category of the recipe (e.g. entree, appetizer, side)
    private String category;
    //geographical region the recipe is from (e.g. French, Korean, etc.)
    private String region;

}
