package com.example.notifymeproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    String uid, email, pass, token, TAG = "FCM_TOKEN",dep;
    Intent ilog;
    EditText uemail, password;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    Boolean user_state;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private String MyPREFERENCES = "statePrefernce";
    String DEVICE_STATUS = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        uemail = findViewById(R.id.edittxt_uname);
        password = findViewById(R.id.edittxt_pwd);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                DEVICE_STATUS = "Device Connected";
                checkUserState();
            }
        } else {
            DEVICE_STATUS = "Device Not Connected.\n Secure an Internet Connection and login.";
            Boolean app_auth_state=sharedpreferences.getBoolean("userstate",true);
            String type= sharedpreferences.getString("user_type","");
            if (app_auth_state){
                if(type.equals("staff")){
                    startActivity(new Intent(LoginActivity.this, StaffHomeAcrivity.class));
                    //Toast.makeText(LoginActivity.this, "You're Logged in as Staff", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    //Toast.makeText(LoginActivity.this, "You're Logged in as Staff", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else{
                Toast.makeText(LoginActivity.this, DEVICE_STATUS, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void loginUser(View view) {
        email=uemail.getText().toString().trim();
        pass=password.getText().toString().trim();

        if(email.equals("") || pass.equals("")){
            if(email.equals("")){
                Toast.makeText(this,"EMAIL ID REQUIRED",Toast.LENGTH_SHORT).show();
            }
            if(pass.equals("")){
                Toast.makeText(this,"PASSWORD REQUIRED",Toast.LENGTH_SHORT).show();
            }
            if (email.equals("") && pass.equals("")){
                Toast.makeText(this,"EMAIL & PASSWORD REQUIRED",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        onAuthSuccess(task.getResult().getUser());
                        //Toast.makeText(signinActivity.this, "Successfully Signed In", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        user_state=false;
                        editor.putBoolean("userstate",user_state);
                        editor.commit();
                        Toast.makeText(LoginActivity.this, "LOGIN FAILED. EMAIL AND/OR PASSWORD INVALID", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void onAuthSuccess(FirebaseUser user) {
        if(user!=null){
            ref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String mdep = dataSnapshot.child("udep").getValue(String.class);
                    String value = dataSnapshot.child("usertype").getValue(String.class);
                    //generateAndStoreToken(mdep);
                    FirebaseMessaging.getInstance().subscribeToTopic(mdep.replace(" ","_")) /*.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Topic subscribed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })*/;
                    //for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //Toast.makeText(LoginActivity.this, value, Toast.LENGTH_SHORT).show();
                    if(value.equals("Staff")) {
                        user_state=true;
                        // editor = sharedpreferences.edit();
                        editor.putBoolean("userstate",user_state);
                        editor.putString("user_type","staff");
                        editor.commit();
                        //String jason = (String) snapshot.getValue();
                        //Toast.makeText(signinActivity.this, jason, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, StaffHomeAcrivity.class));
                        //Toast.makeText(LoginActivity.this, "You're Logged in as Staff", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        user_state=true;
                        // editor = sharedpreferences.edit();
                        editor.putBoolean("userstate",user_state);
                        editor.putString("user_type","student");
                        editor.commit();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        //Toast.makeText(LoginActivity.this, "You're Logged in as Student", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void reset(View view) {
        AlertDialog.Builder mydialog=new AlertDialog.Builder(LoginActivity.this);
        mydialog.setTitle("Reset Password");
        //mydialog=new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.resetpassword, null);
        final EditText edtEmail= dialogView.findViewById(R.id.edittxt_resetpass_email);
        Button sendlink = dialogView.findViewById(R.id.btn_sendlink);
        sendlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_reset= edtEmail.getText().toString().trim();
                if (!edtEmail.equals("")){
                    mAuth.sendPasswordResetEmail(email_reset)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this, "Reset password link has been emailed to you.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Some error occured. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        mydialog.setView(dialogView);
        mydialog.show();

    }

    public void RegisterUser(View view) {
        Intent i=new Intent(this,RegisterActivity.class);
        startActivity(i);
        finish();
    }

    public void checkUserState() {
        if (mAuth.getCurrentUser() != null) {
            //Condition to handle the already logged in user
            uid = mAuth.getCurrentUser().getUid();
            Toast.makeText(this, "You are already logged in.\nRedirecting to HOME page", Toast.LENGTH_SHORT).show();
            //ilog=new Intent(RegisterActivity_3.this,StaffHomeAcrivity.class);
            ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String mdep = dataSnapshot.child("udep").getValue(String.class);
                    String value = dataSnapshot.child("usertype").getValue(String.class);
                    //for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //Toast.makeText(LoginActivity.this, value, Toast.LENGTH_SHORT).show();
                    if (value.equals("Staff")) {
                        //String jason = (String) snapshot.getValue();
                        //Toast.makeText(signinActivity.this, jason, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, StaffHomeAcrivity.class));
                        //Toast.makeText(LoginActivity.this, "You're Logged in as Staff", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        //Toast.makeText(LoginActivity.this, "You're Logged in as Student", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


}
