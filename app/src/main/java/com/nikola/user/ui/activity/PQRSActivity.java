package com.nikola.user.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIInterface;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh on 3/1/2017.
 */

public class PQRSActivity extends AppCompatActivity {

    @BindView(R.id.pqrs_back)
    ImageButton pqrsBack;
    @BindView(R.id.actionbar_lay)
    RelativeLayout actionbarLay;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.web_loader)
    ProgressBar webLoader;
    @BindView(R.id.helpContent)
    TextView helpContent;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    MaterialDialog mSSLConnectionDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.pqrsview);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        String url  = "http://aplicacionwin.com/";
        try {
            webView.getSettings().setJavaScriptEnabled(true); // enable javascript

            final Activity activity = this;

            webView.setWebViewClient(new WebViewClient() {
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

            webView.loadUrl(url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, getText(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }

        pqrsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
