package elango.thaaru.MyFinance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import android.R.integer;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StatisticsFragment extends Fragment
implements LoaderManager.LoaderCallbacks<Cursor>
{
	private static final int NODIRECTION = -1;
	private static final int FORWARD = 1;
	private static final int BACKWARD = 2;	
	private int mPeriodType = G.NOTHING_INT;
	private int mCategoryId = G.NOTHING_INT;
	private int mNoOfPeriodValues = G.NOTHING_INT;
	private Float mMaxAmount = G.NOTHING_FLOAT;
	private ArrayList<Integer> mPeriodKeys = null;
	private String mStatisticsDbViewName = G.NOTHING_STR;
	private View mInflatedView;
	Map<Integer,ElaGraphDataBean> mStatisticsDataMap = new LinkedHashMap<Integer,ElaGraphDataBean>();
	Map<Integer,ElaGraphDataBean> mStatisticsDataMapCopy = null;
	Map<Integer,ElaSimpleBean> mGraphOptionsItemsMap = new LinkedHashMap<Integer,ElaSimpleBean>();
	private static final int VIEW_STATISTICS_LOADER_ID = 1;
	protected Calendar mCalendarObj = Calendar.getInstance();
	private int mColors[] = new int[500];
	
	/*********************************************************************************
	 * First function called when the fragment created before draw themselves
	 *********************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//G.log(  "onCreate called");
		super.onCreate(savedInstanceState);
	}
	
	/*********************************************************************************
	 * Function called when the fragment is drawn in current view
	 *********************************************************************************/
	@Override
	public View onCreateView(LayoutInflater layoutInflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		G.log(  "In onCreateView called");
		mInflatedView = layoutInflater.inflate(R.layout.statistics_fragment, container,false);
		//G.log(  "mInflatedView: " + mInflatedView);

		return mInflatedView;
	}
	
	/*********************************************************************************
	 * Function called after the fragment drawn in user view
	 *********************************************************************************/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
        registerEventHandlers();
        setSelectedPeriodType(R.id.tvYearly);
        generateColors();
    }
	
	public void generateColors()
	{
		Random random = new Random();
		for(int i=0;i<mColors.length;i++)
		{
			mColors[i] = Color.rgb(random.nextInt(200), random.nextInt(200), random.nextInt(200));
		}
	}	
	
	/*********************************************************************************
   	 * Code to set all event listeners to handle the events
   	 *********************************************************************************/
    public void registerEventHandlers()
    {
    	final TextView tvYearly = (TextView)mInflatedView.findViewById(R.id.tvYearly);
    	tvYearly.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View v) 
			{
				if(tvYearly.isSelected())
					return;
				setSelectedPeriodType(R.id.tvYearly);
				loadStatisticsData(NODIRECTION);
			}
		});
    	
    	final TextView tvMonthly = (TextView)mInflatedView.findViewById(R.id.tvMonthly);
    	tvMonthly.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View v) 
			{
				if(tvMonthly.isSelected())
					return;
				setSelectedPeriodType(R.id.tvMonthly);
				loadStatisticsData(NODIRECTION);
			}
		});
    	
    	final TextView tvWeekly = (TextView)mInflatedView.findViewById(R.id.tvWeekly);
    	tvWeekly.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View v) 
			{
				if(tvWeekly.isSelected())
					return;
				setSelectedPeriodType(R.id.tvWeekly);
				loadStatisticsData(NODIRECTION);
			}
		});
    	
    	final TextView tvDaily = (TextView)mInflatedView.findViewById(R.id.tvDaily);
    	tvDaily.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View v) 
			{
				if(tvDaily.isSelected())
					return;
				setSelectedPeriodType(R.id.tvDaily);
				loadStatisticsData(NODIRECTION);
			}
		});
    	
    	Button btnRightDateNavigator = (Button)mInflatedView.findViewById(R.id.btnRightDateNavigator);
    	btnRightDateNavigator.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View v) 
			{
				loadStatisticsData(FORWARD);
			}
		});
    	
    	Button btnLeftDateNavigator = (Button)mInflatedView.findViewById(R.id.btnLeftDateNavigator);
    	btnLeftDateNavigator.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View v) 
			{
				loadStatisticsData(BACKWARD);
			}
		});
    	
    }
    
    public void setSelectedPeriodType(int periodTypeId)
    {
    	int periodTypeViewIds[] = {R.id.tvYearly,R.id.tvMonthly,R.id.tvWeekly,R.id.tvDaily};
    	int periodTypes[] = {G.YEARLY,G.MONTHLY,G.WEEKLY,G.DAILY};
    	for(int i=0;i<periodTypes.length;i++)
    	{
    		TextView txtPeriodType = (TextView)mInflatedView.findViewById(periodTypeViewIds[i]);
    		txtPeriodType.setSelected(false);
    		if(periodTypeId == periodTypeViewIds[i])
    		{
    			txtPeriodType.setSelected(true);
    			mPeriodType = periodTypes[i];
    		}
    	}
    	mCalendarObj = Calendar.getInstance();
    }
    
    public void loadFragmantData(int categoryId)
    {
		mCategoryId = categoryId;
		Handler handler=new Handler();
		G.log("loadFragmantData called ");
		final Runnable r = new Runnable()
		{
		    public void run() 
		    {
		    	loadStatisticsData(NODIRECTION);
		    }
		};
		handler.postDelayed(r,500);    	
    }
    
    public void loadStatisticsData(int direction)
    {
    	G.log("mPeriodType: "+mPeriodType);
    	
    	String dbFromDate = null;
		String dbToDate = null;
		String dateToDisplay = null;
		mStatisticsDataMap.clear();
		
		if (mPeriodType == G.YEARLY) 
		{
			if(direction == FORWARD)
				mCalendarObj.add(Calendar.YEAR, 1);
			else if(direction == BACKWARD)
				mCalendarObj.add(Calendar.YEAR, -1);				
			
			mCalendarObj.set(Calendar.DAY_OF_YEAR,mCalendarObj.getActualMaximum(Calendar.DAY_OF_YEAR));
			mCalendarObj.set(Calendar.HOUR_OF_DAY, 23);
			mCalendarObj.set(Calendar.MINUTE, 59);
				
			dbToDate = G.dateTimeFormatterToDB.format(mCalendarObj.getTime());
			mCalendarObj.set(Calendar.DAY_OF_YEAR, 1);
			dbFromDate = G.dateTimeFormatterToDB_SOD.format(mCalendarObj.getTime());
			dateToDisplay = G.dateFormatterToDisplay_year.format(mCalendarObj.getTime());
			
			mNoOfPeriodValues = 12;
		}
		if (mPeriodType == G.MONTHLY) 
		{
			if(direction == FORWARD)
				mCalendarObj.add(Calendar.MONTH, 1);
			else if(direction == BACKWARD)
				mCalendarObj.add(Calendar.MONTH, -1);				

			mCalendarObj.set(Calendar.DAY_OF_MONTH, mCalendarObj.getActualMaximum(Calendar.DAY_OF_MONTH));
			mCalendarObj.set(Calendar.HOUR_OF_DAY, 23);
			mCalendarObj.set(Calendar.MINUTE, 59);
			
			dbToDate = G.dateTimeFormatterToDB.format(mCalendarObj.getTime());
			mCalendarObj.set(Calendar.DAY_OF_MONTH, 1);
			dbFromDate = G.dateTimeFormatterToDB_SOD.format(mCalendarObj.getTime());
			dateToDisplay = G.dateFormatterToDisplay_monthYear.format(mCalendarObj.getTime());
			
			mNoOfPeriodValues = mCalendarObj.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		if (mPeriodType == G.WEEKLY) 
		{
			if(direction == FORWARD)
				mCalendarObj.add(Calendar.WEEK_OF_YEAR, 1);			
			if(direction == BACKWARD)
				mCalendarObj.add(Calendar.WEEK_OF_YEAR, -1);
			
			if(mCalendarObj.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				mCalendarObj.add(Calendar.WEEK_OF_YEAR, 1);
			mCalendarObj.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			mCalendarObj.set(Calendar.HOUR_OF_DAY, 23);
			mCalendarObj.set(Calendar.MINUTE, 59);
			
			dbToDate = G.dateTimeFormatterToDB.format(mCalendarObj.getTime());
			mCalendarObj.add(Calendar.WEEK_OF_YEAR, -1);
			mCalendarObj.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			dbFromDate = G.dateTimeFormatterToDB_SOD.format(mCalendarObj.getTime());
			dateToDisplay = G.dateFormatterToDisplay.format(mCalendarObj.getTime());
			
			mNoOfPeriodValues = 7;
		}
		
		if (mPeriodType == G.DAILY) 
		{
			if(direction == FORWARD)
				mCalendarObj.add(Calendar.DATE, 1);
			else if(direction == BACKWARD)
				mCalendarObj.add(Calendar.DATE, -1);				

			mCalendarObj.set(Calendar.HOUR_OF_DAY, 23);
			mCalendarObj.set(Calendar.MINUTE, 59);

			dbFromDate = G.dateTimeFormatterToDB_SOD.format(mCalendarObj.getTime());
			dbToDate = G.dateTimeFormatterToDB.format(mCalendarObj.getTime());
			dateToDisplay = G.dateFormatterToDisplay.format(mCalendarObj.getTime());
			mNoOfPeriodValues = 24;
		}
		G.log("dbFromDate: "+dbFromDate + " dbToDate: "+dbToDate );
		G.log("mStatisticsDataMap: "+mStatisticsDataMap);
		
		TextView tvDate = (TextView)mInflatedView.findViewById(R.id.tvDate);
		tvDate.setText(dateToDisplay);
		
		updateAttributeDetails(dbFromDate,dbToDate);
    }
    
    public  void updateAttributeDetails(String dbFromDate,String dbToDate)
    {
    	G.log("dbFromDate: "+dbFromDate + " dbToDate: "+dbToDate +" mCategoryId: "+mCategoryId+" mPeriodType: "+mPeriodType );
    	Uri attributeDetailsUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI,G.TABLE_ATTRIBUTE_DETAILS);
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		operations.add(ContentProviderOperation.newUpdate(attributeDetailsUri)
				.withSelection(G.COLUMN_ATTRIBUTE_CODE+"=? and "+G.COLUMN_ENTITY_TYPE_ID+"=?",new String[]{Integer.toString(G.ATTRIBUTE_START_DATE),Integer.toString(G.ENTITY_STATISTICS)})
				.withValue(G.COLUMN_ATTRIBUTE_VALUE, dbFromDate)
				.build());
		operations.add(ContentProviderOperation.newUpdate(attributeDetailsUri)
				.withSelection(G.COLUMN_ATTRIBUTE_CODE+"=? and "+G.COLUMN_ENTITY_TYPE_ID+"=?",new String[]{Integer.toString(G.ATTRIBUTE_END_DATE),Integer.toString(G.ENTITY_STATISTICS)})
				.withValue(G.COLUMN_ATTRIBUTE_VALUE, dbToDate)
				.build());
		operations.add(ContentProviderOperation.newUpdate(attributeDetailsUri)
				.withSelection(G.COLUMN_ATTRIBUTE_CODE+"=? and "+G.COLUMN_ENTITY_TYPE_ID+"=?",new String[]{Integer.toString(G.ATTRIBUTE_CATEGORY_ID),Integer.toString(G.ENTITY_STATISTICS)})
				.withValue(G.COLUMN_ATTRIBUTE_VALUE, Integer.toString(mCategoryId))
				.build());
		operations.add(ContentProviderOperation.newUpdate(attributeDetailsUri)
				.withSelection(G.COLUMN_ATTRIBUTE_CODE+"=? and "+G.COLUMN_ENTITY_TYPE_ID+"=?",new String[]{Integer.toString(G.ATTRIBUTE_PERIOD_TYPE),Integer.toString(G.ENTITY_STATISTICS)})
				.withValue(G.COLUMN_ATTRIBUTE_VALUE, Integer.toString(mPeriodType))
				.build());
		try 
		{
			getActivity().getContentResolver().applyBatch(ElaContentProvider.AUTHORITY,operations);
			mStatisticsDbViewName = G.VIEW_ACCOUNTS_STATISTICS_SCAT;
			if(mCategoryId == G.CATEGORY_ANY)
				mStatisticsDbViewName = G.VIEW_ACCOUNTS_STATISTICS_CAT;
			if(getLoaderManager().getLoader(VIEW_STATISTICS_LOADER_ID) != null)
			{
				G.log("Attribute Details updated and calling restartLoader");
				getLoaderManager().restartLoader(VIEW_STATISTICS_LOADER_ID, null, this);
			}
			else
			{
				G.log("Attribute Details updated and calling initLoader");
				getLoaderManager().initLoader(VIEW_STATISTICS_LOADER_ID, null, this);				
			}
			
		}
		catch (RemoteException e) 
		{
			e.printStackTrace();
		} 
		catch (OperationApplicationException e) 
		{
			e.printStackTrace();
		}
    }
    
    /*********************************************************************************
	 * Code to create the statistics graph
	 * using cursor loader
	 *********************************************************************************/
	public void createStatisticsGraph(Cursor statisticsCursor) 
	{
		G.log("createStatisticsGraph statisticsCursor.getCount(): "+statisticsCursor.getCount()+" mCategoryId: "+mCategoryId);
		Map<Integer,Float> periodDataMap = new LinkedHashMap<Integer,Float>();
		if(mPeriodType == G.WEEKLY)
		{
			Calendar tmpCalendar = (Calendar) mCalendarObj.clone();
			for(int i=1;i<=mNoOfPeriodValues;i++)
			{
				int dayOfMonth = tmpCalendar.get(Calendar.DAY_OF_MONTH);
				periodDataMap.put(dayOfMonth,0.0f);
				tmpCalendar.add(Calendar.DAY_OF_YEAR,1);
			}
		}
		else
		{
			for(int i=1;i<=mNoOfPeriodValues;i++)
			{
				periodDataMap.put(i,0.0f);
			}
		}
		
		mPeriodKeys = new ArrayList<Integer>(periodDataMap.keySet());

		statisticsCursor.moveToFirst();
		int noOfstatisticsRows = statisticsCursor.getCount();
		
		String catOrSubCatColName = G.NOTHING_STR;
		if(mCategoryId == G.CATEGORY_ANY)
			catOrSubCatColName = G.COLUMN_CATEGORY_ID;
		else
			catOrSubCatColName = G.COLUMN_SUB_CATEGORY_ID;
		
		mGraphOptionsItemsMap.clear();
		int colorsLength = mColors.length;
		for(int i=0;!statisticsCursor.isAfterLast();statisticsCursor.moveToNext(),i++)
		{
			int catOrSubCatId = statisticsCursor.getInt(statisticsCursor.getColumnIndex(catOrSubCatColName));
			String catOrSubCatDesc = statisticsCursor.getString(statisticsCursor.getColumnIndex(G.COLUMN_DESCRIPTION));
			if(mCategoryId == G.CATEGORY_ANY)
				catOrSubCatDesc = getString(getResources().getIdentifier(catOrSubCatDesc, "string", getActivity().getPackageName()));
			String amountStr = statisticsCursor.getString(statisticsCursor.getColumnIndex(G.COLUMN_AMOUNT));
			Float amount = Float.parseFloat(amountStr);
			String periodValue = statisticsCursor.getString(statisticsCursor.getColumnIndex(G.COLUMN_PERIOD_VALUE));			
			
			if(mStatisticsDataMap.containsKey(catOrSubCatId))
			{
				ElaGraphDataBean elaGraphDataBean = mStatisticsDataMap.get(catOrSubCatId);
				Map<Integer,Float> tempPeriodDataMap = elaGraphDataBean.getDataMap();
				tempPeriodDataMap.put(Integer.valueOf(periodValue),amount);
			}
			else
			{
				Map<Integer,Float> tempPeriodDataMap =  new LinkedHashMap<Integer,Float>(periodDataMap);
				tempPeriodDataMap.put(Integer.valueOf(periodValue),amount);
				
				if(colorsLength <= i) i = 0;
				//map to hold data for drawing graph
				ElaGraphDataBean elaGraphDataBean = new ElaGraphDataBean(catOrSubCatId,tempPeriodDataMap,mColors[i]); 
				mStatisticsDataMap.put(catOrSubCatId,elaGraphDataBean);
				
				//map to hold the check box options to enable disable graphs 
				ElaSimpleBean elaSimpleBean = new ElaSimpleBean(catOrSubCatId,catOrSubCatDesc);
				elaSimpleBean.setIntVal2(mColors[i]);
				mGraphOptionsItemsMap.put(catOrSubCatId,elaSimpleBean);
			}			
			mMaxAmount = (mMaxAmount < amount)? amount : mMaxAmount;
			G.log("catOrSubCatId: "+catOrSubCatId+" periodValue: "+periodValue+ " amount: "+amount);			
			
		}
		mStatisticsDataMapCopy = new LinkedHashMap<Integer,ElaGraphDataBean>(mStatisticsDataMap);
		G.log("mStatisticsDataMap: "+mStatisticsDataMap+" mMaxAmount:"+mMaxAmount);		
		TextView noDatePresentTextView = (TextView) mInflatedView.findViewById(R.id.noDataView);
		LinearLayout graphParentView = (LinearLayout) mInflatedView.findViewById(R.id.lytStatisticsContent);
		graphParentView.removeAllViews();
		int graphHeight = graphParentView.getHeight();
		int graphWidth = graphParentView.getWidth();
		G.log("graphHeight: "+graphHeight+" graphWidth: "+graphWidth);
		View btnGrpahDetail = (View) mInflatedView.findViewById(R.id.btnGrpahDetail);
		graphHeight -= btnGrpahDetail.getHeight();
		G.log("after graphHeight: "+graphHeight);
		
		graphParentView.removeAllViews();
		if(noOfstatisticsRows > 0)
		{
			noDatePresentTextView.setVisibility(View.GONE);
			ElaGraph elaGraph = new ElaGraph(getActivity(),graphHeight,graphWidth,mStatisticsDataMap,mPeriodType,mMaxAmount,mPeriodKeys);
			elaGraph.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			graphParentView.addView(elaGraph);
			G.log("elaGraph added");
			
			///////////////////////////////////////////////////////////////
			//Showing the graph options
			///////////////////////////////////////////////////////////////			
			ElaCheckBoxBar graphOptionsBar = (ElaCheckBoxBar) mInflatedView.findViewById(R.id.graphOptionsBar);
			graphOptionsBar.setMaxRows(2);
			graphOptionsBar.setCheckBoxItems(mGraphOptionsItemsMap);
			//G.log("graphOptionsBar.getChildCount();: "+((LinearLayout)graphOptionsBar.getChildAt(0)).getChildCount());
			
			LinearLayout graphOptionsMainLyt = (LinearLayout) graphOptionsBar.getChildAt(0);
			for(int i=0;i<graphOptionsMainLyt.getChildCount();i++)
			{
				LinearLayout linearLayout = (LinearLayout) graphOptionsMainLyt.getChildAt(i);
				for(int j=0;j<linearLayout.getChildCount();j++)
				{
					ElaCheckBoxBar.ElaCheckBoxItem elaCheckBoxItem = (ElaCheckBoxBar.ElaCheckBoxItem) linearLayout.getChildAt(j);
					elaCheckBoxItem.setOnCheckedChangeListener(new OnCheckedChangeListener() 
					{
						@Override
						public void onCheckedChanged(CompoundButton view, boolean checked) 
						{
							onGraphOptionsSelected(view,checked);							
						}
					});
				}
			}
		}
		else 
		{			
			noDatePresentTextView.setVisibility(View.VISIBLE);
			G.log("Empty view added");
		}
	}
	
	public void onGraphOptionsSelected(View view,boolean checked)
	{
		ElaCheckBoxBar graphOptionsBar = (ElaCheckBoxBar) mInflatedView.findViewById(R.id.graphOptionsBar);
		LinearLayout graphOptionsMainLyt = (LinearLayout) graphOptionsBar.getChildAt(0);
			
		ElaCheckBoxBar.ElaCheckBoxItem currentElaCheckBoxItem = (ElaCheckBoxBar.ElaCheckBoxItem)view;
		int catOrSubCatId = currentElaCheckBoxItem.getValue();
		if(!checked)
		{
			G.log("removing from mStatisticsDataMapCopy catOrSubCatId: "+catOrSubCatId);
			if(catOrSubCatId == G.CATEGORY_ANY)
			{
				mStatisticsDataMapCopy.clear();
				for(int i=0;i<graphOptionsMainLyt.getChildCount();i++)
				{
					LinearLayout linearLayout = (LinearLayout) graphOptionsMainLyt.getChildAt(i);
					for(int j=0;j<linearLayout.getChildCount();j++)
					{
						ElaCheckBoxBar.ElaCheckBoxItem elaCheckBoxItem = (ElaCheckBoxBar.ElaCheckBoxItem) linearLayout.getChildAt(j);
						elaCheckBoxItem.setChecked(false);
					}
				}
			}
			else
				mStatisticsDataMapCopy.remove(catOrSubCatId);
		}
		else 
		{
			G.log("adding to mStatisticsDataMapCopy catOrSubCatId: "+catOrSubCatId);
			if(catOrSubCatId == G.CATEGORY_ANY)
			{
				mStatisticsDataMapCopy = new LinkedHashMap<Integer,ElaGraphDataBean>(mStatisticsDataMap);
				for(int i=0;i<graphOptionsMainLyt.getChildCount();i++)
				{
					LinearLayout linearLayout = (LinearLayout) graphOptionsMainLyt.getChildAt(i);
					for(int j=0;j<linearLayout.getChildCount();j++)
					{
						ElaCheckBoxBar.ElaCheckBoxItem elaCheckBoxItem = (ElaCheckBoxBar.ElaCheckBoxItem) linearLayout.getChildAt(j);
						elaCheckBoxItem.setChecked(true);
					}
				}
			}
			else
			{
				ElaGraphDataBean elaGraphDataBean = mStatisticsDataMap.get(catOrSubCatId);
				mStatisticsDataMapCopy.put(catOrSubCatId,elaGraphDataBean);
			}			
		}
		
		LinearLayout graphParentView = (LinearLayout) mInflatedView.findViewById(R.id.lytStatisticsContent);
		graphParentView.removeAllViews();
		int graphHeight = graphParentView.getHeight();
		int graphWidth = graphParentView.getWidth();
		G.log("graphHeight: "+graphHeight+" graphWidth: "+graphWidth);
		View btnGrpahDetail = (View) mInflatedView.findViewById(R.id.btnGrpahDetail);
		graphHeight -= btnGrpahDetail.getHeight();
		G.log("after graphHeight: "+graphHeight);
		
		ElaGraph elaGraph = new ElaGraph(getActivity(),graphHeight,graphWidth,mStatisticsDataMapCopy,mPeriodType,mMaxAmount,mPeriodKeys);
		elaGraph.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		graphParentView.addView(elaGraph);
		
		
	}
	
	public void onSelectStatisticsOption(View view,ViewGroup parentView)
	{
		int childCount = parentView.getChildCount();
		int currentViewId = view.getId();
		for(int i=0;i<childCount;i++)
		{
			TextView textView = (TextView) parentView.getChildAt(i);
			if(currentViewId == textView.getId())
			{
				textView.setSelected(true);
			}
			else
			{
				textView.setSelected(false);
			}
		}
		loadStatisticsData(NODIRECTION);
	}
	
	public int getSelectedStatisticsOption(ViewGroup parentView)
	{
		int selectedValue = -1;
		int childCount = parentView.getChildCount();
		for(int i=0;i<childCount;i++)
		{
			TextView textView = (TextView) parentView.getChildAt(i);
			if(textView.isSelected() == true && textView.getTag() != null)
			{
				selectedValue = (Integer) textView.getTag();
			}
		}
		return selectedValue;
	}
	
	/*********************************************************************************
	 * Implementation of Cursor Loader Methods*
	 * 
	 * Code to load the list of categories
	 *********************************************************************************/
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
	{
		G.log("onCreateLoader called");
		CursorLoader cursorLoader = null;
		switch (id) 
		{
			case VIEW_STATISTICS_LOADER_ID:
				G.log("VIEW_STATISTICS_CAT_LOADER_ID case executing");
				Uri statisticsCatUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI, mStatisticsDbViewName);
				cursorLoader = new CursorLoader(getActivity(), statisticsCatUri, null, null, null, null);
				break;
			default:
				break;
		}
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) 
	{
		switch (cursorLoader.getId()) 
		{
			case VIEW_STATISTICS_LOADER_ID:
				G.log("Loader finished for loaderID: "+cursorLoader.getId()+". calling createStatisticsGraph");
				cursor.setNotificationUri(getActivity().getContentResolver(),ElaContentProvider.CONTENT_URI);
				createStatisticsGraph(cursor);
				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) 
	{
		switch (cursorLoader.getId()) 
		{
			case VIEW_STATISTICS_LOADER_ID:
				break;
		}
	}
	
    /*********************************************************************************
   	 * Code to set Date from Date Picker Dialog
   	 *********************************************************************************/    
    public static class DatePickerFragment extends DialogFragment
    					implements DatePickerDialog.OnDateSetListener 
    {
    	private Calendar calendarObj;
    	private Button btnDatePicker; 
    	public DatePickerFragment(){}
    	public DatePickerFragment(Calendar calendar,Button datePickerBtn)
    	{
    		calendarObj = calendar;
    		btnDatePicker = datePickerBtn;
    	}
    	@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) 
		{
			int year = calendarObj.get(Calendar.YEAR);
			int month = calendarObj.get(Calendar.MONTH);
			int day = calendarObj.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
			
		public void onDateSet(DatePicker view, int year, int month, int day) 
		{
			//Checking if future date is selected
			Calendar tmpCalendar = Calendar.getInstance();
			tmpCalendar.set(year, month, day);
			if(tmpCalendar.after(Calendar.getInstance()))
			{
				Toast toast = Toast.makeText(getActivity(),R.string.err_date_future_not_allowed,Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER,0,0);
				toast.show();
				return;
			}
			calendarObj.set(year, month, day);			
		}
	}	
  
}
