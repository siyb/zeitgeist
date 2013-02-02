package org.geekosphere.zeitgeist.activity;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.data.ZGItem;
import org.geekosphere.zeitgeist.net.WebRequestBuilder;
import org.geekosphere.zeitgeist.processor.ZGItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import at.diamonddogs.service.net.HttpServiceAssister;
import at.diamonddogs.service.processor.ServiceProcessor;

public class ZGActivity extends SherlockFragmentActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZGActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zgactivity);
	}


}
