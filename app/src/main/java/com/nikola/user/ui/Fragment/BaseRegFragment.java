package com.nikola.user.ui.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.nikola.user.network.ApiManager.AsyncTaskCompleteListener;
import com.nikola.user.ui.activity.SignInActivity;

/**
 * Created by user on 8/29/2016.
 */
public class BaseRegFragment extends Fragment implements
        View.OnClickListener, AsyncTaskCompleteListener {
    SignInActivity activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        activity = (SignInActivity) getActivity();


    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

    }

    @Override
    public void onClick(View v) {


    }
}
