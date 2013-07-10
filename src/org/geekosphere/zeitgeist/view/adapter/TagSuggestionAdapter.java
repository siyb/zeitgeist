package org.geekosphere.zeitgeist.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TagSuggestionAdapter extends CursorAdapter {

	public TagSuggestionAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		((TextView) view).setText(cursor.getString(cursor.getColumnIndexOrThrow("tag")));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return new TextView(context);
	}

}
