package ohm.softa.a06;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ohm.softa.a06.model.Joke;

import java.io.IOException;

public class JokeAdapter extends TypeAdapter<Joke> {

	private final Gson gson;

	public JokeAdapter() {
		gson = new Gson();
	}

	// TypeAdapter<T>: A (Gson) type adapter is responsible to convert Java objects to JSON notation and vice versa.
	// Key to this transformation is in the implementation of the following two methods:
	@Override
	public void write(JsonWriter jsonWriter, Joke joke) throws IOException {
		/*There is no need to implement the write method,
		since we're only consuming the API, but not sending to it.*/
	}

	// Write a type adapter that accepts the response objects from ICNDB and outputs an instance of Joke.
	// Register the type adapter with your Retrofit instance Note that you can use annotations on the Joke class,
	// but you will have to write custom code to unwrap the joke from the response object.
	// For this, you have two options:
	// 1) Implement a wrapper class, add appropriate fields, and return the Joke once unwrapped.
	// 2) Unwrap the Joke object manually, by using the reader's .beginObject(), .endObject() and .next*() methods.
	// {
	// "type": "success",
	// "value": {
	// 			  "id": 250,
	// 			  "joke": "The truth will set you free. Unless Chuck Norris has you, in which case, forget it buddy!",
	// 			  "categories": []
	// 		    }
	// }
	@Override
	public Joke read(JsonReader jsonReader) throws IOException {
		Joke result = null;
		/* start to read from JsonReader */
		jsonReader.beginObject();

		/* iterate the reader (iterator!) */
		while (jsonReader.hasNext()) {

			/* switch-case on String (supported since Java 8) */
			switch (jsonReader.nextName()) {
				/* check if request was successfull */
				case "type":
					if (!jsonReader.nextString().equals("success")) {
						throw new IOException();
					}
					break;
				/* serialize the inner value simply by calling Gson because we mapped the fields to JSON keys */
				case "value":
					result = gson.fromJson(jsonReader, Joke.class);
					break;
			}
		}

		/* required to fix JSON document not fully consumed exception */
		jsonReader.endObject();

		return result;
	}
}
