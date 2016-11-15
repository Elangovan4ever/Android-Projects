package com.example.emanickam.sampleapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class myCustomArrayAdapter extends ArrayAdapter<ListItem>
{
    private List<ListItem> listItems;

    String[] images = {"jacket101","jacket102","jacket103","pants101","pants102","shirt101","shirt102","shirt103","shirt104","shirt105","shirt106","shirt107"};

    public myCustomArrayAdapter(Context context, int resource, List<ListItem> objects) {
        super(context, resource, objects);

        listItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        ListItem listItem = listItems.get(position);

        TextView tvItemName = (TextView) convertView.findViewById(R.id.item_name);
        tvItemName.setText(listItem.getItemTitle());

        TextView tvItemPrice = (TextView) convertView.findViewById(R.id.item_price);
        tvItemPrice.setText(listItem.getItemPrice()+"");

        ImageView ivItemImage = (ImageView) convertView.findViewById(R.id.list_image);
        try {
            ivItemImage.setImageBitmap(BitmapFactory.decodeStream(getContext().getAssets().open(images[position]+".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
