package com.nikola.user.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Carlos on 9/20/2017.
 */

public class PayGateWeb extends AppCompatActivity {
    @BindView(R.id.toolbar_help)
    Toolbar toolbarHelp;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.web_loader)
    ProgressBar webLoader;
    @BindView(R.id.helpContent)
    TextView helpContent;
    private String URL = "", stringVariable;
    APIInterface apiInterface;
    PrefUtils prefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = getIntent().getExtras().getString("URl", "URl");
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.helpview);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarHelp);
        getSupportActionBar().setTitle(null);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        //getStaticData();
    }



    @Override
    public void onResume() {
        super.onResume();

    }

    @OnClick(R.id.help_back)
    public void onViewClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PayGateWeb.this);
        builder.setTitle(getResources().getString(R.string.txt_warn));
        builder.setMessage(getResources().getString(R.string.txt_cancel_trans));
        builder.setPositiveButton(getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.txt_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
    }


}
