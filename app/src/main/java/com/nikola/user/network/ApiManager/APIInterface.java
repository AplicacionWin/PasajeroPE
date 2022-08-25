package com.nikola.user.network.ApiManager;

import com.nikola.user.Utils.Const;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

import static com.nikola.user.Utils.Const.ServiceType.VERIFICAR_TELEFONO;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.ADD_CARDS;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.ADD_FAV_PROVIDER;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.ADD_MONEY_TO_WALLET;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.ADVERTISEMENTS;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.AIRPORT_LIST;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.AIRPORT_PACKAGE_FARE;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.APPLY_REFERRAL;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.CANCEL_CREATE_REQUEST;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.CANCEL_LATER_REQUEST;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.CANCEL_ONGOING_RIDE;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.CANCEL_REASON;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.CANCEL_REDEEM_REQUEST;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.CARDS;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.CHANGE_DEFAULT_PAYMENT_MODE;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.CHANGE_PASSWORD;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.CHECK_PENDING_PAYMENTS;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.DELETE_CARD;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.FARE_CALCULATION;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.FAV_PORVIDER_LIST;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.FORGOT_PASSWORD;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.GENERAR_CODIGO;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.GET_LATER;
//import static com.nikola.user.network.ApiManager.APIConsts.Apis.GET_OTP;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.GET_PANIC_CONTACT;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.GET_PAYMENT_MODES;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.GET_PROVIDERS;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.HISTORY;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.HOURLY_FARE_CALCULATION;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.LOCATION_LST;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.LOGIN;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.LOGOUT;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.MAKE_DEFAULT_CARD;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.MAKE_DUE_PAYMENTS;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.OV_EARNIGS;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.PROFILE;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.PROMO_CODE;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.PROVIDER_PROFILE;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.RATE_PROVIDER;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.REDEEMS;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.REDEEM_REQUESTS;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.REFERRAL_CODE;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.REGISTER;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.REMOVE_FAV_PROVIDER;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.REQUESTS_VIEW;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.REQUEST_LATER;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.REQUEST_STATUS_CHECK;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.REQUEST_STATUS_CHECK_NEW;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.REQUEST_TAXI;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.SEND_MONEY_TO_REDEEM;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.SEND_PANIC_MESSAGE;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.SERVICE_TYPES;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.SET_PANIC_CONTACT;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.STATIC_PAGES;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.UPDATE_ADDRESS;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.UPDATE_LOCATION;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.UPDATE_PROFILE;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.VALIDAR_EMAIL;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.VERIFICAR_USUARIO;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.VERIFY_USER;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.WALLET;
import static com.nikola.user.network.ApiManager.APIConsts.Apis.WALLET_PAYMENTS;

public interface APIInterface {

    @FormUrlEncoded
    @POST(REGISTER)
    Call<String> doSocialLoginUser(
            @Field(APIConsts.Params.SOCIAL_ID) String socialUniqueId
            , @Field(APIConsts.Params.LOGIN_BY) String loginBy
            , @Field(APIConsts.Params.EMAIL) String email
            , @Field(APIConsts.Params.FIRSTNAME) String firstName
            , @Field(APIConsts.Params.LAST_NAME) String lastName
            , @Field(APIConsts.Params.PICTURE) String picture
            , @Field(APIConsts.Params.DEVICE_TYPE) String deviceType
            , @Field(APIConsts.Params.DEVICE_TOKEN) String deviceToken
            , @Field(APIConsts.Params.TIMEZONE) String timeZone
    );

    @FormUrlEncoded
    @POST(LOGIN)
    Call<String> doMannualLogin(
            @Field(APIConsts.Params.EMAIL) String socialUniqueId
            , @Field(APIConsts.Params.PASSWORD) String password
            , @Field(APIConsts.Params.DEVICE_TYPE) String deviceType
            , @Field(APIConsts.Params.LOGIN_BY) String loginBy
            , @Field(APIConsts.Params.DEVICE_TOKEN) String deviceToken
            , @Field(APIConsts.Params.TIMEZONE) String timeZone
    );




