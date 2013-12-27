package org.geekosphere.zeitgeist.activity;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.broadcastreceiver.LoadingBroadcastReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;
import at.diamonddogs.service.net.HttpServiceAssister;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.SearchView;

public class ZGActivity extends SherlockFragmentActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZGActivity.class);

	public static final int ACTIVITY_REQUEST_UPLOADFILE = 0;

	private HttpServiceAssister assister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		initializeDefaultSettings();
		setContentView(R.layout.zgactivity);
		assister = new HttpServiceAssister(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.mainmenu, menu);
		MenuItem searchItem = menu.findItem(R.id.mainmenu_search);
		SearchView s = (SearchView) searchItem.getActionView();
		new ZGTagQueryTextListener(this, s, assister);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mainmenu_settings:
			startActivity(new Intent(this, ZGPreferenceActivity.class));
			break;
		case R.id.mainmenu_upload:
			showFileChooser();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			Intent i;
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
				i = Intent.createChooser(intent, getString(R.string.zgactivity_selectimage));
			} else {
				i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			}
			startActivityForResult(i, ACTIVITY_REQUEST_UPLOADFILE);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(this, R.string.zgactivity_couldnothandle, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK && requestCode == ACTIVITY_REQUEST_UPLOADFILE) {
			Intent i = new Intent(intent);
			i.setClass(this, ZGUploadActivity.class);
			startActivity(i);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LoadingBroadcastReceiver.getInstance().setActivity(this);
		assister.bindService();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LoadingBroadcastReceiver.getInstance().setActivity(null);
		assister.unbindService();
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
