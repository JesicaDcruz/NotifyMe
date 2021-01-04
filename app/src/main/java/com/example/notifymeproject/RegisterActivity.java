package com.example.notifymeproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
Spinner reg_userId_choice;
EditText sid,name,email,pass;
Bundle extras1;
    Intent i;
    String regchoice,a,b,c,d,id,n,em,p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sid=findViewById(R.id.edittxt_id);
        name=findViewById(R.id.edittxt_username);
        email=findViewById(R.id.edittxt_email);
        pass=findViewById(R.id.txt_password);
        reg_userId_choice=findViewById(R.id.spnr_regId );
        ArrayAdapter adapter= ArrayAdapter.createFromResource(this,R.array.register_array,R.layout.support_simple_spinner_dropdown_item);
        reg_userId_choice.setAdapter(adapter);
    }



    private boolean validateInputs(){
        boolean res=true;
        //regchoice=reg_userId_choice.getSelectedItem().toString();
        id=sid.getText().toString().trim();
        n=name.getText().toString().trim();
        em=email.getText().toString().trim();
        p=pass.getText().toString().trim();

        if (id.length() == 0) {
            res=false;
            sid.setError("Student/Staff ID required");
        }
        if (n.length() == 0) {
            res=false;
            name.setError("Student/Staff Full Name required");
        }
        if (em.length() == 0) {
            res=false;
            email.setError("Email required");
        }
        if (p.length() == 0) {
            res=false;
            pass.setError("Password required");
        }
        return res;

    }

    public void moveToReg2Screen(View view) {
        if (validateInputs()){
            extras1=new Bundle();
            regchoice=reg_userId_choice.getSelectedItem().toString();
            a=sid.getText().toString().trim();
            b=name.getText().toString().trim();
            c=email.getText().toString().trim();
            d= pass.getText().toString().trim();
            //String[] userval_reg={regchoice,a,b,c};
            //extras1.putStringArray("uservals",userval_reg);
            extras1.putString("userval",regchoice);
            extras1.putString("id", a);
            extras1.putString("name", b);
            extras1.putString("email", c);
            extras1.putString("pass", d);

            if (regchoice.equals("Student")) {
                i = new Intent(this, RegisterActivity_2.class);
                i.putExtras(extras1);
                startActivity(i);
            }
            else{
                i=new Intent(this,RegisterActivity_3.class);
                i.putExtras(extras1);
                startActivity(i);
            }
        }
        else{
            validateInputs();
        }




    }
}
