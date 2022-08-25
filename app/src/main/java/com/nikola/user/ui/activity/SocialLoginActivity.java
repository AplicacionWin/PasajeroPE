package com.nikola.user.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/*import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;*/
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.nikola.user.network.ApiManager.APIConsts.Constants;
import static com.nikola.user.network.ApiManager.APIConsts.Params;

public class SocialLoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    APIInterface apiInterface;
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();
    GoogleSignInClient mGoogleSignInClient;
    //CallbackManager callbackManager;
    PrefUtils prefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_connect_popup);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        ButterKnife.bind(this);
        //callbackManager = CallbackManager.Factory.create();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
    }

    @OnClick({R.id.btn_fb, R.id.btn_gmail, R.id.backBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_fb:
                //doFacebookLogin();
                break;
            case R.id.btn_gmail:
                mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
                mGoogleSignInClient.signOut();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.backBtn:
                onBackPressed();
                break;
        }
    }
/*
    private void doFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                try{
                    if (AccessToken.getCurrentAccessToken() != null) {
                        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
                            Timber.d("%s", object);
                            doSocialLoginUser(object.optString("id")
                                    , object.optString("email")
                                    , object.optString("name"),
                                    ""
                                    , "https://graph.facebook.com/" + object.optString("id") + "/picture?type=large"
                                    , Constants.FACEBOOK_LOGIN);
                        });
                        Bundle params = new Bundle();
                        params.putString("fields", "id,name,link,email,picture");
                        request.setParameters(params);
                        request.executeAsync();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
            }

            @Override
            public void onError(FacebookException error) {
                UiUtils.showShortToast(getApplicationContext(), getString(R.string.please_try_again));
            }
        });
    }
*/
    protected void doSocialLoginUser(String socialUniqueId, String email, String firstName, String lastName, String picture, String loginBy) {
        UiUtils.showLoadingDialog(SocialLoginActivity.this);
        Call<String> call = apiInterface.doSocialLoginUser(socialUniqueId
                , loginBy
                , email
                , firstName
                , lastName
                , picture
                , Constants.ANDROID
                , prefUtils.getStringValue(PrefKeys.FCM_TOKEN, "")
                , TimeZone.getDefault().getID());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject socialLoginResponse = null;
                try {
                    socialLoginResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (socialLoginResponse != null) {
                    if (socialLoginResponse.optString(Const.Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.showShortToast(getApplicationContext(), "Welcome, " + firstName + "!");
                        JSONObject data = socialLoginResponse.optJSONObject(Params.DATA);
                        loginUserInDevice(data, loginBy);
                        prefUtils.setValue(PrefKeys.IS_SOCIAL_LOGIN, true);
                    } else {
                        UiUtils.showShortToast(getApplicationContext(), socialLoginResponse.optString(Params.ERROR));
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


    private void loginUserInDevice(JSONObject data, String loginBy) {
        PrefHelper.setUserLoggedIn(this, data.optInt(Params.USER_ID)
                , data.optString(Params.TOKEN)
                , loginBy
                , data.optString(Params.EMAIL)
                , data.optString(Params.NAME)
                , data.optString(Params.FIRSTNAME)
                , data.optString(Params.LAST_NAME)
                , data.optString(Params.PICTURE)
                , data.optString(Params.PAYMENT_MODE)
                , data.optString(Params.TIMEZONE)
                , data.optString(Params.MOBILE)
                , data.optString(APIConsts.Params.GENDER)
                , data.optString(APIConsts.Params.REFERRAL_CODE)
                , data.optString(APIConsts.Params.REFERRAL_BONUS));
        Intent toHome = new Intent(getApplicationContext(), MainActivity.class);
        toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toHome);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
            default:
                //callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String photoImg;
                try {
                    photoImg = account.getPhotoUrl().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    photoImg = "";
                }
                doSocialLoginUser(account.getId()
                        , account.getEmail()
                        , account.getGivenName()
                        , account.getFamilyName()
                        , photoImg
                        , Constants.GOOGLE_LOGIN);
            }
        } catch (ApiException e) {
            Timber.d("signInResult:failed code=%s", e.getStatusCode());
            UiUtils.showShortToast(getApplicationContext(), getString(R.string.please_try_again));
        }
    }
}
