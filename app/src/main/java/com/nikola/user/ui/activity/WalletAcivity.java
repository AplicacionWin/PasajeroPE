package com.nikola.user.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomBoldRegularTextView;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.Models.WalletPayments;
import com.nikola.user.ui.Adapter.NewWalletAdapter;
import com.nikola.user.ui.Fragment.AddMoneyBottomSheet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletAcivity extends AppCompatActivity implements NewWalletAdapter.TransactionInterface {

    NewWalletAdapter walletAdapter;
    ArrayList<WalletPayments> paymentsList = new ArrayList<>();
    @BindView(R.id.addMoney)
    LinearLayout addMoney;
    @BindView(R.id.transaction)
    LinearLayout transaction;
    @BindView(R.id.redeem)
    LinearLayout redeem;
    @BindView(R.id.recentTransactionRecycler)
    RecyclerView recentTransactionRecycler;

    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.remaining)
    CustomBoldRegularTextView remaining;
    @BindView(R.id.total)
    CustomBoldRegularTextView total;
    @BindView(R.id.toolbar)
    CustomRegularTextView toolbar;
    @BindView(R.id.viewMore)
    CustomRegularTextView viewMore;
    @BindView(R.id.noData)
    CustomRegularTextView noData;
    @BindView(R.id.emptyData)
    ImageView empty_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_acivity);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        setUpAdapter();
        toolbar.setOnClickListener(view -> onBackPressed());
        Glide.with(getApplicationContext()).load(R.drawable.box).into(empty_data);
    }

    private void setUpAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        walletAdapter = new NewWalletAdapter(this, paymentsList, false, this);
        recentTransactionRecycler.setLayoutManager(linearLayoutManager);
        recentTransactionRecycler.setAdapter(walletAdapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, getResources().getIdentifier("layout_animation_from_left", "anim", getPackageName()));
        recentTransactionRecycler.setLayoutAnimation(animation);
        recentTransactionRecycler.scheduleLayoutAnimation();
        recentTransactionRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWalletInfo();
    }

    @OnClick({R.id.addMoney, R.id.redeem, R.id.transaction, R.id.viewMore})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.addMoney:
                AddMoneyBottomSheet addMoneyBottomSheet = new AddMoneyBottomSheet();
                addMoneyBottomSheet.setIsRedeem(false, remaining.getText().toString());
                addMoneyBottomSheet.show(getSupportFragmentManager(), addMoneyBottomSheet.getTag());
                break;
            case R.id.redeem:
                Intent redeemIntent = new Intent(getApplicationContext(), RedeemActivity.class);
                startActivity(redeemIntent);
                break;
            case R.id.transaction:
            case R.id.viewMore:
                Intent transactionIntent = new Intent(getApplicationContext(), TransactionListActivity.class);
                transactionIntent.putExtra(APIConsts.Params.IS_REDEEM, false);
                startActivity(transactionIntent);
                break;
        }
    }

    protected void getWalletInfo() {
        UiUtils.showLoadingDialog(WalletAcivity.this);
        Call<String> call = apiInterface.getWalletData(prefUtils.getIntValue(PrefKeys.USER_ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                paymentsList.clear();
                JSONObject walletResponse = null;
                try {
                    walletResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (walletResponse != null) {
                    if (walletResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONObject data = walletResponse.optJSONObject(APIConsts.Params.DATA);
                        JSONObject wallet = data.optJSONObject(APIConsts.Params.WALLET);
                        remaining.setText(wallet.optString(APIConsts.Params.REMINING_FORMATTED));
                        prefUtils.setValue(APIConsts.Params.REMINING_FORMATTED,wallet.optInt(APIConsts.Params.REMINING));
                        total.setText(wallet.optString(APIConsts.Params.TOTAL_FORMATTED));
                        JSONArray paymentsArray = data.optJSONArray(APIConsts.Params.PAYMENTS);
                        for (int i = 0; i < paymentsArray.length(); i++) {
                            JSONObject paymentsObject = paymentsArray.optJSONObject(i);
                            WalletPayments payments = new WalletPayments();
                            payments.setTitle(paymentsObject.optString(APIConsts.Params.TITLE));
                            payments.setDescription(paymentsObject.optString(APIConsts.Params.DESCRIPTION));
                            payments.setUniqueId(paymentsObject.optString(APIConsts.Params.UNIQUE_ID));
                            payments.setWallet_amount_symbol(paymentsObject.optString(APIConsts.Params.AMOUNT_SYMBOL));
                            payments.setWallet_image(paymentsObject.optString(APIConsts.Params.IMAGE));
                            payments.setAmount(paymentsObject.optString(APIConsts.Params.TOTAL_FORMATTED));
                            paymentsList.add(payments);
                        }

                        walletAdapter.notifyDataSetChanged();
                        empty_data.setVisibility(paymentsList.isEmpty() ? View.VISIBLE : View.GONE);
                        noData.setVisibility(paymentsList.isEmpty() ? View.VISIBLE : View.GONE);
                        recentTransactionRecycler.setVisibility(paymentsList.isEmpty() ? View.GONE : View.VISIBLE);
                    } else {
                        UiUtils.showShortToast(getApplicationContext(), walletResponse.optString(APIConsts.Params.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    @Override
    public void cancelRequest(String redeemId) {
    }

    @Override
    public void onLoadMoreTransactions(int skip) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
