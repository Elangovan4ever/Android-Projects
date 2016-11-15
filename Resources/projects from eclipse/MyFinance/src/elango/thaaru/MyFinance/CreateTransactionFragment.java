package elango.thaaru.MyFinance;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateTransactionFragment extends Fragment
{
	private View mInflatedView;
	
	private EditText mEtAmount;
	private Button mBtnDatePicker;
	private Button mBtnTimePicker;
	private Button mBtnConfirm;
	private Button mBtnReset;
	private Spinner mSpSubCategories;
	private EditText mEtComment;
	
	private Calendar mCalendar = Calendar.getInstance();

	private boolean mIsLoadedAlready = false;
	
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
			ViewGroup container, Bundle savedInstanceState) {
		
		G.log("In onCreateView called");
		mInflatedView = layoutInflater.inflate(R.layout.create_transaction_fragment, container,
				false);
		setGlobalVariables();
        setTodayDate();
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
    }      
   
    /*********************************************************************************
   	 * Code to set class level variables which are used across many functions 
   	 *********************************************************************************/
    public void setGlobalVariables()
    {
    	mEtAmount = (EditText) mInflatedView.findViewById(R.id.etAmount);
        mBtnDatePicker = (Button) mInflatedView.findViewById(R.id.btnDatePicker);
        mBtnTimePicker = (Button) mInflatedView.findViewById(R.id.btnTimePicker);
        mSpSubCategories = (Spinner) mInflatedView.findViewById(R.id.spSubCategories);
        mEtComment = (EditText) mInflatedView.findViewById(R.id.etComment);
        mBtnConfirm = (Button) mInflatedView.findViewById(R.id.btnConfirm);
        mBtnReset = (Button) mInflatedView.findViewById(R.id.btnCancel);
    }
    
    public void showSubCategories(final ArrayList<ElaSimpleBean> elaSpinnerItems,int categoryId)
    {
    	//If fragment already shown once, then no need of doing this again.
    	// Even though fragments data kept alive, this condition needed since 
    	// this function is called from activity every time you scroll
    	if(mIsLoadedAlready)
    		return;
    	
    	mIsLoadedAlready = true;
    	setSubcategoryLabel(categoryId);
    	
    	// Setting adapter of spinner.
    	// Since this affects smooth view pager scrolling , we are running in thread by some delay.
		Handler handler=new Handler();
		final Runnable r = new Runnable()
		{
		    public void run() 
		    {
		        Spinner spinner = (Spinner) mInflatedView.findViewById(R.id.spSubCategories);
		        ArrayAdapter<ElaSimpleBean> spinnerArrayAdapter = new ArrayAdapter<ElaSimpleBean>(getActivity(),android.R.layout.simple_spinner_item,
								elaSpinnerItems);
		    	spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_text_view);
		    	spinner.setAdapter(spinnerArrayAdapter);
		    }
		};
		handler.postDelayed(r,300);
    }
    
	public void setSubcategoryLabel(int categoryId)
	{
		//Setting the sub category label description based on category Id
		String subCategoryCaption="";
    	switch(categoryId)
    	{
    		case 0:
    			subCategoryCaption = getString(R.string.income_sub_categories);
    			break;
    		case 1:
    			subCategoryCaption = getString(R.string.expence_sub_categories);
    			break;
    		case 2:
    			subCategoryCaption = getString(R.string.deposit_sub_categories);
    			break;
    		case 3:
    			subCategoryCaption = getString(R.string.borrow_sub_categories);
    			break;
    		case 4:
    			subCategoryCaption = getString(R.string.lend_sub_categories);
    			break;
    	}
    	TextView tvSubCategories = (TextView) mInflatedView.findViewById(R.id.tvSubCategories);
		tvSubCategories.setText(subCategoryCaption);
	}
	
    /*********************************************************************************
   	 * Code to set all event listeners to handle the events
   	 *********************************************************************************/
    public void registerEventHandlers()
    {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				submitTransaction();
			}
		});
        
        mBtnReset.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				resetScreen();
			}
		});
        
        mBtnDatePicker.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				//showing date picker dialog
				DialogFragment newFragment = new DatePickerFragment(mCalendar,(Button)view);
		        newFragment.show(getFragmentManager(), "datePicker");
			}
		});
        
        mBtnTimePicker.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				//showing time picker dialog
				DialogFragment newFragment = new TimePickerFragment(mCalendar,(Button)view);
		        newFragment.show(getFragmentManager(), "timePicker");
			}
		});
        
        //When open the spinner the opened keyboard should be hidden
        mSpSubCategories.setOnTouchListener(new HideSoftKeyboard());
    }
    
    
    
    /*********************************************************************************
   	 * Code to insert accounts in SQLite through content provider
   	 *********************************************************************************/    
    public void submitTransaction()
    {
		Context context = getActivity().getApplicationContext();
		int toastDuration = Toast.LENGTH_SHORT;
		
		//Checking if amount entered or not
		String amount = mEtAmount.getText().toString();
		if(amount.length() == 0) 
		{
			Toast toast = Toast.makeText(context,R.string.err_enter_amount,toastDuration);
			toast.setGravity(Gravity.CENTER,0,0);
			toast.show();
			return;
		}
		
		//Getting the selected dates from the button and converting to DB custom format
		String actionDate = mBtnDatePicker.getText().toString();
		String actionTime = mBtnTimePicker.getText().toString();
		String displayActionDateTime = actionDate +" "+ actionTime;
		Date actionDateObj =  new Date();
		try
		{
			actionDateObj = G.dateTimeFormatterFromDisplay.parse(displayActionDateTime);
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		actionDate = G.dateTimeFormatterToDB.format(actionDateObj);
		
		//Getting the selected sub category Id
		ElaSimpleBean elaSpinnerItem = (ElaSimpleBean)mSpSubCategories.getSelectedItem();
		int subCategoryId = elaSpinnerItem.getIntVal1();
		String comment = mEtComment.getText().toString();
		
		//inserting data in DB using content resolver
		Uri accountsUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI,G.TABLE_ACCOUNTS);
		ContentValues accountsContentValues = new ContentValues();
		accountsContentValues.put(G.COLUMN_SUB_CATEGORY_ID, subCategoryId);
		accountsContentValues.put(G.COLUMN_AMOUNT, amount);
		accountsContentValues.put(G.COLUMN_ACTION_DATE, actionDate);
		accountsContentValues.put(G.COLUMN_COMMENT, comment);
		Uri resultsUri = getActivity().getContentResolver().insert(accountsUri,accountsContentValues);
		G.log("resultsUri "+resultsUri);
		Toast toast = Toast.makeText(context,R.string.success,toastDuration);
		toast.setGravity(Gravity.CENTER,0,0);
		toast.show();
		resetScreen();
    }    
   
    /*********************************************************************************
   	 * Code to reset the screen
   	 *********************************************************************************/
    private void resetScreen()
    {
    	mEtAmount.setText("");
		setTodayDate();
		mEtComment.setText("");
    }
    
    /*********************************************************************************
   	 * Code to set the current date and time in the screen
   	 *********************************************************************************/
    private void setTodayDate()
    {
    	mCalendar.setTime(new Date());
    	String currentDateString = G.dateFormatterToDisplay.format(mCalendar.getTime());
    	String currentTimeString = G.timeFormatterToDisplay.format(mCalendar.getTime());
    	mBtnDatePicker.setText(currentDateString);
    	mBtnTimePicker.setText(currentTimeString);
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
    		super();
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
    		super();
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

}