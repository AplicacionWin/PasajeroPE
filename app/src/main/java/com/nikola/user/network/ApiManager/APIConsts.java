package com.nikola.user.network.ApiManager;

public class APIConsts {

    public static final String HOME_MAP_FRAGMENT = "homeFragment";
    public static final String ONGOING_FRAGMENT = "ongoingFragment";

    private APIConsts() {
    }

    public static class Constants {
        public static final String MANUAL_LOGIN = "manual";
        public static final String GOOGLE_LOGIN = "google";
        public static final String FACEBOOK_LOGIN = "facebook";
        public static final String ANDROID = "android";
        public static final String SUCCESS = "success";
        public static final String ERROR = "error";
        public static final String TRUE = "true";
        public static final String FALSE = "false";

        public static final String GOOGLE_API_KEY = "AIzaSyBC_Ff-gjCriYGBVzZ6BMpJzTO1Hk7uMaY";
        public static final String OK = "ok";
        public static final String STATUS = "status";

        public class ChatMessageType {
            public static final String USER_TO_PROVIDER = "up";
            public static final String PROVIDER_TO_USER = "pu";
            public static final String HOST_TO_USER = "hu";
            public static final String USER_TO_HOST = "uh";
        }
    }

    public static class RequestStatus {
        public static final int IS_CREATED = 0;
        public static final int IS_ACCEPTED = 1;
        public static final int IS_DRIVER_DEPARTED = 2;
        public static final int IS_DRIVER_ARRIVED = 3;
        public static final int IS_DRIVER_TRIP_STARTED = 4;
        public static final int IS_DRIVER_TRIP_ENDED = 5;
        public static final int IS_DRIVER_RATED = 6;
    }

    public static class PushNotificationStatus {
        public static final String PUSH_NOTIFICATION_REDIRECT_HOME = "1";

        public static final String PUSH_NOTIFICATION_REDIRECT_REQUESTS = "2";

        public static final String PUSH_NOTIFICATION_REDIRECT_REQUEST_VIEW = "3";

        public static final String PUSH_NOTIFICATION_REDIRECT_REQUEST_ONGOING = "4";

        public static final String PUSH_NOTIFICATION_REDIRECT_CHAT = "5";
    }

    public static class Urls {


        public static final String HOST_URL = "http://peru.aplicacionwin.com/";
        public static final String SOCKET_URL = "http://peru.aplicacionwin.com:3000/";

        /*public static final String HOST_URL = "http://167.172.97.206/";
        public static final String SOCKET_URL = "http://167.172.97.206:3000/";*/
        public static final String BASE_URL = HOST_URL;

        private Urls() {
        }
    }

    public static class ErrorCodes {

        public static final int TOKEN_EXPIRED = 1003;
        public static final int USER_DOESNT_EXIST = 1002;
        public static final int INVALID_TOKEN = 1004;
        public static final int CARD_NOT_ADDED = 1005;
        public static final int ID_OR_TOKEN_MISSING = 1006;
        public static final int VERIFY_USER = 1001;
        public static final int NOT_APPROVED = 1000;
        public static final int REDIRECT_PAYMENTS = 111;
        public static final int CARDNOTFOUND = 120;
        public static final int WALLETEMPTY = 6007;
        public static final int ADMIN_APPROVAL = 1000;
        public static final int PRICINGNOTFOUND = 518;


        private ErrorCodes() {
        }
    }

