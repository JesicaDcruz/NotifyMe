package com.example.notifymeproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.notifymeproject.R.layout.listview_row;

public class Customlistadapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;

    public Customlistadapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, listview_row, itemname);


        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(listview_row, null,true);

        TextView extratxt = rowView.findViewById(R.id.Itemname);
        ImageView imageView = rowView.findViewById(R.id.icon);


        extratxt.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        return rowView;

    };
}
