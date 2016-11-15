package elangotsharva.mylistfragmenttest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by EManickam on 28-Jan-2016 0028.
 */
public class Fragment2 extends Fragment {

    final static String ITEM_POSITION = "item_position";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment2, container, false);

    }

    @Override
    public void onStart()
    {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            showSelectedItem(args.getInt(ITEM_POSITION));
        } else
        {
            showSelectedItem(0);
        }
    }

    public void showSelectedItem(int itemPos)
    {
        Log.d("MyTestProject","inside showselected item - start");
        TextView textView = (TextView) getActivity().findViewById(R.id.display_item);
        textView.setText("Selected item: "+itemPos);
    }
}
