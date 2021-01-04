package com.example.notifymeproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    TextView dispname;
    ListView list;
    String disp_name,uid;
    String[] itemname ={
            "Upload Files",
            "View Files",
            "My Notifications",
            "Edit Profile",
            "Log Out"
    };
    Integer[] imgid={
            R.drawable.upload_files,
            R.drawable.view_files,
            R.drawable.manage_notification,
            R.drawable.edit_profile,
            R.drawable.log_out
    };

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    SharedPreferences sharedpreferences;
    private String MyPREFERENCES="statePrefernce";
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth= FirebaseAuth.getInstance();
        dispname=findViewById(R.id.txt_disp_name);
        Customlistadapter adapter=new Customlistadapter(this, itemname, imgid);
        list=findViewById(R.id.lst_view);
        list.setAdapter(adapter);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String Slecteditem= itemname[+position];
                switch (Slecteditem){
                    case "Upload Files":
                        startActivity(new Intent(HomeActivity.this,Upload_files.class));
                        //Toast.makeText(getApplicationContext(), "UF", Toast.LENGTH_SHORT).show();
                        //finish();
                        break;
                    case "View Files":
                        startActivity(new Intent(HomeActivity.this,Viewfiles.class));
                        //Toast.makeText(getApplicationContext(), "VF", Toast.LENGTH_SHORT).show();
                        break;
                    case "My Notifications":
                        startActivity(new Intent(HomeActivity.this,ViewNotification.class));
                        //Toast.makeText(getApplicationContext(), "MN", Toast.LENGTH_SHORT).show();
                        break;
                    case "Edit Profile":
                        startActivity(new Intent(HomeActivity.this,Edit_profile.class));
                        //Toast.makeText(getApplicationContext(), "EP", Toast.LENGTH_SHORT).show();
                        //finish();
                        break;
                    case "Log Out":
                        FirebaseAuth.getInstance().signOut();
                        editor = sharedpreferences.edit();
                        editor.putBoolean("userstate",false);
                        editor.commit();
                        //Toast.makeText(getApplicationContext(), "LO", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Invalid Choice.", Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        uid=mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("uname");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                disp_name ="Hello "+ dataSnapshot.getValue().toString();
                dispname.setText(disp_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
