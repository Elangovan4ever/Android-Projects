package com.example.emanickam.sampleapp;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class MyFragment1 extends Fragment {

    MyFragment1Interface mMyFragment1Interface;

    public static final String ARG_MESSAGE = "arg.message";

    public interface MyFragment1Interface
    {
        public void showMessage(String message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_fragment1, container, false);

        Button button = (Button) view.findViewById(R.id.send_message);

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMessage(v);
                    }
                }
        );

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try
        {
            mMyFragment1Interface = (MyFragment1Interface) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()+" must implement MyFragment1Interface");
        }

    }

    public void sendMessage(View view)
    {
        EditText message = (EditText) getActivity().findViewById(R.id.message);

        mMyFragment1Interface.showMessage(message.getText().toString());
    }

}
