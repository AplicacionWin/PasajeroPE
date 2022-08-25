package com.nikola.user.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.Utils.Const;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.ui.Adapter.SpinnerAdapter;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by user on 1/3/2017.
 */

public class GetStartedActivity extends AppCompatActivity {
    @BindView(R.id.sp_country_reg)
    Spinner spCountryReg;
    @BindView(R.id.welcome_btn)
    CustomRegularTextView welcomeBtn;
    private SpinnerAdapter adapter_language;
    PrefUtils prefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        if (prefUtils.getBooleanValue(PrefKeys.IS_LOGGED_IN, false)) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
            return;
        }

        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        setUpLocale();
    }


    public void setUpLocale() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        if (!TextUtils.isEmpty(new PreferenceHelper(this).getLanguage())) {
            Locale myLocale = null;
            switch (new PreferenceHelper(this).getLanguage()) {
                case "":
                    myLocale = new Locale("");
                    break;
                case "es":
                    myLocale = new Locale("es");
                    break;
                case "en":
                    myLocale = new Locale("en");
                    break;
            }
            Locale.setDefault(myLocale);
            Configuration config = new Configuration();
            config.locale = myLocale;
            this.getResources().updateConfiguration(config,
                    this.getResources().getDisplayMetrics());
        }

        String[] lst_currency = getResources().getStringArray(R.array.language);
        Integer[] currency_imageArray = {null, R.drawable.ecuador_flag, R.drawable.ic_united_states};

        adapter_language = new SpinnerAdapter(this, R.layout.spinner_value_layout, lst_currency, currency_imageArray);
        spCountryReg.setAdapter(adapter_language);
        if (!TextUtils.isEmpty(new PreferenceHelper(this).getLanguage())) {

            switch (new PreferenceHelper(this).getLanguage()) {
                case "":
                    spCountryReg.setSelection(0, false);
                    break;
                case "es":
                    spCountryReg.setSelection(1, false);

                    break;
                case "en":
                    spCountryReg.setSelection(2, false);
                    break;

            }

        }
        spCountryReg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        new PreferenceHelper(GetStartedActivity.this).putLanguage("");
                        break;
                    case 1:
                        new PreferenceHelper(GetStartedActivity.this).putLanguage("es");
                        setLocale("es");
                        break;
                    case 2:
                        new PreferenceHelper(GetStartedActivity.this).putLanguage("en");
                        setLocale("en");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
        Intent refresh = new Intent(this, GetStartedActivity.class);
        startActivity(refresh);
        this.overridePendingTransition(0, 0);
    }

    @OnClick(R.id.welcome_btn)
    public void onViewClicked() {
        Intent i = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(i);
    }

    public void needHelp(View v)
    {
        Uri uriUrl = Uri.parse(Const.NEED_HELP);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}
