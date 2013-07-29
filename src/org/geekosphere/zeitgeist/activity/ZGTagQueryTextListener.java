package org.geekosphere.zeitgeist.activity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geekosphere.zeitgeist.data.ZGTag;
import org.geekosphere.zeitgeist.net.WebRequestBuilder;
import org.geekosphere.zeitgeist.processor.ZGTagProcessor;
import org.geekosphere.zeitgeist.view.adapter.TagSuggestionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.widget.FilterQueryProvider;
import at.diamonddogs.data.dataobjects.CacheInformation;
import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.service.net.HttpServiceAssister;

import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.actionbarsherlock.widget.SearchView.OnSuggestionListener;

public class ZGTagQueryTextListener implements OnQueryTextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZGTagQueryTextListener.class);
	private Context context;
	private HttpServiceAssister assister;
	private TagSuggestionAdapter adapter;
	private ZGTag[] tags;

	public ZGTagQueryTextListener(Context c, SearchView sv, HttpServiceAssister assister) {
		this.context = c;
		this.assister = assister;
		loadTags();
		sv.setOnQueryTextListener(this);
		adapter = new TagSuggestionAdapter(context, createCursorFromQuery(""));
		adapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				return createCursorFromQuery(constraint == null ? "" : constraint.toString());
			}
		});
		sv.setSuggestionsAdapter(adapter);
		sv.setOnSuggestionListener(new OnSuggestionListener() {

			@Override
			public boolean onSuggestionSelect(int position) {
				return false;
			}

			@Override
			public boolean onSuggestionClick(int position) {
				Cursor c = ((Cursor) adapter.getItem(position));
				String tagPressed = c.getString(c.getColumnIndexOrThrow("tag"));
				WebRequestBuilder wrb = new WebRequestBuilder(context);
				wrb.getItemsByTag(tagPressed).page(0).build();
				return false;
			}
		});
	}

	private void loadTags() {
		try {
			WebRequestBuilder b = new WebRequestBuilder(context);
			WebRequest wr = b.getTags().build();
			wr.setCacheTime(CacheInformation.CACHE_1H);
			tags = (ZGTag[]) assister.runSynchronousWebRequest(wr, new ZGTagProcessor()).getPayload();
		} catch (Throwable tr) {
			LOGGER.info("Error while running runSynchronousWebRequest");
		}
	}

	@Override
	public boolean onQueryTextSubmit(String query) {

		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		adapter.getFilterQueryProvider().runQuery(newText);
		return false;
	}

	private Cursor createCursorFromQuery(String query) {
		MatrixCursor mx = new MatrixCursor(new String[] { "_id", "count", "tag" });

		if (tags != null) {
			List<ZGTag> tagList = Arrays.asList(tags);
			Collections.sort(tagList, new Comparator<ZGTag>() {
				@Override
				public int compare(ZGTag lhs, ZGTag rhs) {
					return lhs.getTagName().compareTo(rhs.getTagName());
				}
			});
			for (ZGTag tag : tagList) {
				if (tag.getTagName().contains(query)) {
					mx.addRow(new String[] { String.valueOf(tag.getId()), String.valueOf(tag.getItemCount()), tag.getTagName() });
				}
			}
		}
		return mx;
	}
}
