package com.example;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.RecommendationsRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.ClientCredentials;
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
}
