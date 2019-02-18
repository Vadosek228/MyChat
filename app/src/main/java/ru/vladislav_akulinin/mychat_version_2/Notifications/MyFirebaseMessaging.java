package ru.vladislav_akulinin.mychat_version_2.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ru.vladislav_akulinin.mychat_version_2.MessageActivity;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String TAG = "No Sender";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sender = remoteMessage.getData().get("sender");
        String user = remoteMessage.getData().get("user");

        //не получать уведомления от одного и тогоже пользователя
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //модификация
//        try {
//        if(firebaseUser != null && sender.equals(firebaseUser.getUid())){
//                sendNotification(remoteMessage);
//            }
        //заменяем, чтобы небыло исключения null
//        if (sender != null && firebaseUser != null && sender.equals(firebaseUser.getUid())) {  //не проходит именно в эмуляторе, на телефоне работает
//            sendNotification(remoteMessage);
//        } else {
//            Log.e(TAG, "Не отправилось!");
//            sendNotification(remoteMessage);
//        }
//        }catch (NullPointerException exc){
////            Toast.makeText(MyFirebaseMessaging.this, "Ошибка принятия", Toast.LENGTH_SHORT).show();
//        }

        if(sender != null && firebaseUser != null && sender.equals(firebaseUser.getUid())){
            if(!currentUser.equals(user)){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    senderOreoNotification(remoteMessage);
                }else {
                    sendNotification(remoteMessage);
                }
            }
        } else {
            Log.e(TAG, "Не отправилось!");
            sendNotification(remoteMessage);
        }


    }

    private void senderOreoNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon);

        int i = 0;
        if(j > 0){
            i = j;
        }

        oreoNotification.getManager().notify(i, builder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if(j > 0){
            i = j;
        }

        noti.notify(i, builder.build());
    }
}
