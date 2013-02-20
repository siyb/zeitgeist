package org.geekosphere.zeitgeist.activity;

import org.geekosphere.zeitgeist.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class ZGActivity extends SherlockFragmentActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZGActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zgactivity);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
