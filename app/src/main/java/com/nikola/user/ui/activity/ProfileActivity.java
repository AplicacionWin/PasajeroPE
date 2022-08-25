package com.nikola.user.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomBoldRegularTextView;
import com.nikola.user.NewUtilsAndPref.CustomText.CustomRegularEditView;
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
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by user on 1/7/2017.
 */

public class ProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final int PICK_IMAGE = 100;

    @BindView(R.id.profile_back)
    ImageButton profileBack;
    @BindView(R.id.btn_edit_profile)
    CustomBoldRegularTextView btnEditProfile;
    @BindView(R.id.toolbar_profile)
    Toolbar toolbarProfile;
    @BindView(R.id.actionbar_lay)
    RelativeLayout actionbarLay;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.profile_img_lay)
    RelativeLayout profileImgLay;
    @BindView(R.id.lay_name)
    LinearLayout layName;
    @BindView(R.id.et_profile_email)
    CustomRegularEditView etProfileEmail;
    @BindView(R.id.et_profile_mobile)
    CustomRegularEditView etProfileMobile;
    @BindView(R.id.radio_btn_male)
    RadioButton radioBtnMale;
    @BindView(R.id.radio_btn_others)
    RadioButton radio_btn_others;
    @BindView(R.id.radio_btn_female)
    RadioButton radioBtnFemale;
    @BindView(R.id.profile_radioGroup)
    RadioGroup profileRadioGroup;
    @BindView(R.id.etFirstName)
    CustomRegularEditView etFirstName;
    @BindView(R.id.etLastName)
    CustomRegularEditView etLastName;
    @BindView(R.id.mobileNumber)
    CustomRegularTextView mobileNumber;
    @BindView(R.id.updateMobileNumber)
    CustomRegularTextView updateMobileNumber;
    private String filePath = "";
    private File cameraFile;
    private Uri uri = null;
    private RadioButton rd_btn;
    private String userMobile;
    PrefUtils prefUtils;
    APIInterface apiInterface;
    private Uri fileToUpload = null;
    boolean isEditMode;
    String countryCode;
    private int RC_STORAGE_PERM = 125;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_profile);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        ButterKnife.bind(this);
        etProfileEmail.setEnabled(false);
        enableAndDisableView(false);
        etFirstName.setFilters(new InputFilter[]{EMOJI_FILTER});
        etLastName.setFilters(new InputFilter[]{EMOJI_FILTER});
        if (getIntent().getExtras() != null) {
            String phone = getIntent().getStringExtra(APIConsts.Params.PHONE);
            countryCode = getIntent().getStringExtra(APIConsts.Params.COUNTRY_CODE);
            etProfileMobile.setText(phone);
            enableAndDisableView(true);
            btnEditProfile.setText(getString(R.string.btn_save));
            isEditMode = true;
            setUpProfileData(true);
        } else {
            setUpProfileData(false);
        }

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

    @SuppressLint("ClickableViewAccessibility")
    private void setUpProfileData(boolean isMobileEdit) {
        etProfileEmail.setText("");
        etProfileEmail.append(prefUtils.getStringValue(PrefKeys.USER_EMAIL, ""));
        etFirstName.setText("");
        etFirstName.append(prefUtils.getStringValue(PrefKeys.FIRST_NAME, ""));
        etLastName.setText("");
        etLastName.append(prefUtils.getStringValue(PrefKeys.LAST_NAME, ""));
        if (!isMobileEdit) {
            etProfileMobile.setText("");
            etProfileMobile.append(prefUtils.getStringValue(PrefKeys.USER_MOBILE, ""));
        }

        Glide.with(this).load(prefUtils.getStringValue(PrefKeys.USER_PICTURE, "")).into(profileImage);
        if (prefUtils.getStringValue(PrefKeys.GENDER, "").equalsIgnoreCase(getString(R.string.txt_male_rbutton))) {
            radioBtnMale.setChecked(true);
        } else if (prefUtils.getStringValue(PrefKeys.GENDER, "").equalsIgnoreCase(getString(R.string.others_rbutton))) {
            radio_btn_others.setChecked(true);
        } else {
            radioBtnFemale.setChecked(true);
        }
        etProfileMobile.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            }
            return true;
        });
        updateMobileNumber.setOnClickListener(view -> {
            Intent i = new Intent(ProfileActivity.this, OtpActivity.class);
            i.putExtra("redirectToProfile", true);
            i.putExtra("newOtp", false);
            i.putExtra(Const.Params.PHONE, userMobile);
            i.putExtra(APIConsts.Params.COUNTRY_CODE, countryCode);
            startActivity(i);
            finish();
        });
    }

    private void enableAndDisableView(boolean toggle) {
        profileImage.setEnabled(toggle);
        etFirstName.setEnabled(toggle);
        etLastName.setEnabled(toggle);
        etProfileEmail.setEnabled(toggle);
        radioBtnFemale.setEnabled(toggle);
        radioBtnMale.setEnabled(toggle);
        radio_btn_others.setEnabled(toggle);
//        etProfileMobile.setEnabled(toggle);
//        updateMobileNumber.setVisibility(toggle ? View.VISIBLE : View.GONE);
    }

    protected void updateUserProfile() {
        UiUtils.showLoadingDialog(ProfileActivity.this);
        MultipartBody.Part multipartBody = null;
        if (fileToUpload != null) {
            String path = getRealPathFromURIPath(fileToUpload, this);
            File file = new File(path);
            Timber.d("Filename %s", file.getName());
            // create RequestBody instance tempFrom file
            String mimeType = getContentResolver().getType(fileToUpload);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse(mimeType == null ? "multipart/form-data" : mimeType),
                            file);
            // MultipartBody.Part is used to send also the actual file name
            multipartBody =
                    MultipartBody.Part.createFormData(APIConsts.Params.PICTURE, file.getName(), requestFile);
        }
        String gender = null;
        if (radioBtnFemale.isChecked()) {
            gender = "female";
        } else if (radioBtnMale.isChecked()) {
            gender = "male";
        } else {
            gender = "others";
        }
        Call<String> call = apiInterface.doUpdateProfile(
                prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , etFirstName.getText().toString()
                , etLastName.getText().toString()
                , etProfileEmail.getText().toString()
                , etProfileMobile.getText().toString()
                , multipartBody
                , prefUtils.getStringValue(APIConsts.Params.DEVICE_TOKEN, "")
                , APIConsts.Constants.ANDROID
                , APIConsts.Constants.MANUAL_LOGIN
                , TimeZone.getDefault().getID()
                , gender
                , countryCode);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject updateProfileResponse = null;
                try {
                    updateProfileResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (updateProfileResponse != null) {
                    if (updateProfileResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONObject data = updateProfileResponse.optJSONObject(APIConsts.Params.DATA);
                        UiUtils.showShortToast(ProfileActivity.this, updateProfileResponse.optString(APIConsts.Params.MESSAGE));
                        updateUserInDevice(data);
                        enableAndDisableView(false);
                        btnEditProfile.setText(getString(R.string.btn_edit));
                        isEditMode = false;
                    } else {
                        UiUtils.showShortToast(getApplicationContext(), updateProfileResponse.optString(APIConsts.Params.ERROR));
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

    private void callImagePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(this, getString(R.string.youNeedToGrantPermission),
                    RC_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent, PICK_IMAGE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            fileToUpload = data.getData();
            Glide.with(this)
                    .load(fileToUpload)
                    .thumbnail(0.4f)
                    .into(profileImage);
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public void updateUserInDevice(JSONObject data) {
        PrefHelper.setUserLoggedIn(this, data.optInt(APIConsts.Params.USER_ID)
                , data.optString(APIConsts.Params.TOKEN)
                , ""
                , data.optString(APIConsts.Params.EMAIL)
                , data.optString(APIConsts.Params.NAME)
                , data.optString(APIConsts.Params.FIRSTNAME)
                , data.optString(APIConsts.Params.LAST_NAME)
                , data.optString(APIConsts.Params.PICTURE)
                , data.optString(APIConsts.Params.PAYMENT_MODE)
                , data.optString(APIConsts.Params.TIMEZONE)
                , data.optString(APIConsts.Params.MOBILE_FORMATTED)
                , data.optString(APIConsts.Params.GENDER)
                , data.optString(APIConsts.Params.REFERRAL_CODE)
                , data.optString(APIConsts.Params.REFERRAL_BONUS));
        countryCode = data.optString(APIConsts.Params.COUNTRY_CODE);
    }

    @Override
    public void onBackPressed() {
        UiUtils.hideKeyboard(ProfileActivity.this);
        if (isEditMode) {
            enableAndDisableView(isEditMode);
            btnEditProfile.setText(getString(R.string.btn_edit));
            isEditMode = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfile();
    }

    @OnClick({R.id.profile_back, R.id.btn_edit_profile, R.id.profile_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile_back:
                onBackPressed();
                break;
            case R.id.btn_edit_profile:
                if (btnEditProfile.getText().toString().equals(getString(R.string.btn_edit))) {
                    enableAndDisableView(true);
                    btnEditProfile.setText(getString(R.string.btn_save));
                    etProfileMobile.setText(userMobile);
                    isEditMode = true;
                } else {
                    enableAndDisableView(false);
                    updateUserProfile();
                    isEditMode = false;
                }
                break;
            case R.id.profile_image:
                callImagePicker();
                break;
        }
    }


    //  TODO:handle crop for image
    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), (Calendar.getInstance()
                .getTimeInMillis() + ".jpg")));
        Crop.of(source, outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            filePath = getRealPathFromURIPath(Crop.getOutput(result), this);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.defult_user);
            requestOptions.error(R.drawable.defult_user);
            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(filePath).into(profileImage);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void getProfile() {
        UiUtils.showLoadingDialog(ProfileActivity.this);
        Call<String> call = apiInterface.getProfile(prefUtils.getIntValue(PrefKeys.USER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject profileResponse = null;
                try {
                    profileResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (profileResponse != null) {
                    if (profileResponse.optString(Const.Params.SUCCESS).equals(APIConsts.Constants.TRUE)) {
                        JSONObject data = profileResponse.optJSONObject(Const.Params.DATA);
                        etProfileEmail.setText("");
                        etProfileEmail.append(data.optString(Const.Params.EMAIL));
                        etFirstName.setText("");
                        etFirstName.append(data.optString(Const.Params.FIRSTNAME));
                        etLastName.setText("");
                        etLastName.append(data.optString(Const.Params.LAST_NAME));
                        mobileNumber.setText(data.optString(Const.Params.MOBILE_FORMATTED));
                        userMobile = data.optString(Const.Params.MOBILE);
                        Glide.with(ProfileActivity.this).load(data.optString(Const.Params.PICTURE)).into(profileImage);
                        if (data.optString(Const.Params.GENDER).equalsIgnoreCase(getString(R.string.txt_male_rbutton))) {
                            radioBtnMale.setChecked(true);
                        } else {
                            radioBtnFemale.setChecked(true);
                        }
                        updateUserInDevice(data);
                    } else {
                        UiUtils.showShortToast(ProfileActivity.this, profileResponse.optString(APIConsts.Params.ERROR));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        openGalleryIntent.setType("image/*");
        startActivityForResult(openGalleryIntent, PICK_IMAGE);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

}
