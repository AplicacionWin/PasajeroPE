package com.nikola.user.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomBoldRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.Utils.RecyclerLongPressClickListener;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.ApiManager.ParserUtils;
import com.nikola.user.network.Models.History;
import com.nikola.user.ui.Adapter.HistoryAdapter;

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
 * Created by user on 1/20/2017.
 */

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.TripsInterface {
    @BindView(R.id.ride_lv)
    RecyclerView rideLv;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.emptyIcon)
    ImageView emptyIcon;
    @BindView(R.id.heading)
    CustomBoldRegularTextView heading;
    private ArrayList<History> historylst = new ArrayList<>();
    private HistoryAdapter historyAdapter;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    boolean isHistory;

    private RecyclerView.OnScrollListener tripScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager llmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (llmanager.findLastCompletelyVisibleItemPosition() == (historyAdapter.getItemCount() - 1)) {
                historyAdapter.showLoading();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        apiInterface = APIClient.getClient().create(APIInterface.class);
        if (getIntent().getExtras() != null) {
            isHistory = getIntent().getBooleanExtra("isHistory", true);
        }
        heading.setText(isHistory ? getString(R.string.ride_history) : getString(R.string.schedule_rides));
        Glide.with(getApplicationContext()).load(R.drawable.box).into(emptyIcon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHistoryList(0);
    }

    private void setUpAdapter() {
        historyAdapter = new HistoryAdapter(this, historylst, this, isHistory);
        rideLv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rideLv.setAdapter(historyAdapter);
        rideLv.addOnScrollListener(tripScrollListener);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, getResources().getIdentifier("layout_animation_from_left", "anim", getPackageName()));
        rideLv.setLayoutAnimation(animation);
        rideLv.scheduleLayoutAnimation();
        historyAdapter.notifyDataSetChanged();
        rideLv.addOnItemTouchListener(new RecyclerLongPressClickListener(HistoryActivity.this, rideLv, new RecyclerLongPressClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
    }

    protected void getHistoryList(int skip) {
        if (skip == 0) {
            UiUtils.showLoadingDialog(this);
            historylst.clear();
        }
        Call<String> call;
        if (isHistory)
            call = apiInterface.getHistory(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                    , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                    , skip);
        else
            call = apiInterface.getLaterRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                    , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                    , skip);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                if (skip == 0) {
                    historylst.clear();
                }
                JSONObject historyResponse = null;
                try {
                    historyResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (historyResponse != null) {
                    if (historyResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONArray hisArray = historyResponse.optJSONArray(APIConsts.Params.DATA);
                        historylst = ParserUtils.ParseHistoryArrayList(hisArray, isHistory);
                        setUpAdapter();
                        if (!historylst.isEmpty()) {
                            rideLv.removeOnScrollListener(tripScrollListener);
                        }
                    } else {
                        UiUtils.showShortToast(getApplicationContext(), historyResponse.optString(APIConsts.Params.ERROR));
                    }
                }
                emptyLayout.setVisibility(historylst.isEmpty() ? View.VISIBLE : View.GONE);
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
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @OnClick(R.id.history_back)
    public void onViewClicked() {
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onLoadMoreTrips(int skip) {
        getHistoryList(skip);
    }
}
