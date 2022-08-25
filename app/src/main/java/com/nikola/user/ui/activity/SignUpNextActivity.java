package com.nikola.user.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;

import org.codehaus.jackson.annotate.JsonRawValue;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.nikola.user.NewUtilsAndPref.AppUtils;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularEditView;
import com.nikola.user.NewUtilsAndPref.UiUtils;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefHelper;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefKeys;
import com.nikola.user.NewUtilsAndPref.sharedpref.PrefUtils;
import com.nikola.user.R;
import com.nikola.user.Utils.Const;
import com.nikola.user.login.AbstractClient;
import com.nikola.user.login.GetAddress;
import com.nikola.user.login.RegisterUser;
import com.nikola.user.login.Respuesta;
import com.nikola.user.login.UserData;
import com.nikola.user.network.ApiManager.APIClient;
import com.nikola.user.network.ApiManager.APIConsts;
import com.nikola.user.network.ApiManager.APIInterface;
import com.nikola.user.network.ApiManager.NetworkUtils;
import com.nikola.user.ui.Fragment.SignupFragment;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nikola.user.network.ApiManager.APIConsts.Params.DATA;
import static com.nikola.user.network.ApiManager.APIConsts.Params.EMAIL;

/**
 * Created by caariver on 7/5/2017.
 */

