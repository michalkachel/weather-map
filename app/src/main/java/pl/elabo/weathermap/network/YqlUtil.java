package pl.elabo.weathermap.network;

import com.google.android.gms.maps.model.LatLng;

public class YqlUtil {

	public static String locationQuery(LatLng latLng) {
		return String.format("select * from geo.placefinder where text=\"%s,%s\" and gflags=\"R\"", latLng.latitude, latLng.longitude);
	}

	public static String weatherQuery(String city, String country, String temperatureUnit) {
		return String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s, %s\") and u ='%s'", city, country, temperatureUnit);
	}

	public static String weatherQuery(String woeid, String temperatureUnit) {
		return String.format("select * from weather.forecast where woeid = %s and u ='%s'", woeid, temperatureUnit);
	}

}
