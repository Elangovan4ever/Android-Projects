package elango.thaaru.MyFinance;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

public class SearchFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String CLASS = "SearchFragment ";

	private Bundle mBundleData;

	private SimpleCursorAdapter mSubCategoriesSimpleCursorAdapter;
	private SimpleCursorAdapter mPeriodTypeSimpleCursorAdapter;

	private static Calendar mCalendar;
	private Calendar mFromCalendar = Calendar.getInstance();
	private Calendar mToCalendar = Calendar.getInstance();
	
	private static Button mBtnDatePicker;
	private static Button mBtnTimePicker;
	
	private View mInflatedView;
	private int mCategoryId = G.NOTHING_INT;
	private static final int SUB_CATEGORIES_LOADER_ID = 1;
	private static final int PERIOD_TYPES_LOADER_ID = 2;

	/*********************************************************************************
	 * First function called when the fragment created before draw themselves
	 *********************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
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
		Log.d(G.L, CLASS + "mBundleData: " + mBundleData);
		mInflatedView = layoutInflater.inflate(R.layout.search_fragment, container,false);
		return mInflatedView;
	}

	/*********************************************************************************
	 * Function called after the fragment drawn in user view
	 *********************************************************************************/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		setGlobalVariables();
		loadSubCategories();
		loadPeriodTypes();
		setTodayDate();
		registerEventHandlers();		
	}

	/*********************************************************************************
	 * Code to set class level variables which are used across many functions
	 *********************************************************************************/
	public void setGlobalVariables() 
	{
		//Getting the current selected categoryId
    	ElaTabBar elaTabBar = (ElaTabBar) getActivity().findViewById(R.id.catogoryTabBar);
		mCategoryId = ElaCommonClass.getInstance(getActivity()).getSelectedTabId(elaTabBar);
	}

	/*********************************************************************************
	 * Code to set the current date and time in the screen
	 *********************************************************************************/
	private void setTodayDate() 
	{
		Calendar calendarObj = Calendar.getInstance();
		String dateString = G.dateFormatterToDisplay.format(calendarObj.getTime());
		String timeString = G.timeFormatterToDisplay.format(calendarObj.getTime());
		mToCalendar.setTime(calendarObj.getTime());
		Button btnToDatePicker = (Button) mInflatedView.findViewById(R.id.btnToDatePicker);
		Button btnToTimePicker = (Button) mInflatedView.findViewById(R.id.btnToTimePicker);
		btnToDatePicker.setText(dateString);
		btnToTimePicker.setText(timeString);

		calendarObj.set(Calendar.HOUR_OF_DAY, 0);
		calendarObj.set(Calendar.MINUTE, 0);
		timeString = G.timeFormatterToDisplay.format(calendarObj.getTime());
		mFromCalendar.setTime(calendarObj.getTime());
		Button btnFromDatePicker = (Button) mInflatedView.findViewById(R.id.btnFromDatePicker);
		Button btnFromTimePicker = (Button) mInflatedView.findViewById(R.id.btnFromTimePicker);
		btnFromDatePicker.setText(dateString);
		btnFromTimePicker.setText(timeString);
	}
	
	/*********************************************************************************
	 * Code to load the sub categories from SQLite through content provider and
	 * using cursor loader
	 *********************************************************************************/
	public void loadSubCategories()
	{
		if(mCategoryId == G.CATEGORY_ANY)
		{
			TableRow subCategoryRow = (TableRow)mInflatedView.findViewById(R.id.tableRow1);
			subCategoryRow.setVisibility(View.GONE);
			return;
		}
		
		String[] columns = new String[] { G.COLUMN_DESCRIPTION};
		int[] views = new int[] { android.R.id.text1};
		mSubCategoriesSimpleCursorAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item, null,columns,views, 0);
		Spinner spinner = (Spinner) mInflatedView.findViewById(R.id.spSubCategories);
		mSubCategoriesSimpleCursorAdapter.setDropDownViewResource(R.layout.spinner_text_view);
		spinner.setAdapter(mSubCategoriesSimpleCursorAdapter);
		getLoaderManager().initLoader(SUB_CATEGORIES_LOADER_ID, null, this);

		mSubCategoriesSimpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder()
		{
				public boolean setViewValue(View view, Cursor cursor,int columnIndex) 
				{
					String subCategory = cursor.getString(columnIndex);
					//int resourceId = getResources().getIdentifier(category,"string", getActivity().getPackageName());
					((TextView) view).setText(subCategory);
					return true;
				}
		});
	}

	/*********************************************************************************
	 * Code to load the period types from SQLite through content provider and
	 * using cursor loader
	 *********************************************************************************/
	public void loadPeriodTypes() 
	{
		String[] columns = new String[] { G.COLUMN_DESCRIPTION};
		int[] views = new int[] { android.R.id.text1};
		mPeriodTypeSimpleCursorAdapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_spinner_item, null,columns,views,0);
		Spinner spinner = (Spinner) mInflatedView.findViewById(R.id.spCycleOptions);
		mPeriodTypeSimpleCursorAdapter.setDropDownViewResource(R.layout.spinner_text_view);
		spinner.setAdapter(mPeriodTypeSimpleCursorAdapter);
		getLoaderManager().initLoader(PERIOD_TYPES_LOADER_ID, null, this);

		mPeriodTypeSimpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() 
		{
			public boolean setViewValue(View view, Cursor cursor,int columnIndex) 
			{
				String periodType = cursor.getString(columnIndex);
				int resourceId = getResources().getIdentifier(periodType,"string", getActivity().getPackageName());
				((TextView) view).setText(getString(resourceId));
				return true;
			}
		});
	}

	/*********************************************************************************
	 * Code to set all event listeners to handle the events
	 *********************************************************************************/
	public void registerEventHandlers() 
	{
		Button btnSearch = (Button) mInflatedView.findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				searchAccount();
			}
		});

		Button btnReset = (Button) mInflatedView.findViewById(R.id.btnCancel);
		btnReset.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				resetScreen();
			}
		});
		
		RadioGroup rgPeriodOptions = (RadioGroup) mInflatedView.findViewById(R.id.rgPeriodOptions);
		rgPeriodOptions.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(RadioGroup group,int checkedViewId) 
			{
				HideSoftKeyboard hideSoftKeyboard = new HideSoftKeyboard();
				hideSoftKeyboard.onClick(group);
				onRadioButtonClicked(checkedViewId);
			}
		});
		
		Button btnFromDatePicker = (Button) mInflatedView.findViewById(R.id.btnFromDatePicker);
		btnFromDatePicker.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View view) 
			{
				mBtnDatePicker = (Button) view;
				mCalendar = mFromCalendar;
				showDateDialog();
			}
		});
		
		Button btnFromTimePicker = (Button) mInflatedView.findViewById(R.id.btnFromTimePicker);
		btnFromTimePicker.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View view)
			{
				mBtnTimePicker = (Button) view;
				mCalendar = mFromCalendar;
				showTimeDialog();
			}
		});

		
		Button btnToDatePicker = (Button) mInflatedView.findViewById(R.id.btnToDatePicker);
		btnToDatePicker.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View view) 
			{
				mBtnDatePicker = (Button) view;
				mCalendar = mToCalendar;
				showDateDialog();
			}
		});

		Button btnToTimePicker = (Button) mInflatedView.findViewById(R.id.btnToTimePicker);
		btnToTimePicker.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View view) 
			{
				mBtnTimePicker = (Button) view;
				mCalendar = mToCalendar;
				showTimeDialog();
			}
		});
		
		Spinner spSubCategories = (Spinner) mInflatedView.findViewById(R.id.spSubCategories);
		spSubCategories.setOnTouchListener(new HideSoftKeyboard());
		
		Spinner spCycleOptions = (Spinner) mInflatedView.findViewById(R.id.spCycleOptions);		
		spCycleOptions.setOnTouchListener(new HideSoftKeyboard());
	}

	public void showDateDialog() 
	{
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	public void showTimeDialog() 
	{
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	/*********************************************************************************
	 * Code to insert accounts in SQLite through content provider
	 *********************************************************************************/
	public void searchAccount() 
	{
		Log.d(G.L, CLASS + "======================== searchAccount ====================");
		//Intent intent = new Intent(getActivity(), Statement.class);
		Spinner spSubCategories = (Spinner) mInflatedView.findViewById(R.id.spSubCategories);
		int subCategoryId = G.NOTHING_INT;
		if(mCategoryId != G.CATEGORY_ANY)
			subCategoryId = (int) spSubCategories.getSelectedItemId();
		Log.d(G.L, CLASS + "subCategory: " + subCategoryId);
		Bundle args = new Bundle();
		args.putInt(G.KEY_SUB_CATEGORY_ID, subCategoryId);
		
		RadioButton rbPeriodCycle = (RadioButton) mInflatedView.findViewById(R.id.radioPeriodCycle);
		int periodCycle = (rbPeriodCycle.isChecked()) ? G.PERIOD_SELECTION_BY_CYCLE : G.PERIOD_SELECTION_BY_RANGE;
		String fromToDate = G.NOTHING_STR;
		if (periodCycle == G.PERIOD_SELECTION_BY_CYCLE)
		{
			Spinner spCycleOptions = (Spinner) mInflatedView.findViewById(R.id.spCycleOptions);
			int periodType = (int) spCycleOptions.getSelectedItemId();
			if(periodType != G.PERIOD_ANY)
				fromToDate = getFromToDate(periodType);			
		}
		else
		{
			String startDate = G.dateTimeFormatterToDB.format(mFromCalendar.getTime());
			String endDate = G.dateTimeFormatterToDB.format(mToCalendar.getTime());
			fromToDate = startDate + "~" + endDate;			
		}
		Log.d(G.L, CLASS + "fromToDate: " + fromToDate);
		args.putString(G.KEY_PERIOD, fromToDate);
		
		EditText etComment = (EditText) mInflatedView.findViewById(R.id.etComment);
		String comment = etComment.getText().toString();
		Log.d(G.L, CLASS + "comment: " + comment);
		if (comment.length() == 0)
			comment = G.NOTHING_STR;
		args.putString(G.KEY_COMMENT, comment);
		
		LinearLayout lytSearchContent = (LinearLayout) mInflatedView.findViewById(R.id.lytSearchContent);
		lytSearchContent.removeAllViews();
		final StatementFragment statementFragment = new StatementFragment();
		statementFragment.setArguments(args);
    	FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.lytSearchContent,statementFragment);
        fragmentTransaction.commit();
        Log.d(G.L, CLASS + "statementFragment added");
        
        TextView tvSearchTransactions = (TextView) getParentFragment().getView().findViewById(R.id.tvSearchTransactions);
        tvSearchTransactions.setText(getString(R.string.search_again));
	}
	
	public String getFromToDate(int period)
	{
		Log.d(G.L, CLASS + "period: "+period);
		String fromDBDate = null;
		String toDBDate = null;
		Calendar calendarObj = Calendar.getInstance();
		switch(period)
		{
			case G.PERIOD_TODAY: 
				fromDBDate = G.dateTimeFormatterToDB_SOD.format(calendarObj.getTime());
				toDBDate = G.dateTimeFormatterToDB.format(calendarObj.getTime());
				break;
			case G.PERIOD_YESTERDAY:
				calendarObj.add(Calendar.DATE, -1);
				fromDBDate = G.dateTimeFormatterToDB_SOD.format(calendarObj.getTime());
				calendarObj.set(Calendar.HOUR_OF_DAY, 23);
				calendarObj.set(Calendar.MINUTE, 59);
				toDBDate = G.dateTimeFormatterToDB.format(calendarObj.getTime());
				break;
			case G.PERIOD_THIS_WEEK:
				toDBDate = G.dateTimeFormatterToDB.format(calendarObj.getTime());
				if(calendarObj.get(Calendar.DAY_OF_WEEK) == 1)
				{
					calendarObj.add(Calendar.WEEK_OF_YEAR, -1);
				}
				calendarObj.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				fromDBDate = G.dateTimeFormatterToDB_SOD.format(calendarObj.getTime());
				break;
			case G.PERIOD_LAST_WEEK:
				if(calendarObj.get(Calendar.DAY_OF_WEEK) == 1)
				{
					calendarObj.add(Calendar.WEEK_OF_YEAR, -1);
				}
				calendarObj.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				calendarObj.set(Calendar.HOUR_OF_DAY, 23);
				calendarObj.set(Calendar.MINUTE, 59);
				toDBDate = G.dateTimeFormatterToDB.format(calendarObj.getTime());
				calendarObj.add(Calendar.WEEK_OF_YEAR, -1);
				calendarObj.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				fromDBDate = G.dateTimeFormatterToDB_SOD.format(calendarObj.getTime());
				break;
			case G.PERIOD_THIS_MONTH: 
				toDBDate = G.dateTimeFormatterToDB.format(calendarObj.getTime());
				calendarObj.set(Calendar.DAY_OF_MONTH, 1);
				fromDBDate = G.dateTimeFormatterToDB_SOD.format(calendarObj.getTime());
				break;
			case G.PERIOD_LAST_MONTH:
				calendarObj.add(Calendar.MONTH, -1);
				calendarObj.set(Calendar.DAY_OF_MONTH, 1);
				fromDBDate = G.dateTimeFormatterToDB_SOD.format(calendarObj.getTime());
				calendarObj.set(Calendar.DAY_OF_MONTH, calendarObj.getActualMaximum(Calendar.DAY_OF_MONTH));
				calendarObj.set(Calendar.HOUR_OF_DAY, 23);
				calendarObj.set(Calendar.MINUTE, 59);
				toDBDate = G.dateTimeFormatterToDB.format(calendarObj.getTime());
				break;
			case G.PERIOD_THIS_YEAR:
				toDBDate = G.dateTimeFormatterToDB.format(calendarObj.getTime());
				calendarObj.set(Calendar.DAY_OF_YEAR, 1);
				fromDBDate = G.dateTimeFormatterToDB_SOD.format(calendarObj.getTime());
				break;
			case G.PERIOD_LAST_YEAR: 
				calendarObj.add(Calendar.YEAR, -1);
				calendarObj.set(Calendar.DAY_OF_YEAR, 1);
				fromDBDate = G.dateTimeFormatterToDB_SOD.format(calendarObj.getTime());
				calendarObj.set(Calendar.DAY_OF_YEAR,calendarObj.getActualMaximum(Calendar.DAY_OF_YEAR));
				calendarObj.set(Calendar.HOUR_OF_DAY, 23);
				calendarObj.set(Calendar.MINUTE, 59);
				toDBDate = G.dateTimeFormatterToDB.format(calendarObj.getTime());
				break;
			default:
				fromDBDate = G.dateTimeFormatterToDB_SOD.format(calendarObj.getTime());
				toDBDate = G.dateTimeFormatterToDB.format(calendarObj.getTime());
		}
		return fromDBDate + "~" + toDBDate;
	}

	/*********************************************************************************
	 * Code to reset the screen
	 *********************************************************************************/
	private void resetScreen() 
	{
		Spinner spCategories = (Spinner) mInflatedView.findViewById(R.id.spSubCategories);
		Spinner spCycleOptions = (Spinner) mInflatedView.findViewById(R.id.spCycleOptions);		
		spCategories.setSelection(-1);
		spCycleOptions.setSelection(-1);
		
		EditText etComment = (EditText) mInflatedView.findViewById(R.id.etComment);
		etComment.setText("");
		
		setTodayDate();
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
			case SUB_CATEGORIES_LOADER_ID:				
				String projection = null;
				String[] argList = null;
				G.log("mCategoryId : "+mCategoryId);
				if(mCategoryId != G.CATEGORY_ANY)
				{
					projection =  G.COLUMN_CATEGORY_ID+"=?";
					argList = new String[]{Integer.toString(mCategoryId)};
				}
				G.log("subcategories projection : "+projection);
				Uri subCategorysUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI, G.TABLE_SUB_CATEGORIES);
				cursorLoader = new CursorLoader(getActivity(), subCategorysUri,null,projection,argList, null);
				break;
			case PERIOD_TYPES_LOADER_ID:
				Uri periodTypesUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI, G.TABLE_PERIOD_TYPE);
				cursorLoader = new CursorLoader(getActivity(), periodTypesUri,null, null, null, null);
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
			case SUB_CATEGORIES_LOADER_ID:
				G.log("subcategories count : "+cursor.getCount());
				mSubCategoriesSimpleCursorAdapter.swapCursor(cursor);
				Spinner subCategorySpinner = (Spinner) mInflatedView.findViewById(R.id.spSubCategories);
				ElaCommonClass.setSpinnerSelectionById(subCategorySpinner, -1);
				break;
			case PERIOD_TYPES_LOADER_ID:
				mPeriodTypeSimpleCursorAdapter.swapCursor(cursor);
				Spinner periodOptionsSpinner = (Spinner) mInflatedView.findViewById(R.id.spCycleOptions);
				ElaCommonClass.setSpinnerSelectionById(periodOptionsSpinner, -1);
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
			case PERIOD_TYPES_LOADER_ID:
				mPeriodTypeSimpleCursorAdapter.swapCursor(null);
				break;
		}
	}

	/*********************************************************************************
	 * Code to handle the radio button events. switching between period types
	 *********************************************************************************/
	public void onRadioButtonClicked(int checkedViewId) 
	{
		TableRow tableRow3 = (TableRow) mInflatedView.findViewById(R.id.tableRow3);
		TableRow tableRow3_1 = (TableRow) mInflatedView.findViewById(R.id.tableRow3_1);
		switch (checkedViewId) 
		{
			case R.id.radioPeriodCycle:
				tableRow3.setVisibility(View.VISIBLE);
				tableRow3_1.setVisibility(View.GONE);
				break;
			case R.id.radioDateRange:
				tableRow3.setVisibility(View.GONE);
				tableRow3_1.setVisibility(View.VISIBLE);
				break;
		}
	}

	/*********************************************************************************
	 * Code to set Date from Date Picker Dialog
	 *********************************************************************************/
	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener 
	{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) 
		{	
			int year = mCalendar.get(Calendar.YEAR);
			int month = mCalendar.get(Calendar.MONTH);
			int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) 
		{
			mCalendar.set(year, month, day);
			String currentDateString = G.dateFormatterToDisplay.format(mCalendar.getTime());
			mBtnDatePicker.setText(currentDateString);
		}
	}

	/*********************************************************************************
	 * Code to set Time from Time Picker Dialog
	 *********************************************************************************/
	public static class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener 
	{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) 
		{
			int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = mCalendar.get(Calendar.MINUTE);
			return new TimePickerDialog(getActivity(), this, hour, minute,DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
		{
			mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			mCalendar.set(Calendar.MINUTE, minute);
			mCalendar.set(Calendar.AM_PM, hourOfDay < 12 ? Calendar.AM: Calendar.PM);
			String currentTimeString = G.timeFormatterToDisplay.format(mCalendar.getTime());
			mBtnTimePicker.setText(currentTimeString);
		}
	}

}