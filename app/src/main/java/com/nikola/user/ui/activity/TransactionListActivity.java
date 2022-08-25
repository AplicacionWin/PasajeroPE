package com.nikola.user.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionListActivity extends AppCompatActivity implements NewWalletAdapter.TransactionInterface {

    @BindView(R.id.recentTransactionRecycler)
    RecyclerView recentTransactionRecycler;
    NewWalletAdapter walletAdapter;
    ArrayList<WalletPayments> paymentsList = new ArrayList<>();
    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.empty_data)
    ImageView emptyData;
    boolean isRedeem;
    @BindView(R.id.noData)
    CustomRegularTextView noData;

    private RecyclerView.OnScrollListener transactionScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager llmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (llmanager.findLastCompletelyVisibleItemPosition() == (walletAdapter.getItemCount() - 1)) {
                walletAdapter.showLoading();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null)
            isRedeem = getIntent().getBooleanExtra(APIConsts.Params.IS_REDEEM, false);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        apiInterface = APIClient.getClient().create(APIInterface.class);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        toolbar.setTitle(getString(R.string.transactions));
        setUpAdapter();
        getTransactions(0);
        Glide.with(getApplicationContext()).load(R.drawable.box).into(emptyData);
    }

    private void setUpAdapter() {

        walletAdapter = new NewWalletAdapter(this, paymentsList, isRedeem, this);
        recentTransactionRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recentTransactionRecycler.setAdapter(walletAdapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, getResources().getIdentifier("layout_animation_from_left", "anim", getPackageName()));
        recentTransactionRecycler.setLayoutAnimation(animation);
        recentTransactionRecycler.scheduleLayoutAnimation();
        recentTransactionRecycler.addOnScrollListener(transactionScrollListener);
    }

    protected void getTransactions(int skip) {
        if (skip == 0) {
            UiUtils.showLoadingDialog(TransactionListActivity.this);
            paymentsList.clear();
        }
        Call<String> call;
        if (!isRedeem)
            call = apiInterface.getWalletTransactions(prefUtils.getIntValue(PrefKeys.USER_ID, 0),
                    prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                    skip);
        else
            call = apiInterface.getAllRedeemRequests(prefUtils.getIntValue(PrefKeys.USER_ID, 0),
                    prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                    skip);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject walletResponse = null;
                try {
                    walletResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (walletResponse != null) {
                    if (walletResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONArray data = walletResponse.optJSONArray(APIConsts.Params.DATA);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject paymentsObject = data.optJSONObject(i);
                            WalletPayments payments = new WalletPayments();
                            payments.setTitle(paymentsObject.optString(APIConsts.Params.TITLE));
                            payments.setRedeemId(paymentsObject.optString(APIConsts.Params.USER_REDEEM_REQUEST_ID));
                            payments.setDescription(paymentsObject.optString(APIConsts.Params.DESCRIPTION));
                            payments.setUniqueId(paymentsObject.optString(APIConsts.Params.UNIQUE_ID));
                            payments.setWallet_amount_symbol(paymentsObject.optString(APIConsts.Params.AMOUNT_SYMBOL));
                            payments.setWallet_image(paymentsObject.optString(APIConsts.Params.IMAGE));
                            payments.setAmount(paymentsObject.optString(APIConsts.Params.TOTAL_FORMATTED));
                            payments.setCancelButtonStatus(paymentsObject.optInt(APIConsts.Params.CANCEL_BUTTON_STATUS) == 1);
                            paymentsList.add(payments);
                        }

                        walletAdapter.notifyDataSetChanged();
                        emptyData.setVisibility(paymentsList.isEmpty() ? View.VISIBLE : View.GONE);
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
        cancelRedeemRequest(redeemId);
    }

    private void cancelRedeemRequest(String redeemId) {
        UiUtils.showLoadingDialog(TransactionListActivity.this);
        Call<String> call = apiInterface.cancelRedeemRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                redeemId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject cancelRequestResponse = null;
                try {
                    cancelRequestResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cancelRequestResponse != null) {
                    if (cancelRequestResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.showShortToast(TransactionListActivity.this, cancelRequestResponse.optString(APIConsts.Params.MESSAGE));
                        getTransactions(0);
                    }
                } else {
                    UiUtils.showShortToast(TransactionListActivity.this, cancelRequestResponse.optString(APIConsts.Params.ERROR));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(TransactionListActivity.this)) {
                    UiUtils.showShortToast(TransactionListActivity.this, getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    @Override
    public void onLoadMoreTransactions(int skip) {
        getTransactions(skip);
    }
}
