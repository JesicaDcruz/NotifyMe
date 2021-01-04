package com.example.notifymeproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class joinAssociation extends AppCompatActivity {
    String association,cid,split_assoc;
    Spinner assoc;
    ArrayAdapter adp2;
    FirebaseAuth mAuth;
    DatabaseReference dbref;
    int i, count ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_association);

        Toolbar myToolbar =  findViewById(R.id.tb_myfiles);
        myToolbar.setTitle("Join Associations");
        myToolbar.setTitleTextAppearance(this, R.style.CustomText);
        setSupportActionBar(myToolbar);

        mAuth=FirebaseAuth.getInstance();
        cid=mAuth.getCurrentUser().getUid();
        dbref= FirebaseDatabase.getInstance().getReference();

        assoc = findViewById(R.id.spnr_assoc);
        adp2= ArrayAdapter.createFromResource(joinAssociation.this,R.array.Reg_association,R.layout.support_simple_spinner_dropdown_item);
        assoc.setAdapter(adp2);

        /*assoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String topic= assoc.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


    }

    public void join_assoc(View view) {
        association = assoc.getSelectedItem().toString();
        split_assoc= association.replace(" ","_");
        FirebaseMessaging.getInstance().subscribeToTopic(split_assoc)
                /*.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(joinAssociation.this, "Topic subscribed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })*/;
        dbref.child("association").child(cid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count= (int) dataSnapshot.getChildrenCount();
                i=count+1;
                //Toast.makeText(joinAssociation.this, String.valueOf(i), Toast.LENGTH_LONG).show();
                addAssoc(i,association);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private  void addAssoc(int cnt,String assoc){
        dbref.child("association").child(cid).child("a"+cnt).setValue(assoc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //i++;
                Toast.makeText(joinAssociation.this, "Congratualtions!!You're now a member of "+ association, Toast.LENGTH_SHORT).show();
                //j=i;
            }
        });

    }


}
