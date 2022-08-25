package com.nikola.user.ui.Fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Const;
import com.nikola.user.Utils.ItemClickSupport;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.listener.AdapterCallback;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.ApiManager.ParserUtils;
import com.nikola.user.network.ApiManager.VollyRequester;
import com.nikola.user.network.Location.LocationHelper;
import com.nikola.user.network.Models.NearByDrivers;
import com.nikola.user.network.Models.Payments;
import com.nikola.user.network.Models.RequestDetail;
import com.nikola.user.network.Models.TaxiTypes;
import com.nikola.user.ui.Adapter.PaymentModeAdapter;
import com.nikola.user.ui.Adapter.TaxiAdapter;
import com.nikola.user.ui.activity.PaymentsActivity;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import static com.facebook.FacebookSdk.getApplicationContext;
import static com.nikola.user.network.ApiManager.APIConsts.RequestStatus.IS_ACCEPTED;
import static com.nikola.user.network.ApiManager.ParserUtils.parseRequestStatus;


/**
 * Created by user on 2/3/2017.
 */

public class RequestMapFragment extends BaseMapFragment implements LocationHelper.OnLocationReceived, OnMapReadyCallback, AdapterCallback,
        PaymentModeAdapter.WalletListener {

    @BindView(R.id.lst_vehicle)
    RecyclerView lstVehicle;
    @BindView(R.id.load_progress)
    ProgressBar loadProgress;
    @BindView(R.id.instruction_lay)
    LinearLayout instructionLay;
    @BindView(R.id.v_)
    View v;
    public static CustomRegularTextView tvCashtype;
    @BindView(R.id.tv_no_seats)
    CustomRegularTextView tvNoSeats;
    @BindView(R.id.lay_payment)
    RelativeLayout layPayment;
    @BindView(R.id.tv_promocode)
    CustomRegularTextView tvPromocode;
    @BindView(R.id.promo_layout)
    LinearLayout promoLayout;
    public static CustomRegularTextView btnRequestCab;
    @BindView(R.id.map_lay)
    RelativeLayout mapLay;
    @BindView(R.id.btn_mylocation)
    ImageButton btnMylocation;
    @BindView(R.id.schedule_date)
    CustomRegularTextView scheduleDate;
    @BindView(R.id.distance)
    CustomRegularTextView tv_distancia;
    @BindView(R.id.duration)
    CustomRegularTextView tv_duracion;
    //@BindView(R.id.btn_request_later)
    //CustomRegularTextView btnRequestLater;
    @BindView(R.id.date_layout)
    LinearLayout dateLayout;
    private GoogleMap googleMap;
    private Bundle mBundle;
    SupportMapFragment user_request_map;
    private View view;
    private LocationHelper locHelper;
    private Location myLocation;
    private LatLng latlong;
    private static final int DURATION = 1000;
    private static Marker pickup_marker, drop_marker, stop_marker;
    MarkerOptions pickup_opt;
    private static Polyline poly_line;
    private ArrayList<TaxiTypes> typesList;
    private TaxiAdapter taxiAdapter;
    private ArrayList<NearByDrivers> driverslatlngs = new ArrayList<>();
    private String nearest_eta = "--";
    private HashMap<Marker, Integer> markermap = new HashMap<>();
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private int marker_position;
    private String service_id;
    private ProgressBar pbfareProgress;
    private Dialog req_load_dialog;
    private LatLng driverlatlan;
    Handler providerhandler;
    Handler checkreqstatus;
    private ArrayList<Payments> paymentlst;
    private String pickup_add = "", tax_price = "", promoCode = "";
    private String base_price = "", min_price = "", booking_fee = "", currency = "", distance_unit = "";
    private List<LatLng> listLatLng = new ArrayList<>();
    private Polyline blackPolyLine, greyPolyLine;
    private Dialog promo_dialog;
    TextView tv_estimate_fare, tv_total_dis;
    EditText et_promocode;
    Unbinder unbinder;
    PrefUtils prefUtils;
    APIInterface apiInterface;
    RequestDetail requestDetail;
    public  static  String estimatedFare;
    public static boolean laterRequest = false;
    //Date picker
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    String datetime = "";
    PreferenceHelper preferenceHelper;
    int pos;
    LinearLayout layout;
    boolean isAddMoneyToWallet;
    int height = 30;
    int width = 30;
    Bitmap smallPingGreen;
    Bitmap smallPingRed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.request_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        user_request_map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.user_request_map);
        preferenceHelper = new PreferenceHelper(activity);
        tvCashtype = view.findViewById(R.id.tv_cashtype);
        btnRequestCab = view.findViewById(R.id.btn_request_cab);
        if (null != user_request_map)
            user_request_map.getMapAsync(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        btnMylocation.setOnClickListener(this);
        btnRequestCab.setOnClickListener(this);
        layPayment.setOnClickListener(this);
        promoLayout.setOnClickListener(this);
        dateLayout.setOnClickListener(this);
        prefUtils = PrefUtils.getInstance(activity);
        //updatePaymentMode();
        tvCashtype.setText(String.format("Tu modo de pago: %s", prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, "")));
        setUpAdapter();
        BitmapDrawable user_pin_green = (BitmapDrawable)getResources().getDrawable(R.drawable.imagen_logo);
        Bitmap b = user_pin_green.getBitmap();
        smallPingGreen = Bitmap.createScaledBitmap(b, width, height, false);

        BitmapDrawable user_pin_red = (BitmapDrawable)getResources().getDrawable(R.drawable.flag);
        Bitmap r = user_pin_red.getBitmap();
        smallPingRed = Bitmap.createScaledBitmap(r, width, height, false);
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_mylocation:
                break;
            /*case R.id.lay_payment:
                getPaymentMethods();
                break;*/
            case R.id.promo_layout:
                showpromo();
                break;
            case R.id.btn_request_cab:
                createANowRequest();
                break;
            /*case R.id.btn_request_later:
                if (!laterRequest) {
                    dateLayout.setVisibility(View.VISIBLE);
                    laterRequest = true;
                    datePicker();
                    updateUI();
                    btnRequestLater.setText(getString(R.string.txt_schedule));
                } else {
                    laterRequest = false;
                    updateUI();
                    createARequestLater();
                }
                break;*/
            case R.id.date_layout:
                datePicker();
                break;
        }
    }

    private void createARequestLater() {
        //disableLaterBtn();
        Call<String> call = apiInterface.scheduleALaterRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , service_id
                , s_address
                , d_address
                , pic_latlan.latitude
                , pic_latlan.longitude
                , drop_latlan.latitude
                , drop_latlan.longitude
                , 1
                , datetime
                , estimatedFare
                , prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject laterRequestResponse = null;
                try {
                    laterRequestResponse = new JSONObject(response.body());
                    if (laterRequestResponse != null) {
                        if (laterRequestResponse.optString(Const.Params.SUCCESS).equalsIgnoreCase(APIConsts.Constants.TRUE)) {
                            UiUtils.showShortToast(activity, laterRequestResponse.optString(APIConsts.Params.MESSAGE));
                            JSONObject data = laterRequestResponse.optJSONObject(APIConsts.Params.DATA);
                            ScheduleDetailBottomSheet scheduleDetailBottomSheet = new ScheduleDetailBottomSheet();
                            scheduleDetailBottomSheet.setRequestId(data.optString(APIConsts.Params.REQUEST_ID));
                            scheduleDetailBottomSheet.setRedirection(true);
                            scheduleDetailBottomSheet.show(activity.getSupportFragmentManager(), scheduleDetailBottomSheet.getTag());
                        } else {
                            //enableLaterBtn();
                            UiUtils.showShortToast(activity, laterRequestResponse.optString(APIConsts.Params.ERROR));
                            if (laterRequestResponse.optInt(APIConsts.Params.ERROR_CODE) == APIConsts.ErrorCodes.REDIRECT_PAYMENTS) {
                                startActivity(new Intent(activity, PaymentsActivity.class));
                            }
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

    private void datePicker() {
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
                (dialog, which) -> {
                    btnRequestCab.setVisibility(View.VISIBLE);
                    dpd.dismiss();
                    laterRequest = false;
                });
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
        dpd.setOnCancelListener(dialogInterface -> {
            btnRequestCab.setVisibility(View.VISIBLE);
            laterRequest = false;
        });
        dpd.show();
    }

    private void TimePicker() {
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
                        scheduleDate.setText(String.format("%s :- %s", "Schedule On ", datetime));
                    }
                }, mHour, mMinute, false);
        tpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                (dialog, which) -> {
                    btnRequestCab.setVisibility(View.VISIBLE);
                    tpd.dismiss();
                    laterRequest = false;
                });
        tpd.setOnCancelListener(dialogInterface -> {
            btnRequestCab.setVisibility(View.VISIBLE);
            laterRequest = false;
        });
        tpd.show();
    }

    private void setuptypesView() {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        lstVehicle.setLayoutManager(mLayoutManager);
        lstVehicle.setItemAnimator(new DefaultItemAnimator());
        lstVehicle.addItemDecoration(new SpacesItemDecoration(size.x / 20));
        ItemClickSupport.addTo(lstVehicle).setOnItemClickListener((recyclerView, position, v) -> {
            new PreferenceHelper(activity).putRequestType(typesList.get(position).getId());
            btnRequestCab.setText(getResources().getString(R.string.txt_reqst) + " " + typesList.get(position).getTaxitype());
            service_id = typesList.get(position).getId();
            //prefUtils.getIntValue(APIConsts.Params.PRICE, typesList.get(position).getEstimatedPrice());
            tvNoSeats.setText("1-" + " " + typesList.get(position).getTaxi_seats());
            taxiAdapter.OnItemClicked(position);
            taxiAdapter.notifyDataSetChanged();
            try{
                getAvailableProviders(latlong);
            } catch (Exception e) {
                e.printStackTrace();
            }
            laterRequest = false;
            updateUI();
            pos = position;
            new PreferenceHelper(activity).putTaxi_name(typesList.get(position).getTaxitype());
        });
//        lstVehicle.addOnItemTouchListener(new RecyclerLongPressClickListener(activity, lstVehicle, new RecyclerLongPressClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                service_id = typesList.get(position).getId();
//                laterRequest = false;
//                updateUI();
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//                service_id = typesList.get(position).getId();
//                getDistanceAndDuration(pic_latlan, drop_latlan, false, false);
//                prefUtils.getIntValue(APIConsts.Params.PRICE, typesList.get(position).getEstimatedPrice());
//                //updatePaymentMode();
//                showfareestimate(typesList.get(position).getTaxitype(), typesList.get(position).getTaxi_price_distance(), typesList.get(position).getTaxi_price_min(), typesList.get(position).getTaxiimage(), typesList.get(position).getTaxi_seats(), typesList.get(position).getBasefare());
//            }
//        }));
    }

    public void updateUI() {
        promoLayout.setVisibility(!laterRequest ? View.VISIBLE : View.GONE);
        btnRequestCab.setVisibility(laterRequest ? View.GONE : View.VISIBLE);
        dateLayout.setVisibility(laterRequest && scheduleDate.getText().toString().equals("") ? View.VISIBLE : View.GONE);
        //btnRequestLater.setText(laterRequest && !scheduleDate.getText().toString().equals("") ? getString(R.string.txt_schedule) : getString(R.string.requestLater));

    }

    @Override
    public void onMapReady(GoogleMap mgoogleMap) {
        googleMap = mgoogleMap;
        if (googleMap != null) {
            googleMap.setTrafficEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                return;

            googleMap.setMyLocationEnabled(true);

            if (pic_latlan != null) {
                pickup_opt = new MarkerOptions();
                pickup_opt.position(pic_latlan);
                pickup_opt.title(activity.getResources().getString(R.string.txt_pickup_address));
                pickup_opt.anchor(0.5f, 0.5f);
                pickup_opt.zIndex(1);
                pickup_opt.icon(BitmapDescriptorFactory
                        .fromBitmap(smallPingGreen));
                pickup_marker = googleMap.addMarker(pickup_opt);
                btnMylocation.setVisibility(View.GONE);
            }
            if (drop_latlan != null) {
                MarkerOptions opt = new MarkerOptions();
                opt.position(drop_latlan);
                opt.title(activity.getResources().getString(R.string.txt_drop_loc));
                opt.anchor(0.5f, 0.5f);
                opt.icon(BitmapDescriptorFactory
                        .fromBitmap(smallPingRed));
                drop_marker = googleMap.addMarker(opt);
            }
            if (stop_latlan != null) {
                MarkerOptions opt = new MarkerOptions();
                opt.position(stop_latlan);
                opt.anchor(0.5f, 0.5f);
                opt.icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.pin_stop));
                stop_marker = googleMap.addMarker(opt);
            }
        }
        /*
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                           @Override
                                           public View getInfoWindow(Marker marker) {
                                               View vew = null;
                                               if (drop_marker != null) {
                                                   if (marker.getId().equals(drop_marker.getId())) {
                                                       vew = activity.getLayoutInflater().inflate(R.layout.info_window_dest, null);

                                                   } else if (marker.getId().equals(pickup_marker.getId())) {
                                                       pickup_marker.hideInfoWindow();
                                                   } else {
                                                       vew = activity.getLayoutInflater().inflate(R.layout.driver_info_window, null);
                                                       TextView txt_driver_name = (TextView) vew.findViewById(R.id.driver_name);
                                                       if (driverslatlngs.size() > 0) {
                                                           txt_driver_name.setText(driverslatlngs.get(marker_position).getDriver_name());
                                                           SimpleRatingBar driver_rate = (SimpleRatingBar) vew.findViewById(R.id.driver_rate);
                                                           driver_rate.setRating(driverslatlngs.get(marker_position).getDriver_rate());
                                                       }
                                                   }
                                               } else {
                                                   vew = activity.getLayoutInflater().inflate(R.layout.driver_info_window, null);
                                                   TextView txt_driver_name = (TextView) vew.findViewById(R.id.driver_name);
                                                   if (driverslatlngs.size() > 0) {
                                                       txt_driver_name.setText(driverslatlngs.get(marker_position).getDriver_name());
                                                       SimpleRatingBar driver_rate = (SimpleRatingBar) vew.findViewById(R.id.driver_rate);
                                                       driver_rate.setRating(driverslatlngs.get(marker_position).getDriver_rate());
                                                   }
                                               }
                                               return vew;
                                           }

                                           @Override
                                           public View getInfoContents(Marker marker) {
                                               return null;
                                           }
                                       }
        );
        googleMap.setOnMarkerClickListener(marker -> {
            try {
                if (pickup_marker != null) {
                    pickup_marker.hideInfoWindow();
                    if (!marker.getId().equals(pickup_marker.getId()) && !marker.getId().equals(drop_marker.getId())) {
                        marker_position = markermap.get(marker);
                    } else
                        marker_position = markermap.get(marker);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });*/
        if (pickup_marker != null && drop_marker != null) {
            fitmarkers_toMap();
        }
    }

    @Override
    public void onMethodCallback(String id, String taxitype, String taxi_price_distance, String taxi_price_min, String taxiimage, String taxi_seats, String basefare) {
        try {
            service_id = id;
            getDistanceAndDuration(pic_latlan, drop_latlan, true, false);
            showfareestimate(taxitype, taxi_price_distance, taxi_price_min, taxiimage, taxi_seats, basefare);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void handleWalletVisibility() {
        layout.setVisibility(isAddMoneyToWallet ? View.VISIBLE : View.GONE);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(space, 2, space, 2);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        typesList = new ArrayList<>();
        paymentlst = new ArrayList<>();
        providerhandler = new Handler();
        checkreqstatus = new Handler();
    }

    protected void getServiceTypes(float dis, float dur) {
        Call<String> call;
        if (dis == 0)
            call = apiInterface.getServiceTypes(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                    , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        else
            call = apiInterface.getServiceWithDistance(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                    , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                    , dis
                    , dur
                    ,pic_latlan.latitude
                    ,pic_latlan.longitude);
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
                            estimatedFare = String.valueOf(typesList.get(0).getEstimatedPrice());
                            setUpAdapter();
                            service_id = typesList.get(0).getId();
                        }
                        else {
                            UiUtils.showShortToast(activity, servicesResponse.optString(APIConsts.Params.ERROR));
                        }
                    } else {
                        UiUtils.showShortToast(activity, servicesResponse.optString(APIConsts.Params.ERROR));
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

    private void setUpAdapter() {
        taxiAdapter = new TaxiAdapter(activity, typesList, this);
        lstVehicle.setAdapter(taxiAdapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(activity, getResources().getIdentifier("layout_animation_from_right", "anim", activity.getPackageName()));
        lstVehicle.setLayoutAnimation(animation);
        lstVehicle.scheduleLayoutAnimation();
        taxiAdapter.notifyDataSetChanged();
        loadProgress.setVisibility(View.GONE);
        if (typesList.size() > 0) {
            btnRequestCab.setText(getResources().getString(R.string.txt_reqst) + " " + typesList.get(0).getTaxitype());
            tvNoSeats.setText("1-" + " " + typesList.get(0).getTaxi_seats());
            new PreferenceHelper(activity).putTaxi_name(typesList.get(0).getTaxitype());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
        }
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        locHelper = new LocationHelper(activity);
        locHelper.setLocationReceivedLister(this);
//        getServiceTypes(0, 0);
        setuptypesView();
        startgetreqstatus();
        if (new PreferenceHelper(activity).getRequestId() == Const.NO_REQUEST) {
            startgetProvider();
        }
        if (pic_latlan != null && drop_latlan != null) {
            getDirectionsWay(pic_latlan.latitude, pic_latlan.longitude, drop_latlan.latitude, drop_latlan.longitude);
            getDistanceAndDuration(pic_latlan, drop_latlan, false, false);


        }
        promoLayout.setVisibility(!laterRequest ? View.VISIBLE : View.GONE);
        btnRequestCab.setVisibility(laterRequest && scheduleDate.getText().toString().equals("") ? View.GONE : View.VISIBLE);
        dateLayout.setVisibility(laterRequest && scheduleDate.getText().toString().equals("") ? View.VISIBLE : View.GONE);
        /*btnRequestLater.setText(laterRequest && !scheduleDate.getText().toString().equals("") ? getString(R.string.txt_schedule) : getString(R.string.requestLater));
        btnRequestLater.setOnClickListener(view1 -> {
            if (!laterRequest) {
                dateLayout.setVisibility(View.VISIBLE);
                laterRequest = true;
                updateUI();
                datePicker();
            } else {
                laterRequest = false;
                createARequestLater();
            }
        });*/
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
    public void onLocationReceived(LatLng latilong) {
        try {
            if (latilong != null)
                latlong = latilong;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationReceived(Location location) {
        try {
            if (location != null) {
                myLocation = location;
                LatLng latLang = new LatLng(location.getLatitude(),
                        location.getLongitude());

                if (latLang != null)
                    latlong = latLang;
                //

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getAvailableProviders(LatLng latLng) {
        Call<String> call = apiInterface.getAllAvailableProviders(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , latLng.latitude
                , latLng.longitude
                , service_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                driverslatlngs.clear();
                JSONObject setMarkersResponse = null;
                try {
                    setMarkersResponse = new JSONObject(response.body());
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

    private void setUpMarkers() {
        if (googleMap != null) {
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
        }
        if (driverslatlngs.size() > 0) {
            driverlatlan = driverslatlngs.get(0).getLatlan();
            if (driverlatlan != null && pic_latlan != null) {
                //getDistanceAndDuration(pic_latlan, driverlatlan, false, true);
            }
            if (null != markers && markers.size() > 0) {
                for (Marker marker : markers) {
                    marker.remove();
                }
            }
            final Marker[] driver_marker = new Marker[1];
            for (int i = 0; i < driverslatlngs.size(); i++) {
                final MarkerOptions currentOption = new MarkerOptions();
                currentOption.position(driverslatlngs.get(i).getLatlan());
                currentOption.title(driverslatlngs.get(i).getDriver_name());
                currentOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_booking_lux_map_topview));
                if (googleMap != null) {
                    activity.runOnUiThread(() -> driver_marker[0] = googleMap.addMarker(currentOption));
                    markers.add(driver_marker[0]);
                    markermap.put(driver_marker[0], i);
                    btnRequestCab.setEnabled(true);
                    btnRequestCab.setText(getResources().getString(R.string.txt_reqst) + " " + new PreferenceHelper(activity).getTaxi_name());
                    btnRequestCab.setTextColor(activity.getResources().getColor(R.color.white));
                    btnRequestCab.setBackgroundColor(activity.getResources().getColor(R.color.black));
                }
            }
        } else {
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
            btnRequestCab.setEnabled(false);
            btnRequestCab.setText(getResources().getString(R.string.btn_no_driver));
            btnRequestCab.setTextColor(activity.getResources().getColor(R.color.deep_grey));
            btnRequestCab.setBackgroundColor(activity.getResources().getColor(R.color.main_color));
            if (pickup_opt != null && pickup_marker != null) {
                activity.runOnUiThread(() -> pickup_marker.setIcon((BitmapDescriptorFactory
                        .fromBitmap(smallPingGreen))));

            }
        }
    }

    @Override
    public void onConntected(Bundle bundle) {
    }

    @Override
    public void onConntected(Location location) {
        if (location != null && googleMap != null) {
            LatLng currentlatLang = new LatLng(location.getLatitude(), location.getLongitude());
            pickup_add = getCompleteAddressString(currentlatLang.latitude, currentlatLang.longitude);
        }
    }

    protected void getDistanceAndDuration(LatLng picLatLng, LatLng dropLatLng, boolean isFareCalculation, boolean isSetUpMaker) {
        String url = APIConsts.Apis.DISTANCE_LOCATION_API + picLatLng.latitude + "," + picLatLng.longitude + "&destinations=" + dropLatLng.latitude + "," + dropLatLng.longitude +
                "&mode=driving&language=en-EN&key=" + APIConsts.Constants.GOOGLE_API_KEY + "&sensor=false";
        Call<String> call = apiInterface.getLocationBasedResponse(url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject fareResponse = null;
                try {
                    fareResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (fareResponse != null) {
                    fareResponse.optString(APIConsts.Constants.STATUS).equals(APIConsts.Constants.OK);
                    int dur = 0;
                    double trip_dis = 0;
                    String distance = "", duration = "";
                    try {
                        JSONArray sourceArray = fareResponse.optJSONArray("origin_addresses");
                        String sourceObject = (String) sourceArray.opt(0);
                        JSONArray destinationArray = fareResponse.optJSONArray("destination_addresses");
                        String destinationObject = (String) destinationArray.opt(0);
                        JSONArray jsonArray = fareResponse.optJSONArray("rows");
                        JSONObject elementsObject = jsonArray.optJSONObject(0);
                        JSONArray elementsArray = elementsObject.optJSONArray("elements");
                        JSONObject distanceObject = elementsArray.optJSONObject(0);
                        JSONObject dObject = distanceObject.optJSONObject("distance");
                        distance = dObject.optString("text");
                        JSONObject durationObject = distanceObject.optJSONObject("duration");
                        duration = durationObject.optString("text");

                        tv_distancia.setText("Dis: "+ distance);
                        tv_duracion.setText("Dur: "+ duration);

                        String dis = dObject.optString("value");
                        dur = durationObject.optInt("value");
                        trip_dis = Integer.valueOf(dis) * 0.001;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (isFareCalculation && !isSetUpMaker) {
                        getFareCalculation(trip_dis, String.valueOf(dur), service_id);
                        tv_total_dis.setText(distance);
                        nearest_eta = duration;
                    } else if (!isSetUpMaker)
                        getServiceTypes((float) trip_dis, dur);
                    if (pickup_opt != null && pickup_marker != null) {
                        String finalDuration = duration;
                        //activity.runOnUiThread(() -> pickup_marker.setIcon((BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(finalDuration)))));
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

    private Bitmap getMarkerBitmapFromView(String eta) {
        String time = eta.replaceAll("\\s+", "\n");
        View customMarkerView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.eta_info_window, null);
        TextView markertext = (TextView) customMarkerView.findViewById(R.id.txt_eta);
        markertext.setText(time);
        markertext.setAllCaps(true);
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

    private void getDirections(double latitude, double longitude, double latitude1, double longitude1) {
        if (!AndyUtils.isNetworkAvailable(activity))
            return;
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.DIRECTION_API_BASE + Const.ORIGIN + "="
                + latitude + "," + longitude + "&" + Const.DESTINATION + "="
                + latitude1 + "," + longitude1 + "&" + Const.EXTANCTION);
        new VollyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_DIRECTION_API, this);
    }

   /* private void getDirectionsWay(double latitude, double longitude, double latitude1, double longitude1, double latitideStop, double longitudeStop) {
        if (!AndyUtils.isNetworkAvailable(activity))
            return;
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.DIRECTION_API_BASE + Const.ORIGIN + "="
                + latitude + "," + longitude + "&" + Const.DESTINATION + "="
                + latitude1 + "," + longitude1 + "&" + Const.WAYPOINTS + "="
                + latitideStop + "," + longitudeStop + "&" + Const.EXTANCTION);
        Log.e("asher", "directions stop map " + map);
        new VollyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_DIRECTION_API, this);
    }*/
    private void getDirectionsWay(double latitude, double longitude, double latitude1, double longitude1) {
        String directionwayUrl = Const.DIRECTION_API_BASE + Const.ORIGIN + "="
                + latitude + "," + longitude + "&" + Const.DESTINATION + "="
                + latitude1 + "," + longitude1 + "&" + Const.EXTANCTION;
        Call<String> call = apiInterface.getDirectionsWay(directionwayUrl);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("asher", "directions response " + response);
                drawPolyLines(response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    public void drawPolyLines(Response<String> response) {
        if (response != null) {
            drawPath(response.body());
        }
    }
    public void drawPath(String result) {
        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            PolylineOptions options = new PolylineOptions().width(8).color(getResources().getColor(R.color.black)).geodesic(true);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    options.add(point);
                }
            }
            this.listLatLng.addAll(list);
            if (googleMap != null) {
                blackPolyLine = googleMap.addPolyline(options);
                poly_line = blackPolyLine;
            }
            PolylineOptions greyOptions = new PolylineOptions();
            greyOptions.width(8);
            greyOptions.color(Color.GRAY);
            greyPolyLine = googleMap.addPolyline(greyOptions);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Log.e("asher", "inside animate polyline ");
                animatePolyLine();
            }
        } catch (JSONException e) {
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }


    private void fitmarkers_toMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickup_marker.getPosition());
        builder.include(drop_marker.getPosition());
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.5); // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,120);
        if (null != googleMap) {
            googleMap.moveCamera(cu);
        }
    }

    protected void getPaymentMethods() {
        Call<String> call = apiInterface.getPaymentMethods(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , String.valueOf(typesList.get(pos).getEstimatedPrice()));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                driverslatlngs.clear();
                JSONObject paymentModeResponse = null;
                try {
                    paymentModeResponse = new JSONObject(response.body());
                    if (paymentModeResponse.optString(APIConsts.Constants.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        paymentlst.clear();
                        JSONObject data = paymentModeResponse.optJSONObject(APIConsts.Params.DATA);
                        JSONArray paymentModeArray = data.optJSONArray(APIConsts.Params.PAYMENT_MODES);
                        for (int i = 0; i < paymentModeArray.length(); i++) {
                            JSONObject paymentObject = paymentModeArray.optJSONObject(i);
                            Payments paymnts = new Payments();
                            paymnts.setPaymentMode(paymentObject.optString(APIConsts.Params.PAYMENT_MODE));
                            paymnts.setPayment_name(paymentObject.optString(APIConsts.Params.NAME));
                            paymentlst.add(paymnts);
                        }
                        isAddMoneyToWallet = data.optInt(APIConsts.Params.IS_USER_ADD_MONEY_TO_WALLET) == 1;
                        String amountToAdd = data.optString(APIConsts.Params.ADD_WALLET_AMOUNT_FORMATTED);
                        String remaining = data.optString(APIConsts.Params.WALLET_REMAINING_FORMATTED);
                        String amountNotFormatted = data.optString(APIConsts.Params.ADD_WALLET_AMOUNT);
                        if (paymentlst != null && isAdded()) {
                            showpaymentoptionlst(paymentlst, isAddMoneyToWallet, amountToAdd, remaining, amountNotFormatted);
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

    private void disableRequestBtn() {
        btnRequestCab.setEnabled(false);
        btnRequestCab.setBackgroundColor(activity.getResources().getColor(R.color.dark_grey));
    }

    private void enableRequestBtn() {
        btnRequestCab.setEnabled(true);
        btnRequestCab.setBackgroundColor(activity.getResources().getColor(R.color.black));
    }

    /*private void enableLaterBtn() {
        btnRequestLater.setEnabled(false);
        btnRequestLater.setBackgroundColor(activity.getResources().getColor(R.color.dark_grey));
    }

    private void disableLaterBtn() {
        btnRequestLater.setEnabled(true);
        btnRequestLater.setBackgroundColor(activity.getResources().getColor(R.color.black));
    }
*/

    protected void createANowRequest() {
        disableRequestBtn();
        stopCheckingforstatus();
        showreqloader();
        try {
            Call<String> call = apiInterface.createNowRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                    , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                    , service_id
                    , s_address
                    , d_address
                    , stop_address != null ? stop_address : ""
                    , pic_latlan.latitude
                    , pic_latlan.longitude
                    , drop_latlan.latitude
                    , drop_latlan.longitude
                    , stop_latlan != null ? stop_latlan.latitude : 0
                    , stop_latlan != null ? stop_latlan.longitude : 0
                    , 1
                    , et_promocode != null ? et_promocode.getText().toString() : ""
                    , estimatedFare
                    , prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, "")
            );
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    JSONObject createRequestResponse = null;
                    try {
                        createRequestResponse = new JSONObject(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (createRequestResponse != null) {
                        if (createRequestResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                            startgetProvider();
                            startCheckstatusTimer();
                        } else {
                            enableRequestBtn();
                            UiUtils.showShortToast(getActivity(), createRequestResponse.optString(APIConsts.Params.ERROR));
                            if (req_load_dialog != null && req_load_dialog.isShowing()) {
                                req_load_dialog.dismiss();
                                stopCheckingforstatus();
                            }
                            cancelCreateRequest();
                            if (createRequestResponse.optInt(APIConsts.Params.ERROR_CODE) == APIConsts.ErrorCodes.WALLETEMPTY) {
                                AddMoneyBottomSheet addMoneyBottomSheet = new AddMoneyBottomSheet();
                                addMoneyBottomSheet.setIsRedeem(false, "");
                                addMoneyBottomSheet.show(getFragmentManager(), addMoneyBottomSheet.getTag());
                            }
                            if (createRequestResponse.optInt(APIConsts.Params.ERROR_CODE) == APIConsts.ErrorCodes.REDIRECT_PAYMENTS) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return strAdd;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.currentFragment = Const.REQUEST_FRAGMENT;

    }

    @Override
    public void onPause() {
        super.onPause();

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


    private void stopCheckingforstatus() {
        if (checkreqstatus != null) {
            checkreqstatus.removeCallbacks(reqrunnable);
        }
    }

    Runnable runnable = new Runnable() {
        public void run() {
            try{
                getAvailableProviders(latlong);
            } catch (Exception e) {
                e.printStackTrace();
            }
            providerhandler.postDelayed(this, 20000);
        }
    };

    private void startgetreqstatus() {
        startCheckstatusTimer();
    }

    private void startCheckstatusTimer() {
        checkreqstatus.postDelayed(reqrunnable, 5000);
    }


    Runnable reqrunnable = new Runnable() {
        public void run() {
            requestStatusCheck();
            checkreqstatus.postDelayed(this, 5000);
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
                    requestDetail = new RequestDetail();
                    requestDetail = parseRequestStatus(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (requestStatusResponse != null) {
                    if (requestStatusResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        Bundle bundle = new Bundle();
                        JSONObject jsonObject = requestStatusResponse.optJSONObject(APIConsts.Params.DATA);
                        JSONArray data = jsonObject.optJSONArray(APIConsts.Params.DATA);
                        if (data.length() == 0) {
                            try {
                                if (null != req_load_dialog && req_load_dialog.isShowing()) {
                                    req_load_dialog.cancel();
                                    UiUtils.showShortToast(activity, getString(R.string.noProvidersFound));
                                    enableRequestBtn();
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            OngoingTripFragment travalfragment = new OngoingTripFragment();
                            if (requestDetail.getTripStatus() == IS_ACCEPTED) {
                                bundle.putSerializable(Const.REQUEST_DETAIL, requestDetail);
                                bundle.putInt(Const.DRIVER_STATUS, Const.IS_ACCEPTED);
                                if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                    stopCheckingforproviders();
                                    travalfragment.setArguments(bundle);
                                    activity.addFragment(travalfragment, false, Const.TRAVEL_MAP_FRAGMENT, true);
                                }
                            }
//                            BaseMapFragment.drop_latlan = null;
//                            BaseMapFragment.pic_latlan = null;
//                            BaseMapFragment.s_address = "";
//                            BaseMapFragment.d_address = "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.e("mahi", "on destory view is calling");
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.user_request_map);
        if (f != null) {
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*TO clear all views */
        ViewGroup mContainer = getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
        googleMap = null;
        if (req_load_dialog != null && req_load_dialog.isShowing()) {
            req_load_dialog.dismiss();
        }
        stopCheckingforproviders();
        stopCheckingforstatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCheckingforproviders();
        if (googleMap != null) {
            googleMap.clear();
        }
        stopCheckingforproviders();
        stopCheckingforstatus();
    }

    private void showpaymentoptionlst(final ArrayList<Payments> paymentlst, boolean isAddMoneyToWallet, String amountToAdd,
                                      String remaingAmount, String amountNotFormatted) {
        final Dialog pay_dialog = new Dialog(activity, R.style.DialogThemeforview);
        pay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pay_dialog.setCancelable(true);
        pay_dialog.setContentView(R.layout.select_payment);
        ImageButton btn_pay_viewcancel = pay_dialog.findViewById(R.id.btn_pay_viewcancel);
        RecyclerView lv_cards = pay_dialog.findViewById(R.id.lv_cards);
        TextView addMoney = pay_dialog.findViewById(R.id.addAmountText);
        TextView navigateToAddMoney = pay_dialog.findViewById(R.id.navigateToAddMoney);
        layout = pay_dialog.findViewById(R.id.layout);

        addMoney.setText(String.format("%s %s %s", getString(R.string.youNeedToAddMoney), amountToAdd, getString(R.string.toYourWallet)));

        navigateToAddMoney.setOnClickListener(view -> {
            AddMoneyBottomSheet addMoneyBottomSheet = new AddMoneyBottomSheet();
            addMoneyBottomSheet.setIsRedeem(false, remaingAmount);
            addMoneyBottomSheet.setAmount(amountNotFormatted);
            addMoneyBottomSheet.show(getActivity().getSupportFragmentManager(), addMoneyBottomSheet.getTag());
            pay_dialog.dismiss();
        });

        TextView tv_payment_title = pay_dialog.findViewById(R.id.tv_payment_title);
        PaymentModeAdapter payadapter = new PaymentModeAdapter(activity, paymentlst, pay_dialog, this::handleWalletVisibility, isAddMoneyToWallet);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        lv_cards.setLayoutManager(mLayoutManager);
        lv_cards.setItemAnimator(new DefaultItemAnimator());
        lv_cards.setAdapter(payadapter);
        btn_pay_viewcancel.setOnClickListener(view -> pay_dialog.dismiss());
        pay_dialog.show();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (fareResponse != null) {
                    if (fareResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONObject data = fareResponse.optJSONObject(APIConsts.Params.DATA);
                        String fare = data.optString("estimated_fare_formatted");
                        estimatedFare = data.optString("estimated_fare");
                        tax_price = data.optString("tax_price");
                        base_price = data.optString("base_price");
                        min_price = data.optString("min_fare");
                        booking_fee = data.optString("booking_fee");
                        currency = data.optString("currency");
                        distance_unit = data.optString("distance_unit");
                        tv_estimate_fare.setVisibility(View.VISIBLE);
                        tv_estimate_fare.setText(fare);
                        if (pbfareProgress != null) {
                            pbfareProgress.setVisibility(View.GONE);
                        }
                    }
                } else {
                    UiUtils.showShortToast(activity, fareResponse.optString(APIConsts.Params.ERROR));
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

    private void showfareestimate(String taxitype, final String taxi_price_distance,
                                  final String taxi_price_min, String taxiimage, String taxi_seats, final String taxi_cost) {
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
        Glide.with(activity).load(taxiimage).error(R.drawable.frontal_taxi_cab).into(type_img);
        tv_fare_taxi_name.setText(taxitype);
        tv_total_capcity.setText("1-" + taxi_seats);
        btn_info.setOnClickListener(view -> showfarebreakdown(base_price, taxi_price_distance, taxi_price_min, min_price, booking_fee, currency, distance_unit));
        fare_done.setOnClickListener(view -> faredialog.dismiss());
        faredialog.show();
    }

    private void showfarebreakdown(String base_price, String taxi_price_distance, String
            taxi_price_min, String min_price, String booking_fee, String currency, String distance_unit) {
        final Dialog farebreak = new Dialog(activity, R.style.DialogSlideAnim_leftright_Fullscreen);
        farebreak.requestWindowFeature(Window.FEATURE_NO_TITLE);
        farebreak.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        farebreak.setCancelable(true);
        farebreak.setContentView(R.layout.fare_breakdown);
        TextView tv_dis_title = (TextView) farebreak.findViewById(R.id.tv_dis_title);
        TextView tv_base_fare = (TextView) farebreak.findViewById(R.id.tv_base_fare);
        TextView tv_min_fare = (TextView) farebreak.findViewById(R.id.tv_min_fare);
        TextView tv_per_min_cost = (TextView) farebreak.findViewById(R.id.tv_per_min_cost);
        TextView tv_per_km_price = (TextView) farebreak.findViewById(R.id.tv_per_km_price);
        TextView tv_service_tax_price = (TextView) farebreak.findViewById(R.id.tv_service_tax_price);
        TextView tv_booking_price = (TextView) farebreak.findViewById(R.id.tv_booking_price);
        tv_base_fare.setText(currency + base_price);
        tv_booking_price.setText(currency + booking_fee);
        tv_min_fare.setText(currency + min_price);
        tv_per_min_cost.setText(currency + taxi_price_min);
        tv_per_km_price.setText(currency + taxi_price_distance);
        tv_service_tax_price.setText(currency + tax_price);
        ImageView close_popup = (ImageView) farebreak.findViewById(R.id.close_popup);
        tv_dis_title.setText(getResources().getString(R.string.txt_per) + " " + distance_unit);
        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                farebreak.cancel();
            }
        });
        farebreak.show();

    }

    private void showreqloader() {
        stopCheckingforproviders();
        req_load_dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        req_load_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        req_load_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        req_load_dialog.setCancelable(false);
        req_load_dialog.setContentView(R.layout.request_loading);
        final RippleBackground rippleBackground = req_load_dialog.findViewById(R.id.content);
        TextView cancel_req_create = req_load_dialog.findViewById(R.id.cancel_req_create);
        final TextView req_status = req_load_dialog.findViewById(R.id.req_status);
        final TextView requestType = req_load_dialog.findViewById(R.id.requestType);
        requestType.setVisibility(View.GONE);
        rippleBackground.startRippleAnimation();
        cancel_req_create.setOnClickListener(view -> {
            req_status.setText(getResources().getString(R.string.txt_canceling_req));
            cancelCreateRequest();
            new PreferenceHelper(activity).clearRequestData();
            startgetProvider();
            stopCheckingforstatus();
        });
        req_load_dialog.show();
    }

    private void cancel_create_req() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.CANCEL_CREATE_REQUEST);
        map.put(Const.Params.ID, String.valueOf(prefUtils.getIntValue(PrefKeys.USER_ID, 0)));
        map.put(Const.Params.TOKEN, prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        Log.d("mahi", map.toString());
        new VollyRequester(activity, Const.POST, map, Const.ServiceCode.CANCEL_CREATE_REQUEST, this);
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
                if (cancelResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                    enableRequestBtn();
                    if (req_load_dialog != null && req_load_dialog.isShowing()) {
                        req_load_dialog.dismiss();
                    }
                } else {
                    UiUtils.showShortToast(activity, cancelResponse.optString(APIConsts.Params.ERROR));
                }
                Log.d("mahi", "cancel req_response" + response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    private void animatePolyLine() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(1200);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animator1 -> {
            List<LatLng> latLngList = blackPolyLine.getPoints();
            int initialPointSize = latLngList.size();
            int animatedValue = (int) animator1.getAnimatedValue();
            int newPoints = (animatedValue * listLatLng.size()) / 100;
            if (initialPointSize < newPoints) {
                latLngList.addAll(listLatLng.subList(initialPointSize, newPoints));
                blackPolyLine.setPoints(latLngList);
            }


        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.e("asher", "inside animate polyline listener");
            animator.addListener(polyLineAnimationListener);
            animator.start();
        }

    }

    Animator.AnimatorListener polyLineAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            List<LatLng> blackLatLng = blackPolyLine.getPoints();
            List<LatLng> greyLatLng = greyPolyLine.getPoints();
            greyLatLng.clear();
            greyLatLng.addAll(blackLatLng);
            blackLatLng.clear();
            blackPolyLine.setPoints(blackLatLng);
            greyPolyLine.setPoints(greyLatLng);
            blackPolyLine.setZIndex(2);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                animator.start();
                animatePolyLine();
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    };

    private void showpromo() {
        promo_dialog = new Dialog(activity, R.style.DialogSlideAnim_leftright);
        promo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        promo_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        promo_dialog.setCancelable(true);
        promo_dialog.setContentView(R.layout.promo_layout);
        et_promocode = promo_dialog.findViewById(R.id.et_promocode);
        et_promocode.setFilters(new InputFilter[]{EMOJI_FILTER, new InputFilter.AllCaps()});
        TextView cencel_promocode = promo_dialog.findViewById(R.id.cencel_promocode);
        TextView apply_promocode = promo_dialog.findViewById(R.id.apply_promocode);
        apply_promocode.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(et_promocode.getText().toString())) {
                applyPromoCode(et_promocode.getText().toString());
            } else {
                AndyUtils.showShortToast(getResources().getString(R.string.txt_error_promo), activity);
                et_promocode.requestFocus();
            }
        });
        cencel_promocode.setOnClickListener(view -> {
            promo_dialog.dismiss();
            AndyUtils.hideKeyBoard(getActivity());
        });
        promo_dialog.show();
    }

    public static InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {
                int type = Character.getType(source.charAt(index));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    };

    private void ApplyPromoCode(String promoValue) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            return;
        }
        AndyUtils.showSimpleProgressDialog(activity, "", false);
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.VALIDATE_PROMO);
        map.put(Const.Params.ID, String.valueOf(prefUtils.getIntValue(PrefKeys.USER_ID, 0)));
        map.put(Const.Params.TOKEN, prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        map.put(Const.Params.PROMOCODE, promoValue);
        Log.d("mahi", map.toString());
        new VollyRequester(activity, Const.POST, map, Const.ServiceCode.VALIDATE_PROMO, this);

    }

    protected void applyPromoCode(String value) {
        Call<String> call = apiInterface.applyPromoCode(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , value);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject applyPromoResponse = null;
                try {
                    applyPromoResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (applyPromoResponse.optString("success").equals("true")) {
                    AndyUtils.showShortToast(applyPromoResponse.optString("message"), activity);
                    promoCode = tvPromocode.getText().toString();
                    promo_dialog.dismiss();
                    tvPromocode.setText(getResources().getString(R.string.txt_promo_success));
                    tvPromocode.setTextColor(getResources().getColor(R.color.dark_green));
                    promoLayout.setEnabled(false);
                    AndyUtils.hideKeyBoard(getActivity());
                } else {
                    AndyUtils.showShortToast(applyPromoResponse.optString("error"), activity);
                    tvPromocode.requestFocus();
                    promoLayout.setEnabled(true);
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

