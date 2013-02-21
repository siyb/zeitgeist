package org.geekosphere.zeitgeist.view.adapter;

import java.util.concurrent.atomic.AtomicInteger;

import org.geekosphere.zeitgeist.data.ZGItem;
import org.geekosphere.zeitgeist.net.WebRequestBuilder;
import org.geekosphere.zeitgeist.processor.ZGItemProcessor;
import org.geekosphere.zeitgeist.processor.ZGSingleItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import at.diamonddogs.data.dataobjects.CacheInformation;
import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.service.net.HttpService.WebRequestReturnContainer;
import at.diamonddogs.service.net.HttpServiceAssister;
import at.diamonddogs.service.processor.ImageProcessor;

import com.commonsware.cwac.endless.EndlessAdapter;

public class EndlessImageListAdapter extends EndlessAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(EndlessImageListAdapter.class);

	private HttpServiceAssister assister;
	private Bitmap cachedItem;
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
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminate(true);
		return pb;
	}

	@Override
	protected boolean cacheInBackground() throws Exception {
		cachedItem = getNextItem();
		return cachedItem != null;
	}

	private Bitmap getNextItem() {
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
			return (Bitmap) ((WebRequestReturnContainer) assister.runSynchronousWebRequest(imageWr, new ImageProcessor())).getPayload();
		} else {
			return getNextItem();
		}

	}

	@Override
	protected void appendCachedData() {
		((ZGAdapter) getWrappedAdapter()).add(cachedItem);
	}

	private static final class ZGAdapter extends ArrayAdapter<Bitmap> {

		public ZGAdapter(Context context) {
			super(context, -1);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new ImageView(getContext());
				((ImageView) convertView).setScaleType(ScaleType.CENTER_INSIDE);
			}
			Bitmap item = getItem(position);
			ImageView imageView = (ImageView) convertView;
			imageView.setImageBitmap(item);
			return convertView;
		}
	}

}
