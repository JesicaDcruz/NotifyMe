package com.example.notifymeproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewNotification extends AppCompatActivity {
    String title, header,body,cid;
    private static final String TAG = "FCM_Notifys";
    List<String> titles;
    List<String> headers;
    List<String> messages;
    ListView notifications;
    //List<Integer> imgid;
    SQLiteService mydb;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);

        Toolbar myToolbar = findViewById(R.id.tb_notication);
        myToolbar.setTitle("My Notifications");
        myToolbar.setTitleTextAppearance(this, R.style.CustomText);
        setSupportActionBar(myToolbar);

        auth = FirebaseAuth.getInstance();
        cid = auth.getCurrentUser().getUid();

        mydb = new SQLiteService(this);

        notifications = findViewById(R.id.lst_notifications);

        titles = new ArrayList<>();
        headers = new ArrayList<>();
        messages = new ArrayList<>();

        //mydb.deleteAllNotifications();
        mydb.deleteNotifications();

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            title = extras.getString("title");
            header = extras.getString("headers");
            body = extras.getString("body");
            Log.d(TAG, "title: " + title + " headers: " + header + " body: " + body);
            Boolean insertstate = mydb.insertNotifications(cid,header, title, body);

            HashMap<String, ArrayList<String>> hashMap = mydb.getAllNotifications(cid);
            NotfysListAdapter adapter = new NotfysListAdapter(ViewNotification.this, hashMap.get("header"), hashMap.get("title"), hashMap.get("body"), R.drawable.notification);
            notifications.setAdapter(adapter);
        }
        else{
           // mydb.deleteAllNotifications();
            HashMap<String, ArrayList<String>> hashMap = mydb.getAllNotifications(cid);
            NotfysListAdapter adapter = new NotfysListAdapter(ViewNotification.this, hashMap.get("header"), hashMap.get("title"), hashMap.get("body"), R.drawable.notification);
            notifications.setAdapter(adapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*if (getIntent().getExtras() != null) {
            String  title=getIntent().getExtras().getString("title");
            titles.add(title);
            String header=getIntent().getExtras().getString("headers");
            headers.add(header);
            String body=getIntent().getExtras().getString("body");
            messages.add(body);
            Log.d(TAG, "title: " + title + " headers: " + header + " body: " + body);



            *//*for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);

                //Log.d(TAG, headers.get(0).toString()+titles.get(0).toString()+messages.get(0).toString());
            }*//*
        }
        else{
            Log.d(TAG, "No payload");
        }*/

    }
}
