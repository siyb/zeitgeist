package org.geekosphere.zeitgeist.activity;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.broadcastreceiver.LoadingBroadcastReceiver;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class ZGUploadActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.zguploadactivity);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LoadingBroadcastReceiver.getInstance().setActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		LoadingBroadcastReceiver.getInstance().setActivity(null);
	}
}
