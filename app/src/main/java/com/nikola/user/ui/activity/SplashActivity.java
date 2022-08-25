package com.nikola.user.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.nikola.user.NewUtilsAndPref.splash.SplashAnimationHelper;
import com.nikola.user.R;
import com.nikola.user.fcm.FCMIntentService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 7/19/2017.
 */

public class SplashActivity extends AppCompatActivity {

    private static final int RC_APP_UPDATE = 100;
    @BindView(R.id.splashAnimationLayout)
    RelativeLayout splashAnimationLayout;
    private SplashAnimationHelper.SplashRouteAnimation splashRouteAnimation;
    AppUpdateManager appUpdateManager;
    InstallStateUpdatedListener listener;
    final long[] VIBRATE_PATTERN    = { 100, 200, 300, 400, 500, 400, 300, 200, 100 };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CreateFCMDefaultChannel();

        setContentView(R.layout.layout_animation);
        ButterKnife.bind(this);
        try {
            checkForAnAppUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            animateToHomeScreen();
            startProgressAnimation();
        } else {
            Intent i = new Intent(getApplicationContext(), GetStartedActivity.class);
            startActivity(i);
        }
        try {
            startService(new Intent(this, FCMIntentService.class));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
/*
    private void CreateFCMDefaultChannel()
    {
        Uri uriBeep = Uri.parse("android.resource://com.winridesharec.passenger/" + R.raw.win);
        String channelId = "Default";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Notificaciones Pasajero", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GRAY);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(uriBeep, audioAttributes);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }*/

    private void CreateFCMDefaultChannel()
    {
        Uri uriBeep = Uri.parse("android.resource://com.winridesharec.driver/" + R.raw.win);
        String channelId = getApplicationContext().getString(R.string.default_notification_channel_id);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Notificaciones Conductor", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GRAY);
            channel.setVibrationPattern(VIBRATE_PATTERN);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(uriBeep, audioAttributes);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public void checkForAnAppUpdate() {
        listener = installState -> {
            try{
                if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        appUpdateManager = AppUpdateManagerFactory.create(SplashActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateManager.registerListener(listener);
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.FLEXIBLE, SplashActivity.this, RC_APP_UPDATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.splashAnimationLayout),
                        getString(R.string.updateAvailable),
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.restartCaps), view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }


    private void startProgressAnimation() {
        this.splashRouteAnimation = new SplashAnimationHelper().createSplashAnimation(this);
        this.splashRouteAnimation.startAnimation(this.splashAnimationLayout);
    }


    private void animateToHomeScreen() {
        AnimatorSet localAnimatorSet1 = new AnimatorSet();
        AnimatorSet localAnimatorSet2 = new AnimatorSet();
        AnimatorSet localAnimatorSet3 = new AnimatorSet();
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        localArrayList1.add(ObjectAnimator.ofFloat(this.splashAnimationLayout, "alpha", new float[]{1.0F, 0.0F}));
        localAnimatorSet2.setDuration(1000);
        localAnimatorSet2.playTogether(localArrayList1);
        localAnimatorSet3.playSequentially(localArrayList2);
        localAnimatorSet3.setDuration(500L);
        localAnimatorSet3.setStartDelay(50L);
        localAnimatorSet1.playSequentially(localAnimatorSet2, localAnimatorSet3);
        localAnimatorSet1.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator paramAnonymousAnimator) {
            }

            public void onAnimationEnd(Animator paramAnonymousAnimator) {
                if (SplashActivity.this.splashRouteAnimation != null) {
                    startActivity(new Intent(getApplicationContext(), GetStartedActivity.class));
                }
            }

            public void onAnimationRepeat(Animator paramAnonymousAnimator) {
            }

            public void onAnimationStart(Animator paramAnonymousAnimator) {
            }
        });
        localAnimatorSet1.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
            }
        }
    }
}
