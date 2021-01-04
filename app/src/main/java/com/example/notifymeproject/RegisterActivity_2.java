package com.example.notifymeproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity_2 extends AppCompatActivity {
    String userval,sid,name,email,pass,mob,courses,dpt,sems;
    Spinner course, dept, sem;
    ArrayAdapter adp0,adp1,adp2;
    EditText mob_no;
    Intent intent,i2;
    Bundle extras2;
int i;
    //Declare an instance of FirebaseAuth
    private FirebaseAuth uAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_2);
        mob_no=findViewById(R.id.edittxt_mobile);
        uAuth = FirebaseAuth.getInstance();
        course= findViewById(R.id.spin_course);
        adp1= ArrayAdapter.createFromResource(this,R.array.Reg_course,R.layout.support_simple_spinner_dropdown_item);
        course.setAdapter(adp1);
        dept=findViewById(R.id.spin_dept);
        adp0= ArrayAdapter.createFromResource(this,R.array.Reg_depts,R.layout.support_simple_spinner_dropdown_item);
        dept.setAdapter(adp0);
        sem=findViewById(R.id.spin_sem);
        adp2= ArrayAdapter.createFromResource(this,R.array.Reg_sems,R.layout.support_simple_spinner_dropdown_item);
        sem.setAdapter(adp2);

//get data from regactivity_1
        intent = getIntent();
        Bundle extras1=intent.getExtras();
        //String[] uservals=extras1.getStringArray("uservals");
         userval=extras1.getString("userval");
          sid=extras1.getString("id");
         name=extras1.getString("name");
         email=extras1.getString("email");
         pass=extras1.getString("pass");
        //Toast.makeText(this,"data sent"+ userval+" "+sid+" "+name+" "+email+" "+pass,Toast.LENGTH_LONG).show();
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        if (uAuth.getCurrentUser() != null) {
            //Condition to handle the already logged in user
            Toast.makeText(this, "You are already logged in.", Toast.LENGTH_LONG).show();
            i2=new Intent(RegisterActivity_2.this,HomeActivity.class);
        }
    }*/

    private boolean validateUserInput(){
        Boolean res;
        mob=mob_no.getText().toString().trim();
        if (TextUtils.isEmpty(mob)){
            mob_no.setError("Mobile Number Required");
            res=false;
        }
        else if(mob.length()!=10){
            mob_no.setError("Length of Mobile Number should be 10");
            res=false;
        }
        else
            res= true;
        return res;

    }

    private void insertData(){
        uAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //String userId=user.getUid();
                            UserData user = new UserData(userval,sid,name,email,dpt,courses,sems,mob);
                            FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).setValue(user)
                                    .addOnCompleteListener(RegisterActivity_2.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(RegisterActivity_2.this,"Congrats!! Registeration Successfull",Toast.LENGTH_SHORT).show();
                                            FirebaseMessaging.getInstance().subscribeToTopic(dpt.replace(" ","_")+"_"+courses+"_"+sems.replace(" ","_"))
                                                    /*.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(RegisterActivity_2.this, "Topic subscribed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    })*/;
                                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                            finish();
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity_2.this,"Registeration Not Successfull",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void moveToHome(View view) {
        dpt=dept.getSelectedItem().toString();
        courses=course.getSelectedItem().toString();
        sems=sem.getSelectedItem().toString();
        if (validateUserInput()) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    final String token=task.getResult().getToken();
                    FirebaseDatabase.getInstance().getReference().child("group_tokens").child(dpt+"Student").child(sid).setValue(token)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Log.i("regactivity","Success");
                                }
                            });
                    //storeRegistrationToken(token,dpt2);
                }
            });
            insertData();
           /* FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    String token=task.getResult().getToken();
                    FirebaseDatabase.getInstance().getReference().child("group_tokens").child(dpt+"Student").child(sid).setValue(token)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Log.i("regactivity","Success");
                                }
                            });
                    //storeRegistrationToken(token,dpt2);
                }
            });*/

        }

       /*extras2=new Bundle();
        courses= course.getSelectedItem().toString();
        dpt= dept.getSelectedItem().toString();
        sems= sem.getSelectedItem().toString();
        mob=mob_no.getText().toString();
        extras2.putString("userval",userval);
        extras2.putString("id", sid);
        extras2.putString("name", name);
        extras2.putString("email", email);
        extras2.putString("course",courses);
        extras2.putString("dep", dpt);
        extras2.putString("sem",sems);
        extras2.putString("mobile",mob);
        i2=new Intent(this,HomeActivity.class);
        i2.putExtras(extras2);
        startActivity(i2);*/

    }
    private  void addToken(int cnt, String tk){
        FirebaseDatabase.getInstance().getReference().child("group_tokens").child(dpt+"Student").child("t"+cnt).setValue(tk)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.i("regactivity","Success");
                    }
                });

    }

}
