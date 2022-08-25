package com.nikola.user.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.Models.PaymentMode;
import com.nikola.user.ui.Fragment.IncompletePaymentDialog;
import com.nikola.user.ui.activity.PaymentsActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DefaultPaymentModesAdapter extends RecyclerView.Adapter<DefaultPaymentModesAdapter.PaymentModeViewHolder> {

    private PaymentsActivity context;
    private LayoutInflater inflater;
    private ArrayList<PaymentMode> paymentModes;
    private APIInterface apiInterface;
    private PrefUtils prefUtils;
    Refresh refresh;
    PreferenceHelper preferenceHelper;

    public DefaultPaymentModesAdapter(PaymentsActivity context, ArrayList<PaymentMode> paymentModes, Refresh refresh) {
        this.paymentModes = paymentModes;
        this.context = context;
        this.prefUtils = PrefUtils.getInstance(context);
        this.refresh = refresh;
        this.apiInterface = APIClient.getClient().create(APIInterface.class);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public void onBindViewHolder(@NonNull PaymentModeViewHolder viewHolder, int position) {
        PaymentMode paymentMode = paymentModes.get(position);
        viewHolder.paymentModeName.setText(paymentMode.getName());
        Glide.with(context)
                .load(paymentMode.getImage())
                .apply(new RequestOptions().error(R.drawable.card))
                .into(viewHolder.paymentModePhoto);
        viewHolder.paymentModeSelected.setImageResource(
                paymentMode.isDefault() ?
                        R.drawable.toggle_on
                        : R.drawable.toggle_off);
        viewHolder.root.setOnClickListener(v -> {
            setDefaultPaymentMode(paymentMode);
        });
    }

    @Override
    public int getItemCount() {
        return paymentModes.size();
    }

    @NonNull
    @Override
    public PaymentModeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_payment_mode, viewGroup, false);
        return new PaymentModeViewHolder(view);
    }

    private void setDefaultPaymentMode(PaymentMode paymentMode) {
        preferenceHelper = new PreferenceHelper(context);
        UiUtils.showLoadingDialog(context);
        Call<String> call = apiInterface.changeDefaultPaymentMode(prefUtils.getIntValue(PrefKeys.USER_ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                paymentMode.getName()
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject resp = null;
                try {
                    resp = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resp != null) {
                    if (resp.optBoolean(APIConsts.Constants.SUCCESS)) {
                        for (PaymentMode mode : paymentModes) {
                            mode.setDefault(false);
                        }
                        JSONObject jsonObject = resp.optJSONObject(APIConsts.Params.DATA);
                        paymentMode.setDefault(true);
                        notifyDataSetChanged();
                        UiUtils.showShortToast(context, resp.optString(APIConsts.Params.MESSAGE));
                        refresh.refreshScreen();
                        preferenceHelper.putdefalutPayment_Mode(jsonObject.optString(APIConsts.Params.PAYMENT_MODE));
                        prefUtils.setValue(PrefKeys.PAYMENT_MODE,jsonObject.optString(APIConsts.Params.PAYMENT_MODE) );
                        if(IncompletePaymentDialog.paymentMode != null) {
                            IncompletePaymentDialog.paymentMode.setText(String.format("Change you payment mode: %s", paymentMode.getName()));
                            context.finish();
                        }
                    } else {
                        UiUtils.showShortToast(context, resp.optString(APIConsts.Params.ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(context)) {
                    UiUtils.showShortToast(context, context.getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    public String getChosenPaymentMode() {
        for (PaymentMode mode : paymentModes) {
            if (mode.isDefault())
                return mode.getName();
        }
        return null;
    }

    class PaymentModeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.paymentModeName)
        TextView paymentModeName;
        @BindView(R.id.paymentModeSelected)
        ImageView paymentModeSelected;
        @BindView(R.id.paymentModePhoto)
        ImageView paymentModePhoto;
        @BindView(R.id.paymentModeRoot)
        ViewGroup root;

        PaymentModeViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface Refresh
    {
        void refreshScreen();
    }
}
