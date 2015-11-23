package pl.elabo.weathermap.app;

import android.app.Application;

import pl.elabo.weathermap.BuildConfig;
import timber.log.Timber;


public class WeatherApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}
	}
}
