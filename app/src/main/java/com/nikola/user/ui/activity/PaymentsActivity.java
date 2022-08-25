package com.nikola.user.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.Models.CreditCard;
import com.nikola.user.network.Models.PaymentMode;
import com.nikola.user.ui.Adapter.CreditCardAdapter;
import com.nikola.user.ui.Adapter.DefaultPaymentModesAdapter;
import com.stripe.android.Stripe;
//import com.stripe.android.ApiResultCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PaymentsActivity extends BaseActivity implements CreditCardAdapter.CreditCardInterface, DefaultPaymentModesAdapter.Refresh, CreditCardAdapter.GetDefaultCards {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cardsRecycler)
    RecyclerView cardsRecycler;
    @BindView(R.id.empty_cards_layout)
    View noCardsLayout;
    @BindView(R.id.add_card)
    TextView addCard;

    @BindView(R.id.emptyImage)
    ImageView emptyIcon;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;

    @BindView(R.id.savedCardsLayout)
    LinearLayout savedCardsLayout;

    CreditCardAdapter cardAdapter;
    APIInterface apiInterface;
    ArrayList<CreditCard> creditCards = new ArrayList<>();
    ArrayList<PaymentMode> paymentModes = new ArrayList<>();
    @BindView(R.id.paymentModesRecycler)
    RecyclerView paymentModesRecycler;
    @BindView(R.id.empty_payment_mode_layout)
    TextView emptyPaymentModeLayout;
    private Stripe stripe;
    private static final String IS_CHOOSING = "isChoosing";
    private boolean isChoosing;


    DefaultPaymentModesAdapter defaultPaymentModesAdapter;

    public static Intent getCallingIntent(Context context, boolean isChoosing) {
        Intent intent = new Intent(context, PaymentsActivity.class);
        intent.putExtra(IS_CHOOSING, isChoosing);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        initStripe();
        setupToolbar();
        setupCardsList();
        setupPaymentsList();
        getAllPaymentMethods();
        Intent intent = getIntent();
        isChoosing = intent.getBooleanExtra(IS_CHOOSING, false);
        Glide.with(getApplicationContext()).load(R.drawable.box).into(emptyIcon);
    }

    private void initStripe() {
        String stripeKey = getString(R.string.stripe_pk);
        stripe = new Stripe(this, stripeKey);
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.payments_text));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.back));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void setupCardsList() {
        LinearLayoutManager cardsLayoutManager = new LinearLayoutManager(this);
        cardsRecycler.setLayoutManager(cardsLayoutManager);
        cardsRecycler.setHasFixedSize(true);
        cardAdapter = new CreditCardAdapter(this, creditCards, this);
        cardsRecycler.setAdapter(cardAdapter);
    }

    private void setupPaymentsList() {
        LinearLayoutManager paymentsLayoutManager = new LinearLayoutManager(this);
        paymentModesRecycler.setLayoutManager(paymentsLayoutManager);
        DividerItemDecoration paymentsDivider = new DividerItemDecoration(paymentModesRecycler.getContext(),
                paymentsLayoutManager.getOrientation());
        paymentModesRecycler.addItemDecoration(paymentsDivider);
        paymentModesRecycler.setHasFixedSize(true);
        defaultPaymentModesAdapter = new DefaultPaymentModesAdapter(this, paymentModes, this);
        paymentModesRecycler.setAdapter(defaultPaymentModesAdapter);
    }

    @OnClick(R.id.add_card)
    protected void addCard() {
        //TODO show stripe stuff here
        View view = getLayoutInflater().inflate(R.layout.dialog_add_card, null);
        CardInputWidget cardInputWidget = view.findViewById(R.id.card_input_widget);
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_card))
                .setView(view)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    Card cardToSave = cardInputWidget.getCard();
                    if (cardToSave == null) {
                        dialog.cancel();
                        UiUtils.showShortToast(PaymentsActivity.this, getString(R.string.invalid_card));
                    } else {
                        dialog.cancel();
                        addStripeCard(cardToSave);
                    }
                })
                .setNegativeButton(getString(R.string.no), ((dialog, which) -> {
                }))
                .create().show();
    }

    protected void getAllPaymentMethods() {
        UiUtils.showLoadingDialog(this);
        paymentModes.clear();
        creditCards.clear();
        Call<String> call = apiInterface.getAllCards(
                PrefUtils.getInstance(this).getIntValue(PrefKeys.USER_ID, 0)
                , PrefUtils.getInstance(this).getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject paymentMethodsResp = null;
                try {
                    paymentMethodsResp = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paymentMethodsResp != null) {
                    if (paymentMethodsResp.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONObject data = paymentMethodsResp.optJSONObject(APIConsts.Params.DATA);
                        JSONArray paymentMethods = data.optJSONArray(APIConsts.Params.PAYMENT_MODES);
                        JSONArray cards = data.optJSONArray(APIConsts.Params.CARDS);
                        parsePaymentModes(paymentMethods);
                        parseCards(cards);

                    } else {
                        UiUtils.showShortToast(PaymentsActivity.this, paymentMethodsResp.optString(APIConsts.Params.ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    private void parsePaymentModes(JSONArray paymentMethods) {
        for (int i = 0; i < paymentMethods.length(); i++) {
            JSONObject paymentObj = null;
            try {
                paymentObj = paymentMethods.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (paymentObj != null) {
                paymentModes.add(new PaymentMode(i
                        , paymentObj.optString(APIConsts.Params.NAME)
                        , paymentObj.optString(APIConsts.Params.PAYMENT_MODE_IMAGE)
                        , paymentObj.optInt(APIConsts.Params.IS_DEFAULT) == 1
                ));
            }
        }
        defaultPaymentModesAdapter.notifyDataSetChanged();
        boolean isEmptyData = paymentModes.size() == 0;
        paymentModesRecycler.setVisibility(isEmptyData ? View.GONE : View.VISIBLE);
        emptyPaymentModeLayout.setVisibility(isEmptyData ? View.VISIBLE : View.GONE);
    }


    private void parseCards(JSONArray cards) {
        for (int i = 0; i < cards.length(); i++) {
            JSONObject cardObj = null;
            try {
                cardObj = cards.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (cardObj != null) {
                creditCards.add(new CreditCard(cardObj.optString(APIConsts.Params.USER_CARD_ID)
                        , cardObj.optString(APIConsts.Params.CARD_LAST_FOUR)
                        , cardObj.optString(APIConsts.Params.CARD_NAME)
                        , cardObj.optString(APIConsts.Params.IS_DEFAULT).equals("1")
                ));
            }
        }
        cardAdapter.notifyDataSetChanged();
        boolean isEmptyData = creditCards.size() == 0;
        cardsRecycler.setVisibility(isEmptyData ? View.GONE : View.VISIBLE);
        emptyLayout.setVisibility(isEmptyData ? View.VISIBLE : View.GONE);
    }

    protected void addCardInBackend(String cardToken) {
        Call<String> call = apiInterface.addCard(
                PrefUtils.getInstance(this).getIntValue(PrefKeys.USER_ID, 0)
                , PrefUtils.getInstance(this).getStringValue(PrefKeys.SESSION_TOKEN, "")
                , cardToken);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject addCardResponse = null;
                try {
                    addCardResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (addCardResponse != null) {
                    if (addCardResponse.optString(APIConsts.Constants.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.showShortToast(PaymentsActivity.this, addCardResponse.optString(APIConsts.Params.MESSAGE));
                        JSONObject addedCard = addCardResponse.optJSONObject(APIConsts.Params.DATA);
                        if (addedCard != null) {
                            creditCards.add(new CreditCard(addedCard.optString(APIConsts.Params.USER_CARD_ID)
                                    , addedCard.optString(APIConsts.Params.CARD_LAST_FOUR)
                                    , addedCard.optString(APIConsts.Params.CARD_NAME)
                                    , addedCard.optString(APIConsts.Params.IS_DEFAULT).equals("1")
                            ));
                            cardAdapter.notifyDataSetChanged();
                            onCardUpdate(false);
                        }
                    } else {
                        UiUtils.showShortToast(PaymentsActivity.this, addCardResponse.optString(APIConsts.Params.ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    private void addStripeCard(final Card card) {
        UiUtils.showLoadingDialog(this);
/*
        stripe.createCardToken(card, new ApiResultCallback<Token>() {
            @Override
            public void onError(Exception error) {
                UiUtils.showShortToast(PaymentsActivity.this, getString(R.string.invalid_card));
                UiUtils.hideLoadingDialog();
            }

            @Override
            public void onSuccess(Token token) {
                addCardInBackend(token.getId());
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCardUpdate(boolean isEmpty) {
        cardsRecycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void refreshScreen() {
        getAllPaymentMethods();
    }

    @Override
    public void getDefaultCards() {
        getAllPaymentMethods();
    }
}
