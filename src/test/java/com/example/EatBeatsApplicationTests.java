package com.example;

import com.google.common.base.Joiner;
import com.wrapper.spotify.exceptions.WebApiException;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
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
	SongService songService;

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
			Recipe testRecipe2 = new Recipe("season2", "name2", "category2", "region2", "description2");

			//make test user and set recipe to user
			User testUser = new User("name", "pass");
			testRecipe.setUser(testUser);
			testRecipe2.setUser(testUser);

			//save recipe and user to repo
			userRepo.save(testUser);
			recipeRepo.save(testRecipe);
			recipeRepo.save(testRecipe2);

			//construct new songs
			Song testSong1 = new Song("Coldplay", "Yellow");
			Song testSong2 = new Song("Sun Ra", "Space is the Place");
			Song testSong3 = new Song("Wu-Tang Clan", "C.R.E.A.M.");
			Song testSong4 = new Song("Wu-Tang Clan", "Gravel Pit");

			//use this for exception testing
			//Song testSong5 = new Song("MF DOOM", "Saffron");

			//make song list and save to db
			List<Song> testSongs = Arrays.asList(testSong1, testSong2);
			List<Song> testSongs2 = Arrays.asList(testSong3, testSong4);
			songService.tagAndSaveSongsFromRecipe(testSongs, testRecipe);
			songService.tagAndSaveSongsFromRecipe(testSongs2, testRecipe2);

			//construct playlist
			Playlist testPlaylist = new Playlist(testRecipe, testSongs, testUser);
			Playlist testPlaylist2 = new Playlist(testRecipe, testSongs2, testUser);

			//save playlist to db

			if (playlistRepo.findById(1) == null){
				playlistRepo.save(testPlaylist);
			}

			if (playlistRepo.findById(2) == null){
				playlistRepo.save(testPlaylist2);
			}

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
		//todo: refactor to "searchByTrackNameAndArtist"
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
		Song testSong1 = songRepo.findByNameIgnoreCase("yellow");
		Song testSong2 = songRepo.findByNameIgnoreCase("space is the place");
		Song testSong3 = songRepo.findByNameIgnoreCase("C.R.E.A.M.");

		Recipe testRecipe = recipeRepo.findFirstByName("name");
		testRecipe.setName("Coq Au Vin");
		User testUser = userRepo.findFirstByUsername("name");
		//testRecipe.setUser(testUser);
		//userRepo.save(testUser);
		//recipeRepo.save(testRecipe);

		List<Song> songs = Arrays.asList(testSong1, testSong2, testSong3);

		ArrayList<String> songIdsToBeAdded = new ArrayList<>();
		songIdsToBeAdded.add(testSong1.getSpotifyId());
		songIdsToBeAdded.add(testSong2.getSpotifyId());
		songIdsToBeAdded.add(testSong3.getSpotifyId());

		Playlist playlist = new Playlist(testRecipe, songs, testUser);

		//todo: clean this up (most of it is greyed out)
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

		//tests casing
		Song testSong1 = songRepo.findByNameIgnoreCase("yellow");
		Song testSong2 = songRepo.findByNameIgnoreCase("space is the place");
		Song testSong3 = songRepo.findByNameIgnoreCase("C.r.E.A.m.");

		Recipe testRecipe = recipeRepo.findFirstByName("name");
		//testRecipe.setName("Coq Au Vin");
		User testUser = userRepo.findFirstByUsername("name");
		//userRepo.save(testUser);
		//testRecipe.setUser(testUser);
		//recipeRepo.save(testRecipe);

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
		User testUser = userRepo.findFirstByUsername("name");

		//userRepo.save(testUser);
		//List<Playlist> testPlaylists;

		boolean isAPlaylist = true;

		/*
		//two of everything is necessary for testing multiple playlists
		Song testSong1 = songRepo.findByNameIgnoreCase("yellow");
		Song testSong2 = songRepo.findByNameIgnoreCase("space is the place");
		Song testSong3 = songRepo.findByNameIgnoreCase("C.R.E.a.M.");
		Song testSong4 = songRepo.findByNameIgnoreCase("gravel pit");

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
		*/

		//act
		List <Playlist> testPlaylists = playlistRepo.findByUser(testUser);

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

	//todo: fix bug: playlists with same songs can't be added
	@Test
	public void whenPlaylistConstructedThenSongsHaveSameTagsAsRecipe() throws IOException, WebApiException, PasswordHasher.CannotPerformOperationException {

		//arrange
		User testUser1 = new User("uname", "utest");
		userRepo.save(testUser1);
		Song testSong1 = new Song ("Coldplay", "Parachutes");
		Song testSong2 = new Song ("Coldplay", "The Scientist");
		songRepo.save(testSong1);
		songRepo.save(testSong2);

		Recipe testRecipe = new Recipe();
		testRecipe.setName("Coq Au Vin");
		testRecipe.setCategory("Entree");
		testRecipe.setDescription("description");
		testRecipe.setSeason("Spring");
		testRecipe.setRegion("French");
		testRecipe.setUser(testUser1);

		recipeRepo.save(testRecipe);

		//holds tags in recipe
		ArrayList<String> testRecipeTags = new ArrayList<>();
		testRecipeTags.add(testRecipe.getCategory());
		testRecipeTags.add(testRecipe.getRegion());
		testRecipeTags.add(testRecipe.getSeason());

		List<Song> songsToBeAdded = Arrays.asList(testSong1, testSong2);

		//act
		Playlist testPlaylist3 = new Playlist(testRecipe, songsToBeAdded, testUser1);

		playlistRepo.save(testPlaylist3);

		//adds tags from songs here
		ArrayList<String> testSongTags = new ArrayList<>();
		ArrayList<String> testSongTags2 = new ArrayList<>();
		testSongTags.addAll(testPlaylist3.getSongs().get(0).getTags());
		testSongTags2.addAll(testPlaylist3.getSongs().get(1).getTags());

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

	/**
	 * Given a title and artist
	 * When title and artist are searched on Spotify
	 * Then Song is returned with correct title, artist, and spotify id
	 */

	@Test
	public void whenArtistAndTitleAreSearchedOnSpotifyThenCorrectSongIsReturned() throws IOException, WebApiException {

		//arrange
		//todo: add artist field to Song
		String testArtist = "Coldplay";
		String testTitle = "Yellow";
		String expectedId = "3AJwUDP919kvQ9QcozQPxg";

		//act
		Song testSong = spotifyService.getSongFromSpotify(testTitle, testArtist);

		//assert
		assertThat(testSong.getName().equals(testTitle), is(true));
		assertThat(testSong.getSpotifyId().equals(expectedId), is(true));

	}

	/**
	 * Given a playlist
	 * When recommendations request is made from playlist
	 * Then correct recommendations request is created
	 */


	@Test
	//treats entire test method as if it exists in a single Hibernate session (or so we think!)
	@Transactional
	public void whenPlaylistCreatedThenRecommendationsRequestIsCreatedAndRecommendationsRequestUrlIsCorrect() throws IOException, WebApiException {

		//arrange
		Playlist testPlaylist = playlistRepo.findById(1);
		String name = "name";
		Song testSong1 = songRepo.findByNameIgnoreCase("yellow");
		Song testSong2 = songRepo.findByNameIgnoreCase("space is the place");
		List<Song> testSongs = Arrays.asList(testSong1, testSong2);
		testPlaylist.setSongs(testSongs);

		//act
		String playlistUrl = spotifyService.createRecommendationsPlaylistUrlFromPlaylist(testPlaylist, name);

		//assert
		assertThat(playlistUrl.startsWith("https://embed.spotify.com/?uri=spotify:trackset:"), is(true));
		assertThat(playlistUrl.equals("https://embed.spotify.com/?uri=spotify:trackset:"), is(false));

	}

	/**
	 * Given a category, region, and season
	 * When database is queried with these (above) parameters
	 * Then all songs returned have all three of these (above) parameters
	 */

	@Test
	public void whenDatabaseQueriedWithCategoryRegionAndSeasonThenSongsReturnedHaveGivenParameters(){

		//arrange
		//see @before method

		//act
		List<Song> testSongs = songRepo.findByCategoryAndRegionAndSeason("category", "region", "season");

		//assert
		boolean matchingTags = true;
		for (Song song : testSongs){

			//todo: formatting!
			if (
			!song.getCategory().equals("category") ||
			!song.getRegion().equals("region") ||
			!song.getSeason().equals("season")
			) {
				matchingTags = false;
			}
		}

		assertThat(matchingTags, is(true));

	}

	/**
	 * Given a list of songs and a recipe
	 * When songs are assigned tags from recipe
	 * Then songs have correct tags
	 */

	@Test
	public void whenGivenSongsAndRecipeThenSongsHaveCorrectTags(){

		//arrange
		//also tests whether songs can be searched with wrong casing
		Song testSong1 = songRepo.findByNameIgnoreCase("yellow");
		Song testSong2 = songRepo.findByNameIgnoreCase("c.r.e.a.m.");
		Song testSong3 = songRepo.findByNameIgnoreCase("space is the place");
		Recipe testRecipe = recipeRepo.findFirstByName("name");

		List<Song> testSongs = Arrays.asList(testSong1, testSong2, testSong3);

		//act
		List<Song> taggedSongs = songService.tagAndSaveSongsFromRecipe(testSongs, testRecipe);

		//assert
		boolean matchingTags = true;
		for (Song song : taggedSongs){

			//todo: formatting!
			if (
					!song.getCategory().equals("category") ||
							!song.getRegion().equals("region") ||
							!song.getSeason().equals("season")
					) {
				matchingTags = false;
			}
		}

		assertThat(matchingTags, is(true));

	}

	//todo: test behavior for when playlist created is empty

	/**
	 * Given a playlist
	 * When playlist contains zero songs
	 * Then playlist is not saved to database
	 */

	@Test
	public void whenPlaylistContainsZeroSongsThenNotSavedToDatabase(){

		//arrange
		User testUser = userRepo.findFirstByUsername("name");
		Recipe testRecipe = recipeRepo.findById(1);
		ArrayList<Song> emptySongs = new ArrayList<>();

		//act
		Playlist testPlaylist = new Playlist(testRecipe, emptySongs, testUser);
		int testId = testPlaylist.getId();
		playlistRepo.save(testPlaylist);

		//assert
		assertThat(playlistRepo.findById(testId) == null, is(true));

	}

	/**
	 * Given two duplicate songs
	 * When songs are added to db
	 * Then only one instance of song is returned upon query
	 */

	@Test
	public void whenDuplicateSongsAddedToDBThenOnlyOneSongReturned(){

		//arrange
		Song testSong = songRepo.findByNameIgnoreCase("yellow");
		Song testSong2 = songRepo.findByNameIgnoreCase("yellow");

		//act
		songRepo.save(testSong);
		songRepo.save(testSong2);

		//act
		assertThat(songRepo.findByNameIgnoreCase("Yellow").getName(), is("Yellow"));
	}

	/**
	 * Given two playlists with identical lists of songs
	 * When playlists are saved to db
	 * Then no errors thrown
	 */


	//todo: find way to save playlists with duplicate songlists
	@Test
	public void whenTwoPlaylistsWithIdenticalSongsSavedThenNoErrorsThrown() throws PasswordHasher.CannotPerformOperationException {

		//arrange
		Song testSong1 = songRepo.findByNameIgnoreCase("yellow");
		Song testSong2 = songRepo.findByNameIgnoreCase("gravel pit");
		List<Song> testSongs = Arrays.asList(testSong1, testSong2);
		Recipe testRecipe = recipeRepo.findById(1);
		User testUser = userRepo.findFirstByUsername("name");
		User testUser2 = new User("name2", "pass2");
		userRepo.save(testUser2);
		Recipe testRecipe2 = recipeRepo.findById(2);
		Playlist testPlaylist1 = new Playlist(testRecipe, testSongs, testUser);
		Playlist testPlaylist2 = new Playlist(testRecipe2, testSongs, testUser2);
		boolean completedTest = true;

		//act
		playlistRepo.save(testPlaylist1);
		playlistRepo.save(testPlaylist2);

		//assert
		//always true because we're testing to see if test completes without errors
		assertThat(completedTest, is(true));

	}

	/**
	 * Given a playlist
	 * When playlist is saved as favorite for user
	 * Then playlist appears in user playlist list
	 */

	@Test
	public void whenPlaylistAddedAsFavoriteThenPlaylistSavedInUser(){

		//arrange
		User testUser = userRepo.findFirstByUsername("name");
		Playlist testPlaylist = playlistRepo.findByUser(testUser).get(0);

		//act
		userService.saveFavoritePlaylist(testUser, testPlaylist);

		//assert
		assertThat(testUser.getFavoritePlaylists().contains(testPlaylist), is(true));

	}

	/**
	 * Given a song
	 * When song preview is requested through Spotify
	 * Then correct song preview url is returned
	 */

	@Test
	public void whenSongPreviewRequestedThenCorrectUrlReturned() throws IOException, WebApiException {

		//arrange
		Song song = songRepo.findByNameIgnoreCase("yellow");
		String expectedUrl = "https://p.scdn.co/mp3-preview/c119ca773ef844108da21c4679dc54c007cf0926";

		//act
		String previewUrl = songService.getSongPreviewUrl(song);

		//assert
		assertThat(expectedUrl.equals(previewUrl), is(true));

	}

	/**
	 * Given a recipe
	 * When recipe tags are used to retrive a suggested playlist url
	 * Then list of songs is returned and added to DB and all songs share tags with recipe
	 */

	@Test
	public void whenRecipeUsedToGetSuggestedUrlThenListOfSongsReturnedAndAddedToDBAndAllSongsShareTagsWithRecipe() throws IOException, WebApiException {

		//arrange
		Recipe testRecipe = recipeRepo.findFirstByName("name");
		User testUser = userRepo.findFirstByUsername("name");

		//act
		ArrayList<Song> testSongs = new ArrayList<>();
		testSongs = spotifyService.getListOfSuggestedSongsFromRecipeAndSaveToDatabase(testRecipe, testUser);

		//assert
		//tests each song added in @before and sees if it's the same song as each song in testSongs
		boolean addedToDBInBeforeMethod = false;
		boolean recipeTagsMatchSongTags = true;
		boolean songHasEmptyTagsList = false;

		for (Song song : songRepo.findAll()){
			if (testSongs.contains(song)){
				addedToDBInBeforeMethod = true;
			}
		}

		for (Song song : testSongs){
			for (String tag : song.getTags()){
				if (!testRecipe.getTags().contains(tag)){
					recipeTagsMatchSongTags = false;
				}
			}
		}

		for (Song song : testSongs){
			if (song.getTags().size() == 0){
				songHasEmptyTagsList = true;
			}
		}

		assertThat(testSongs.isEmpty(), is(false));
		assertThat(addedToDBInBeforeMethod, is(false));
		assertThat(recipeTagsMatchSongTags, is(true));
		assertThat(songHasEmptyTagsList, is(false));

	}

	/**
	 * Given a spotify playlist url
	 * When url is passed into playlist service method
	 * Then Playlist is constructed from url
	 */

	@Test
	public void whenGivenPlaylistUrlThenPlaylistConstructed() throws IOException, WebApiException {

		//arrange
		User testUser = userRepo.findFirstByUsername("name");
		Recipe testRecipe = recipeRepo.findFirstByName("name");
		String testUrl = "https://embed.spotify.com/?uri=spotify:trackset:name:0Uk9bW60QCv6jed1ZZmMci,7vdk9Uneu2PHxurWSdyM1l,4RWptQk4Q2wIw1FqnvazPJ,0buHhiv1hy4rvey8dXySPb,6qNWmjlMAW503WLZLfjUba,2ca5QQcYbR3qvbcjyVonQL,2XkXuuH2oF1SIA4Z62f5QJ,57kW8lv4E2PwYot87eZ42h,3OOISpk4jowR2WEiA4il7b,7bW7G8UDLw8lyZRIlHnKS8,3m1OBsAL14iXPeGIvwe6Ly,4shwvoJGExMDnvwvWcCZDy,4nKj5XllRENTREzT7G6E6D,2EgJi1CKzLgsbSRH7A5lcA,1hrGazFzcxeYXkLAee9SE1,0oFb23xIwILgr9BOZFMYWA,5UeXJ3Gg50YmHxn838NiZp,5u37dzOTaofExiyOlYir65,6BMLponrLSBFBSqgM0012P,2tNRbOjxMj7FAdOBfKjmch";

		//act
		Playlist testPlaylist = playlistService.makePlaylistFromPlaylistUrl(testUrl, testRecipe, testUser);

		//assert
		assertThat(testPlaylist.getSpotifyLink().equals(testUrl), is(true));
		assertThat(testPlaylist.getSongs().isEmpty(), is(false));


	}

	/**
	 * Given a User and Playlist
	 * When playlist is saved to User object, User saved to database and then retrieved
	 * Then playlist is contained within retrieved User
	 */

	@Test
	public void whenUserSavedWithPlaylistThenUserRetrievedFromDatabaseWithPlaylist() throws PasswordHasher.CannotPerformOperationException {

		//arrange
		User testUser = new User("test2", "pass");
		Playlist testPlaylist = playlistRepo.findById(1);

		testPlaylist.setUser(testUser);

		//act
		userRepo.save(testUser);
		playlistRepo.save(testPlaylist);
		User retrievedUser = userRepo.findFirstByUsername("test2");

		//assert
		assertThat(retrievedUser.getFavoritePlaylists().get(0).getSpotifyLink().equals(testPlaylist.getSpotifyLink()), is(true));

	}

}
