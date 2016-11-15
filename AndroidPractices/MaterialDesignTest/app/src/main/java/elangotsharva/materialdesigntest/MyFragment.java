package elangotsharva.materialdesigntest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by EManickam on 14-Feb-2016 0014.
 */
public class MyFragment extends Fragment {

    public final static String TAB_ID_KEY = "Tab_Id";

    public MyFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        String textToDisplay = "Default";
        Bundle args = getArguments();
        if(args != null) {
            textToDisplay = "Fragment in Tab : " + args.getInt(MyFragment.TAB_ID_KEY);
        }

        View view = inflater.inflate(R.layout.my_fragment, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text_in_fragment);
        textView.setText(textToDisplay);
        return view;
    }

    public static MyFragment createInstance(int tabId)
    {
        MyFragment myFragment = new MyFragment();
        Bundle args = new Bundle();
        args.putInt(TAB_ID_KEY,tabId);
        myFragment.setArguments(args);

        return myFragment;
    }
}
