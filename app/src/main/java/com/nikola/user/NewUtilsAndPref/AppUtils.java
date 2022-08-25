package com.nikola.user.NewUtilsAndPref;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.core.content.PermissionChecker;

import com.nikola.user.R;
import com.nikola.user.ui.activity.MainActivity;

import com.sensorberg.permissionbitte.PermissionBitte;


import java.security.acl.Permission;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AppUtils {
    private AppUtils(){

    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = new Date().getTime();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "justo ahora";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "hace un minuto";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " hace unos minutos";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "hace una hora";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "ayer";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static RequestBody getPartFor(String stuff) {
        return RequestBody.create(MediaType.parse("text/plain"), stuff);
    }
}
