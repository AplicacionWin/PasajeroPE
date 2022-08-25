package com.nikola.user.ui.activity;

import android.app.AlertDialog;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Carlos on 3/1/2017.
 */

public class CreditCardPaymentWebActivity extends AppCompatActivity {

    @BindView(R.id.creditCardPaymenetwebView)
    WebView webView;
    @BindView(R.id.creditCardPaymenetweb_loader)
    ProgressBar webLoader;
    @BindView(R.id.creditCardPaymenetContent)
    TextView helpContent;
    @BindView(R.id.creditCardPaymenetnodata)
    TextView nodata;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    MaterialDialog mSSLConnectionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.cardpaymentview);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        int requestId = bundle.getInt("requestId");
        loadWebViewLoad(webView,requestId);
    }

    private void loadWebViewLoad(WebView webView, int requestId) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                webLoader.setVisibility(View.GONE);
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
        webView.loadUrl(Const.ServiceType.PAGO_TARJETA+requestId);

    }

    @OnClick(R.id.creditCardPaymenet_back)
    public void onViewClicked() {
        onBackPressed();
    }



}
