package com.example;

import com.google.common.base.Joiner;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.RecommendationsRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.ClientCredentials;
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
import java.util.ArrayList;
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

	@Before
	public void before() throws IOException, WebApiException, PasswordHasher.CannotPerformOperationException {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		User testUser = new User("name", "pass");
		String season = "season";
		String name = "name";
		String category = "category";
		String region = "region";
		String description = "description";
		Recipe testRecipe = new Recipe(season, name, category, region, description);
		testRecipe.setUser(testUser);
		userRepo.save(testUser);
		recipeRepo.save(testRecipe);
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

	@Test
	public void whenRecipeDeletedThenRecipeNotInDatabase(){

		//arrange
		Recipe testRecipe = recipeRepo.findFirstByName("name");
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



}
