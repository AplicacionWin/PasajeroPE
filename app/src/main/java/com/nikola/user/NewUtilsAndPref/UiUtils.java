package com.nikola.user.NewUtilsAndPref;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nikola.user.R;

public class UiUtils {

    private static final String TAG = UiUtils.class.getSimpleName();
    private static Dialog loadingDialog;

    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showLoadingDialog(Context context) {
        loadingDialog = new Dialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.api_loading_lottie);
        ImageView loader = loadingDialog.findViewById(R.id.loader);
        Glide.with(context).load(R.raw.logoperu).into(loader);
        if (!loadingDialog.isShowing())
            loadingDialog.show();
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public static void hideLoadingDialog() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing())
                loadingDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SpannableString getSearchSpanString(String searchKey, String searchResult) {
        if (searchKey == null || searchResult == null)
            return new SpannableString(searchResult);
        SpannableString spannableString = new SpannableString(searchResult);
        if (searchResult.toLowerCase().startsWith(searchKey.toLowerCase())) {
            spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                    searchKey.length(), searchResult.length(), 0);
        }
        return spannableString;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