    public static class Apis {
        public static final String API_STR = "api/user/";
        public static final String LOGIN = API_STR + "nuevoLogin";
        public static final String REGISTER = API_STR + "nuevoRegister";
        public static final String FORGOT_PASSWORD = API_STR + "forgot_password";
        public static final String CHANGE_PASSWORD = API_STR + "change_password";
        public static final String PROFILE = API_STR + "profile";
        public static final String GENERAR_CODIGO = API_STR + "generar_codigo";
        //public static final String CODIGOPOSTAL = API_STR + "country_code";
        public static final String VERIFY_USER = API_STR + "verifieUserData";
        public static final String VERIFICAR_TELEFONO = API_STR + "check-mobile";
        public static final String BUSCAR_EMAIL = API_STR + "check-email";
        public static final String APPLY_REFERRAL = API_STR + "check-username";
        public static final String VERIFICAR_USUARIO = API_STR + "check-username";
        public static final String VALIDAR_EMAIL = API_STR + "check-email";
        public static final String VERIFY_EMAIL_APP = API_STR + "check-email";
        public static final String OV_EARNIGS = API_STR + "get-balance";
        public static final String UPDATE_PROFILE = API_STR + "update_profile";
        public static final String LOGOUT = API_STR + "logout";
        public static final String DELETE_CARD = API_STR + "cards_delete";
        public static final String MAKE_DEFAULT_CARD = API_STR + "cards_default";
        public static final String HISTORY = API_STR + "requests_history";
        public static final String GET_LATER = API_STR + "requests_upcoming";
        public static final String CARDS = API_STR + "cards_list";
        public static final String ADD_CARDS = API_STR + "cards_add";
        public static final String ADVERTISEMENTS = API_STR + "advertisements";
        public static final String SERVICE_TYPES = API_STR + "services_list";
        public static final String REQUEST_LATER = API_STR + "requests_create_later";
        public static final String REQUEST_STATUS_CHECK = API_STR + "request_status_check";
        public static final String REQUEST_STATUS_CHECK_NEW = API_STR + "request_status_check_new";
        public static final String FARE_CALCULATION = API_STR + "fare_calculator";
        public static final String AIRPORT_PACKAGE_FARE = API_STR + "airport_package_fare";
        public static final String REQUEST_TAXI = API_STR + "requests_create";
        public static final String LOCATION_LST = API_STR + "locations_list";
        public static final String AIRPORT_LIST = API_STR + "airports_list";
        public static final String CANCEL_CREATE_REQUEST = API_STR + "waiting_request_cancel";
        public static final String UPDATE_ADDRESS = API_STR + "requests_address_update";
        public static final String CANCEL_ONGOING_RIDE = API_STR + "requests_cancel";
        public static final String SEND_PANIC_MESSAGE = API_STR + "sendPanicMessage";
        public static final String SET_PANIC_CONTACT = API_STR + "setPanicContact";
        public static final String GET_PANIC_CONTACT = API_STR + "getPanicContact";
        public static final String CHANGE_DEFAULT_PAYMENT_MODE = API_STR + "payment_mode_default";
        public static final String CANCEL_REASON = API_STR + "cancellation_reasons";
        public static final String VALIDATE_PROMO = API_STR + "promo_code_check";
        public static final String GET_PROVIDERS = API_STR + "guest_provider_list";
        public static final String RATE_PROVIDER = API_STR + "requests_rate_provider";
        public static final String GET_PAYMENT_MODES = API_STR + "requests_create_payment_modes";
        public static final String CANCEL_LATER_REQUEST = API_STR + "requests_cancel_later";
        public static final String CHAT_DETAILS = API_STR + "requests_chat_view";
        //public static final String REFERRAL_CODE = API_STR + "referral_codes";
        public static final String REFERRAL_CODE = API_STR + "check-username";
        public static final String REQUESTS_VIEW = API_STR + "requests_view";
        public static final String PROVIDER_PROFILE = API_STR + "provider_profile";
        public static final String WALLET = API_STR + "wallets";
        public static final String WALLET_PAYMENTS = API_STR + "wallets_payments";
        public static final String ADD_MONEY_TO_WALLET = API_STR + "wallets_add_money";
        public static final String REDEEMS = API_STR + "redeems";
        public static final String CANCEL_REDEEM_REQUEST = API_STR + "redeems_requests_cancel";
        public static final String SEND_MONEY_TO_REDEEM = API_STR + "redeems_requests_send";
        public static final String REDEEM_REQUESTS = API_STR + "redeems_requests";
        public static final String STATIC_PAGES = API_STR + "pages/list";
        public static final String PROMO_CODE = API_STR + "promo_code_check";
        public static final String HOURLY_FARE_CALCULATION = API_STR + "hourly_package_fare";
        public static final String REMOVE_FAV_PROVIDER = API_STR + "user_favourite_providers_delete";
        public static final String ADD_FAV_PROVIDER = API_STR + "user_favourite_providers_add";
        public static final String FAV_PORVIDER_LIST = API_STR + "user_favourite_providers";
        public static final String CHECK_PENDING_PAYMENTS = API_STR + "is_any_pending_payment";
        public static final String MAKE_DUE_PAYMENTS = API_STR + "user_pending_payment";
        public static final String UPDATE_LOCATION = API_STR + "update_location";

