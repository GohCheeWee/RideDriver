package com.jby.ridedriver.registration.others;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.jby.ridedriver.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class CustomNotificationManager {

    private static final int ID_BIG_NOTIFICATION = 234;
    private static final int ID_SMALL_NOTIFICATION = 235;
    long[] pattern = {500,500,500,500,500};

   private Context mCtx;

    CustomNotificationManager(Context mCtx) {
       this.mCtx = mCtx;
   }

   public void showBigNotification(String title, String message, String url, Intent intent) {
       PendingIntent resultPendingIntent =
               PendingIntent.getActivity(
                       mCtx,
                       ID_BIG_NOTIFICATION,
                       intent,
                       PendingIntent.FLAG_UPDATE_CURRENT
               );
       NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
       bigPictureStyle.setBigContentTitle(title);
       bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
       bigPictureStyle.bigPicture(getBitmapFromURL(url));
       NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
       Notification notification;
       notification = mBuilder
               .setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
               .setAutoCancel(true)
               .setContentIntent(resultPendingIntent)
               .setContentTitle(title)
               .setStyle(bigPictureStyle)
               .setSmallIcon(R.mipmap.ic_launcher)
               .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
               .setContentText(message)
               .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
               .setShowWhen(true)
               .setPriority(Notification.PRIORITY_MAX)
               .setVibrate(pattern)
               .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
               .build();

       notification.flags |= Notification.FLAG_AUTO_CANCEL;

       NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
       notificationManager.notify(ID_BIG_NOTIFICATION, notification);
   }

   public void showSmallNotification(String title, String message, Intent intent) {
       PendingIntent resultPendingIntent =
               PendingIntent.getActivity(
                       mCtx,
                       ID_SMALL_NOTIFICATION,
                       intent,
                       PendingIntent.FLAG_UPDATE_CURRENT
               );


       NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
       Notification notification;
       notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
               .setAutoCancel(true)
               .setContentIntent(resultPendingIntent)
               .setContentTitle(title)
               .setSmallIcon(R.mipmap.ic_launcher)
               .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
               .setContentText(message)
               .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
               .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
               .setVibrate(pattern)
               .build();

       notification.flags |= Notification.FLAG_AUTO_CANCEL;

       NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
       notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
   }

   private Bitmap getBitmapFromURL(String strURL) {
       try {
           URL url = new URL(strURL);
           HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           connection.setDoInput(true);
           connection.connect();
           InputStream input = connection.getInputStream();
           return BitmapFactory.decodeStream(input);
       } catch (IOException e) {
           e.printStackTrace();
           return null;
       }
   }
}
