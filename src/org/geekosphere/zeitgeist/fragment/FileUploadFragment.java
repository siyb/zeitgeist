package org.geekosphere.zeitgeist.fragment;

import org.geekosphere.zeitgeist.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;

public class FileUploadFragment extends SherlockFragment implements OnClickListener {
	private ImageView image;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fileuploadfragment, container, false);
		image = (ImageView) v.findViewById(R.id.fileuploadfragment_iv_preview);
		v.findViewById(R.id.fileuploadfragment_b_upload).setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {

	}
}
