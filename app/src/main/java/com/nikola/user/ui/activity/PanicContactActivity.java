package com.nikola.user.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanicContactActivity extends AppCompatActivity {

    APIInterface apiInterface;
    PrefUtils prefUtils;

    @BindView(R.id.savePanicContact)
    Button savePanicContact;
    @BindView(R.id.panicContactNumber)
    EditText panicContactNumber;
    boolean enable = false;
    @BindView(R.id.panic_back)
    ImageButton panic_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_contact_activity);
        ButterKnife.bind(this);
        prefUtils = PrefUtils.getInstance(PanicContactActivity.this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        panicContactNumber.setEnabled(enable);
        panicContactNumber.setBackgroundColor(Color.LTGRAY);
        savePanicContact.setText(getText(R.string.btn_edit));
        panic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        savePanicContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(enable){
                    if(panicContactNumber.getText().toString().length()==10)
                    {
                        sendPanicMessage();
                        enable= false;
                        panicContactNumber.setEnabled(false);
                        savePanicContact.setText(R.string.btn_edit);
                        panicContactNumber.setBackgroundColor(Color.LTGRAY);
                    }
                    else
                    {
                        UiUtils.showShortToast(PanicContactActivity.this, getString(R.string.invalid));
                    }
                }
                else
                {
                    panicContactNumber.setEnabled(true);
                    savePanicContact.setText(R.string.save_contact);
                    enable=true;
                    panicContactNumber.setBackgroundColor(Color.WHITE);
                }
            }
        });
        getPanicMessage();
    }

    protected void sendPanicMessage() {
        UiUtils.showLoadingDialog(PanicContactActivity.this);

        Call<String> call = apiInterface.setPanicContact(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                ,panicContactNumber.getText().toString()
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject savePanicContact = null;
                try {
                    savePanicContact = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (savePanicContact != null) {
                    if (savePanicContact.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        UiUtils.showShortToast(PanicContactActivity.this, savePanicContact.optString(APIConsts.Params.MESSAGE));
                    } else {
                        UiUtils.showShortToast(PanicContactActivity.this, savePanicContact.optString(APIConsts.Params.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(PanicContactActivity.this)) {
                    UiUtils.showShortToast(PanicContactActivity.this, getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    protected void getPanicMessage() {
        UiUtils.showLoadingDialog(PanicContactActivity.this);

        Call<String> call = apiInterface.getPanicContact(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject getPanicContact = null;
                try {
                    getPanicContact = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (savePanicContact != null) {
                    if (getPanicContact.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        panicContactNumber.setText(getPanicContact.optString(APIConsts.Params.DATA));
                    } else {
                        UiUtils.showShortToast(PanicContactActivity.this, getPanicContact.optString(APIConsts.Params.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(PanicContactActivity.this)) {
                    UiUtils.showShortToast(PanicContactActivity.this, getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
