package org.geekosphere.zeitgeist.fragment;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.view.adapter.EndlessImageListAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.actionbarsherlock.app.SherlockListFragment;

public class ImageListFragment extends SherlockListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new EndlessImageListAdapter(getActivity()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.imagelistfragment, container, false);
	}
}
