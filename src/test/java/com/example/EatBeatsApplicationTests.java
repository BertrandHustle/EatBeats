package com.example;

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

	@Before
	public void before() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
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

}
