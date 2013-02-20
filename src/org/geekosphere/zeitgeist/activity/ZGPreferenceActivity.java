package org.geekosphere.zeitgeist.activity;

import org.geekosphere.zeitgeist.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ZGPreferenceActivity extends PreferenceActivity {
	public static final String KEY_INITIALIZED = "KEY_INITIALIZED";
	public static final String KEY_HOST = "KEY_HOST";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.zgpreferenceactivity);
	}
}
