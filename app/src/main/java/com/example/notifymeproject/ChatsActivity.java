package com.example.notifymeproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {
    private static final String TAG = "CHATS";
    String chats_title,id,name,title, header,body;
FirebaseAuth auth;
DatabaseReference ref;
MessageAdapter adp;
List<Message> msgs;
RecyclerView recvw;
EditText mymsg;
    JSONArray mytokens;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        final Toolbar myToolbar =  findViewById(R.id.tb_chats);
        myToolbar.setTitle(chats_title+" Chat Room");
        myToolbar.setTitleTextAppearance(this, R.style.CustomText);
        setSupportActionBar(myToolbar);

        recvw =  findViewById(R.id.recycle_vw_chats_dep);
        recvw.setNestedScrollingEnabled(false);
        mymsg = findViewById(R.id.edittxt_msg);

        auth = FirebaseAuth.getInstance();
        msgs = new ArrayList<>();

        if(getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            title = extras.getString("title");
            header = extras.getString("headers");
            body = extras.getString("body");
            Log.d(TAG, "title: " + title + " headers: " + header + " body: " + body);
            myToolbar.setTitle(title.replace("_"," ")+" Chat Room");
            getChats(auth.getCurrentUser().getUid(),title.replace("_"," "));
        }
        else{
          FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String dep = dataSnapshot.child("udep").getValue().toString();
                    myToolbar.setTitle(dep+" Chat Room");
                    getChats(auth.getCurrentUser().getUid(),dep);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        //chats_title = getIntent().getStringExtra("chat_title");

    }

    @Override
    protected void onStart() {
        super.onStart();

    }



    @Override
    protected void onResume() {
        super.onResume();
        msgs = new ArrayList<>();

    }

    private void displayMessages(List<Message> msgs) {
        recvw.setLayoutManager(new LinearLayoutManager(ChatsActivity.this));
        adp =new MessageAdapter(ChatsActivity.this,msgs,ref);
        recvw.setAdapter(adp);

    }

    public void sendMessage(View view) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        Date date = new Date();
        final String messg = mymsg.getText().toString().trim();
        if (messg.length()!=0){
            Message mssg = new Message(messg,name,dateFormat.format(date));
            mymsg.setText("");
            ref.push().setValue(mssg).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.i(TAG, name+" : "+messg);
                    sendNewMessageNotification(messg);

                }
            });
        }
        else{
            Toast.makeText(this,"Blank messages not allowed.",Toast.LENGTH_LONG).show();
        }
    }

   /* public void refresh_content(View view) {
       // recvw.
        msgs = new ArrayList<>();
        displayMessages(msgs);
    }*/

    public void getChats(final String id,String chats_title){
        FirebaseDatabase.getInstance().getReference().child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("uname").getValue().toString();
                AllMethods.name=name;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       /* messages format in db:
        +messages
                +dep_assoc_chats:
                    +m1:
                        +sender:
                        +msg:
                    +m2:
                        +sender:
                        +msg:*/
        ref = FirebaseDatabase.getInstance().getReference().child("messages").child(chats_title+"_chats");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message msg= dataSnapshot.getValue(Message.class);
                msg.setKey(dataSnapshot.getKey());
                msgs.add(msg);
                displayMessages(msgs);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message msg= dataSnapshot.getValue(Message.class);
                msg.setKey(dataSnapshot.getKey());

                List<Message> newMsgs = new ArrayList<Message>();
                for (Message m: msgs){
                    if(m.getKey().equals(msg.getKey())){
                        newMsgs.add(msg);
                    }
                    else{
                        newMsgs.add(m);
                    }
                }

                msgs=newMsgs;
                //adp.notifyDataSetChanged();
                displayMessages(msgs);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Message msg= dataSnapshot.getValue(Message.class);
                msg.setKey(dataSnapshot.getKey());

                List<Message> newMsgs = new ArrayList<Message>();
                for (Message m: msgs){
                    if(m.getKey().equals(msg.getKey())){
                        newMsgs.add(m);
                    }
                }
                msgs.remove(newMsgs.get(0));
               // msgs=newMsgs;
                //adp.notifyDataSetChanged();
                displayMessages(msgs);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendNewMessageNotification(final String messg){
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sid= dataSnapshot.child("userid").getValue(String.class);
                String dep = dataSnapshot.child("udep").getValue(String.class);
                getTokens(messg,sid,dep);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       // NotificationHelper myhelper=new NotificationHelper();
        //myhelper.generateNotification(getApplicationContext(),chats_title.replace(" ","_"),name+":"+messg,"NA",chats_title);
    }

    private void getTokens(final String msg, final String id, final String title){
        FirebaseDatabase.getInstance().getReference().child("group_tokens").child(title+"Staff").addValueEventListener(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mytokens=new JSONArray();
                for(DataSnapshot item:dataSnapshot.getChildren()){
                    if(!item.getKey().equals(id))
                        mytokens.put(dataSnapshot.child(item.getKey()).getValue(String.class));
                }
                NotificationHelper myhelper = new NotificationHelper();
                myhelper.generateNotification(getApplicationContext(),title.replace(" ","_"),name+":"+msg,"NA",mytokens);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
