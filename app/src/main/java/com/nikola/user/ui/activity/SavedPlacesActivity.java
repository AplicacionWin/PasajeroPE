package com.nikola.user.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Const;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.network.ApiManager.AsyncTaskCompleteListener;
import com.nikola.user.network.ApiManager.VollyRequester;
import com.nikola.user.network.Models.Favourites;
import com.nikola.user.ui.Adapter.FavouritesAdapter;
import com.nikola.user.ui.Adapter.PlacesAutoCompleteAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SavedPlacesActivity extends AppCompatActivity implements AsyncTaskCompleteListener {
    @BindView(R.id.fav_back)
    ImageButton favBack;
    @BindView(R.id.fav_add)
    ImageButton favAdd;
    @BindView(R.id.toolbar_fav)
    Toolbar toolbarFav;
    @BindView(R.id.fav_lv)
    RecyclerView favLv;
    @BindView(R.id.fav_progress_bar)
    ProgressBar favProgressBar;
    @BindView(R.id.fav_empty)
    CustomRegularTextView favEmpty;
    private ArrayList<Favourites> favouritesArrayList;
    private FavouritesAdapter favouritesAdapter;
    private AutoCompleteTextView addFavAddress;
    LatLng fav_latLng;
    String addFav_name = "";
    TextView addFavName, addFav_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_places);
        ButterKnife.bind(this);
        favouritesArrayList = new ArrayList<>();
        setSupportActionBar(toolbarFav);
        getSupportActionBar().setTitle(null);
        favBack.setOnClickListener(view -> onBackPressed());
        favAdd.setOnClickListener(view -> openAddFavDialog());
        getSavedPlaces();
    }

    private void openAddFavDialog() {
        final Dialog addFavDialog = new Dialog(this, R.style.DialogThemeforview);
        addFavDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addFavDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        addFavDialog.setCancelable(true);
        addFavDialog.setContentView(R.layout.add_fav_layout);
        addFavAddress = addFavDialog.findViewById(R.id.addFav_address);
        addFavName = addFavDialog.findViewById(R.id.addFav_name);
        final PlacesAutoCompleteAdapter favAddressAdapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_text);
        addFavAddress.setAdapter(favAddressAdapter);
        addFavAddress.setOnItemClickListener((adapterView, view, i, l) -> {
            addFavAddress.setSelection(0);
            AndyUtils.hideKeyBoard(getApplicationContext());
            final String selectedDestPlace = favAddressAdapter.getItem(i);
            try {
                getLocationforDest(URLEncoder.encode(addFavAddress.getText().toString(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        });

        addFav_ok = addFavDialog.findViewById(R.id.addFav_ok);
        addFav_ok.setOnClickListener(view -> {
            addFav_name = addFavName.getText().toString();
            if (addFav_name != "") {
                if (fav_latLng != null) {
                    addFav();
                    addFavDialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.enterPlaceAddress), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.enterPlaceNam), Toast.LENGTH_SHORT).show();
            }
        });
        addFavDialog.show();
    }

    private void getLocationforDest(String selectedDestPlace) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.LOCATION_API_BASE + selectedDestPlace + "&key=" + Const.GOOGLE_API_KEY);
        new VollyRequester(this, Const.GET, map, Const.ServiceCode.LOCATION_API_BASE_DESTINATION, this);
    }

    private void addFav() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            return;
        }
        AndyUtils.showSimpleProgressDialog(this, "", false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.ADD_FAV);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());
        map.put(Const.Params.LATITUDE, String.valueOf(fav_latLng.latitude));
        map.put(Const.Params.LONGITUDE, String.valueOf(fav_latLng.longitude));
        map.put(Const.Params.ADDRESS, addFavAddress.getText().toString());
        map.put(Const.Params.FAVOURITE_NAME, addFav_name);
        new VollyRequester(this, Const.POST, map, Const.ServiceCode.ADD_FAV,
                this);
    }

    private void getSavedPlaces() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            return;
        }
        AndyUtils.showSimpleProgressDialog(this, "", false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.GET_SAVED_PLACES);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());
        new VollyRequester(this, Const.POST, map, Const.ServiceCode.GET_SAVED_PLACES, this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {
            case Const.ServiceCode.GET_SAVED_PLACES:
                AndyUtils.removeProgressDialog();
                if (response != null) {
                    try {
                        JSONObject favobj = new JSONObject(response);
                        if (favobj.getString(Const.Params.SUCCESS).equals(Const.Params.TRUE)) {
                            if (favouritesArrayList != null)
                                favouritesArrayList.clear();
                            JSONArray hisArray = favobj.getJSONArray(Const.Params.DATA);
                            if (hisArray.length() > 0) {
                                for (int i = 0; i < hisArray.length(); i++) {
                                    JSONObject obj = hisArray.getJSONObject(i);
                                    Favourites fav = new Favourites();
                                    fav.setFav_Id(obj.getString(Const.Params.ID));
                                    fav.setFav_Address(obj.getString(Const.Params.ADDRESS));
                                    fav.setFav_Latitude(obj.getString(Const.Params.LATITUDE));
                                    fav.setFav_Longitude(obj.getString(Const.Params.LONGITUDE));
                                    fav.setFav_Name(obj.getString(Const.Params.FAVOURITE_NAME));
                                    favouritesArrayList.add(fav);
                                }

                                if (favouritesArrayList != null) {
                                    favouritesAdapter = new FavouritesAdapter(this, favouritesArrayList);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    favLv.setLayoutManager(mLayoutManager);
                                    favLv.setItemAnimator(new DefaultItemAnimator());
                                    favLv.setAdapter(favouritesAdapter);
                                }
                            } else {
                                favEmpty.setVisibility(View.VISIBLE);
                            }

                        } else {
                            favProgressBar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Const.ServiceCode.LOCATION_API_BASE_DESTINATION:
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray(Const.Params.RESULT);
                        JSONObject locObj = jarray.getJSONObject(0);
                        JSONObject geometryOBJ = locObj.optJSONObject(Const.Params.GEOMETRY);
                        JSONObject locationOBJ = geometryOBJ.optJSONObject(Const.Params.LOCATION);
                        double lat = locationOBJ.getDouble(Const.Params.LAT);
                        double lan = locationOBJ.getDouble(Const.Params.LNG);
                        fav_latLng = new LatLng(lat, lan);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Const.ServiceCode.ADD_FAV:
                AndyUtils.removeProgressDialog();
                if (response != null) {
                    try {
                        JSONObject favobj = new JSONObject(response);
                        if (favobj.getString(Const.Params.SUCCESS).equals(Const.Params.TRUE)) {
                            getSavedPlaces();
                            Toast.makeText(this, getString(R.string.addesSuccess), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

}
