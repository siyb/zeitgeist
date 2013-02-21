package org.geekosphere.zeitgeist.fragment;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.view.adapter.EndlessImageListAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;

import com.actionbarsherlock.app.SherlockFragment;

public class ShowRoomFragment extends SherlockFragment implements OnTouchListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShowRoomFragment.class);

	private AdapterViewFlipper stackView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.showroomfragment, container, false);
		stackView = (AdapterViewFlipper) v.findViewById(R.id.showroomfragment_sv_gallery);
		stackView.setAdapter(new EndlessImageListAdapter(getActivity(), false));
		stackView.setOnTouchListener(this);
		return v;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		stackView.showNext();
		return false;
	}

}
