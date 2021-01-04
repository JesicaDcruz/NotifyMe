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

public class RegisterActivity_3 extends AppCompatActivity {
    Intent intent3, i3;
    Spinner dept2;
    EditText mobno;
    ArrayAdapter adps;
     String userval, sid, name, email,pass, mnum1, dpt2;
int i;
    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_3);
        mAuth = FirebaseAuth.getInstance();
        dept2 = findViewById(R.id.spn_dep2);
        adps = ArrayAdapter.createFromResource(this, R.array.Reg_depts, R.layout.support_simple_spinner_dropdown_item);
        dept2.setAdapter(adps);
        mobno = findViewById(R.id.edittxt_mobStaff);

//intent to get Staff user data from register to this activity
        intent3 = getIntent();
        Bundle extras1 = intent3.getExtras();
        userval = extras1.getString("userval");
        sid = extras1.getString("id");
        name = extras1.getString("name");
        email = extras1.getString("email");
        pass=extras1.getString("pass");
        //Toast.makeText(this, "data sent " + userval + " " + sid + " " + name + " " + email+" " + pass, Toast.LENGTH_LONG).show();
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            //Condition to handle the already logged in user
            Toast.makeText(this, "You are already logged in.", Toast.LENGTH_LONG).show();
            i3=new Intent(RegisterActivity_3.this,StaffHomeAcrivity.class);
        }
    }*/

    private void writedata(final String dpts){
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //String userId=user.getUid();
                            UserData user = new UserData(userval,sid,name,email,dpts,"NA","NA",mnum1);
                            FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).setValue(user)
                                    .addOnCompleteListener(RegisterActivity_3.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(RegisterActivity_3.this,"Congrats!! Registeration Successfull.",Toast.LENGTH_SHORT).show();
                                    FirebaseMessaging.getInstance().subscribeToTopic(dpt2.replace(" ","_"))
                                            /*.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity_3.this, "Topic subscribed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })*/;

                                    startActivity(new Intent(getApplicationContext(),StaffHomeAcrivity.class));
                                    finish();
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity_3.this,"Registeration Not Successfull",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void moveToStaffHome(View view) {
        dpt2 = dept2.getSelectedItem().toString().trim();
        mnum1 = mobno.getText().toString().trim();
        if (TextUtils.isEmpty(mnum1)) {
            mobno.setError("Mobile Number Required");
        }
        else if (TextUtils.getTrimmedLength(mnum1) != 10) {
            mobno.setError("Invalid Mobile Number");
        }
        else {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    final String token=task.getResult().getToken();
                    FirebaseDatabase.getInstance().getReference().child("group_tokens").child(dpt2+"Staff").child(sid).setValue(token)
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
            writedata(dpt2);
        }


        /*extras3 = new Bundle();
        //dpt2=dept2.getSelectedItem().toString();
        // mob=mobno.getText().toString();
        extras3.putString("userval", userval);
        extras3.putString("id", sid);
        extras3.putString("name", name);
        extras3.putString("email", email);
        extras3.putString("dep", dpt2);
        extras3.putString("mno", mnum1);*/

    }


}



