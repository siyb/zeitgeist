package org.geekosphere.zeitgeist.fragment;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.data.ZGItem;
import org.geekosphere.zeitgeist.view.adapter.EndlessImageListAdapter;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragment;

public class ImageListFragment extends SherlockFragment implements OnItemClickListener, OnLongClickListener {
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
		grid.setOnLongClickListener(this);
		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		ZGItem item = adapter.getZGItem(position).first;
		ImageZoomFragment imageZoomFragment = new ImageZoomFragment();
		imageZoomFragment.setItem(item);
		imageZoomFragment.show(getActivity().getSupportFragmentManager(), "");
	}

	@Override
	public boolean onLongClick(View v) {
		getSherlockActivity().startActionMode(new Callback() {

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {

			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				return false;
			}
		});
		return true;
	}
}
