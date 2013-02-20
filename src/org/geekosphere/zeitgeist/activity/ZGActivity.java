package org.geekosphere.zeitgeist.activity;

import org.geekosphere.zeitgeist.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ZGActivity extends SherlockFragmentActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZGActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeDefaultSettings();
		setContentView(R.layout.zgactivity);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mainmenu_settings:
			startActivity(new Intent(this, ZGPreferenceActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initializeDefaultSettings() {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		if (p.getBoolean(ZGPreferenceActivity.KEY_INITIALIZED, false)) {
			return;
		}
		LOGGER.info("Initializing default settings");
		// @formatter:off
		p
			.edit()
				.putBoolean(ZGPreferenceActivity.KEY_INITIALIZED, true)
				.putString(ZGPreferenceActivity.KEY_HOST, "http://zeitgeist.li/")
			.commit();
		// @formatter:on
	}
}
