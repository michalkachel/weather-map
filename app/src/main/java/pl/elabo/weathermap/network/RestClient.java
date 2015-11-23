package pl.elabo.weathermap.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import pl.elabo.weathermap.app.AppConstants;
import pl.elabo.weathermap.model.Location;
import pl.elabo.weathermap.model.Weather;
import pl.elabo.weathermap.network.deserializer.LocationResponseDeserializer;
import pl.elabo.weathermap.network.deserializer.WeatherResponseDeserializer;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class RestClient {

	private static RestClient sRestClient;
	private OkHttpClient mOkHttpClient;

	private WeatherApi mWeatherApi;

	public RestClient() {
		mOkHttpClient = new OkHttpClient();
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		mOkHttpClient.interceptors().add(interceptor);

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Location.class, new LocationResponseDeserializer<Location>())
				.registerTypeAdapter(Weather.class, new WeatherResponseDeserializer<Weather>())
				.create();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(AppConstants.QUERY_URL)
				.client(mOkHttpClient)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.build();

		mWeatherApi = retrofit.create(WeatherApi.class);
	}

	private static RestClient getInstance() {
		if (sRestClient == null) {
			sRestClient = new RestClient();
		}
		return sRestClient;
	}

	public static OkHttpClient getOkHttpClient() {
		return getInstance().mOkHttpClient;
	}

	public static WeatherApi getWeatherApi() {
		return getInstance().mWeatherApi;
	}
}
