package com.example.notifymeproject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {

    final private String myserverKey="AAAAXN2U_O4:APA91bFfKwHfZALysYSXt-DAjvS8QuJyh00znYLwtYH1k9RcPCvkC19hww72n5-y-sOqagcQAp6rHwS8iRjCb4lkQSUtnbCXzDxsjOCMgc77uGxeH0KHvFPnNqCqfL8WZ66VwTOQJvIA";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key="+myserverKey;
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    //String NOTIFICATION_TITLE, NOTIFICATION_MESSAGE, TOPIC;
    String title,msg,topic;
    //Context context;

    public NotificationHelper(){ }

    public void generateNotification(Context context, String title, String msg, String topic, JSONArray token){
       JSONArray TOKEN = token;
        String TOPIC = "/topics/"+topic; //topic must match with what the receiver subscribed to
        String NOTIFICATION_TITLE = title;
        String NOTIFICATION_MESSAGE =msg;


        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        JSONObject dataBody = new JSONObject();
        try {
            if(!topic.equals("NA")){
                notifcationBody.put("title", NOTIFICATION_TITLE);
                notifcationBody.put("body", NOTIFICATION_MESSAGE);
                notifcationBody.put("click_action", "ViewNotification");
                dataBody.put("title",NOTIFICATION_TITLE);
                dataBody.put("body",NOTIFICATION_MESSAGE);
                dataBody.put("headers",topic.replace("_"," "));
                dataBody.put("gotoactivity","ViewNotification");
                notification.put("to", TOPIC);
                notification.put("notification", notifcationBody);
                notification.put("data",dataBody);
            }
            else{
                notifcationBody.put("title", NOTIFICATION_TITLE);
                notifcationBody.put("body", NOTIFICATION_MESSAGE);
                notifcationBody.put("click_action", "ChatsActivity");
                dataBody.put("title",NOTIFICATION_TITLE);
                dataBody.put("body",NOTIFICATION_MESSAGE);
                dataBody.put("headers",topic.replace("_"," "));
                dataBody.put("gotoactivity","ChatsActivity");
                notification.put("registration_ids", TOKEN);
                notification.put("notification", notifcationBody);
                notification.put("data",dataBody);
            }

        }
        catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendmyNotification(context,notification);
    }

    public void sendmyNotification(final Context context, JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,FCM_API, notification,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse: " + response.toString());
                Toast.makeText(context, "Sent", Toast.LENGTH_LONG).show();
                /*title.setText("");
                body.setText("");*/
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Try Again", Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }


}
