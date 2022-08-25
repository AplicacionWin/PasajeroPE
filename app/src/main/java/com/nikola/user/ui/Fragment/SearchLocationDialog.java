package com.nikola.user.ui.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.AsyncTaskCompleteListener;
import com.nikola.user.network.ApiManager.VollyRequester;
import com.nikola.user.ui.Adapter.FavoriteAddressAdapter;
import com.nikola.user.ui.Adapter.PlacesRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchLocationDialog extends DialogFragment implements AsyncTaskCompleteListener {

    Unbinder unbinder;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.searchBar)
    EditText searchBar;
    @BindView(R.id.locationRecycler)
    RecyclerView locationRecycler;
    @BindView(R.id.favoriteRecycler)
    RecyclerView favoriteRecycler;
    private ArrayList<String> resultList = new ArrayList<>();
    private ArrayList<String> favoriteList = new ArrayList<>();
    PlacesRecyclerAdapter placesAutoCompleteAdapter;
    FavoriteAddressAdapter favoriteAddressAdapter;
    boolean sClick, dClick, stopClick;
    Dialog dialog;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        this.dialog = dialog;
        View contentView = View.inflate(getContext(), R.layout.search_location_dialog, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getActivity());
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getSearchLocation(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        setAdapter();
        //setFavoriteAdapter();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(Objects.requireNonNull(getActivity()), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

    public SearchLocationDialog(boolean sClick, boolean dClick, boolean stopClick) {
        this.sClick = sClick;
        this.dClick = dClick;
        this.stopClick = stopClick;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    public void setAdapter() {
        locationRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        placesAutoCompleteAdapter = new PlacesRecyclerAdapter(getActivity(), resultList, sClick, dClick, stopClick, dialog);
        locationRecycler.setAdapter(placesAutoCompleteAdapter);
    }

    public void setFavoriteAdapter() {
        favoriteList.add("Koramangala");
        favoriteList.add("Bannergatta Road");
        favoriteList.add("Codegama Electonic city");
        favoriteList.add("5th block");
        favoriteList.add("Kudle Gate");
        favoriteRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoriteAddressAdapter = new FavoriteAddressAdapter(getActivity(), favoriteList);
        favoriteRecycler.setAdapter(favoriteAddressAdapter);
    }

    private void getSearchLocation(String input) {
        if (!AndyUtils.isNetworkAvailable(getActivity()))
            return;
        HashMap<String, String> map = new HashMap<>();
        try {
            map.put(Const.Params.URL, Const.PLACES_API_BASE + Const.TYPE_AUTOCOMPLETE + Const.OUT_JSON + "?sensor=false&key="
                    + Const.PLACES_AUTOCOMPLETE_API_KEY + "&radius=500" + "&input=" + URLEncoder.encode(input, "utf8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        new VollyRequester(getActivity(), Const.GET, map, 101, this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case 101:
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    final JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
                    getActivity().runOnUiThread(() -> {
                        resultList.clear();
                        for (int i = 0; i < predsJsonArray.length(); i++) {
                            try {
                                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                                placesAutoCompleteAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
