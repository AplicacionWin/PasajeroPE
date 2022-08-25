package com.nikola.user.network.ApiManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.R;

public class NetworkUtils {
    private NetworkUtils() {

    }

    public static boolean isNetworkConnected(Context context) {
        if (context == null) return false;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    @SuppressLint("HardwareIds")
    public static String getDeviceToken(Context context) {
        if (context == null) return "";
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static void onApiError(Context context) {
        if (context == null) return;
        UiUtils.hideLoadingDialog();
        UiUtils.showShortToast(context, context.getString(R.string.something_went_wrong));
    }
}
