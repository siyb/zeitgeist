package org.geekosphere.zeitgeist.view.adapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.geekosphere.zeitgeist.R;
import org.geekosphere.zeitgeist.activity.ZGPreferenceActivity;
import org.geekosphere.zeitgeist.broadcastreceiver.LoadingBroadcastReceiver;
import org.geekosphere.zeitgeist.data.ZGItem;
import org.geekosphere.zeitgeist.net.WebRequestBuilder;
import org.geekosphere.zeitgeist.processor.ZGItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import at.diamonddogs.data.dataobjects.CacheInformation;
import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.service.net.HttpService.WebRequestReturnContainer;
import at.diamonddogs.service.net.HttpServiceAssister;
import at.diamonddogs.service.processor.ImageProcessor;

import com.commonsware.cwac.endless.EndlessAdapter;

public class EndlessImageListAdapter extends EndlessAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(EndlessImageListAdapter.class);
	private static final long INDETERMINATE_PROGRESS_TIMEOUT_POLL_MS = 500;

	private HttpServiceAssister assister;
	private Pair<ZGItem, Bitmap> cachedItem;
	protected boolean thumbnail = true;

	protected final AtomicInteger currentPage = new AtomicInteger(1);
	protected final AtomicInteger listPointer = new AtomicInteger(0);

	protected List<ZGItem> zgItems;

	private ImageView placeHolderView;

	private final AtomicBoolean currentlyLoadingImage = new AtomicBoolean(false);
	private Timer timer;

	public EndlessImageListAdapter(Context context) {
		super(context, new ZGAdapter(context), -1);
		assister = new HttpServiceAssister(context);
		assister.bindService();
	}

	public EndlessImageListAdapter(Context context, boolean thumbnail) {
		this(context);
		this.thumbnail = thumbnail;
	}

	public EndlessImageListAdapter(Context context, EndlessImageListAdapter adapter) {
		this(context, false);
		this.currentPage.set(adapter.currentPage.get());
		this.listPointer.set(adapter.listPointer.get());
		this.zgItems = adapter.zgItems;
	}

	@Override
	protected View getPendingView(ViewGroup parent) {
		if (placeHolderView == null) {
			placeHolderView = new ImageView(getContext());
			placeHolderView.setImageResource(R.drawable.app_icon);
			placeHolderView.setScaleType(ScaleType.CENTER_INSIDE);
			placeHolderView.setBackgroundResource(android.R.color.transparent);
		}
		return placeHolderView;
	}

	@Override
	protected boolean cacheInBackground() throws Exception {
		cancelAndRenewIndeterminateProgressTimeout();
		cachedItem = getNextItem();
		return cachedItem != null;
	}

	private void cancelAndRenewIndeterminateProgressTimeout() {
		if (timer != null) {
			timer.cancel();
		} else {
			LoadingBroadcastReceiver.getInstance().sendLoadingIntent(getContext());
		}
		renewTimer();
	}

	private void renewTimer() {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (currentlyLoadingImage.get()) {
					renewTimer();
				} else {
					timer = null;
					LoadingBroadcastReceiver.getInstance().sendLoadingDoneIntent(getContext());
				}
			}
		}, INDETERMINATE_PROGRESS_TIMEOUT_POLL_MS);
	}

	@Override
	protected void appendCachedData() {
		((ZGAdapter) getWrappedAdapter()).add(cachedItem);
	}

	private Pair<ZGItem, Bitmap> getNextItem() {
		populateZGItemsIfNecessary();

		ZGItem item = zgItems.get(listPointer.getAndIncrement());
		LOGGER.info("getNextItem: using imageurl of " + item.getId());
		String imageUrl;
		if (thumbnail) {
			imageUrl = item.getRelativeThumbnailPath();
		} else {
			imageUrl = item.getRelativeImagePath();
		}
		if (imageUrl != null) {
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getContext());
			String url = p.getString(ZGPreferenceActivity.KEY_HOST, "") + imageUrl;

			LOGGER.info("Getting Image: " + url + " thumb? " + thumbnail);
			// TODO: put in WebRequestBuilder
			WebRequest imageWr = new WebRequest();
			imageWr.setUrl(url);
			imageWr.setProcessorId(ImageProcessor.ID);
			imageWr.setCacheTime(CacheInformation.CACHE_FOREVER);
			currentlyLoadingImage.set(true);
			Bitmap b = (Bitmap) ((WebRequestReturnContainer) assister.runSynchronousWebRequest(imageWr, new ImageProcessor())).getPayload();
			currentlyLoadingImage.set(false);
			Pair<ZGItem, Bitmap> returnPair = new Pair<ZGItem, Bitmap>(item, b);

			return returnPair;
		} else {
			return getNextItem();
		}

	}

	private void populateZGItemsIfNecessary() {
		if (zgItems == null || lastImageLoaded()) {
			WebRequestBuilder wrb = new WebRequestBuilder(getContext());
			listPointer.set(0);
			WebRequest wr;
			LOGGER.info("getNextItem: Getting items for page " + currentPage);
			// TODO: use a factory to alternate between this request and the
			// request required by tag search
			wr = wrb.getItems().page(currentPage.getAndIncrement()).build();
			ZGItem[] items = (ZGItem[]) ((WebRequestReturnContainer) assister.runSynchronousWebRequest(wr, new ZGItemProcessor()))
					.getPayload();
			zgItems = Collections.synchronizedList(Arrays.asList(items));
			LOGGER.info("getNextItem: got " + zgItems.size());
		}
	}

	private boolean lastImageLoaded() {
		return (listPointer.get() == (zgItems.size() - 1));
	}

	public Pair<ZGItem, Bitmap> getZGItem(int position) {
		return (Pair<ZGItem, Bitmap>) getWrappedAdapter().getItem(position);
	}

	private static final class ZGAdapter extends ArrayAdapter<Pair<ZGItem, Bitmap>> {
		private LayoutInflater inflater;

		public ZGAdapter(Context context) {
			super(context, -1);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item_endlessimagelistadapter, parent, false);
				ImageView iv = (ImageView) convertView.findViewById(R.id.list_item_endlessimagelistadapter_iv_image);
				iv.setScaleType(ScaleType.CENTER_INSIDE);
				convertView.setTag(iv);
			}
			ImageView iv = (ImageView) convertView.getTag();
			Pair<ZGItem, Bitmap> item = getItem(position);
			iv.setImageBitmap(item.second);
			return convertView;
		}
	}

}
