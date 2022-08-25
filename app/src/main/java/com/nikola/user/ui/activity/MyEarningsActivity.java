package com.nikola.user.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nikola.user.NewUtilsAndPref.CustomText.CustomLightTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mahesh on 3/1/2017.
 */

public class MyEarningsActivity extends AppCompatActivity {

    @BindView(R.id.my_earnings_back)
    ImageButton myEarningsBack;

    @BindView(R.id.earningsAmtOV)
    CustomLightTextView earningsAmtOV;

    APIInterface apiInterface;
    PrefUtils prefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.myearnings_layout);
        ButterKnife.bind(this);
        myEarningsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        getEarningsOV();

    }

    private void getEarningsOV() {

        Call<String> call = apiInterface.getEarningsData(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {
                    try {
                        JSONObject earningsObj = new JSONObject(response.body());
                        if (earningsObj.optString(Const.Params.SUCCESS).equals(Const.Params.TRUE)) {
                            JSONObject data = earningsObj.getJSONObject(Const.Params.DATA);
                            String code = data.getString(Const.Params.STATUS);
                            if(code.equalsIgnoreCase("200"))
                            {
                                JSONObject data1 = data.getJSONObject(Const.Params.DATA);
                                JSONObject posts = data1.getJSONObject(Const.Params.POSTS);
                                JSONArray currencies = posts.getJSONArray(Const.Params.CURRENCIES);
                                earningsAmtOV.setText(currencies.getJSONObject(0).optString(Const.Params.AMOUNT_FORMATTED));
                            }
                            else
                            {
                                earningsAmtOV.setText("$0.00");
                            }
                        }
                        else
                        {
                            earningsAmtOV.setText("$0.00");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        earningsAmtOV.setText("$0.00");
                    }
                }
                else
                {
                    earningsAmtOV.setText("$0.00");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });

    }
}
