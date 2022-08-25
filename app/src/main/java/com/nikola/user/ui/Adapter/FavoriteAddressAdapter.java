package com.nikola.user.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteAddressAdapter extends RecyclerView.Adapter<FavoriteAddressAdapter.ViewHolder> {

    public static final String LOG_TAG = "PlacesAutoCompleteAdapter";
    private Context mcontext;
    ArrayList<String> resultList;

    public FavoriteAddressAdapter(Context context, ArrayList<String> resultList) {
        this.mcontext = context;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_search_location, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.locationName.setText(resultList.get(position));
        Glide.with(mcontext).load(R.drawable.like).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.locationName)
        CustomRegularTextView locationName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
