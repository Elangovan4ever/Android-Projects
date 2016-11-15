package elango.thaaru.MyFinance;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ElaSummaryBar extends HorizontalScrollView 
{
	private static final String CLASS = "ElaSummaryBar ";
	private LinearLayout mSummaryLayout;
	private int mMaxRows = 1;
	
	public int getMaxRows() 
	{
		return mMaxRows;
	}

	public void setMaxRows(int maxRows) 
	{
		this.mMaxRows = maxRows;
	}

	public ElaSummaryBar(Context context,AttributeSet attributeSet)
	{
		super(context,attributeSet);
		setHorizontalScrollBarEnabled(false);
		mSummaryLayout = new LinearLayout(context);
		addView(mSummaryLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
	}

	public void setSummaryItems(Map<Integer,ElaSimpleBean> dataMap)
	{
		mSummaryLayout.removeAllViews();
		int index = 0;
		for(Entry<Integer, ElaSimpleBean> mapEntry : dataMap.entrySet())
		{
			ElaSimpleBean elaSimpleBean = mapEntry.getValue();
			String summary = elaSimpleBean.getStrVal2()+" : "+elaSimpleBean.getStrVal1();
			ElaSummaryItem elaSummaryItem = new ElaSummaryItem(getContext());
			elaSummaryItem.setText(summary);
			elaSummaryItem.setIndex(index);
			elaSummaryItem.setPadding(10,10,0,10);
			mSummaryLayout.addView(elaSummaryItem, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
			requestLayout();
			index++;
		}
	}	
	
	public static class ElaSummaryItem extends TextView
	{
		private int mIndex;
		
		public ElaSummaryItem(Context context)
		{
			super(context,null,R.attr.summaryItemAppearance);
		}
		
		public void setIndex(int index)
		{
			mIndex = index;
		}
		
		public int getIndex()
		{
			return mIndex;
		}		
	}
	
}

