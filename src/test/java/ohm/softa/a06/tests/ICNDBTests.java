package ohm.softa.a06.tests;

import ohm.softa.a06.ICNDBApi;
import ohm.softa.a06.JokeAdapter;
import ohm.softa.a06.model.Joke;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Kurfer
 * Created on 11/10/17.
 */
class ICNDBTests {

	private static final Logger logger = LogManager.getLogger(ICNDBTests.class);
	private static final int REQUEST_COUNT = 10;
	private ICNDBApi icndbApi;

	@BeforeEach
	void setup() {
		Gson gson = new GsonBuilder()
			.registerTypeAdapter(Joke.class, new JokeAdapter())
			//.registerTypeAdapter(Joke[].class, new JokeArrayAdapter())
			.create();

		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create(gson))
			.baseUrl("http://api.icndb.com")
			.build();

		icndbApi = retrofit.create(ICNDBApi.class);
	}

	/*@Test
	void testCollision1() throws IOException {
		Set<Integer> jokeNumbers = new HashSet<>();
		int requests = 0;
		boolean collision = false;

		while (requests++ < REQUEST_COUNT) {
			// Joke j = null;
			// TEST GSON:
			Gson gson = new Gson();
			*//* String[] s = new String[]{"nerdy"};
			Joke j = new Joke(1, "Ghosts are actually caused by Chuck Norris killing people faster than Death can process them.", s);
			System.out.println(gson.toJson(j)); *//*

			// JSON String --> Object
			Joke j = gson.fromJson("{\"id\": 0, \"joke\": \"Haha.\"}", Joke.class);
			// categories remains `null`
			// Objec --> JSON String
			System.out.println(j);
			System.out.println(gson.toJson(j));


			if(jokeNumbers.contains(j.getNumber())) {
				logger.info(String.format("Collision at joke %s", j.getNumber()));
				collision = true;
				break;
			}

			jokeNumbers.add(j.getNumber());
			logger.info(j.toString());
		}

		assertTrue(collision, String.format("Completed %d requests without collision; consider increasing REQUEST_COUNT", requests));
	}*/

	@Test
	void testCollision() throws IOException {
		Set<Integer> jokeNumbers = new HashSet<>();
		int requests = 0;
		boolean collision = false;

		while (requests++ < REQUEST_COUNT) {
			// TODO Prepare call object
			// TODO Perform a synchronous request
			// TODO Extract object
			Call<Joke> jokeCall = icndbApi.getRandomJoke();
			Response<Joke> jokeResponse = jokeCall.execute();
			if(!jokeResponse.isSuccessful()) continue;
			Joke j = jokeResponse.body();

			if(jokeNumbers.contains(j.getNumber())) {
				logger.info(String.format("Collision at joke %s", j.getNumber()));
				collision = true;
				break;
			}

			jokeNumbers.add(j.getNumber());
			logger.info(j.toString());
		}

		assertTrue(collision, String.format("Completed %d requests without collision; consider increasing REQUEST_COUNT", requests));
	}

	@Test
	void testGetRandomJokeWithChangedName() throws IOException {
		Joke j = icndbApi.getRandomJoke("Bruce", "Wayne").execute().body();
		assertNotNull(j);
		assertFalse(j.getContent().contains("Chuck"));
		assertFalse(j.getContent().contains("Norris"));
		logger.info(j.toString());
	}

	@Test
	void testGetMultipleRandomJokes() throws IOException {
		List<Joke> jokes = icndbApi.getRandomJokes(5).execute().body();
		assertNotNull(jokes);
		assertEquals(5, jokes.size());

		for(Joke j : jokes) {
			logger.info(j.toString());
		}
	}

	@Test
	void testGetMultipleRandomJokesWithChangedName() throws IOException {
		List<Joke> jokes = icndbApi.getRandomJokes(5, "Bruce", "Wayne").execute().body();
		assertNotNull(jokes);
		assertEquals(5, jokes.size());

		for(Joke j : jokes) {
			assertFalse(j.getContent().contains("Chuck"));
			assertFalse(j.getContent().contains("Norris"));
			logger.info(j.toString());
		}
	}

	@Test
	void testGetMultipleRandomJokesWithCategoriesFilter() throws IOException {
		List<Joke> jokes = icndbApi.getRandomJokes(5, new String[]{"nerdy"}).execute().body();

		assertNotNull(jokes);
		assertEquals(5, jokes.size());

		for(Joke j : jokes) {
			boolean containedNerdy = false;
			for(String rubric : j.getRubrics()) {
				/* shorthand for containedNerdy = containedNerdy | rubric.equals("nerdy");  */
				containedNerdy |= rubric.equals("nerdy");
			}
			assertTrue(containedNerdy);
			logger.info(j.toString());
		}
	}

/*	@Test
	void testGetMultipleRandomJokesWithCategoriesFilterAndChangedName() throws IOException {
		List<Joke> jokes = icndbApi.getRandomJokes(5, new String[]{"nerdy"}, "Bruce", "Wayne").execute().body();

		assertNotNull(jokes);
		assertEquals(5, jokes.size());

		for(Joke j : jokes) {
			assertFalse(j.getContent().contains("Chuck"));
			assertFalse(j.getContent().contains("Norris"));
			assertTrue(j.getRubrics().stream().anyMatch(r -> r.equals("nerdy")));
			logger.info(j.toString());
		}
	}*/

	@Test
	void testGetJokeById() throws IOException {

		List<Integer> randomIds = new ArrayList<>(10);

		for(int i = 0; i < 10; i++) {
			randomIds.add(icndbApi.getRandomJoke().execute().body().getNumber());
		}

		for(Integer id : randomIds) {
			Joke j = icndbApi.getJoke(id).execute().body();
			assertNotNull(j);
			assertTrue(randomIds.contains(j.getNumber()));
			logger.info(j.toString());
		}
	}
}
