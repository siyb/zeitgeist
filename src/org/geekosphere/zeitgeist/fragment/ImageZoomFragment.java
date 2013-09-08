package org.geekosphere.zeitgeist.fragment;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.view.adapter.EndlessImageListAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;

import com.actionbarsherlock.app.SherlockDialogFragment;

@SuppressLint("ValidFragment")
public class ImageZoomFragment extends SherlockDialogFragment implements OnClickListener {
	private AdapterViewFlipper imageViewFlipper;
	private EndlessImageListAdapter adapter;

	public ImageZoomFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.imagezoomfragment, container, false);
		imageViewFlipper = (AdapterViewFlipper) v.findViewById(R.id.imagezoomfragment_avf_viewflipper);
		imageViewFlipper.setAdapter(adapter);
		v.findViewById(R.id.imagezoomfragment_iv_next).setOnClickListener(this);
		v.findViewById(R.id.imagezoomfragment_iv_prev).setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imagezoomfragment_iv_next:
			imageViewFlipper.showNext();
			break;
		case R.id.imagezoomfragment_iv_prev:
			imageViewFlipper.showPrevious();
			break;
		}
	}

	public void setAdapter(Context c, EndlessImageListAdapter adapter) {
		this.adapter = new EndlessImageListAdapter(c.getApplicationContext(), adapter);
	}
}
