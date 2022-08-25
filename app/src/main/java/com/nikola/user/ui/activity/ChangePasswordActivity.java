package com.nikola.user.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

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

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.currentPassword)
    EditText currentPassword;
    @BindView(R.id.newPassword)
    EditText newPassword;
    @BindView(R.id.newPasswordConfirm)
    EditText newPasswordConfirm;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        apiInterface = APIClient.getClient().create(APIInterface.class);
        toolbar.setTitle(getString(R.string.change_password_text));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @OnClick(R.id.changePasswordButton)
    protected void changePasswordClicked() {
        if (validateFields()) {
            changePasswordApp(currentPassword.getText().toString()
                    , newPassword.getText().toString()
                    , newPasswordConfirm.getText().toString());
            //changePassword(currentPassword.getText().toString()
                    //, newPassword.getText().toString()
                    //, newPasswordConfirm.getText().toString());
        }
    }

    private boolean validateFields() {
        String curPass = currentPassword.getText().toString();
        String newPass = newPassword.getText().toString();
        String newPassConfirm = newPasswordConfirm.getText().toString();
        if (curPass.trim().length() == 0
                || newPass.trim().length() == 0
                || newPassConfirm.trim().length() == 0) {
            UiUtils.showShortToast(this, getString(R.string.password_cant_be_empty));
            return false;
        }
        if (curPass.trim().length() < 6
                || newPass.trim().length() < 6
                || newPassConfirm.trim().length() < 6) {
            UiUtils.showShortToast(this, getString(R.string.minimum_six_characters));
            return false;
        }
        if (!newPass.equals(newPassConfirm)) {
            UiUtils.showShortToast(this, getString(R.string.passwords_dont_match));
            return false;
        }
        return true;
    }

    /*private void changePassword(String oldpass, String newPass, String newPassConfrim){
        UiUtils.showLoadingDialog(ChangePasswordActivity.this);

        new Thread() {
            public void run() {


                ObjectMapper mapper = new ObjectMapper();
                HashMap<String, Object> map = new HashMap<>();
                map.put("userid", prefUtils.getStringValue(PrefKeys.USERID, ""));
                map.put("password",newPass);
                String response ="";
                try{

                    String json = mapper.writeValueAsString(map);
                    String url = new GetAddress().getUrl(Const.USER_UPDATE);
                    Log.d("url: " , url);
                    AbstractClient serv = new AbstractClient();
                    response = serv.webJson(url, json);
                    Log.d("response: " , response);
                    Log.d("json: " , json);

                    if(!response.equalsIgnoreCase(""))
                    {
                        JSONObject obj = new JSONObject(response);
                        if(obj.getString("status").equalsIgnoreCase("200"))
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    changePasswordApp(oldpass,newPass,newPassConfrim);
                                }
                            });
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UiUtils.showShortToast(ChangePasswordActivity.this, getResources().getString(R.string.somethingWrong) );

                                }
                            });
                        }

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                finally {
                    UiUtils.hideLoadingDialog();
                }

            }
        }.start();
    }*/

    private void changePasswordApp(String oldPass, String newPass, String newPassConfirm) {
        UiUtils.showLoadingDialog(this);
        PrefUtils prefUtils = PrefUtils.getInstance(this);
        Call<String> call = apiInterface.changePassword(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , oldPass
                , newPass
                , newPassConfirm);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject changePasswordResponse = null;
                try {
                    changePasswordResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (changePasswordResponse != null) {
                    if (changePasswordResponse.optString(APIConsts.Constants.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.showShortToast(ChangePasswordActivity.this, changePasswordResponse.optString(APIConsts.Params.MESSAGE));
                        startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                        finish();
                    } else {
                        UiUtils.showShortToast(ChangePasswordActivity.this, changePasswordResponse.optString(APIConsts.Params.ERROR));
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

    protected void doLogOutUser() {
        UiUtils.showLoadingDialog(this);
        PrefUtils preferences = PrefUtils.getInstance(this);
        Call<String> call = apiInterface.doLogoutUser(
                preferences.getIntValue(PrefKeys.USER_ID, 0)
                , preferences.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject logoutResponse = null;
                try {
                    logoutResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (logoutResponse != null)
                    if (logoutResponse.optString(APIConsts.Constants.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.showShortToast(ChangePasswordActivity.this, getString(R.string.login_again_with_new_password));
                        logOutUserInDevice();
                    } else {
                        UiUtils.showShortToast(ChangePasswordActivity.this, logoutResponse.optString(APIConsts.Params.ERROR));
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

    private void logOutUserInDevice() {
        PrefHelper.setUserLoggedOut(this);
        Intent restartActivity = new Intent(this, SplashActivity.class);
        restartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(restartActivity);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
