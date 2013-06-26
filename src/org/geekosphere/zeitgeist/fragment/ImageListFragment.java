package org.geekosphere.zeitgeist.fragment;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.data.ZGItem;
import org.geekosphere.zeitgeist.view.adapter.EndlessImageListAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragment;

public class ImageListFragment extends SherlockFragment implements OnItemClickListener {
	private GridView grid;
	private EndlessImageListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.imagelistfragment, container, false);
		grid = (GridView) v.findViewById(R.id.imagelistfragment_gv_grid);
		grid.setAdapter(adapter = new EndlessImageListAdapter(getActivity()));
		grid.setOnItemClickListener(this);
		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		ZGItem item = adapter.getZGItem(position);
		ImageZoomFragment imageZoomFragment = new ImageZoomFragment();
		imageZoomFragment.setItem(item);
		imageZoomFragment.show(getActivity().getSupportFragmentManager(), "");
	}
}
