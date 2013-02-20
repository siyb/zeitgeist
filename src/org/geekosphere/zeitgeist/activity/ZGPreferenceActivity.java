package org.geekosphere.zeitgeist.activity;

import org.geekosphere.zeitgeist.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ZGPreferenceActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.zgpreferenceactivity);
	}
}
