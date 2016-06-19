package com.example;

import com.google.common.base.Joiner;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.RecommendationsRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Track;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EatBeatsApplication.class)
@WebAppConfiguration
public class EatBeatsApplicationTests {

	@Autowired
	MockHttpSession mockHttpSession;

	@Autowired
	WebApplicationContext webApplicationContext;

	MockMvc mockMvc;

	@Autowired
	UserRepo userRepo;

	@Autowired
	RecipeRepo recipeRepo;

	@Autowired
	RecipeService recipeService;

	@Autowired
	UserService userService;

	@Autowired
	SpotifyService spotifyService;

	@Autowired
	PlaylistRepo playlistRepo;

	@Autowired
	SongRepo songRepo;

	@Autowired
	PlaylistService playlistService;

	//todo: remove this and/or clean it up
	@Before
	public void before() throws IOException, WebApiException, PasswordHasher.CannotPerformOperationException {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		//make test recipe
		String season = "season";
		String name = "name";
		String category = "category";
		String region = "region";
		String description = "description";
		Recipe testRecipe = new Recipe(season, name, category, region, description);

		//make test user and set recipe to user
		User testUser = new User("name", "pass");
		testRecipe.setUser(testUser);

		//save recipe and user to repo
		userRepo.save(testUser);
		recipeRepo.save(testRecipe);

		//construct new songs
		Song testSong1 = new Song("Coldplay", "Yellow");
		Song testSong2 = new Song("Sun Ra", "Space is the Place");
		Song testSong3 = new Song("Wu-Tang Clan", "C.R.E.A.M.");

		//save new songs to repo
		songRepo.save(testSong1);
		songRepo.save(testSong2);
		songRepo.save(testSong3);

		//make song list and construct playlist
		List<Song> testSongs = Arrays.asList(testSong1, testSong2, testSong3);
		Playlist testPlaylist = new Playlist(testRecipe, testSongs, testUser);

		//save playlist to repo
		playlistRepo.save(testPlaylist);
	}

	@Test
	public void contextLoads() {
	}

	/**
	 * Given a username and password
	 * When account is created
	 * Then account is stored in database and password is correctly hashed
	 */

	@Test
	public void whenAccountCreatedUsernameAndPasswordStoredAndHashed() throws Exception {

		//arrange
		String testName = "test";
		String testPass = "pass";

		//act
		userService.createAndStoreUser(testName, testPass);
		User fetchUser = userRepo.findFirstByUsername(testName);

		//assert
		assertThat(fetchUser.getUsername(), is("test"));
		assertThat(PasswordHasher.verifyPassword(testPass, fetchUser.getPassword()), is(true));

	}

	/**
	 * Given a user profile
	 * When user goes to "my recipes" page
	 * Then all recipes belonging to that user are retrieved from database
	 */

	@Test
	public void whenMyRecipesPageAccessedThenAllUserRecipesRetrievedFromDatabase() throws Exception {

		//arrange
		//creates new user, saves to db, sets session attributes
		User testUser = new User("username", "pass");
		userRepo.save(testUser);

		Recipe testRecipe = new Recipe("season", "name", "category", "region", "description");
		Recipe testRecipe2 = new Recipe("season2", "name2", "category2", "region2", "description2");
		Recipe testRecipe3 = new Recipe("season3", "name3", "category3", "region3", "description3");

		testRecipe.setUser(testUser);
		testRecipe2.setUser(testUser);
		testRecipe3.setUser(testUser);

		recipeRepo.save(testRecipe);
		recipeRepo.save(testRecipe2);
		recipeRepo.save(testRecipe3);

		//act
		List<Recipe> testRecipeList = recipeRepo.findByUser(testUser);

		//assert
		assertThat(testRecipeList.get(0).getName().equals(testRecipe.getName()), is(true));
		assertThat(testRecipeList.get(1).getDescription().equals(testRecipe2.getDescription()), is(true));
		assertThat(testRecipeList.get(2).getSeason().equals(testRecipe3.getSeason()), is(true));
	}

	/**
	 * Given a new recipe
	 * When recipe is added to db
	 * Recipe can be retrieved from db
	 */

