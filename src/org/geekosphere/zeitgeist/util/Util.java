package org.geekosphere.zeitgeist.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Util {
	public static void showCentricToast(Context context, String text, int duration) {
		Toast t = Toast.makeText(context, text, duration);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}

	public static void showCentricToast(Context context, int text, int duration) {
		Toast t = Toast.makeText(context, text, duration);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}
}
