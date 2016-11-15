package elango.thaaru.MyFinance;

import java.io.BufferedReader;
import java.io.FileReader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class ElaCommonClass 
{
	private static ElaCommonClass elaCommonClass = new ElaCommonClass( );
	private static Context mContext;
	private static String CLASS = "ElaCommonClass ";
	
	private static Cursor mCategoriesCusrsor = null;
	
	private ElaCommonClass(){}
	
	public static ElaCommonClass getInstance(Context context) 
	{
		mContext = context;
		return elaCommonClass;
	}
	
	public static void setCategories(Cursor categoriesCusrsor)
	{
		mCategoriesCusrsor = categoriesCusrsor;
	}
	
	public static Cursor getCategories()
	{
		return mCategoriesCusrsor;
	}
	
	public static void setSpinnerSelectionById(Spinner spinner,int itemId)
	{
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) spinner.getAdapter();
		for(int position=0;position<adapter.getCount();position++)
		{
			if(adapter.getItemId(position) == itemId)
			{
				spinner.setSelection(position);
				return;
			}
		}
		spinner.setSelection(0);
	}
	
	public int getSelectedTabId(ElaTabBar elaTabBar)
	{
		LinearLayout tabLayout = (LinearLayout)elaTabBar.getChildAt(0);
		int selectedValue = G.NOTHING_INT;		
		int childCount = tabLayout.getChildCount();
		//G.log(" childCount: "+childCount);
		for(int i=0;i<childCount;i++)
		{
			ElaTabBar.ElaTabItem elaTabItem = (ElaTabBar.ElaTabItem) tabLayout.getChildAt(i);
			if(elaTabItem.isSelected() == true && elaTabItem.getTag() != null)
			{
				selectedValue = (Integer) elaTabItem.getTag();
			}
		}
		return selectedValue;
	}
	
	public String getCategoryDescription(int categoryId)
	{
		//Setting the sub category label description based on category Id
		String subCategoryCaption="";
    	switch(categoryId)
    	{
    		case 0:
    			subCategoryCaption = mContext.getString(R.string.income_sub_categories);
    			break;
    		case 1:
    			subCategoryCaption = mContext.getString(R.string.expence_sub_categories);
    			break;
    		case 2:
    			subCategoryCaption = mContext.getString(R.string.deposit_sub_categories);
    			break;
    		case 3:
    			subCategoryCaption = mContext.getString(R.string.borrow_sub_categories);
    			break;
    		case 4:
    			subCategoryCaption = mContext.getString(R.string.lend_sub_categories);
    			break;
    	}
    	return subCategoryCaption;
    }
}
