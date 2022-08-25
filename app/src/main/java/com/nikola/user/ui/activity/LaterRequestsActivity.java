package com.nikola.user.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.nikola.user.network.ApiManager.ParserUtils;
import com.nikola.user.network.Models.Later;
import com.nikola.user.ui.Adapter.LaterAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 2/3/2017.
 */

public class LaterRequestsActivity extends AppCompatActivity implements LaterAdapter.CancelLaterRequest, LaterAdapter.TripsInterface {

    @BindView(R.id.ride_lv)
    RecyclerView rideLv;
    @BindView(R.id.ride_progress_bar)
    ProgressBar rideProgressBar;
    @BindView(R.id.later_empty)
    CustomRegularTextView laterEmpty;
    private ArrayList<Later> laterArrayList = new ArrayList<>();
    private LaterAdapter laterAdapter;
    APIInterface apiInterface;
    PrefUtils prefUtils;

    private RecyclerView.OnScrollListener tripScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager llmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (llmanager.findLastCompletelyVisibleItemPosition() == (laterAdapter.getItemCount() - 1)) {
                laterAdapter.showLoading();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.later_request_layout);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        getLaterRequestList(0);
    }

    private void setUpAdapter() {
        laterAdapter = new LaterAdapter(LaterRequestsActivity.this, laterArrayList, this, this);
        rideLv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rideLv.setAdapter(laterAdapter);
        rideLv.addOnScrollListener(tripScrollListener);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, getResources().getIdentifier("layout_animation_from_left", "anim", getPackageName()));
        rideLv.setLayoutAnimation(animation);
        rideLv.scheduleLayoutAnimation();
    }

    protected void getLaterRequestList(int skip) {
        if(skip == 0)
        UiUtils.showLoadingDialog(this);
        Call<String> call = apiInterface.getLaterRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , skip);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject laterResponse = null;
                try {
                    laterResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (laterResponse != null) {
                    if (laterResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {

                        if(skip == 0) {
                            rideProgressBar.setVisibility(View.GONE);
                            laterArrayList.clear();
                        }
                        JSONArray data = laterResponse.optJSONArray(APIConsts.Params.DATA);
                        laterArrayList = ParserUtils.ParseLaterRequest(data);

                        if(skip == 0)
                        setUpAdapter();
                        laterAdapter.notifyDataSetChanged();
                        if (laterArrayList.isEmpty()) {
                            if (skip == 0) {
                                laterEmpty.setVisibility(laterArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                            }
                            rideLv.removeOnScrollListener(tripScrollListener);
                        }
                    } else {
                        UiUtils.showShortToast(getApplicationContext(), laterResponse.optString(APIConsts.Params.ERROR));
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.later_back)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void cancelRequest(String requestId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.confirmation).setMessage(R.string.delete_message).setPositiveButton(R.string.txt_yes, (dialog, which) -> {
            cancelALaterRequest(requestId);
        }).setNegativeButton(R.string.no, (dialog, which) -> {
            dialog.dismiss();
        }).show();
    }

    private void cancelALaterRequest(String requestId) {
        UiUtils.showLoadingDialog(this);
        Call<String> call = apiInterface.cancelLaterRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , requestId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject laterResponse = null;
                try {
                    laterResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (laterResponse != null) {
                    if (laterResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        getLaterRequestList(0);
                    } else {
                        UiUtils.showShortToast(getApplicationContext(), laterResponse.optString(APIConsts.Params.ERROR));
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


    @Override
    public void onLoadMoreTrips(int skip) {
        getLaterRequestList(skip);
    }
}
