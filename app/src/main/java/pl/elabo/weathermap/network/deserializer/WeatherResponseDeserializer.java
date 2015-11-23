package pl.elabo.weathermap.network.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class WeatherResponseDeserializer<T> implements JsonDeserializer<T> {
	@Override
	public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
		JsonElement query = je.getAsJsonObject().get("query");
		JsonElement results = query.getAsJsonObject().get("results");
		JsonElement channel = results.getAsJsonObject().get("channel");
		JsonElement item = channel.getAsJsonObject().get("item");
		return new Gson().fromJson(item, type);
	}
}
