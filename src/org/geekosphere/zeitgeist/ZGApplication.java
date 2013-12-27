package org.geekosphere.zeitgeist;

import org.geekosphere.zeitgeist.broadcastreceiver.LoadingBroadcastReceiver;

import android.app.Application;

public class ZGApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		LoadingBroadcastReceiver.getInstance().registerReceiver(this);
	}
}
