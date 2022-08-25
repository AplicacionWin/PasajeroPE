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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Commonutils;
import com.nikola.user.Utils.Const;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.ApiManager.ParserUtils;
import com.nikola.user.network.Models.AirportLst;
import com.nikola.user.network.Models.LocationList;
import com.nikola.user.network.Models.RequestDetail;
import com.nikola.user.network.Models.TaxiTypes;
import com.nikola.user.ui.Adapter.AirportLstAdapter;
import com.nikola.user.ui.Adapter.LocationAdapter;
import com.nikola.user.ui.Adapter.TypesAdapter;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nikola.user.Utils.Const.NO_REQUEST;
import static com.nikola.user.network.ApiManager.APIConsts.RequestStatus.IS_ACCEPTED;

/**
 * Created by user on 2/23/2017.
 */

public class AirportBookingFragment extends BaseMapFragment implements View.OnClickListener {
    @BindView(R.id.from_airport)
    CustomRegularTextView fromAirport;
    @BindView(R.id.switch_mode)
    SwitchCompat switchMode;
    @BindView(R.id.to_airport)
    CustomRegularTextView toAirport;
    @BindView(R.id.sp_select_address)
    Spinner spSelectAddress;
    @BindView(R.id.et_airport_address)
    AutoCompleteTextView etAirportAddress;
    @BindView(R.id.sp_type_airport)
    Spinner spTypeAirport;
    @BindView(R.id.trip_fair)
    CustomRegularTextView tripFair;
    @BindView(R.id.trip_tolls)
    CustomRegularTextView tripTolls;
    private ArrayList<TaxiTypes> typesList = new ArrayList<>();
    private ArrayList<AirportLst> airportList = new ArrayList<>();
    private ArrayList<LocationList> locationList = new ArrayList<>();
    LocationAdapter locationadapter;
    private LatLng s_latlan, d_latlan;
    private String s_address, d_address;
    private String airport_details_id, location_details_id = "";
    private String airport_package_id = "";
    private boolean ischecked = false;
    private Dialog req_load_dialog;
    Handler checkreqstatus;
    private String datetime = "";
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    Unbinder unbinder;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    RequestDetail requestDetail;
    int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkreqstatus = new Handler();
        prefUtils = PrefUtils.getInstance(activity);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        getServiceTypes();
        getAirportsList();
    }

    protected void getAirportsList() {
        Call<String> call;
        call = apiInterface.getAirportList(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject airportListResponse = null;
                try {
                    airportListResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (airportListResponse != null) {
                    if (airportListResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONArray jarray = airportListResponse.optJSONArray(APIConsts.Params.DATA);
                        if (jarray.length() > 0) {
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject taxiobj = jarray.optJSONObject(i);
                                AirportLst type = new AirportLst();
                                type.setAirport_id(taxiobj.optString(Const.Params.AIRPORT_ID));
                                type.setAirport_address(taxiobj.optString(Const.Params.NAME));
                                type.setLatitude(taxiobj.optDouble(Const.Params.LATITUDE));
                                type.setLongitude(taxiobj.optDouble(Const.Params.LONGITUDE));
                                airportList.add(type);
                            }
                            if (airportList != null) {
                                AirportLstAdapter adapter = new AirportLstAdapter(activity, airportList);
                                spSelectAddress.setAdapter(adapter);
                            }
                        } else {
                            UiUtils.showShortToast(activity, airportListResponse.optString(APIConsts.Params.ERROR));
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

    protected void getServiceTypes() {
        Call<String> call;
        call = apiInterface.getServiceDistanceAirport(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , 1222
                , 2
                , 1);
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
                        TypesAdapter adapter = new TypesAdapter(activity, typesList);
                        spTypeAirport.setAdapter(adapter);
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

    protected void makeAirportFareCalculation(String locationDetailsId, String airportDetailsId, String serviceId) {
        Call<String> call;
        call = apiInterface.airportFareCalculation(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , airportDetailsId
                , locationDetailsId
                , serviceId);
        call.enqueue(new Callback<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject airportFareResponse = null;
                try {
                    airportFareResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (airportFareResponse != null) {
                    if (airportFareResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONObject resultObj = airportFareResponse.optJSONObject(APIConsts.Params.DATA);

                        airport_package_id = resultObj.optString(Const.Params.AIRPORT_PRICE_ID);
                        String tripfare = resultObj.optString(Const.Params.PRICE_FORMATTED);
                        String no_tolls = resultObj.optString(Const.Params.NO_OF_TOLLS);
                        String currency = resultObj.optString(Const.Params.CURRENCEY);

                        tripFair.setText(String.format("%s %s", currency, tripfare));
                        tripTolls.setText(no_tolls);
                    } else {
                        tripFair.setText(getString(R.string.not_avail));
                        tripTolls.setText(getString(R.string.not_avail));
                        if (airportFareResponse.optInt(Const.Params.ERROR_CODE)==(APIConsts.ErrorCodes.PRICINGNOTFOUND) )
                            UiUtils.showShortToast(activity,airportFareResponse.optString(Const.Params.ERROR_MSG));
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
        View view = inflater.inflate(R.layout.airport_booking, container, false);
        unbinder = ButterKnife.bind(this, view);
        setOnItemChangeListerners();
        onTextChangeListerners();

          etAirportAddress.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etAirportAddress.getRight() - etAirportAddress.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    etAirportAddress.setText("");
                    return true;
                }
            }
            return false;
        });
        return view;
    }


    private void setOnItemChangeListerners() {
        spTypeAirport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView != null && typesList.size() > 0) {
                    makeAirportFareCalculation(location_details_id, airport_details_id, typesList.get(spTypeAirport.getSelectedItemPosition()).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spSelectAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView != null && typesList.size() > 0) {
                    final AirportLst airlst = (AirportLst) adapterView.getItemAtPosition(i);
                    airport_details_id = airlst.getAirport_id();
                    makeAirportFareCalculation(location_details_id, airport_details_id, typesList.get(spTypeAirport.getSelectedItemPosition()).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        switchMode.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                ischecked = true;
                toAirport.setTextColor(activity.getResources().getColor(R.color.black));
                etAirportAddress.setHint(getResources().getString(R.string.txt_set_drop_loc));
                fromAirport.setTextColor(activity.getResources().getColor(R.color.circle_color));
            } else {
                ischecked = false;
                toAirport.setTextColor(activity.getResources().getColor(R.color.circle_color));
                fromAirport.setTextColor(activity.getResources().getColor(R.color.black));
            }

        });

        fromAirport.setOnClickListener(view -> {
            ischecked = true;
            fromAirport.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.drawable_not_my_chat));
            toAirport.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.drawable_edittext_background));
            fromAirport.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            toAirport.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            fromAirport.setPadding(convertDpToPixel(30), convertDpToPixel(15), convertDpToPixel(30), convertDpToPixel(15));
        });


        toAirport.setOnClickListener(view -> {
            ischecked = false;
            toAirport.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.drawable_not_my_chat));
            fromAirport.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.drawable_edittext_background));
            toAirport.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            fromAirport.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            toAirport.setPadding(convertDpToPixel(30), convertDpToPixel(15), convertDpToPixel(30), convertDpToPixel(15));
        });

        etAirportAddress.setOnItemClickListener((adapterView, view14, i, l) -> {
            pos = i;
            if (isAdded()) {
                activity.runOnUiThread(() -> locationadapter.notifyDataSetChanged());
            }
            etAirportAddress.setSelection(0);
            final LocationList locationlst = (LocationList) adapterView.getItemAtPosition(i);
            d_address = locationlst.getLocation_address();
            location_details_id = locationlst.getLocation_id();
            etAirportAddress.setText("");
            etAirportAddress.append(d_address);
            new Thread(() -> {
                makeAirportFareCalculation(location_details_id, airport_details_id, typesList.get(spTypeAirport.getSelectedItemPosition()).getId());
            }).start();
        });
    }

    public int convertDpToPixel(int dp){
        return dp * (getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void onTextChangeListerners() {
        etAirportAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    locationList(charSequence);
                } else {
                    tripFair.setText("--");
                    tripTolls.setText("--");
                    airport_package_id = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
                        final Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            scheduleAAirportRide(typesList.get(spTypeAirport.getSelectedItemPosition()).getId());
                        }, 1000);

                    }
                }, mHour, mMinute, false);
        tpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                (dialog, which) -> tpd.dismiss());
        tpd.show();

    }

    protected void scheduleAAirportRide(String service_type) {
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.scheduleaAirportRide(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , service_type
                , s_address
                , d_address
                , s_latlan.latitude
                , s_latlan.longitude
                , d_latlan.latitude
                , d_latlan.longitude
                , 3
                , airport_package_id
                , prefUtils.getStringValue(PrefKeys.PAYMENT_MODE, "")
                , datetime
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject createRequestResponse = null;
                try {
                    createRequestResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (createRequestResponse != null) {
                    if (createRequestResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONObject jsonObject = createRequestResponse.optJSONObject(APIConsts.Params.DATA);
                        String requestId = jsonObject.optString(APIConsts.Params.REQUEST_ID);
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(getResources().getString(R.string.txt_trip_schedule_success))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.txt_ok), (dialog, id) -> {
                                    dialog.dismiss();
//                                    activity.addFragment(new HomeFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                                    ScheduleDetailBottomSheet scheduleDetailBottomSheet = new ScheduleDetailBottomSheet();
                                    scheduleDetailBottomSheet.setRequestId(requestId);
                                    scheduleDetailBottomSheet.setRedirection(true);
                                    scheduleDetailBottomSheet.show(activity.getSupportFragmentManager(), scheduleDetailBottomSheet.getTag());

                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        UiUtils.showShortToast(activity, createRequestResponse.optString(APIConsts.Params.ERROR));
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


    protected void createaAirportRide() {
        Call<String> call = apiInterface.createAirportNowRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , typesList.get(spTypeAirport.getSelectedItemPosition()).getId()
                , s_address
                , d_address
                , s_latlan.latitude
                , s_latlan.longitude
                , d_latlan.latitude
                , d_latlan.longitude
                , 3
                , airport_package_id
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
                        showreqloader();
                        startgetreqstatus();
                    } else {
                        UiUtils.showShortToast(activity, createRequestResponse.optString(APIConsts.Params.ERROR));
                        if (req_load_dialog != null)
                            req_load_dialog.cancel();
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

    protected void locationList(CharSequence type) {
        Call<String> call = apiInterface.locationList(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , type.toString()
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject locationResponse = null;
                try {
                    locationResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (locationResponse != null) {
                    if (locationResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        locationList.clear();
                        JSONArray jarray = locationResponse.optJSONArray(APIConsts.Params.DATA);
                        for (int i = 0; i < jarray.length(); i++) {
                            JSONObject taxiobj = jarray.optJSONObject(i);
                            LocationList type = new LocationList();
                            type.setLocation_id(taxiobj.optString(Const.Params.LOCATION_ID));
                            type.setLocation_address(taxiobj.optString(Const.Params.NAME));
                            type.setLatitude(taxiobj.optDouble(Const.Params.LATITUDE));
                            type.setLongitude(taxiobj.optDouble(Const.Params.LONGITUDE));
                            locationList.add(type);
                        }
                        locationadapter = new LocationAdapter(activity, R.layout.autocomplete_list_text, locationList);
                        etAirportAddress.setAdapter(locationadapter);
                        locationadapter.notifyDataSetChanged();

                    } else {
                        UiUtils.showShortToast(activity, locationResponse.optString(APIConsts.Params.ERROR));
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
//    private void showreqloader() {
//        req_load_dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        req_load_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        req_load_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
//        req_load_dialog.setContentView(R.layout.request_loading);
//        final RippleBackground rippleBackground = req_load_dialog.findViewById(R.id.content);
//        TextView cancel_req_create = req_load_dialog.findViewById(R.id.cancel_req_create);
//        final TextView req_status = req_load_dialog.findViewById(R.id.req_status);
//        final TextView requestType = req_load_dialog.findViewById(R.id.requestType);
//        rippleBackground.startRippleAnimation();
//        cancel_req_create.setOnClickListener(view -> {
//            req_status.setText(getResources().getString(R.string.txt_canceling_req));
//            cancelRequest();
//            stopCheckingforstatus();
//        });
//        req_load_dialog.create();
//        req_load_dialog.show();
//    }

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
        requestType.setVisibility(View.GONE);
        rippleBackground.startRippleAnimation();
        cancel_req_create.setOnClickListener(view -> {
            req_status.setText(getResources().getString(R.string.txt_canceling_req));
            cancelRequest();
            new PreferenceHelper(activity).clearRequestData();
        });
        req_load_dialog.show();
    }

    protected void cancelRequest() {
        Call<String> call;
        call = apiInterface.cancelRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
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

    private void startgetreqstatus() {
        startCheckstatusTimer();
    }

    private void startCheckstatusTimer() {
        checkreqstatus.postDelayed(reqrunnable, 5000);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (requestStatusResponse != null) {
                    if (requestStatusResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        requestDetail = new RequestDetail();
                        requestDetail = ParserUtils.parseRequestStatus(response.body());
                        Bundle bundle = new Bundle();
                        OngoingTripFragment travalfragment = new OngoingTripFragment();
                        if (requestDetail.getTripStatus() == IS_ACCEPTED) {
                            if (req_load_dialog != null)
                                req_load_dialog.cancel();
                            bundle.putSerializable(Const.REQUEST_DETAIL, requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS, Const.IS_ACCEPTED);
                            if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                stopCheckingforstatus();
                                travalfragment.setArguments(bundle);
                                activity.addFragment(travalfragment, false, Const.TRAVEL_MAP_FRAGMENT, true);
                            }
                            BaseMapFragment.drop_latlan = null;
                            BaseMapFragment.pic_latlan = null;
                            BaseMapFragment.s_address = "";
                            BaseMapFragment.d_address = "";
                        }
                        else if(requestDetail.getTripStatus() == NO_REQUEST) {
                            if (req_load_dialog != null){
                                req_load_dialog.dismiss();
                                UiUtils.showShortToast(getActivity(), getString(R.string.no_provider_are_avaiable));
                                stopCheckingforstatus();
                            }
                        }
                    } else {
                        UiUtils.showShortToast(activity, requestStatusResponse.optString(APIConsts.Params.ERROR));
                        if (req_load_dialog != null) {
                            req_load_dialog.cancel();
                            stopCheckingforstatus();
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.currentFragment = Const.AIRPORT_FRAGMENT;
    }

    @Override
    public void onDestroyView() {
        ViewGroup mContainer = getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
        stopCheckingforstatus();
        super.onDestroyView();

    }

    @OnClick({R.id.airport_back, R.id.airport_book_btn, R.id.airport_book_btn_later})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.airport_back:
                activity.addFragment(new HomeFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                break;
            case R.id.airport_book_btn:
                try {
                    if (airport_package_id.equals("")) {
                        Commonutils.showtoast(getResources().getString(R.string.txt_error_select_details), activity);
                    } else if (etAirportAddress.getText().toString().length() == 0) {
                        Commonutils.showtoast(getResources().getString(R.string.txt_error_enter_address), activity);
                        etAirportAddress.requestFocus();
                    } else {
                        if (!ischecked == true) {
                            d_latlan = new LatLng(airportList.get(spSelectAddress.getSelectedItemPosition()).getLatitude(),
                                    airportList.get(spSelectAddress.getSelectedItemPosition()).getLongitude());
                            s_latlan = new LatLng(locationList.get(pos).getLatitude(), locationList.get(pos).getLongitude());
                            d_address = airportList.get(spSelectAddress.getSelectedItemPosition()).getAirport_address();
                            s_address = locationList.get(pos).getLocation_address();
                        } else {
                            s_latlan = new LatLng(airportList.get(spSelectAddress.getSelectedItemPosition()).getLatitude(),
                                    airportList.get(spSelectAddress.getSelectedItemPosition()).getLongitude());
                            d_latlan = new LatLng(locationList.get(pos).getLatitude(), locationList.get(pos).getLongitude());
                            s_address = airportList.get(pos).getAirport_address();
                            d_address = locationList.get(spSelectAddress.getSelectedItemPosition()).getLocation_address();
                        }
                        if (!tripFair.getText().toString().equalsIgnoreCase("--Not Available--") && !tripFair.getText().toString().equalsIgnoreCase("")) {
                            createaAirportRide();
                        } else {
                            UiUtils.showShortToast(getActivity(), getString(R.string.please_select_a_package));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.airport_book_btn_later:
                try{
                    if (airport_package_id.equals("")) {
                        Commonutils.showtoast(getResources().getString(R.string.txt_error_select_details), activity);
                    } else if (etAirportAddress.getText().toString().length() == 0) {
                        Commonutils.showtoast(getResources().getString(R.string.txt_error_enter_address), activity);
                        etAirportAddress.requestFocus();
                    } else {
                        if (!ischecked) {
                            d_latlan = new LatLng(airportList.get(spSelectAddress.getSelectedItemPosition()).getLatitude(),
                                    airportList.get(spSelectAddress.getSelectedItemPosition()).getLongitude());
                            s_latlan = new LatLng(locationList.get(pos).getLatitude(), locationList.get(pos).getLongitude());
                            d_address = airportList.get(spSelectAddress.getSelectedItemPosition()).getAirport_address();
                            s_address = locationList.get(pos).getLocation_address();
                            DatePicker();
                        } else {
                            s_latlan = new LatLng(airportList.get(spSelectAddress.getSelectedItemPosition()).getLatitude(),
                                    airportList.get(spSelectAddress.getSelectedItemPosition()).getLongitude());
                            d_latlan = new LatLng(locationList.get(pos).getLatitude(), locationList.get(pos).getLongitude());
                            DatePicker();
                            s_address = airportList.get(spSelectAddress.getSelectedItemPosition()).getAirport_address();
                            d_address = locationList.get(pos).getLocation_address();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}