	@Test
	public void whenRecipeAddedThenRecipeRetrievedFromDatabase() throws PasswordHasher.CannotPerformOperationException {

		//arrange
		User user = new User("name", "pass");
		String season = "season";
		String name = "name";
		String category = "category";
		String region = "region";
		String description = "description";
		Recipe testRecipe = new Recipe(season, name, category, region, description);
		testRecipe.setUser(user);
		userRepo.save(user);

		//act
		recipeService.saveRecipe(user, season, name, category, region, description);
		Recipe fetchRecipe = recipeRepo.findFirstByName("name");

		//assert
		assertThat(fetchRecipe.getName(), is(testRecipe.getName()));
		assertThat(fetchRecipe.getDescription(), is(testRecipe.getDescription()));
		assertThat(fetchRecipe.getCategory(), is(testRecipe.getCategory()));
		assertThat(fetchRecipe.getSeason(), is(testRecipe.getSeason()));
		assertThat(fetchRecipe.getRegion(), is(testRecipe.getRegion()));

	}

	/**
	 * Given a recipe
	 * When recipe is retrieved from database and edited
	 * Then recipe is stored in database with new values
	 */

	@Test
	public void whenRecipeEditedThenNewValuesSavedInDatabase(){

		//arrange
		Recipe testRecipe = recipeRepo.findFirstByName("name");

		String editedCategory = "editedCategory";
		String editedName= "editedName";
		String editedDescription = "editedDescription";
		String editedRegion = "editedRegion";
		String editedSeason = "editedSeason";

		//act
		//edited values
		//todo: fix formatting!
		recipeService.editRecipe(testRecipe, editedCategory, editedName, editedDescription, editedRegion, editedSeason);

		recipeRepo.save(testRecipe);
		Recipe editedRecipe = recipeRepo.findFirstByName(editedName);

		//assert
		assertThat(testRecipe.getName().equals(editedRecipe.getName()), is(true));
		assertThat(testRecipe.getCategory().equals(editedRecipe.getCategory()), is(true));
		assertThat(testRecipe.getRegion().equals(editedRecipe.getRegion()), is(true));
		assertThat(testRecipe.getSeason().equals(editedRecipe.getSeason()), is(true));
		assertThat(testRecipe.getDescription().equals(editedRecipe.getDescription()), is(true));


	}

	/**
	 * Given a recipe
	 * When recipe is deleted from database
	 * Then recipe no longer appears in database
	 */

	//todo: fix bug where recipe cannot be deleted if it belongs to a playlist (or add option to let user know this!)
	//e.g. (This will delete all playlists associated with this recipe, are you sure?)
	//or: distinguish between user recipes and public recipes
	@Test
	public void whenRecipeDeletedThenRecipeNotInDatabase(){

		//arrange
		User testUser = new User();
		userRepo.save(testUser);
		Recipe testRecipe = new Recipe();
		testRecipe.setUser(testUser);
		recipeRepo.save(testRecipe);
		int testDeleteId = testRecipe.getId();

		//act
		recipeRepo.delete(testDeleteId);

		//assert
		assertThat((recipeRepo.findById(testDeleteId) == null), is(true));

	}

	/**
	 * Given a track id seed
	 * When id is used in recommendations request
	 * Then returns Track List containing random tracks
	 */

	@Test
	public void whenGivenTrackIDSeedThenRandomRecommendationsReturned() throws IOException, WebApiException {

		//arrange
		String testId = "55PqUrPAZ67MYPvTptskA4";
		ArrayList<String> testSeeds = new ArrayList<>();
		testSeeds.add(testId);

		//act
		//gets two different track lists to ensure each is random
		List<Track> testTracks = spotifyService.getListOfRecommendationsFromSeedTracks(testSeeds);
		List<Track> testTracks2 = spotifyService.getListOfRecommendationsFromSeedTracks(testSeeds);

		boolean test1 = false;
		for (Track track : testTracks){
			if (    (track.getAlbum() != null) &&
					(track.getName() != null) &&
					(track.getArtists() != null) &&
					(track.getDiscNumber() != 0) &&
					(track.getPopularity() != 0)
					){
				test1 = true;
			}
		}

		boolean test2 = false;
		for (Track track : testTracks2){
			if (    (track.getAlbum() != null) &&
					(track.getName() != null) &&
					(track.getArtists() != null) &&
					(track.getDiscNumber() != 0) &&
					(track.getPopularity() != 0)
					){
				test2 = true;
			}
		}

		String testTrack1Id = testTracks.get(0).getId();
		String testTrack2Id = testTracks2.get(0).getId();

		//assert
		//tests that each track list contains tracks
		assertThat((test1 && test2), is(true));
		//tests for randomness
		assertThat((testTrack1Id.equals(testTrack2Id)), is(false));

	}

