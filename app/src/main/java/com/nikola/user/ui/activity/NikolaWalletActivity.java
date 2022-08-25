package com.nikola.user.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nikola.user.R;
import com.nikola.user.RealmController.RealmController;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Const;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularEditView;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.network.ApiManager.AsyncTaskCompleteListener;
import com.nikola.user.network.ApiManager.VollyRequester;
import com.nikola.user.network.Models.User;
import com.nikola.user.network.Models.Wallets;
import com.nikola.user.ui.Adapter.WalletAdapter;
//import com.paypal.android.sdk.payments.PayPalAuthorization;
//import com.paypal.android.sdk.payments.PayPalConfiguration;
//import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
//import com.paypal.android.sdk.payments.PayPalPayment;
//import com.paypal.android.sdk.payments.PayPalService;
//import com.paypal.android.sdk.payments.PaymentActivity;
//import com.paypal.android.sdk.payments.PaymentConfirmation;
//import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
//import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by Carlos on 29/8/2017.
 */

public class NikolaWalletActivity extends AppCompatActivity implements AsyncTaskCompleteListener {
    @BindView(R.id.toolbar_wallet)
    Toolbar toolbarWallet;
    @BindView(R.id.tv_total_balance)
    CustomRegularTextView tvTotalBalance;
    @BindView(R.id.et_enter_amount)
    CustomRegularEditView etEnterAmount;
    @BindView(R.id.btn_add_money)
    Button btnAddMoney;
    private String CONFIG_ENVIRONMENT = "";
    private String CONFIG_CLIENT_ID = "";
    private String MODE = "";
    private Realm realm;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    //PayPalPayment addMoneytoWallet;
    private ArrayList<Wallets> walletLst = new ArrayList<Wallets>();
    //private static PayPalConfiguration config;
    User userprofile;
    private String WALLET_SECRET_KEY = "", STRIPE_KEY = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.nikola_wallet);
        ButterKnife.bind(this);
        WALLET_SECRET_KEY = new PreferenceHelper(this).getWallet_key();
        this.realm = RealmController.with(this).getRealm();
        RealmController.with(this).refresh();
        userprofile = RealmController.with(this).getUser(Integer.valueOf(new PreferenceHelper(this).getUserId()));
        setSupportActionBar(toolbarWallet);
        getSupportActionBar().setTitle(null);
        if (!TextUtils.isEmpty(WALLET_SECRET_KEY)) {
            getWalletTypes();
            getWalletBalance();
        }
        btnAddMoney.setEnabled(false);
        btnAddMoney.setBackgroundColor(getResources().getColor(R.color.light_grey));
    }

    private void getWalletBalance() {
        if (!AndyUtils.isNetworkAvailable(this))
            return;
        if (userprofile != null) {
            AndyUtils.showSimpleProgressDialog(this, getString(R.string.txt_load), false);
            HashMap<String, String> map = new HashMap<String, String>();
            HashMap<String, String> mapHead = new HashMap<String, String>();
            try {
                map.put(Const.Params.URL, Const.ServiceType.WALLET_BALANCE + new PreferenceHelper(this).getUserId() + "/balance?" + "name=" + URLEncoder.encode(userprofile.getFname() + " " + userprofile.getLname(), "utf-8") + "&full_mobile_no=" + userprofile.getPhone() + "&email=" + userprofile.getEmail());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mapHead.put("authorization", WALLET_SECRET_KEY);
            Log.d("mahi", map.toString());
            new VollyRequester(this, Const.GET, map, Const.ServiceCode.WALLET_BALANCE, this, mapHead);
        }
    }

    private void getWalletTypes() {
        if (!AndyUtils.isNetworkAvailable(this))
            return;
        HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, String> mapHead = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.WALLET_TYPES);
        mapHead.put("Authorization", WALLET_SECRET_KEY);
        Log.d("mahi", map.toString());
        new VollyRequester(this, Const.GET, map, Const.ServiceCode.WALLET_TYPES, this, mapHead);

    }

    private void showWalletOptions(final ArrayList<Wallets> walletLst) {
        final Dialog dialog = new Dialog(this, R.style.DialogThemeforview);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.wallet_popup);
        GridView grid_wallet = dialog.findViewById(R.id.grid_wallet);
        WalletAdapter Adapter = new WalletAdapter(this, walletLst);
        grid_wallet.setAdapter(Adapter);

        grid_wallet.setOnItemClickListener((parent, view, position, id) -> {
            switch (walletLst.get(position).getGateway_name()) {
                /*case Const.paypal:
                    if (TextUtils.isEmpty(CONFIG_CLIENT_ID)) {
                        AndyUtils.showShortToast(getResources().getString(R.string.error_payment_key), NikolaWalletActivity.this);
                        dialog.dismiss();
                    } else {
                        payBypaypal(etEnterAmount.getText().toString());
                        dialog.dismiss();
                    }
                    break;*/
                case Const.paygate:
                    dialog.dismiss();
                    //showPayGate();
                    break;
                case Const.stripe:
                    dialog.dismiss();
                    //showStripe();
                    break;
            }
        });
        dialog.show();
    }
