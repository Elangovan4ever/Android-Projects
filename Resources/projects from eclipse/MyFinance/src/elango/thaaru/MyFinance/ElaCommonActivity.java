package elango.thaaru.MyFinance;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public abstract class ElaCommonActivity extends FragmentActivity
{
	static final String CLASS = "ElaCommonActivity";
	
	private boolean customTitleSupported;
	
	public void enableTitleFeatures()
	{
		customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}
	
	public void setTitleStyle(String caption)
	{
        if(customTitleSupported) 
        {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
            TextView captionTextView = (TextView) findViewById(R.id.tvCaption);
            captionTextView.setText(caption);
        }
	}

	/*********************************************************************************
   	 *  function that is called on creation of menu
   	 *********************************************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.action_bars_menu,menu);
		return true;
	}
	
	/*********************************************************************************
   	 *  Function to start search activity
   	 *********************************************************************************/
	public void ShowSearch(MenuItem menuItem)
	{
		Intent intent = new Intent(this,Search.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	/*********************************************************************************
   	 *  Function to start activity that shows information about application
   	 *********************************************************************************/
	public void ShowAboutApp(MenuItem menuItem)
	{
		Intent intent = new Intent(this,Settings.class);
		startActivity(intent);
	}
	
	/*********************************************************************************
   	 *  Function to start activity that shows information about me
   	 *********************************************************************************/
	public void ShowAboutUs(MenuItem menuItem)
	{
		Intent intent = new Intent(this,Settings.class);
		startActivity(intent);
	}
	
	/*********************************************************************************
   	 *  Function to handle events when an item clicked from action bar menu
   	 *********************************************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
	        case android.R.id.home:
	            Intent intent = new Intent(this, MainOptionsActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