	//todo: put test for building playlist with specific track ids here

	/**
	 * Given a list of tracks
	 * When list is passed in as argument
	 * Then method returns a string of comma separated track ids
	 */

	@Test
	public void whenGivenTrackListThenCommaSeparatedStringOfTrackIdsReturned() throws IOException, WebApiException {

		//arrange
		String testId = "55PqUrPAZ67MYPvTptskA4";
		ArrayList<String> testSeeds = new ArrayList<>();
		testSeeds.add(testId);
		List<Track> testTracks = spotifyService.getListOfRecommendationsFromSeedTracks(testSeeds);

		ArrayList<String> tracksToJoin = new ArrayList<>();

		//does this test have a point? It's just duplicating its own method
		for (Track track : testTracks){
			if (track.getId() != null){
				tracksToJoin.add(track.getId());
			}
		}

		//act
		String testTrackIds = spotifyService.getCommaJoinedTrackIds(testTracks);
		String joinedTracksIds = Joiner.on(",").join(tracksToJoin);


		//assert
		assertThat(testTrackIds.equals(joinedTracksIds), is (true));

	}

	/**
	 * Given a song title
	 * When title is searched through spotify
	 * Then top 3 search results are returned
	 */

	@Test
	public void whenSongTitleSearchedThenTopThreeSearchResultsReturned() throws IOException, WebApiException {

		//arrange
		String testSongName = "space is the place";
		String testArtist = "Sun Ra";
		String expectedId = "0JOKubEAJYJSh9DQ0hDDQq";

		//act
		String searchResultId = spotifyService.searchByTrackName(testSongName, testArtist);

		//assert
		assertThat(searchResultId.equals(expectedId), is (true));

	}

	/**
	 * Given a list of songs
	 * When playlist is constructed with said songs, saved to database, and retrieved from db
	 * Then playlist contains all expected songs
	 */

	@Test
	public void whenPlaylistBuiltAndSavedThenPlaylistContainsCorrectSongs() throws IOException, WebApiException {

		//arrange
		//todo: move this to @before method (do this by creating objects AND adding them to database, then pulling them out in test)
		Song testSong1 = new Song("Coldplay", "Yellow");
		Song testSong2 = new Song("Sun Ra", "Space is the Place");
		Song testSong3 = new Song("Wu-Tang Clan", "C.R.E.A.M.");

		songRepo.save(testSong1);
		songRepo.save(testSong2);
		songRepo.save(testSong3);

		Recipe testRecipe = new Recipe();
		testRecipe.setName("Coq Au Vin");
		User testUser = new User();
		testRecipe.setUser(testUser);
		userRepo.save(testUser);
		recipeRepo.save(testRecipe);

		List<Song> songs = Arrays.asList(testSong1, testSong2, testSong3);

		ArrayList<String> songIdsToBeAdded = new ArrayList<>();
		songIdsToBeAdded.add(testSong1.getSpotifyId());
		songIdsToBeAdded.add(testSong2.getSpotifyId());
		songIdsToBeAdded.add(testSong3.getSpotifyId());

		Playlist playlist = new Playlist(testRecipe, songs, testUser);

		//joins ids together for assertion check
		String joinedIdsToBeAdded = Joiner.on(",").join(songIdsToBeAdded);

		//act
		playlistRepo.save(playlist);
		Playlist retrievedPlaylist = playlistRepo.findById(playlist.getId());

		//String joinedRetrievedPlaylistIds = Joiner.on(",").join(retrievedPlaylist.getSongSpotifyIds());

		//assert
		assertThat(songs.equals(playlist.getSongs()), is(true));
	}

	/**
	 * Given a list of songs
	 * When Playlist is constructed with list
	 * Then Playlist contains correct spotify playlist link
	 */

