package elango.thaaru.MyFinance;

import java.util.LinkedHashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ElaPagerAdapter extends FragmentPagerAdapter
{
	private Map<Integer,Fragment> mFragmentsMap = new LinkedHashMap<Integer,Fragment>();
	private Map<Integer,String> mPageTitleMap = new LinkedHashMap<Integer,String>();
	private static final String CLASS = "ElaPagerAdapter ";
	
	public ElaPagerAdapter(FragmentManager fragmentManager,Map<Integer,Fragment> pageReferenceMap,Map<Integer,String> pageTitleMap )
	{
		super(fragmentManager);
		mFragmentsMap = pageReferenceMap;
		mPageTitleMap = pageTitleMap;
	}

	@Override
	public Fragment getItem(int index) 
	{
		/*G.log("Called getItem"); 
		CreateTransactionFragment fragment = new CreateTransactionFragment();
		mFragmentsMap.put(index, fragment);			
		return fragment;*/
		return mFragmentsMap.get(index);
	}
	
	@Override
	public void destroyItem(View container, int index, Object object) 
	{
	    super.destroyItem((ViewGroup)container, index, object);
	    mFragmentsMap.remove(index);
	}
	
	@Override
	public int getCount()
	{
        return mPageTitleMap.size();
    }
	
	@Override
    public CharSequence getPageTitle(int index) 
	{ 
        return mPageTitleMap.get(index);
    }
	
	public Fragment getFragment(int index)
	{
	    return mFragmentsMap.get(index);
	}
	
}