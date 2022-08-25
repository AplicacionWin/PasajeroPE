package com.nikola.user.ui.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomBoldRegularTextView;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Const;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.ApiManager.ParserUtils;
import com.nikola.user.network.Models.RequestDetail;
import com.nikola.user.ui.activity.CreditCardPaymentWebActivity;
import com.nikola.user.ui.activity.MainActivity;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 1/12/2017.
 */

public class RatingFragment extends BaseMapFragment {
    @BindView(R.id.tv_total)
    CustomBoldRegularTextView tvTotal;
    @BindView(R.id.tv_cancellation_fee)
    CustomRegularTextView tvCancellationFee;
    @BindView(R.id.iv_feedback_vehicle)
    CircleImageView ivFeedbackVehicle;
    @BindView(R.id.iv_feedback_user)
    CircleImageView ivFeedbackUser;
    @BindView(R.id.iv_feedback_location)
    CircleImageView ivFeedbackLocation;
    @BindView(R.id.text_time)
    CustomBoldRegularTextView textTime;
    @BindView(R.id.text_distance)
    CustomBoldRegularTextView textDistance;
    @BindView(R.id.layout_distance)
    LinearLayout layoutDistance;
    @BindView(R.id.tv_payment_type)
    CustomRegularTextView tvPaymentType;
    @BindView(R.id.tv_no_tolls)
    CustomRegularTextView tvNoTolls;
    @BindView(R.id.toll_layout)
    LinearLayout tollLayout;
    @BindView(R.id.simple_rating_bar)
    SimpleRatingBar simpleRatingBar;
    @BindView(R.id.btn_submit_rating)
    CustomRegularTextView btnSubmitRating;
    @BindView(R.id.add_fav)
    LinearLayout add_Fav;
    @BindView(R.id.favIcon)
    ImageView favIcon;
    @BindView(R.id.makeFavourite)
    CustomRegularTextView makeFav;
    private RequestDetail requestDetail;
    int rating = 0;
    private AlertDialog.Builder paybuilder;
    private boolean ispayshowing = false;
    PrefUtils prefUtils;
    Unbinder unbinder;
    APIInterface apiInterface;
    PreferenceHelper preferenceHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        preferenceHelper = new PreferenceHelper(activity);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        rating = simpleRatingBar.getRating();
        prefUtils = PrefUtils.getInstance(getActivity());
        simpleRatingBar.setListener(value -> rating = value);
        btnSubmitRating.setOnClickListener(view1 -> rateProvider());
        return view;
    }

    @OnClick(R.id.add_fav)
    public void onFavClick() {
        if (!requestDetail.isFavoriteProvider()) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.confirm_to_favourite)
                    .setMessage(R.string.are_sure_to_add_provider_to_favorites)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> addFavouriteProvider())
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                    .create().show();
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.confrim_unfavourite)
                    .setMessage(R.string.remove_fromfavourite)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        removeFavProvider();
                    })
                    .setNegativeButton(getString(R.string.no), (dialog, which) ->
                            dialog.cancel())
                    .create().show();
        }
    }

    @OnClick(R.id.btn_forma_pago)
    public void onClickFormaPago() {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.txt_pasarela_pago)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    dialog.cancel();
                    prefUtils.setValue(Const.MOSTRAR_MODAL_PAGO_TARJETA, false);
                    cargarWebViewPasarelaDePago(requestDetail);
                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                    dialog.cancel();
                    prefUtils.setValue(Const.MOSTRAR_MODAL_PAGO_TARJETA, false);
                })
                .create().show();
    }

    private void removeFavProvider() {
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.removeFavProvider(prefUtils.getIntValue(PrefKeys.USER_ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                requestDetail.getUserFavoriteId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject removeResponse = null;
                try {
                    removeResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (removeResponse != null) {
                    UiUtils.hideLoadingDialog();
                    UiUtils.showShortToast(activity, removeResponse.optString(APIConsts.Params.MESSAGE));
                    makeFav.setText(getString(R.string.make_favourite));
                    Glide.with(getActivity()).load(R.drawable.unlike).into(favIcon);
                    requestDetail.setFavoriteProvider(false);
                } else {
                    UiUtils.showShortToast(activity, removeResponse.optString(APIConsts.Params.ERROR_MESSAGE));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(activity)) {
                    UiUtils.showShortToast(activity, activity.getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }


    private void addFavouriteProvider() {
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.addFavProvider(prefUtils.getIntValue(PrefKeys.USER_ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                requestDetail.getDriver_id());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject listResponse = null;
                try {
                    listResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (listResponse != null) {
                    UiUtils.hideLoadingDialog();
                    if (listResponse.optString(Const.Params.SUCCESS).equalsIgnoreCase(APIConsts.Constants.TRUE)) {
                        UiUtils.showShortToast(activity, listResponse.optString(APIConsts.Params.MESSAGE));
                        makeFav.setText(getString(R.string.removeFav));
                        Glide.with(getActivity()).load(R.drawable.like).into(favIcon);
                        requestDetail.setFavoriteProvider(true);
                    } else {
                        UiUtils.showShortToast(activity, listResponse.optString(APIConsts.Params.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(activity)) {
                    UiUtils.showShortToast(activity, getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup mContainer = getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        AndyUtils.removeProgressDialog();
        activity.currentFragment = Const.RATING_FRAGMENT;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle mBundle = getArguments();
        requestStatusCheck();
        if (mBundle != null) {
            requestDetail = (RequestDetail) mBundle.getSerializable(
                    Const.REQUEST_DETAIL);
            //tvTotal.setText(requestDetail.getTrip_total_price() != null ? requestDetail.getCurrnecy_unit() + " " + requestDetail.getTrip_total_price() : "0.00");
            tvTotal.setText(getActivity().getText(R.string.taximetro));
            textTime.setText(requestDetail.getTrip_time() != null ?requestDetail.getTrip_time() + " " + getResources().getString(R.string.min) : "0");
            textDistance.setText(requestDetail.getTrip_distance() != null ? requestDetail.getTrip_distance() + " " + requestDetail.getDistance_unit() :  "0");
            tvPaymentType.setText(requestDetail.getPayment_mode() != null ? getResources().getString(R.string.txt_payment_type) + requestDetail.getPayment_mode() : "");
            UiUtils.showShortToast(getActivity(), getActivity().getText(R.string.payment_message).toString());
            Glide.with(activity).load(requestDetail.getDriver_picture()).into(ivFeedbackUser);
            Glide.with(activity).load(requestDetail.getVehical_img()).into(ivFeedbackVehicle);
            if (!requestDetail.getD_lat().equals("") && !requestDetail.getD_lon().equals("")) {
                try {
                    Glide.with(activity).load(getGoogleMapThumbnail(Double.valueOf(requestDetail.getD_lat()), Double.valueOf(requestDetail.getD_lon()))).centerCrop().into(ivFeedbackLocation);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (requestDetail.getDriverStatus() == 3) {
                if (isAdded() && ispayshowing == false && activity.currentFragment.equals(Const.RATING_FRAGMENT)) {
                }
            }
            if (null != requestDetail.getCancellationFee() && !requestDetail.getCancellationFee().equals("0")) {
                tvCancellationFee.setVisibility(View.VISIBLE);
                tvCancellationFee.setText(getResources().getString(R.string.txt_trip_cancel_fee) + " " + requestDetail.getCurrnecy_unit() + " " + requestDetail.getCancellationFee());
            } else {
                tvCancellationFee.setVisibility(View.GONE);
            }
            if (requestDetail.getRequest_type().equals("1") || requestDetail.getRequest_type().equals("2")) {
                tollLayout.setVisibility(View.GONE);
            } else {
                tollLayout.setVisibility(View.VISIBLE);
                tvNoTolls.setText(getResources().getString(R.string.txt_toll) + ":" + " " + requestDetail.getNo_tolls());
            }
            if (requestDetail.getRequest_type().equals("2") || requestDetail.getRequest_type().equals("3")) {
                layoutDistance.setVisibility(View.GONE);
                ivFeedbackLocation.setVisibility(View.GONE);
            } else {
                layoutDistance.setVisibility(View.VISIBLE);
                ivFeedbackLocation.setVisibility(View.VISIBLE);
            }
            if (requestDetail.isFavoriteProvider()) {
                makeFav.setText(getString(R.string.removeFav));
                Glide.with(getActivity()).load(R.drawable.like).into(favIcon);
            } else {
                makeFav.setText(getString(R.string.make_favourite));
                Glide.with(getActivity()).load(R.drawable.unlike).into(favIcon);
            }

        }
    }

    public static String getGoogleMapThumbnail(double lati, double longi) {
        String staticMapUrl = "http://maps.google.com/maps/api/staticmap?center=" + lati + "," + longi + "&markers=" + lati + "," + longi + "&zoom=14&size=150x120&sensor=false&key=" + Const.GOOGLE_API_KEY;
        return staticMapUrl;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.currentFragment = Const.RATING_FRAGMENT;

    }


    protected void rateProvider() {
        disableStatusBtn();
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.rateProvider(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , requestDetail.getRequestId()
                , ""
                , rating);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject ratingResponse = null;
                try {
                    ratingResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ratingResponse != null) {
                    if (ratingResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.showShortToast(activity, ratingResponse.optString(APIConsts.Params.MESSAGE));
                        preferenceHelper.putIs_Ongoing(true);
                        Intent i = new Intent(activity, MainActivity.class);
                        startActivity(i);
                    } else {
                        UiUtils.showShortToast(getActivity(), ratingResponse.optString(APIConsts.Params.ERROR));
                        enableStatusBtn();
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

    public void enableStatusBtn() {
        btnSubmitRating.setEnabled(true);
        btnSubmitRating.setBackgroundColor(activity.getResources().getColor(R.color.black));
    }

    public void disableStatusBtn() {
        btnSubmitRating.setEnabled(false);
        btnSubmitRating.setBackgroundColor(activity.getResources().getColor(R.color.dark_grey));
    }

    protected void requestStatusCheck() {
        Call<String> call = apiInterface.pingRequestStatusCheck(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
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
                            prefUtils.setValue(PrefKeys.REQUEST_ID, requestDetail.getRequestId());
                            int reqId = prefUtils.getIntValue(PrefKeys.REQUEST_ID, requestDetail.getRequestId());
                            prefUtils.setValue(PrefKeys.PROVIDER_ID, requestDetail.getProviderId());

                            activity.runOnUiThread(() -> {
                                tvTotal.setText(requestDetail.getTrip_total_price() != null ? requestDetail.getCurrnecy_unit() + " " + requestDetail.getTrip_total_price() : "0.00");
                               // tvTotal.setText(getActivity().getText(R.string.taximetro));
                                textTime.setText(requestDetail.getTrip_time() != null ?requestDetail.getTrip_time() + " " + getResources().getString(R.string.min) : "0");
                                textDistance.setText(requestDetail.getTrip_distance() != null ? requestDetail.getTrip_distance() + " " + requestDetail.getDistance_unit() :  "0");
                            });

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

    private void cargarWebViewPasarelaDePago(RequestDetail requestDetail) {

        Intent intent = new Intent(getContext(), CreditCardPaymentWebActivity.class);
        intent.putExtra("requestId",requestDetail.getRequestId());
        startActivity(intent);

    }
}