        //        Google Apis
        public static final String LOCATION_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        public static final String DISTANCE_LOCATION_API = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    }

    public static class Params {
        public static final String ID = "id";
        public static final String TOKEN = "token";
        public static final String NAME = "name";
       // public static final String NAME = "name";
        public static final String USERNAME = "username";
        public static final String EMAIL = "email";
        public static final String IDENTIFICACION = "identificacion";
        public static final String CODIGO = "codigo";
        public static final String CODIGO_W = "codigo_w";
        public static final String CODIGOPOSTAL = "country_code";
        public static final String GENERAR = "generar";
        public static final String PASSWORD = "password";
        public static final String LOGIN_BY = "login_by";
        public static final String ERROR_MESSAGE = "error_message";
        public static final String DESCRIPTION = "description";
        public static final String PICTURE = "picture";
        public static final String NOTIF_PUSH_STATUS = "push_notification_status";
        public static final String NOTIF_EMAIL_STATUS = "email_status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String ERROR = "error";
        public static final String CODE = "code";
        public static final String CONTACT_NUMBER = "contact_number";
        public static final String SOCIAL_ID = "social_unique_id";
        public static final String DEVICE_TYPE = "device_type";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String PHONE = "phone";
        public static final String FIRSTNAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String CURRENCY = "currency";
        public static final String TIMEZONE = "timezone";
        public static final String REFERRAL_CODE = "referral_code";
        public static final String REFERRAL_BONUS = "referee_bonus";
        public static final String GENDER = "gender";
        public static final String CARDS = "cards";
        public static final String CARD_LAST_FOUR = "last_four";
        public static final String CARD_NAME = "card_holder_name";
        public static final String IS_DEFAULT = "is_default";
        public static final String USER_CARD_ID = "user_card_id";
        public static final String CARD_TOKEN = "card_token";
        public static final String REQUESTS = "requests";
        public static final String SERVICES = "services";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String PROVIDERS = "providers";
        public static final String S_ADDRESS = "s_address";
        public static final String D_ADDRESS = "d_address";
        public static final String D_LATITUDE = "d_latitude";
        public static final String D_LONGITUDE = "d_longitude";
        public static final String REQUEST_SERVICE_TYPE = "request_status_type";
        public static final String DISTANCE = "distance";
        public static final String TIME = "time";
        public static final String LATITUD_ORIGEN = "latitud_origen";
        public static final String LONGITUD_ORIGEN = "longitud_origen";
        public static final String SERVICE_ID = "service_id";
        public static final String DURATION = "duration";
        public static final String AIRPORT_DETAILS_ID = "airport_details_id";
        public static final String AIRPORT_ID = "airport_id";
        public static final String LOCATION_DETAILS_ID = "location_details_id";
        public static final String LOCATION_ID = "location_id";
        public static final String AIRPORT_PRICE_DETAILS = "airport_price_details";
        public static final String AIRPORT_PRICE_ID = "airport_price_id";
        public static final String KEY = "key";
        public static final String AIRPORT_DETAILS = "airport_details";
        public static final String REQUEST_ID = "request_id";
        public static final String ADDRESS = "address";
        public static final String CHANGE_TYPE = "change_type";
        public static final String REASON_ID = "reason_id";
        public static final String CANCELLATION_REASON = "cancellation_reason";
        public static final String PAYMENT_MODE = "payment_mode";
        public static final String MOBILE = "mobile";
        public static final String USER_ID = "user_id";
        public static final String OLD_PASSWORD = "old_password";
        public static final String CONFIRM_PASSWORD = "password_confirmation";
        public static final String ERROR_CODE = "error_code";
        public static final String PAYMENT_MODES = "payment_modes";
        public static final String PAYMENT_MODE_IMAGE = "payment_mode_image";
        public static final String SKIP = "skip";
        public static final String DATE = "date";
        public static final String PROVIDER_NAME = "provider_name";
        public static final String TAXI_NAME = "taxi_name";
        public static final String TOTAL = "total";
        public static final String MAP_IMAGE = "map_image";
        public static final String BASE_PRICE = "base_price";
        public static final String DISTANCE_TRAVEL = "distance_travel";
        public static final String TOTAL_TIME = "total_time";
        public static final String TAX_PRICE = "tax_price";
        public static final String TIME_PRICE = "time_price";
        public static final String DISTANCE_PRICE = "distance_price";
        public static final String MIN_PRICE = "min_fare";
        public static final String BOOKING_FEE = "booking_fee";
        public static final String DISTANCE_UNIT = "distance_unit";
        public static final String REQUESTED_TIME = "requested_time";
        public static final String SERVICE_TYPE_NAME = "service_type_name";
        public static final String TYPE_PICTURE = "type_picture";
        public static final String URL = "url";
        public static final String SERVICE_TYPE_ID = "service_type_id";
        public static final String MIN_FARE = "min_fare";
        public static final String PRICE_PER_MIN = "price_per_min";
        public static final String PRICE_PER_UNIT_DISTANCE = "price_per_unit_distance";
        public static final String NUMBER_SEAT = "number_seat";
        public static final String CANCELLATION_FINE = "cancellation_fine";
        public static final String PROVIDER_STATUS = "provider_status";
        public static final String STATUS = "status";
        public static final String PROVIDER_MOBILE = "provider_mobile";
        public static final String PROVIDER_PICTURE = "provider_picture";
        public static final String CAR_IMAGE = "car_image";
        public static final String MODEL = "model";
        public static final String COLOR = "color";
        public static final String PLATE_NO = "plate_no";
        public static final String REQUEST_STATUS_TYPE = "request_status_type";
        public static final String NUMBER_TOLLS = "number_tolls";
        public static final String PROVIDER_ID = "provider_id";
        public static final String RATING = "rating";
        public static final String S_LATITUDE = "s_latitude";
        public static final String S_LONGITUDE = "s_longitude";
        public static final String D_LANGITUDE = "d_latitude";
        public static final String ADD_STOP_LATITUDE = "adstop_latitude";
        public static final String ADD_STOP_LONGITUDE = "adstop_longitude";
        public static final String IS_ADD_STOP = "is_adstop";
        public static final String IS_ADDRESS_CHANGED = "is_address_changed";
        public static final String DRIVER_LANGITUDE = "driver_latitude";
        public static final String DRIVER_LONGITUDE = "driver_longitude";
        public static final String INVOICE = "invoice";
        public static final String COMMENT = "comment";
        public static final String ADD_STOP_ADDRESS = "adstop_address";
        public static final String PROMO_CODE = "promo_code";
        public static final String COMMONID = "commonid";
        public static final String MYID = "myid";
        public static final String UPDATED_AT = "updated_at";
        public static final String TYPE = "type";
        public static final String CHAT_TYPE = "chat_type";
        public static final String USER_PICTURE = "user_picture";
        public static final String REFERRER_BONUS_FORMATTED = "referrer_bonus_formatted";
        public static final String IS_PREFILL = "is_prefill";
        public static final String SERVICE_TAX = "service_tax";

