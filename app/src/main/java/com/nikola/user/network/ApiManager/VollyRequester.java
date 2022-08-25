package com.nikola.user.network.ApiManager;

import android.content.Context;

import android.util.Log;

//import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
//import com.android.volley.Response;
import com.android.volley.ServerError;
//import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
//import com.braintreepayments.api.Json;
import com.nikola.user.R;
import com.nikola.user.Utils.AndyUtils;
import com.nikola.user.Utils.Commonutils;
import com.nikola.user.Utils.Const;
import com.nikola.user.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Amal on 28-06-2015.
 */
public class VollyRequester {

    private Context activity;
    private AsyncTaskCompleteListener asyncTaskCompleteListener;
    private Map<String, String> map;
    int servicecode;

    // SeekbarTimer seekbar;

    public VollyRequester(Context activity, int method_type, Map<String, String> map, int servicecode, AsyncTaskCompleteListener asyncTaskCompleteListener) {
        int method;
        this.activity = activity;
        this.asyncTaskCompleteListener = asyncTaskCompleteListener;
        this.map = map;

        this.servicecode = servicecode;
        if (method_type == 0)
            method = Request.Method.GET;
        else
            method = Request.Method.POST;
        String URL = map.get(Const.Params.URL);
        map.remove(Const.Params.URL);

        if (method == Request.Method.POST)
            volley_requester(method, URL, (map == null) ? null : map);
        else
            volley_requester(URL);


    }

    public VollyRequester(Context activity, int method_type, Map<String, String> map, int servicecode, AsyncTaskCompleteListener asyncTaskCompleteListener,Map<String,String> headerMap) {
        int method = 0;
        this.activity = activity;
        this.asyncTaskCompleteListener = asyncTaskCompleteListener;
        this.map = map;

        this.servicecode = servicecode;
        if (method_type == 0)
            method = Request.Method.GET;
        else if(method_type==1)
            method = Request.Method.POST;
        else if(method_type==2)
            method = Request.Method.PUT;
        else if(method_type==3)
            method = Request.Method.DELETE;

        String URL = map.get(Const.Params.URL);
        map.remove(Const.Params.URL);

        if (method == Request.Method.POST || method==Request.Method.DELETE || method==Request.Method.PUT )
            volley_requesterHeader(method, URL, (map == null) ? null : map,(headerMap==null)?null:headerMap);
        else if(method==Request.Method.GET )
            volley_requesterHeader(URL,headerMap);


    }

    public void volley_requester(int method, String url, final Map<String, String> requestbody) {
        StringRequest jsonObjRequest = new StringRequest(method,
                url,
                response -> {
                    if(response!=null){
                        asyncTaskCompleteListener.onTaskCompleted(response.toString(), servicecode);
                    }

                }, error -> {

                    if (error instanceof NoConnectionError) {
                        Commonutils.progressdialog_hide();
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params;
                params = requestbody;
                return params;
            }

        };

        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                Const.TIMEOUT,
                Const.MAX_RETRY,
                Const.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjRequest);

    }

    public void volley_requester(String url) {

        JsonObjectRequest jsongetrequest = new JsonObjectRequest(url, null, response -> {
            if(response!=null){
                asyncTaskCompleteListener.onTaskCompleted(response.toString(), servicecode);
            }

        }, error -> {
            if (error instanceof NoConnectionError) {
                String msg = activity.getResources().getString(R.string.network_error);
                Commonutils.showtoast(msg, activity);
                Commonutils.progressdialog_hide();

            }
            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    JSONObject obj = new JSONObject(res);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(jsongetrequest);
    }


    public void volley_requesterHeader(int method, String url, final Map<String, String> requestbody, final Map<String,String> headerMap) {
        HttpsTrustManager.verifyHostnames();
        AndyUtils.appLog("Ashutosh", "Url in http " + url);

        StringRequest jsonObjRequest = new StringRequest
                (method,
                        url,
                        response -> {

                            if(response!=null) {
                                AndyUtils.appLog("HttpRequester Response", response.toString());

                                asyncTaskCompleteListener.onTaskCompleted(response.toString(), servicecode);
                            }
                        }, error -> {
                            AndyUtils.appLog("HttpRequester Error",error.toString());
                            if (error instanceof NoConnectionError) {
                                String msg = "No network connection.Please check your internet";
                            }
                        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                AndyUtils.appLog("HttpRequester"," GetParams");
                Map<String, String> params;
                params = requestbody;
                return params;
            }


            @Override
            public Map<String, String> getHeaders() {
                if(headerMap!=null)
                {
                    return headerMap;

                }
                return headerMap;
            }
        };

        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                Const.TIMEOUT,
                Const.MAX_RETRY,
                Const.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjRequest);

    }


    public void volley_requesterHeader(String url, final Map<String,String> headerMap) {
        HttpsTrustManager.verifyHostnames();
        JsonObjectRequest jsongetrequest = new JsonObjectRequest(url, null, response -> {
            if(response!=null) {
                asyncTaskCompleteListener.onTaskCompleted(response.toString(), servicecode);
                Log.d("Ashutosh", "volley requester response " + response.toString());
                //seekbar.cancel();
            }

        }, error -> {
            if (error instanceof NoConnectionError) {
                Log.d("Ashutosh", "volley requester 2" + error.toString());
                String msg = "No network connection.Please check your internet";
            }
        }){

            @Override
            public Map<String, String> getHeaders() {
                return headerMap;
            }
        };


        AppController.getInstance().addToRequestQueue(jsongetrequest);
    }

}
