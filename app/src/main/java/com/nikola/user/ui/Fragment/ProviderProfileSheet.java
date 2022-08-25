package com.nikola.user.ui.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.Models.ProviderDetails;
import com.nikola.user.ui.Adapter.ProviderReviewsAdapter;

import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderProfileSheet extends BottomSheetDialogFragment implements ProviderReviewsAdapter.OnLoadMoreHomes {

    Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    View container;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.numHomes)
    TextView numHomes;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.homesRecycler)
    RecyclerView homesRecycler;
    ArrayList<ProviderDetails> providerDetailsList = new ArrayList<>();


    ProviderDetails providerDetails = new ProviderDetails();
    private String providerId;
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
    private APIInterface apiInterface;
    private Context context;

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.layout_provider_info, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        setCancelable(false);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        getProviderData(providerId);
    }

    private void getProviderData(String providerId) {
        PrefUtils preferences = PrefUtils.getInstance(context);
        Call<String> call = apiInterface.getProviderProfile(
                preferences.getIntValue(PrefKeys.USER_ID, 0),
                preferences.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                providerId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject providerResponse = null;
                try {
                    providerResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isAdded() && providerResponse != null) {
                    if (providerResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        if (providerDetails.parse(providerResponse.optJSONObject(Const.Params.DATA))) {
                            onProviderDataUpdated();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private void onProviderDataUpdated() {
        name.setText(providerDetails.getProviderName());
        Glide.with(context)
                .load(providerDetails.getPicture())
                .centerCrop()
                .into(image);

        description.setText(Html.fromHtml(providerDetails.getDescription()));
        name.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                providerDetails.isVerified() ? context.getResources().getDrawable(R.drawable.defult_user) : null, null);
        //Homes
        numHomes.setText(MessageFormat.format("{0} Homes",
                providerDetails.getNumHomes()));
        homesRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        homesRecycler.setAdapter(new ProviderReviewsAdapter(context, this, providerDetailsList));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onLoadMoreHomes(int size) {

    }

    @Override
    public void refreshPage() {

    }

}
