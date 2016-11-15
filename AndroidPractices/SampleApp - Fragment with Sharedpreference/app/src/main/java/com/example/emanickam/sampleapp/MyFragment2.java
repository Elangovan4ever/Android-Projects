package com.example.emanickam.sampleapp;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment2 extends Fragment {

    public static final String ARG_MESSAGE = "arg.message";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_fragment2, container, false);

        if(getArguments() != null) {
            TextView displayMessage = (TextView) view.findViewById(R.id.display_message);
            displayMessage.setText(getArguments().getString(ARG_MESSAGE));
        }

        return view;
    }

    public void displayMessage(String message)
    {
        TextView displayMessage = (TextView) getActivity().findViewById(R.id.display_message);
        displayMessage.setText(message);
    }

}
