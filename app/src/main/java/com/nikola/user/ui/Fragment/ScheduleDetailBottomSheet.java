package com.nikola.user.ui.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomBoldRegularTextView;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Const;
import com.nikola.user.Utils.RecyclerLongPressClickListener;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.Models.CancelReason;
import com.nikola.user.ui.Adapter.CancelReasonAdapter;
import com.nikola.user.ui.activity.ChatActivity;
import com.nikola.user.ui.activity.HistoryActivity;
import com.nikola.user.ui.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleDetailBottomSheet extends DialogFragment {

    Unbinder unbinder;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    String requestId;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tripTime)
    CustomRegularTextView tripTime;
    @BindView(R.id.requestUniqueId)
    CustomRegularTextView requestUniqueId;
    @BindView(R.id.amount)
    CustomBoldRegularTextView amount;
    @BindView(R.id.mapImage)
    ImageView mapImage;
    @BindView(R.id.providerImage)
    CircleImageView providerImage;
    @BindView(R.id.providerName)
    CustomRegularTextView providerName;
    @BindView(R.id.rating)
    SimpleRatingBar rating;
    @BindView(R.id.serviceImage)
    CircleImageView serviceImage;
    @BindView(R.id.serviceName)
    CustomRegularTextView serviceName;
    @BindView(R.id.modelName)
    CustomRegularTextView modelName;
    @BindView(R.id.cancel_reason)
    CustomRegularTextView cancel_reason;
    @BindView(R.id.cancel_reason_layout)
    LinearLayout cancel_reason_layout;
    @BindView(R.id.sourceAddress)
    CustomRegularTextView sourceAddress;
    @BindView(R.id.destAddress)
    CustomRegularTextView destAddress;
    @BindView(R.id.ridefare)
    CustomRegularTextView ridefare;
    @BindView(R.id.serviceFee)
    CustomRegularTextView serviceFee;
    @BindView(R.id.cancellationFee)
    CustomRegularTextView cancellationFee;
    @BindView(R.id.discount)
    CustomRegularTextView discount;
    @BindView(R.id.total)
    CustomBoldRegularTextView total;
    @BindView(R.id.chat)
    CustomRegularTextView chat;
    @BindView(R.id.tack)
    CustomRegularTextView track;
    @BindView(R.id.cancel)
    CustomRegularTextView cancel;
    @BindView(R.id.taxes)
    CustomRegularTextView taxes;
    @BindView(R.id.providerLayout)
    RelativeLayout providerLayout;
    @BindView(R.id.invoiceBreakdown)
    ImageView invoiceBreakdown;
    @BindView(R.id.invoiceLayout)
    LinearLayout invoiceLayout;
    @BindView(R.id.rootLayout)
    ScrollView rootLayout;
    @BindView(R.id.buttonLayout)
    LinearLayout buttonLayout;
    @BindView(R.id.loader)
    ImageView loader;
    @BindView(R.id.payment_mode)
    CustomRegularTextView payment_mode;
    @BindView(R.id.topLine)
    View topLine;
    Dialog dialog;
    private ArrayList<CancelReason> cancelReasonLst = new ArrayList<>();
    boolean isHistory;
    boolean waitingBtnCancel;
    boolean redirectMainActivity;

    String basePrice, bookingFee, timePrice, timePriceBreakdown, distanceFee, distanceFeeBreakdown, surgePrice, debtPrice,
            discountPrice, taxPrice, totalAmt, paymentMethod, paymentId;

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setRedirection(boolean redirect) {
        this.redirectMainActivity = redirect;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.history_detail_view, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        Glide.with(getActivity()).load(R.raw.car).into(loader);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getActivity());
        getSingleResponse(requestId);
        back.setOnClickListener(view -> {
            if(redirectMainActivity) {
                startActivity(new Intent(getActivity(), MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
            dialog.dismiss();
                });
        invoiceBreakdown.setOnClickListener(view -> invoicBreakDown());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if(redirectMainActivity) {
                    startActivity(new Intent(getActivity(), MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
                dismiss();
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    private void getSingleResponse(String requestId) {
        loader.setVisibility(View.VISIBLE);
        rootLayout.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.GONE);
        Call<String> call = apiInterface.getRequestsView(prefUtils.getIntValue(PrefKeys.USER_ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                requestId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject singleResponse = null;
                try {
                    rootLayout.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);
                    buttonLayout.setVisibility(View.VISIBLE);
                    singleResponse = new JSONObject(response.body());
                    if (singleResponse != null) {
                        if (singleResponse.optString(APIConsts.Constants.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                            JSONObject data = singleResponse.optJSONObject(Const.Params.DATA);
                            isHistory = data.optInt(APIConsts.Params.LATER) == 0;
                            if(!isHistory){
                                if (data.optString("status_text").equalsIgnoreCase("Cancelled") || data.optString("status_text").equalsIgnoreCase("Completed")){
                                    isHistory = true;
                                }
                            }
                            tripTime.setText(data.optString(isHistory ? APIConsts.Params.REQUEST_CREATED_TIME : APIConsts.Params.SCHEDULED_TIME));
                            requestUniqueId.setText("#" + data.optString(APIConsts.Params.REQUEST_UNIQUE_ID));
                            paymentId = "#" + data.optString(APIConsts.Params.REQUEST_UNIQUE_ID);
                            sourceAddress.setText(data.optString(APIConsts.Params.S_ADDRESS));
                            destAddress.setText(data.optString(APIConsts.Params.D_ADDRESS).isEmpty() ? getResources().getString(R.string.not_available) : data.optString(APIConsts.Params.D_ADDRESS));
                            Glide.with(getActivity())
                                    .load(data.optString(APIConsts.Params.TYPE_PICTURE))
                                    .into(serviceImage);
                            Glide.with(getActivity())
                                    .load(data.optString(APIConsts.Params.REQUEST_MAP_IMAGE))
                                    .into(mapImage);
                            serviceName.setText(data.optString(APIConsts.Params.SERVICE_TYPE_NAME));
                            modelName.setText(data.optString(APIConsts.Params.SERVICE_MODEL));
                            if(!data.optString(APIConsts.Params.USER_CANCEL_REASON).equalsIgnoreCase(""))
                            {
                                cancel_reason_layout.setVisibility(View.VISIBLE);
                                cancel_reason.setText(getText(R.string.cancelBy)+" "+data.optString(APIConsts.Params.USER_CANCEL_REASON));
                            }
                            else if(!data.optString(APIConsts.Params.PROVIDER_CANCEL_REASON).equalsIgnoreCase(""))
                            {
                                cancel_reason_layout.setVisibility(View.VISIBLE);
                                cancel_reason.setText(getText(R.string.cancelBy)+" "+data.optString(APIConsts.Params.PROVIDER_CANCEL_REASON));
                            }
                            else
                                cancel_reason_layout.setVisibility(View.GONE);
                            try {
                                JSONObject providerDetails = data.getJSONObject(APIConsts.Params.PROVIDER_DETAILS);
                                if (providerDetails != null) {
                                    providerName.setText(providerDetails.optString(APIConsts.Params.PROVIDER_NAME));
                                    Glide.with(getActivity())
                                            .load(data.optString(APIConsts.Params.PROVIDER_PICTURE))
                                            .into(providerImage);
                                } else {
                                    providerLayout.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                providerLayout.setVisibility(View.GONE);
                            }
                            JSONObject invoiceDetails = data.getJSONObject(APIConsts.Params.INVOICE_DETAILS);
                            ridefare.setText(invoiceDetails.optString(APIConsts.Params.RIDE_FARE));
                            serviceFee.setText(invoiceDetails.optString(APIConsts.Params.SERVICE_FARE_FORMATTED));
                            cancellationFee.setText(invoiceDetails.optString(APIConsts.Params.CANCELLATION_FARE_FORMATTED));
                            discount.setText(invoiceDetails.optString(APIConsts.Params.DISCOUNT));
                            Log.e("Discount",invoiceDetails.optString(APIConsts.Params.DISCOUNT));

                            total.setText(invoiceDetails.optString(APIConsts.Params.TOTAL_FORMATTED));
                            taxes.setText(String.format("Includes %s taxes", invoiceDetails.optString(APIConsts.Params.TAX_FARE_FORMATTED)));
                            rating.setRating(data.optInt(APIConsts.Params.RATING));
                            basePrice = invoiceDetails.optString(APIConsts.Params.BASE_PRICE_FORMATTED);
                            bookingFee = invoiceDetails.optString(APIConsts.Params.BOOKING_FEE_FORMATTED);
                            timePrice = invoiceDetails.optString(APIConsts.Params.TIME_PRICE_FORMATTED);
                            distanceFee = invoiceDetails.optString(APIConsts.Params.DISTANCE_PRICE_FORMATTED);
                            surgePrice = invoiceDetails.optString(APIConsts.Params.SURGE_PRICE_FORMATTED);
                            debtPrice = invoiceDetails.optString(APIConsts.Params.DEBT_AMOUNT_FORMATTED);
                            discountPrice = String.format("- %s", invoiceDetails.optString(APIConsts.Params.DISCOUNT));
                            taxPrice = invoiceDetails.optString(APIConsts.Params.TAX_FARE_FORMATTED);
                            totalAmt = invoiceDetails.optString(APIConsts.Params.TOTAL_FORMATTED);
                            paymentMethod = invoiceDetails.optString(APIConsts.Params.PAYMENT_MODE);
                            payment_mode.setText(invoiceDetails.optString(APIConsts.Params.PAYMENT_MODE));
                            amount.setText(invoiceDetails.optString(APIConsts.Params.TOTAL_FORMATTED));
                            timePriceBreakdown = String.format("(%s)", invoiceDetails.optString(APIConsts.Params.TIME_PRICE_NOTE));
                            distanceFeeBreakdown = String.format("(%s)", invoiceDetails.optString(APIConsts.Params.DISTANCE_PRICE_NOTE));
                            JSONObject requestBtnStatus = data.optJSONObject(APIConsts.Params.REQUEST_BUTTON_STATUS);
                            track.setVisibility(requestBtnStatus.optInt(APIConsts.Params.TRACK_STATUS) == 0 ? View.GONE : View.VISIBLE);
                            chat.setVisibility(requestBtnStatus.optInt(APIConsts.Params.MESSAGE_BTN_STATUS) == 0 ? View.GONE : View.VISIBLE);
                            invoiceLayout.setVisibility(requestBtnStatus.optInt(APIConsts.Params.INVOICE_BUTTON_STATUS) == 0 ? View.GONE : View.VISIBLE);
                            cancel.setVisibility(requestBtnStatus.optInt(APIConsts.Params.WAITING_BTN_CANCEL) == 1 || requestBtnStatus.optInt(APIConsts.Params.CANCEL_BUTTON_STATUS) == 1 ? View.VISIBLE : View.GONE  );
                            waitingBtnCancel = requestBtnStatus.optInt(APIConsts.Params.WAITING_BTN_CANCEL) == 1;
                            if (waitingBtnCancel) {
                                isHistory = false;
                            }

                            if(data.optString("status_text").equalsIgnoreCase("Completed")){
                                buttonLayout.setVisibility(View.GONE);
                            } else {
                                buttonLayout.setVisibility(View.VISIBLE);
                            }

                            if(cancel.getVisibility() == View.GONE && track.getVisibility() == View.GONE && chat.getVisibility() == View.GONE) {
                                topLine.setVisibility(View.GONE);
                            }
                        } else {
                            UiUtils.showShortToast(getActivity(), singleResponse.optString(APIConsts.Params.ERROR_MESSAGE));
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void invoicBreakDown() {
        dialog = new Dialog(getActivity(), R.style.AppTheme);
        dialog.setContentView(R.layout.dialog_invoice_breakdown);
        CustomRegularTextView basePriceView, bookingFeeView, timePriceView, timePriceBreakdownView, distanceFeeView, distanceFeeBreakdownView,
                surgePriceView, debtPriceView, discountPriceView, taxPriceView, paymentMethodView, paymentIdView, cancellationFeeText;
        CustomBoldRegularTextView totalAmtView;
        ImageView back = dialog.findViewById(R.id.back);
        basePriceView = dialog.findViewById(R.id.baseFare);
        bookingFeeView = dialog.findViewById(R.id.bookingFare);
        timePriceView = dialog.findViewById(R.id.timePrice);
        timePriceBreakdownView = dialog.findViewById(R.id.timeBreakdown);
        distanceFeeView = dialog.findViewById(R.id.distance);
        distanceFeeBreakdownView = dialog.findViewById(R.id.distanceBreakdown);
        surgePriceView = dialog.findViewById(R.id.surgePrice);
        debtPriceView = dialog.findViewById(R.id.debtPrice);
        discountPriceView = dialog.findViewById(R.id.discount);
        taxPriceView = dialog.findViewById(R.id.taxPrice);
        totalAmtView = dialog.findViewById(R.id.total);
        paymentMethodView = dialog.findViewById(R.id.paymentMode);
        paymentIdView = dialog.findViewById(R.id.paymentId);
        cancellationFeeText = dialog.findViewById(R.id.cancellationFee);
        basePriceView.setText(basePrice);
        bookingFeeView.setText(bookingFee);
        timePriceView.setText(timePrice);
        timePriceBreakdownView.setText(timePriceBreakdown);
        distanceFeeView.setText(distanceFee);
        distanceFeeBreakdownView.setText(distanceFeeBreakdown);
        surgePriceView.setText(surgePrice);
        debtPriceView.setText(debtPrice);
        discountPriceView.setText(discountPrice);
        taxPriceView.setText(taxPrice);
        totalAmtView.setText(totalAmt);
        paymentMethodView.setText(paymentMethod);
        paymentIdView.setText(paymentId);
        cancellationFeeText.setText(cancellationFee.getText().toString());
        back.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    @OnClick({R.id.chat, R.id.tack, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.chat:
                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                startActivity(chatIntent);
                break;
            case R.id.cancel:
                if (isHistory) {
                    final iOSDialog cancelDialog = new iOSDialog(getActivity());
                    cancelDialog.setTitle(getResources().getString(R.string.txt_cancel_ride));
                    cancelDialog.setSubtitle(getResources().getString(R.string.cancel_txt));
                    cancelDialog.setNegativeLabel(getResources().getString(R.string.txt_no));
                    cancelDialog.setPositiveLabel(getResources().getString(R.string.txt_yes));
                    cancelDialog.setBoldPositiveLabel(false);
                    cancelDialog.setNegativeListener(view1 -> cancelDialog.dismiss());
                    cancelDialog.setPositiveListener(view12 -> {
                        getCancelReasons("",false);
                        cancelDialog.dismiss();
                    });
                    cancelDialog.show();
                } else if (waitingBtnCancel) {
                    cancelWaitingRequest();
                } else {
                    getCancelReasons(requestId,true);

                }
                break;
            case R.id.tack:
                Intent i = new Intent(getActivity(), MainActivity.class);
                i.getBooleanExtra("isOngoing", true);
                startActivity(i);
                break;
        }
    }

    protected void cancelWaitingRequest() {
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
                if (cancelResponse.optString(APIConsts.Constants.SUCCESS).equalsIgnoreCase(APIConsts.Constants.TRUE)) {
                    UiUtils.showLongToast(getActivity(), cancelResponse.optString(APIConsts.Params.MESSAGE));
                    dismiss();
                    startActivity(new Intent(getActivity(), HistoryActivity.class).putExtra("isHistory", true)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

    protected void getCancelReasons(String requestId, boolean isOngoing) {
        Call<String> call = apiInterface.cancelReasonsList(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                cancelReasonLst.clear();
                JSONObject cancelReasonsResponse = null;
                try {
                    cancelReasonsResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cancelReasonsResponse != null) {
                    if (cancelReasonsResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONArray cancelReasonArray = cancelReasonsResponse.optJSONArray(APIConsts.Params.DATA);
                        if (null != cancelReasonArray && cancelReasonArray.length() > 0)
                            for (int i = 0; i < cancelReasonArray.length(); i++) {
                                JSONObject dataObj = cancelReasonArray.optJSONObject(i);
                                CancelReason cancel = new CancelReason();
                                cancel.setReasonId(dataObj.optString(Const.Params.REASON_ID));
                                cancel.setReasontext(dataObj.optString(Const.Params.CANCEL_RESON));
                                cancelReasonLst.add(cancel);
                            }
                        if (null != cancelReasonLst && cancelReasonLst.size() > 0) {
                            CancelReasonDialog(cancelReasonLst,isOngoing,requestId);
                        } else {
                            AndyUtils.showShortToast(getResources().getString(R.string.txt_no_cancel_reason), getActivity());
                        }
                    } else {
                        UiUtils.showShortToast(getActivity(), cancelReasonsResponse.optString(APIConsts.Params.ERROR));
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

    private void CancelReasonDialog(final ArrayList<CancelReason> cancelReasonLst, boolean onGoing, String requestId) {
        final Dialog CancelReasondialog = new Dialog(getActivity(), R.style.DialogThemeforview);
        CancelReasondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CancelReasondialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        CancelReasondialog.setCancelable(false);
        CancelReasondialog.setContentView(R.layout.cancel_request_layout);
        RecyclerView cancel_reason_lst = CancelReasondialog.findViewById(R.id.cancel_reason_lst);
        CancelReasonAdapter CancelAdapter = new CancelReasonAdapter(getActivity(), cancelReasonLst);
        cancel_reason_lst.setLayoutManager(new LinearLayoutManager(getActivity()));
        cancel_reason_lst.setAdapter(CancelAdapter);
        cancel_reason_lst.addOnItemTouchListener(new RecyclerLongPressClickListener(getActivity(), cancel_reason_lst, new RecyclerLongPressClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!onGoing){
                    cancelOngoingRide(cancelReasonLst.get(position).getReasonId(), cancelReasonLst.get(position).getReasontext());
                    CancelReasondialog.dismiss();
                } else {
                    cancelRequest(requestId);
                    CancelReasondialog.dismiss();
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
        CancelReasondialog.show();
    }

    protected void cancelOngoingRide(String reason_id, String reasontext) {
        Call<String> call = apiInterface.cancelOngoingRide(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , Integer.parseInt(requestId)
                , reason_id
                , reasontext);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject cancelResponse = null;
                try {
                    cancelResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cancelResponse != null) {
                    if (cancelResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
                    } else {
                        UiUtils.showShortToast(getActivity(), cancelResponse.optString(APIConsts.Params.ERROR));
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

    public void cancelRequest(String requestId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.confirmation).setMessage(R.string.delete_message).setPositiveButton(R.string.txt_yes, (dialog, which) -> {
            cancelALaterRequest(requestId);
        }).setNegativeButton(R.string.no, (dialog, which) -> {
            dialog.dismiss();
        }).show();
    }

    private void cancelALaterRequest(String requestId) {
        Call<String> call = apiInterface.cancelLaterRequest(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , requestId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject laterResponse = null;
                try {
                    laterResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (laterResponse != null) {
                    if (laterResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        dismiss();
                        UiUtils.showShortToast(getActivity(), laterResponse.optString(APIConsts.Params.MESSAGE));
                        startActivity(new Intent(getActivity(), HistoryActivity.class).putExtra("isHistory", isHistory)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        UiUtils.showShortToast(getActivity(), laterResponse.optString(APIConsts.Params.ERROR));
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

}
