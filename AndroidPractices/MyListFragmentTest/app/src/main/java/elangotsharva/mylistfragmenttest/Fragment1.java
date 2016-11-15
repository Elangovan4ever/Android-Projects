package elangotsharva.mylistfragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/**
 * Created by EManickam on 28-Jan-2016 0028.
 */
public class Fragment1 extends ListFragment implements AdapterView.OnItemClickListener
{
    Fragment1Listener fragment1Listener;

    public interface Fragment1Listener
    {
        public void onFragment1ListItemClick(int itemPos);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            fragment1Listener = (Fragment1Listener) context;
        }
        catch (ClassCastException ce){
            throw new ClassCastException(context.toString()+" must implement Fragment1Listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment1, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.myliststrings,android.R.layout.simple_list_item_1);
        setListAdapter(arrayAdapter);

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        fragment1Listener.onFragment1ListItemClick(i);

    }
}
