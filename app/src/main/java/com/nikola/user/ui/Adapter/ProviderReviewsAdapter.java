package com.nikola.user.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nikola.user.R;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.Models.ProviderDetails;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ProviderReviewsAdapter extends RecyclerView.Adapter<ProviderReviewsAdapter.ViewHolder> {

    public static final int SHOWING_EXPLORE_HOME = 1;
    public static final int SHOWING_SAVED_HOME = 2;
    public static final int SHOWING_MAP_HOME = 3;

    private Context context;
    private LayoutInflater inflater;
    private int showingType;
    private OnLoadMoreHomes listener;
    private APIInterface apiInterface;
    ArrayList<ProviderDetails> details = new ArrayList<>();

    public ProviderReviewsAdapter(Context context, OnLoadMoreHomes onLoadMoreHomes, ArrayList<ProviderDetails> details) {
        this.details = details;
        this.context = context;
        this.listener = onLoadMoreHomes;
        this.showingType = showingType;
        this.apiInterface = APIClient.getClient().create(APIInterface.class);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.item_provider_homes, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public interface OnLoadMoreHomes {
        void onLoadMoreHomes(int size);

        void refreshPage();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
