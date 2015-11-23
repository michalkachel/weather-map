package pl.elabo.weathermap.activity;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.elabo.weathermap.ColorUtil;
import pl.elabo.weathermap.MessageManager;
import pl.elabo.weathermap.R;
import pl.elabo.weathermap.app.AppConstants;
import pl.elabo.weathermap.model.Location;
import pl.elabo.weathermap.model.Weather;
import pl.elabo.weathermap.network.RestClient;
import pl.elabo.weathermap.network.YqlUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class WeatherMapActivity extends FragmentActivity implements OnMapReadyCallback {
	public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 11;

	@Bind(R.id.background)
	View mBackground;
	@Bind(R.id.weather_panel)
	ViewGroup mWeatherPanel;
	@Bind(R.id.location)
	TextView mLocation;
	@Bind(R.id.temperature)
	TextView mTemperature;
	@Bind(R.id.condition_desc)
	TextView mConditionDescription;

	private GoogleMap mMap;
	private boolean mWasFirstLocationFix = false;
	private Call<Location> mLocationCall;
	private Call<Weather> mWeatherCall;

	private int mCurrentColor = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_map);
		ButterKnife.bind(this);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		LatLng initialLatLng = new LatLng(AppConstants.INITIAL_LON, AppConstants.INITIAL_LAT);

		mMap = googleMap;
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, AppConstants.DEFAULT_MAP_ZOOM));
		mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				requestForWeatherLocation(cameraPosition.target);
			}
		});

		handleLocationPermission();
	}

	private void handleLocationPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			initMyLocation();
		} else {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
		}
	}

	private void initMyLocation() {
		mMap.setMyLocationEnabled(true);
		mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(android.location.Location location) {
				if (!mWasFirstLocationFix) {
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), AppConstants.DEFAULT_MAP_ZOOM));
					mWasFirstLocationFix = true;
				}
			}
		});
	}

	private void requestForWeatherLocation(LatLng latLng) {
		cancelNetworkCalls();
		mLocationCall = RestClient.getWeatherApi().queryLocation(YqlUtil.locationQuery(latLng), AppConstants.JSON_FORMAT);
		mLocationCall.enqueue(new Callback<Location>() {
			@Override
			public void onResponse(Response<Location> response, Retrofit retrofit) {
				final Location location = response.body();
				if (response.isSuccess() && location != null && location.getWoeid() != null) {
					requestForWeather(location);
				} else {
					hideWeather();
				}
			}

			@Override
			public void onFailure(Throwable t) {
				if (!isCausedByCancel(t)) {
					t.printStackTrace();
					onWeatherFailure();
				}
			}
		});
	}

	private void requestForWeather(final Location location) {
		mWeatherCall = RestClient.getWeatherApi().queryWeather(YqlUtil.weatherQuery(location.getWoeid(), AppConstants.TEMPERATURE_UNIT), AppConstants.JSON_FORMAT);
		mWeatherCall.enqueue(new Callback<Weather>() {
			@Override
			public void onResponse(Response<Weather> response, Retrofit retrofit) {
				final Weather weather = response.body();
				if (response.isSuccess() && weather != null) {
					showWeather(location, weather);
				} else {
					hideWeather();
				}
			}

			@Override
			public void onFailure(Throwable t) {
				if (!isCausedByCancel(t)) {
					t.printStackTrace();
					onWeatherFailure();
				}
			}
		});
	}

	private boolean isCausedByCancel(Throwable t) {
		return t.getMessage() != null && t.getMessage().equals("Canceled");
	}

	private void showWeather(Location location, Weather weather) {
		mWeatherPanel.setVisibility(View.VISIBLE);
		mLocation.setText(location.getCity());
		mTemperature.setText(String.format("%s%s", weather.getTemperature(), getString(R.string.celcius)));
		mConditionDescription.setText(weather.getConditionDescription());

		try {
			changeBackground(Integer.valueOf(weather.getTemperature()));
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}

	private void changeBackground(int temperature) {
		int colorTo = ColorUtil.getColor(temperature);
		ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), mCurrentColor, colorTo);
		colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				mBackground.setBackgroundColor((Integer) animator.getAnimatedValue());
			}

		});
		colorAnimation.setDuration(1000);
		colorAnimation.start();
		mCurrentColor = colorTo;
	}

	private void hideWeather() {
		mWeatherPanel.setVisibility(View.GONE);
	}

	private void onWeatherFailure() {
		MessageManager.showMessage(WeatherMapActivity.this, R.string.error_weather_call, false);
	}

	private void cancelNetworkCalls() {
		RestClient.getOkHttpClient().getDispatcher().getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				if (mLocationCall != null) {
					mLocationCall.cancel();
				}
				if (mWeatherCall != null) {
					mWeatherCall.cancel();
				}
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					initMyLocation();
				} else {
					MessageManager.showMessage(WeatherMapActivity.this, R.string.message_location_cannot_be_accessed, true);
				}
				return;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		cancelNetworkCalls();
	}
}
