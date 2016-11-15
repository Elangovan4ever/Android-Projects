package elango.thaaru.MyFinance;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ManageSubCategoriesFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private SimpleCursorAdapter mSubCategoriesSimpleCursorAdapter;
	private ElaQuickAction mQuickActionBar;
	private static final String CLASS = "ManageSubCategoriesFragment ";
	private long mSelectedSubCategoryId;
	private String mSelectedSubCategoryDescription;
	private View mInflatedView;
	private int mCategoryId = G.NOTHING_INT;
	
	private boolean mIsLoadedAlready = false;
	
	private static final int SUB_CATEGORIES_LOADER_ID = 1;

	/*********************************************************************************
	 * First function called when the fragment created before draw themselves
	 *********************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/*********************************************************************************
	 * Function called when the fragment is drawn in current view
	 *********************************************************************************/
	@Override
	public View onCreateView(LayoutInflater layoutInflater,
			ViewGroup container, Bundle savedInstanceState) 
	{
		mInflatedView = layoutInflater.inflate(R.layout.manage_sub_categories_fragment, container,
				false);
		return mInflatedView;
	}

	/*********************************************************************************
	 * Function called after the fragment drawn in user view
	 *********************************************************************************/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		View[] QuickActionItems = initializeQuickActionBar();
		setSubCategoriesViewBinding();
		registerEventHandlers(QuickActionItems);
	}
	
	/*********************************************************************************
	 * Code to set all event listeners to handle the events
	 *********************************************************************************/
	public void registerEventHandlers(View[] QuickActionItems) 
	{
		final ListView listViewSubCategories = (ListView) mInflatedView.findViewById(R.id.lvSubCategories);
		listViewSubCategories.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,int position, long id) 
			{
				Log.d(G.L, CLASS + "Clicked id: " + id);
				mQuickActionBar.showQucikAction(view);
				mSelectedSubCategoryId = id;
				mSelectedSubCategoryDescription = ((TextView) view).getText().toString();
			}
		});
		
		TextView btnAddSubCategory = (TextView) mInflatedView.findViewById(R.id.btnAddSubCategory);
		btnAddSubCategory.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				onClickAddSubCategory(view);
			}
		});
		
		QuickActionItems[0].setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				onClickEditSubCategory(view);
				
			}
		});

		QuickActionItems[1].setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				onClickDeleteSubCategory(view);
			}
		});
	}
	
    public void loadFragmantData()
    {
    	//If fragment already shown once, then no need of doing this again.
    	// Even though fragments data kept alive, this condition needed since 
    	// this function is called from activity every time you scroll
    	if(mIsLoadedAlready)
    		return;
    	
    	mIsLoadedAlready = true;    	
		
    	Handler handler=new Handler();
		final Runnable r = new Runnable()
		{
		    public void run() 
		    {
		    	//Getting the current selected categoryId
		    	ElaTabBar elaTabBar = (ElaTabBar) getActivity().findViewById(R.id.catogoryTabBar);
				mCategoryId = ElaCommonClass.getInstance(getActivity()).getSelectedTabId(elaTabBar);
				G.log("loadFragmantData categoryId: "+mCategoryId);
		    	loadSubCategories();
		    }
		};
		handler.postDelayed(r,200);
    	//loadSubCategories();
    }
    
    /*********************************************************************************
	 * Code to load the sub categories
	 *********************************************************************************/
	public void loadSubCategories() 
	{
		getLoaderManager().initLoader(SUB_CATEGORIES_LOADER_ID, null, this);
	}
	
	/*********************************************************************************
	 * Code to add the sub categories
	 *********************************************************************************/
	public void onClickAddSubCategory(View view) 
	{
		AlertDialog.Builder dialogAddSubCategory = new AlertDialog.Builder(getActivity());
		dialogAddSubCategory.setTitle(getString(R.string.add_sub_category));
		String promptMessage = ElaCommonClass.getInstance(getActivity()).getCategoryDescription(mCategoryId);
		promptMessage += " - " + getString(R.string.sub_category) +" "+ getString(R.string.description);
		dialogAddSubCategory.setMessage(promptMessage);

		// Set an EditText view to get user input 
		final EditText etSubCategoyDesc = new EditText(getActivity());
		dialogAddSubCategory.setView(etSubCategoyDesc);

		dialogAddSubCategory.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				//inserting data in DB using content resolver
				Uri subCategoriesUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI,G.TABLE_SUB_CATEGORIES);
				ContentValues subCategoryValues = new ContentValues();
				subCategoryValues.put(G.COLUMN_DESCRIPTION, etSubCategoyDesc.getText().toString());
				subCategoryValues.put(G.COLUMN_CATEGORY_ID, mCategoryId);
				getActivity().getContentResolver().insert(subCategoriesUri,subCategoryValues);
				getActivity().getContentResolver().notifyChange(subCategoriesUri,null);
				mQuickActionBar.dismiss();
				Toast toast = Toast.makeText(getActivity(),R.string.success,Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER,0,0);
				toast.show();				
			}
		});

		dialogAddSubCategory.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
		{
		  public void onClick(DialogInterface dialog, int whichButton) 
		  {
			  mQuickActionBar.dismiss();
		  }
		});

		dialogAddSubCategory.show();
	}
	
	/*********************************************************************************
	 * Code to add the sub categories
	 *********************************************************************************/
	public void onClickEditSubCategory(View view) 
	{
		AlertDialog.Builder dialogAddSubCategory = new AlertDialog.Builder(getActivity());
		dialogAddSubCategory.setTitle(getString(R.string.edit_sub_category));
		String promptMessage = getString(R.string.old_value)+" : " + mSelectedSubCategoryDescription;
		dialogAddSubCategory.setMessage(promptMessage);
		
		LinearLayout lytAddSubCategory = new LinearLayout(getActivity());
		lytAddSubCategory.setOrientation(LinearLayout.VERTICAL);
		
		// Set an EditText view to get user input 
		final TextView tvNewSubCategoyLabel = new TextView(getActivity());
		tvNewSubCategoyLabel.setText(getString(R.string.new_value));
		lytAddSubCategory.addView(tvNewSubCategoyLabel);

		// Set an EditText view to get user input 
		final EditText etSubCategoyDesc = new EditText(getActivity());
		lytAddSubCategory.addView(etSubCategoyDesc);
		
		dialogAddSubCategory.setView(lytAddSubCategory);

		dialogAddSubCategory.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				//inserting data in DB using content resolver
				Uri subCategoriesUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI,G.TABLE_SUB_CATEGORIES);
				ContentValues subCategoryValues = new ContentValues();				
				subCategoryValues.put(G.COLUMN_DESCRIPTION, etSubCategoyDesc.getText().toString());
				String subCategoryId = String.valueOf(mSelectedSubCategoryId);
				getActivity().getContentResolver().update(subCategoriesUri,subCategoryValues,G.COLUMN_ID + "=?", new String[]{subCategoryId});
				getActivity().getContentResolver().notifyChange(subCategoriesUri,null);
				mQuickActionBar.dismiss();
				Toast toast = Toast.makeText(getActivity(),R.string.success,Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER,0,0);
				toast.show();				
			}
		});

		dialogAddSubCategory.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
		{
		  public void onClick(DialogInterface dialog, int whichButton) 
		  {
			  mQuickActionBar.dismiss();
		  }
		});

		dialogAddSubCategory.show();
	}
	
	/*********************************************************************************
	 * Code to add the sub categories
	 *********************************************************************************/
	public void onClickDeleteSubCategory(View view) 
	{
		AlertDialog.Builder dialogDeleteSubCategory = new AlertDialog.Builder(getActivity());
		dialogDeleteSubCategory.setTitle(getString(R.string.delete_sub_category));
		String promptMessage = getString(R.string.are_you_sure_delete) + " " + mSelectedSubCategoryDescription;
		dialogDeleteSubCategory.setMessage(promptMessage);

		dialogDeleteSubCategory.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				Uri subCategoriesUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI, G.TABLE_SUB_CATEGORIES);
				String subCategoryId = String.valueOf(mSelectedSubCategoryId);
				Log.d(G.L, CLASS + "idToDelete: " + subCategoryId);
				getActivity().getContentResolver().delete(subCategoriesUri,G.COLUMN_ID + "=?", new String[]{subCategoryId});
				getActivity().getContentResolver().notifyChange(subCategoriesUri,null);
				mQuickActionBar.dismiss();
			}
		});

		dialogDeleteSubCategory.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
		{
		  public void onClick(DialogInterface dialog, int whichButton) 
		  {
			  mQuickActionBar.dismiss();
		  }
		});

		dialogDeleteSubCategory.show();
	}
	
	/*********************************************************************************
	 * Code to set sub categories view binding to display data in readable format
	 *********************************************************************************/
	public void setSubCategoriesViewBinding() 
	{
		String[] columns = { G.COLUMN_DESCRIPTION};
		int[] views = { R.id.tvSubCategory};
		mSubCategoriesSimpleCursorAdapter = new SimpleCursorAdapter(getActivity(),R.layout.manage_sub_categories_list_item, null, columns, views, 0);
		ListView listViewSubCategories = (ListView) mInflatedView.findViewById(R.id.lvSubCategories);
		listViewSubCategories.setAdapter(mSubCategoriesSimpleCursorAdapter);
		mSubCategoriesSimpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() 
		{
			public boolean setViewValue(View view, Cursor cursor,int columnIndex) 
			{
				switch (view.getId()) 
				{
					case android.R.id.text1:
						String subCategory = cursor.getString(columnIndex);
						((TextView) view).setText(subCategory);
						return true;
				}
				return false;
			}
		});
	}

	/*********************************************************************************
	 * Code to set up the Quick action bar that can be shown when you click a
	 * transaction
	 *********************************************************************************/
	public View[] initializeQuickActionBar() 
	{
		mQuickActionBar = new ElaQuickAction(getActivity());
		View editTransactionView = mQuickActionBar.addQuickActionItem("Edit",R.drawable.edit_icon);
		View deleteTransactionView = mQuickActionBar.addQuickActionItem("Delete", R.drawable.delete_icon);
		View[] QuickActionItems = { editTransactionView, deleteTransactionView };
		return QuickActionItems;
	}

	/*********************************************************************************
	 * Implementation of Cursor Loader Methods*
	 * 
	 * Code to load the accounts using cursor loader
	 *********************************************************************************/
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
	{
		CursorLoader cursorLoader = null;
		switch (id) 
		{
			case SUB_CATEGORIES_LOADER_ID:				
				//Loading data from sub categories table
				Uri subCategoriesUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI,G.TABLE_SUB_CATEGORIES);
				cursorLoader = new CursorLoader(getActivity(),subCategoriesUri,null,G.COLUMN_CATEGORY_ID+"=?",new String[]{Integer.toString(mCategoryId)},null);
				break;
		}
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) 
	{
		cursor.setNotificationUri(getActivity().getContentResolver(),ElaContentProvider.CONTENT_URI);
		switch (cursorLoader.getId()) 
		{
			case SUB_CATEGORIES_LOADER_ID:
				Log.d(G.L, CLASS + " cursor size:"+cursor.getCount());
				mSubCategoriesSimpleCursorAdapter.swapCursor(cursor);
				break;
			default:
				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) 
	{
		switch (cursorLoader.getId()) 
		{
			case SUB_CATEGORIES_LOADER_ID:
				mSubCategoriesSimpleCursorAdapter.swapCursor(null);
				break;
			default:
				break;
		}
	}

}
