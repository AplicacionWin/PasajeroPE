package com.nikola.user.ui.Adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nikola.user.network.Models.Favourites;
import com.nikola.user.R;

import java.util.List;

/**
 * Created by asher on 2/7/2018.
 */

public class FavouritesDisplayAdapter extends RecyclerView.Adapter<FavouritesDisplayAdapter.TypesViewHolder>  {

    private Activity mContext;
    private List<Favourites> itemsFavList;

    public FavouritesDisplayAdapter(Activity context, List<Favourites> itemsFavList) {
        mContext = context;
        this.itemsFavList = itemsFavList;
    }



    public class TypesViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_fav_name, tv_fav_address;

        public TypesViewHolder(View itemView) {
            super(itemView);
            tv_fav_address = (TextView) itemView.findViewById(R.id.tv_fav_address_display);
            tv_fav_name = (TextView) itemView.findViewById(R.id.tv_fav_name_display);
        }
    }


    @Override
    public FavouritesDisplayAdapter.TypesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fav_display_item, null);
        FavouritesDisplayAdapter.TypesViewHolder holder = new FavouritesDisplayAdapter.TypesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FavouritesDisplayAdapter.TypesViewHolder typesViewHolder, int i) {
        final Favourites fav_itme = itemsFavList.get(i);
        if (fav_itme != null) {
            typesViewHolder.tv_fav_address.setText(fav_itme.getFav_Address());
            typesViewHolder.tv_fav_name.setText(fav_itme.getFav_Name());

        }
    }



    @Override
    public int getItemCount() {
        return itemsFavList.size();
    }
}

