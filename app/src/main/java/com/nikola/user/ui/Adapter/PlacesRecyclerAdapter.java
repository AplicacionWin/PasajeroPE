package com.nikola.user.ui.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nikola.user.ui.Fragment.SearchPlaceFragment.btnSearch;
import static com.nikola.user.ui.Fragment.SearchPlaceFragment.des_latLng;
import static com.nikola.user.ui.Fragment.SearchPlaceFragment.etDestinationAddress;
import static com.nikola.user.ui.Fragment.SearchPlaceFragment.etSourceAddress;
import static com.nikola.user.ui.Fragment.SearchPlaceFragment.etStopAddress;
import static com.nikola.user.ui.Fragment.SearchPlaceFragment.sourceLatLng;
import static com.nikola.user.ui.Fragment.SearchPlaceFragment.stop_latLng;


public class PlacesRecyclerAdapter extends RecyclerView.Adapter<PlacesRecyclerAdapter.ViewHolder> {
    public static final String LOG_TAG = "PlacesAutoCompleteAdapter";
    private Context mcontext;
    ArrayList<String> resultList;
    boolean sClick, dClick, stopClick;
    Dialog dialog;

    public PlacesRecyclerAdapter(Context context, ArrayList<String> resultList, boolean sClick, boolean dClick, boolean stopClick, Dialog dialog) {
        this.mcontext = context;
        this.resultList = resultList;
        this.sClick = sClick;
        this.dClick = dClick;
        this.stopClick = stopClick;
        this.dialog = dialog;
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
        Glide.with(mcontext).load(R.drawable.pointer).into(holder.icon);
        holder.locationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stopClick) {
                    etStopAddress.setText(resultList.get(position));
                    sourceLatLng = getLatLngFrmAddress(resultList.get(position));
                } else if (dClick) {
                    etDestinationAddress.setText(resultList.get(position));
                    des_latLng = getLatLngFrmAddress(resultList.get(position));
                    btnSearch.setEnabled(true);
                    btnSearch.setBackgroundColor(mcontext.getResources().getColor(R.color.black));
                } else if (sClick) {
                    etSourceAddress.setText(resultList.get(position));
                    stop_latLng = getLatLngFrmAddress(resultList.get(position));
                }
                dialog.dismiss();
                InputMethodManager imm = (InputMethodManager) mcontext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private LatLng getLatLngFrmAddress(String address) {
        LatLng latLng = null;
        Geocoder geocoder = new Geocoder(mcontext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latLng;
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.locationName)
        CustomRegularTextView locationName;
        @BindView(R.id.icon)
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
