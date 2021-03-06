package com.example;

import com.google.common.base.Joiner;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.RecommendationsRequest;
import com.wrapper.spotify.methods.TrackRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;
import org.springframework.beans.factory.annotation.Autowired;
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


    String clientId = "219a55148b404fb18b61c44cdb010719";
    String clientSecret = "6cd7039b1a314ed7ab5208d07f9db234";

    @Autowired
    PlaylistService playlistService;

    @Autowired
    SongService songService;

    @Autowired
    SongRepo songRepo;


    public List<Track> getListOfRecommendationsFromSeedTracks(ArrayList<String> seeds) throws IOException, WebApiException {

        //todo: move this into separate method
        final Api api = Api.builder()
                .clientId(clientId)
                .clientSecret(clientSecret).build();

        final ClientCredentialsGrantRequest clientCredentialsGrantRequest = api.clientCredentialsGrant().build();
        ClientCredentials clientCredentials = clientCredentialsGrantRequest.get();

        api.setAccessToken(clientCredentials.getAccessToken());
        final RecommendationsRequest recommendationsRequest = api.getRecommendations()
                .seedTrack(seeds)
                .build();

        List<Track> tracks = recommendationsRequest.get();
        return tracks;

    }

    /*
    public String getRecommendationsPlaylistUrlFromSeedTracks(List<Track> tracks, String name) throws IOException, WebApiException {

        //makes array of passed-in track ids and converts to list of recommended track ids, then joins
        //those ids into a single string separated by commas
        ArrayList<String> joinedTrackIds = getSpotifyIdArrayFromTracks(tracks);
        List<Track> seedTracks = getListOfRecommendationsFromSeedTracks(joinedTrackIds);
        String joinedRecommendationIds = getCommaJoinedTrackIds(seedTracks);

        //builds and returns recommendations playlist url
        String recommendationsPlaylistUrl = "https://embed.spotify.com/?uri=spotify:trackset:"+name+":"+joinedRecommendationIds;
        return recommendationsPlaylistUrl;

    }


    public ArrayList<String> getSpotifyIdArrayFromTracks(List<Track> tracks){

        ArrayList<String> trackIds = new ArrayList<>();

        for (Track track : tracks){
            if (track.getId() != null){
                trackIds.add(track.getId());
            }
        }
        return trackIds;
    }
    */

    //refactor this so it's named better
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

    /*
    public String getCommaJoinedTrackIdsFromSongList(List<Song> songs){
        //holds song ids
        ArrayList<String> songIds = new ArrayList<>();

        //checks if each song has a Spotify id, and adds to arraylist if it does
        //(probably an unnecessary check, but there for safety)
        for (Song song : songs){
            if (song.getSpotifyId() != null){
                songIds.add(song.getSpotifyId());
            }
        }

        //joins tracks on comma and returns joined string
        String joinedTracks = Joiner.on(",").join(songIds);
        return joinedTracks;
    }
    */

    //todo: rename this to denote that it returns id, not song
    public String searchByTrackName (String trackName, String artist) throws IOException, WebApiException {

        final Api api = Api.builder()
                .clientId(clientId)
                .clientSecret(clientSecret).build();

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
        //todo: fix this so if artist name isn't exact match it gets real artist name, not the one passed in by the user
        try {
            for (Track track : searchResultTracks) {

                //iterates over all artists in each track and checks to see if queried artist matches
                boolean artistContainedInSearchResults = false;

                for(SimpleArtist trackArtist : track.getArtists()){
                    if (trackArtist.getName().contains(artist)){
                        artistContainedInSearchResults = true;
                    }
                }

                if (artistContainedInSearchResults &&
                        track.getName().equalsIgnoreCase(trackName)) {
                    searchResultSpotifyId = track.getId();
                }
            }
        } catch (Exception e){
            searchResultSpotifyId = "no results found!";
        }

        return searchResultSpotifyId;

    }

    public Track getTrackFromSpotify(String trackName, String artist) throws IOException, WebApiException {

        final Api api = Api.builder()
                .clientId(clientId)
                .clientSecret(clientSecret).build();

        final ClientCredentialsGrantRequest clientCredentialsGrantRequest = api.clientCredentialsGrant().build();
        ClientCredentials clientCredentials = clientCredentialsGrantRequest.get();

        api.setAccessToken(clientCredentials.getAccessToken());

        //surrounds trackName with quotes so Spotify API can get an exact query match
        final TrackSearchRequest trackSearchRequest = api.searchTracks("\"" + trackName + "\"").limit(3).query(trackName)
                .build();

        //todo: make this so it doesn't mirror above code
        //init arraylists and return track

        ArrayList<Track> searchResultTracks = new ArrayList<>();
        searchResultTracks.addAll(trackSearchRequest.get().getItems());
        Track returnTrack;

        try {
            returnTrack = searchResultTracks.get(0);
        } catch (Exception e){
            return null;
        }

        return returnTrack;

    }

    public Song getSongFromSpotify(String trackName, String artist) throws IOException, WebApiException {

        final Api api = Api.builder()
                .clientId(clientId)
                .clientSecret(clientSecret).build();

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
        Track returnTrack = searchResultTracks.get(0);

        /*
        //todo: check if song is in DB first
        //makes new song from title, artist of search result
        try {
            for (Track track : searchResultTracks) {
                if ((track.getArtists().get(0).getName().equals(artist)
                        && track.getName().equalsIgnoreCase(trackName))) {
                    returnTrack = track;
                } else {
                    System.out.println("no exact match found!");
                }
            }
        } catch (NullPointerException npe){
            System.out.println("no results found!");
        }
        */

        Song song = new Song(returnTrack.getArtists().get(0).getName(), returnTrack.getName());
        return song;
    }

    public String createRecommendationsPlaylistUrlFromPlaylist (Playlist playlist, String name) throws IOException, WebApiException {

        List<Song> testSongs = playlist.getSongs();
        ArrayList<String> testSeeds = new ArrayList<>();

        for (Song song : testSongs){
            String spotifyId = song.getSpotifyId();
            //checks for duplicates and imposes limit of 5 because of spotify api limits
            if (!testSeeds.contains(spotifyId) && testSeeds.size() < 5){
                testSeeds.add(spotifyId);
            }
        }

        //todo: fix duplicates here!
        //todo: clean this up so it's more efficient
        List<Track> recommendationTracks = getListOfRecommendationsFromSeedTracks(testSeeds);
        String joinedIds = getCommaJoinedTrackIds(recommendationTracks);

        String recommendationsUrl = "https://embed.spotify.com/?uri=spotify:trackset:"+name+":"+joinedIds;

        return recommendationsUrl;

    }

    public ArrayList<Song> getListOfSuggestedSongsFromRecipeAndSaveToDatabase(Recipe recipe, User user) throws IOException, WebApiException {

        long now = System.currentTimeMillis();

        //gets recommendationUrl from recipe and user
        //todo: fix this so it doesn't rely on playlist service
        //todo: refactor this to use Doug's improved method system (see Slack)

        //init
        Playlist playlist = playlistService.makePlaylistFromRecipe(recipe, user);
        String recommendationUrl;

        System.out.println("****make playlist from recipe "+ (System.currentTimeMillis() - now));

        //if playlist isn't null do, otherwise return null

        if (playlist != null){
            recommendationUrl = createRecommendationsPlaylistUrlFromPlaylist(playlist, recipe.getName());
        } else {
            return null;
        }

        System.out.println("****make recommendations "+ (System.currentTimeMillis() - now));


        //splits url on "trackset:", then on comma to get array of songIds
        String recipeName = recipe.getName();
        String[] splitOnTrackset = recommendationUrl.split(recipeName+":");
        String[] suggestedSongIds = splitOnTrackset[1].split(",");

        ArrayList<Song> songs = new ArrayList<>();

        System.out.println("****post-string splitting "+ (System.currentTimeMillis() - now));

        for (String id : suggestedSongIds){
            Song song = (getSongFromSpotifyId(id));
            songs.add(song);
        }

        System.out.println("****gets song from spotify "+ (System.currentTimeMillis() - now));

        songService.tagAndSaveSongsFromRecipe(songs, recipe);

        return songs;
    }

    public Song getSongFromSpotifyId(String id) throws IOException, WebApiException {

        final Api api = Api.builder()
                .clientId(clientId)
                .clientSecret(clientSecret).build();

        final ClientCredentialsGrantRequest clientCredentialsGrantRequest = api.clientCredentialsGrant().build();
        ClientCredentials clientCredentials = clientCredentialsGrantRequest.get();

        api.setAccessToken(clientCredentials.getAccessToken());

        //surrounds trackName with quotes so Spotify API can get an exact query match
        final TrackRequest trackRequest = api.getTrack(id)
                .build();

        Track track = trackRequest.get();

        Song song = songService.convertTrackToSong(track);
        return song;
    }
}
