package elango.thaaru.MyFinance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public class Search extends ElaCommonActivity
{
	private static final String CLASS = "CreateTransaction ";
	
	/*********************************************************************************
   	 * Initial function that is called on creation of activity
   	 *********************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);        
        
        loadSearchFragment();
    }
   
    /*********************************************************************************
   	 * Code to load the search fragment 
   	 *********************************************************************************/
    public void loadSearchFragment()
    {
    	Fragment searchFragment = new SearchFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.lytSearchActivity,searchFragment);
        fragmentTransaction.commit();
    }   
}