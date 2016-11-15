package elango.thaaru.MyFinance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class Statement extends ElaCommonActivity
{
	private static final String CLASS = "Statement ";

	/*********************************************************************************
   	 *  Initial function that is called on creation of activity
   	 *********************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statement);
        
        Bundle statementBundle = getIntent().getExtras();
        G.log("statementBundle: "+statementBundle);
        Fragment statementFragment = new StatementFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        statementFragment.setArguments(statementBundle);
        fragmentTransaction.add(R.id.statementLayout,statementFragment);
        fragmentTransaction.commit();
    }
}