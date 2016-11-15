package elango.thaaru.MyFinance;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class StatementFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> 
{
	private SimpleCursorAdapter mStatementsSimpleCursorAdapter;
	private ElaQuickAction mQuickActionBar;
	private long mListItemId;
	private Bundle mBundleData;
	private View mInflatedView;
	private int mCategoryId = G.NOTHING_INT;
	
	private static final int VIEW_ACCOUNTS_LOADER_ID = 1;
	private static final int VIEW_ACCOUNTS_SUMMARY_BY_CAT_LOADER_ID = 2;
	private static final int VIEW_ACCOUNTS_SUMMARY_BY_SUB_CAT_LOADER_ID = 3;

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
		mBundleData = getArguments();
		G.log("mBundleData :"+mBundleData);
		mInflatedView = layoutInflater.inflate(R.layout.statement_fragment, container,
				false);
		return mInflatedView;
	}

	/*********************************************************************************
	 * Function called after the fragment drawn in user view
	 *********************************************************************************/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		G.log("statementFragment onActivityCreated called");
		super.onActivityCreated(savedInstanceState);
		if(mBundleData != null)
			loadLatestTransactions();
		
		View[] QuickActionItems = initializeQuickActionBar();
		registerEventHandlers(QuickActionItems);
	}

	/*********************************************************************************
	 * Code to load the specific number of recent transactions from SQLite
	 * through content provider and using cursor loader
	 *********************************************************************************/
	public void loadLatestTransactions() 
	{
		G.log("============================statementFragment=============================================");
		//Getting the current selected categoryId
    	ElaTabBar elaTabBar = (ElaTabBar) getActivity().findViewById(R.id.catogoryTabBar);
		mCategoryId = ElaCommonClass.getInstance(getActivity()).getSelectedTabId(elaTabBar);
		
		String[] columns = new String[] { G.COLUMN_DESCRIPTION,G.COLUMN_DESCRIPTION1, G.COLUMN_ACTION_DATE, G.COLUMN_COMMENT, G.COLUMN_AMOUNT };
		int[] views = new int[] { R.id.tvSubCategory, R.id.tvCategory,R.id.tvActionDate, R.id.tvComment, R.id.tvAmount};
		mStatementsSimpleCursorAdapter = new SimpleCursorAdapter(getActivity(),R.layout.statement_info_views, null, columns, views, 0);
		ListView listViewStatement = (ListView) mInflatedView.findViewById(R.id.lvStatement);
		listViewStatement.setAdapter(mStatementsSimpleCursorAdapter);
		mStatementsSimpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() 
		{
			public boolean setViewValue(View view, Cursor cursor,int columnIndex) 
			{
				switch (view.getId()) 
				{
					case R.id.tvActionDate:
						String dbDateString = cursor.getString(columnIndex);
						Date dbDate = new Date();
						try 
						{
							dbDate = G.dateTimeFormatterFromDB.parse(dbDateString);
						} 
						catch (ParseException e) 
						{
							e.printStackTrace();
						}
						String formattedDateString = G.dateTimeFormatterFromDisplay.format(dbDate);
						((TextView) view).setText(formattedDateString);
						return true;
					case R.id.tvCategory:
						String category = cursor.getString(columnIndex);
						int resourceId = getResources().getIdentifier(category, "string",getActivity().getPackageName());
						((TextView) view).setText(getString(resourceId));
						return true;
				}
				return false;
			}
		});
		getLoaderManager().initLoader(VIEW_ACCOUNTS_LOADER_ID, null, this);
		
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
	 * Code to set all event listeners to handle the events
	 *********************************************************************************/
	public void registerEventHandlers(View[] QuickActionItems) 
	{
		ListView listViewStatement = (ListView) mInflatedView.findViewById(R.id.lvStatement);
		listViewStatement.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,int position, long id) 
			{
				G.log("Clicked id: " + id);
				mQuickActionBar.showQucikAction(view);
				mListItemId = id;
			}
		});

		QuickActionItems[0].setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(getActivity(), EditTransaction.class);
				G.log("mListItemId: " + mListItemId);
				intent.putExtra(G.KEY_ACCOUNTS_ID, mListItemId);
				startActivity(intent);
				mQuickActionBar.dismiss();
			}
		});

		QuickActionItems[1].setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Uri accountsUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI, G.TABLE_ACCOUNTS);
				String idToDelete = String.valueOf(mListItemId);
				G.log("idToDelete: " + idToDelete);
				getActivity().getContentResolver().delete(accountsUri,G.COLUMN_ID + "=?", new String[]{idToDelete});
				getActivity().getContentResolver().notifyChange(accountsUri,null);
				mQuickActionBar.dismiss();
			}
		});
	}

	public CursorLoader loadAccountsBySelectParams(int cursorLoaderId)
	{
		CursorLoader cursorLoader = null;
		String whereColumns = null;
		String[] whereArgs = null;
		List<String> whereArgsList = new ArrayList<String>();
		boolean hasSelectionArgs = false;
		String prefix = "";
		
		//bundle data will be null for latest transactions
		//bundle data will be sent when search params selected
		if (mBundleData != null) 
		{
			int subCategoryId = mBundleData.getInt(G.KEY_SUB_CATEGORY_ID);
			if (subCategoryId != G.NOTHING_INT) 
			{
				whereColumns = G.COLUMN_SUB_CATEGORY_ID + "=?";
				whereArgsList.add(Integer.toString(subCategoryId));
				hasSelectionArgs = true;
			}
			
			String periodStr = mBundleData.getString(G.KEY_PERIOD);
			if(!periodStr.equals(G.NOTHING_STR))
			{
				String[] DateArray = periodStr.split("~");
				String fromDBDate = DateArray[0];
				String toDBDate = DateArray[1];
				G.log("fromDBDate: " + fromDBDate);
				G.log("toDBDate: " + toDBDate);
				prefix = (hasSelectionArgs == true) ? whereColumns + " and " : "";
				whereColumns = prefix + G.COLUMN_ACTION_DATE + " between ? and ?";
				whereArgsList.add(fromDBDate);
				whereArgsList.add(toDBDate);
			}
			
			String comment = mBundleData.getString(G.KEY_COMMENT);
			if (!comment.equals(G.NOTHING_STR)) 
			{
				prefix = (hasSelectionArgs == true) ? whereColumns + " and " : "";
				whereColumns = prefix + "upper("+ G.COLUMN_COMMENT + ") like ?";
				whereArgsList.add("%"+comment.toUpperCase()+"%");
			}
		}
		G.log("FINAL mCategoryId: " + mCategoryId);
		if(mCategoryId != G.NOTHING_INT && mCategoryId != G.CATEGORY_ANY) 
		{
			prefix = (hasSelectionArgs == true) ? whereColumns + " and " : "";
			whereColumns = prefix + G.COLUMN_CATEGORY_ID + "=?";
			whereArgsList.add(Integer.toString(mCategoryId));
		}
				
		if (whereArgsList.size() != 0)
			whereArgs = whereArgsList.toArray(new String[whereArgsList.size()]);
		G.log("FINAL whereColumns: " + whereColumns);
		G.log("FINAL whereArgs: " + whereArgs);
		Uri accountsUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI,G.VIEW_ACCOUNTS);
		String groupBy = null;
		String[] projection = null;
		G.log("FINAL cursorLoaderId: " + cursorLoaderId);
		if (cursorLoaderId == VIEW_ACCOUNTS_SUMMARY_BY_CAT_LOADER_ID) 
		{
			groupBy = G.COLUMN_CATEGORY_ID;
			String[] tmpProjection = new String[] { G.COLUMN_CATEGORY_ID,G.COLUMN_DESCRIPTION1,
					"sum(" + G.COLUMN_AMOUNT + ") as " + G.COLUMN_AMOUNT };
			projection = tmpProjection;
		}
		if (cursorLoaderId == VIEW_ACCOUNTS_SUMMARY_BY_SUB_CAT_LOADER_ID) 
		{
			groupBy = G.COLUMN_SUB_CATEGORY_ID;
			String[] tmpProjection = new String[] { G.COLUMN_SUB_CATEGORY_ID,G.COLUMN_DESCRIPTION,
					"sum(" + G.COLUMN_AMOUNT + ") as " + G.COLUMN_AMOUNT };
			projection = tmpProjection;
		}		
		
		G.log("FINAL groupBy: " + groupBy);
		G.log("FINAL projection: " + projection);
		if(whereArgs!= null)
		{
		G.log("FINAL whereArgs.length: " + whereArgs.length);
		G.log("FINAL whereArgs[0]: " + whereArgs[0]);
		}
		cursorLoader = new CursorLoader(getActivity(), accountsUri, projection, whereColumns, whereArgs, groupBy);
		return cursorLoader;
	}

	public void setAccountsSummaryOnScreen(Cursor accountsSummaryCursor) 
	{
		G.log("accountsSummaryCursor size:"+accountsSummaryCursor.getCount());
		int i = 0;
		Map<Integer,ElaSimpleBean> summaryItemsMap = new LinkedHashMap<Integer,ElaSimpleBean>();
		accountsSummaryCursor.moveToFirst();
		while (!accountsSummaryCursor.isAfterLast()) 
		{
			String summaryDescription = "";
			int summaryId = G.NOTHING_INT;
			if(mCategoryId == G.CATEGORY_ANY)
			{
				String categoryStr = accountsSummaryCursor.getString(accountsSummaryCursor.getColumnIndex(G.COLUMN_DESCRIPTION1));
				int resourceId = getResources().getIdentifier(categoryStr,"string", getActivity().getPackageName());
				summaryDescription = getString(resourceId);
				summaryId = accountsSummaryCursor.getInt(accountsSummaryCursor.getColumnIndex(G.COLUMN_CATEGORY_ID));
			}
			else 
			{
				summaryDescription = accountsSummaryCursor.getString(accountsSummaryCursor.getColumnIndex(G.COLUMN_DESCRIPTION));
				summaryId = accountsSummaryCursor.getInt(accountsSummaryCursor.getColumnIndex(G.COLUMN_SUB_CATEGORY_ID));
			}
			String amount = accountsSummaryCursor.getString(accountsSummaryCursor.getColumnIndex(G.COLUMN_AMOUNT));
			
			summaryItemsMap.put(i,new ElaSimpleBean(summaryId,amount,summaryDescription));
			accountsSummaryCursor.moveToNext();
			i++;
		}
		
		// Creating the summary Items
		ElaSummaryBar elaSummaryBar = (ElaSummaryBar) mInflatedView.findViewById(R.id.accountSummaryBar);
		elaSummaryBar.setSummaryItems(summaryItemsMap);
	}
	

	/*********************************************************************************
	 * Implementation of Cursor Loader Methods*
	 * 
	 * Code to load the accounts using cursor loader
	 *********************************************************************************/
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
	{
		G.log("Entering onCreateLoader");
		
		CursorLoader cursorLoader = null;
		try
		{
			cursorLoader = loadAccountsBySelectParams(id);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// TODO: handle exception
		}
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) 
	{
		cursor.setNotificationUri(getActivity().getContentResolver(),ElaContentProvider.CONTENT_URI);
		switch (cursorLoader.getId()) 
		{
			case VIEW_ACCOUNTS_LOADER_ID:
				G.log("cursor size:"+cursor.getCount());
				if(cursor.getCount() == 0)
				{
					TextView noDataView = (TextView) mInflatedView.findViewById(R.id.noDataView);
					noDataView.setVisibility(View.VISIBLE);
				}
				else
				{
					mStatementsSimpleCursorAdapter.swapCursor(cursor);
				}
				if(mCategoryId == G.CATEGORY_ANY)
					getLoaderManager().initLoader(VIEW_ACCOUNTS_SUMMARY_BY_CAT_LOADER_ID, null, this);
				else
					getLoaderManager().initLoader(VIEW_ACCOUNTS_SUMMARY_BY_SUB_CAT_LOADER_ID, null, this);
				break;
			case VIEW_ACCOUNTS_SUMMARY_BY_CAT_LOADER_ID:
			case VIEW_ACCOUNTS_SUMMARY_BY_SUB_CAT_LOADER_ID:
				setAccountsSummaryOnScreen(cursor);
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
			case VIEW_ACCOUNTS_LOADER_ID:
				mStatementsSimpleCursorAdapter.swapCursor(null);
				break;
			case VIEW_ACCOUNTS_SUMMARY_BY_CAT_LOADER_ID:
				break;
			case VIEW_ACCOUNTS_SUMMARY_BY_SUB_CAT_LOADER_ID:
				break;
			default:
				break;
		}
	}

}
