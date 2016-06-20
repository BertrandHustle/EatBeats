package com.example;

import com.google.common.base.Joiner;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.RecommendationsRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Track;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Holds methods for handling spotify requests
 * and retrieving objects from said requests
 */

@Service
public class SpotifyService {

    public List<Track> getListOfRecommendationsFromSeedTracks(ArrayList<String> seeds) throws IOException, WebApiException {

        //todo: move this into separate method
        final Api api = Api.builder()
                .clientId("f5b8721c375a43eb801334c0d4329a0d")
                .clientSecret("e4cf678de40843279f667da0b7dfabae").build();

        final ClientCredentialsGrantRequest clientCredentialsGrantRequest = api.clientCredentialsGrant().build();
        ClientCredentials clientCredentials = clientCredentialsGrantRequest.get();

        api.setAccessToken(clientCredentials.getAccessToken());
        final RecommendationsRequest recommendationsRequest = api.getRecommendations()
                .seedTrack(seeds)
                .build();

        List<Track> tracks = recommendationsRequest.get();
        return tracks;

    }

    public String getCommaJoinedTrackIds(List<Track> tracks){

        //holds track ids
        ArrayList<String> trackIds = new ArrayList<>();

        //checks if each track has an id, and adds to arraylist if it does
        //(probably an unnecessary check, but there for safety)
        for (Track track : tracks){
            if (track.getId() != null){
                trackIds.add(track.getId());
            }
        }

        //joins tracks on comma and returns joined string
        String joinedTracks = Joiner.on(",").join(trackIds);
        return joinedTracks;

    }

    //todo: rename this to denote that it returns id, not song
    public String searchByTrackName (String trackName, String artist) throws IOException, WebApiException {

        final Api api = Api.builder()
                .clientId("f5b8721c375a43eb801334c0d4329a0d")
                .clientSecret("e4cf678de40843279f667da0b7dfabae").build();

        final ClientCredentialsGrantRequest clientCredentialsGrantRequest = api.clientCredentialsGrant().build();
        ClientCredentials clientCredentials = clientCredentialsGrantRequest.get();

        api.setAccessToken(clientCredentials.getAccessToken());

        //surrounds trackName with quotes so Spotify API can get an exact query match
        final TrackSearchRequest trackSearchRequest = api.searchTracks("\"" + trackName + "\"").limit(3).query(trackName)
                .build();

        //init arraylists
        ArrayList<Track> searchResultTracks = new ArrayList<>();
        String searchResultSpotifyId = "";

        searchResultTracks.addAll(trackSearchRequest.get().getItems());

        //try/catch exception handling for an empty search result
        //iterates through search results and finds correct result id by artist/song name
        try {
            for (Track track : searchResultTracks) {
                if ((track.getArtists().get(0).getName().equals(artist)
                        && track.getName().equalsIgnoreCase(trackName))) {
                    searchResultSpotifyId = track.getId();
                }
            }
        } catch (NullPointerException npe){
            searchResultSpotifyId = "no results found!";
        }

        return searchResultSpotifyId;

    }

    public Song getSongFromSpotify(String trackName, String artist) throws IOException, WebApiException {

        final Api api = Api.builder()
                .clientId("f5b8721c375a43eb801334c0d4329a0d")
                .clientSecret("e4cf678de40843279f667da0b7dfabae").build();

        final ClientCredentialsGrantRequest clientCredentialsGrantRequest = api.clientCredentialsGrant().build();
        ClientCredentials clientCredentials = clientCredentialsGrantRequest.get();

        api.setAccessToken(clientCredentials.getAccessToken());

        //surrounds trackName with quotes so Spotify API can get an exact query match
        final TrackSearchRequest trackSearchRequest = api.searchTracks("\"" + trackName + "\"").limit(3).query(trackName)
                .build();

        //todo: make this so it doesn't mirror above code
        //init arraylists
        ArrayList<Track> searchResultTracks = new ArrayList<>();

        searchResultTracks.addAll(trackSearchRequest.get().getItems());
        Track returnTrack = new Track();

        //makes new song from title, artist of search result
        try {
            for (Track track : searchResultTracks) {
                if ((track.getArtists().get(0).getName().equals(artist)
                        && track.getName().equalsIgnoreCase(trackName))) {
                    returnTrack = track;
                }
            }
        } catch (NullPointerException npe){
            System.out.println("no results found!");
        }

        Song song = new Song(returnTrack.getArtists().get(0).getName(), returnTrack.getName());
        return song;
    }
}
