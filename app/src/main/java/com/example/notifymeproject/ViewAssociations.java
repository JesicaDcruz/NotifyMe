package com.example.notifymeproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAssociations extends AppCompatActivity {
    private static final String TAG = "assoc: " ;
    String cid,selecteditem;
ListView myassoc;
FirebaseAuth auth;
DatabaseReference ref;
    ArrayList<String> myass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_associations);

        Toolbar myToolbar =  findViewById(R.id.tb_myassoc);
        myToolbar.setTitle("My Associations");
        myToolbar.setTitleTextAppearance(this, R.style.CustomText);
        setSupportActionBar(myToolbar);

        myassoc = findViewById(R.id.lstvw_assoc);

        auth=FirebaseAuth.getInstance();
        cid = auth.getCurrentUser().getUid();

        myassoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selecteditem = myass.get(i);
                Toast.makeText(ViewAssociations.this,selecteditem,Toast.LENGTH_SHORT).show();
                Intent dept_intent = new Intent(ViewAssociations.this,ChatsActivity.class);
                dept_intent.putExtra("chat_title",selecteditem);
                startActivity(dept_intent);

            }
        });

        ref= FirebaseDatabase.getInstance().getReference().child("association").child(cid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myass=new ArrayList<>();
                int count= (int) dataSnapshot.getChildrenCount();
                for (int i=1;i<=count;i++){
                    myass.add(dataSnapshot.child("a"+i).getValue(String.class));
                }
                String fname[]=new String[myass.size()];
                Integer fimgid[]=new Integer[myass.size()];
                for (int j=0;j<myass.size();j++){
                    //fname[j]=myass.get(j);
                    fimgid[j]=R.drawable.myassocs;
                }
                Customlistadapter adp=new Customlistadapter(ViewAssociations.this,myass.toArray(fname),fimgid);
                myassoc.setAdapter(adp);
               /* for (int i=1;i<=assoc.size();i++){
                    myass = assoc.get("a"+i).toString();
                    Log.i(TAG,myass);
                }*/



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