        public static final String USER_CANCEL_REASON = "user_cancel_reason";
        public static final String PROVIDER_CANCEL_REASON = "provider_cancel_reason";
        public static final String REQUEST_UNIQUE_ID = "request_unique_id";
        public static final String START_TIME = "start_time";
        public static final String PROVIDER_ARRIVAL_TIME = "provider_arrival_time";
        public static final String REQUEST_MAP_IMAGE = "request_map_image";
        public static final String TOTAL_FORMATTED = "total_formatted";
        public static final String RIDE_FARE = "ride_fare_formatted";
        public static final String CANCELLATION_FARE_FORMATTED = "cancellation_fare_formatted";
        public static final String TAX_FARE_FORMATTED = "tax_fare_formatted";
        public static final String SERVICE_FARE_FORMATTED = "service_fare_formatted";
        public static final String DISCOUNT = "discount_formatted";
        public static final String REQUEST_BUTTON_STATUS = "requests_btn_status";
        public static final String TRACK_STATUS = "track_status";
        public static final String CANCEL_BUTTON_STATUS = "cancel_btn_status";
        public static final String MESSAGE_BTN_STATUS = "message_btn_status";
        public static final String REQUEST_CREATED_TIME = "request_created_time";
        public static final String INVOICE_DETAILS = "invoice_details";
        public static final String PROVIDER_DETAILS = "provider_details";
        public static final String SERVICE_MODEL = "service_model";
        public static final String BASE_PRICE_FORMATTED = "base_price_formatted";
        public static final String BOOKING_FEE_FORMATTED = "booking_fee_formatted";
        public static final String TIME_PRICE_FORMATTED = "time_price_formatted";
        public static final String TIME_PRICE_NOTE = "time_price_note";
        public static final String DISTANCE_PRICE_FORMATTED = "distance_price_formatted";
        public static final String SURGE_PRICE_FORMATTED = "surge_price_formatted";
        public static final String DEBT_AMOUNT_FORMATTED = "debt_amount_formatted";
        public static final String DISTANCE_PRICE_NOTE = "distance_price_note";
        public static final String REQUEST_STAUS_ICON = "request_icon_status";
        public static final String REQUEST_STAUS_ICON_TEXT = "request_icon_status_text";
        public static final String INVOICE_BUTTON_STATUS = "invoice_status";
        public static final String COUNTRY_CODE = "country_code";
        public static final String WALLET = "wallet";
        public static final String REMAINING = "remaining";
        public static final String PAYMENTS = "payments";
        public static final String TITLE = "title";
        public static final String UNIQUE_ID = "unique_id";
        public static final String WALLET_SYSTEM_SYMBOL = "wallet_amount_symbol";
        public static final String WALLET_IMAGE = "wallet_image";
        public static final String PAID_AMOUNT = "paid_amount";
        public static final String AMOUNT = "amount";
        public static final String USED = "used";
        public static final String IMAGE = "image";
        public static final String AMOUNT_SYMBOL = "amount_symbol";
        public static final String USER_REDEEM_REQUEST_ID = "user_redeem_request_id";
        public static final String IS_REDEEM = "isRedeem";
        public static final String REMINING_FORMATTED = "remaining_formatted";
        public static final String REMINING = "remaining";

