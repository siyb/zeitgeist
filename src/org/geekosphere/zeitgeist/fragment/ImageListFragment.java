package org.geekosphere.zeitgeist.fragment;

import org.geekosphere.zeitgeist.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;

public class ImageListFragment extends SherlockListFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.imagelistfragment, container, false);

	}
}
