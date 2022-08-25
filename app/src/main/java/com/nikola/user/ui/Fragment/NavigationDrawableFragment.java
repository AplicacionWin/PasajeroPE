package com.nikola.user.ui.Fragment;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nikola.user.BuildConfig;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularTextView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefHelper;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.network.Models.UserSettings;
import com.nikola.user.ui.Adapter.UserSettingsAdapter;
import com.nikola.user.ui.activity.ChangePasswordActivity;
import com.nikola.user.ui.activity.FavProviderActivity;
import com.nikola.user.ui.activity.GetStartedActivity;
import com.nikola.user.ui.activity.HelpwebActivity;
import com.nikola.user.ui.activity.HistoryActivity;
import com.nikola.user.ui.activity.MainActivity;
import com.nikola.user.ui.activity.MyEarningsActivity;
import com.nikola.user.ui.activity.PQRSActivity;
import com.nikola.user.ui.activity.PanicContactActivity;
import com.nikola.user.ui.activity.PaymentsActivity;
import com.nikola.user.ui.activity.ProfileActivity;
import com.nikola.user.ui.activity.SupportActivity;
import com.nikola.user.ui.activity.WalletAcivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by user on 12/28/2016.
 */
public class NavigationDrawableFragment extends BaseMapFragment implements AdapterView.OnItemClickListener {

