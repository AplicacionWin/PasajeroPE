package com.nikola.user.ui.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularEditView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.ui.activity.SignInActivity;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 1/5/2017.
 */

public class ForgotpassFragment extends AppCompatActivity {

    @BindView(R.id.et_email_forgot)
    CustomRegularEditView etEmailForgot;
    @BindView(R.id.input_layout_email_forgot)
    TextInputLayout inputLayoutEmailForgot;
    APIInterface apiInterface;
    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

    }

    @OnClick({R.id.btn_forgot_cancel, R.id.forgot_pass_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_forgot_cancel:
                Intent i = new Intent(ForgotpassFragment.this, SignInActivity.class);
                startActivity(i);
                break;
            case R.id.forgot_pass_btn:
                if (etEmailForgot.getText().toString().length() == 0) {
                    etEmailForgot.requestFocus();
                    inputLayoutEmailForgot.setError(getString(R.string.txt_email_error));
                } else {
                    inputLayoutEmailForgot.setError(null);
                    forgotPassword();
                }
                break;
        }
    }

    protected void forgotPassword() {
        UiUtils.showLoadingDialog(ForgotpassFragment.this);
        Call<String> call = apiInterface.makeCallForgotPassword(etEmailForgot.getText().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject forgotPasswordResponse = null;
                try {
                    forgotPasswordResponse = new JSONObject(response.body());
                    if (forgotPasswordResponse.optString(Const.Params.SUCCESS).equals(Const.Params.TRUE)) {
                        UiUtils.showShortToast(ForgotpassFragment.this, getString(R.string.txt_success_forgot_password));
                        Intent i = new Intent(ForgotpassFragment.this, SignInActivity.class);
                        startActivity(i);
                    } else {
                        AndyUtils.showShortToast(forgotPasswordResponse.optString(APIConsts.Params.ERROR), ForgotpassFragment.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
