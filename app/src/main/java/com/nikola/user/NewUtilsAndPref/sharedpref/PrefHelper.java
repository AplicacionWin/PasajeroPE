package com.nikola.user.NewUtilsAndPref.sharedpref;

import android.content.Context;

import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.EMAIL_NOTIFICATIONS;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.FIRST_NAME;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.GENDER;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.IS_LOGGED_IN;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.LAST_NAME;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.LOGIN_TYPE;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.PAYMENT_MODE;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.PUSH_NOTIFICATIONS;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.REFERRAL_BONUS;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.REFERRAL_CODE;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.SESSION_TOKEN;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.TIMEZONE;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.USERNAME;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.USER_ABOUT;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.USER_EMAIL;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.USER_ID;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.USERID;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.USER_MOBILE;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.USER_NAME;
import static com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys.USER_PICTURE;

public class PrefHelper {

    public static void setUserLoggedIn(Context context, String userid, String username, int id, String token, String loginType, String email, String name, String firstName, String lastName,  String picture,
                                       String paymentMode, String timeZone, String mobile, String gender , String referralCode , String referralBonus) {
        PrefUtils preferences = PrefUtils.getInstance(context);
        preferences.setValue(USERID,userid);
        preferences.setValue(USERNAME,username);
        preferences.setValue(IS_LOGGED_IN, true);
        preferences.setValue(USER_ID, id);
        preferences.setValue(SESSION_TOKEN, token);
        preferences.setValue(LOGIN_TYPE, loginType);
        preferences.setValue(USER_EMAIL, email);
        preferences.setValue(USER_NAME, name);
        preferences.setValue(FIRST_NAME, firstName);
        preferences.setValue(LAST_NAME, lastName);
        preferences.setValue(USER_PICTURE, picture);
        preferences.setValue(PAYMENT_MODE, paymentMode);
        preferences.setValue(TIMEZONE, timeZone);
        preferences.setValue(USER_MOBILE, mobile);
        preferences.setValue(GENDER, gender);
        preferences.setValue(REFERRAL_CODE, referralCode);
        preferences.setValue(REFERRAL_BONUS, referralBonus);
    }

    public static void setUserLoggedIn(Context context,  int id, String token, String loginType, String email, String name, String firstName, String lastName,  String picture,
                                       String paymentMode, String timeZone, String mobile, String gender , String referralCode , String referralBonus) {
        PrefUtils preferences = PrefUtils.getInstance(context);
        preferences.setValue(IS_LOGGED_IN, true);
        preferences.setValue(USER_ID, id);
        preferences.setValue(SESSION_TOKEN, token);
        preferences.setValue(LOGIN_TYPE, loginType);
        preferences.setValue(USER_EMAIL, email);
        preferences.setValue(USER_NAME, name);
        preferences.setValue(FIRST_NAME, firstName);
        preferences.setValue(LAST_NAME, lastName);
        preferences.setValue(USER_PICTURE, picture);
        preferences.setValue(PAYMENT_MODE, paymentMode);
        preferences.setValue(TIMEZONE, timeZone);
        preferences.setValue(USER_MOBILE, mobile);
        preferences.setValue(GENDER, gender);
        preferences.setValue(REFERRAL_CODE, referralCode);
        preferences.setValue(REFERRAL_BONUS, referralBonus);
    }

    public static void setUserLoggedOut(Context context) {
        PrefUtils preferences = PrefUtils.getInstance(context);
        preferences.removeKey(USERID);
        preferences.removeKey(IS_LOGGED_IN);
        preferences.removeKey(USER_ID);
        preferences.removeKey(SESSION_TOKEN);
        preferences.removeKey(LOGIN_TYPE);
        preferences.removeKey(USER_EMAIL);
        preferences.removeKey(USER_NAME);
        preferences.removeKey(USER_ABOUT);
        preferences.removeKey(USER_PICTURE);
        preferences.removeKey(PUSH_NOTIFICATIONS);
        preferences.removeKey(EMAIL_NOTIFICATIONS);
    }
}
