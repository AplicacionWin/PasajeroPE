package com.nikola.user.ui.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Commonutils;
import com.nikola.user.Utils.Const;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.ApiManager.ParserUtils;
import com.nikola.user.network.ApiManager.VollyRequester;
import com.nikola.user.network.Models.RequestDetail;
import com.nikola.user.network.Models.TaxiTypes;
import com.nikola.user.ui.Adapter.PlacesAutoCompleteAdapter;
import com.nikola.user.ui.Adapter.TypesAdapter;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 2/13/2017.
 */

public class HourlyBookngFragment extends BaseMapFragment implements View.OnClickListener {
    @BindView(R.id.hourly_back)
    ImageButton hourlyBack;
    @BindView(R.id.et_hourly_source_address)
    AutoCompleteTextView etHourlySourceAddress;
    @BindView(R.id.et_no_hours)
    EditText etNoHours;
    @BindView(R.id.sp_type)
    Spinner spType;
    @BindView(R.id.trip_fair)
    CustomRegularTextView tripFair;
    @BindView(R.id.text_distance)
    CustomRegularTextView textDistance;
    private ArrayList<TaxiTypes> typesList = new ArrayList<>();
    private PlacesAutoCompleteAdapter placesadapter;
    private PlacesAutoCompleteAdapter dest_placesadapter;
    private LatLng des_latLng, source_latlan;
    private String service_type = "";
    private String hourly_package_id = "";
    private Dialog req_load_dialog;
    Handler checkreqstatus;
    private String datetime = "";
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    Unbinder unbinder;
    PrefUtils prefUtils;
    APIInterface apiInterface;
    RequestDetail requestDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkreqstatus = new Handler();
        prefUtils = PrefUtils.getInstance(activity);
        apiInterface = APIClient.getClient().create(APIInterface.class);

    }

    private void getTypes() {
        if (!AndyUtils.isNetworkAvailable(activity))
            return;
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.TAXI_TYPE);
        map.put(Const.Params.ID, String.valueOf(prefUtils.getIntValue(PrefKeys.USER_ID, 0)));
        map.put(Const.Params.TOKEN, prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        Log.d("mahi", map.toString());
        new VollyRequester(activity, Const.POST, map, Const.ServiceCode.HOMETAXI_TYPE, this);
    }

    protected void getServiceTypes() {
        Call<String> call;
        call = apiInterface.getServiceTypesForHourlyBooking(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , 1
                , source_latlan.latitude
                , source_latlan.longitude);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject servicesResponse = null;
                try {
                    servicesResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (servicesResponse != null) {
                    if (servicesResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONArray jsonArray = servicesResponse.optJSONArray(APIConsts.Params.DATA);
                        typesList = ParserUtils.ParseServicesList(jsonArray);
                        if (typesList != null) {
                            TypesAdapter adapter = new TypesAdapter(activity, typesList);
                            spType.setAdapter(adapter);
                        }
                    } else {
                        UiUtils.showShortToast(activity, servicesResponse.optString(APIConsts.Params.ERROR_MESSAGE));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hourly_booking, container, false);
        unbinder = ButterKnife.bind(this, view);
        setPlacesAdapter();
        itemTextChangeList();
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPlacesAdapter() {
        try {
            placesadapter = new PlacesAutoCompleteAdapter(activity, R.layout.autocomplete_list_text);
            dest_placesadapter = new PlacesAutoCompleteAdapter(activity, R.layout.autocomplete_list_text);
            if (placesadapter != null)
                etHourlySourceAddress.setAdapter(placesadapter);
            etHourlySourceAddress.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etHourlySourceAddress.getRight() - etHourlySourceAddress.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        etHourlySourceAddress.setText("");
                        return true;
                    }
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void itemTextChangeList() {
        try {
            etNoHours.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try{
                        if (charSequence.length() != 0 && typesList !=  null ) {
                            makeHourlyFareCalculation(typesList.get(spType.getSelectedItemPosition()).getId(), charSequence);
                        } else {
                            tripFair.setText("--");
                            textDistance.setText("--");
                            hourly_package_id = "";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (typesList != null && etNoHours.getText().toString().length() != 0) {
                    makeHourlyFareCalculation(typesList.get(i).getId(), etNoHours.getText().toString());
                } else {
                    tripFair.setText("--");
                    textDistance.setText("--");
                    hourly_package_id = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        etHourlySourceAddress.setOnItemClickListener((adapterView, view1, i, l) -> {
            final String selectedSourcePlace = placesadapter.getItem(i);
            etHourlySourceAddress.append(selectedSourcePlace);
            AndyUtils.hideKeyBoard(activity);
            new Thread(() -> {
                try {
                    getLatlanfromAddress(URLEncoder.encode(selectedSourcePlace, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }).start();
        });
        hourlyBack.setOnClickListener(view12 -> activity.addFragment(new HomeFragment(), false, Const.HOME_MAP_FRAGMENT, true));
    }

    private void getLatlanfromAddress(String selectedSourcePlace) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.LOCATION_API_BASE + selectedSourcePlace + "&key=" + Const.GOOGLE_API_KEY);
        Log.d("mahi", "map for s_loc" + map);
        new VollyRequester(activity, Const.GET, map, Const.ServiceCode.LOCATION_API_BASE_SOURCE, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String source_address = "";
        if (savedInstanceState == null) {
            Bundle mBundle = getArguments();
            if (mBundle == null) {
                source_address = "";
            } else {
                source_address = mBundle.getString("pickup_address");
                source_latlan = new LatLng(mBundle.getDouble("pickup_lat"),mBundle.getDouble("pickup_lon"));
                etHourlySourceAddress.setText("");
                etHourlySourceAddress.append(source_address);
                getServiceTypes();
            }
        } else {
            source_address = (String) savedInstanceState.getSerializable("pickup_address");
            etHourlySourceAddress.setText("");
            etHourlySourceAddress.append(source_address);
        }
        try {
            getLatlanfromAddress(URLEncoder.encode(etHourlySourceAddress.getText().toString(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    protected void makeHourlyFareCalculation(String serviceType, CharSequence noOfHours) {
        Call<String> call;
        call = apiInterface.hourlyFareCalculation(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , serviceType
                , noOfHours.toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject job = new JSONObject(response.body());
                    if (job.getString("success").equals("true")) {
                        JSONObject fareobj = job.getJSONObject("data");
                        String price = fareobj.getString("price_formatted");
//                        String price = fareobj.getString("price_formatted");
                        String currency = fareobj.getString("currency");
                        String distance = fareobj.getString("distance_formatted");
                        hourly_package_id = fareobj.getString("hourly_package_id");
                        tripFair.setText(price);
                        textDistance.setText(distance);
                    } else {
                        tripFair.setText("--");
                        textDistance.setText("--");
                        hourly_package_id = "";
                        if (job.has("error_messages")) {
                            String error = job.getString("error_messages");
                            Commonutils.showtoast(error, activity);
                        }
                        if (job.has("error")) {
                            String error_msg = job.getString("error");
                            Commonutils.showtoast(error_msg, activity);
                        }

                    }

                } catch (JSONException e) {
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
    public void onResume() {
        super.onResume();
        activity.currentFragment = Const.HOURLY_FRAGMENT;
    }

    private void DatePicker() {
        final Calendar c = Calendar.getInstance();
        Calendar dateTime = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
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
        if (dateTime.getTimeInMillis() >= c.getTimeInMillis()) {
            //it's after current
            int hour = hourOfDay % 12;
        } else {
            Toast.makeText(activity, "Invalid Time", Toast.LENGTH_LONG).show();
        }
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
                        scheduleAHourlyPackage(datetime);
                    }
                }, mHour, mMinute, false);
        tpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                (dialog, which) -> tpd.dismiss());
        tpd.show();

    }

    private void showreqloader() {
        req_load_dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        req_load_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        req_load_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        req_load_dialog.setCancelable(false);
        req_load_dialog.setContentView(R.layout.request_loading);
        final RippleBackground rippleBackground = req_load_dialog.findViewById(R.id.content);
        TextView cancel_req_create = req_load_dialog.findViewById(R.id.cancel_req_create);
        final TextView req_status = req_load_dialog.findViewById(R.id.req_status);
        final TextView requestType = req_load_dialog.findViewById(R.id.requestType);
        requestType.setText(getString(R.string.rental_bookings));
        rippleBackground.startRippleAnimation();
        cancel_req_create.setOnClickListener(view -> {
            req_status.setText(getResources().getString(R.string.txt_canceling_req));
            cancelRequest();
            new PreferenceHelper(activity).clearRequestData();
            stopCheckingforstatus();
        });
        req_load_dialog.show();
    }

    private void startgetreqstatus() {
        startCheckstatusTimer();
    }

    private void startCheckstatusTimer() {
        checkreqstatus.postDelayed(reqrunnable, 4000);
    }

    private void stopCheckingforstatus() {
        if (checkreqstatus != null) {
            checkreqstatus.removeCallbacks(reqrunnable);
            Log.d("mahi", "stop status handler");
        }
    }

    Runnable reqrunnable = new Runnable() {
        public void run() {
            requestStatusCheck();
            checkreqstatus.postDelayed(this, 15000);
        }
    };


    protected void requestStatusCheck() {
        Call<String> call = apiInterface.pingRequestStatusCheck(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {
                    Bundle bundle = new Bundle();
                    RequestDetail requestDetail = ParserUtils.parseRequestStatus(response.body());
                    OngoingTripFragment travalfragment = new OngoingTripFragment();
                    if (requestDetail == null) {
                        return;
                    }
                    switch (requestDetail.getTripStatus()) {
                        case Const.NO_REQUEST:
                            new PreferenceHelper(activity).clearRequestData();
                            // startgetProvider();
                            if (req_load_dialog != null && req_load_dialog.isShowing()) {
                                req_load_dialog.dismiss();
                                Commonutils.showtoast(getResources().getString(R.string.txt_no_provider_error), activity);
                                stopCheckingforstatus();
                            }
                            break;
                        case Const.IS_ACCEPTED:
                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                stopCheckingforstatus();
                                if (req_load_dialog != null && req_load_dialog.isShowing()) {
                                    req_load_dialog.dismiss();
                                }
                                travalfragment.setArguments(bundle);
                                activity.addFragment(travalfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }
                            break;
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

    protected void createAHourlyRide() {
        showreqloader();
        startgetreqstatus();
        Call<String> call = apiInterface.createAHourlyRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , typesList.get(spType.getSelectedItemPosition()).getId()
                , etHourlySourceAddress.getText().toString()
                , source_latlan.latitude
                , source_latlan.longitude
                , 2
                , hourly_package_id
                , prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, "")
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject job1 = new JSONObject(response.body());
                    if (job1.getString("success").equals("true")) {
                    } else {
                        if (req_load_dialog != null && req_load_dialog.isShowing()) {
                            req_load_dialog.show();
                        }
                        String error = job1.getString("error");
                        Commonutils.showtoast(error, activity);
                    }
                } catch (JSONException e) {
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


    protected void scheduleAHourlyPackage(String datetime) {
        Call<String> call = apiInterface.scheduleAHourlyPackage(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , typesList.get(spType.getSelectedItemPosition()).getId()
                , etHourlySourceAddress.getText().toString()
                , source_latlan.latitude
                , source_latlan.longitude
                , 2
                , hourly_package_id
                , prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, "")
                , datetime
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject job = new JSONObject(response.body());
                    if (job.getString("success").equals("true")) {
                        JSONObject jsonObject = job.optJSONObject(APIConsts.Params.DATA);
                        String requestId = jsonObject.optString(APIConsts.Params.REQUEST_ID);
                        Commonutils.progressdialog_hide();
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(getResources().getString(R.string.txt_trip_schedule_success))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.txt_ok), (dialog, id) -> {
                                    dialog.dismiss();
                                    ScheduleDetailBottomSheet scheduleDetailBottomSheet = new ScheduleDetailBottomSheet();
                                    scheduleDetailBottomSheet.setRequestId(requestId);
                                    scheduleDetailBottomSheet.setRedirection(true);
                                    scheduleDetailBottomSheet.show(activity.getSupportFragmentManager(), scheduleDetailBottomSheet.getTag());
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Commonutils.progressdialog_hide();
                        String error = job.getString("error");
                        Commonutils.showtoast(error, activity);
                    }
                } catch (JSONException e) {
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

    protected void cancelRequest() {
        UiUtils.showLoadingDialog(activity);
        Call<String> call;
        call = apiInterface.cancelRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject servicesResponse = null;
                try {
                    servicesResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (servicesResponse != null) {
                    if (servicesResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        if (req_load_dialog != null) {
                            req_load_dialog.cancel();
                        }
                    } else {
                        UiUtils.showShortToast(activity, servicesResponse.optString(APIConsts.Params.ERROR));
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
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.HOMETAXI_TYPE:
                try {
                    JSONObject job = new JSONObject(response);
                    AndyUtils.removeProgressDialog();
                    if (job.getString("success").equals("true")) {
                        Log.d("serviceResonse", response);
                        typesList.clear();
                        JSONArray jarray = job.getJSONArray("data");
                        if (jarray.length() > 0) {
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject taxiobj = jarray.getJSONObject(i);
                                TaxiTypes type = new TaxiTypes();
                                type.setId(taxiobj.getString("service_type_id"));
                                type.setTaxi_cost(taxiobj.getString("min_fare"));
                                type.setTaxiimage(taxiobj.getString("picture"));
                                type.setTaxitype(taxiobj.getString("service_type_name"));
                                type.setTaxi_price_min(taxiobj.getString("price_per_min"));
                                type.setTaxi_price_distance(taxiobj.getString("price_per_unit_distance"));
                                type.setTaxi_seats(taxiobj.getString("number_seat"));
                                typesList.add(type);
                            }
                            if (typesList != null) {
                                TypesAdapter adapter = new TypesAdapter(activity, typesList);
                                spType.setAdapter(adapter);
                            }
                        }
                    } else {
                        UiUtils.showLongToast(getActivity(), job.optString(APIConsts.Params.ERROR));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.LOCATION_API_BASE_SOURCE:
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        JSONObject locObj = jarray.getJSONObject(0);
                        JSONObject geometryOBJ = locObj.optJSONObject("geometry");
                        JSONObject locationOBJ = geometryOBJ.optJSONObject("location");
                        double lat = locationOBJ.getDouble("lat");
                        double lan = locationOBJ.getDouble("lng");
                        source_latlan = new LatLng(lat, lan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.REQUEST_TAXI:
                Log.d("mahi", "create req_response" + response);
                try {
                    JSONObject job1 = new JSONObject(response);
                    if (job1.getString("success").equals("true")) {
                    } else {
                        if (req_load_dialog != null && req_load_dialog.isShowing()) {
                            req_load_dialog.dismiss();
                        }
                        String error = job1.getString("error");
                        Commonutils.showtoast(error, activity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.HOURLY_PACKAGE_FARE:
                Log.d("mahi", "fare response" + response);
                AndyUtils.removeProgressDialog();
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        JSONObject fareobj = job.getJSONObject("data");
                        String price = fareobj.getString("price_formatted");
                        String currency = fareobj.getString("currency");
                        String distance = fareobj.getString("distance_formatted");
                        hourly_package_id = fareobj.getString("hourly_package_id");
                        tripFair.setText(price);
                        textDistance.setText(distance);
                    } else {
                        tripFair.setText("--");
                        textDistance.setText("--");
                        hourly_package_id = "";
                        if (job.has("error_messages")) {
                            String error = job.getString("error_messages");
                            Commonutils.showtoast(error, activity);
                        }
                        if (job.has("error")) {
                            String error_msg = job.getString("error");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.CANCEL_CREATE_REQUEST:
                Log.d("mahi", "cancel req_response" + response);
                if (req_load_dialog != null && req_load_dialog.isShowing()) {
                    req_load_dialog.dismiss();
                }
                break;
            case Const.ServiceCode.CHECKREQUEST_STATUS:
                Log.d("mahi", "check req status" + response);
                if (response != null) {
                    Bundle bundle = new Bundle();
                    RequestDetail requestDetail = ParserUtils.parseRequestStatus(response);
                    OngoingTripFragment travalfragment = new OngoingTripFragment();
                    if (requestDetail == null) {
                        return;
                    }
                    switch (requestDetail.getTripStatus()) {
                        case Const.NO_REQUEST:
                            new PreferenceHelper(activity).clearRequestData();
                            // startgetProvider();
                            if (req_load_dialog != null && req_load_dialog.isShowing()) {
                                req_load_dialog.dismiss();
                                Commonutils.showtoast(getResources().getString(R.string.txt_no_provider_error), activity);
                                stopCheckingforstatus();
                            }
                            break;
                        case Const.IS_ACCEPTED:
                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                stopCheckingforstatus();
                                if (req_load_dialog != null && req_load_dialog.isShowing()) {
                                    req_load_dialog.dismiss();
                                }
                                travalfragment.setArguments(bundle);
                                activity.addFragment(travalfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }
                            break;
                    }
                }
                break;
            case Const.ServiceCode.REQUEST_LATER:
                Log.d("mahi", "create req later" + response);
                if (response != null) {
                    try {
                        JSONObject job = new JSONObject(response);
                        if (job.getString("success").equals("true")) {
                            JSONObject jsonObject = job.optJSONObject(APIConsts.Params.DATA);
                            String requestId = jsonObject.optString(APIConsts.Params.REQUEST_ID);
                            Commonutils.progressdialog_hide();
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(getResources().getString(R.string.txt_trip_schedule_success))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.txt_ok), (dialog, id) -> {
                                        dialog.dismiss();
                                        ScheduleDetailBottomSheet scheduleDetailBottomSheet = new ScheduleDetailBottomSheet();
                                        scheduleDetailBottomSheet.setRequestId(requestId);
                                        scheduleDetailBottomSheet.setRedirection(true);
                                        scheduleDetailBottomSheet.show(activity.getSupportFragmentManager(), scheduleDetailBottomSheet.getTag());
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Commonutils.progressdialog_hide();
                            String error = job.getString("error");
                            Commonutils.showtoast(error, activity);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        /*TO clear all views */
        ViewGroup mContainer = getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
        super.onDestroyView();
    }

    @OnClick({R.id.hourly_book_btn, R.id.hourly_book_btn_later})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.hourly_book_btn:
                if (etHourlySourceAddress.getText().toString().length() == 0) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_pickup_error), activity);
                    etHourlySourceAddress.requestFocus();
                } else if (hourly_package_id.equals("")) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_hourly_error), activity);
                    etNoHours.requestFocus();
                } else {
                    createAHourlyRide();
                }
                break;
            case R.id.hourly_book_btn_later:
                if (etHourlySourceAddress.getText().toString().length() == 0) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_pickup_error), activity);
                    etHourlySourceAddress.requestFocus();
                } else if (hourly_package_id.equals("")) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_hourly_error), activity);
                    etNoHours.requestFocus();
                } else {
                    DatePicker();
                }
                break;
        }
    }
}