/*
    private void showStripe() {
        final Dialog payStripe = new Dialog(NikolaWalletActivity.this, R.style.DialogThemeforview);
        payStripe.requestWindowFeature(Window.FEATURE_NO_TITLE);
        payStripe.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        payStripe.setCancelable(true);
        payStripe.setContentView(R.layout.strip_layout);
        final CardInputWidget mCardInputWidget = payStripe.findViewById(R.id.card_input_widget_stripe);

        final Stripe stripe = new Stripe(NikolaWalletActivity.this, STRIPE_KEY);


        payStripe.findViewById(R.id.confirm_button).setOnClickListener(view -> {
            if (null == mCardInputWidget.getCard()) {
                AndyUtils.showShortToast(getResources().getString(R.string.error_card), NikolaWalletActivity.this);
            } else if (null != mCardInputWidget.getCard().getNumber() && null != mCardInputWidget.getCard().getExpMonth() && null != mCardInputWidget.getCard().getExpYear() && null != mCardInputWidget.getCard().getCvcCheck()) {
                AndyUtils.showSimpleProgressDialog(NikolaWalletActivity.this, getResources().getString(R.string.txt_load), false);
                Card card = new Card(mCardInputWidget.getCard().getNumber(), mCardInputWidget.getCard().getExpMonth(), mCardInputWidget.getCard().getExpYear(), mCardInputWidget.getCard().getCvcCheck());
                if (card.validateNumber()) {
                    stripe.createCardToken(
                            card,
                            new ApiResultCallback<>() {
                                public void onSuccess(Token token) {
                                    AndyUtils.removeProgressDialog();
                                    CreditBalance(token.getId(), getString(R.string.stripe));
                                    payStripe.dismiss();
                                }

                                public void onError(Exception error) {
                                    Toast.makeText(NikolaWalletActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                }
            } else {
                AndyUtils.showShortToast(getResources().getString(R.string.error_card), NikolaWalletActivity.this);
            }

        });
        payStripe.show();
    }


    private void showPayGate() {
        final Dialog payGatedialog = new Dialog(NikolaWalletActivity.this, R.style.DialogThemeforview);
        payGatedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        payGatedialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        payGatedialog.setCancelable(true);
        payGatedialog.setContentView(R.layout.creditcard_view);
        FloatingActionButton btn_floating_ok = payGatedialog.findViewById(R.id.btn_floating_ok);
        final EditText et_card_no = payGatedialog.findViewById(R.id.et_card_no);
        final EditText et_card_month = payGatedialog.findViewById(R.id.et_card_month);
        final EditText et_card_year = payGatedialog.findViewById(R.id.et_card_year);
        final EditText et_card_ccv = payGatedialog.findViewById(R.id.et_card_ccv);
        final EditText et_card_holder_fname = payGatedialog.findViewById(R.id.et_card_holder_fname);
        final EditText et_card_holder_lname = payGatedialog.findViewById(R.id.et_card_holder_lname);
        final EditText et_card_holder_email = payGatedialog.findViewById(R.id.et_card_holder_email);

        btn_floating_ok.setOnClickListener(v -> {
            if (TextUtils.isEmpty(et_card_no.getText().toString())) {
                AndyUtils.showShortToast(getResources().getString(R.string.txt_enter_card), NikolaWalletActivity.this);
                et_card_no.requestFocus();
            } else if (TextUtils.isEmpty(et_card_month.getText().toString())) {
                AndyUtils.showShortToast(getResources().getString(R.string.txt_error_month), NikolaWalletActivity.this);
                et_card_month.requestFocus();
            } else if (TextUtils.isEmpty(et_card_year.getText().toString())) {
                AndyUtils.showShortToast(getResources().getString(R.string.txt_error_year), NikolaWalletActivity.this);
                et_card_year.requestFocus();
            } else if (TextUtils.isEmpty(et_card_ccv.getText().toString())) {
                AndyUtils.showShortToast(getResources().getString(R.string.txt_error_ccv), NikolaWalletActivity.this);
                et_card_ccv.requestFocus();
            } else if (TextUtils.isEmpty(et_card_holder_fname.getText().toString())) {
                AndyUtils.showShortToast(getResources().getString(R.string.txt_fname_error), NikolaWalletActivity.this);
                et_card_holder_fname.requestFocus();
            } else if (TextUtils.isEmpty(et_card_holder_lname.getText().toString())) {
                AndyUtils.showShortToast(getResources().getString(R.string.txt_lname_error), NikolaWalletActivity.this);
                et_card_holder_lname.requestFocus();
            } else if (TextUtils.isEmpty(et_card_holder_email.getText().toString())) {
                AndyUtils.showShortToast(getResources().getString(R.string.txt_email_error), NikolaWalletActivity.this);
                et_card_holder_email.requestFocus();
            } else {
                payGatedialog.dismiss();
                sendpayGateData(et_card_no.getText().toString(), et_card_month.getText().toString(), et_card_year.getText().toString(), et_card_ccv.getText().toString(), et_card_holder_fname.getText().toString(), et_card_holder_lname.getText().toString(), et_card_holder_email.getText().toString());
            }
        });
        payGatedialog.show();
    }

    private void sendpayGateData(String cardNo, String cardMonth, String cardYear, String cardCVV, String Fname, String Lname, String email) {
        if (!AndyUtils.isNetworkAvailable(this))
            return;
        if (userprofile != null) {
            AndyUtils.showSimpleProgressDialog(this, "Loading...", false);
            HashMap<String, String> map = new HashMap<String, String>();
            HashMap<String, String> mapHead = new HashMap<String, String>();
            map.put(Const.Params.URL, Const.ServiceType.WALLET_BALANCE + new PreferenceHelper(this).getUserId() + "/paygate/initiate");
            map.put(Const.Params.FIRSTNAME, Fname);
            map.put(Const.Params.LAST_NAME, Lname);
            map.put(Const.Params.EMAIL, email);
            map.put("amount", etEnterAmount.getText().toString());
            mapHead.put("authorization", WALLET_SECRET_KEY);
            Log.d("mahi", map.toString());
            new VollyRequester(this, Const.POST, map, Const.ServiceCode.WALLET_PAYGATE, this, mapHead);
        }

    }*/

    /*private void payBypaypal(String amount) {
        addMoneytoWallet = new PayPalPayment(new BigDecimal(amount), "USD", "Wallet", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(NikolaWalletActivity.this, PaymentActivity.class);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, addMoneytoWallet);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    JSONObject job = confirm.toJSONObject();
                    JSONObject responseOBJ = job.optJSONObject("response");
                    String TRX_ID = responseOBJ.optString("id");
                    if (null != TRX_ID && !(TRX_ID.length() == 0)) {
                        CreditBalance(TRX_ID, "PAYPAL");
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println(R.string.user_is_cancelled);
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                System.out.println(R.string.an_invalid_payment_or_payapalconfiguration_was_submitted);
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject()
                                .toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

//                        sendAuthorizationToServer(auth);
                        Toast.makeText(getApplicationContext(),
                                "Future Payment code received from PayPal",
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample",
                                "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }*/

    private void CreditBalance(String trx_id, String paymentType) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            return;
        }
        AndyUtils.showSimpleProgressDialog(this, "Adding Amount...", false);
        HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, String> mapHead = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.WALLET_HOST_URL + "/api/businesses/users/" + new PreferenceHelper(this).getUserId() + "/balance/credit");
        map.put(Const.Params.GATEWAY, paymentType);
        map.put(Const.Params.PAYMENT_ID, trx_id);
        if (paymentType.equals(getString(R.string.stripe)))
            map.put(Const.Params.AMOUNT, etEnterAmount.getText().toString());
        mapHead.put(Const.Params.AUTHORIZATION, WALLET_SECRET_KEY);
        new VollyRequester(this, Const.POST, map, Const.ServiceCode.WALLET_CREDIT, this, mapHead);
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        //stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.WALLET_TYPES:
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString(Const.Params.SUCCESS).equals(Const.Params.TRUE)) {
                        walletLst.clear();
                        JSONObject dataObj = job.getJSONObject(Const.Params.DATA);
                        JSONArray gatwayArray = dataObj.getJSONArray(Const.Params.GATEWAYS);
                        for (int i = 0; i < gatwayArray.length(); i++) {
                            Wallets wall = new Wallets();
                            JSONObject wallObj = gatwayArray.getJSONObject(i);
                            wall.setGateway_id(wallObj.optString("gateway_id"));
                            wall.setGateway_name(wallObj.optString("gateway_name"));
                            JSONObject settingObj = wallObj.optJSONObject("settings");
                            if (settingObj.has("PAYPAL_CLIENT_ID")) {
                                CONFIG_CLIENT_ID = settingObj.optString("PAYPAL_CLIENT_ID");
                            }
                            if (settingObj.has("PAYPAL_LIVE_MODE")) {
                                MODE = settingObj.optString("PAYPAL_LIVE_MODE");
                            }
                            if (settingObj.has("stripe_api_publishable_key")) {
                                STRIPE_KEY = settingObj.optString("stripe_api_publishable_key");
                            }

                            walletLst.add(wall);
                        }
                        if (TextUtils.isEmpty(CONFIG_CLIENT_ID)) {
                            btnAddMoney.setEnabled(true);
                            btnAddMoney.setBackgroundColor(getResources().getColor(R.color.black));
                            AndyUtils.removeProgressDialog();
                            AndyUtils.showShortToast(getResources().getString(R.string.error_payment_key), this);
                        } else {
                            //IntitiatePayPal();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case Const.ServiceCode.WALLET_BALANCE:

                Log.d("wallet", "balance" + response);
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        JSONObject data = job.getJSONObject("data");
                        tvTotalBalance.setText("$" + " " + data.optString("user_balance"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case Const.ServiceCode.WALLET_CREDIT:
                AndyUtils.removeProgressDialog();
                Log.d("wallet", "balance credit" + response);
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        JSONObject data = job.getJSONObject("data");
                        tvTotalBalance.setText("$" + " " + data.optString("user_balance"));
                        etEnterAmount.setText("");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_money_success),
                                Toast.LENGTH_LONG).show();
                    } else {

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.fail_add_money),
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case Const.ServiceCode.WALLET_PAYGATE:
                AndyUtils.removeProgressDialog();
                Log.d("wallet", "Paygate URL" + response);
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        JSONObject data = job.getJSONObject("data");
                        String URl = data.optString("payment_start_url");

                        Intent intent = new Intent(NikolaWalletActivity.this, PayGateWeb.class);
                        intent.putExtra("URl", URl);// pass your values and retrieve them in the other Activity using keyName
                        startActivity(intent);
                        this.finish();
                    } else {
                        String text = job.optString("text");
                        Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    /*private void IntitiatePayPal() {
        if (MODE.equals("true")) {
            CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
        } else {
            CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
        }
        if (!TextUtils.isEmpty(CONFIG_CLIENT_ID))
            config = new PayPalConfiguration()
                    .environment(CONFIG_ENVIRONMENT)
                    .clientId(CONFIG_CLIENT_ID);
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent(NikolaWalletActivity.this, PayPalService.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                startService(intent);
                btnAddMoney.setEnabled(true);
                btnAddMoney.setBackgroundColor(getResources().getColor(R.color.black));
                AndyUtils.removeProgressDialog();
            }
        }.start();
    }*/

    @OnClick({R.id.wallet_back, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.btn_add_money})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.wallet_back:
                onBackPressed();
                break;
            case R.id.tv_1:
                etEnterAmount.setText("");
                etEnterAmount.setText(getString(R.string.twoHundred));
                etEnterAmount.setSelection(etEnterAmount.getText().toString().length());
                break;
            case R.id.tv_2:
                etEnterAmount.setText("");
                etEnterAmount.setText(getString(R.string.fiveHundred));
                etEnterAmount.setSelection(etEnterAmount.getText().toString().length());
                break;
            case R.id.tv_3:
                etEnterAmount.setText("");
                etEnterAmount.setText(getString(R.string.thousand));
                etEnterAmount.setSelection(etEnterAmount.getText().toString().length());
                break;
            case R.id.btn_add_money:
                if (TextUtils.isEmpty(etEnterAmount.getText().toString())) {
                    etEnterAmount.setError(getResources().getString(R.string.error_empty_amount));
                    etEnterAmount.requestFocus();
                } else {
                    if (null != walletLst && walletLst.size() > 0) {
                        showWalletOptions(walletLst);
                    } else {
                        AndyUtils.showShortToast(getResources().getString(R.string.error_no_wallet_gateway), this);
                    }

                }
                break;
        }
    }
}
