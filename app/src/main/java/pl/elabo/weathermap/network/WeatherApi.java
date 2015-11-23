package pl.elabo.weathermap.network;


import pl.elabo.weathermap.model.Location;
import pl.elabo.weathermap.model.Weather;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface WeatherApi {

	@GET("yql")
	Call<Location> queryLocation(@Query("q") String query, @Query("format") String format);

	@GET("yql")
	Call<Weather> queryWeather(@Query("q") String query, @Query("format") String format);

}
