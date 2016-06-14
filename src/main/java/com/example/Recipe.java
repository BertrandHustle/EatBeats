package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

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
    //description of the recipe
    private String description;

    //links many to one mapped on User
    @ManyToOne
    @NotNull
    User user;

    public Recipe() {
    }

    public Recipe(String season, String name, String category, String region, String description) {
        this.season = season;
        this.name = name;
        this.category = category;
        this.region = region;
        this.description = description;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
