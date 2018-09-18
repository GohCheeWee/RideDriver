package com.jby.ridedriver.registration.others;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jby.ridedriver.registration.home.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.jby.ridedriver.registration.home.HomeActivity.TAG;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    public static String NotificationBroadCast = "notificationBroadCast";

    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }

    private void sendPushNotification(JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            String matchRideId = data.getString("match_ride_id");
            Intent activityIntent;
            Bundle bundle = new Bundle();

            activityIntent = new Intent(getApplicationContext(), HomeActivity.class);
            bundle.putString("match_ride_id", matchRideId);
            activityIntent.putExtras(bundle);

//            notification
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannelHelper notificationChannelHelper = new NotificationChannelHelper(getApplicationContext());
                NotificationCompat.Builder builder = notificationChannelHelper.getChannel1Notification(title, message, activityIntent);
                notificationChannelHelper.getManager().notify(1, builder.build());

            }else{
                CustomNotificationManager mNotificationManager = new CustomNotificationManager(getApplicationContext());
                mNotificationManager.showSmallNotification(title, message, activityIntent);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void sendBroadCastToActivity(String matchRideId){
        Intent intent = new Intent(NotificationBroadCast);
        intent.putExtra("match_ride_id", matchRideId);
        broadcaster.sendBroadcast(intent);
    }

}
