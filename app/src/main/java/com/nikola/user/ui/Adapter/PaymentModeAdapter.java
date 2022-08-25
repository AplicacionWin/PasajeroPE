package com.nikola.user.ui.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.network.Models.Payments;

import java.util.List;

import static com.nikola.user.ui.Fragment.RequestMapFragment.tvCashtype;

/**
 * Created by user on 1/21/2017.
 */

public class PaymentModeAdapter extends RecyclerView.Adapter<PaymentModeAdapter.typesViewHolder> {

    private Activity mContext;
    private List<Payments> itemspaymentList;
    Dialog dialog;
    PrefUtils prefUtils;
    PreferenceHelper preferenceHelper;
    WalletListener walletListener;
    boolean isAddMoneyToWallet;


    public PaymentModeAdapter(Activity context, List<Payments> itemspaymentListm, Dialog payDialog, WalletListener walletListener, boolean isAddMoneyToWallet) {
        mContext = context;
        this.itemspaymentList = itemspaymentListm;
        this.dialog = payDialog;
        prefUtils = PrefUtils.getInstance(context);
        this.walletListener = walletListener;
        this.isAddMoneyToWallet = isAddMoneyToWallet;
    }

    @Override
    public PaymentModeAdapter.typesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.select_payment_item, null);
        PaymentModeAdapter.typesViewHolder holder = new PaymentModeAdapter.typesViewHolder(view);
        preferenceHelper = new PreferenceHelper(mContext);
        ;
        return holder;
    }

    @Override
    public void onBindViewHolder(final PaymentModeAdapter.typesViewHolder holder, int position) {
        Payments payment_itme = itemspaymentList.get(position);
        if (payment_itme != null) {
            holder.payment_img.setText(String.format("Pay by %s", payment_itme.getPayment_name()));
        }
        holder.payment_img.setOnClickListener(view -> {
            prefUtils.setValue(PrefKeys.PAYMENT_MODE, payment_itme.getPaymentMode());
            if (payment_itme.getPaymentMode().equalsIgnoreCase("wallet") && isAddMoneyToWallet) {
                prefUtils.setValue(PrefKeys.PAYMENT_MODE, payment_itme.getPaymentMode());
                tvCashtype.setText(String.format("Tu modo de pago: %s", prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, "")));
                walletListener.handleWalletVisibility();
            } else {
                String paymentName = payment_itme.getPaymentMode();
                prefUtils.setValue(PrefKeys.PAYMENT_MODE, payment_itme.getPaymentMode());
                tvCashtype.setText(String.format("Tu modo de pago: %s", prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, "")));
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemspaymentList.size();
    }


    public class typesViewHolder extends RecyclerView.ViewHolder {
        private Button payment_img;

        public typesViewHolder(View itemView) {
            super(itemView);
            payment_img = (Button) itemView.findViewById(R.id.payment_btn);

        }
    }

    public interface WalletListener {
        void handleWalletVisibility();
    }

}


