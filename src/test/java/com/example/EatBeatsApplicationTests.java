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
		mockMvc.perform(
				MockMvcRequestBuilders.post("/create-account")
				.param("username", "test")
				.param("password", "pass")
		);

		User fetchUser = userRepo.findFirstByUsername(testName);

		//assert
		assertThat(fetchUser.getUsername(), is("test"));
		assertThat(PasswordHasher.verifyPassword(testPass, fetchUser.getPassword()), is(true));

	}

	/**
	 * Given a recipe
	 * When recipe is submitted
	 * Then recipe appears in database
	 */

	@Test
	public void whenRecipeAddedThenRecipeStoredInDatabase() throws Exception {

		//arrange
		User testUser = new User("username", "pass");
		userRepo.save(testUser);

		Recipe recipe = new Recipe("season", "name", "category", "region", "description");

		mockHttpSession.setAttribute("username", testUser.getUsername());
		mockHttpSession.setAttribute("password", testUser.getPassword());

		//act
		mockMvc.perform(

				MockMvcRequestBuilders.post("/create-recipe")

						.sessionAttr("username", "username")
						.sessionAttr("password", "pass")
						.param("season", "season")
						.param("name", "name")
						.param("category", "category")
						.param("region", "region")
						.param("description", "description")

		);

		Recipe fetchRecipe = recipeRepo.findFirstByName("name");

		//can't compare actual recipes because of object identity separation
		//assert
		assertThat(fetchRecipe.getName(), is(recipe.getName()));
		assertThat(fetchRecipe.getDescription(), is(recipe.getDescription()));
		assertThat(fetchRecipe.getCategory(), is(recipe.getCategory()));
		assertThat(fetchRecipe.getSeason(), is(recipe.getSeason()));
		assertThat(fetchRecipe.getRegion(), is(recipe.getRegion()));
	}

	/**
	 * Given a user profile
	 * When user goes to "my recipes" page
	 * Then recipes are retrieved from database and displayed
	 */

	@Test
	public void whenMyRecipesPageAccessedThenAllUserRecipesRetrievedAndDisplayed() throws Exception {

		//arrange
		//creates new user, saves to db, sets session attributes
		User testUser = new User("username", "pass");
		userRepo.save(testUser);

		//todo: move to before
		mockHttpSession.setAttribute("username", testUser.getUsername());
		mockHttpSession.setAttribute("password", testUser.getPassword());

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
		mockMvc.perform(
				MockMvcRequestBuilders.get("/my-recipes")
						.sessionAttr("username", "username")
						.sessionAttr("password", "pass")
		);

		//act

		List<Recipe> testRecipeList = recipeRepo.findByUser(testUser);

		//assert
		//todo: use .equals
		assertThat(testRecipeList.get(0).equals(testRecipe), is(true));
		assertThat(testRecipeList.get(0).equals(testRecipe2), is(true));
		assertThat(testRecipeList.get(0).equals(testRecipe3), is(true));
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
