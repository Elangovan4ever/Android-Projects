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

public class ElaTabBar extends HorizontalScrollView 
implements ViewPager.OnPageChangeListener
{
	private static final String CLASS = "ElaTabBar ";
	private ViewPager mViewPager;
	private static LinearLayout mTabLayout;
	private ElaCommonInterface mElaCommonInterface;
	
	public ElaTabBar(Context context,AttributeSet attributeSet)
	{
		super(context,attributeSet);
		setHorizontalScrollBarEnabled(false);
		mTabLayout = new LinearLayout(context);
		addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
	}

	public void setViewPager(ViewPager viewPager,Map<Integer,String> dataMap,Context activityReference)
	{
		mElaCommonInterface = (ElaCommonInterface) activityReference;
		mTabLayout.removeAllViews();
		mViewPager = viewPager;
		mViewPager.setOnPageChangeListener(this);
		
		PagerAdapter adapter = mViewPager.getAdapter();
		int index = 0;
		for(Entry<Integer, String> mapEntry : dataMap.entrySet())
		{
			CharSequence title = adapter.getPageTitle(index);
			ElaTabItem elaTabItem = new ElaTabItem(getContext());
			elaTabItem.setText(title);
			elaTabItem.setIndex(index);
			int dataKey = mapEntry.getKey();
			elaTabItem.setTag(dataKey);
			mTabLayout.addView(elaTabItem, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
			requestLayout();
			index++;
		}
		setCurrentTabItem(0);
	}	
	
	public void setCurrentTabItem(int index)
	{
		mViewPager.setCurrentItem(index);
		
		int tabsCount = mTabLayout.getChildCount();
		for(int i=0;i<tabsCount;i++)
		{
			View view = mTabLayout.getChildAt(i);
			boolean isSelected = (index == i);
			view.setSelected(isSelected);
			if (isSelected) 
			{
				//G.log("setCurrentTabItem selected View Id: "+i);
                animateToCurrentTab(index);
            }
		}
		mElaCommonInterface.handleSelectorEvent();
		
		
	}
	
	
	public void animateToCurrentTab(int index)
	{
		final ElaTabItem elaTabItem = (ElaTabItem) mTabLayout.getChildAt(index);
		Runnable runnableThread = new Runnable() 
		{			
			@Override
			public void run() 
			{
				final int scrollPos = elaTabItem.getLeft() - (getWidth() - elaTabItem.getWidth()) / 2;
				smoothScrollTo(scrollPos, 0);
			}
		};
		post(runnableThread);
		
	}


	@Override
	public void onPageScrollStateChanged(int arg0) 
	{
		//Log.d("L","onPageScrollStateChanged");
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) 
	{
		//Log.d("L","onPageScrolled");
	}

	@Override
	public void onPageSelected(int position)
	{
		setCurrentTabItem(position);
		//Log.d("L","onPageSelected");
	}
	
	public static class ElaTabItem extends TextView
	{
		private int mIndex;
		
		public ElaTabItem(Context context)
		{
			super(context,null,R.attr.tabItemAppearance);
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

