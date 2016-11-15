package elango.thaaru.MyFinance;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import elango.thaaru.MyFinance.CreateTransactionFragment.DatePickerFragment;
import elango.thaaru.MyFinance.CreateTransactionFragment.TimePickerFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditTransaction extends ElaCommonActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
	private static final String CLASS = "EditTransaction ";
	
	private EditText mEtAmount;
	private Button mBtnDatePicker;
	private Button mBtnTimePicker;
	private Button mBtnConfirm;
	private Button mBtnReset;
	private Spinner mSpSubCategories;
	private EditText mEtComment;
	private String mAccountIdToEdit;
	
	private Calendar mCalendar = Calendar.getInstance();
	private static final int SUB_CATEGORY_LOADER_ID = 1;
	private static final int ACCOUNT_LOADER_ID = 2;

	private SimpleCursorAdapter mSubCategoriesSimpleCursorAdapter;
	
	/*********************************************************************************
   	 * Initial function that is called on creation of activity
   	 *********************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        super.enableTitleFeatures();
        setContentView(R.layout.edit_transaction);
        super.setTitleStyle(getString(R.string.edit_transaction));
        
        Bundle extrasBundle = getIntent().getExtras();
        if(extrasBundle == null || extrasBundle.getLong(G.KEY_ACCOUNTS_ID) == 0L)
        	return;
        mAccountIdToEdit = String.valueOf(extrasBundle.getLong(G.KEY_ACCOUNTS_ID));
        G.log("mAccountIdToEdit: "+mAccountIdToEdit);
        
        setGlobalVariables();
        loadSubCategories();
        loadAccount();
        registerEventHandlers();        
    }
   
    /*********************************************************************************
   	 * Code to set class level variables which are used across many functions 
   	 *********************************************************************************/
    public void setGlobalVariables()
    {
    	mEtAmount = (EditText) findViewById(R.id.etAmount);
        mBtnDatePicker = (Button) findViewById(R.id.btnDatePicker);
        mBtnTimePicker = (Button) findViewById(R.id.btnTimePicker);
        mSpSubCategories = (Spinner) findViewById(R.id.spSubCategories);
        mEtComment = (EditText) findViewById(R.id.etComment); 
        mBtnConfirm = (Button) findViewById(R.id.btnConfirm);
        mBtnReset = (Button) findViewById(R.id.btnCancel);
    }
    
    /*********************************************************************************
	 * Code to load the sub categories
	 *********************************************************************************/
	public void loadSubCategories() 
	{
		getSupportLoaderManager().initLoader(SUB_CATEGORY_LOADER_ID, null, this);
		
		String[] columns = new String[] { G.COLUMN_DESCRIPTION};
		int[] views = new int[] { android.R.id.text1};
		mSubCategoriesSimpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,columns,views, 0);
		Spinner spinner = (Spinner) findViewById(R.id.spSubCategories);
		mSubCategoriesSimpleCursorAdapter.setDropDownViewResource(R.layout.spinner_text_view);
		spinner.setAdapter(mSubCategoriesSimpleCursorAdapter);

		mSubCategoriesSimpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder()
		{
				public boolean setViewValue(View view, Cursor cursor,int columnIndex) 
				{
					String subCategory = cursor.getString(columnIndex);
					//int resourceId = getResources().getIdentifier(category,"string", this.getPackageName());
					((TextView) view).setText(subCategory);
					return true;
				}
		});
	}
	
    /*********************************************************************************
   	 * Code to load the account for the selected account ID
   	 * using cursor loader
   	 *********************************************************************************/
    public void loadAccount()
    {
    	G.log("Loading account");
    	getSupportLoaderManager().initLoader(ACCOUNT_LOADER_ID, null, this);
    }
    
    public void setAccountValuesInScreen(Cursor cursor)
    {
    	G.log("setAccountValuesInScreen");
    	cursor.moveToFirst();
    	if(!cursor.isAfterLast())
    	{
    		String amount = cursor.getString(cursor.getColumnIndex(G.COLUMN_AMOUNT));
    		String dateTimeStrFromDb = cursor.getString(cursor.getColumnIndex(G.COLUMN_ACTION_DATE));
    		int subCategoryId = cursor.getInt(cursor.getColumnIndex(G.COLUMN_SUB_CATEGORY_ID));
    		G.log("subCategoryId: "+subCategoryId);
    		String comment = cursor.getString(cursor.getColumnIndex(G.COLUMN_COMMENT));
    		mEtAmount.setText(amount);
    		ElaCommonClass.setSpinnerSelectionById(mSpSubCategories,0);
    		Date dateTimeFromDb = new Date();
    		try 
    		{
				dateTimeFromDb = G.dateTimeFormatterFromDB.parse(dateTimeStrFromDb);
			} 
    		catch (ParseException e) 
    		{
				e.printStackTrace();
			}
    		String dateToDisplay = G.dateFormatterToDisplay.format(dateTimeFromDb);
    		String timeToDisplay = G.timeFormatterToDisplay.format(dateTimeFromDb);
    		mBtnDatePicker.setText(dateToDisplay);
    		mBtnTimePicker.setText(timeToDisplay);
    		if(comment != null)
    			mEtComment.setText(comment);
    	}
    	
    }
    
    /*********************************************************************************
   	 * Code to set all event listeners to handle the events
   	 *********************************************************************************/
    public void registerEventHandlers()
    {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View v) 
			{
				updateAccount();
			}
		});
        
        mBtnReset.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View v) 
			{
				loadAccount();
			}
		});
        
        mSpSubCategories.setOnTouchListener(new HideSoftKeyboard());
    }
    
    /*********************************************************************************
   	 * Code to insert accounts in SQLite through content provider
   	 *********************************************************************************/    
    public void updateAccount()
    {
		Context context = getApplicationContext();
		int toastDuration = Toast.LENGTH_SHORT;
		String amount = mEtAmount.getText().toString();
		if(amount.length() == 0) 
		{
			Toast toast = Toast.makeText(context,R.string.err_enter_amount,toastDuration);
			toast.show();
			return;
		}
		String actionDate = mBtnDatePicker.getText().toString();
		String actionTime = mBtnTimePicker.getText().toString();
		String actionDateTime = actionDate +" "+ actionTime;
		G.log("actionDateTime: "+actionDateTime);
		Date actionDateObj =  new Date();
		try
		{
			actionDateObj = G.dateTimeFormatterFromDisplay.parse(actionDateTime);
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		actionDate = G.dateTimeFormatterToDB.format(actionDateObj);
		G.log("final actionDate: "+actionDate);
		int subCategoryId = (int) mSpSubCategories.getSelectedItemId();
		String comment = mEtComment.getText().toString();
		
		Uri accountsUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI,G.TABLE_ACCOUNTS);
		ContentValues accountsContentValues = new ContentValues();
		accountsContentValues.put(G.COLUMN_SUB_CATEGORY_ID, subCategoryId);
		accountsContentValues.put(G.COLUMN_AMOUNT, amount);
		accountsContentValues.put(G.COLUMN_ACTION_DATE, actionDate);
		accountsContentValues.put(G.COLUMN_COMMENT, comment);
		getContentResolver().update(accountsUri,accountsContentValues,
				G.COLUMN_ID+"=?",new String[]{mAccountIdToEdit});
		getContentResolver().notifyChange(accountsUri,null);
		Toast toast = Toast.makeText(context,R.string.success,toastDuration);
		toast.show();
    }    
    
    /*********************************************************************************
   	 * Code to show date picker dialog
   	 *********************************************************************************/
    public void showDatePickerDialog(View view) 
    {
    	DialogFragment newFragment = new DatePickerFragment(mCalendar,(Button)view);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    
    /*********************************************************************************
   	 * Code to show time picker dialog
   	 *********************************************************************************/
    public void showTimePickerDialog(View view) 
    {
        DialogFragment newFragment = new TimePickerFragment(mCalendar,(Button)view);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    
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
			String currentDateString =  G.dateFormatterToDisplay.format(calendarObj.getTime());
			btnDatePicker.setText(currentDateString);
		}
	}
	
	/*********************************************************************************
	* Code to set Time from Time Picker Dialog
	*********************************************************************************/    
	public static class TimePickerFragment extends DialogFragment
	            implements TimePickerDialog.OnTimeSetListener 
	{
		private Calendar calendarObj;
		private Button btnTimePicker;
		public TimePickerFragment(){}
		public TimePickerFragment(Calendar calendar,Button timePickerBtn)
		{
			calendarObj = calendar;
			btnTimePicker = timePickerBtn;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) 
		{
			int hour = calendarObj.get(Calendar.HOUR_OF_DAY);
			int minute = calendarObj.get(Calendar.MINUTE);
			return new TimePickerDialog(getActivity(), this, hour, minute,
			DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			//Checking if future date is selected
			Calendar tmpCalendar = Calendar.getInstance();
			tmpCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
			tmpCalendar.set(Calendar.MINUTE,minute);
			if(tmpCalendar.after(Calendar.getInstance()))
			{
				Toast toast = Toast.makeText(getActivity(),R.string.err_date_future_not_allowed,Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER,0,0);
				toast.show();
				return;
			}
			calendarObj.set(Calendar.HOUR,hourOfDay);
			calendarObj.set(Calendar.MINUTE,minute);
			calendarObj.set(Calendar.AM_PM, hourOfDay < 12 ? Calendar.AM : Calendar.PM);
			String currentTimeString = G.timeFormatterToDisplay.format(calendarObj.getTime());        	
			btnTimePicker.setText(currentTimeString);
		}
	}
    
    /*********************************************************************************
 	 * Implementation of Cursor Loader Methods*
 	 * 
 	 * Code to load the accounts using cursor loader
 	 *********************************************************************************/
 	@Override
 	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
 	{
 		G.log("Entering onCreateLoader: "+id);
 		CursorLoader cursorLoader = null;
 		switch (id) 
 		{
 			case SUB_CATEGORY_LOADER_ID:
 				Uri subCategoriesUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI,G.TABLE_SUB_CATEGORIES);
				cursorLoader = new CursorLoader(this,subCategoriesUri,null,null,null,null);
				break;
 			case ACCOUNT_LOADER_ID: 				
 				Uri accountsUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI,G.VIEW_ACCOUNTS);
 				cursorLoader = new CursorLoader(this, accountsUri, null, G.COLUMN_ID+"=?",new String[]{mAccountIdToEdit}, null);
 				break;
 			default:
 				break;
 		}		
 		return cursorLoader;
 	}

 	@Override
 	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
 	{
 		G.log("accounts loader finished");
 		switch (cursorLoader.getId()) 
 		{
 			case SUB_CATEGORY_LOADER_ID:
 				mSubCategoriesSimpleCursorAdapter.swapCursor(cursor); 				
 				break;
 			case ACCOUNT_LOADER_ID:
 				setAccountValuesInScreen(cursor);
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
	 		case SUB_CATEGORY_LOADER_ID:
	 			mSubCategoriesSimpleCursorAdapter.swapCursor(null);
	 			break;
	 		case ACCOUNT_LOADER_ID:
	 			break;
 			default:
 				break;
 		}		
 	}
}