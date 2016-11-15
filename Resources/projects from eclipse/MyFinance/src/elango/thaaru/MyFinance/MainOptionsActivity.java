package elango.thaaru.MyFinance;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class MainOptionsActivity extends ElaCommonActivity 
implements OnTouchListener,LoaderManager.LoaderCallbacks<Cursor>
{	

	private static Button mBtnAddTransaction;
	private static Button mBtnEditTransactions;
	private static Button mBtnStatistics;
	private static Button mBtnBudgetLimits;
	private static Button mBtnFutureBudget;
	private static Button mBtnSettings;
	
	private static final int CATEGORY_LOADER_ID = 1;
	
	/*********************************************************************************
   	 *  Initial function that is called on creation of activity
   	 *********************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		super.enableTitleFeatures();
		getSupportLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this);
		setContentView(R.layout.main_options_activity);
		setGlobalVariables();
		registerEventHandlers();
		super.setTitleStyle(getString(R.string.app_name));
		loadNonLogFileNmaes();
	}
	
    
    /*********************************************************************************
   	 * Code to set class level variables which are used across many functions 
   	 *********************************************************************************/
    public void setGlobalVariables()
    {
    	mBtnAddTransaction = (Button) findViewById(R.id.btnAddTransaction);
    	mBtnEditTransactions = (Button) findViewById(R.id.btnEditTransactions);
    	mBtnStatistics = (Button) findViewById(R.id.btnStatistics);
    	mBtnBudgetLimits = (Button) findViewById(R.id.btnBudgetLimits);
    	mBtnFutureBudget = (Button) findViewById(R.id.btnFutureBudget);
    	mBtnSettings = (Button) findViewById(R.id.btnSettings);
    }
    
    /*********************************************************************************
   	 * Code to set all event listeners to handle the events
   	 *********************************************************************************/
    public void registerEventHandlers()
    {
    	mBtnAddTransaction.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				onClickAddTransaction(view);
			}
		});
    	
    	mBtnEditTransactions.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				onClickEditTransactions(view);
			}
		});
    	
    	mBtnStatistics.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				onClickStatistics(view);
			}
		});
    	
    	mBtnBudgetLimits.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				onClickBudgetLimit(view);
			}
		});
    	
    	mBtnFutureBudget.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				onClickFutureBudget(view);
			}
		});
    	
    	mBtnSettings.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View view) 
			{
				onClickManageSubCategories(view);
			}
		});
    	
    	/*mBtnAddTransaction.setOnTouchListener(this);
    	mBtnEditTransactions.setOnTouchListener(this);
    	mBtnStatistics.setOnTouchListener(this);      
    	mBtnBudgetLimits.setOnTouchListener(this);    
    	mBtnFutureBudget.setOnTouchListener(this);    
    	mBtnSettings.setOnTouchListener(this); */       
    }
    
	/*********************************************************************************
   	 *  Function called when menu created.
   	 *  This is overridden from its super class to hide the up arrow since this is home page
   	 *********************************************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//getActionBar().setDisplayHomeAsUpEnabled(false);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.action_bars_menu,menu);
		return true;
	}
	
	/*********************************************************************************
   	 *  Function to start createTransaction activity
   	 *********************************************************************************/
	public void onClickAddTransaction(View view)
	{
		Intent intent = new Intent(this,CreateTransaction.class);
		startActivity(intent);
	}
	
	/*********************************************************************************
   	 *  Function to start createTransaction activity
   	 *********************************************************************************/
	public void onClickBudgetLimit(View view)
	{
		Intent intent = new Intent(this,CreateTransaction.class);
		startActivity(intent);
	}
	
	/*********************************************************************************
   	 *  Function to start view/Edit transaction activity
   	 *********************************************************************************/
	public void onClickEditTransactions(View view)
	{
		Intent intent = new Intent(this,ViewEditTransaction.class);
		startActivity(intent);
	}	
	
	/*********************************************************************************
   	 *  Function to start createTransaction activity
   	 *********************************************************************************/
	public void onClickStatistics(View view)
	{
		Intent intent = new Intent(this,Statistics.class);
		startActivity(intent);
	}
	
	/*********************************************************************************
   	 *  Function to start application specific settings activity
   	 *********************************************************************************/
	public void onClickSettings(View view)
	{
		Intent intent = new Intent(this,Settings.class);
		startActivity(intent);
	}
	
	/*********************************************************************************
   	 *  Function to start createTransaction activity
   	 *********************************************************************************/
	public void onClickFutureBudget(View view)
	{
		Intent intent = new Intent(this,FutureTransactions.class);
		startActivity(intent);
	}
	
	/*********************************************************************************
   	 *  Function to start createTransaction activity
   	 *********************************************************************************/
	public void onClickManageSubCategories(View view)
	{
		Intent intent = new Intent(this,ManageSubCategories.class);
		startActivity(intent);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) 
	{
		Button button = (Button) view;
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) 
		{
			G.log("action down");
			button.getBackground().setColorFilter(0xFF838B83, PorterDuff.Mode.MULTIPLY);
			return true;
		} 
		/*else if (motionEvent.getAction() == MotionEvent.ACTION_UP) 
		{
			G.log("action up");
			button.getBackground().setColorFilter(0xFF838B83, PorterDuff.Mode.DARKEN);
			return true;
		}*/
		return false;
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
			case CATEGORY_LOADER_ID:
				Uri categorysUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI, G.TABLE_CATEGORIES);
				cursorLoader = new CursorLoader(this, categorysUri, null, null, null, null);
				break;
		}
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) 
	{		
		switch (cursorLoader.getId()) 
		{
			case CATEGORY_LOADER_ID:
				ElaCommonClass.setCategories(cursor);
				break;

		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) 
	{
		switch (cursorLoader.getId()) 
		{
			case CATEGORY_LOADER_ID:
				break;
		}
	}
	
	//Delete this function on deployment
	public void loadNonLogFileNmaes()
	{
		try
		{
			InputStream inputStream = getResources().openRawResource(R.raw.dont_log_filenames);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	        String fileName;
	        while((fileName = br.readLine()) != null) 
	        {
	        	if(!fileName.startsWith("--"))
	        		G.nonLogFileNames.add(fileName);
	        }
		}
		catch (Exception e) 
		{
			G.log("!!! Exception: "+e.getMessage());
			e.printStackTrace();
		}
	}

		
}
