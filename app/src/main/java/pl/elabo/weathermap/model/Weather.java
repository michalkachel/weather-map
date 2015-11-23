package pl.elabo.weathermap.model;

import com.google.gson.annotations.SerializedName;

public class Weather {

	@SerializedName("description")
	String mForecastDescription;

	@SerializedName("condition")
	Condition mCondition;

	public String getForecastDescription() {
		return mForecastDescription;
	}

	public String getTemperature() {
		return mCondition.mTemperature;
	}

	public String getConditionDescription() {
		return mCondition.mConditionDescription;
	}

	class Condition {

		@SerializedName("temp")
		String mTemperature;

		@SerializedName("text")
		String mConditionDescription;
	}
}
