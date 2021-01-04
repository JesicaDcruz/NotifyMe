package com.example.notifymeproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;


public class ManageNotifications extends AppCompatActivity {

    //private String TAG="MN_TAG";
    EditText title,body;
    Spinner topix,dep,cou,sem;
    ArrayAdapter adp2;
    RadioGroup rnotify,rdepy;
    LinearLayout studentsendto,staffsendto;
    String token,mytopic;
    //RadioButton rdep,rassoc;
   /* final private String myserverKey="AAAAXN2U_O4:APA91bFfKwHfZALysYSXt-DAjvS8QuJyh00znYLwtYH1k9RcPCvkC19hww72n5-y-sOqagcQAp6rHwS8iRjCb4lkQSUtnbCXzDxsjOCMgc77uGxeH0KHvFPnNqCqfL8WZ66VwTOQJvIA";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key="+myserverKey;
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

   String NOTIFICATION_TITLE, NOTIFICATION_MESSAGE, TOPIC;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_notifications);

        Toolbar myToolbar =  findViewById(R.id.tb_mn);
        myToolbar.setTitle("Manage Notifications");
        myToolbar.setTitleTextAppearance(this, R.style.CustomText);
        setSupportActionBar(myToolbar);

        title=findViewById(R.id.etxt_title);
        body=findViewById(R.id.mtxtbody);
        topix=findViewById(R.id.spnr_topic);
        dep=findViewById(R.id.spnr_d);
        ArrayAdapter d= ArrayAdapter.createFromResource(ManageNotifications.this,R.array.Reg_depts,R.layout.support_simple_spinner_dropdown_item);
        dep.setAdapter(d);
        cou=findViewById(R.id.spnr_c);
        ArrayAdapter c= ArrayAdapter.createFromResource(ManageNotifications.this,R.array.Reg_course,R.layout.support_simple_spinner_dropdown_item);
        cou.setAdapter(c);
        sem=findViewById(R.id.spnr_s);
        ArrayAdapter s= ArrayAdapter.createFromResource(ManageNotifications.this,R.array.Reg_sems,R.layout.support_simple_spinner_dropdown_item);
        sem.setAdapter(s);
        studentsendto=findViewById(R.id.ll_student_send_to);
        staffsendto=findViewById(R.id.ll_staff_send_to);
        rdepy=findViewById(R.id.radiogrp_depy);
       rnotify=findViewById(R.id.radiogrp_notify);
       rdepy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup radioGroup, int i) {
               int checkedRadioButtonId = rdepy.getCheckedRadioButtonId();

               if (checkedRadioButtonId==R.id.radio_student){
                   staffsendto.setVisibility(View.GONE);
                   studentsendto.setVisibility(View.VISIBLE);
               }
               else {
                   staffsendto.setVisibility(View.VISIBLE);
                   studentsendto.setVisibility(View.GONE);
                   rnotify.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                       @Override
                       public void onCheckedChanged(RadioGroup radioGroup, int i) {
                           int checkedRadioButton = rnotify.getCheckedRadioButtonId();

                           if(checkedRadioButton==R.id.radio_dept){
                               adp2= ArrayAdapter.createFromResource(ManageNotifications.this,R.array.Reg_depts,R.layout.support_simple_spinner_dropdown_item);
                               topix.setAdapter(adp2);
                           }
                           else{
                               adp2= ArrayAdapter.createFromResource(ManageNotifications.this,R.array.Reg_association,R.layout.support_simple_spinner_dropdown_item);
                               topix.setAdapter(adp2);
                           }
                       }
                   });
               }
           }
       });


      /* FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                         token = task.getResult().getToken();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        String msg ="FCM token generated  "+token;
                                Log.d(TAG, msg);
                        Toast.makeText(ManageNotifications.this, msg+token, Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    public void sendNotification(View view){
        int checkedRadioButton = rdepy.getCheckedRadioButtonId();
        if (checkedRadioButton==R.id.radio_student){
            String deps=dep.getSelectedItem().toString();
            String cour=cou.getSelectedItem().toString();
            String seems=sem.getSelectedItem().toString();
             mytopic=deps+"_"+cour+"_"+seems;
        }
        else{
             mytopic=topix.getSelectedItem().toString();
        }

        String new_topic=mytopic.replace(" ","_");
        String mytitle=title.getText().toString();
        String message=body.getText().toString();

        JSONArray token = new JSONArray();
        token.put(0);
        NotificationHelper myhelper=new NotificationHelper();
        myhelper.generateNotification(getApplicationContext(),mytitle,message,new_topic,token);

        title.setText("");
        body.setText("");

        /*TOPIC = "/topics/"+new_topic; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = title.getText().toString();
        NOTIFICATION_MESSAGE = body.getText().toString();

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        JSONObject dataBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("body", NOTIFICATION_MESSAGE);
            notifcationBody.put("click_action", "ViewNotification");
            dataBody.put("title",NOTIFICATION_TITLE);
            dataBody.put("body",NOTIFICATION_MESSAGE);
            dataBody.put("headers",mytopic);
            notification.put("to", TOPIC);
            notification.put("notification", notifcationBody);
            notification.put("data",dataBody);
        }
        catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendmyNotification(notification);*/
    }

    /*private void sendmyNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,FCM_API, notification,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        title.setText("");
                        body.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ManageNotifications.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work"+error.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }*/

    public void viewNotification(View view) {
        startActivity(new Intent(this, ViewNotification.class));
            //Intent intent=new Inten
    }
}
