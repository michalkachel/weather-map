package pl.elabo.weathermap.model;

import com.google.gson.annotations.SerializedName;

public class Location {

	@SerializedName("woeid")
	String mWoeid;

	@SerializedName("city")
	String mCity;

	public String getWoeid() {
		return mWoeid;
	}

	public String getCity() {
		return mCity;
	}
}