	@Test
	public void whenPlaylistConstructedThenCorrectSpotifyPlaylistLinkContainedInPlaylist() throws IOException, WebApiException {

		//arrange
		//remember: artist and title have to be in CORRECT ORDER when songs are created!
		//todo: add a "fuzzy" search for song creation/track lookup
		Song testSong1 = new Song("Coldplay", "Yellow");
		Song testSong2 = new Song("Sun Ra", "Space is the Place");
		Song testSong3 = new Song("Wu-Tang Clan", "C.R.E.A.M.");

		Recipe testRecipe = new Recipe();
		testRecipe.setName("Coq Au Vin");
		User testUser = new User();
		userRepo.save(testUser);
		testRecipe.setUser(testUser);
		recipeRepo.save(testRecipe);

		List<Song> songsToBeAdded = Arrays.asList(testSong1, testSong2, testSong3);

		//constructs playlist
		ArrayList<String> songIdsToBeAdded = new ArrayList<>();
		songIdsToBeAdded.add(testSong1.getSpotifyId());
		songIdsToBeAdded.add(testSong2.getSpotifyId());
		songIdsToBeAdded.add(testSong3.getSpotifyId());

		ArrayList<String> testSongIds = new ArrayList<>();

		//gets spotify id of each song and adds to arraylist
		for (String id : songIdsToBeAdded){
			testSongIds.add(id);
		}

		String joinedIds = Joiner.on(",").join(testSongIds);

		String expectedUrl = "https://embed.spotify.com/?uri=spotify:trackset:"+testRecipe.getName()+":"+joinedIds;

		//act
		Playlist playlist = new Playlist(testRecipe, songsToBeAdded, testUser);
		String testUrl = playlist.getSpotifyLink();

		//assert
		assertThat(testUrl.equals(expectedUrl), is(true));

	}

	/**
	 * Given a song title and artist
	 * When Song is created
	 * Then Song contains correct spotify id
	 */

	@Test
	public void whenSongCreatedThenSongHasCorrectSpotifyId() throws IOException, WebApiException {

		//arrange
		String testArtist = "Coldplay";
		String testTitle = "Yellow";
		String expectedId = "3AJwUDP919kvQ9QcozQPxg";

		//act
		Song testSong1 = new Song(testArtist, testTitle);

		//assert
		assertThat(expectedId.equals(testSong1.getSpotifyId()), is(true));

	}

	//todo: make and test method for retrieving all of user's playlists

	/**
	 * Given a user
	 * When user's playlists are retrieved from database
	 * Then playlists are successfully retrieved
	 */

	@Test
	public void whenUserGivenThenPlaylistsRetrievedFromDatabase() throws IOException, WebApiException, PasswordHasher.CannotPerformOperationException {

		//arrange
		User testUser = new User("name", "pass");

		userRepo.save(testUser);
		List<Playlist> testPlaylists = new ArrayList<>();

		boolean isAPlaylist = true;

		//two of everything is necessary for testing multiple playlists
		//todo: fix this by moving to @before! (see above note)
		Song testSong1 = new Song("Coldplay", "Yellow");
		Song testSong2 = new Song("Sun Ra", "Space is the Place");
		Song testSong3 = new Song("Wu-Tang Clan", "C.R.E.A.M.");
		Song testSong4 = new Song("Wu-Tang Clan", "Gravel Pit");

		songRepo.save(testSong1);
		songRepo.save(testSong2);
		songRepo.save(testSong3);
		songRepo.save(testSong4);

		Recipe testRecipe = new Recipe();
		testRecipe.setName("Coq Au Vin");
		testRecipe.setUser(testUser);
		Recipe testRecipe2 = new Recipe();
		testRecipe2.setName("Ortolan");
		testRecipe2.setUser(testUser);

		recipeRepo.save(testRecipe);
		recipeRepo.save(testRecipe2);

		List<Song> songs = Arrays.asList(testSong1, testSong2);
		List<Song> songs2 = Arrays.asList(testSong3, testSong4);

		ArrayList<String> songIdsToBeAdded = new ArrayList<>();
		ArrayList<String> songIdsToBeAdded2 = new ArrayList<>();
		songIdsToBeAdded.add(testSong1.getSpotifyId());
		songIdsToBeAdded.add(testSong2.getSpotifyId());
		songIdsToBeAdded2.add(testSong3.getSpotifyId());
		songIdsToBeAdded2.add(testSong4.getSpotifyId());

		//todo: refactor Playlist constructor so it takes songs, not song ids
		Playlist testPlaylist = new Playlist(testRecipe, songs, testUser);
		Playlist testPlaylist2 = new Playlist(testRecipe2, songs2, testUser);

		playlistRepo.save(testPlaylist);
		playlistRepo.save(testPlaylist2);

		//act
		testPlaylists = playlistRepo.findByUser(testUser);

		//assert
		//tests if every object in testPlaylists is a playlist
		for (Playlist playlist : testPlaylists){
			if (playlist.getClass() != Playlist.class){
				isAPlaylist = false;
			}
		}

		//tests if the list retrieved by repo isn't empty (may be unnecessary because of null pointer exception)
		assertThat(!testPlaylists.isEmpty(), is(true));
		assertThat(isAPlaylist, is(true));

	}

