package pl.elabo.weathermap;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class MessageManager {

	public static void showMessage(Context context, String message, boolean longDuration) {
		Toast.makeText(context, message, longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}

	public static void showMessage(Context context, @StringRes int stringRes, boolean longDuration) {
		showMessage(context, context.getString(stringRes), longDuration);
	}

}
