package com.nikola.user.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoInternetActivity extends AppCompatActivity {

    @BindView(R.id.close)
    CustomRegularTextView close;
    public static boolean isInternetActivityCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        ButterKnife.bind(this);
        isInternetActivityCalled = true;
        close.setOnClickListener(view -> {
            finish();
            System.exit(0);
        });
    }
}
