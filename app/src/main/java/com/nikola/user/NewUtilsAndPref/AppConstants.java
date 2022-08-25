    package com.nikola.user.NewUtilsAndPref;

public class AppConstants {

    public static final int API_STATUS_CODE_LOCAL_ERROR = 0;

    //TODO change data here
    public static final String DB_NAME = "mindorks_mvp.db";
    public static final String PREF_NAME = "mindorks_pref";

    public static final long NULL_INDEX = -1L;

    public static final String SEED_DATABASE_OPTIONS = "seed/options.json";
    public static final String SEED_DATABASE_QUESTIONS = "seed/questions.json";

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final long LOCATION_INTERVAL = 5000;
    public static final long FASTEST_LOCATION_INTERVAL = 5000;
    private AppConstants() {
        // This utility class is not publicly instantiable
    }


    public class CancelStatusCodes {
        public static final int CAN_CANCEL = 1;
        public static final int CAN_NOT_CANCEL = 0;
    }

    public static class Fragments {
        public static final String REGISTER_FRAGMENT = "registerFragment";
        public static final String OPT_FRAGMENT = "otpFragment";
        public static final String SIGNUP_NEXT_FRAGMENT = "signUpNextFragment";

        private Fragments() {
        }
    }

}