	/**
	 * Given a playlist
	 * When playlist is created
	 * Then all songs in playlist have the same tags as the recipe used to construct the playlist
	 */

	@Test
	public void whenPlaylistConstructedThenSongsHaveSameTagsAsRecipe() throws IOException, WebApiException {

		//arrange
		User testUser = new User();
		userRepo.save(testUser);
		Song testSong1 = new Song("Coldplay", "Yellow");
		Song testSong2 = new Song("Sun Ra", "Space is the Place");

		songRepo.save(testSong1);
		songRepo.save(testSong2);

		Recipe testRecipe = new Recipe();
		testRecipe.setName("Coq Au Vin");
		testRecipe.setCategory("Entree");
		testRecipe.setDescription("description");
		testRecipe.setSeason("Spring");
		testRecipe.setRegion("French");
		testRecipe.setUser(testUser);

		recipeRepo.save(testRecipe);

		//holds tags in recipe
		ArrayList<String> testRecipeTags = new ArrayList<>();
		testRecipeTags.add(testRecipe.getCategory());
		//testRecipeTags.add(testRecipe.getDescription());
		//testRecipeTags.add(testRecipe.getName());
		testRecipeTags.add(testRecipe.getRegion());
		testRecipeTags.add(testRecipe.getSeason());

		List<Song> songsToBeAdded = Arrays.asList(testSong1, testSong2);

		//act
		Playlist testPlaylist = new Playlist(testRecipe, songsToBeAdded, testUser);

		playlistRepo.save(testPlaylist);

		//adds tags from songs here
		ArrayList<String> testSongTags = new ArrayList<>();
		ArrayList<String> testSongTags2 = new ArrayList<>();
		testSongTags.addAll(testPlaylist.getSongs().get(0).getTags());
		testSongTags2.addAll(testPlaylist.getSongs().get(1).getTags());

		//assert
		boolean matchingTags = true;

		for (String tag: testRecipeTags){
			if (!testSongTags.contains(tag)||!testSongTags2.contains(tag)){
				matchingTags = false;
			}
		}

		assertThat(matchingTags, is(true));

	}

	//todo: make and test method for making playlist based on song tags

	/**
	 * Given a recipe
	 * When spotify playlist is created for recipe using recommendations request
	 * Then all songs in Playlist have same tags as recipe
	 */

	@Test
	public void whenGivenRecipeThenPlaylistCreatedFromRecipeTags() throws IOException, WebApiException {

		//arrange
		User testUser = userRepo.findFirstByUsername("name");
		Recipe testRecipe = recipeRepo.findFirstByName("name");
		Song testSong = new Song("artist", "title");

		ArrayList<String> testRecipeTags = new ArrayList<>();
		testRecipeTags.add(testRecipe.getCategory());
		testRecipeTags.add(testRecipe.getRegion());
		testRecipeTags.add(testRecipe.getSeason());

		testSong.setTags(testRecipeTags);
		testSong.setCategory(testRecipe.getCategory());
		songRepo.save(testSong);

		//act
		Playlist testPlaylist = playlistService.makePlaylistFromRecipe(testRecipe, testUser);
		List<Song> testSongs = testPlaylist.getSongs();

		//assert

		boolean doTagsMatch = true;

		for (Song song : testSongs){
			for (String tag : song.getTags()){
				if (!testRecipeTags.contains(tag)){
					doTagsMatch = false;
				}
			}
		}

		testPlaylist.getSpotifyLink();

		assertThat(doTagsMatch, is(true));
		assertThat(testPlaylist.getSongs().isEmpty(), is(false));

	}



	//todo: figure out how and when songs are tagged (must relate to recipe somehow)
	/* we already have recipes as a necessary part of the Playlist constructor, we can just
	tag each song in the playlist with the tags on the recipe, then add all songs to the db */

}
