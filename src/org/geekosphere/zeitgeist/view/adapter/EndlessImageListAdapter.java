package org.geekosphere.zeitgeist.view.adapter;

import org.geekosphere.zeitgeist.data.ZGItem;
import org.geekosphere.zeitgeist.data.ZGItem.ZGItemType;
import org.geekosphere.zeitgeist.net.WebRequestBuilder;
import org.geekosphere.zeitgeist.processor.ZGItemProcessor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.service.net.HttpServiceAssister;
import at.diamonddogs.service.processor.ImageProcessor;
import at.diamonddogs.service.processor.ImageProcessor.ImageProcessHandler;

import com.commonsware.cwac.endless.EndlessAdapter;

public class EndlessImageListAdapter extends EndlessAdapter {
	private HttpServiceAssister assister;
	private int page = -1;
	private ZGItem[] cachedItems;

	public EndlessImageListAdapter(Context context) {
		super(context, new ZGAdapter(context), -1);
		assister = new HttpServiceAssister(context);
		assister.bindService();
		((ZGAdapter) getWrappedAdapter()).setAssister(assister);
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

	private ZGItem[] getNextPageItems() {
		page++;
		WebRequestBuilder wrb = new WebRequestBuilder();
		WebRequest wr = wrb.getItems().page(page).build();
		return (ZGItem[]) assister.runSynchronousWebRequest(wr,
				new ZGItemProcessor());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void appendCachedData() {
		for (ZGItem item : cachedItems) {
			if (item.getType() == ZGItemType.IMAGE) {
				((ArrayAdapter<ZGItem>) getWrappedAdapter()).add(item);
			}
		}
	}

	private static final class ZGAdapter extends ArrayAdapter<ZGItem> {
		private HttpServiceAssister assister;

		public ZGAdapter(Context context) {
			super(context, -1);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				ImageView iv = new ImageView(getContext());
				iv.setScaleType(ScaleType.CENTER_INSIDE);
				convertView = iv;
			}
			ZGItem item = (ZGItem) getItem(position);
			String url = "http://zeitgeist.li"
					+ item.getRelativeThumbnailPath();
			WebRequest wr = new WebRequest();
			wr.setUrl(url);
			wr.setProcessorId(ImageProcessor.ID);
			assister.runWebRequest(new ImageProcessHandler(
					(ImageView) convertView, url), wr, new ImageProcessor());
			return convertView;
		}

		public void setAssister(HttpServiceAssister assister) {
			this.assister = assister;
		}
	}
}
