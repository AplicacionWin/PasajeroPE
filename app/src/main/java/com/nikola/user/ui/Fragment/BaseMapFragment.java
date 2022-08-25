package com.nikola.user.ui.Fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.nikola.user.network.ApiManager.AsyncTaskCompleteListener;
import com.nikola.user.ui.activity.MainActivity;

/**
 * Created by user on 1/5/2017.
 */

public abstract class BaseMapFragment extends Fragment implements
        View.OnClickListener, AsyncTaskCompleteListener {
    MainActivity activity;
    public static LatLng pic_latlan;
    public static LatLng drop_latlan;
    public static boolean searching =false;
    public static String s_address,d_address="",stop_address="";
    public static LatLng stop_latlan;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();


    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

    }

    @Override
    public void onClick(View v) {


    }

    @Nullable
    public abstract View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);
}
