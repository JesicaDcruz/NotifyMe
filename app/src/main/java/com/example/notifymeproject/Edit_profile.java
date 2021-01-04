package com.example.notifymeproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.HashMap;


public class Edit_profile extends AppCompatActivity {
    TextView cour,seme;
    EditText id,name,email,mob,dep;
    Spinner cou,sem ;
    String uid,muserId,mname,memail,cont,mdep,mcour,msem,type;
    ArrayAdapter adp1,adp2;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth=FirebaseAuth.getInstance();

        Toolbar myToolbar =  findViewById(R.id.tb_editprofile);
        myToolbar.setTitle("My Details");
        myToolbar.setTitleTextAppearance(this, R.style.CustomText);
        setSupportActionBar(myToolbar);

        cour=findViewById(R.id.txt_cour);
        seme=findViewById(R.id.txt_sem);
        id=findViewById(R.id.edittxt_id);
        name=findViewById(R.id.edittxt_name);
        email=findViewById(R.id.edittxt_email);
        mob=findViewById(R.id.edittxt_mob);
        dep=findViewById(R.id.edittxt_dep);
        cou=findViewById(R.id.spnr_cour);
        adp1= ArrayAdapter.createFromResource(this,R.array.Reg_course,R.layout.support_simple_spinner_dropdown_item);
        cou.setAdapter(adp1);
        sem=findViewById(R.id.spnr_sem);
        adp2= ArrayAdapter.createFromResource(this,R.array.Reg_sems,R.layout.support_simple_spinner_dropdown_item);
        sem.setAdapter(adp2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        uid=mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //user.clear();
                //for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    muserId = dataSnapshot.child("userid").getValue(String.class);
                    mname = dataSnapshot.child("uname").getValue(String.class);
                    memail = dataSnapshot.child("uemail").getValue(String.class);
                    cont = dataSnapshot.child("mob_pass").getValue(String.class);
                    mdep = dataSnapshot.child("udep").getValue(String.class);
                    mcour = dataSnapshot.child("ucourse").getValue(String.class);
                    msem = dataSnapshot.child("usem").getValue(String.class);
                    type = dataSnapshot.child("usertype").getValue(String.class);
                //}
                id.setText(muserId);
                name.setText(mname);
                email.setText(memail);
                mob.setText(cont);
                dep.setText(mdep);

                //setSpinText();
                if (type.equals("Staff")){
                    cour.setVisibility(View.GONE);
                    seme.setVisibility(View.GONE);
                    cou.setVisibility(View.GONE);
                    sem.setVisibility(View.GONE);
                }
                else{
                    setSpinText(cou,mcour);
                    setSpinText(sem,msem);
                }

                /*cou.setText(mcour);
                sem.setText(msem);*/
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void setSpinText(Spinner spin, String text)
    {
        for(int i= 0; i < spin.getAdapter().getCount(); i++)
        {
            if(spin.getAdapter().getItem(i).toString().contains(text))
            {
                spin.setSelection(i);
            }
        }

    }

    public void update_details(View view) {
        uid=mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        HashMap<String, Object> result = new HashMap<>();
        if (type.equals("Staff")){
            result.put("uemail", email.getText().toString());
            result.put("mob_pass",mob.getText().toString());
            result.put("ucourse","NA");
            result.put("usem","NA");
        }
        else{
            result.put("uemail", email.getText().toString());
            result.put("mob_pass",mob.getText().toString());
            result.put("ucourse",cou.getSelectedItem().toString());
            result.put("usem",sem.getSelectedItem().toString());
        }
        ref.updateChildren(result);
        Toast.makeText(this,"Details updated",Toast.LENGTH_LONG).show();
        Intent i=new Intent(this,StaffHomeAcrivity.class);

    }
}