    @FormUrlEncoded
    @POST(GENERAR_CODIGO)
    Call<String> getCodigo(
            @Field(APIConsts.Params.EMAIL) String email,
            @Field(APIConsts.Params.MOBILE) String mobile,
            @Field(APIConsts.Params.COUNTRY_CODE) String country_code,
            @Field(APIConsts.Params.CODIGO) String codigo,
            @Field(APIConsts.Params.CODIGO_W) String codigo_w
    );


    @FormUrlEncoded
    @POST(VERIFY_USER)
    Call<String> verifyUser(
            @Field(APIConsts.Params.EMAIL) String mail,
            @Field(APIConsts.Params.USERNAME) String user,
            @Field(APIConsts.Params.REFERRAL_CODE) String ref
    );


    @FormUrlEncoded
    @POST(REGISTER)
    Call<String> doSignUpUser(
            @Field(APIConsts.Params.FIRSTNAME) String firstName
            , @Field(APIConsts.Params.LAST_NAME) String lastName
            , @Field(APIConsts.Params.USERNAME) String username
            , @Field(APIConsts.Params.EMAIL) String email
            , @Field(APIConsts.Params.PASSWORD) String password
            , @Field(APIConsts.Params.MOBILE) String phone
            , @Field(APIConsts.Params.CURRENCY) String currency
            , @Field(APIConsts.Params.DEVICE_TOKEN) String deviceToken
            , @Field(APIConsts.Params.DEVICE_TYPE) String deviceType
            , @Field(APIConsts.Params.LOGIN_BY) String loginBy
            , @Field(APIConsts.Params.TIMEZONE) String timeZone
            , @Field(APIConsts.Params.REFERRAL_CODE) String referralCode
            , @Field(APIConsts.Params.COUNTRY_CODE) String countryCode
            , @Field(APIConsts.Params.IDENTIFICACION) String identificacion);

    @FormUrlEncoded
    @POST(OV_EARNIGS)
    Call<String> getEarningsData(@Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token);

    @FormUrlEncoded
    @POST(APPLY_REFERRAL)
    Call<String> applyReferralCode(
            @Field(APIConsts.Params.REFERRAL_CODE) String referralCode
    );

    @FormUrlEncoded
    @POST(VERIFICAR_USUARIO)
    Call<String> verificarUsuario(
            @Field(APIConsts.Params.USERNAME) String username
    );
    @FormUrlEncoded
    @POST(VALIDAR_EMAIL)
    Call<String> applymail(
            @Field(APIConsts.Params.EMAIL) String email
    );

    @FormUrlEncoded
    @POST(VERIFICAR_TELEFONO)
    Call<String> verificarTelefono(
            @Field(APIConsts.Params.MOBILE) String mobile
    );

    @FormUrlEncoded
    @POST(FORGOT_PASSWORD)
    Call<String> makeCallForgotPassword(
            @Field(APIConsts.Params.EMAIL) String email
    );

    @Multipart
    @POST(UPDATE_PROFILE)
    Call<String> doUpdateProfile(
            @Part(APIConsts.Params.ID) int id
            , @Part(APIConsts.Params.TOKEN) String token
            , @Part(APIConsts.Params.FIRSTNAME) String firstName
            , @Part(APIConsts.Params.LAST_NAME) String lastName
            , @Part(APIConsts.Params.EMAIL) String email
            , @Part(APIConsts.Params.MOBILE) String phone
            , @Part MultipartBody.Part picture
            , @Part(APIConsts.Params.DEVICE_TOKEN) String deviceToken
            , @Part(APIConsts.Params.DEVICE_TYPE) String deviceType
            , @Part(APIConsts.Params.LOGIN_BY) String loginBy
            , @Part(APIConsts.Params.TIMEZONE) String timeZone
            , @Part(APIConsts.Params.GENDER) String gender
            , @Part(APIConsts.Params.COUNTRY_CODE) String countryCode
    );

    @FormUrlEncoded
    @POST(LOGOUT)
    Call<String> doLogoutUser(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(CARDS)
    Call<String> getAllCards(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(ADD_CARDS)
    Call<String> addCard(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.CARD_TOKEN) String cardToken
    );

    @FormUrlEncoded
    @POST(DELETE_CARD)
    Call<String> deleteCard(@Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.USER_CARD_ID) String cardId);


