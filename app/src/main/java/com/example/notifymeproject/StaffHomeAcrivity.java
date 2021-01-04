package com.example.notifymeproject;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class StaffHomeAcrivity extends AppCompatActivity {
    Intent i4;
    TextView dispname;
    ListView list;
    String disp_name,uid,dep,userid;
    int i;
    String[] itemname ={
            "Upload Files",
            "View Files",
            "Department Chats",
            "Association Chats",
            "Manage Notification",
            "Join Associations",
            "Edit Profile",
            "Log Out"
    };
    Integer[] imgid={
            R.drawable.upload_files,
            R.drawable.view_files,
            R.drawable.dpt_chat,
            R.drawable.assoc_chats,
            R.drawable.manage_notification,
            R.drawable.association,
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
        setContentView(R.layout.activity_staff_home_acrivity);
        mAuth= FirebaseAuth.getInstance();
        dispname=findViewById(R.id.txt_disp_name);

        uid=mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userid= dataSnapshot.child("userid").getValue().toString();
                disp_name ="Hello "+ dataSnapshot.child("uname").getValue().toString();
                dispname.setText(disp_name);
                dep = dataSnapshot.child("udep").getValue().toString();
                updateToken(dep,userid);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                        startActivity(new Intent(StaffHomeAcrivity.this,Upload_files.class));
                        //Toast.makeText(getApplicationContext(), "UF", Toast.LENGTH_SHORT).show();
                        //finish();
                        break;
                    case "View Files":
                        startActivity(new Intent(StaffHomeAcrivity.this,StaffViewFiles.class));
                        //Toast.makeText(getApplicationContext(), "VF", Toast.LENGTH_SHORT).show();
                        break;
                    case "Department Chats":
                       /* Intent dept_intent = new Intent(StaffHomeAcrivity.this,ChatsActivity.class);
                        dept_intent.putExtra("chat_title",dep);*/
                        startActivity(new Intent(StaffHomeAcrivity.this,ChatsActivity.class));
                        //Toast.makeText(getApplicationContext(), "DC", Toast.LENGTH_SHORT).show();
                        break;
                    case "Association Chats":
                        startActivity(new Intent(StaffHomeAcrivity.this,ViewAssociations.class));
                        //Toast.makeText(getApplicationContext(), "AC", Toast.LENGTH_SHORT).show();
                        break;
                    case "Manage Notification":
                        startActivity(new Intent(StaffHomeAcrivity.this,ManageNotifications.class));
                       // Toast.makeText(getApplicationContext(), "MN", Toast.LENGTH_SHORT).show();
                        break;
                    case "Join Associations":
                        startActivity(new Intent(StaffHomeAcrivity.this,joinAssociation.class));
                        //Toast.makeText(getApplicationContext(), "MN", Toast.LENGTH_SHORT).show();
                        break;
                    case "Edit Profile":
                        startActivity(new Intent(StaffHomeAcrivity.this,Edit_profile.class));
                        //Toast.makeText(getApplicationContext(), "EP", Toast.LENGTH_SHORT).show();
                        //finish();
                        break;
                    case "Log Out":
                        FirebaseAuth.getInstance().signOut();
                        editor = sharedpreferences.edit();
                        editor.putBoolean("userstate",false);
                        editor.commit();
                        //Toast.makeText(getApplicationContext(), "LO", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(StaffHomeAcrivity.this, LoginActivity.class));
                        finish();
                        break;
                     default:
                         Toast.makeText(getApplicationContext(), "Select Your Operation", Toast.LENGTH_SHORT).show();
                         break;
                }


            }
        });
        /*i4 = getIntent();
        Bundle extras1=i4.getExtras();
        String userval=extras1.getString("userval");
        String  a=extras1.getString("id");
        String b=extras1.getString("name");
        String c=extras1.getString("email");
        String d=extras1.getString("dep");
        String e=extras1.getString("mno");
        Toast.makeText(this,"data sent "+userval+" "+a+" "+b+" "+c+" "+d+" "+e,Toast.LENGTH_LONG).show();*/
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public  void updateToken(final String dpt, final String myid){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                final String token=task.getResult().getToken();
                FirebaseDatabase.getInstance().getReference().child("group_tokens").child(dpt+"Staff").child(myid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String stored_token=dataSnapshot.getValue(String.class);
                        int count= (int) dataSnapshot.getChildrenCount();
                        i=count+1;
                        if(!stored_token.equals(token)){
                            Log.i("Stffhome","token updated");
                            addToken(i,token,dpt,myid);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //storeRegistrationToken(token,dpt2);
            }
        });
    }

    private  void addToken(int cnt, String tk,String dpt,String mid){
        FirebaseDatabase.getInstance().getReference().child("group_tokens").child(dpt+"Staff").child(mid).setValue(tk)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.i("regactivity","Success");
                    }
                });

    }

}
