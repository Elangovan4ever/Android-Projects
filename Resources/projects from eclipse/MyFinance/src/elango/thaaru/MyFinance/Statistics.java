package elango.thaaru.MyFinance;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class Statistics extends ElaCommonActivity 
implements ElaCommonInterface
{

	private ViewPager mViewPager;
	
	private Map<Integer,String> mCategoriesMapByIndex = new LinkedHashMap<Integer,String>();
	private Map<Integer,String> mCategoriesMapById = new LinkedHashMap<Integer,String>();
	
	Map<Integer,Float> mStatisticsDataMap = new LinkedHashMap<Integer,Float>();
	
	protected static Calendar mCalendarObj = Calendar.getInstance();
	
	/*********************************************************************************
   	 * Initial function that is called on creation of activity
   	 *********************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        super.enableTitleFeatures();
        setContentView(R.layout.statistics);
        super.setTitleStyle(getString(R.string.statistics));
        
        //Showing the Categories Which already loaded in home screen
        showCategoriesInScreen(ElaCommonClass.getCategories());
    }
    
    public void showCategoriesInScreen(Cursor categorysCursor)
	{    	
		categorysCursor.moveToFirst();
		int i=0;
		for (;!categorysCursor.isAfterLast();categorysCursor.moveToNext()) 
		{
			int category = categorysCursor.getInt(categorysCursor.getColumnIndex(G.COLUMN_ID));
			//Hiding the unnecessary category for this screen

			String categoryDesc = categorysCursor.getString(categorysCursor.getColumnIndex(G.COLUMN_DESCRIPTION));
			int categoryStrResourceId = getResources().getIdentifier(categoryDesc, "string", getPackageName());
			categoryDesc = getString(categoryStrResourceId);
			
			//mapping description against the category Id
			mCategoriesMapById.put(category,categoryDesc);
			
			//mapping description against the index. This will be used by the ViewPager
			mCategoriesMapByIndex.put(i++,categoryDesc);
		}
		
		initiateViewPager();
	}
    
    /*********************************************************************************
   	 * Code to initiate the view pager which holds horizontal screens 
   	 *********************************************************************************/
    public void initiateViewPager()
    {    	 
    	mViewPager = (ViewPager) findViewById(R.id.vpStatisticsPager);
    	
    	int categoryCount = mCategoriesMapByIndex.size();
    	//This will keep the data entered for all fragments
    	mViewPager.setOffscreenPageLimit(categoryCount);
    	
    	//Creating all fragments and passing them to pagerAdapter
    	Map<Integer,Fragment> statisticsFragmentMap = new LinkedHashMap<Integer,Fragment>();
    	for(int i =0; i<categoryCount; i++)
    	{
    		StatisticsFragment statisticsFragment = new StatisticsFragment();
    		statisticsFragmentMap.put(i, statisticsFragment);
    	}
    	
    	//Creating adapter for view pager. This adapter is custom adapter
    	ElaPagerAdapter elaPagerAdapter = new ElaPagerAdapter(getSupportFragmentManager(),statisticsFragmentMap,mCategoriesMapByIndex);
    	mViewPager.setAdapter(elaPagerAdapter);

    	//To Allow vertical Scroll view also work
    	mViewPager.setOnTouchListener(new View.OnTouchListener()
    	{
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    	
    	// Creating the Tab with categories
		ElaTabBar elaTabBar = (ElaTabBar) findViewById(R.id.catogoryTabBar);
		elaTabBar.setViewPager(mViewPager,mCategoriesMapById,this);
		
		registerSelectorEventHandlers();
    }
    
    public void registerSelectorEventHandlers()
    {
    	ElaTabBar elaTabBar = (ElaTabBar) findViewById(R.id.catogoryTabBar);
    	final LinearLayout categoriesLayout = (LinearLayout)elaTabBar.getChildAt(0);
		int childCount = categoriesLayout.getChildCount();
		//G.log("childCount: "+childCount);
		for(int i=0;i<childCount;i++)
		{
			ElaTabBar.ElaTabItem elaTabItem = (ElaTabBar.ElaTabItem) categoriesLayout.getChildAt(i);
			elaTabItem.setOnClickListener(new OnClickListener() 
			{				
				@Override
				public void onClick(View view) 
				{
					G.log("onClick called");
					//To scroll to the respective fragment based on selected categoryId
					int currTabIndex = ((ElaTabBar.ElaTabItem)view).getIndex();
					mViewPager.setCurrentItem(currTabIndex);
				}
			});
			
		}
    }
    
    public void handleSelectorEvent()
    {
    	loadFragmantData();
    }
    
    public void loadFragmantData()
    {
    	//Getting the current displayed fragment
    	int index = mViewPager.getCurrentItem();
    	ElaPagerAdapter adapter = ((ElaPagerAdapter)mViewPager.getAdapter());
    	StatisticsFragment fragment = (StatisticsFragment) adapter.getFragment(index);
    	
    	//Getting the current selected categoryId
    	ElaTabBar elaTabBar = (ElaTabBar) findViewById(R.id.catogoryTabBar);
		int categoryId = ElaCommonClass.getInstance(this).getSelectedTabId(elaTabBar);
		G.log("categoryId: "+categoryId);
		
		//passing the category Id for loading fragment data
		fragment.loadFragmantData(categoryId);
    }
}
