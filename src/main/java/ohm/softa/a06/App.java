package ohm.softa.a06;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ohm.softa.a06.model.ApiResponse;
import ohm.softa.a06.model.Joke;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Peter Kurfer
 * Created on 11/10/17.
 */
public class App{

	// TODO fetch a random joke and print it to STDOUT
	// Modify the main method in the App class to create an instance of the ICNDBApi using Retrofit.Builder.
	// You need to add a converter factory that helps converting the JSON response to an object;
	// you can set Gson using GsonConverterFactory.create().
	public static void main(String[] args) throws IOException {

		Gson gson = new GsonBuilder()
			.registerTypeAdapter(Joke.class, new JokeAdapter())
			.create();

		Retrofit retrofit = new Retrofit.Builder()
			.baseUrl("http://api.icndb.com/")
			.addConverterFactory(GsonConverterFactory.create())
			.build();

		/*ICNDBApi service = retrofit.create(ICNDBApi.class);
		Call<ApiResponse<Joke>> call = service.getRandomJoke(Arrays.toString(new String[] {"nerdy", "expicit"}));
		//Call<Joke> call = service.getRandomJoke();
		//Joke joke = call.execute().body();
		Joke joke = call.execute().body().value;
		System.out.println(call.request());
		System.out.println(joke);*/
	}
}
