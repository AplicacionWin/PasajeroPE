package com.nikola.user.ui.Fragment;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularEditView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Const;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.ui.activity.SignInActivity;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 1/5/2017.
 */

public class RecoverPassFragment extends AppCompatActivity {

    public WebView mWebView;
    public ImageButton btnCancel;
    MaterialDialog mSSLConnectionDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recover_password);
        ButterKnife.bind(this);
        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.loadUrl("https://mywinrideshare.com/go/password_reset.html");
        btnCancel = (ImageButton) findViewById(R.id.btn_forgot_cancel);
        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                // the main thing is to show dialog informing user
                // that SSL cert is invalid and prompt him to continue without
                // protection: handler.proceed();
                // or cancel: handler.cancel();
                String message;
                switch(error.getPrimaryError()) {
                    case SslError.SSL_DATE_INVALID:
                        message = getString(R.string.ssl_cert_error_date_invalid);
                        break;
                    case SslError.SSL_EXPIRED:
                        message = getString(R.string.ssl_cert_error_expired);
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = getString(R.string.ssl_cert_error_idmismatch);
                        break;
                    case SslError.SSL_INVALID:
                        message = getString(R.string.ssl_cert_error_invalid);
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = getString(R.string.ssl_cert_error_not_yet_valid);
                        break;
                    case SslError.SSL_UNTRUSTED:
                        message = getString(R.string.ssl_cert_error_untrusted);
                        break;
                    default:
                        message = getString(R.string.ssl_cert_error_cert_invalid);
                }

                mSSLConnectionDialog = new MaterialDialog.Builder(getApplicationContext())
                        .title(R.string.ssl_cert_error_title)
                        .content(message)
                        .positiveText(R.string.txt_continue)
                        .negativeText(R.string.txt_cancel)
                        .titleColorRes(R.color.black)
                        .positiveColorRes(R.color.red)
                        .contentColorRes(R.color.light_grey)
                        .backgroundColorRes(R.color.light_grey)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                mSSLConnectionDialog.dismiss();

                                handler.proceed();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                handler.cancel();
                            }
                        })
                        .build();
                mSSLConnectionDialog.show();
            }
        });


    }

    @OnClick({R.id.btn_forgot_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_forgot_cancel:

                finish();
                break;

        }
    }
}
