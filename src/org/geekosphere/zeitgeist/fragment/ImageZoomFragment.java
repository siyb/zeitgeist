package org.geekosphere.zeitgeist.fragment;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.activity.ZGPreferenceActivity;
import org.geekosphere.zeitgeist.data.ZGItem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.service.net.HttpServiceAssister;
import at.diamonddogs.service.processor.ImageProcessor;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class ImageZoomFragment extends SherlockDialogFragment {
	private ZGItem item;
	private HttpServiceAssister assister;
	private ImageView imageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
		assister = new HttpServiceAssister(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.imagezoomfragment, container, false);
		imageView = (ImageView) v.findViewById(R.id.imagezoomfragment_iv_display);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		assister.bindService();
		loadImage();
	}

	@Override
	public void onPause() {
		super.onPause();
		assister.unbindService();
	}

	private void loadImage() {
		if (isResumed()) {
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String imageUrl = p.getString(ZGPreferenceActivity.KEY_HOST, "") + item.getRelativeImagePath();
			WebRequest wr = ImageProcessor.getDefaultImageRequest(imageUrl);
			assister.runWebRequest(new ImageProcessor.ImageProcessHandler(imageView, imageUrl), wr, new ImageProcessor());
		}
	}

	public void setItem(ZGItem item) {
		this.item = item;
	}
}
