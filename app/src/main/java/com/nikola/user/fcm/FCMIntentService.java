package com.nikola.user.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.ui.activity.ChatActivity;
import com.nikola.user.ui.activity.HistoryActivity;
import com.nikola.user.ui.activity.MainActivity;

import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * Created by user on 6/29/2015.
 */

public class FCMIntentService extends FirebaseMessagingService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private String date = "";
    private Map data;
    String from;


    @Override
    public void onMessageReceived(RemoteMessage message) {
        try {
            Log.d("msg", "onMessageReceived: " + message.getData().get("message"));
            Log.e("data", "msg" + message.getData());
            Log.e("data", "msg" + message.getNotification().getBody());
            for (Map.Entry<String, String> entry : message.getData().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
            }
            Intent intent = null;
            String requestId = "", type = "", hostId = "";
            if (message.getData().get("request_id") != null)
                requestId = message.getData().get("request_id");
            if (message.getData().get("redirection_type") != null)
                type = message.getData().get("redirection_type");
            Log.e("Type ", type);
            switch (type) {
                case APIConsts.PushNotificationStatus.PUSH_NOTIFICATION_REDIRECT_HOME:
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    break;
                case APIConsts.PushNotificationStatus.PUSH_NOTIFICATION_REDIRECT_CHAT:
                    intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra(APIConsts.Params.PROVIDER_ID, "");
                    intent.putExtra(APIConsts.Params.USER_PICTURE, "");
                    intent.putExtra(APIConsts.Params.REQUEST_ID, requestId);
                    break;
                case APIConsts.PushNotificationStatus.PUSH_NOTIFICATION_REDIRECT_REQUEST_ONGOING:
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    break;
                case APIConsts.PushNotificationStatus.PUSH_NOTIFICATION_REDIRECT_REQUEST_VIEW:
                    intent = new Intent(getApplicationContext(), HistoryActivity.class);
                    intent.putExtra("isHistory", true);
                    break;
                case APIConsts.PushNotificationStatus.PUSH_NOTIFICATION_REDIRECT_REQUESTS:
                    intent = new Intent(getApplicationContext(), HistoryActivity.class);
                    intent.putExtra("isHistory", true);
                    break;
            }

            Uri uriBeep = Uri.parse("android.resource://com.winrideshareec.passenger/" + R.raw.win);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String channelId = "Default";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Notificaciones Pasajero", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.setLightColor(Color.GRAY);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                channel.setSound(uriBeep, audioAttributes);
                channel.enableVibration(true);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }

            if (intent == null)
            {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(uriBeep)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(message.getNotification().getBody())
                    .setAutoCancel(true)
                    .setPriority(1)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent);

            if (manager != null) {
                manager.notify(0, builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        saveDeviceToken(token);
    }

    private void saveDeviceToken(String token) {
        PrefUtils.getInstance(this).setValue(PrefKeys.FCM_TOKEN, token);
    }
}
