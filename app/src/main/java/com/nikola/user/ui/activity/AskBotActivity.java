package com.nikola.user.ui.activity;

import android.app.AlertDialog;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nikola.user.R;
import com.nikola.user.Utils.PreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Carlos on 5/24/2017.
 */

public class AskBotActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.web_loader)
    ProgressBar webLoader;
    MaterialDialog mSSLConnectionDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.helpview);
        ButterKnife.bind(this);
        loadWebViewLoad(webView);
    }

    private void loadWebViewLoad(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("http://prevue.info/web-view/?token=" + new PreferenceHelper(this).getUserId() + "&" + "session_token" + "=" + new PreferenceHelper(this).getSessionToken());
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                webLoader.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("My Webview", url);
                view.loadUrl(url);
                return false;
            }

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

    @OnClick(R.id.help_back)
    public void onViewClicked() {
        onBackPressed();
    }

    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setMessage(getResources().getString(R.string.txt_load_web));
            builder.setPositiveButton(getResources().getString(R.string.txt_continue), (dialog, which) -> handler.proceed());
            builder.setNegativeButton(getResources().getString(R.string.txt_cancel), (dialog, which) -> {
                handler.cancel();
                onBackPressed();
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    @Override
    public void onBackPressed() {
        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}