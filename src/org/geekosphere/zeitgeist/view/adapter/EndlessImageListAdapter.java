package org.geekosphere.zeitgeist.view.adapter;

import org.geekosphere.zeitgeist.data.ZGItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.commonsware.cwac.endless.EndlessAdapter;

public class EndlessImageListAdapter extends EndlessAdapter{
	public EndlessImageListAdapter(Context context,
			int pendingResource) {
		super(context, new ArrayAdapter<ZGItem>(context,-1), pendingResource);
	}

	@Override
	protected View getPendingView(ViewGroup parent) {
		View imageView = new ImageView(getContext());
		return imageView;
	}
	@Override
	protected boolean cacheInBackground() throws Exception {
		return false;
	}

	@Override
	protected void appendCachedData() {
		// TODO Auto-generated method stub
		
	}

}
