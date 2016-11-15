package com.example.emanickam.sampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    public static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG,"onCreate");

        ListView listView = (ListView) findViewById(R.id.listview);
        List<ListItem> listItems = createDummyItems();

        ArrayAdapter<ListItem> listAdapter = new myCustomArrayAdapter(this, R.layout.list_item, listItems);
        listView.setAdapter(listAdapter);
    }

    private List<ListItem> createDummyItems()
    {
        List<ListItem> tempListItems = new ArrayList<>();

        for (int i=0;i<10; i++){
            ListItem listItem = new ListItem();
            listItem.setItemTitle("Title"+i);
            listItem.setItemPrice(i*1000);

            tempListItems.add(listItem);
        }

        return tempListItems;

    }
}