    public static CircleImageView ivUserIcon;
    @BindView(R.id.tv_user_name)
    CustomRegularTextView tvUserName;
    @BindView(R.id.tv_build_version)
    CustomRegularTextView tvBuildVersion;
    @BindView(R.id.lv_drawer_user_settings)
    ListView lvDrawerUserSettings;
    private MainActivity activity;
    Dialog dialog;
    Unbinder unbinder;
    PrefUtils prefUtils;
    APIInterface apiInterface;
    TextView refCodeEarned;
    String shareMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_drawer_layout, container, false);
        activity = (MainActivity) getActivity();
        prefUtils = PrefUtils.getInstance(getActivity());
        unbinder = ButterKnife.bind(this, view);
        ivUserIcon = view.findViewById(R.id.iv_user_icon);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        String pictureUrl = prefUtils.getStringValue(PrefKeys.USER_PICTURE, "");
        String name = prefUtils.getStringValue(PrefKeys.USER_NAME, "");
        tvBuildVersion.setText("V " + BuildConfig.VERSION_NAME);
        if (!pictureUrl.equals("")) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.defult_user);
            requestOptions.error(R.drawable.defult_user);
            Glide.with(activity)
                    .setDefaultRequestOptions(requestOptions)
                    .load(pictureUrl)
                    .thumbnail(0.4f)
                    .into(ivUserIcon);
        }
        if (!name.equals("")) {
            tvUserName.setText(name);
        }
        setAdapter();
        return view;
    }

    private void setAdapter() {
        UserSettingsAdapter settingsAdapter = new UserSettingsAdapter(activity, getUserSettingsList());
        lvDrawerUserSettings.setAdapter(settingsAdapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(activity, getResources().getIdentifier("layout_animation_from_left", "anim", activity.getPackageName()));
        animation.setDelay(0f);
        lvDrawerUserSettings.setLayoutAnimation(animation);
        lvDrawerUserSettings.scheduleLayoutAnimation();
        lvDrawerUserSettings.setOnItemClickListener(this);
    }


    private List<UserSettings> getUserSettingsList() {
        List<UserSettings> userSettingsList = new ArrayList<>();
        userSettingsList.add(new UserSettings(R.drawable.home_map_marker, getString(R.string.my_home)));
        userSettingsList.add(new UserSettings(R.drawable.clock_alert, getString(R.string.ride_history)));
        userSettingsList.add(new UserSettings(R.drawable.ic_clock_map, getResources().getString(R.string.title_rentale)));
        //userSettingsList.add(new UserSettings(R.drawable.calendar_clock, getString(R.string.later_title)));

        //userSettingsList.add(new UserSettings(R.drawable.ic_airplane_flight, getResources().getString(R.string.airport_title)));
        //userSettingsList.add(new UserSettings(R.drawable.wallet, getResources().getString(R.string.wallet)));
        //userSettingsList.add(new UserSettings(R.drawable.credit_card, getString(R.string.my_payment)));
        //userSettingsList.add(new UserSettings(R.drawable.favorite_pro_24, getString(R.string.favourite_provider)));
        /*
        userSettingsList.add(new UserSettings(R.drawable.sale, getString(R.string.referral_title)));
        if (!prefUtils.getBooleanValue(PrefKeys.IS_SOCIAL_LOGIN, false)) {
            userSettingsList.add(new UserSettings(R.drawable.ic_lock_open_black_24dp, getString(R.string.change_password_text)));
        }
        */

        userSettingsList.add(new UserSettings(R.drawable.help_circle, getString(R.string.my_help)));
        //userSettingsList.add(new UserSettings(R.drawable.ic_menu_support_normal, getString(R.string.support)));
        //userSettingsList.add(new UserSettings(R.drawable.ic_menu_my_calculator_normal, getString(R.string.my_earnings)));
        userSettingsList.add(new UserSettings(R.drawable.ic_my_social_network_icon, getString(R.string.my_social_network)));
        userSettingsList.add(new UserSettings(R.drawable.ic_sos, getString(R.string.panic_contact)));
        //userSettingsList.add(new UserSettings(R.drawable.ic_incident, "PQRS"));
        userSettingsList.add(new UserSettings(R.drawable.ic_power_off, getString(R.string.txt_logout)));
        return userSettingsList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        activity.closeDrawer();
        switch (position) {
            case 0:
                startActivity(new Intent(getActivity(), MainActivity.class));
                break;
            case 1:
                startActivity(new Intent(activity, HistoryActivity.class).putExtra("isHistory", true));
                break;
            //case 2:
                /*
                HourlyBookngFragment hourlyfragment = new HourlyBookngFragment();
                Bundle nbundle = new Bundle();
                nbundle.putString("pickup_address", HomeFragment.pickup_add);
                nbundle.putDouble("pickup_lat",HomeFragment.selectMarker.getPosition().latitude);
                nbundle.putDouble("pickup_lon",HomeFragment.selectMarker.getPosition().longitude);
                hourlyfragment.setArguments(nbundle);
                activity.addFragment(hourlyfragment, false, Const.HOURLY_FRAGMENT, true);
                break;

                 */
            /*case 2:
                startActivity(new Intent(activity, HistoryActivity.class).putExtra("isHistory", false));
                break;
            case 3:
                HourlyBookngFragment hourlyfragment = new HourlyBookngFragment();
                Bundle nbundle = new Bundle();
                nbundle.putString("pickup_address", HomeFragment.pickup_add);
                hourlyfragment.setArguments(nbundle);
                activity.addFragment(hourlyfragment, false, Const.HOURLY_FRAGMENT, true);
                break;
            case 4:
                activity.addFragment(new AirportBookingFragment(), false, Const.AIRPORT_FRAGMENT, true);
                break;
            case 5:
                startActivity(new Intent(activity, WalletAcivity.class));
                break;
            case 6:
                startActivity(new Intent(activity, PaymentsActivity.class));
                break;
            case 7:
                startActivity(new Intent(activity, FavProviderActivity.class));
                break;*/
            //case 3:
                /*
                showrefferal();
                break;
                 */
            //case 4:
                /*
                if (!prefUtils.getBooleanValue(PrefKeys.IS_SOCIAL_LOGIN, false))
                    startActivity(new Intent(activity, ChangePasswordActivity.class));
                else
                    startActivity(new Intent(activity, HelpwebActivity.class));
                break;
                */
            case 3:
                if (!prefUtils.getBooleanValue(PrefKeys.IS_SOCIAL_LOGIN, false))
                    startActivity(new Intent(activity, HelpwebActivity.class));
                else
                    showlogoutdailog();
                break;
            //case 6:
                /*
                startActivity(new Intent(activity, SupportActivity.class));
                break;

                 */
            //case 7:
                /*
                startActivity(new Intent(activity, MyEarningsActivity.class));
                break;
                */
            //case 8:
                /*
                Uri uriUrl2 = Uri.parse(Const.MY_SOCIAL_NETWORK);
                Intent launchBrowser2 = new Intent(Intent.ACTION_VIEW, uriUrl2);
                startActivity(launchBrowser2);
                break;
                */
            case 5:
                startActivity(new Intent(activity, PanicContactActivity.class));
                break;
            /*case 10:
                startActivity(new Intent(activity, PQRSActivity.class));
                break;*/
            case 6:
                showlogoutdailog();
                break;

        }

    }

    private void showrefferal() {
        getReferralCode();
        final Dialog refrel_dialog = new Dialog(activity, R.style.DialogThemeforview);
        refrel_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        refrel_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        refrel_dialog.setCancelable(true);
        refrel_dialog.setContentView(R.layout.refferalcode_layout);
        TextView refCode = refrel_dialog.findViewById(R.id.refCode);
        refCodeEarned = refrel_dialog.findViewById(R.id.txt_referl_earn);
        ImageView copyCode = refrel_dialog.findViewById(R.id.copyCode);
        refCode.setText(prefUtils.getStringValue(PrefKeys.REFERRAL_CODE, ""));
        (refrel_dialog.findViewById(R.id.referral_back)).setOnClickListener(view -> refrel_dialog.dismiss());
        (refrel_dialog.findViewById(R.id.twitter_share)).setOnClickListener(view -> refrel_dialog.dismiss());
        if (prefUtils.getStringValue(PrefKeys.REFERRAL_BONUS, "").isEmpty()) {
            ((TextView) refrel_dialog.findViewById(R.id.txt_referl_earn)).setText("00");
        } else {
            ((TextView) refrel_dialog.findViewById(R.id.txt_referl_earn)).setText(prefUtils.getStringValue(PrefKeys.REFERRAL_BONUS, ""));
        }
        (refrel_dialog.findViewById(R.id.gm_share)).setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });
        copyCode.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", refCode.getText().toString());
            clipboard.setPrimaryClip(clip);
            UiUtils.showShortToast(activity, getString(R.string.copiedCode));
        });
        (refrel_dialog.findViewById(R.id.fb_share)).setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.facebook.orca");
            try {
                startActivity(sendIntent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(activity, getString(R.string.facebook_messager), Toast.LENGTH_LONG).show();
            }
        });
        refrel_dialog.show();
    }

    private void showlogoutdailog() {
        dialog = new Dialog(activity, R.style.DialogThemeforview);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout_dialog);
        TextView btn_logout_yes = dialog.findViewById(R.id.btn_logout_yes);
        btn_logout_yes.setOnClickListener(view -> doLogoutUser());
        TextView btn_logout_no = dialog.findViewById(R.id.btn_logout_no);
        btn_logout_no.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    @OnClick(R.id.iv_user_icon)
    public void onViewClicked() {
        Intent i = new Intent(activity, ProfileActivity.class);
        startActivity(i);
    }

    protected void doLogoutUser() {
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.doLogoutUser(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject loginResponse = null;
                try {
                    loginResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loginResponse != null) {
                    if (loginResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        PrefHelper.setUserLoggedOut(getActivity());
                        Intent i = new Intent(activity, GetStartedActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {
                        UiUtils.showShortToast(activity, loginResponse.optString(APIConsts.Params.ERROR));
                        prefUtils.setValue(PrefKeys.IS_LOGGED_IN, false);
                        startActivity(new Intent(activity, GetStartedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

    protected void getReferralCode() {
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.getReferralCode(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""), prefUtils.getStringValue(PrefKeys.USERNAME, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject referralCodeResponse = null;
                try {
                    referralCodeResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (referralCodeResponse != null) {
                    if (referralCodeResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONObject data = referralCodeResponse.optJSONObject(APIConsts.Params.DATA);
                        refCodeEarned.setText(data.optString(APIConsts.Params.REFERRER_BONUS_FORMATTED));
                        try {
                            prefUtils.setValue(PrefKeys.REFERRAL_CODE,data.getString(APIConsts.Params.REFERRAL_CODE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        shareMessage = data.optString(APIConsts.Params.REFERAL_SHARE_MESSAGE);
                    } else {
                        UiUtils.showShortToast(activity, referralCodeResponse.optString(APIConsts.Params.ERROR));
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
