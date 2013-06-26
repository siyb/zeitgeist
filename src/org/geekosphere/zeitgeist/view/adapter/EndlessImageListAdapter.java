package org.geekosphere.zeitgeist.view.adapter;

import java.util.concurrent.atomic.AtomicInteger;

import org.geekosphere.zeitgeist.activity.ZGActivity;
import org.geekosphere.zeitgeist.data.ZGItem;
import org.geekosphere.zeitgeist.net.WebRequestBuilder;
import org.geekosphere.zeitgeist.processor.ZGItemProcessor;
import org.geekosphere.zeitgeist.processor.ZGSingleItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
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

	private HttpServiceAssister assister;
	private Pair<ZGItem, Bitmap> cachedItem;
	private boolean thumbnail = true;

	private final AtomicInteger currentItem = new AtomicInteger(-1);

	public EndlessImageListAdapter(Context context) {
		super(context, new ZGAdapter(context), -1);
		assister = new HttpServiceAssister(context);
		assister.bindService();
	}

	public EndlessImageListAdapter(Context context, boolean thumbnail) {
		super(context, new ZGAdapter(context), -1);
		this.thumbnail = thumbnail;
		assister = new HttpServiceAssister(context);
		assister.bindService();
	}

	@Override
	protected View getPendingView(ViewGroup parent) {
		// return dummy view
		return new View(getContext());
	}

	@Override
	protected boolean cacheInBackground() throws Exception {
		LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(ZGActivity.INTENT_ACTION_LOADING));
		cachedItem = getNextItem();
		return cachedItem != null;
	}

	private Pair<ZGItem, Bitmap> getNextItem() {
		WebRequestBuilder wrb = new WebRequestBuilder(getContext());
		WebRequest wr;
		if (currentItem.get() == -1) {
			wr = wrb.getItems().page(1).build();
			wrb.reset();
			ZGItem[] items = (ZGItem[]) ((WebRequestReturnContainer) assister.runSynchronousWebRequest(wr, new ZGItemProcessor()))
					.getPayload();
			currentItem.set(items[0].getId());
		}
		wr = wrb.getItems().withId(currentItem.get()).build();
		wr.setCacheTime(CacheInformation.CACHE_FOREVER);

		LOGGER.info("Getting thumbnail (info url):" + wr.getUrl());
		ZGItem item = (ZGItem) ((WebRequestReturnContainer) assister.runSynchronousWebRequest(wr, new ZGSingleItemProcessor()))
				.getPayload();
		currentItem.decrementAndGet();
		String imageUrl;
		if (thumbnail) {
			imageUrl = item.getRelativeThumbnailPath();
		} else {
			imageUrl = item.getRelativeImagePath();
		}
		if (imageUrl != null) {
			String url = "http://zeitgeist.li" + imageUrl;

			LOGGER.info("Getting Image: " + url + " thumb? " + thumbnail);
			// TODO: put in WebRequestBuilder
			WebRequest imageWr = new WebRequest();
			imageWr.setUrl(url);
			imageWr.setProcessorId(ImageProcessor.ID);
			imageWr.setCacheTime(CacheInformation.CACHE_FOREVER);
			Bitmap b = (Bitmap) ((WebRequestReturnContainer) assister.runSynchronousWebRequest(imageWr, new ImageProcessor())).getPayload();
			Pair<ZGItem, Bitmap> returnPair = new Pair<ZGItem, Bitmap>(item, b);

			return returnPair;
		} else {
			return getNextItem();
		}

	}

	@Override
	protected void appendCachedData() {
		((ZGAdapter) getWrappedAdapter()).add(cachedItem);
		LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(ZGActivity.INTENT_ACTION_LOADING_DONE));

	}

	public Pair<ZGItem, Bitmap> getZGItem(int position) {
		return (Pair<ZGItem, Bitmap>) getWrappedAdapter().getItem(position);
	}

	private static final class ZGAdapter extends ArrayAdapter<Pair<ZGItem, Bitmap>> {

		public ZGAdapter(Context context) {
			super(context, -1);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new ImageView(getContext());
				((ImageView) convertView).setScaleType(ScaleType.CENTER_INSIDE);
			}
			Pair<ZGItem, Bitmap> item = getItem(position);
			ImageView imageView = (ImageView) convertView;
			imageView.setImageBitmap(item.second);
			return convertView;
		}
	}

}
