package com.nikola.user.ui.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nikola.user.NewUtilsAndPref.AppUtils;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.R;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordBottomSheet extends BottomSheetDialogFragment {

    @BindView(R.id.yourEmail)
    EditText email;
    Unbinder unbinder;
    APIInterface apiInterface;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.layout_forgot_password, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        setCancelable(false);
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }


    @OnClick(R.id.sendConfirmationEmail)
    protected void sendEmailClicked() {
        if (validateFields()) {
            sendConfirmationMail();
        }
    }

    private void sendConfirmationMail() {
        UiUtils.showLoadingDialog(getActivity());
        Call<String> call = apiInterface.forgotPassword(email.getText().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject forgotPasswordResponse = null;
                try {
                    forgotPasswordResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (forgotPasswordResponse != null) {
                    if (forgotPasswordResponse.optString(APIConsts.Constants.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.showShortToast(getActivity(), forgotPasswordResponse.optString(APIConsts.Params.MESSAGE));
                        dismiss();
                    } else {
                        UiUtils.showShortToast(getActivity(), forgotPasswordResponse.optString(APIConsts.Params.ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    private boolean validateFields() {
        if (email.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(getActivity(), getString(R.string.email_cant_be_empty));
            return false;
        }
        if (!AppUtils.isValidEmail(email.getText().toString())) {
            UiUtils.showShortToast(getActivity(), getString(R.string.enter_valid_email));
            return false;
        }
        return true;
    }

    @OnClick(R.id.closeBtn)
    protected void closeSheet() {
        dismiss();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
