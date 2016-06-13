package com.example;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
	WebApplicationContext webApplicationContext;

	MockMvc mockMvc;

	@Autowired
	UserRepo userRepo;

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

}
