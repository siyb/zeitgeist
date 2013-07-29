package org.geekosphere.zeitgeist.fragment;

import java.io.File;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.net.WebRequestBuilder;
import org.geekosphere.zeitgeist.processor.ZGItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.service.net.HttpServiceAssister;
import at.diamonddogs.service.processor.ImageProcessor;
import at.diamonddogs.service.processor.ServiceProcessor;

import com.actionbarsherlock.app.SherlockFragment;

public class FileUploadFragment extends SherlockFragment implements OnClickListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadFragment.class);
	private static final float SCALEFACTOR = 512.0f;

	private Uri imageUri;
	private ImageView image;
	private HttpServiceAssister assister;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		assister = new HttpServiceAssister(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fileuploadfragment, container, false);
		image = (ImageView) v.findViewById(R.id.fileuploadfragment_iv_preview);
		v.findViewById(R.id.fileuploadfragment_b_upload).setOnClickListener(this);
		image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		assister.bindService();
		setUriFromIntent();
	}

	private void setUriFromIntent() {

		new Thread() {
			@Override
			public void run() {
				Intent i = getActivity().getIntent();
				imageUri = (Uri) i.getParcelableExtra(Intent.EXTRA_STREAM);
				if (imageUri == null) {
					imageUri = i.getData();
				}
				if (imageUri == null) {
					if (i.hasExtra("android.intent.extra.TEXT")) {
						imageUri = Uri.parse(i.getStringExtra("android.intent.extra.TEXT"));
					} else if (i.hasExtra("android.intent.extra.SUBJECT")) {
						imageUri = Uri.parse(i.getStringExtra("android.intent.extra.SUBJECT"));
					}
				}
				LOGGER.info("Data URI: " + imageUri);
				if (imageUri != null) {
					if (imageUri.getScheme().contains("http")) {
						downloadImage(imageUri);
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								image.setImageBitmap(getScaledBitmapFromFile(ImageProcessor.getImageFileUrl(imageUri.toString(),
										getActivity())));
							}
						});
					} else {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								image.setImageBitmap(getScaledBitmapFromLocalUri());
							}
						});
					}
				} else {
					Toast.makeText(getActivity(), R.string.fileuploadfragment_noimagedata, Toast.LENGTH_SHORT).show();
					if (!isDetached()) {
						getActivity().finish();
					}
				}
			}
		}.start();

	}

	private void downloadImage(Uri u) {
		String url = u.toString();
		WebRequest wr = ImageProcessor.getDefaultImageRequest(url);
		assister.runSynchronousWebRequest(wr, new ImageProcessor());
	}

	private Bitmap getScaledBitmapFromFile(String filePath) {
		Bitmap src = BitmapFactory.decodeFile(filePath);
		return scaleImage(src);
	}

	private Bitmap getScaledBitmapFromLocalUri() {
		Bitmap src = BitmapFactory.decodeFile(getRealPathFromURI(imageUri));
		return scaleImage(src);
	}

	private Bitmap scaleImage(Bitmap src) {
		int scaledHight = (int) (src.getHeight() * (SCALEFACTOR / src.getWidth()));
		return Bitmap.createScaledBitmap(src, (int) SCALEFACTOR, scaledHight, true);
	}

	@Override
	public void onPause() {
		super.onPause();
		assister.unbindService();
	}

	@Override
	public void onClick(View v) {
		WebRequestBuilder b = new WebRequestBuilder(getActivity());
		WebRequest wr = b.newItem().addUploadFile(new File[] { new File(getRealPathFromURI(imageUri)) }).build();
		wr.setReadTimeout(10000);
		wr.setConnectionTimeout(5000);
		assister.runWebRequest(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == ZGItemProcessor.ID) {
					// TODO: 500 - error
					int statusCode = msg.getData().getInt(ServiceProcessor.BUNDLE_EXTRA_MESSAGE_HTTPSTATUSCODE);
					if (msg.arg1 == ServiceProcessor.RETURN_MESSAGE_OK) {
						getActivity().finish();
					} else {
						Toast.makeText(getActivity(), getString(R.string.fileuploadfragment_erroruploading), Toast.LENGTH_SHORT).show();
					}
				}
			}
		}, wr, new ZGItemProcessor());
	}

	private String getRealPathFromURI(Uri contentUri) {
		if (imageUri.getScheme().contains("http")) {
			return ImageProcessor.getImageFileUrl(imageUri.toString(), getActivity());
		} else {
			String[] proj = { MediaStore.Images.Media.DATA };
			CursorLoader loader = new CursorLoader(getActivity(), contentUri, proj, null, null, null);
			Cursor cursor = loader.loadInBackground();
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
	}
}
