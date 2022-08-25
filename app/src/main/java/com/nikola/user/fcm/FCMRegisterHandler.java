package com.nikola.user.fcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.Utils.PreferenceHelper;
;

/**
 * Created by user on 6/29/2015.
 */
public class FCMRegisterHandler extends FirebaseMessagingService {

    private Activity activity;
    public static final String EXTRA_MESSAGE = "mensaje";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    PreferenceHelper prefs;
    PrefUtils prefUtils = PrefUtils.getInstance(getApplicationContext());

    private String regid;
    private String TAG = "pavan";

    public FCMRegisterHandler(Activity activity, BroadcastReceiver mHandleMessageReceiver) {
        prefs = new PreferenceHelper(activity);
        try {
            this.activity = activity;
            if (checkPlayServices()) {
                regid = getRegistrationId(activity);
                prefs.putDeviceToken(regid);
                prefUtils.setValue(PrefKeys.FCM_TOKEN, regid);
                if (regid.isEmpty()) {
                    registerInBackground();
                }
            } else {
                Log.d(TAG, "No valid Google Play Services APK found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "Este dispositivo no es compatible.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("No se pudo obtener el nombre del paquete: " + e);
        }
    }


    private String getRegistrationId(Context context) {
        String registrationId = prefs.getRegistrationID();
        if (registrationId.isEmpty()) {
            Log.d(TAG, "No se ha encontrado el registro.");
            return "";
        }
        int registeredVersion = prefs.getAppVersion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "Se ha cambiado la versión de la aplicación.");
            return "";
        }
        return registrationId;
    }


    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                msg = "Device registered, registration ID=" + regid;
                storeRegistrationId(activity, regid);
                return msg;
            }


            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);

    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.d(TAG, "Saving regId on app version " + appVersion);
        Log.d(TAG, "RegID " + regId);
        prefs.putAppVersion(appVersion);
        prefs.putRegisterationID(regId);
        prefs.putDeviceToken(regId);
        prefUtils.setValue(PrefKeys.FCM_TOKEN, regId);
    }


}
