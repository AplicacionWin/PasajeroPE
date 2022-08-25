package com.nikola.user.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
//import com.nikola.user.NewUtilsAndPref.AppUtils;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularEditView;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefHelper;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.ui.Fragment.ForgotpassFragment;
import com.nikola.user.ui.Fragment.RecoverPassFragment;
import com.nikola.user.ui.Fragment.SignupFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nikola.user.network.ApiManager.APIConsts.Params;
import static com.nikola.user.network.ApiManager.APIConsts.Params.DATA;

/**
 * Created by user on 1/4/2017.
 */

public class SignInActivity extends AppCompatActivity {
    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.email)
    CustomRegularEditView email;
    @BindView(R.id.password)
    CustomRegularEditView password;
    @BindView(R.id.forgotPassword)
    CustomRegularTextView forgotPassword;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.socialLogin)
    CustomRegularTextView socialLogin;
    @BindView(R.id.signUp)
    CustomRegularTextView signUp;
    @BindView(R.id.inputLayoutPassword)
    TextInputLayout inputLayoutPassword;
    @BindView(R.id.tv_need_help)
    TextView needHelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        inputLayoutPassword.setHintAnimationEnabled(false);
        inputLayoutPassword.setHint("");
        password.setHint(getString(R.string.password));

        Spannable wordtoSpan = new SpannableString(getString(R.string.do_you_have_an_account_signup));
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), 23, 33, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUp.setText(wordtoSpan);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean validateFields() {

        if (password.getText().toString().length() < 6) {
            UiUtils.showShortToast(this, getString(R.string.minimum_six_characters));
            return false;
        }
        return true;
    }

        protected void doLoginUser() {
            UiUtils.showLoadingDialog(SignInActivity.this);
            Call<String> call = apiInterface.doMannualLogin(email.getText().toString()
                    , password.getText().toString()
                    , APIConsts.Constants.ANDROID
                    , APIConsts.Constants.MANUAL_LOGIN
                    , prefUtils.getStringValue(PrefKeys.FCM_TOKEN, "")
                    , TimeZone.getDefault().getID());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    UiUtils.hideLoadingDialog();
                    JSONObject loginResponse = null;
                    try {
                        loginResponse = new JSONObject(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loginResponse != null) {
                        if (loginResponse.optString(APIConsts.Constants.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                            JSONObject data = loginResponse.optJSONObject(DATA);
                            try {
                                //String username = data.getString(PrefKeys.USERNAME);
                                //String userId = data.getString(PrefKeys.USERID);
                                loginUserInDevice(data, "username", APIConsts.Constants.MANUAL_LOGIN,"userId");
                                prefUtils.setValue(PrefKeys.IS_SOCIAL_LOGIN, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            UiUtils.showShortToast(getApplicationContext(), loginResponse.optString(Params.ERROR));
                        }
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
    /*
    protected void doLoginUser() {
        UiUtils.showLoadingDialog(SignInActivity.this);

        new Thread() {
            public void run() {
                Respuesta respuesta = new AuthUser().authUser(email.getText().toString(),
                        password.getText().toString(), prefUtils.getStringValue(PrefKeys.FCM_TOKEN, ""));

                if (respuesta.getCodigo()!=null && "0".equalsIgnoreCase(respuesta.getCodigo())) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginUserInDevice(respuesta.getMensaje(), respuesta.getUsername(), APIConsts.Constants.MANUAL_LOGIN,respuesta.getUserId());
                            prefUtils.setValue(PrefKeys.IS_SOCIAL_LOGIN, false);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UiUtils.showShortToast(getApplicationContext(), getResources().getString(R.string.something_went_wrong));
                        }
                    });

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UiUtils.hideLoadingDialog();
                    }
                });

            }
        }.start();
    }*/

    public void loginUserInDevice(JSONObject data, String username, String loginBy, String userId) {
        PrefHelper.setUserLoggedIn(this
                ,userId
                ,username
                ,data.optInt(Params.USER_ID)
                , data.optString(Params.TOKEN)
                , loginBy
                , data.optString(Params.EMAIL)
                , data.optString(Params.NAME)
                , data.optString(Params.FIRSTNAME)
                , data.optString(Params.LAST_NAME)
                , data.optString(Params.PICTURE)
                , data.optString(Params.PAYMENT_MODE)
                , data.optString(Params.TIMEZONE)
                , data.optString(Params.MOBILE)
                , data.optString(Params.GENDER)
                , data.optString(Params.REFERRAL_CODE)
                , data.optString(Params.REFERRAL_BONUS));
        prefUtils.setValue(Params.PAYMENT_MODE, data.optString(Params.PAYMENT_MODE));
        Intent toHome = new Intent(getApplicationContext(), MainActivity.class);
        toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toHome);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick({R.id.Nuevo, R.id.login, R.id.socialLogin, R.id.forgotPassword, R.id.tv_need_help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Nuevo:
               Intent forgotIntent1 = new Intent(getApplicationContext(),SignUpNextActivity.class);
                startActivity(forgotIntent1);
                break;
            case R.id.login:
                if (validateFields()) {
                    doLoginUser();
                }
                break;
            case R.id.socialLogin:
                startActivity(new Intent(getApplicationContext(), SocialLoginActivity.class));
                break;
            case R.id.forgotPassword:
                Intent forgotIntent = new Intent(getApplicationContext(), ForgotpassFragment.class);
                startActivity(forgotIntent);
                break;
            case R.id.tv_need_help:
                Uri uriUrl = Uri.parse(Const.NEED_HELP);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
        }
    }
}
