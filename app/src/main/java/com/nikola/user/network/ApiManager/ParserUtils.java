package com.nikola.user.network.ApiManager;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.nikola.user.Utils.Const;
import com.nikola.user.network.Models.AdsList;
import com.nikola.user.network.Models.ChatObject;
import com.nikola.user.network.Models.History;
import com.nikola.user.network.Models.Later;
import com.nikola.user.network.Models.NearByDrivers;
import com.nikola.user.network.Models.RequestDetail;
import com.nikola.user.network.Models.TaxiTypes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.nikola.user.network.ApiManager.APIConsts.Constants;
import static com.nikola.user.network.ApiManager.APIConsts.Constants.SUCCESS;
import static com.nikola.user.network.ApiManager.APIConsts.Params;

public class ParserUtils {

    public static ArrayList<History> ParseHistoryArrayList(JSONArray data, boolean isHistory) {
        ArrayList<History> tripItems = new ArrayList<>();
        if (data == null)
            return tripItems;
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.optJSONObject(i);
            History tripItem = parseHistoryItem(item, isHistory);
            if (tripItem != null)
                tripItems.add(tripItem);
        }
        return tripItems;
    }

    private static History parseHistoryItem(JSONObject item, boolean isHistory) {
        History historyItem = new History();
        if (item == null)
            return null;
        historyItem.setRequestId(item.optString(Params.REQUEST_ID));
        historyItem.setHistory_Dadd(item.optString(Params.D_ADDRESS));
        historyItem.setHistory_Sadd(item.optString(Params.S_ADDRESS));
        historyItem.setHistory_date(item.optString(isHistory ? Params.REQUEST_CREATED_TIME : Params.SCHEDULED_TIME));
        historyItem.setProvider_name(item.optString(Params.PROVIDER_NAME));
        historyItem.setProviderPicture(item.optString(Params.PICTURE));
        historyItem.setHistory_type(item.optString(Params.TAXI_NAME));
        historyItem.setHistory_total(item.optString(Params.TOTAL));
        historyItem.setHistory_picture(item.optString(Params.PICTURE));
        historyItem.setMap_image(item.optString(Params.MAP_IMAGE));
        historyItem.setBase_price(item.optString(Params.BASE_PRICE));
        historyItem.setDistance_travel(item.optString(Params.DISTANCE_TRAVEL));
        historyItem.setTotal_time(item.optString(Params.TOTAL_TIME));
        historyItem.setTax_price(item.optString(Params.TAX_PRICE));
        historyItem.setTime_price(item.optString(Params.TIME_PRICE));
        historyItem.setDistance_price(item.optString(Params.DISTANCE_PRICE));
        historyItem.setMin_price(item.optString(Params.MIN_PRICE));
        historyItem.setBooking_fee(item.optString(Params.BOOKING_FEE));
        historyItem.setCurrnecy_unit(item.optString(Params.CURRENCY));
        historyItem.setDistance_unit(item.optString(Params.DISTANCE_UNIT));
        historyItem.setLatitude(item.optDouble(Params.LATITUDE));
        historyItem.setLongitude(item.optDouble(Params.LONGITUDE));
        historyItem.setRequestUniqueId("#" + item.optString(Params.REQUEST_UNIQUE_ID));
        historyItem.setRequest_ico_status(item.optInt(Params.REQUEST_STAUS_ICON));
        historyItem.setRequest_icon_status_text(item.optString(Params.REQUEST_STAUS_ICON_TEXT));
        historyItem.setHistory_date(item.optString(item.optString(Params.REQUEST_STAUS_ICON_TEXT).equals("Scheduled")? Params.SCHEDULED_TIME : Params.REQUEST_CREATED_TIME ));
        historyItem.setCancelReason(item.optString(Params.CANCELLATION_REASON));
        return historyItem;
    }


    public static ArrayList<Later> ParseLaterRequest(JSONArray data) {
        ArrayList<Later> laterArray = new ArrayList<>();
        if (data == null)
            return laterArray;
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.optJSONObject(i);
            Later laterItem = ParseLaterItems(item);
            if (laterItem != null)
                laterArray.add(laterItem);
        }
        return laterArray;
    }

    private static Later ParseLaterItems(JSONObject item) {
        Later laterItem = new Later();
        if (item == null)
            return null;
        laterItem.setReq_id(item.optString(Params.REQUEST_ID));
        laterItem.setReq_date(item.optString(Params.REQUESTED_TIME));
        laterItem.setReq_type(item.optString(Params.SERVICE_TYPE_NAME));
        laterItem.setReq_pic(item.optString(Params.TYPE_PICTURE));
        laterItem.setD_address(item.optString(Params.D_ADDRESS));
        laterItem.setS_address(item.optString(Params.S_ADDRESS));
        return laterItem;
    }


    public static ArrayList<AdsList> ParseAdsList(JSONArray data) {
        ArrayList<AdsList> adsList = new ArrayList<>();
        if (data == null)
            return adsList;
        for (int i = 0; i < data.length(); i++) {
            JSONObject adsObject = data.optJSONObject(i);
            AdsList adsItem = ParseAdsItem(adsObject);
            if (adsItem != null)
                adsList.add(adsItem);
        }
        return adsList;
    }

    private static AdsList ParseAdsItem(JSONObject item) {
        AdsList adsList = new AdsList();
        if (item == null)
            return null;
        adsList.setAdDescription(item.optString(Params.DESCRIPTION));
        adsList.setAdId(item.optString(Params.ID));
        adsList.setAdImage(item.optString(Params.PICTURE));
        adsList.setAdUrl(item.optString(Params.URL));
        return adsList;
    }

    public static ArrayList<TaxiTypes> ParseServicesList(JSONArray data) {
        ArrayList<TaxiTypes> taxiList = new ArrayList<>();
        if (data == null)
            return taxiList;
        for (int i = 0; i < data.length(); i++) {
            JSONObject serviceObject = data.optJSONObject(i);
            TaxiTypes serviceItem = ParseServicesItem(serviceObject);
            if (serviceItem != null)
                taxiList.add(serviceItem);
        }
        return taxiList;
    }

    private static TaxiTypes ParseServicesItem(JSONObject item) {
        TaxiTypes taxiItem = new TaxiTypes();
        if (item == null)
            return null;
        taxiItem.setCurrencey_unit(item.optString(Params.CURRENCY));
        taxiItem.setId(item.optString(Params.SERVICE_TYPE_ID));
        taxiItem.setTaxi_cost(item.optString(Params.MIN_FARE));
        taxiItem.setTaxiimage(item.optString(Params.PICTURE));
        taxiItem.setTaxitype(item.optString(Params.SERVICE_TYPE_NAME));
        taxiItem.setTaxi_price_min(item.optString(Params.PRICE_PER_MIN));
        taxiItem.setTaxi_price_distance(item.optString(Params.PRICE_PER_UNIT_DISTANCE));
        taxiItem.setTaxi_seats(item.optString(Params.NUMBER_SEAT));
        taxiItem.setEstimatedFare(item.optString(Params.ESTIMATED_FARE_FORMATTED));
        taxiItem.setEstimatedPrice(item.optDouble(Params.ESTIMATED_PRICE));
        taxiItem.setBasefare(item.optString(Params.ESTIMATED_FARE_FORMATTED));
        return taxiItem;
    }

    public static ArrayList<NearByDrivers> ParseAvailableProviderList(JSONArray data) {
        ArrayList<NearByDrivers> nearByDrivers = new ArrayList<>();
        if (data == null)
            return nearByDrivers;
        for (int i = 0; i < data.length(); i++) {
            JSONObject driverObject = data.optJSONObject(i);
            NearByDrivers nearByDriverItem = ParseNearByDriversItem(driverObject);
            if (driverObject != null)
                nearByDrivers.add(nearByDriverItem);
        }
        return nearByDrivers;
    }



    private static NearByDrivers ParseNearByDriversItem(JSONObject item) {
        if (item == null)
            return null;
        NearByDrivers drivers = new NearByDrivers();
        drivers.setLatlan(new LatLng(item.optDouble("latitude"),
                item.optDouble("longitude")));
        drivers.setId(item.optString("provider_id"));
        drivers.setDriver_name(item.optString("first_name"));
        drivers.setDriver_rate(item.optInt("rating") == 0 ? 0 : item.optInt("raiting"));
        drivers.setDriver_distance(item.optString("distance"));
        return drivers;
    }

    public static RequestDetail parseRequestStatus(String response) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        RequestDetail requestDetail = new RequestDetail();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(SUCCESS)) {
                JSONObject data = jsonObject.optJSONObject(Params.DATA);
                requestDetail.setCancellationFee(data.optString(Params.CANCELLATION_FINE));
                JSONArray jarray = data.getJSONArray(Params.DATA);
                if (jarray.length() > 0) {
                    JSONObject driverdata = jarray.getJSONObject(0);
                    if (driverdata != null) {
                        if (driverdata.has(Params.PROVIDER_STATUS)) {
                            requestDetail.setTripStatus(driverdata.getInt(Params.PROVIDER_STATUS));
                        }
                        requestDetail.setCurrnecy_unit(driverdata.optString(Params.CURRENCY));
                        requestDetail.setRequestId(driverdata.optInt(Params.REQUEST_ID));
                        requestDetail.setDriver_name(driverdata.getString(Params.PROVIDER_NAME));
                        requestDetail.setDriverStatus(driverdata.getInt(Params.STATUS));
                        requestDetail.setDriver_mobile(driverdata.getString(Params.PROVIDER_MOBILE_FORMATTED));
                        requestDetail.setDriver_picture(driverdata.getString(Params.PROVIDER_PICTURE));
                        requestDetail.setDriver_car_picture(driverdata.getString("type_picture"));
                        requestDetail.setDriver_car_model(driverdata.getString(Params.MODEL));
                        requestDetail.setDriver_car_color(driverdata.getString(Params.COLOR));
                        requestDetail.setDriver_car_number(driverdata.getString(Params.PLATE_NO));
                        requestDetail.setRequest_type(driverdata.optString(Params.REQUEST_STATUS_TYPE));
                        requestDetail.setNo_tolls(data.optString(Params.NUMBER_TOLLS));
                        requestDetail.setDriver_id(driverdata.getString(Params.PROVIDER_ID));
                        requestDetail.setDriver_rating(driverdata.getString(Params.RATING));
                        requestDetail.setS_address(driverdata.getString(Params.S_ADDRESS));
                        requestDetail.setD_address(driverdata.getString(Params.D_ADDRESS));
                        requestDetail.setS_lat(driverdata.getString(Params.S_LATITUDE));
                        requestDetail.setS_lon(driverdata.getString(Params.S_LONGITUDE));
                        requestDetail.setD_lat(driverdata.getString(Params.D_LANGITUDE));
                        requestDetail.setD_lon(driverdata.getString(Params.D_LONGITUDE));
                        requestDetail.setAdStopLatitude(driverdata.getString(Params.ADD_STOP_LATITUDE));
                        requestDetail.setAdStopLongitude(driverdata.getString(Params.ADD_STOP_LONGITUDE));
                        requestDetail.setAdStopAddress(driverdata.getString(Params.ADD_STOP_ADDRESS));
                        requestDetail.setIsAdStop(driverdata.getString(Params.IS_ADD_STOP));
                        requestDetail.setVehical_img(driverdata.getString(Params.TYPE_PICTURE));
                        requestDetail.setVehical_img(driverdata.getString(Params.TYPE_PICTURE));
                        requestDetail.setFavoriteProvider(driverdata.getInt(Params.IS_FAVORITE_PROVIDER) == 1);
                        requestDetail.setUserFavoriteId(driverdata.optString(Params.USER_FAVOURITE_ID));
                        requestDetail.setRequest_unique_id(driverdata.optString(Params.REQUEST_UNIQUE_ID));
                        if(null != driverdata.optString(Params.START_TIME))
                            requestDetail.setStart_time(driverdata.optString(Params.START_TIME));
                        if(!"".equalsIgnoreCase(driverdata.getString(APIConsts.Params.DRIVER_LANGITUDE)))
                        {
                        requestDetail.setDriver_latitude(Double.valueOf(driverdata.getString(APIConsts.Params.DRIVER_LANGITUDE)));
                        requestDetail.setDriver_longitude(Double.valueOf(driverdata.getString(APIConsts.Params.DRIVER_LONGITUDE)));
                    }

                    }
                    JSONArray invoicejarray = data.getJSONArray(Params.INVOICE);
                    if (invoicejarray.length() > 0) {
                        JSONObject invoiceobj = invoicejarray.getJSONObject(0);
                        requestDetail.setTrip_time(invoiceobj.getString(Params.TOTAL_TIME));
                        requestDetail.setPayment_mode(invoiceobj.getString(Params.PAYMENT_MODE));
                        requestDetail.setTrip_base_price(invoiceobj.getString(Params.BASE_PRICE));
                        requestDetail.setTrip_total_price(invoiceobj.getString(Params.TOTAL));
                        requestDetail.setDistance_unit(invoiceobj.optString(Params.DISTANCE_UNIT));
                        requestDetail.setTrip_distance(invoiceobj.getString(Params.DISTANCE_TRAVEL));
                    }
                } else {
                    requestDetail.setTripStatus(Const.NO_REQUEST);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestDetail;
    }

    public static ArrayList<ChatObject> parseChatObjects(JSONArray data) {
        ArrayList<ChatObject> chatMessages = new ArrayList<>();
        if (data == null)
            return chatMessages;
        for (int i = data.length(); i >= 0; i--) {
            JSONObject item = data.optJSONObject(i);
            ChatObject chatMessage = parseChatObject(item);
            if (chatMessage != null)
                chatMessages.add(chatMessage);
        }
        return chatMessages;
    }


    private static ChatObject parseChatObject(JSONObject item) {
        ChatObject chatMessage = new ChatObject();
        if (item == null)
            return null;
        chatMessage.setBookingId(item.optInt(Params.REQUEST_ID));
        chatMessage.setProviderId(item.optInt(Params.PROVIDER_ID));
        chatMessage.setPersonImage(item.optString(Params.PROVIDER_PICTURE));
        chatMessage.setMyText(item.optString(Params.TYPE).equals(Constants.ChatMessageType.USER_TO_PROVIDER));
        chatMessage.setMessageText(item.optString(Params.MESSAGE));
        chatMessage.setMessageTime(item.optString(Params.UPDATED_AT));
        return chatMessage;
    }



}
