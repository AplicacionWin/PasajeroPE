package com.nikola.user.ui.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.ui.activity.PaymentsActivity;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncompletePaymentDialog extends DialogFragment {

    Unbinder unbinder;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    String dueAmount;
    @BindView(R.id.amount)
    CustomRegularTextView amount;
    public static CustomRegularTextView paymentMode;
    @BindView(R.id.payDue)
    CustomRegularTextView payDue;


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.incompete_payment_dialog, null);
        unbinder = ButterKnife.bind(this, contentView);
        paymentMode = contentView.findViewById(R.id.paymentMode);
        dialog.setContentView(contentView);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        amount.setText(dueAmount);
        paymentMode.setText(String.format("Change payment mode: %s", prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, "")));
    }

    public IncompletePaymentDialog(String dueAmount) {
        this.dueAmount = dueAmount;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.paymentMode, R.id.payDue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.paymentMode:
                Intent i = new Intent(getActivity(), PaymentsActivity.class);
                startActivity(i);
                break;
            case R.id.payDue:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(getString(R.string.paymentConfirmation))
                        .setMessage(String.format("%s %s", getString(R.string.are_you_sure), "Pay now"))
                        .setPositiveButton(R.string.txt_yes, (dialog, which) -> {
                            makeDuePayments();
                        }).setNegativeButton(R.string.no, (dialog, which) -> {
                            alertDialogBuilder.setOnDismissListener(dialogInterface -> dialogInterface.dismiss());
                }).show();
                break;
        }
    }

    private void makeDuePayments() {
        UiUtils.showLoadingDialog(getActivity());
        Call<String> call = apiInterface.makeDuePayments(prefUtils.getIntValue(PrefKeys.USER_ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject paymentsResponse = null;
                try {
                    paymentsResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paymentsResponse != null) {
                    if (paymentsResponse.optString(APIConsts.Constants.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONObject data = paymentsResponse.optJSONObject(APIConsts.Params.DATA);
                        UiUtils.showShortToast(getActivity(), data.optString(APIConsts.Params.MESSAGE));
                    } else {
                        UiUtils.showShortToast(getActivity(), paymentsResponse.optString(APIConsts.Params.ERROR));
                        dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }
}
