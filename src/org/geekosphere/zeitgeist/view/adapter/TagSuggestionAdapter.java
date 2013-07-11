package org.geekosphere.zeitgeist.view.adapter;

import org.geekosphere.zeitgeist.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TagSuggestionAdapter extends CursorAdapter {

	public TagSuggestionAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder vh = (ViewHolder) view.getTag();
		vh.tagName.setText(cursor.getString(cursor.getColumnIndexOrThrow("tag")));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View v = LayoutInflater.from(context).inflate(R.layout.list_item_tagsuggestionadapter, viewGroup, false);
		ViewHolder vh = new ViewHolder();
		vh.tagName = (TextView) v.findViewById(R.id.list_item_tagsuggestionadapter_tv_display);
		v.setTag(vh);
		return v;
	}

	private static final class ViewHolder {
		private TextView tagName;
	}
}