public class SignUpNextActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    int i = 0;
    @BindView(R.id.user_fname)
    CustomRegularEditView userFname;
    @BindView(R.id.user_fusername)
    CustomRegularEditView etUsername;
    @BindView(R.id.user_lname)
    CustomRegularEditView userLname;
    @BindView(R.id.user_email)
    CustomRegularEditView userEmail;
    @BindView(R.id.identificacion)
    CustomRegularEditView identificacion;
    @BindView(R.id.user_password)
    CustomRegularEditView userPassword;
    @BindView(R.id.Generar_numero)
    CustomRegularEditView generarN;
    @BindView(R.id.recibir_numero)
    CustomRegularEditView recibirN;
    @BindView(R.id.Generar_numero1)
    CustomRegularEditView generarN1;
    @BindView(R.id.recibir_numero1)
    CustomRegularEditView recibirN1;
    @BindView(R.id.CodPosta)
    CustomRegularEditView codpostal;
    @BindView(R.id.user_referral_code)
    CustomRegularEditView userReferralCode;
    @BindView(R.id.et_otp_mobile)
    CustomRegularEditView userMobileNumber;
    @BindView(R.id.regPassword)
    TextInputLayout regPassword;
    @BindView(R.id.rigister_btn1)
    Button registtrarN;
    @BindView(R.id.rigister_btn2)
    Button registtrarN1;
    Dialog requestDialog;
    private String mobile = "", countryCode;
    Unbinder unbinder;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.termsDesc)
    TextView termsDesc;
    @BindView(R.id.checkBox)
    CheckBox checkBox;
    AbstractClient serv;
    boolean usernameCheck = false, emailcheck = false, termsAccept = false, referralCodeCheck = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup2);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(SignUpNextActivity.this);
        userFname.setFilters(new InputFilter[]{EMOJI_FILTER});
        etUsername.setFilters(new InputFilter[]{EMOJI_FILTER});
        userLname.setFilters(new InputFilter[]{EMOJI_FILTER});
        if (getIntent().getExtras() != null) {
            mobile = getIntent().getStringExtra(Const.Params.PHONE);
            countryCode = getIntent().getStringExtra(APIConsts.Params.COUNTRY_CODE);
        }
        serv = new AbstractClient();
        regPassword.setHintAnimationEnabled(false);
        regPassword.setHint("");
        userPassword.setHint(getString(R.string.password));
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            checkBox.requestFocus();
            if (userReferralCode.isFocused())
                userReferralCode.clearFocus();

            if (checkBox.isChecked()) {
                termsAccept = true;
            } else
                termsAccept = false;

        });

        etUsername.setOnFocusChangeListener(this);
        userEmail.setOnFocusChangeListener(this);
        userReferralCode.setOnFocusChangeListener(this);

        Spannable termsAndConditionsSpan = new SpannableString(getString(R.string.please_accept_our_terms_and_conditions_to_proceed));
        termsAndConditionsSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent i = new Intent(getApplicationContext(), HelpwebActivity.class);
                startActivity(i);
            }
        }, 18, 37, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsAndConditionsSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent i = new Intent(getApplicationContext(), HelpwebActivity.class);
                startActivity(i);
            }
        }, 42, 56, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsDesc.setText(termsAndConditionsSpan);
        termsDesc.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public static InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    };

    private boolean ValidarCampos() {

        if (userEmail.getText().toString().trim().isEmpty()) {

            UiUtils.showShortToast(SignUpNextActivity.this, "El campo correo debe estar lleno");
            userEmail.setError(getResources().getString(R.string.txt_email_error2));
            userEmail.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo correo no debe estar vacio");
            return false;

        } else if (!AppUtils.isValidEmail(userEmail.getText().toString().trim())) {

            UiUtils.showShortToast(SignUpNextActivity.this, "Ingresar un correo valido");
            userEmail.setError(getResources().getString(R.string.txt_email_error3));
            userEmail.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("Ingrese un correo valido ejemplo win@gmail.com");
            return false;
        }
        if (codpostal.getText().toString().trim().isEmpty()) {

            UiUtils.showShortToast(SignUpNextActivity.this, "El Prefijo del Pais no esta lleno");
            codpostal.setError(getResources().getString(R.string.txt_email_error1));
            codpostal.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo codigo postal debe no debe estar vacio");
            return false;
        }
        if (userMobileNumber.getText().toString().trim().isEmpty()) {

            UiUtils.showShortToast(SignUpNextActivity.this, "Debe ingresar un celular valido");
            userMobileNumber.setError(getResources().getString(R.string.txt_email_error4));
            userMobileNumber.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo telefono no debe estar vacio");
            return false;
        }
        if (userMobileNumber.getText().toString().equals(9)) {

            UiUtils.showShortToast(SignUpNextActivity.this, "El celular debe contener 9 digitos");
            userMobileNumber.setError(getResources().getString(R.string.txt_email_error5));
            userMobileNumber.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo telefono debe tener 9 caracteres");
            return false;
        }if (identificacion.getText().toString().trim().isEmpty()) {

            UiUtils.showShortToast(SignUpNextActivity.this, "Debe ingresar una cédula valida");
            identificacion.setError(getResources().getString(R.string.txt_email_error6));
            identificacion.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo cedula no debe estar vacio");
            return false;
        } else if (identificacion.getText().toString().length() < 7) {
            UiUtils.showShortToast(SignUpNextActivity.this, "Debe ingresar una cedula valida");
            identificacion.setError(getResources().getString(R.string.txt_email_error6));
            identificacion.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo cedula debe tener minimo 7 caracteres y maximo 10");
            return false;
        }if (userReferralCode.getText().toString().trim().isEmpty()) {
            UiUtils.showShortToast(SignUpNextActivity.this, "Debe ingresar una referido valido");
            userReferralCode.setError(getResources().getString(R.string.txt_email_error7));
            userReferralCode.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo referido no debe estar vacio");
            return false;
        }if (etUsername.getText().toString().trim().isEmpty()) {
            UiUtils.showShortToast(SignUpNextActivity.this, "Debe ingresar los nombres validos");
            etUsername.setError(getResources().getString(R.string.txt_email_error8));
            etUsername.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo usuario no debe estar vacio");
            return false;
        }if (userFname.getText().toString().trim().isEmpty()) {
            UiUtils.showShortToast(SignUpNextActivity.this, "Debe ingresar los nombres ");
            userFname.setError(getResources().getString(R.string.txt_email_error9));
            userFname.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo nombres no debe estar vacio");
            return false;
        }if (userLname.getText().toString().trim().isEmpty()) {
            UiUtils.showShortToast(SignUpNextActivity.this, "Debe ingresar los apellidos");
            userLname.setError(getResources().getString(R.string.txt_email_error10));
            userLname.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo apellidos no debe estar vacio");
            return false;
        }if (userPassword.getText().toString().length() < 6) {
            UiUtils.showShortToast(SignUpNextActivity.this, "Debe ingresar una Contraseña valida");
            userPassword.setError(getResources().getString(R.string.txt_email_error10));
            userPassword.requestFocus();
            TextView test = (TextView)findViewById(R.id.textView3);
            test.setText("El campo contraseña debe contener minimo 6 caracteres entre AZ o numeros");
            return false;
        }if (termsAccept == false) {
            Toast.makeText(this, "Aceptar los terminos y condiciones", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @OnClick({/*R.id.applyRefCode, */R.id.btn_Buscar})
    public void onViewClicked6(View view) {
        verificarEmail1();
    }

    @OnClick({/*R.id.applyRefCode, */R.id.rigister_btn1})
    public void onViewClicked1(View view) {
    switch (view.getId()) {
            case R.id.rigister_btn1:
                if (ValidarCampos()) {
                    Button btm1;
                    TextView tv1;
                    TextView tv2;
                    Button btm2;
                    btm1 = findViewById(R.id.rigister_btn1);
                    btm2 = findViewById(R.id.rigister_btn2);
                    tv1 = findViewById(R.id.Generar_numero);
                    tv2 = findViewById(R.id.Generar_numero1);
                    final Random random = new Random();
                    String randonNumber = String.valueOf(random.nextInt(999999));
                    String randonNumber1 = String.valueOf(random.nextInt(999999));
                    tv1.setText(randonNumber);////correo///
                    tv2.setText(randonNumber1);///wassap////
                    //recibirN.setVisibility(View.VISIBLE);
                    registtrarN.setVisibility(View.VISIBLE);
                    recibirN1.setVisibility(View.VISIBLE);
                    btm2.setVisibility(View.VISIBLE);
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Se Envio un código a su WhatsApp", Toast.LENGTH_SHORT);
                    toast1.show();
                    recargarCodigo();
                }
                break;
        }
    }

    @OnClick({/*R.id.applyRefCode, */R.id.rigister_btn2})
    public void onViewClicked2(View view) {
        switch (view.getId()) {
            case R.id.rigister_btn2:
                Button btm1;
                btm1 = findViewById(R.id.rigister_btn1);
                generarN.getText().toString().trim().length();
                generarN1.getText().toString().trim().length();
                recibirN.getText().toString().trim().length();
                recibirN1.getText().toString().trim().length();
                if (generarN1.getText().toString().equals(recibirN1.getText().toString())) {
                    //recibirN.setEnabled(false);
                    recibirN1.setEnabled(false);
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Su codigo se valido Exitosamente", Toast.LENGTH_SHORT);
                    toast1.show();
                    btm1.setVisibility(View.INVISIBLE);
                    doSignUpUserApp("999999", etUsername.getText().toString());

                } else {

                    recibirN.setVisibility(View.INVISIBLE);
                    Toast toast1 = Toast.makeText(getApplicationContext(), "!!ErrorVuelva a generar un codigo muchas gracias!!", Toast.LENGTH_SHORT);
                    toast1.show();
                }
                break;
        }
    }

    /*
    protected void verifyUserApp() {
        new Thread() {
            public void run() {
                try {
                    String url = Const.ServiceType.VERIFICAR_USUARIO;
                    AbstractClient serv = new AbstractClient();
                    String response = serv.web(url, "username=" + etUsername.getText().toString());
                    System.out.println("response register: " + response);
                    JSONObject obj = new JSONObject(response);
                    Boolean status = obj.getBoolean("existe");
                    if (status.equals(true)) {
                        etUsername.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_white));
                        //etUsername.requestFocus();
                    } else {
                        etUsername.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                        //etUsername.requestFocus();
                    }
                } catch (Exception e) {
                    usernameCheck = false;
                }
            }
        }.start();
    }
*/

/*
    protected void verifyReferido() {
        new Thread() {
            public void run() {
                try {
                    String url = Const.ServiceType.VERIFICAR_REFERIDO;
                    AbstractClient serv = new AbstractClient();
                    String response = serv.web(url, "email=" + userEmail.getText().toString());
                    System.out.println("response register: " + response);
                    JSONObject obj = new JSONObject(response);
                    Boolean status = obj.getBoolean("existe");
                    obj.getJSONObject("existe");

                    if (status.equals(true)) {
                        userReferralCode.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                        Toast toast1 = Toast.makeText(getApplicationContext(), "!!Usuario referido detectado exitosamente!!", Toast.LENGTH_SHORT);
                        toast1.show();
                    } else {
                        Toast toast1 = Toast.makeText(getApplicationContext(), "!!Error Error Usuario Referido no Existe vuelva a escribir!!", Toast.LENGTH_SHORT);
                        toast1.show();
                        userReferralCode.requestFocus();
                    }
                } catch (Exception e) {
                    usernameCheck = false;
                }
            }
        }.start();
    }
*/

    public void MostrarTodo() {
        userLname.setEnabled(true);
        userFname.setEnabled(true);
        userMobileNumber.setEnabled(true);
        userReferralCode.setEnabled(true);
        etUsername.setEnabled(true);
        codpostal.setEnabled(true);
        identificacion.setEnabled(true);
        regPassword.setEnabled(true);
        recibirN.setEnabled(true);
        recibirN1.setEnabled(true);

    }
    public void OcultarTodo() {
        userLname.setEnabled(false);
        userFname.setEnabled(false);
        userMobileNumber.setEnabled(false);
        userReferralCode.setEnabled(false);
        etUsername.setEnabled(false);
        codpostal.setEnabled(false);
        identificacion.setEnabled(false);
        regPassword.setEnabled(false);
        recibirN.setEnabled(false);
        recibirN1.setEnabled(false);
    }

    /*
    protected void verificarRefEmail() {
        new Thread() {
            public void run() {
                try {
                    String url = Const.ServiceType.VERIFICAR_CORREO;
                    AbstractClient serv = new AbstractClient();
                    String response = serv.web(url, "email=" + userEmail.getText().toString());
                    System.out.println("response register: " + response);
                    JSONObject obj = new JSONObject(response);
                    Boolean status = obj.getBoolean("existe");
                    if (status.equals(true)) {
                        etUsername.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_white));
                        //etUsername.requestFocus();
                    } else {
                        etUsername.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                        //etUsername.requestFocus();
                    }
                } catch (Exception e) {
                    usernameCheck = false;
                }
            }
        }.start();
    }

*/
    protected void verificarEmail1() {
        new Thread() {
            public void run() {
                String username_usuario = "";
                String nombre_usuario = "";
                String apellido_usuario = "";
                String telefono_usuario = "";
                String prefijo_usuario = "";
                String sponsor_usuario = "";

                try {
                    String url = Const.ServiceType.VERIFY_EMAIL_APP;
                    AbstractClient serv = new AbstractClient();
                    String response = serv.web(url, "email=" + userEmail.getText().toString());
                    System.out.println("response register: " + response);
                    JSONObject tuJson = new JSONObject(response);
                    JSONArray resultArray = tuJson.getJSONArray("result");
                    JSONObject indice0 = resultArray.getJSONObject(0);
                    Boolean existe = indice0.getBoolean("existe");
                    Boolean backend = indice0.getBoolean("backend");
                    System.out.println("response register: " + backend + existe);
                    JSONObject usuario1 = indice0.getJSONObject("usuario");
                    nombre_usuario = usuario1.getString("nombre");
                    apellido_usuario=usuario1.getString("apellido");
                    if (existe.equals(true)&&backend.equals(true)) {
                        if(existe==true&&backend==true){
                            TextView test = (TextView)findViewById(R.id.textView3);
                            test.setText("Bienvenido:"+nombre_usuario+" "+apellido_usuario+" Su correo ya fue actualizado inicie sesion");
                            OcultarTodo();
                        }
                    }else if (existe.equals(true)&&backend.equals(false)) {
                        verificarCod();
                        TextView test = (TextView)findViewById(R.id.textView3);
                        test.setText("Bienvenido:"+nombre_usuario+" "+apellido_usuario+" estos son sus datos registrados ");
                    }else if (existe.equals(false)&&backend.equals(false)) {
                        MostrarTodo();
                        TextView test = (TextView)findViewById(R.id.textView3);
                        test.setText("Bienvenido: por favor llenar el formulario");
                    }
                } catch (Exception e) {}
            }
        }.start();
    }


    protected void verificarCod() {
        new Thread() {
            public void run() {
                String username_usuario = "";
                String nombre_usuario = "";
                String apellido_usuario = "";
                String telefono_usuario = "";
                String prefijo_usuario = "";
                String sponsor_usuario = "";

                try {
                    String url = Const.ServiceType.VERIFY_EMAIL_APP;
                    AbstractClient serv = new AbstractClient();
                    String response = serv.web(url, "email=" + userEmail.getText().toString());
                    System.out.println("response register: " + response);
                    JSONObject tuJson = new JSONObject(response);
                    JSONArray resultArray = tuJson.getJSONArray("result");
                    JSONObject indice0 = resultArray.getJSONObject(0);
                    Boolean existe = indice0.getBoolean("existe");
                    Boolean backend = indice0.getBoolean("backend");
                    System.out.println("response register: " + backend + existe);
                    try {
                        JSONObject usuario1 = indice0.getJSONObject("usuario");
                        prefijo_usuario = usuario1.getString("prefijo_pais");
                        codpostal.setText(prefijo_usuario);
                    } catch (Exception e) {
                        codpostal.setEnabled(false);
                        codpostal.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                    }
                    try {
                        JSONObject usuario1 = indice0.getJSONObject("usuario");
                        telefono_usuario = usuario1.getString("telefono");
                        userMobileNumber.setText(telefono_usuario);
                    } catch (Exception e) {
                        userMobileNumber.setEnabled(false);
                        userMobileNumber.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                    }
                    try {
                        JSONObject usuario1 = indice0.getJSONObject("usuario");
                        sponsor_usuario = usuario1.getString("sponsor_username");
                        userReferralCode.setText(sponsor_usuario);
                    } catch (Exception e) {
                        userReferralCode.setEnabled(false);
                        userReferralCode.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                    }
                    try {
                        JSONObject usuario1 = indice0.getJSONObject("usuario");
                        username_usuario = usuario1.getString("username");
                        etUsername.setText(username_usuario);
                    } catch (Exception e) {
                        etUsername.setEnabled(false);
                        etUsername.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                    }
                    try {
                        JSONObject usuario1 = indice0.getJSONObject("usuario");
                        nombre_usuario = usuario1.getString("nombre");
                        userFname.setText(nombre_usuario);
                    } catch (Exception e) {
                        userFname.setEnabled(false);
                        userFname.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                    }
                    try {
                        JSONObject usuario1 = indice0.getJSONObject("usuario");
                        apellido_usuario = usuario1.getString("apellido");
                        userLname.setText(apellido_usuario);
                    } catch (Exception e) {
                        userLname.setEnabled(false);
                        userLname.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent(this, SignInActivity.class);
        startActivity(goBack);
        finish();
    }


    protected void doSignUpUserApp(String userId, String username){
        UiUtils.showLoadingDialog(SignUpNextActivity.this);
        Call<String> call = apiInterface.doSignUpUser(userFname.getText().toString()
                , userLname.getText().toString().trim()
                , etUsername.getText().toString().trim()
                , userEmail.getText().toString().trim()
                , userPassword.getText().toString().trim()
                , userMobileNumber.getText().toString().trim()
                , "1"
                , prefUtils.getStringValue(PrefKeys.FCM_TOKEN, "")
                , APIConsts.Constants.ANDROID
                , APIConsts.Constants.MANUAL_LOGIN
                , TimeZone.getDefault().getID()
                , userReferralCode.getText().toString().trim()
                , codpostal.getText().toString().trim()
                ,identificacion.getText().toString().trim()
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject signUpResponse = null;
                try {
                    signUpResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (signUpResponse != null) {
                    if (signUpResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.showLongToast(SignUpNextActivity.this, signUpResponse.optString(APIConsts.Params.MESSAGE));
                        JSONObject data = signUpResponse.optJSONObject(DATA);
                        loginUserInDevice(data, username, APIConsts.Constants.MANUAL_LOGIN,userId);
                        prefUtils.setValue(PrefKeys.IS_SOCIAL_LOGIN, false);
                    } else {
                        UiUtils.showShortToast(SignUpNextActivity.this, signUpResponse.optString(APIConsts.Params.ERROR));
                        if (signUpResponse.optInt(APIConsts.Params.ERROR_CODE) == APIConsts.ErrorCodes.NOT_APPROVED ) {
                            startActivity(new Intent(SignUpNextActivity.this, SignInActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
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

    ////metodo para ingresar el codigo autogenrado////////

    public void recargarCodigo() {
        UiUtils.showLoadingDialog(this);
        Call<String> call = apiInterface.getCodigo(
                userEmail.getText().toString(),
                userMobileNumber.getText().toString(),
                codpostal.getText().toString(),
                generarN.getText().toString(),///correo///
                generarN1.getText().toString()///wassap///
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject recargaResponse = null;
                try {
                    recargaResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (recargaResponse != null) {
                    if (recargaResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        String message = recargaResponse.optString(APIConsts.Params.MESSAGE);
                        UiUtils.showShortToast(SignUpNextActivity.this, message);
                        Toast toast1 = Toast.makeText(getApplicationContext(), "Su codigo se genero Exitosamente", Toast.LENGTH_SHORT);
                        toast1.show();
                    } else {
                        UiUtils.showShortToast(SignUpNextActivity.this, recargaResponse.optString(APIConsts.Params.ERROR));
                    }
                }
            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }


    public void loginUserInDevice(JSONObject data, String username, String loginBy, String userId) {
        PrefHelper.setUserLoggedIn(this
                , userId
                , username
                , data.optInt(APIConsts.Params.USER_ID)
                , data.optString(APIConsts.Params.TOKEN)
                , loginBy
                , data.optString(APIConsts.Params.EMAIL)
                , data.optString(APIConsts.Params.NAME)
                , data.optString(APIConsts.Params.FIRSTNAME)
                , data.optString(APIConsts.Params.LAST_NAME)
                , data.optString(APIConsts.Params.PICTURE)
                , data.optString(APIConsts.Params.PAYMENT_MODE)
                , data.optString(APIConsts.Params.TIMEZONE)
                , data.optString(APIConsts.Params.MOBILE)
                , data.optString(APIConsts.Params.GENDER)
                , data.optString(APIConsts.Params.REFERRAL_CODE)
                , data.optString(APIConsts.Params.REFERRAL_BONUS));
        Intent toHome = new Intent(SignUpNextActivity.this, MainActivity.class);
        toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toHome);
        finish();
    }




    @Override
    public void onFocusChange(View v, boolean hasFocus) {}}
