package com.nikola.user.ui.Fragment;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Commonutils;
import com.nikola.user.Utils.Const;
import com.nikola.user.Utils.ItemClickSupport;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.listener.AdapterCallback;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.ApiManager.ParserUtils;
import com.nikola.user.network.Location.LocationHelper;
import com.nikola.user.network.Models.AdsList;
import com.nikola.user.network.Models.NearByDrivers;
import com.nikola.user.network.Models.RequestDetail;
import com.nikola.user.network.Models.TaxiTypes;
import com.nikola.user.ui.Adapter.AdsAdapter;
import com.nikola.user.ui.Adapter.TaxiAdapter;
import com.nikola.user.ui.activity.MainActivity;
import com.nikola.user.ui.activity.PaymentsActivity;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.nikola.user.network.ApiManager.APIConsts.RequestStatus.IS_ACCEPTED;
import static com.nikola.user.network.ApiManager.APIConsts.RequestStatus.IS_DRIVER_ARRIVED;
import static com.nikola.user.network.ApiManager.APIConsts.RequestStatus.IS_DRIVER_DEPARTED;
import static com.nikola.user.network.ApiManager.APIConsts.RequestStatus.IS_DRIVER_RATED;
import static com.nikola.user.network.ApiManager.APIConsts.RequestStatus.IS_DRIVER_TRIP_ENDED;
import static com.nikola.user.network.ApiManager.APIConsts.RequestStatus.IS_DRIVER_TRIP_STARTED;
import static com.nikola.user.network.ApiManager.ParserUtils.parseRequestStatus;

/**
 * Created by user on 1/5/2017.
 */

