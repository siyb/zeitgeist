package org.geekosphere.zeitgeist.activity;

import org.geekosphere.zeitgeist.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class ZGActivity extends SherlockFragmentActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZGActivity.class);

	public static final String INTENT_ACTION_LOADING = "org.geekosphere.zeitgeist.activity.ZGActivity.INTENT_EXTRA_LOADING";
	public static final String INTENT_ACTION_LOADING_DONE = "org.geekosphere.zeitgeist.activity.ZGActivity.INTENT_EXTRA_LOADING_DONE";

	private LoadingBroadcastReceiver loadingBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setSupportProgressBarIndeterminateVisibility(false);
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

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(loadingBroadcastReceiver = new LoadingBroadcastReceiver(),
				new IntentFilter(INTENT_ACTION_LOADING));
		LocalBroadcastManager.getInstance(this).registerReceiver(loadingBroadcastReceiver = new LoadingBroadcastReceiver(),
				new IntentFilter(INTENT_ACTION_LOADING_DONE));
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

	private final class LoadingBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(INTENT_ACTION_LOADING)) {
				setSupportProgressBarIndeterminateVisibility(true);
			} else {
				setSupportProgressBarIndeterminateVisibility(false);
			}
		}
	}
}
