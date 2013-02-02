package org.geekosphere.zeitgeist.view.adapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.geekosphere.zeitgeist.data.ZGItem;
import org.geekosphere.zeitgeist.data.ZGItem.ZGItemType;
import org.geekosphere.zeitgeist.net.WebRequestBuilder;
import org.geekosphere.zeitgeist.processor.ZGItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import at.diamonddogs.data.dataobjects.CacheInformation;
import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.service.net.HttpServiceAssister;
import at.diamonddogs.service.processor.ImageProcessor;

import com.commonsware.cwac.endless.EndlessAdapter;

public class EndlessImageListAdapter extends EndlessAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(EndlessImageListAdapter.class);

	private HttpServiceAssister assister;
	private int page = -1;
	private Bitmap[] cachedItems;

	public EndlessImageListAdapter(Context context) {
		super(context, new ZGAdapter(context), -1);
		assister = new HttpServiceAssister(context);
		assister.bindService();
	}

	@Override
	protected View getPendingView(ViewGroup parent) {
		TextView tv = new TextView(getContext());
		tv.setText("WAIT...");
		return tv;
	}

	@Override
	protected boolean cacheInBackground() throws Exception {
		cachedItems = getNextPageItems();
		return cachedItems.length > 0;
	}

	private Bitmap[] getNextPageItems() {
		page++;
		WebRequestBuilder wrb = new WebRequestBuilder();
		WebRequest wr = wrb.getItems().page(page).build();
		LOGGER.error("Getting page " + page + " " + wr.getUrl());
		ZGItem[] items = (ZGItem[]) assister.runSynchronousWebRequest(wr, new ZGItemProcessor());
		ArrayList<Bitmap> ret = new ArrayList<Bitmap>(items.length);
		for (ZGItem item : items) {
			if (item.getType() == ZGItemType.IMAGE) {
				LOGGER.error("ITEM --> " +item);
				String url = "http://zeitgeist.li" + item.getRelativeThumbnailPath();
				WebRequest imageWr = new WebRequest();
				imageWr.setUrl(url);
				imageWr.setProcessorId(ImageProcessor.ID);
				imageWr.setCacheTime(CacheInformation.CACHE_7D);
				ret.add((Bitmap) assister.runSynchronousWebRequest(imageWr, new ImageProcessor()));
			}
		}
		return ret.toArray(new Bitmap[ret.size()]);
	}

	@Override
	protected void appendCachedData() {
		for (Bitmap b : cachedItems) {
			((ZGAdapter) getWrappedAdapter()).add(b);
		}
	}

	private static final class ZGAdapter extends ArrayAdapter<Bitmap> {

		public ZGAdapter(Context context) {
			super(context, -1);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = new ImageView(getContext());
			convertView = iv;
			iv.setScaleType(ScaleType.CENTER_INSIDE);

			iv.setImageBitmap(getItem(position));
			return convertView;
		}
	}
}
