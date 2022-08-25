package com.nikola.user.ui.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.Models.WalletPayments;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 1/20/2017.
 */

public class NewWalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<WalletPayments> payments;
    boolean isRedeem;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    TransactionInterface transactionInterface;

    public NewWalletAdapter(Context context, ArrayList<WalletPayments> payments, boolean isRedeem, TransactionInterface transactionInterface) {
        this.mContext = context;
        this.payments = payments;
        this.isRedeem = isRedeem;
        this.transactionInterface = transactionInterface;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_wallet, null);
        WalletViewHolder holder = new WalletViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof WalletViewHolder) {
            WalletViewHolder holder = (WalletViewHolder) viewHolder;
            WalletPayments item = payments.get(position);
            holder.title.setText(item.getTitle());
            holder.status.setText(item.getDescription());
            holder.amount.setText(String.format("%s %s", item.getWallet_amount_symbol(), item.getAmount()));
            Glide.with(mContext).load(item.getWallet_image()).into(holder.icon);
            holder.cancel.setVisibility(isRedeem && item.getCancelButtonStatus() ? View.VISIBLE : View.GONE);
            String redeemId = item.getRedeemId();
            holder.cancel.setOnClickListener(view -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle(R.string.confirmation).setMessage(R.string.are_you_sure_to_cancel)
                        .setPositiveButton(R.string.txt_yes, (dialog, which) -> {
                            transactionInterface.cancelRequest(redeemId);
                        }).setNegativeButton(R.string.no, (dialog, which) -> {
                    dialog.dismiss();
                }).show();
            });

            if (item.getWallet_amount_symbol().equalsIgnoreCase("+")) {
                holder.amount.setTextColor(ContextCompat.getColor(mContext, R.color.green));

            } else {
                holder.amount.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            }

        }
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }


    public class WalletViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.title)
        CustomRegularTextView title;
        @BindView(R.id.status)
        CustomRegularTextView status;
        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.cancel)
        TextView cancel;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface TransactionInterface {
        void cancelRequest(String redeemId);

        void onLoadMoreTransactions(int skip);
    }

    public void showLoading() {
        if (transactionInterface != null)
            transactionInterface.onLoadMoreTransactions(payments.size());
    }
}


