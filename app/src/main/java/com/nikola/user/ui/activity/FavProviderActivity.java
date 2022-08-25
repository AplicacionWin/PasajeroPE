package com.nikola.user.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.braintreepayments.api.Json;
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
import com.nikola.user.network.Models.FavProvider;
import com.nikola.user.ui.Adapter.FavoriteProviderAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavProviderActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fav_providers_RV)
    RecyclerView fav_Provider_Rv;
    @BindView(R.id.no_fav)
    TextView noFav;
    ArrayList<FavProvider> favProviders = new ArrayList<>();
    APIInterface apiInterface;
    PrefUtils prefUtils;
    FavoriteProviderAdapter favoriteProviderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_provider);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(this);
        setupToolbar();
        getFavProvider();
    }

    private void getFavProvider() {
        UiUtils.showLoadingDialog(this);
        Call<String> call = apiInterface.listFavProvider(prefUtils.getIntValue(PrefKeys.USER_ID,0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN,""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject listResponse = null;
                try{
                    listResponse = new JSONObject(response.body());
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (listResponse != null){
                    UiUtils.hideLoadingDialog();
                    if (listResponse.optString(Const.Params.SUCCESS).equalsIgnoreCase(APIConsts.Constants.TRUE)){
                        JSONArray jsonArray = listResponse.optJSONArray(APIConsts.Params.DATA);
                        if (jsonArray.length()!=0){
                            noFav.setVisibility(View.GONE);
                            fav_Provider_Rv.setVisibility(View.VISIBLE);
                            favProviders.clear();
                            for (int i=0 ; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                FavProvider favProvider = new FavProvider();
                                favProvider.setId(String.valueOf(jsonObject.optInt(APIConsts.Params.USER_ID)));
                                favProvider.setUserFavProviderId(String.valueOf(jsonObject.optInt(APIConsts.Params.USER_FAVOURITE_ID)));
                                favProvider.setProviderId(String.valueOf(jsonObject.optInt(APIConsts.Params.PROVIDER_ID)));
                                favProvider.setName(jsonObject.optString(APIConsts.Params.PROVIDER_NAME));
                                favProvider.setProUrl(jsonObject.optString(APIConsts.Params.PROVIDER_PICTURE));
                                favProvider.setMobile(jsonObject.optString(APIConsts.Params.PROVIDER_MOBILE));
                                favProvider.setRating(jsonObject.optInt(APIConsts.Params.PROVIDER_RATING));
                                favProviders.add(favProvider);
                            }
                            setProviderAdapter(favProviders);
                        } else {
                            noFav.setVisibility(View.VISIBLE);
                            fav_Provider_Rv.setVisibility(View.GONE);
                        }
                    }else {
                        UiUtils.showShortToast(FavProviderActivity.this, listResponse.optString(APIConsts.Params.ERROR));
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(FavProviderActivity.this)) {
                    UiUtils.showShortToast(FavProviderActivity.this, getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    private void setProviderAdapter(ArrayList<FavProvider> favProviders) {
        favoriteProviderAdapter = new FavoriteProviderAdapter(this, favProviders);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        fav_Provider_Rv.setLayoutManager(linearLayoutManager);
        fav_Provider_Rv.setAdapter(favoriteProviderAdapter);
        favoriteProviderAdapter.notifyDataSetChanged();
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.fav_provider));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.back));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
}