    @FormUrlEncoded
    @POST(MAKE_DEFAULT_CARD)
    Call<String> makeCardDefault(@Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.USER_CARD_ID) String cardId);

    @FormUrlEncoded
    @POST(HISTORY)
    Call<String> getHistory(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SKIP) int skip);

    @FormUrlEncoded
    @POST(GET_LATER)
    Call<String> getLaterRequest(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SKIP) int skip);

    @FormUrlEncoded
    @POST(ADVERTISEMENTS)
    Call<String> getAdsFromBackend(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token);


    @FormUrlEncoded
    @POST(SERVICE_TYPES)
    Call<String> getServiceTypes(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token);

    @FormUrlEncoded
    @POST(SERVICE_TYPES)
    Call<String> getServiceTypesForHourlyBooking(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.IS_HOURLY_BOOKING) int isHourlyBooking
            , @Field(APIConsts.Params.LATITUD_ORIGEN) double longitud_origen
            , @Field(APIConsts.Params.LONGITUD_ORIGEN) double latitud_origen);


    @FormUrlEncoded
    @POST(SERVICE_TYPES)
    Call<String> getServiceDistanceAirport(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.DISTANCE) float distance
            , @Field(APIConsts.Params.TIME) float duration
            , @Field(APIConsts.Params.IS_AIRPORT) int isAirport
    );

    @FormUrlEncoded
    @POST(SERVICE_TYPES)
    Call<String> getServiceWithDistance(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.DISTANCE) float distance
            , @Field(APIConsts.Params.TIME) float duration
            , @Field(APIConsts.Params.LATITUD_ORIGEN) double longitud_origen
            , @Field(APIConsts.Params.LONGITUD_ORIGEN) double latitud_origen
    );

    @FormUrlEncoded
    @POST(GET_PROVIDERS)
    Call<String> getAllAvailableProviders(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.LATITUDE) double lat
            , @Field(APIConsts.Params.LONGITUDE) double lng
            , @Field(APIConsts.Params.SERVICE_TYPE_ID) String serviceType
    );

    @FormUrlEncoded
    @POST(REQUEST_LATER)
    Call<String> scheduleALaterRequest(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SERVICE_TYPE_ID) String serviceType
            , @Field(APIConsts.Params.S_ADDRESS) String sAddress
            , @Field(APIConsts.Params.D_ADDRESS) String dAddress
            , @Field(APIConsts.Params.S_LATITUDE) double lat
            , @Field(APIConsts.Params.S_LONGITUDE) double lng
            , @Field(APIConsts.Params.D_LATITUDE) double dLat
            , @Field(APIConsts.Params.D_LONGITUDE) double dLng
            , @Field(APIConsts.Params.REQUEST_SERVICE_TYPE) int requestStatusType
            , @Field(APIConsts.Params.REQUESTED_TIME) String requestedTime
            , @Field(APIConsts.Params.ESTIMATED_PRICE) String estimatedFare
            , @Field(APIConsts.Params.PAYMENT_MODE) String paymentMode
    );

    @FormUrlEncoded
    @POST(REQUEST_STATUS_CHECK)
    Call<String> pingRequestStatusCheck(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token);


    @FormUrlEncoded
    @POST(FARE_CALCULATION)
    Call<String> calculateFare(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.DISTANCE) double distance
            , @Field(APIConsts.Params.TIME) String time
            , @Field(APIConsts.Params.SERVICE_TYPE_ID) String serviceType
    );

    @GET
    Call<String> getLocationBasedResponse(@Url String url);

    @FormUrlEncoded
    @POST(AIRPORT_PACKAGE_FARE)
    Call<String> airportFareCalculation(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.AIRPORT_ID) String airportDetailsId
            , @Field(APIConsts.Params.LOCATION_ID) String locationDetailsId
            , @Field(APIConsts.Params.SERVICE_TYPE_ID) String serviceType
    );

    @FormUrlEncoded
    @POST(HOURLY_FARE_CALCULATION)
    Call<String>  hourlyFareCalculation(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SERVICE_TYPE_ID) String serviceTypeId
            , @Field(APIConsts.Params.NO_HOUR) String noOfHours
    );

    @FormUrlEncoded
    @POST(REQUEST_LATER)
    Call<String> scheduleaAirportRide(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SERVICE_TYPE_ID) String serviceType
            , @Field(APIConsts.Params.S_ADDRESS) String sAddress
            , @Field(APIConsts.Params.D_ADDRESS) String dAddress
            , @Field(APIConsts.Params.S_LATITUDE) double lat
            , @Field(APIConsts.Params.S_LONGITUDE) double lng
            , @Field(APIConsts.Params.D_LATITUDE) double dLat
            , @Field(APIConsts.Params.D_LONGITUDE) double dLng
            , @Field(APIConsts.Params.REQUEST_SERVICE_TYPE) int requestStatusType
            , @Field(APIConsts.Params.AIRPORT_PRICE_ID) String airportPriceId
            , @Field(APIConsts.Params.PAYMENT_MODE) String paymentMode
            , @Field(APIConsts.Params.REQUESTED_TIME) String time
    );

    @FormUrlEncoded
    @POST(REQUEST_TAXI)
    Call<String> createAirportNowRequest(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SERVICE_TYPE_ID) String serviceType
            , @Field(APIConsts.Params.S_ADDRESS) String sAddress
            , @Field(APIConsts.Params.D_ADDRESS) String dAddress
            , @Field(APIConsts.Params.S_LATITUDE) double lat
            , @Field(APIConsts.Params.S_LONGITUDE) double lng
            , @Field(APIConsts.Params.D_LATITUDE) double dLat
            , @Field(APIConsts.Params.D_LONGITUDE) double dLng
            , @Field(APIConsts.Params.REQUEST_SERVICE_TYPE) int requestStatusType
            , @Field(APIConsts.Params.AIRPORT_PRICE_ID) String airportPriceId
            , @Field(APIConsts.Params.PAYMENT_MODE) String paymentMode
    );

    @FormUrlEncoded
    @POST(REQUEST_TAXI)
    Call<String> createAHourlyRequest(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SERVICE_TYPE_ID) String serviceType
            , @Field(APIConsts.Params.S_ADDRESS) String sAddress
            , @Field(APIConsts.Params.S_LATITUDE) double lat
            , @Field(APIConsts.Params.S_LONGITUDE) double lng
            , @Field(APIConsts.Params.REQUEST_SERVICE_TYPE) int requestStatusType
            , @Field(APIConsts.Params.HOURLY_PACKAGE_ID) String hourlyPackageId
            , @Field(APIConsts.Params.PAYMENT_MODE) String paymentMode
    );

    @FormUrlEncoded
    @POST(REQUEST_LATER)
    Call<String> scheduleAHourlyPackage(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SERVICE_TYPE_ID) String serviceType
            , @Field(APIConsts.Params.S_ADDRESS) String sAddress
            , @Field(APIConsts.Params.S_LATITUDE) double lat
            , @Field(APIConsts.Params.S_LONGITUDE) double lng
            , @Field(APIConsts.Params.REQUEST_SERVICE_TYPE) int requestStatusType
            , @Field(APIConsts.Params.HOURLY_PACKAGE_ID) String hourlyPackageId
            , @Field(APIConsts.Params.PAYMENT_MODE) String paymentMode
            , @Field(APIConsts.Params.REQUESTED_TIME) String time
    );

    @FormUrlEncoded
    @POST(LOCATION_LST)
    Call<String> locationList(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.KEY) String key
    );

    @FormUrlEncoded
    @POST(AIRPORT_LIST)
    Call<String> getAirportList(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(CANCEL_CREATE_REQUEST)
    Call<String> cancelRequest(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(UPDATE_ADDRESS)
    Call<String> updateAddress(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.REQUEST_ID) int requestId
            , @Field(APIConsts.Params.ADDRESS) String address
            , @Field(APIConsts.Params.LATITUDE) double lat
            , @Field(APIConsts.Params.LONGITUDE) double lng
            , @Field(APIConsts.Params.CHANGE_TYPE) String changeType
    );

    @FormUrlEncoded
    @POST(CANCEL_ONGOING_RIDE)
    Call<String> cancelOngoingRide(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.REQUEST_ID) int requestId
            , @Field(APIConsts.Params.REASON_ID) String reasonId
            , @Field(APIConsts.Params.CANCELLATION_REASON) String cancellationReason
    );

    @FormUrlEncoded
    @POST(SEND_PANIC_MESSAGE)
    Call<String> sendPanicMessage(
            @Field(APIConsts.Params.ID) int id
            ,@Field(APIConsts.Params.REQUEST_ID) int requestId
            ,@Field(APIConsts.Params.ADDRESS) String actualAddress

    );
    @FormUrlEncoded
    @POST(SET_PANIC_CONTACT)
    Call<String> setPanicContact(
            @Field(APIConsts.Params.ID) int id
            ,@Field(APIConsts.Params.CONTACT_NUMBER) String contactNumber

    );

    @FormUrlEncoded
    @POST(GET_PANIC_CONTACT)
    Call<String> getPanicContact(
            @Field(APIConsts.Params.ID) int id


    );

    @FormUrlEncoded
    @POST(CANCEL_REASON)
    Call<String> cancelReasonsList(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(CHANGE_PASSWORD)
    Call<String> changePassword(@Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.OLD_PASSWORD) String curPassword
            , @Field(APIConsts.Params.PASSWORD) String newPassword
            , @Field(APIConsts.Params.CONFIRM_PASSWORD) String newPasswordConfirm);

    @FormUrlEncoded
    @POST(FORGOT_PASSWORD)
    Call<String> forgotPassword(@Field(APIConsts.Params.EMAIL) String email);


    @FormUrlEncoded
    @POST(CHANGE_DEFAULT_PAYMENT_MODE)
    Call<String> changeDefaultPaymentMode(
            @Field(APIConsts.Params.ID) int id,
            @Field(APIConsts.Params.TOKEN) String token,
            @Field(APIConsts.Params.PAYMENT_MODE) String paymentMode
    );

    @FormUrlEncoded
    @POST(RATE_PROVIDER)
    Call<String> rateProvider(
            @Field(APIConsts.Params.ID) int id,
            @Field(APIConsts.Params.TOKEN) String token,
            @Field(APIConsts.Params.REQUEST_ID) int requestId,
            @Field(APIConsts.Params.COMMENT) String comment,
            @Field(APIConsts.Params.RATING) int rating
    );

    @FormUrlEncoded
    @POST(GET_PAYMENT_MODES)
    Call<String> getPaymentMethods(
            @Field(APIConsts.Params.ID) int id,
            @Field(APIConsts.Params.TOKEN) String token,
            @Field(APIConsts.Params.ESTIMATED_PRICE) String estimatedFare
    );

    @FormUrlEncoded
    @POST(REQUEST_TAXI)
    Call<String> createNowRequest(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SERVICE_TYPE_ID) String serviceType
            , @Field(APIConsts.Params.S_ADDRESS) String sAddress
            , @Field(APIConsts.Params.D_ADDRESS) String dAddress
            , @Field(APIConsts.Params.ADD_STOP_ADDRESS) String stopAddress
            , @Field(APIConsts.Params.S_LATITUDE) double lat
            , @Field(APIConsts.Params.S_LONGITUDE) double lng
            , @Field(APIConsts.Params.D_LATITUDE) double dLat
            , @Field(APIConsts.Params.D_LONGITUDE) double dLng
            , @Field(APIConsts.Params.ADD_STOP_LATITUDE) double stopLat
            , @Field(APIConsts.Params.ADD_STOP_LONGITUDE) double stopLng
            , @Field(APIConsts.Params.REQUEST_SERVICE_TYPE) int requestStatusType
            , @Field(APIConsts.Params.PROMO_CODE) String airportPriceId
            , @Field(APIConsts.Params.ESTIMATED_PRICE) String estimatedPrice
            , @Field(APIConsts.Params.PAYMENT_MODE) String paymentMode);

    @FormUrlEncoded
    @POST(CANCEL_LATER_REQUEST)
    Call<String> cancelLaterRequest(
            @Field(APIConsts.Params.ID) int id,
            @Field(APIConsts.Params.TOKEN) String token,
            @Field(APIConsts.Params.REQUEST_ID) String requestId);


    @FormUrlEncoded
    @POST(APIConsts.Apis.CHAT_DETAILS)
    Call<String> getChatDetails(@Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.REQUEST_ID) int bookingId
            , @Field(APIConsts.Params.PROVIDER_ID) String providerId
            , @Field(APIConsts.Params.SKIP) int skip);

    @FormUrlEncoded
    @POST(REFERRAL_CODE)
    Call<String> getReferralCode(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.USERNAME) String username
    );

    @FormUrlEncoded
    @POST(REQUESTS_VIEW)
    Call<String> getRequestsView(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.REQUEST_ID) String requestId
    );

    @FormUrlEncoded
    @POST(PROFILE)
    Call<String> getProfile(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(PROVIDER_PROFILE)
    Call<String> getProviderProfile(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.PROVIDER_ID) String providerId
    );

    @GET(STATIC_PAGES)
    Call<String> getStaticPages(@Query(Const.Params.PAGE_TYPE) String pageType);

    @FormUrlEncoded
    @POST(WALLET)
    Call<String> getWalletData(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(WALLET_PAYMENTS)
    Call<String> getWalletTransactions(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SKIP) int skip
    );

    @FormUrlEncoded
    @POST(ADD_MONEY_TO_WALLET)
    Call<String> addMoneyToWallet(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.AMOUNT) String amount
            , @Field(APIConsts.Params.PAYMENT_MODE) String paymentMode
    );

    @FormUrlEncoded
    @POST(CANCEL_REDEEM_REQUEST)
    Call<String> cancelRedeemRequest(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.USER_REDEEM_REQUEST_ID) String userRequestId
    );

    @FormUrlEncoded
    @POST(REDEEMS)
    Call<String> getRedeemsList(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(SEND_MONEY_TO_REDEEM)
    Call<String> sendMoneyForRedeem(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.AMOUNT) String amount
    );

    @FormUrlEncoded
    @POST(REDEEM_REQUESTS)
    Call<String> getAllRedeemRequests(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.SKIP) int skip
    );

    @FormUrlEncoded
    @POST(PROMO_CODE)
    Call<String> applyPromoCode(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.PROMO_CODE) String promoCode
    );

    @FormUrlEncoded
    @POST(REMOVE_FAV_PROVIDER)
    Call<String> removeFavProvider(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.USER_FAVOURITE_ID) String userFavId);

    @FormUrlEncoded
    @POST(ADD_FAV_PROVIDER)
    Call<String> addFavProvider(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.PROVIDER_ID) String providerId
    );

    @FormUrlEncoded
    @POST(FAV_PORVIDER_LIST)
    Call<String> listFavProvider(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(CHECK_PENDING_PAYMENTS)
    Call<String> checkingForPendingPayments(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(MAKE_DUE_PAYMENTS)
    Call<String> makeDuePayments(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.PAYMENT_MODE) String paymentMode
    );

    @FormUrlEncoded
    @POST(UPDATE_LOCATION)
    Call<String> updateCurrentLocation(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
            , @Field(APIConsts.Params.LATITUDE) double latitude
            , @Field(APIConsts.Params.LONGITUDE) double longitude
    );

    @Multipart
    @POST(UPDATE_PROFILE)
    Call<String> updateMobileNumber(
            @Part(APIConsts.Params.ID) int id
            , @Part(APIConsts.Params.TOKEN) String token
            , @Part(APIConsts.Params.MOBILE) String phone
            , @Part(APIConsts.Params.COUNTRY_CODE) String countryCode);


    @FormUrlEncoded
    @POST(REQUEST_STATUS_CHECK_NEW)
    Call<String> requestStatusCheckNew(
            @Field(APIConsts.Params.ID) int id
            , @Field(APIConsts.Params.TOKEN) String token
    );

    @GET
    Call<String> getDirectionsWay(@Url String url);



}
