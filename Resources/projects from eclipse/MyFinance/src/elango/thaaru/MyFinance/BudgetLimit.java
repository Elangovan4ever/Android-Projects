package elango.thaaru.MyFinance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class BudgetLimit extends ElaCommonActivity 
implements LoaderManager.LoaderCallbacks<Cursor>,ElaCommonInterface
{
	private ViewPager mViewPager;
	private static final int SUB_CATEGORY_LOADER_ID = 1;
	private Map<Integer,String> mCategoriesMapByIndex = new LinkedHashMap<Integer,String>();
	private Map<Integer,String> mCategoriesMapById = new LinkedHashMap<Integer,String>();
	
	private Map<Integer,ArrayList<ElaSimpleBean>> mSubCategoriesMap = new LinkedHashMap<Integer,ArrayList<ElaSimpleBean>>();
	
	/*********************************************************************************
   	 * Initial function that is called on creation of activity
   	 *********************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        super.enableTitleFeatures();
        setContentView(R.layout.create_transaction);        
        super.setTitleStyle(getString(R.string.add_transaction));
        
        //Showing the Categories Which already loaded in home screen
        showCategoriesInScreen(ElaCommonClass.getCategories());
    }
    
	/*********************************************************************************
	 * Code to load the sub categories
	 *********************************************************************************/
	public void loadSubCategories() 
	{
		getSupportLoaderManager().initLoader(SUB_CATEGORY_LOADER_ID, null, this);
	}
	
	
    /*********************************************************************************
   	 * Code to initiate the view pager which holds horizontal screens 
   	 *********************************************************************************/
    public void initiateViewPager()
    {    	 
    	mViewPager = (ViewPager) findViewById(R.id.vpAddTransactionPager);
    	
    	int categoryCount = mCategoriesMapByIndex.size();
    	//This will keep the data entered for all fragments
    	mViewPager.setOffscreenPageLimit(categoryCount);
    	
    	//Creating all fragments and passing them to pagerAdapter
    	Map<Integer,Fragment> transactionFragmentMap = new LinkedHashMap<Integer,Fragment>();
    	for(int i =0; i<categoryCount; i++)
    	{
    		CreateTransactionFragment createTransactionFragment = new CreateTransactionFragment();
    		transactionFragmentMap.put(i, createTransactionFragment);
    	}
    	
    	//Creating adapter for view pager. This adapter is custom adapter
    	ElaPagerAdapter elaPagerAdapter = new ElaPagerAdapter(getSupportFragmentManager(),transactionFragmentMap,mCategoriesMapByIndex);
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
    
    public void handleSelectorEvent()
	{
    	loadFragmantData();
	}
    
    public void showCategoriesInScreen(Cursor categorysCursor)
	{    	
		categorysCursor.moveToFirst();
		int i=0;
		for (;!categorysCursor.isAfterLast();categorysCursor.moveToNext()) 
		{
			int category = categorysCursor.getInt(categorysCursor.getColumnIndex(G.COLUMN_ID));
			//Hiding the unnecessary category for this screen
			if(category == G.CATEGORY_ANY)
				continue;
			String categoryDesc = categorysCursor.getString(categorysCursor.getColumnIndex(G.COLUMN_DESCRIPTION));
			int categoryStrResourceId = getResources().getIdentifier(categoryDesc, "string", getPackageName());
			categoryDesc = getString(categoryStrResourceId);
			
			//mapping description against the category Id
			mCategoriesMapById.put(category,categoryDesc);
			
			//mapping description against the index. This will be used by the ViewPager
			mCategoriesMapByIndex.put(i++,categoryDesc);
		}	
	
		//After showing the categories in screen , loading the sub category.
		loadSubCategories();		
	}
    
    public void loadFragmantData()
    {
    	//Getting the current displayed fragment
    	int index = mViewPager.getCurrentItem();
    	ElaPagerAdapter adapter = ((ElaPagerAdapter)mViewPager.getAdapter());
    	CreateTransactionFragment fragment = (CreateTransactionFragment) adapter.getFragment(index);
    	
    	//Getting the current selected categoryId
    	ElaTabBar elaTabBar = (ElaTabBar) findViewById(R.id.catogoryTabBar);
		int categoryId = ElaCommonClass.getInstance(this).getSelectedTabId(elaTabBar);
		
		//Getting the sub categories for selected category Id and passing it to the fragment
		ArrayList<ElaSimpleBean> elaSpinnerItems = mSubCategoriesMap.get(categoryId);
		if(elaSpinnerItems != null)
		{
			fragment.showSubCategories(elaSpinnerItems,categoryId);
		}  
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

    /*********************************************************************************
   	 * Implementation of Cursor Loader Methods*
   	 * 
   	 * Code to load the list of Categories
   	 *********************************************************************************/
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
	{
		CursorLoader cursorLoader = null;
		switch (id) 
		{
			case SUB_CATEGORY_LOADER_ID:
				//Loading data from sub categories table
				Uri subCategoriesUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI,G.TABLE_SUB_CATEGORIES);
				cursorLoader = new CursorLoader(this,subCategoriesUri,null,null,null,null);
				break;
		}
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) 
	{		
		switch (cursorLoader.getId()) 
		{
			case SUB_CATEGORY_LOADER_ID:
				//Preparing a map which contains an array list of sub categories.
				// Each sub categories array list is mapped to the particular category Id
				mSubCategoriesMap.clear();
				cursor.moveToFirst();
				for (;!cursor.isAfterLast();cursor.moveToNext()) 
				{ 
					int subCategoryId = cursor.getInt(cursor.getColumnIndex(G.COLUMN_ID));
					String subCategoryDesc = cursor.getString(cursor.getColumnIndex(G.COLUMN_DESCRIPTION));
					int categoryId = cursor.getInt(cursor.getColumnIndex(G.COLUMN_CATEGORY_ID));
					ArrayList<ElaSimpleBean> elaSpinnerItems;
					
					if(mSubCategoriesMap.get(categoryId) != null)
					{
						G.log("adding presented subcategory subCategoryId: "+subCategoryId);
						elaSpinnerItems = mSubCategoriesMap.get(categoryId);				
						elaSpinnerItems.add(new ElaSimpleBean(subCategoryId,subCategoryDesc));
					}
					else
					{
						elaSpinnerItems = new ArrayList<ElaSimpleBean>();
						G.log("adding subcategory subCategoryId: "+subCategoryId+" subCategoryDesc: "+subCategoryDesc);
						elaSpinnerItems.add(new ElaSimpleBean(subCategoryId,subCategoryDesc));
						G.log("adding subcategory elaSpinnerItems size: "+elaSpinnerItems.size());
						mSubCategoriesMap.put(categoryId,elaSpinnerItems);
					}
				}
				G.log("cursor count: "+cursor.getCount());
				// Since the data is loaded, this function will create the view pager
				// View pager will come up with one default first fragment
				initiateViewPager();
				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) 
	{
		switch (cursorLoader.getId()) 
		{
			case SUB_CATEGORY_LOADER_ID:
				break;
		}
	}   

	
    
}