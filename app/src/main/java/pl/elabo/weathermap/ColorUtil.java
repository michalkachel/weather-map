package pl.elabo.weathermap;

import android.graphics.Color;

public class ColorUtil {

	public static final int CELCIUS_MAX_VALUE = 20;
	public static final int CELCIUS_MIN_VALUE = -10;

	public static int getColor(int celcius) {
		if (celcius > CELCIUS_MAX_VALUE) {
			celcius = CELCIUS_MAX_VALUE;
		} else if (celcius < CELCIUS_MIN_VALUE) {
			celcius = CELCIUS_MIN_VALUE;
		}

		int unit = (int) (Math.abs(255 / (CELCIUS_MAX_VALUE - CELCIUS_MIN_VALUE)));

		int red = unit * celcius;
		int green = 0;
		int blue = 255 - unit * celcius;
		int color = Color.argb(255, red, green, blue);
		return color;
	}
}


