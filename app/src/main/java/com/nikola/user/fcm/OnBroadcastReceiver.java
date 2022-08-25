package com.nikola.user.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.nikola.user.ui.activity.NoInternetActivity;

import static com.nikola.user.ui.activity.NoInternetActivity.isInternetActivityCalled;

/**
 * Created by user on 1/2/2017.
 */

public class OnBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("mahi","on kill app received");
        Intent i = new Intent("com.doctor.ghealth.GCMhandlers.FCMRegisterHandler");
        i.setClass(context, FCMRegisterHandler.class);
        context.startService(i);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        } else {
            if(!isInternetActivityCalled) {
                Intent noInternetActivity = new Intent(context, NoInternetActivity.class);
                noInternetActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(noInternetActivity);
            }
        }
    }
}