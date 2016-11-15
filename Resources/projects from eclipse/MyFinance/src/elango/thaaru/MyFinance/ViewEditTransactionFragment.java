package elango.thaaru.MyFinance;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;


public class ViewEditTransactionFragment extends Fragment
{
	private static final String CLASS = "ViewEditTransactionFragment ";
	private View mInflatedView;
	private int mCategoryId = G.NOTHING_INT;
	
	private boolean mIsLoadedAlready = false;
	
	/*********************************************************************************
   	 *  Initial function that is called on creation of activity
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
		mInflatedView = layoutInflater.inflate(R.layout.view_edit_transactions_fragment, container,
				false);		
		return mInflatedView;
	}
	
	/*********************************************************************************
	 * Function called after the fragment drawn in user view
	 *********************************************************************************/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		G.log("In onActivityCreated called");
		super.onActivityCreated(savedInstanceState);        
        registerEventHandlers();        
    }
	 
	/*********************************************************************************
   	 * Code to set all event listeners to handle the events
   	 *********************************************************************************/
    public void registerEventHandlers()
    {
    	final TextView tvRecentTransactions = (TextView) mInflatedView.findViewById(R.id.tvRecentTransactions);
    	final TextView tvSearchTransactions = (TextView) mInflatedView.findViewById(R.id.tvSearchTransactions);
    	tvRecentTransactions.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View arg0)
			{
				tvRecentTransactions.setSelected(true);
				tvSearchTransactions.setSelected(false);
				loadStatementFragment();
			}
		});
    	
    	tvSearchTransactions.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View arg0)
			{
				tvRecentTransactions.setSelected(false);
				tvSearchTransactions.setSelected(true);
				loadSearchFragment();
			}
		});
    }
	
    public void loadFragmantData(int categoryId)
    {
    	mCategoryId = categoryId;
       	loadStatementFragment();
    }
    
	/*********************************************************************************
   	 * Code to load the statement fragment 
   	 *********************************************************************************/
    public void loadStatementFragment()
    {
    	//If fragment already shown once, then no need of doing this again.
    	// Even though fragments data kept alive, this condition needed since 
    	// this function is called from activity every time you scroll
    	
    	if(mIsLoadedAlready)
    	{
    		try 
    		{
	    		final StatementFragment statementFragment = (StatementFragment) getChildFragmentManager().findFragmentById(R.id.viewEditContentLayout);
	    		if(statementFragment != null)
	    		{
		    		Handler handler=new Handler();
					final Runnable r = new Runnable()
					{
					    public void run() 
					    {
					    	final TextView tvRecentTransactions = (TextView) mInflatedView.findViewById(R.id.tvRecentTransactions);
					    	final TextView tvSearchTransactions = (TextView) mInflatedView.findViewById(R.id.tvSearchTransactions);
					    	tvRecentTransactions.setSelected(true);
					    	tvSearchTransactions.setSelected(false);
					    	statementFragment.loadLatestTransactions();
					    }
					};
					handler.postDelayed(r,300);
					return;
	    		}   
    		}
    		catch (ClassCastException e) 
    		{
				// This exception will come when user comes back from search fragment by clicking radio button.
    			// Because search fragment cannot be converted to statement fragment
			}
    	}    		
    	
    	mIsLoadedAlready = true;
    	Handler handler=new Handler();
		final Runnable r = new Runnable()
		{
		    public void run() 
		    {
		    	G.log("loadStatementFragment called");
		    	final StatementFragment statementFragment = new StatementFragment();
		    	FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		        fragmentTransaction.replace(R.id.viewEditContentLayout,statementFragment);
		        fragmentTransaction.commit();
		        
		        Handler handler=new Handler();
				final Runnable r = new Runnable()
				{
				    public void run() 
				    {
				    	final StatementFragment statementFragment = (StatementFragment) getChildFragmentManager().findFragmentById(R.id.viewEditContentLayout);
				    	final TextView tvRecentTransactions = (TextView) mInflatedView.findViewById(R.id.tvRecentTransactions);
				    	final TextView tvSearchTransactions = (TextView) mInflatedView.findViewById(R.id.tvSearchTransactions);
				    	tvRecentTransactions.setSelected(true);
				    	tvSearchTransactions.setSelected(false);
				    	statementFragment.loadLatestTransactions();
				    }
				};
				handler.postDelayed(r,200);
		    }
		};
		handler.postDelayed(r,200);

    }
    
    /*********************************************************************************
   	 * Code to load the search fragment 
   	 *********************************************************************************/
    public void loadSearchFragment()
    {
    	TextView tvSearchTransactions = (TextView) mInflatedView.findViewById(R.id.tvSearchTransactions);
        tvSearchTransactions.setText(getString(R.string.search));
        
    	Fragment searchFragment = new SearchFragment();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.viewEditContentLayout,searchFragment);
        fragmentTransaction.commit();
    }
    
   
}