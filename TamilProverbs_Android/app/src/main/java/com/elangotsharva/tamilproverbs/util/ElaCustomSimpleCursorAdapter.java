package com.elangotsharva.tamilproverbs.util;

import android.content.Context;
import android.database.Cursor;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class ElaCustomSimpleCursorAdapter extends SimpleCursorAdapter implements SectionIndexer {
	
	HashMap<String, Integer> mIndexer;
	String[] sections = new String[0];
	
	/*********************************************************************************
	 * Callback function called on creating the SimpleCursorAdapter
	 *********************************************************************************/
	public ElaCustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		G.log("start");
	}
	
	/*********************************************************************************
	 * Returns the position number of alphabet. Position will be in the range of 1 to
	 * number of alphabets
	 *********************************************************************************/
	@Override
	public int getPositionForSection(int section) {
		try {
			G.log("start");
			G.log("section: " + section);
			G.log("sections[section]: " + sections[section]);
			int pos = (mIndexer.get(sections[section]) == null) ? 0 : mIndexer.get(sections[section]);
			G.log("pos: " + mIndexer.get(sections[section]));
			G.log("end");
			return pos;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public int getSectionForPosition(int position) {
		G.log("start");
		G.log("end");
		return 0;
		
	}
	
	@Override
	public Object[] getSections() {
		G.log("start");
		G.log("end");
		return sections;
	}
	
	/*********************************************************************************
	 * Custom function on setting the data on list view.
	 * This is to get the alphabets using the fetched data from DB
	 *********************************************************************************/
	@Override
	public Cursor swapCursor(Cursor cursor) {
		try {
			if (cursor == null)
				return null;
			mIndexer = new HashMap<String, Integer>();
			G.log("mIndexer: " + mIndexer);
			final int allRowsCount = cursor.getCount();
			G.log("allRowsCount: " + allRowsCount);
			G.log("cursor: " + cursor);
			cursor.moveToFirst();
			
			for (int pos = 0; pos < allRowsCount; pos++) {
				String dictWord = cursor.getString(cursor.getColumnIndex(G.COLUMN_PROVERB));
				String key = dictWord.toUpperCase().substring(0, 1);
				if (!mIndexer.containsKey(key)) {
					G.log("Inserting in the map: " + key + " with pos as : " + pos);
					mIndexer.put(key, pos);
				}
				cursor.moveToNext();
			}
			
			Set<String> sectionLetters = mIndexer.keySet();
			ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
			Collections.sort(sectionList);
			sections = new String[sectionList.size()];
			sectionList.toArray(sections);
			G.log("end");
			
			return super.swapCursor(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
