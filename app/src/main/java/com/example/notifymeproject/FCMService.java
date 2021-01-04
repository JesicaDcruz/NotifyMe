package com.example.notifymeproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "FCM__TAG";
    public  static int NOTIFICATION_ID=0;
//    SQLiteService mydb;
Intent intent;
    @Override
    public void onNewToken(@NonNull String s) {
        String mytoken= s;
        Log.d(TAG, "Refreshed token: " + mytoken);


    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String NOTIFICATION_CHANNEL_ID= getString(R.string.notifyme_notifcations);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        /*String title= remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");*/


            if (remoteMessage.getNotification() != null) {
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
                    NotificationChannel fcmchannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "FCM_CHANNEL", NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(fcmchannel);
                }
                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                String gotoactivity=remoteMessage.getData().get("gotoactivity");
               // Log.d(TAG, "title: " + title + "  body:  " + body);

                /*String header=  remoteMessage.getData().get("headers");
                String titles= remoteMessage.getData().get("title");
                String msg= remoteMessage.getData().get("body");
                Log.d(TAG, "title: " + titles + "  body:  " + msg+ "header: "+header);*/
                Bundle extras=new Bundle();
                extras.putString("headers",remoteMessage.getData().get("headers"));
                extras.putString("title",remoteMessage.getData().get("title"));
                extras.putString("body",remoteMessage.getData().get("body"));

                if(gotoactivity.equals("ViewNotification")){
                    intent =new Intent(this,ViewNotification.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(extras);
                }
                else{
                    intent =new Intent(this,ChatsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(extras);
                }



                PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT,extras);

                Uri notifysound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification nb =new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.notify_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.notify_logo))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(notifysound)
                        .setContentIntent(pendingIntent)
                        .build();
                notificationManager.notify(NOTIFICATION_ID++,nb);
                //generateMessage(title, body);
            }
    }




    /*private void generateMessage(String title, String body) {


        Intent intent =new Intent(this,ViewNotification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        *//*TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ViewNotification.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_ONE_SHOT);*//*
       // PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);


       *//* if(NOTIFICATION_ID>1073741824){
            NOTIFICATION_ID=0;
        }*//*


       // notificationManager.notify(NOTIFICATION_ID++,nb.build());
        //startForeground(NOTIFICATION_ID++,nb.build());
    }*/

}
