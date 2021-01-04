package com.example.notifymeproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.example.notifymeproject.R.layout.notfys_row;

public class NotfysListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> header;
    private final ArrayList<String> title;
    private final ArrayList<String> body;
    private final Integer imgid;

    public NotfysListAdapter(Activity context, ArrayList<String> header,ArrayList<String> title,ArrayList<String> body,Integer imageid) {
        super(context, notfys_row, header);


        this.context=context;
        this.header=header;
        this.title=title;
        this.body=body;
        this.imgid=imageid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(notfys_row, null,true);

        TextView head = rowView.findViewById(R.id.txt_header);
        TextView titles = rowView.findViewById(R.id.txt_title);
        TextView msg = rowView.findViewById(R.id.txt_body);
        ImageView imageView = rowView.findViewById(R.id.icon);

        head.setText(header.get(position));
        titles.setText(title.get(position));
        msg.setText(body.get(position));
        imageView.setImageResource(imgid);

        return rowView;
    }
}
