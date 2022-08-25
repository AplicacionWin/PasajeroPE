package com.nikola.user.ui.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.Location.LocationHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by user on 1/7/2017.
 */

public class SearchPlaceFragment extends BaseMapFragment implements
        View.OnClickListener,
        LocationHelper.OnLocationReceived,
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener
{

    public static AutoCompleteTextView etSourceAddress;
    public static AutoCompleteTextView etStopAddress;
    public static AutoCompleteTextView etDestinationAddress;
    public static Button btnSearch;
    //@BindView(R.id.addStop)
    //ImageView addStop;
    @BindView(R.id.stopLayout)
    LinearLayout stopLayout;
    //    @BindView(R.id.sourceFavIcon)
//    ImageView sourceFavIcon;
//    @BindView(R.id.stopFavIcon)
//    ImageView stopFavIcon;
//    @BindView(R.id.destFavIcon)
//    ImageView destFavIcon;
    private GoogleMap gMap;
    private LocationHelper locHelper;
    private LatLng currentLatLan;
    private Bundle mBundle;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private boolean s_click = false, d_click = false, stop_click = false;
    String sourceAddress, destAddress, stopAddress;
    public static LatLng des_latLng, sourceLatLng, stop_latLng;
    SupportMapFragment search_place_map;
    private GoogleMap googleMap;
    private Marker PickUpMarker, DropMarker, StopMarker, originMarker, destinationMarker;
    Unbinder unbinder;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    Marker marker;

    int height = 30;
    int width = 30;
    Bitmap smallPingGreen;
    Bitmap smallPingRed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locHelper = new LocationHelper(activity);
        locHelper.setLocationReceivedLister(this);
        mBundle = savedInstanceState;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(activity);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
        }
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.source_destination_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        etSourceAddress = view.findViewById(R.id.et_source_address);
        etDestinationAddress = view.findViewById(R.id.et_destination_address);
        etDestinationAddress.requestFocus();
        etStopAddress = view.findViewById(R.id.et_stop_address);
        btnSearch = view.findViewById(R.id.btn_search);
        search_place_map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.search_place_map);
        if (null != search_place_map) {
            search_place_map.getMapAsync(this);
        }
        //addStop.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.add));
        btnSearch.setEnabled(false);
        btnSearch.setBackgroundColor(getResources().getColor(R.color.light_grey));
        etSourceAddress.setText(sourceAddress);
        setOnTouchListener();
        setSourecAndDestination("");
        setOnLongClickLis();
        BitmapDrawable user_pin_green = (BitmapDrawable)getResources().getDrawable(R.drawable.imagen_logo);
        Bitmap b = user_pin_green.getBitmap();
        smallPingGreen = Bitmap.createScaledBitmap(b, width, height, false);

        BitmapDrawable user_pin_red = (BitmapDrawable)getResources().getDrawable(R.drawable.flag);
        Bitmap r = user_pin_red.getBitmap();
        smallPingRed = Bitmap.createScaledBitmap(r, width, height, false);

        return view;
    }

    private void setOnLongClickLis() {
        etSourceAddress.setOnLongClickListener(view -> {
            etSourceAddress.setText("");
            etSourceAddress.requestFocus();
            return false;
        });
        etDestinationAddress.setOnLongClickListener(view1 -> {
            etDestinationAddress.setText("");
            etDestinationAddress.requestFocus();
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener() {
        etSourceAddress.setOnTouchListener((v, event) -> {
            etSourceAddress.requestFocus();

            if(null != originMarker)
            {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(originMarker.getPosition().latitude,originMarker.getPosition().longitude))
                        .zoom(16).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            if(null == originMarker)
            {

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(BaseMapFragment.pic_latlan)
                        .zoom(16).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            if (MotionEvent.ACTION_UP == event.getAction()) {
                d_click = false;
                s_click = true;
                stop_click = false;
                if (!Places.isInitialized()) {
                    Places.initialize(activity.getApplicationContext(), Const.PLACES_AUTOCOMPLETE_API_KEY);
                }
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .setCountry("EC")
                        .build(activity);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//                SearchLocationDialog searchLocationDialog = new SearchLocationDialog(s_click, d_click, stop_click);
//                searchLocationDialog.show(getActivity().getSupportFragmentManager(), searchLocationDialog.getTag());
            }
            return true; // return is important...
        });
        etStopAddress.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_UP == event.getAction()) {
                d_click = false;
                s_click = false;
                stop_click = true;
                if (!Places.isInitialized()) {
                    Places.initialize(activity.getApplicationContext(), Const.PLACES_AUTOCOMPLETE_API_KEY);
                }
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .setCountry("EC")
                        .build(activity);

                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//                SearchLocationDialog searchLocationDialog = new SearchLocationDialog(s_click, d_click, stop_click);
//                searchLocationDialog.show(getActivity().getSupportFragmentManager(), searchLocationDialog.getTag());
            }
            return true;
        });
        etDestinationAddress.setOnTouchListener((v, event) -> {
            etDestinationAddress.requestFocus();
            if(null != destinationMarker)
            {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(destinationMarker.getPosition().latitude,destinationMarker.getPosition().longitude))
                        .zoom(16).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            if (MotionEvent.ACTION_UP == event.getAction()) {
                d_click = true;
                s_click = false;
                stop_click = false;
                if (!Places.isInitialized())
                    Places.initialize(activity.getApplicationContext(), Const.PLACES_AUTOCOMPLETE_API_KEY);
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .setCountry("EC")
                        .build(activity);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//                SearchLocationDialog searchLocationDialog = new SearchLocationDialog(s_click, d_click, stop_click);
//                searchLocationDialog.show(getActivity().getSupportFragmentManager(), searchLocationDialog.getTag());
            }
            return true;
        });
    }

    public void setSourecAndDestination(String pickUpAddress) {
        sourceAddress = pickUpAddress;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    final Place place = Autocomplete.getPlaceFromIntent(data);
                    activity.runOnUiThread(() -> {
                        if (s_click == true) {
                            getAddressSuggestions(place.getLatLng());
                            sourceAddress = String.valueOf(place.getAddress());
                            sourceLatLng = place.getLatLng();
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(sourceLatLng)
                                    .zoom(16).build();
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            originMarker.setPosition(sourceLatLng);
                        } else if (d_click == true) {
                            getAddressSuggestions(place.getLatLng());
                            destAddress = String.valueOf(place.getAddress());
                            des_latLng = place.getLatLng();
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(des_latLng)
                                    .zoom(16).build();
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            destinationMarker.setPosition(des_latLng);
                        } else if (stop_click == true) {
                            getAddressSuggestions(place.getLatLng());
                            stopAddress = String.valueOf(place.getAddress());
                            stop_latLng = place.getLatLng();
                        }
                    });
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i("asher", status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void getAddressSuggestions(LatLng latLng) {
        String url = Const.GEO_DEST + latLng.latitude + "," + latLng.longitude + "&key=" + APIConsts.Constants.GOOGLE_API_KEY;
        Call<String> call = apiInterface.getLocationBasedResponse(url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject fareResponse = null;
                try {
                    fareResponse = new JSONObject(response.body());
                    JSONArray jarray = fareResponse.optJSONArray("results");
                    final JSONObject locObj = jarray.optJSONObject(0);
                    activity.runOnUiThread(() -> {
                        if (s_click == true) {
                            etSourceAddress.setText(sourceAddress);
                            BaseMapFragment.pic_latlan = sourceLatLng;

                        } else if (d_click == true) {
                            BaseMapFragment.d_address = destAddress;
                            etDestinationAddress.setText("");
                            etDestinationAddress.append(destAddress);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(des_latLng,
                                    16));
                            if (null != getActivity() && isAdded()) {
                                btnSearch.setEnabled(true);
                                btnSearch.setBackgroundColor(getResources().getColor(R.color.black));
                            }

                        } else if (stop_click == true) {
                            BaseMapFragment.stop_address = stopAddress;
                            etStopAddress.setText("");
                            etStopAddress.append(stopAddress);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stop_latLng,
                                    16));
                            if (StopMarker == null) {
                                MarkerOptions markerOpt = new MarkerOptions();
                                markerOpt.position(stop_latLng);
                                markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_stop));
                                StopMarker = googleMap.addMarker(markerOpt);
                            } else {
                                StopMarker.setPosition(stop_latLng);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    @Override
    public void onCameraIdle() {
        if(etDestinationAddress.isFocused())
        {
            if(null == destinationMarker)
            {
                destinationMarker = googleMap.addMarker(new MarkerOptions().position(googleMap.getCameraPosition().target)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallPingRed)));
            }
            LatLng position = destinationMarker.getPosition();
            etDestinationAddress.setText(getAddressFromLatLng(position.latitude, position.longitude));
            des_latLng = position;
            BaseMapFragment.d_address = getAddressFromLatLng(des_latLng.latitude,des_latLng.longitude);
            if(null != destinationMarker)
            {
                enableSearchbtn();
            }
        }
        else if(etSourceAddress.isFocused())
        {
            if(null == originMarker)
            {
                originMarker = googleMap.addMarker(new MarkerOptions().position(googleMap.getCameraPosition().target)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallPingGreen)));
            }
            LatLng position = originMarker.getPosition();
            etSourceAddress.setText(getAddressFromLatLng(position.latitude, position.longitude));
            sourceLatLng = position;
            if(null != destinationMarker)
            {
                enableSearchbtn();
            }
        }
    }

    @Override
    public void onCameraMove() {

        if(etDestinationAddress.isFocused() && null != destinationMarker){
            destinationMarker.setPosition(googleMap.getCameraPosition().target);
        }
        else if(etSourceAddress.isFocused() && null != originMarker){
            originMarker.setPosition(googleMap.getCameraPosition().target);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        activity.currentFragment = Const.SEARCH_FRAGMENT;
    }

    @Override
    public void onLocationReceived(LatLng latlong) {
        currentLatLan  = latlong;
    }

    @Override
    public void onLocationReceived(Location location) {

    }

    @Override
    public void onConntected(Bundle bundle) {
    }

    @Override
    public void onConntected(Location location) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gMap = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup mContainer = getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        try {
            googleMap = gMap;
            d_click = true;
            AndyUtils.removeProgressDialog();
            if (googleMap != null) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(true);
                googleMap.setOnCameraIdleListener(this);
                googleMap.setOnCameraMoveListener(this);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(BaseMapFragment.pic_latlan)
                        .zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                if (null != googleMap) {
                    /*MarkerOptions markerOpt = new MarkerOptions();
                    markerOpt.position(BaseMapFragment.pic_latlan);
                    markerOpt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pickup_location));
                    PickUpMarker = googleMap.addMarker(markerOpt);*/

                }
                //googleMap.setOnMapClickListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.search_back, R.id.addStop, R.id.btn_pickLoc, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_back:
                activity.addFragment(new HomeFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                break;
            case R.id.btn_pickLoc:
                if (null != googleMap && currentLatLan != null)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLan, 16));
                break;
            case R.id.btn_search:
                if (des_latLng != null) {
                    BaseMapFragment.drop_latlan = des_latLng;
                    BaseMapFragment.d_address = getAddressFromLatLng(des_latLng.latitude,des_latLng.longitude);
                    if (stop_latLng != null) {
                        Log.e("asher", "stop search map " + stop_latLng);
                        BaseMapFragment.stop_latlan = stop_latLng;
                    }
                    BaseMapFragment.searching = true;
                    AndyUtils.hideKeyBoard(activity);
                    BaseMapFragment.s_address = etSourceAddress.getText().toString();
                    if(null != originMarker)
                    {
                        BaseMapFragment.pic_latlan = originMarker.getPosition();
                    }
                    activity.addFragment(new RequestMapFragment(), false, Const.REQUEST_FRAGMENT, true);
                }
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                break;
        }
    }

    /*@Override
    public void onMapClick(LatLng latLng) {
        if(marker!= null)
        marker.remove();
        MarkerOptions pickup_opt = new MarkerOptions();
        pickup_opt.position(latLng);
        if (s_click) {
            etSourceAddress.setText(getAddressFromLatLng(latLng.latitude, latLng.longitude));
            pickup_opt.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(etSourceAddress.getText().toString())));
            sourceLatLng = latLng;
        } else if (d_click) {
            etDestinationAddress.setText(getAddressFromLatLng(latLng.latitude, latLng.longitude));
            pickup_opt.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(etDestinationAddress.getText().toString())));
            des_latLng = latLng;
            enableSearchbtn();
        } else {
            etStopAddress.setText(getAddressFromLatLng(latLng.latitude, latLng.longitude));
            pickup_opt.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(etStopAddress.getText().toString())));
            stop_latLng = latLng;
        }
        marker = googleMap.addMarker(pickup_opt);
    }*/

    private void enableSearchbtn()
    {
        btnSearch.setEnabled(true);
        btnSearch.setBackgroundColor(getResources().getColor(R.color.black));
    }


    private Bitmap getMarkerBitmapFromView(String place) {
        View customMarkerView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.info_window_pickup, null);
        TextView markertext = (TextView) customMarkerView.findViewById(R.id.txt_pickup_location);
        markertext.setText(place);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}