package com.nikola.user.ui.Fragment;

import android.app.Dialog;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Commonutils;
import com.nikola.user.Utils.Const;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularEditView;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.Utils.RecyclerLongPressClickListener;
import com.nikola.user.network.ApiManager.AsyncTaskCompleteListener;
import com.nikola.user.network.ApiManager.ParserUtils;
import com.nikola.user.network.ApiManager.VollyRequester;
import com.nikola.user.network.Models.RequestDetail;
import com.nikola.user.network.Models.TaxiTypes;
import com.nikola.user.ui.Adapter.BoltTypesAdapter;
import com.nikola.user.ui.Adapter.SearchPlaceAdapter;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Carlos on 4/20/2017.
 */

public class MessageFragment extends BaseMapFragment implements AsyncTaskCompleteListener, View.OnClickListener {
    @BindView(R.id.bolt_back)
    ImageButton boltBack;
    @BindView(R.id.toolbar_bolt)
    Toolbar toolbarBolt;
    @BindView(R.id.first_msg)
    CustomRegularTextView firstMsg;
    @BindView(R.id.bolt_msg_1)
    CustomRegularTextView boltMsg1;
    @BindView(R.id.btn_yes)
    Button btnYes;
    @BindView(R.id.btn_no)
    Button btnNo;
    @BindView(R.id.btns_layout)
    LinearLayout btnsLayout;
    @BindView(R.id.bolt_msg_2)
    CustomRegularTextView boltMsg2;
    @BindView(R.id.iv_source_map)
    ImageView ivSourceMap;
    @BindView(R.id.tv_s_address)
    CustomRegularTextView tvSAddress;
    @BindView(R.id.source_layout)
    LinearLayout sourceLayout;
    @BindView(R.id.bolt_msg_3)
    CustomRegularTextView boltMsg3;
    @BindView(R.id.iv_dest_map)
    ImageView ivDestMap;
    @BindView(R.id.tv_dest_address)
    CustomRegularTextView tvDestAddress;
    @BindView(R.id.destination_layout)
    LinearLayout destinationLayout;
    @BindView(R.id.bolt_msg_4)
    CustomRegularTextView boltMsg4;
    @BindView(R.id.recycel_type)
    RecyclerView recycelType;
    @BindView(R.id.tv_approximate_price)
    CustomRegularTextView tvApproximatePrice;
    @BindView(R.id.btn_create_request)
    Button btnCreateRequest;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btns_request)
    LinearLayout btnsRequest;
    @BindView(R.id.scroll_bolt)
    ScrollView scrollBolt;
    @BindView(R.id.et_message)
    CustomRegularEditView etMessage;
    @BindView(R.id.btn_send)
    ImageView btnSend;
    @BindView(R.id.lay_msg_send)
    LinearLayout layMsgSend;
    private BoltTypesAdapter bolttypeAdapter;
    private ArrayList<TaxiTypes> typesList = new ArrayList<TaxiTypes>();
    private LatLng s_LatLng, d_latlan;
    private ArrayList<String> resultList;
    private SearchPlaceAdapter searchAdapter;
    private String service_id = "";
    private Handler checkreqstatus = new Handler();
    private String d_Address = "", s_address = "";
    private Dialog req_load_dialog;
    private View view;
    ListView searchListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bolt_layout, container, false);
        getTypes();
        setUpAdapter();
        scrollBolt.post(() -> scrollBolt.fullScroll(ScrollView.FOCUS_DOWN));
        return view;
    }

    private void setUpAdapter() {
        recycelType.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        recycelType.setItemAnimator(new DefaultItemAnimator());
        recycelType.addItemDecoration(new SpacesItemDecoration(3));
        bolttypeAdapter = new BoltTypesAdapter(activity, typesList);
        recycelType.setAdapter(bolttypeAdapter);
        recycelType.addOnItemTouchListener(new RecyclerLongPressClickListener(activity, recycelType, new RecyclerLongPressClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                service_id = typesList.get(position).getId();
                findDistanceAndTime(s_LatLng, d_latlan);
            }
            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findDistanceAndTime(LatLng s_latlan, LatLng d_latlan) {
        if (!AndyUtils.isNetworkAvailable(activity))
            return;
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.GOOGLE_MATRIX_URL + Const.Params.ORIGINS + "="
                + s_latlan.latitude + "," + s_latlan.longitude + "&" + Const.Params.DESTINATION + "="
                + d_latlan.latitude + "," + d_latlan.longitude + "&" + Const.Params.MODE + "="
                + "driving" + "&" + Const.Params.LANGUAGE + "="
                + "en-EN" + "&" + "key=" + Const.GOOGLE_API_KEY + "&" + Const.Params.SENSOR + "="
                + false);
        new VollyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_MATRIX, this);
    }

    private void clearAll() {
        firstMsg.setVisibility(View.GONE);
        boltMsg1.setVisibility(View.GONE);
        boltMsg2.setVisibility(View.GONE);
        sourceLayout.setVisibility(View.GONE);
        boltMsg3.setVisibility(View.GONE);
        destinationLayout.setVisibility(View.GONE);
        boltMsg4.setVisibility(View.GONE);
        recycelType.setVisibility(View.GONE);
        tvApproximatePrice.setVisibility(View.GONE);
        btnsRequest.setVisibility(View.GONE);
        layMsgSend.setVisibility(View.VISIBLE);
        etMessage.setText("");
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
            checkreqstatus();
            checkreqstatus.postDelayed(this, 4000);
        }
    };


    private void checkreqstatus() {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.CHECKREQUEST_STATUS);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        Log.d("mahi", map.toString());
        new VollyRequester(activity, Const.POST, map, Const.ServiceCode.CHECKREQUEST_STATUS, this);
    }

    private void RequestTaxi() {
        showreqloader();
        startgetreqstatus();
        if (!AndyUtils.isNetworkAvailable(activity)) {
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.REQUEST_TAXI);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        if (s_LatLng != null) {
            map.put(Const.Params.S_LATITUDE, String.valueOf(s_LatLng.latitude));
            map.put(Const.Params.S_LONGITUDE, String.valueOf(s_LatLng.longitude));
        }
        if (d_latlan != null) {
            map.put(Const.Params.D_LONGITUDE, String.valueOf(d_latlan.longitude));
            map.put(Const.Params.D_LATITUDE, String.valueOf(d_latlan.latitude));
        }
        map.put(Const.Params.SERVICE_TYPE, service_id);
        map.put(Const.Params.S_ADDRESS, s_address);
        map.put(Const.Params.D_ADDRESS, d_Address);
        map.put(Const.Params.REQ_STATUS_TYPE, "1");
        Log.d("mahi", map.toString());
        new VollyRequester(activity, Const.POST, map, Const.ServiceCode.REQUEST_TAXI, this);
    }

    private void showDistanceDialog() {
        final Dialog dialog = new Dialog(activity, R.style.DialogThemeforview);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_distance_search);
        dialog.setCancelable(false);
        final EditText autoCompleteTextView = dialog.findViewById(R.id.auto_search);
        searchListView = dialog.findViewById(R.id.lv_search);
        resultList = new ArrayList<>();
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = autoCompleteTextView.getText().toString().toLowerCase(Locale.getDefault());
                getRunnableAddress(text);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchListView.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            AndyUtils.hideKeyBoard(activity);
            s_address = (String) searchAdapter.getItem(arg2);
            autoCompleteTextView.setSelection(0);
            Handler handler = new Handler();
            handler.post(() -> {
                sourceLayout.setVisibility(View.VISIBLE);
                tvSAddress.setText(s_address);
                searchAdapter = null;
                dialog.cancel();
                s_LatLng = AndyUtils.getLatLngFromAddress(s_address, activity);
                if (s_LatLng != null) {
                    String img = getGoogleMapThumbnail(s_LatLng.latitude, s_LatLng.longitude);
                    Glide.with(activity).load(img).into(ivSourceMap);
                } else {
                }
                boltMsg3.setVisibility(View.VISIBLE);
                scrollBolt.post(() -> scrollBolt.fullScroll(ScrollView.FOCUS_DOWN));
                new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        if (d_latlan == null) {
                            showdestinationsearch();
                        }
                    }
                }.start();
            });
        });
        dialog.show();

    }

    private void showreqloader() {
        req_load_dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        req_load_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        req_load_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        req_load_dialog.setCancelable(false);
        req_load_dialog.setContentView(R.layout.request_loading);
        final RippleBackground rippleBackground = req_load_dialog.findViewById(R.id.content);
        TextView cancel_req_create =  req_load_dialog.findViewById(R.id.cancel_req_create);
        final TextView req_status = req_load_dialog.findViewById(R.id.req_status);
        rippleBackground.startRippleAnimation();
        cancel_req_create.setOnClickListener(view -> {
            req_status.setText(getResources().getString(R.string.txt_canceling_req));
            cancel_create_req();
            new PreferenceHelper(activity).clearRequestData();
            stopCheckingforstatus();
            clearAll();
        });
        req_load_dialog.show();
    }

    private void cancel_create_req() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.CANCEL_CREATE_REQUEST);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        Log.d("mahi", map.toString());
        new VollyRequester(activity, Const.POST, map, Const.ServiceCode.CANCEL_CREATE_REQUEST, this);
    }

    private void showdestinationsearch() {
        final Dialog dialog = new Dialog(activity, R.style.DialogThemeforview);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_distance_search);
        dialog.setCancelable(false);
        ImageView backButton = dialog.findViewById(R.id.iv_back);
        final EditText autoCompleteTextView = dialog.findViewById(R.id.auto_search);
        searchListView = dialog.findViewById(R.id.lv_search);
        resultList = new ArrayList<>();
        autoCompleteTextView.setHint(getResources().getString(R.string.txt_set_drop_loc));
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = autoCompleteTextView.getText().toString().toLowerCase(Locale.getDefault());
                getRunnableAddress(text);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchListView.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            AndyUtils.hideKeyBoard(activity);
            d_Address = (String) searchAdapter.getItem(arg2);
            autoCompleteTextView.setSelection(0);
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    destinationLayout.setVisibility(View.VISIBLE);
                    tvDestAddress.setText(d_Address);
                    searchAdapter = null;
                    dialog.cancel();
                    d_latlan = AndyUtils.getLatLngFromAddress(d_Address, activity);
                    if (d_latlan != null) {
                        String url = getGoogleMapThumbnail(d_latlan.latitude, d_latlan.longitude);
                        Glide.with(activity).load(url).into(ivDestMap);
                    }
                    scrollBolt.post(() -> scrollBolt.fullScroll(ScrollView.FOCUS_DOWN));
                    new CountDownTimer(3000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            boltMsg4.setVisibility(View.VISIBLE);
                            recycelType.setVisibility(View.VISIBLE);
                            scrollBolt.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollBolt.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                        }
                    }.start();
                }
            });
        });
        dialog.show();
    }

    public static String getGoogleMapThumbnail(double lati, double longi) {
        String staticMapUrl = "http://maps.google.com/maps/api/staticmap?center=" + lati + "," + longi + "&markers=" + lati + "," + longi + "&zoom=14&size=200x200&sensor=false";
        return staticMapUrl;
    }

    private void getRunnableAddress(final String text) {
        Handler handler = null;
        Runnable run = () -> getAddress(text);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        } else {
            handler = new Handler();
        }
        handler.postDelayed(run, 1000);
    }

    private void getAddress(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, AndyUtils.getPlaceAutoCompleteUrl(text));
        AndyUtils.appLog("Ashutosh", "AddressMap" + map);
        new VollyRequester(activity, Const.GET, map, 205, this);
    }

    @OnClick({R.id.btn_yes, R.id.btn_no, R.id.iv_source_map, R.id.iv_dest_map, R.id.btn_create_request, R.id.btn_cancel, R.id.btn_send, R.id.bolt_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bolt_back:
                activity.addFragment(new HomeFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                break;
            case R.id.btn_send:
                if (etMessage.getText().toString().length() == 0) {
                    AndyUtils.showShortToast("Please enter message!", activity);
                } else {
                    firstMsg.setText(etMessage.getText().toString());
                    firstMsg.setVisibility(View.VISIBLE);
                    boltMsg1.setVisibility(View.VISIBLE);
                    layMsgSend.setVisibility(View.GONE);
                    btnsLayout.setVisibility(View.VISIBLE);
                    AndyUtils.hideKeyBoard(activity);
                }
                break;
            case R.id.btn_no:
                activity.addFragment(new HomeFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                break;
            case R.id.btn_yes:
                btnsLayout.setVisibility(View.GONE);
                boltMsg2.setVisibility(View.VISIBLE);
                new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        showDistanceDialog();
                    }

                }.start();
                break;
            case R.id.iv_source_map:
                showDistanceDialog();
                break;
            case R.id.iv_dest_map:
                showdestinationsearch();
                break;
            case R.id.btn_create_request:
                btnsRequest.setVisibility(View.GONE);
                RequestTaxi();
                break;
            case R.id.btn_cancel:
                clearAll();
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

    private void getTypes() {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.TAXI_TYPE);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        Log.d("mahi", map.toString());
        new VollyRequester(activity, Const.POST, map, Const.ServiceCode.HOMETAXI_TYPE, this);
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.HOMETAXI_TYPE:
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        typesList.clear();
                        JSONArray jarray = job.getJSONArray("services");
                        if (jarray.length() > 0) {
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject taxiobj = jarray.getJSONObject(i);
                                TaxiTypes type = new TaxiTypes();
                                type.setId(taxiobj.getString("id"));
                                type.setTaxi_cost(taxiobj.getString("min_fare"));
                                type.setTaxiimage(taxiobj.getString("picture"));
                                type.setTaxitype(taxiobj.getString("name"));
                                type.setTaxi_price_min(taxiobj.getString("price_per_min"));
                                type.setTaxi_price_distance(taxiobj.getString("price_per_unit_distance"));
                                type.setTaxi_seats(taxiobj.getString("number_seat"));
                                typesList.add(type);
                            }
                            bolttypeAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.REQUEST_TAXI:
                Log.d("mahi", "create req_response" + response);
                try {
                    JSONObject job1 = new JSONObject(response);
                    if (!job1.getString("success").equals("true")) {
                        if (req_load_dialog != null && req_load_dialog.isShowing()) {
                            req_load_dialog.dismiss();
                            stopCheckingforstatus();
                        }
                        String error = job1.getString("error");
                        Commonutils.showtoast(error, activity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                            if (req_load_dialog != null && req_load_dialog.isShowing()) {
                                req_load_dialog.dismiss();
                                Commonutils.showtoast(getResources().getString(R.string.txt_no_provider_error), activity);
                                stopCheckingforstatus();
                                clearAll();
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
                                activity.addFragment(travalfragment, false, Const.TRAVEL_MAP_FRAGMENT, true);
                            }
                            BaseMapFragment.drop_latlan = null;
                            BaseMapFragment.pic_latlan = null;
                            BaseMapFragment.s_address = "";
                            BaseMapFragment.d_address = "";
                            break;
                    }
                }
                break;
            case Const.ServiceCode.CANCEL_CREATE_REQUEST:
                Log.d("mahi", "cancel req_response" + response);
                if (req_load_dialog != null && req_load_dialog.isShowing()) {
                    req_load_dialog.dismiss();
                }
                break;
            case Const.ServiceCode.GOOGLE_MATRIX:
                Log.d("mahi", "google distance api" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("OK")) {
                        JSONArray sourceArray = jsonObject.getJSONArray("origin_addresses");
                        String sourceObject = (String) sourceArray.get(0);
                        JSONArray destinationArray = jsonObject.getJSONArray("destination_addresses");
                        String destinationObject = (String) destinationArray.get(0);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        JSONObject elementsObject = jsonArray.getJSONObject(0);
                        JSONArray elementsArray = elementsObject.getJSONArray("elements");
                        JSONObject distanceObject = elementsArray.getJSONObject(0);
                        JSONObject dObject = distanceObject.getJSONObject("distance");
                        String distance = dObject.getString("text");
                        JSONObject durationObject = distanceObject.getJSONObject("duration");
                        String duration = durationObject.getString("text");
                        String dis = dObject.getString("value");
                        String dur = durationObject.getString("value");
                        Log.d("mahi", "time and dis" + dur + " " + dis);
                        getFare(dis, dur, service_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.FARE_CALCULATION:
                if (response != null) {
                    try {
                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {
                            String max_fare = job1.getString("estimated_fare_from");
                            String min_fare = job1.getString("estimated_fare_to");
                            tvApproximatePrice.setVisibility(View.VISIBLE);
                            tvApproximatePrice.setText("App.Cost:" + " " + "$" + " " + max_fare + " - " + min_fare);
                            btnsRequest.setVisibility(View.VISIBLE);
                            scrollBolt.post(() -> scrollBolt.fullScroll(ScrollView.FOCUS_DOWN));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case 205:
                AndyUtils.appLog("Ashutosh", "GetAddress" + response);
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
                    if (predsJsonArray.length() > 0) {
                        resultList.clear();
                        for (int i = 0; i < predsJsonArray.length(); i++) {
                            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                        }
                    } else {
                        resultList.clear();
                    }
                    if (searchAdapter == null) {
                        searchAdapter = new SearchPlaceAdapter(activity, resultList);
                        searchListView.setAdapter(searchAdapter);
                    } else {
                        searchAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Log.e("", "Cannot process JSON results", e);
                }
                break;
        }

    }

    private void getFare(String dis, String dur, String service_id) {
        if (!AndyUtils.isNetworkAvailable(activity))
            return;
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.FARE_CALCULATION);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.DISTANCE, dis);
        map.put(Const.Params.TIME, dur);
        map.put(Const.Params.TAXI_TYPE, service_id);
        Log.d("mahi", map.toString());
        new VollyRequester(activity, Const.POST, map, Const.ServiceCode.FARE_CALCULATION, this);
    }
}
