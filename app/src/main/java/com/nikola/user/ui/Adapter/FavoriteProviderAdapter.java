package com.nikola.user.ui.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.Models.FavProvider;
import com.nikola.user.ui.activity.FavProviderActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteProviderAdapter extends RecyclerView.Adapter<FavoriteProviderAdapter.ViewHolder> {
    private Context context;
    FavProviderActivity activity;
    ArrayList<FavProvider> favProviders = new ArrayList<>();
    APIInterface apiInterface;
    PrefUtils prefUtils;

    public FavoriteProviderAdapter(FavProviderActivity activity, ArrayList<FavProvider> favProviders) {
        this.context = context;
        this.activity = activity;
        this.favProviders = favProviders;
        this.apiInterface = APIClient.getClient().create(APIInterface.class);
        this.prefUtils = PrefUtils.getInstance(activity);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.favorite_provider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavProvider favProvider = favProviders.get(position);

        holder.providerName.setText(favProvider.getName());
        holder.rating.setRating(favProvider.getRating());

        holder.mobile.setText(favProvider.getMobile());
       // Glide.with(activity).load(favProvider.getProUrl()).into(holder.providerPicture);
        Glide.with(activity)
                .load(favProvider.getProUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                       UiUtils.hideLoadingDialog();
                        return false;
                    }
                })
                .into(holder.providerPicture);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.favOrNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.confrim_unfavourite)
                        .setMessage(R.string.remove_fromfavourite)
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeFavProvider(favProvider.getUserFavProviderId(), position);
                            }
                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create().show();
            }
        });
    }

    private void removeFavProvider(String userFavProviderId, int position) {
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.removeFavProvider(prefUtils.getIntValue(PrefKeys.USER_ID,0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN,""),
                userFavProviderId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject removeResponse = null;
                try{
                    removeResponse = new JSONObject(response.body());
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (removeResponse != null){
                    UiUtils.hideLoadingDialog();
                    favProviders.remove(position);
                    UiUtils.showShortToast(activity,removeResponse.optString(APIConsts.Params.MESSAGE));
                    //activity.startActivity(new Intent(activity, FavProviderActivity.class));
                    activity.finish();

                } else {
                    UiUtils.showShortToast(activity, removeResponse.optString(APIConsts.Params.ERROR_MESSAGE));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(activity)) {
                    UiUtils.showShortToast(activity, activity.getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return favProviders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.providerPicture)
        CircleImageView providerPicture;
        @BindView(R.id.providerName)
        TextView providerName;
        @BindView(R.id.rating)
        SimpleRatingBar rating;
        @BindView(R.id.favOrNot)
        ImageView favOrNot;
        @BindView(R.id.root)
        ViewGroup root;
        @BindView(R.id.providerMobile)
        TextView mobile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