public class HomeFragment extends Fragment implements
        LocationHelper.OnLocationReceived,
        OnMapReadyCallback,
        AdapterCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener
{

    private static final int RC_LOCATION_PERM = 1000 ;
    @BindView(R.id.map_lay)
    RelativeLayout mapLay;
    @BindView(R.id.tv_current_location)
    CustomRegularTextView tvCurrentLocation;
    @BindView(R.id.btn_schedule)
    ImageButton btnSchedule;
    @BindView(R.id.layout_search)
    LinearLayout layoutSearch;
    @BindView(R.id.btn_mylocation)
    ImageButton btnMylocation;
    @BindView(R.id.fab_coffee)
    FloatingActionButton fabCoffee;
    @BindView(R.id.btn_floating_hourly)
    FloatingActionButton btnFloatingHourly;
    @BindView(R.id.btn_floating_airport)
    FloatingActionButton btnFloatingAirport;
    @BindView(R.id.btn_floating_bolt)
    FloatingActionButton btnFloatingBolt;
    @BindView(R.id.imageViewArrow)
    ImageView imageViewArrow;
    @BindView(R.id.recycAds)
    RecyclerView recycAds;
    @BindView(R.id.card_travel)
    CardView cardTravel;
    @BindView(R.id.design_bottom_sheet)
    RelativeLayout designBottomSheet;
    @BindView(R.id.solicitar)
    Button solicitar;
    private String TAG = HomeFragment.class.getSimpleName();
    private GoogleMap googleMap;
    private Bundle mBundle;
    SupportMapFragment HomemapFragment;
    private View view;
    private LocationHelper locHelper;
    private Location myLocation;
    private LatLng latlong;
    private static final int DURATION = 1500;
    static Marker pickup_marker, drop_marker, my_marker, selectMarker;
    MarkerOptions pickup_opt;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String base_price = "", min_price = "", booking_fee = "", currency = "", distance_unit = "", tax_price = "";
    TaxiAdapter taxiAdapter;
    private boolean isPickUpSelected;
    private ArrayList<TaxiTypes> typesList = new ArrayList<>();
    private ArrayList<NearByDrivers> driverslatlngs = new ArrayList<>();
    private HashMap<Marker, Integer> markermap = new HashMap<>();
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private int marker_position;
    private LatLng driverlatlan;
    public static Handler providerhandler;
    public static String pickup_add = "";
    String datetime = "";
    private LatLng sch_pic_latLng, sch_drop_latLng;
    private String taxi_type;
    private AdsAdapter adsAdapter;
    private List<AdsList> adsLists;
    AutoCompleteTextView et_sch_source_address, et_sch_destination_address;
    TextView tv_time_date, tv_estimate_fare, tv_total_dis;
    ProgressBar pbfareProgress;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    Unbinder unbinder;
    RequestDetail requestDetail;
    MainActivity activity;
    Dialog requestDialog;
    RecyclerView lst_sch_vehicle;
    Dialog schedule_dialog;
    PreferenceHelper preferenceHelper;
    int height = 30;
    int width = 30;
    Bitmap smallPingGreen;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_map_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        HomemapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.home_map);
        preferenceHelper = new PreferenceHelper(getActivity());
        if (HomemapFragment != null) {
            HomemapFragment.getMapAsync(this);
        }

        if (latlong != null)
            tvCurrentLocation.setText(getAddressFromLatLng(latlong.latitude, latlong.longitude));
        BitmapDrawable user_pin_green = (BitmapDrawable)getResources().getDrawable(R.drawable.imagen_logo);
        Bitmap b = user_pin_green.getBitmap();
        smallPingGreen = Bitmap.createScaledBitmap(b, width, height, false);
        return view;
    }


    private void rotateArrow(float v) {
        imageViewArrow.setRotation(-180 * v);
    }

    @Override
    public void onMapReady(GoogleMap mgoogleMap) {
        googleMap = mgoogleMap;
        if (googleMap != null) {
            AndyUtils.removeProgressDialog();
            googleMap.setTrafficEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
            googleMap.setOnCameraMoveListener(this);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setOnCameraIdleListener(this);

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                EasyPermissions.requestPermissions(this, getString(R.string.youNeedToGrantPermission),
                        RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION);
                return;
            }

        }
        mgoogleMap.setOnMapClickListener(this);
        googleMap.setOnMyLocationButtonClickListener(() -> {
            tvCurrentLocation.setText(getAddressFromLatLng(latlong.latitude, latlong.longitude));
            try{
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong,
                        16));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        providerhandler = new Handler();
        getServiceTypes(0, 0);
    }

    protected void getServiceTypes(float distance, float duration) {
        Call<String> call;
        if (distance == 0)
            call = apiInterface.getServiceTypes(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                    , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        else
            call = apiInterface.getServiceWithDistance(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                    , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                    , distance
                    , duration
                    , latlong.latitude
                    , latlong.longitude);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject servicesResponse = null;
                try {
                    servicesResponse = new JSONObject(response.body());
                    if (servicesResponse != null) {
                        if (servicesResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                            JSONArray jsonArray = servicesResponse.optJSONArray(APIConsts.Params.DATA);
                            typesList = ParserUtils.ParseServicesList(jsonArray);
                            taxiAdapter = new TaxiAdapter(activity, typesList, null);
                            if (lst_sch_vehicle != null)
                                lst_sch_vehicle.setAdapter(taxiAdapter);
                            taxiAdapter.notifyDataSetChanged();
                        } else {

                        }
                    }
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
        }
        locHelper = new LocationHelper(activity);
        locHelper.setLocationReceivedLister(this);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        startgetProvider();
    }

    @Override
    public void onLocationReceived(final LatLng latlong) {
        if (latlong != null)
            if (tvCurrentLocation.getText().toString().equalsIgnoreCase("")) {
                tvCurrentLocation.setText(getAddressFromLatLng(latlong.latitude, latlong.longitude));
            }
    }

    @Override
    public void onLocationReceived(Location location) {
        if (location != null) {
            myLocation = location;
            LatLng latLang = new LatLng(location.getLatitude(),
                    location.getLongitude());

            latlong = latLang;
            if (tvCurrentLocation.getText().toString().equalsIgnoreCase("")) {
                tvCurrentLocation.setText(getAddressFromLatLng(latLang.latitude, latLang.longitude));
            }
        }
    }

    private void addOverlay(double latitude, double longitude) {
        if (null != googleMap) {
            GroundOverlay groundOverlay = googleMap.addGroundOverlay(new
                    GroundOverlayOptions()
                    .position(new LatLng(latitude, longitude), 100)
                    .transparency(0.5f)
                    .zIndex(3)
                    .image(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(activity.getResources().getDrawable(R.drawable.map_overlay)))));
            startOverlayAnimation(groundOverlay);
        }
    }

    private void startOverlayAnimation(final GroundOverlay groundOverlay) {
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator vAnimator = ValueAnimator.ofInt(0, 200);
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);
        vAnimator.setInterpolator(new LinearInterpolator());
        vAnimator.addUpdateListener(valueAnimator -> {
            final Integer val = (Integer) valueAnimator.getAnimatedValue();
            groundOverlay.setDimensions(val);
        });
        ValueAnimator tAnimator = ValueAnimator.ofFloat(0, 1);
        tAnimator.setRepeatCount(ValueAnimator.INFINITE);
        tAnimator.setRepeatMode(ValueAnimator.RESTART);
        tAnimator.setInterpolator(new LinearInterpolator());
        tAnimator.addUpdateListener(valueAnimator -> {
            Float val = (Float) valueAnimator.getAnimatedValue();
            groundOverlay.setTransparency(val);
        });
        animatorSet.setDuration(3000);
        animatorSet.playTogether(vAnimator, tAnimator);
        animatorSet.start();
    }


    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    protected void getAvailableProviders(LatLng latLng) {
        Call<String> call = apiInterface.getAllAvailableProviders(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , latLng.latitude
                , latLng.longitude
                , prefUtils.getStringValue(PrefKeys.REQUEST_TYPE, "")); //TODO Add in login
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                driverslatlngs.clear();
                JSONObject setMarkersResponse = null;
                try {
                    setMarkersResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (setMarkersResponse != null) {
                    if (setMarkersResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONArray jsonArray = setMarkersResponse.optJSONArray(APIConsts.Params.DATA);
                        driverslatlngs = ParserUtils.ParseAvailableProviderList(jsonArray);
                        if (googleMap != null) {
                            googleMap.getUiSettings().setScrollGesturesEnabled(true);
                        }
                        setUpMarkers();
                    } else {
                        UiUtils.showShortToast(activity, setMarkersResponse.optString(APIConsts.Params.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private void setUpMarkers() {
        if (driverslatlngs.size() > 0) {
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
            for (int i = 0; i < driverslatlngs.size(); i++) {
                final MarkerOptions currentOption = new MarkerOptions();
                currentOption.position(driverslatlngs.get(i).getLatlan());
                currentOption.title(driverslatlngs.get(i).getDriver_name());
                currentOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_booking_lux_map_topview));
                if (googleMap != null) {
                    final Marker[] driver_marker = new Marker[1];
                    final int finalI = i;
                    activity.runOnUiThread(() -> {
                        if (null == driver_marker[0]) {
                            driver_marker[0] = googleMap.addMarker(currentOption);

                        } else {
                            driver_marker[0].setPosition(driverslatlngs.get(finalI).getLatlan());
                        }

                    });
                    markers.add(driver_marker[0]);
                    markermap.put(driver_marker[0], i);

                }
            }
        } else {
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
        }
    }

    @Override
    public void onConntected(Bundle bundle) {
    }

    @Override
    public void onConntected(Location location) {
        if (location != null && null != googleMap) {
            final LatLng currentlatLang = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatLang,
                    16));
            BaseMapFragment.pic_latlan = currentlatLang;
            pickup_add = getAddressFromLatLng(currentlatLang.latitude, currentlatLang.longitude);
            addOverlay(currentlatLang.latitude, currentlatLang.longitude);
            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.position(currentlatLang);
            markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_map));
            Marker locMark = googleMap.addMarker(markerOpt);
            markermap.put(locMark, -2);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showshcdule(String pickup_add, final ArrayList<TaxiTypes> typesList) {
        sch_drop_latLng = null;
        schedule_dialog = new Dialog(activity, R.style.DialogSlideAnim);
        if (!schedule_dialog.isShowing()) {
            schedule_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            schedule_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            schedule_dialog.setCancelable(true);
            schedule_dialog.setContentView(R.layout.schedule_layout);
            et_sch_source_address = schedule_dialog.findViewById(R.id.et_sch_source_address);
            et_sch_destination_address = schedule_dialog.findViewById(R.id.et_sch_destination_address);
            et_sch_source_address.setText(pickup_add);
            tv_time_date = schedule_dialog.findViewById(R.id.tv_time_date);
            sch_pic_latLng = getLatLngFrmAddress(et_sch_source_address.getText().toString());
            et_sch_source_address.setOnTouchListener((v, event) -> {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    isPickUpSelected = true;
                    if (!Places.isInitialized()) {
                        Places.initialize(activity.getApplicationContext(), Const.PLACES_AUTOCOMPLETE_API_KEY);
                    }
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(activity);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                }
                return true;
            });
            et_sch_destination_address.setOnTouchListener((v, event) -> {
                isPickUpSelected = false;
                try {
                    if (MotionEvent.ACTION_UP == event.getAction()) {
                        if (!Places.isInitialized()) {
                            Places.initialize(activity.getApplicationContext(), Const.PLACES_AUTOCOMPLETE_API_KEY);
                        }
                        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                        Intent intent = new Autocomplete.IntentBuilder(
                                AutocompleteActivityMode.FULLSCREEN, fields)
                                .build(activity);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            });
            TextView btn_sch_submit = schedule_dialog.findViewById(R.id.btn_sch_submit);
            lst_sch_vehicle = schedule_dialog.findViewById(R.id.lst_sch_vehicle);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            lst_sch_vehicle.setLayoutManager(mLayoutManager);
            lst_sch_vehicle.setItemAnimator(new DefaultItemAnimator());
            lst_sch_vehicle.addItemDecoration(new SpacesItemDecoration(18));
            if (typesList != null) {
                taxiAdapter = new TaxiAdapter(activity, typesList, this);
                lst_sch_vehicle.setAdapter(taxiAdapter);
                if (typesList.size() > 0) {
                    taxi_type = typesList.get(0).getId();
                }

            }
            btn_sch_submit.setOnClickListener(view -> {
                if (tv_time_date.getText().toString().length() == 0) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_error_date_time), activity);
                } else if (et_sch_destination_address.getText().toString().length() == 0) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_destination_error), activity);
                } else {
                    schedule_dialog.dismiss();
                    scheduleARide(sch_drop_latLng, sch_pic_latLng, taxi_type, tv_time_date.getText().toString(), et_sch_source_address.getText().toString(), et_sch_destination_address.getText().toString());
                }
            });
            ItemClickSupport.addTo(lst_sch_vehicle)
                    .setOnItemClickListener((recyclerView, position, v) -> {
                        // do it
                        try{
                            taxi_type = typesList.get(position).getId();
                            taxiAdapter.OnItemClicked(position);
                            taxiAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
            tv_time_date.setOnClickListener(view -> DatePicker());
            schedule_dialog.show();
        }
    }

    private LatLng getLatLngFrmAddress(String address) {
        LatLng latLng = null;
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
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

    protected void scheduleARide(LatLng destLatLng, LatLng pickLatLng, String type, String datetime, String sAddress, String dAddress) {
        Call<String> call = apiInterface.scheduleALaterRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , type
                , sAddress
                , dAddress
                , pickLatLng.latitude
                , pickLatLng.longitude
                , destLatLng.latitude
                , destLatLng.longitude
                , 1
                , datetime
                , RequestMapFragment.estimatedFare
                , prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                driverslatlngs.clear();
                JSONObject laterRequestResponse = null;
                try {
                    laterRequestResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (laterRequestResponse != null) {
                    if (laterRequestResponse.optString(Const.Params.SUCCESS).equalsIgnoreCase(APIConsts.Constants.TRUE)) {
                        UiUtils.showShortToast(activity, laterRequestResponse.optString(APIConsts.Params.MESSAGE));
                        JSONObject data = laterRequestResponse.optJSONObject(APIConsts.Params.DATA);
                        ScheduleDetailBottomSheet scheduleDetailBottomSheet = new ScheduleDetailBottomSheet();
                        scheduleDetailBottomSheet.setRequestId(data.optString(APIConsts.Params.REQUEST_ID));
                        scheduleDetailBottomSheet.show(activity.getSupportFragmentManager(), scheduleDetailBottomSheet.getTag());
                    } else {
                        UiUtils.showShortToast(activity, laterRequestResponse.optString(APIConsts.Params.ERROR));
                        if (laterRequestResponse.optInt(APIConsts.Params.ERROR_CODE) == APIConsts.ErrorCodes.REDIRECT_PAYMENTS) {
                            startActivity(new Intent(activity, PaymentsActivity.class));
                        }
                    }
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

    private void DatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        dpd = new DatePickerDialog(getActivity(), R.style.datepicker,
                (view, year, monthOfYear, dayOfMonth) -> {
                    if (view.isShown()) {
                        datetime = year + "-"
                                + (monthOfYear + 1) + "-"
                                + dayOfMonth;
                        TimePicker();
                        dpd.dismiss();
                    }
                }, mYear, mMonth, mDay);
        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                (dialog, which) -> dpd.dismiss());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        dpd.getDatePicker().setMaxDate(cal.getTimeInMillis());
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        dpd.getDatePicker().setMinDate(cal.getTimeInMillis());
        dpd.show();
    }

    public void TimePicker() {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 30);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        tpd = new TimePickerDialog(getActivity(), R.style.datepicker,
                (view, hourOfDay, minute) -> {
                    if (view.isShown()) {
                        tpd.dismiss();
                        datetime = datetime.concat(" "
                                + hourOfDay + ":"
                                + minute + ":" + "00");
                        tv_time_date.setText(datetime);
                    }
                }, mHour, mMinute, false);
        tpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                (dialog, which) -> tpd.dismiss());
        tpd.show();

    }

    @Override
    public void onMethodCallback(String id, String taxitype, String taxi_price_distance, String taxi_price_min, String taxiimage, String taxi_seats, String basefare) {
        taxi_type = id;
        if (null != sch_pic_latLng && null != sch_drop_latLng) {
            getDistanceAndDuration(sch_pic_latLng, sch_drop_latLng, true);
            showfareestimate(taxitype, taxi_price_distance, taxi_price_min, taxiimage, taxi_seats, basefare);
        } else {
            AndyUtils.showShortToast(getResources().getString(R.string.txt_drop_pick_error), activity);
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {


    }


    @Override
    public void onCameraIdle() {
        if(null == selectMarker && null != googleMap)
        {
            selectMarker = googleMap.addMarker(new MarkerOptions().position(googleMap.getCameraPosition().target)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallPingGreen)));
        }
        if(null != googleMap){
            googleMap.clear();
            setUpMarkers();
            selectMarker = googleMap.addMarker(new MarkerOptions().position(googleMap.getCameraPosition().target)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallPingGreen)));
            LatLng position = selectMarker.getPosition();
            tvCurrentLocation.setText(getAddressFromLatLng(position.latitude, position.longitude));
            solicitar.setVisibility(View.VISIBLE);
            layoutSearch.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onCameraMove() {

        if(null != selectMarker && null !=googleMap) {
            selectMarker.setPosition(googleMap.getCameraPosition().target);
        }

    }

    @OnClick({R.id.tv_current_location, R.id.btn_schedule, R.id.btn_mylocation, R.id.btn_floating_hourly, R.id.btn_floating_airport, R.id.btn_floating_bolt, R.id.solicitar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.solicitar:
                if (!tvCurrentLocation.getText().toString().equalsIgnoreCase("")){
                    BaseMapFragment.pic_latlan =new LatLng(selectMarker.getPosition().latitude,selectMarker.getPosition().longitude);
                    SearchPlaceFragment searcfragment = new SearchPlaceFragment();
                    searcfragment.setSourecAndDestination(tvCurrentLocation.getText().toString());
                    activity.addFragment(searcfragment, false, Const.SEARCH_FRAGMENT, true);
                } else {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        EasyPermissions.requestPermissions(this, getString(R.string.youNeedToGrantPermission),
                                RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION);
                        return;
                    }
                }
                break;
            case R.id.btn_mylocation:
                if (googleMap != null && latlong != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
                    tvCurrentLocation.setText(getAddressFromLatLng(latlong.latitude, latlong.longitude));

                }
                break;
            case R.id.btn_schedule:
                if (null != pickup_add && null != typesList) {
                    showshcdule(pickup_add, typesList);
                } else {
                    AndyUtils.showShortToast(getResources().getString(R.string.txt_error), activity);
                }
                break;
            case R.id.btn_floating_hourly:
                HourlyBookngFragment hourlyfragment = new HourlyBookngFragment();
                Bundle nbundle = new Bundle();
                nbundle.putString("pickup_address", pickup_add);
                hourlyfragment.setArguments(nbundle);
                activity.addFragment(hourlyfragment, false, Const.HOURLY_FRAGMENT, true);
                break;
            case R.id.btn_floating_airport:
                activity.addFragment(new AirportBookingFragment(), false, Const.AIRPORT_FRAGMENT, true);
                break;
            case R.id.btn_floating_bolt:
                activity.addFragment(new MessageFragment(), false, Const.BOLT_FRAGMENT, true);
                break;
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        activity.currentFragment = Const.HOME_MAP_FRAGMENT;
    }

    @Override
    public void onStop() {
        super.onStop();
        stopCheckingforproviders();
    }

    private void startgetProvider() {
        startCheckProviderTimer();
    }

    private void startCheckProviderTimer() {
        providerhandler.postDelayed(runnable, 5000);
    }

    private void stopCheckingforproviders() {
        if (providerhandler != null) {
            providerhandler.removeCallbacks(runnable);
        }
    }

    Runnable runnable = new Runnable() {
        public void run() {
            if (latlong != null)
                getAvailableProviders(latlong);
            requestStatusCheck();
            providerhandler.postDelayed(this, 5000);
        }
    };

    protected void requestStatusCheck() {
        Call<String> call = apiInterface.pingRequestStatusCheck(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject requestStatusResponse = null;
                try {
                    requestStatusResponse = new JSONObject(response.body());
                    if (requestStatusResponse != null) {
                        if (requestStatusResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                            requestDetail = new RequestDetail();
                            requestDetail = parseRequestStatus(response.body());
                            preferenceHelper.putModel(requestDetail);
                            Bundle bundle = new Bundle();
                            OngoingTripFragment travalfragment = new OngoingTripFragment();
                            if (requestDetail.getTripStatus() == Const.NO_REQUEST) {
                                if (requestDialog != null)
                                    if (requestDialog.isShowing()) {
                                        requestDialog.cancel();
//                                stopCheckingforproviders();
                                    }
                            } else if (requestDetail.getTripStatus() == IS_ACCEPTED || requestDetail.getTripStatus() == IS_DRIVER_DEPARTED || requestDetail.getTripStatus() == IS_DRIVER_ARRIVED ||
                                    requestDetail.getTripStatus() == IS_DRIVER_TRIP_STARTED || requestDetail.getTripStatus() == IS_DRIVER_TRIP_ENDED || requestDetail.getTripStatus() == IS_DRIVER_RATED) {
                                bundle.putSerializable(Const.REQUEST_DETAIL, requestDetail);
                                bundle.putInt(Const.DRIVER_STATUS, Const.IS_ACCEPTED);
                                preferenceHelper.putIs_Ongoing(false);
                                preferenceHelper.putModel(requestDetail);
                                if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                    stopCheckingforproviders();
                                    travalfragment.setArguments(bundle);
                                    activity.addFragment(travalfragment, false, Const.TRAVEL_MAP_FRAGMENT, true);
                                }
                                BaseMapFragment.drop_latlan = null;
                                BaseMapFragment.pic_latlan = null;
                                BaseMapFragment.s_address = "";
                                BaseMapFragment.d_address = "";
                            } else if (requestDetail.getTripStatus() == 0 && requestDetail.getDriverStatus() == 1) {
                                layoutSearch.setVisibility(View.GONE);
                                designBottomSheet.setVisibility(View.GONE);
                                showRequestDialog();
                            }
                        } else {
                            UiUtils.showShortToast(activity, requestStatusResponse.optString(APIConsts.Params.ERROR));
                        }
                    }
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


    protected void getFareCalculation(double distance, String duration, String serviceId) {
        Call<String> call = apiInterface.calculateFare(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , distance
                , duration
                , serviceId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject fareResponse = null;
                try {
                    fareResponse = new JSONObject(response.body());
                    if (fareResponse != null) {
                        if (fareResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                            JSONObject data = fareResponse.optJSONObject(APIConsts.Params.DATA);
                            String fare = data.optString("estimated_fare_formatted");
                            tax_price = data.optString("tax_price");
                            base_price = data.optString("base_price");
                            min_price = data.optString("min_fare");
                            booking_fee = data.optString("booking_fee");
                            currency = data.optString("currency");
                            distance_unit = data.optString("distance_unit");
                            if (tv_estimate_fare != null) {
                                tv_estimate_fare.setVisibility(View.VISIBLE);
                                tv_estimate_fare.setText(fare);
                            }
                            if (pbfareProgress != null) {
                                pbfareProgress.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        UiUtils.showShortToast(activity, fareResponse.optString(APIConsts.Params.ERROR));
                    }
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


    protected void getDistanceAndDuration(LatLng picLatLng, LatLng dropLatLng, boolean isFareCalculation) {
        String url = APIConsts.Apis.DISTANCE_LOCATION_API + picLatLng.latitude + "," + picLatLng.longitude + "&destinations=" + dropLatLng.latitude + "," + dropLatLng.longitude +
                "&mode=drving&language=en-EN&key=" + APIConsts.Constants.GOOGLE_API_KEY + "&sensor=false";
        Call<String> call = apiInterface.getLocationBasedResponse(url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject fareResponse = null;
                try {
                    fareResponse = new JSONObject(response.body());
                    if (fareResponse != null) {
                        fareResponse.optString(APIConsts.Constants.STATUS).equals(APIConsts.Constants.OK);
                        JSONArray sourceArray = fareResponse.optJSONArray("origin_addresses");
                        String sourceObject = (String) sourceArray.opt(0);
                        JSONArray destinationArray = fareResponse.optJSONArray("destination_addresses");
                        String destinationObject = (String) destinationArray.opt(0);
                        JSONArray jsonArray = fareResponse.optJSONArray("rows");
                        JSONObject elementsObject = jsonArray.optJSONObject(0);
                        JSONArray elementsArray = elementsObject.optJSONArray("elements");
                        JSONObject distanceObject = elementsArray.optJSONObject(0);
                        JSONObject dObject = distanceObject.optJSONObject("distance");
                        String distance = dObject.optString("text");
                        JSONObject durationObject = distanceObject.optJSONObject("duration");
                        String duration = durationObject.optString("text");
                        String dis = dObject.optString("value");
                        int dur = durationObject.optInt("value");
                        double trip_dis = Integer.valueOf(dis) * 0.001;
                        if (isFareCalculation) {
                            getFareCalculation(trip_dis, String.valueOf(dur), taxi_type);
                            if (tv_total_dis != null)
                                tv_total_dis.setText(distance);
                        } else
                            getServiceTypes((float) trip_dis, dur);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UiUtils.showLongToast(getActivity(), getString(R.string.cannotScheduleRide));
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

    public float getDistanceBetween(LatLng picUpLatLng, LatLng dropLatLng) {
        Location loc1 = new Location("");
        loc1.setLatitude(picUpLatLng.latitude);
        loc1.setLongitude(picUpLatLng.longitude);
        Location loc2 = new Location("");
        loc2.setLatitude(dropLatLng.latitude);
        loc2.setLongitude(dropLatLng.latitude);
        float distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment f = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.home_map);
        if (f != null) {
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ViewGroup mContainer = getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
        googleMap = null;
        stopCheckingforproviders();
    }

    @Override
    public void onDestroy() {
        stopCheckingforproviders();
        super.onDestroy();
    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0)).append("\n");
                result.append(address.getCountryName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Place place = Autocomplete.getPlaceFromIntent(data);
                activity.runOnUiThread(() -> {
                    if (isPickUpSelected) {
                        et_sch_source_address.setText(place.getAddress());
                        sch_pic_latLng = place.getLatLng();
                    } else {
                        et_sch_destination_address.setText(place.getAddress());
                        sch_drop_latLng = place.getLatLng();
                    }
                    if (null != sch_drop_latLng && null != sch_pic_latLng) {
                        getDistanceAndDuration(sch_pic_latLng, sch_drop_latLng, false);
                    }
                });
            }
        }

    }

    private void showfareestimate(String taxitype, final String taxi_price_distance, final String taxi_price_min, String taxiimage, String taxi_seats, final String taxi_cost) {
        final Dialog faredialog = new Dialog(activity, R.style.DialogSlideAnim);
        faredialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        faredialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        faredialog.setCancelable(false);
        faredialog.setContentView(R.layout.fare_popup);
        ImageView type_img = faredialog.findViewById(R.id.fare_taxi_img);
        TextView tv_fare_taxi_name = faredialog.findViewById(R.id.tv_fare_taxi_name);
        tv_estimate_fare = faredialog.findViewById(R.id.tv_estimate_fare);
        tv_total_dis = faredialog.findViewById(R.id.tv_total_dis);
        pbfareProgress = faredialog.findViewById(R.id.pbfareProgress);
        TextView tv_total_capcity = faredialog.findViewById(R.id.tv_total_capcity);
        TextView fare_done = faredialog.findViewById(R.id.fare_done);
        ImageView btn_info = faredialog.findViewById(R.id.btn_info);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.frontal_taxi_cab);
        requestOptions.error(R.drawable.frontal_taxi_cab);
        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(taxiimage).into(type_img);
        tv_fare_taxi_name.setText(taxitype);
        tv_total_capcity.setText("1-" + taxi_seats);
        btn_info.setOnClickListener(view -> showfarebreakdown(base_price, taxi_price_distance, taxi_price_min, min_price, booking_fee, currency, distance_unit));
        fare_done.setOnClickListener(view -> faredialog.dismiss());
        faredialog.show();
    }

    private void showfarebreakdown(String base_price, String taxi_price_distance, String taxi_price_min, String min_price, String booking_fee, String currency, String distance_unit) {
        final Dialog farebreak = new Dialog(activity, R.style.DialogSlideAnim_leftright_Fullscreen);
        farebreak.requestWindowFeature(Window.FEATURE_NO_TITLE);
        farebreak.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        farebreak.setCancelable(true);
        farebreak.setContentView(R.layout.fare_breakdown);
        TextView tv_dis_title = farebreak.findViewById(R.id.tv_dis_title);
        TextView tv_base_fare = farebreak.findViewById(R.id.tv_base_fare);
        TextView tv_min_fare = farebreak.findViewById(R.id.tv_min_fare);
        TextView tv_per_min_cost = farebreak.findViewById(R.id.tv_per_min_cost);
        TextView tv_per_km_price = farebreak.findViewById(R.id.tv_per_km_price);
        TextView tv_service_tax_price = farebreak.findViewById(R.id.tv_service_tax_price);
        TextView tv_booking_price = farebreak.findViewById(R.id.tv_booking_price);
        tv_base_fare.setText(currency + base_price);
        tv_booking_price.setText(currency + booking_fee);
        tv_min_fare.setText(currency + min_price);
        tv_per_min_cost.setText(currency + taxi_price_min);
        tv_per_km_price.setText(currency + taxi_price_distance);
        tv_service_tax_price.setText(currency + tax_price);
        ImageView close_popup = farebreak.findViewById(R.id.close_popup);
        tv_dis_title.setText(getResources().getString(R.string.txt_per) + " " + distance_unit);
        close_popup.setOnClickListener(view -> farebreak.cancel());
        farebreak.show();
    }

    private void showRequestDialog() {
        if(requestDialog != null)
        if (requestDialog.isShowing())
            return;
        requestDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        requestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        requestDialog.setCancelable(false);
        requestDialog.setContentView(R.layout.request_loading);
        final RippleBackground rippleBackground = requestDialog.findViewById(R.id.content);
        TextView cancel_req_create = requestDialog.findViewById(R.id.cancel_req_create);
        final TextView req_status = requestDialog.findViewById(R.id.req_status);
        final TextView requestType = requestDialog.findViewById(R.id.requestType);
        requestType.setVisibility(View.GONE);
        rippleBackground.startRippleAnimation();
        cancel_req_create.setOnClickListener(view -> {
            req_status.setText(getResources().getString(R.string.txt_canceling_req));
            cancelCreateRequest();
            new PreferenceHelper(activity).clearRequestData();
            startgetProvider();
        });
        requestDialog.show();
}

    protected void cancelCreateRequest() {
        Call<String> call = apiInterface.cancelRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject cancelResponse = null;
                try {
                    cancelResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("mahi", "cancel req_response" + response);
                if (requestDialog != null && requestDialog.isShowing()) {
                    requestDialog.dismiss();
                    layoutSearch.setVisibility(View.VISIBLE);
                    designBottomSheet.setVisibility(View.VISIBLE);
                } else {
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

}
