package com.example.emanickam.sampleapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

public class MainActivity extends Activity implements MyFragment1.MyFragment1Interface{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG,"On create called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.my_fragment_container) != null)
        {
            if(savedInstanceState != null)
            {
                return;
            }

            Log.d(TAG,"creating fragment1 and adding");
            MyFragment1 myFragment1 = new MyFragment1();
            getFragmentManager().beginTransaction().add(R.id.my_fragment_container,myFragment1).commit();
        }
    }

    @Override
    public void showMessage(String message) {

        MyFragment2 myFragment2 = (MyFragment2) getFragmentManager().findFragmentById(R.id.fragment2);

        if(myFragment2 != null)
        {
            myFragment2.displayMessage(message);
        }
        else{
            myFragment2 = new MyFragment2();
            Bundle args = new Bundle();
            args.putString(MyFragment1.ARG_MESSAGE,message);
            myFragment2.setArguments(args);

            getFragmentManager().beginTransaction().replace(R.id.my_fragment_container,myFragment2).addToBackStack(null).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
