package com.nikola.user.NewUtilsAndPref;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.nikola.user.R;

public class DrawableUtils {

    private DrawableUtils() {

    }

    public static void changeIconDrawableToGray(Context context, Drawable drawable) {
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat
                    .getColor(context, R.color.bt_light_gray), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
