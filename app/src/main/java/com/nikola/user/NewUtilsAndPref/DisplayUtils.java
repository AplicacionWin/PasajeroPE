package com.nikola.user.NewUtilsAndPref;


import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.nikola.user.network.ApiManager.APIConsts;

public class DisplayUtils {


    private DisplayUtils() {

    }


    public static String getStaticMapApprox(Context context, LatLng latLng, int width, int height) {
        if (latLng == null) return "";

        return  "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=" + latLng.latitude + "," +latLng.longitude +
                "&zoom=16" +
                "&size=" + width + "x" + height +
                "&maptype=roadmap" +
                "&path=color:red|fillcolor:0x00d2c196|weight:1|" +
                "enc%3Aad_yHofn%60%40JyFh%40sF%60AcFxAmElBqD~BoCnCiBtC_AzCUzCTvC~%40lChB~BnCnBpDxAlE%60AbFf%40rFLxFMxFg%40rFaAbFyAlEoBpD_CnCmChBwC~%40%7BCT%7BCUuC_AoCiB_CoCmBqDyAmEaAcFi%40sFKyF%3F%3F"+
                "&key=" + APIConsts.Constants.GOOGLE_API_KEY;
    }
}