        public static final String USED_FORMATTED = "used_formatted";
        public static final String ESTIMATED_FARE_FORMATTED = "estimated_fare_formatted";
        public static final String MOBILE_FORMATTED = "mobile_formatted";
        public static final String PROVIDER_MOBILE_FORMATTED = "provider_mobile_formatted";
        public static final String NO_HOUR = "number_hours";
        public static final String HOURLY_PACKAGE_ID = "hourly_package_id";
        public static final String SCHEDULED_TIME = "scheduled_time";
        public static final String LATER = "later";
        public static final String WAITING_BTN_CANCEL = "waiting_cancel_btn_status";
        public static final String USER_FAVOURITE_ID = "user_favourite_id";
        public static final String PROVIDER_RATING = "provider_ratings";
        public static final String IS_USER_NEED_PAY_NOW = "is_user_needs_pay_now";
        public static final String COUNTRY_NAME = "country_name";
        public static final String PRICE = "price";
        public static final String ESTIMATED_PRICE = "estimated_fare";
        public static final String LOCATION = "location";
        public static final String USER_TOKEN = "user_token";
        public static final String REFERAL_SHARE_MESSAGE = "referral_share_message";
        public static final String PROVIDER_LOCATION = "provider-location";
        public static final String USER_LOCATION_UPDATE = "user-location-update";
        public static final String SENDER_UPDATE = "update sender";
        public static final String IS_FAVORITE_PROVIDER = "is_favourite_provider";
        public static final String IS_USER_ADD_MONEY_TO_WALLET = "is_user_add_money_to_wallet";
        public static final String ADD_WALLET_AMOUNT_FORMATTED = "add_wallet_amount_formatted";
        public static final String WALLET_REMAINING_FORMATTED = "wallet_remaining_formatted";
        public static final String ADD_WALLET_AMOUNT = "add_wallet_amount";
        public static final String BEARING = "bearing";
        public static final String IS_AIRPORT = "is_airport";
        public static final String IS_HOURLY_BOOKING = "is_hourly_package";
        public static final String TIMEOUT = "timeout";
    }

}
