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

						//.session(mockHttpSession)
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

}
