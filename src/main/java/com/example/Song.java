package com.example;

import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.*;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Song class for ORM purposes, keeps track of Song's spotify ID and ranking
 * so playlists can be generated
 */

@Entity
public class Song {

    //these properties come from the Spotify API
    /*
    private SimpleAlbum album;
    private List<SimpleArtist> artists;
    private List<String> availableMarkets;
    private int discNumber;
    private int duration;
    private boolean explicit;
    private ExternalIds externalIds;
    private ExternalUrls externalUrls;
    private String href;
    private int popularity;
    private String previewUrl;
    private int trackNumber;
    private SpotifyEntityType type = SpotifyEntityType.TRACK;
    private String uri;
    */

    //added to make song searching by title easier
    private String artist;
    private String spotifyId;
    private String name;

    //these properties are added for ORM purposes/playlist generation

    @Id
    @GeneratedValue
    private int id;
    //ranking of thumbs up vs thumbs down for track
    private int rank;
    //tags for track (e.g. season, category, etc.)
    //todo: change this to be a hashset so duplicates aren't added
    private ArrayList<String> tags = new ArrayList<>();
    private String category;
    private String region;
    private String season;

    //links to Playlist to which Song belongs
    @ManyToOne
    Playlist playlist;

    public Song() {
    }

    public Song(String artist, String name) throws IOException, WebApiException {
        this.artist = artist;
        this.name = name;

        //automatically sets spotify id for song
        //todo: autowire this above, not in method (this is taking up time)
        SpotifyService spotifyService = new SpotifyService();
        String trackId = spotifyService.searchByTrackName(name, artist);
        this.spotifyId = trackId;

    }

    //constructor which passes spotifyId in directly from returned Track object
    public Song(String artist, String name, String spotifyId){
        this.artist = artist;
        this.name = name;
        this.spotifyId = spotifyId;
    }

    /*
    public SimpleAlbum getAlbum() {
        return album;
    }

    public void setAlbum(SimpleAlbum album) {
        this.album = album;
    }

    public List<SimpleArtist> getArtists() {
        return artists;
    }

    public void setArtists(List<SimpleArtist> artists) {
        this.artists = artists;
    }

    public List<String> getAvailableMarkets() {
        return availableMarkets;
    }

    public void setAvailableMarkets(List<String> availableMarkets) {
        this.availableMarkets = availableMarkets;
    }

    public int getDiscNumber() {
        return discNumber;
    }

    public void setDiscNumber(int discNumber) {
        this.discNumber = discNumber;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }

    public ExternalUrls getExternalUrls() {
        return externalUrls;
    }

    public void setExternalUrls(ExternalUrls externalUrls) {
        this.externalUrls = externalUrls;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public SpotifyEntityType getType() {
        return type;
    }

    public void setType(SpotifyEntityType type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    */

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
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

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getId() {
        return id;
    }
}
