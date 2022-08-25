package com.nikola.user.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;

import static android.content.ContentValues.TAG;

public class InstanceIdServices  extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        saveDeviceToken(token);
    }

    private void saveDeviceToken(String token) {
        PrefUtils.getInstance(this).setValue(PrefKeys.FCM_TOKEN, token);
    }

}
