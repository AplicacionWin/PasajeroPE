package com.nikola.user.ui.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hbb20.CountryCodePicker;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularEditView;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Const;
import com.nikola.user.login.AbstractClient;
import com.nikola.user.login.GetAddress;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.ui.activity.OtpActivity;
import com.nikola.user.ui.activity.SignInActivity;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Carlos on 7/5/2017.
 */

public class SignupFragment extends AppCompatActivity {
    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    @BindView(R.id.user_mobile_nuber)
    CustomRegularEditView userMobileNumber;
    @BindView(R.id.redirectLogin)
    CustomRegularTextView redirectLogin;
    Unbinder unbinder;
    SignInActivity activity;
    APIInterface apiInterface;
    PrefUtils prefUtils;

    boolean mobilenumbreCheck = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());

        Spannable wordtoSpan = new SpannableString(getString(R.string.not_here_to_login_signup));
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), 19, 26, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        redirectLogin.setText(wordtoSpan);
    }

    @OnClick({R.id.btn_confirm_phone, R.id.redirectLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm_phone:
                if (validation()) {
                    //getOtp();
                }
                break;
            case R.id.redirectLogin:
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                break;
        }
    }

    protected boolean verifyMobileApp(){
        try {

            String url = Const.ServiceType.VERIFY_MOBILE_APP;
            AbstractClient serv = new AbstractClient();
            String response = serv.web(url, "mobile="+ userMobileNumber.getText().toString());
            System.out.println("response register: " + response);
            JSONObject obj = new JSONObject(response);
            Boolean status = obj.getBoolean("success");

            return status;
        } catch (Exception e) {
            System.out.println("error catch: " + e);
            return false;
        }

    }
    /*protected void verifyMobilXflow() {

        UiUtils.showLoadingDialog(SignupFragment.this);

        new Thread() {
            public void run() {

                ObjectMapper mapper = new ObjectMapper();
                HashMap<String, Object> map = new HashMap<>();
                map.put("phone_number", ccp.getSelectedCountryCodeWithPlus()+ userMobileNumber.getText().toString());
                String response ="";
                try{

                    String json = mapper.writeValueAsString(map);
                    String url = new GetAddress().getUrl(Const.CHECK_PHONENUMBER);
                    Log.d("url: " , url);
                    AbstractClient serv = new AbstractClient();
                    response = serv.webJson(url, json);
                    Log.d("response: " , response);
                    Log.d("json: " , json);

                    if(!response.equalsIgnoreCase(""))
                    {
                        JSONObject obj = new JSONObject(response);
                        JSONObject data = obj.getJSONObject("data");
                        JSONObject post = data.getJSONObject("posts");
                        boolean mobileApp = verifyMobileApp();
                        UiUtils.hideLoadingDialog();
                        if(obj.getString("status").equalsIgnoreCase("200") && mobileApp)
                        {
                            mobilenumbreCheck = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getOtp();
                                }
                            });

                        }
                        else{
                            String error = obj.getString("status").equalsIgnoreCase("200")?getString(R.string.mobileExist):post.getString("Error");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UiUtils.showLongToast(SignupFragment.this,error);
                                }
                            });

                            mobilenumbreCheck = false;
                        }

                    }
                }
                catch (Exception e){

                    e.printStackTrace();
                    mobilenumbreCheck = false;
                }

                finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UiUtils.hideLoadingDialog();
                        }
                    });


                }

            }
        }.start();
    }*/

    private boolean validation() {
        if (userMobileNumber.getText().toString().equalsIgnoreCase("")){
            UiUtils.showShortToast(SignupFragment.this,getString(R.string.empty_mobile_number));
            return false;
        } else if (userMobileNumber.getText().toString().length()>13 ){
            UiUtils.showShortToast(SignupFragment.this,getString(R.string.mobile_number_more_than));
            return false;
        }else if (userMobileNumber.getText().toString().length()<6){
            UiUtils.showShortToast(SignupFragment.this,getString(R.string.mobile_number_less_than));
            return false;
        } else {
            return true;
        }
    }
/*
    protected void getOtp() {
        UiUtils.showLoadingDialog(SignupFragment.this);
        Call<String> call = apiInterface.getOtp(userMobileNumber.getText().toString()
                , ccp.getSelectedCountryCodeWithPlus());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject otpResponse = null;
                try {
                    otpResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (otpResponse.optString(Const.Params.SUCCESS).equals(Const.Params.TRUE)) {
                    JSONObject data = otpResponse.optJSONObject(APIConsts.Params.DATA);
                    String code = data.optString(APIConsts.Params.CODE);
                    if (otpResponse.optString(APIConsts.Params.IS_PREFILL).equalsIgnoreCase("3")) {
                        UiUtils.showLongToast(getApplicationContext(), otpResponse.optString(APIConsts.Params.MESSAGE));
                    }
                    Intent i = new Intent(getApplicationContext(), OtpActivity.class);
                    i.putExtra(Const.Params.PHONE, userMobileNumber.getText().toString());
                    i.putExtra(APIConsts.Params.COUNTRY_CODE, ccp.getSelectedCountryCodeWithPlus());
                    i.putExtra("redirectToProfile", false);
                    i.putExtra("newOtp",false);
                    i.putExtra("code",code);
                    startActivity(i);
                    finish();
                } else {
                    String error = otpResponse.optString(APIConsts.Params.ERROR);
                    AndyUtils.showShortToast(error, SignupFragment.this);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.hideLoadingDialog();
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }
    *
 */
}
