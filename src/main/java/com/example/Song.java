package com.example;

import com.wrapper.spotify.models.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * Song class for ORM purposes, keeps track of Song's spotify ID and ranking
 * so playlists can be generated
 */
public class Song {

    //these properties come from the Spotify API
    private SimpleAlbum album;
    private List<SimpleArtist> artists;
    private List<String> availableMarkets;
    private int discNumber;
    private int duration;
    private boolean explicit;
    private ExternalIds externalIds;
    private ExternalUrls externalUrls;
    private String href;
    private String spotifyid;
    private String name;
    private int popularity;
    private String previewUrl;
    private int trackNumber;
    private SpotifyEntityType type = SpotifyEntityType.TRACK;
    private String uri;

    //these properties are added for ORM purposes/playlist generation

    @Id
    @GeneratedValue
    private int id;
    //ranking of thumbs up vs thumbs down for track
    private int rank;
    //holds tags for track (e.g. season, category, etc.)
    private ArrayList<String> tags = new ArrayList<>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}
