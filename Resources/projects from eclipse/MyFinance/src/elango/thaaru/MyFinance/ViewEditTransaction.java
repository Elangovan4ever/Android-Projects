package elango.thaaru.MyFinance;

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


public class ViewEditTransaction extends ElaCommonActivity implements ElaCommonInterface
{
	private ViewPager mViewPager;
	
	private Map<Integer,String> mCategoriesMapByIndex = new LinkedHashMap<Integer,String>();
	private Map<Integer,String> mCategoriesMapById = new LinkedHashMap<Integer,String>();

	/*********************************************************************************
   	 *  Initial function that is called on creation of activity
   	 *********************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        super.enableTitleFeatures();
        setContentView(R.layout.view_edit_transactions);
        super.setTitleStyle(getString(R.string.edit_transaction));
        
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
    	mViewPager = (ViewPager) findViewById(R.id.vpViewEditTransactionPager);
    	
    	int categoryCount = mCategoriesMapByIndex.size();
    	//Creating all fragments and passing them to pagerAdapter.
    	Map<Integer,Fragment> viewEditTransactionFragmentMap = new LinkedHashMap<Integer,Fragment>();
    	for(int i =0; i<categoryCount; i++)
    	{
    		ViewEditTransactionFragment viewEditTransactionFragment = new ViewEditTransactionFragment();
    		viewEditTransactionFragmentMap.put(i, viewEditTransactionFragment);
    	}
    	
    	//Creating adapter for view pager. This adapter is custom adapter.
    	ElaPagerAdapter elaPagerAdapter = new ElaPagerAdapter(getSupportFragmentManager(),viewEditTransactionFragmentMap,mCategoriesMapByIndex);
    	mViewPager.setAdapter(elaPagerAdapter);
    	G.log("after setting the adapter ");
    	
    	//To Allow vertical Scroll view also work
    	mViewPager.setOnTouchListener(new View.OnTouchListener()
    	{
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    	
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
    	G.log("Loadfragment called");
    	int index = mViewPager.getCurrentItem();
    	ElaPagerAdapter adapter = ((ElaPagerAdapter)mViewPager.getAdapter());
    	ViewEditTransactionFragment fragment = (ViewEditTransactionFragment) adapter.getFragment(index);
    	
    	//Getting the current selected categoryId
    	ElaTabBar elaTabBar = (ElaTabBar) findViewById(R.id.catogoryTabBar);
		int categoryId = ElaCommonClass.getInstance(this).getSelectedTabId(elaTabBar);
		G.log("loadFragmantData categoryId: "+categoryId);
		
		//passing the category Id for loading fragment data
		fragment.loadFragmantData(categoryId);
    }
}