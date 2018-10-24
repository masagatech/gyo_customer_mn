package com.goyo.in.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by jasson on 27/6/16.
 */
public class Constant {
      public static String BASE_URL = "http://admin.goyo.in:8081/api/";
    public static String BASE_NEW = "http://track.goyo.in:8082/goyoapi/";

//    http://35.154.123.76:8081/api/api-list
    //public static String BASE_URL = "http://192.168.1.102:8081/api/";
public static final String URL_SIGNUP = BASE_URL + "userSignUp";
    public static final String URL_LOGIN = BASE_URL + "userLogin";
    public static final String VERIFY_ACCOUNT = BASE_URL + "verifyAccount";
    public static final String RESEND_OTP = BASE_URL + "resendOtp";
    public static final String URL_CHANGE_PASSWORD = BASE_URL + "userPasswordUpdate";
    public static final String URL_GET_USER_PROFILE = BASE_URL + "userProfileGet";
    public static final String saveMedicineInfo = BASE_NEW + "saveMedicineInfo";
    public static final String URL_UPDATE_USER_PROFILE = BASE_URL + "userProfileUpdate";
    public static final String URL_FORGOT_PASSWORD = BASE_URL + "userForgotPassword";
    public static final String URL_RESET_PASSWORD = BASE_URL + "userResetPassword";
    public static final String URL_GET_VEHICLE_TYPES = BASE_URL + "getVehicleTypes";
    public static final String URL_GIVE_FEEDBACK = BASE_URL + "userFeedback";
    public static final String URL_GET_VEHICLE_LIST = BASE_URL + "getVehiclesList";
    public static final String URL_GET_USER_RIDES = BASE_URL + "getUserRides";
    public static final String URL_SAVE_RIDE = BASE_URL + "saveRide";
    public static final String URL_LOGOUT = BASE_URL + "logout";
    public static final String URL_GET_VEHICLE_CHARGE = BASE_URL + "getVehicleTypeCharge";
    public static final String URL_GET_TERIFF_CARD = BASE_URL + "getTeriffCard";
    public static final String URL_GET_RIDE = BASE_URL + "getRide";
    public static final String URL_CITY_TYPE = BASE_URL + "getCities";
    public static final String URL_CONFIRM_RIDE = BASE_URL + "confirmRide";
    public static final String URL_REFERRAL_CODE = BASE_URL + "getReferralCode";
    public static final String URL_USER_WALLET = BASE_URL + "getUserWallet";
    public static final String URL_RIDE_RATE = BASE_URL + "rideRate";
    public static final String URL_GET_RIDE_CANCEL = BASE_URL + "getRideCancelReasons";
    public static final String URL_USER_RIDE_CANCEL = BASE_URL + "cancelRide";
    public static final String URL_GET_DRIVER_LOCATIOIN = BASE_URL + "getDriverLocation";
    public static final String URL_GET_PROMOTION_CODES = BASE_URL + "getPromotionCodes";
    public static final String URL_APPLY_PROMOCODE = BASE_URL + "rideApplyPromotionCode";
    public static final String URL_ADD_MONEY = BASE_URL + "addMoney";
    public static final String URL_GET_NOTIF = BASE_URL + "getNotifications";
    public static final String URL_GET_NOTIF_INFO = BASE_URL + "getNotificationInfo";
    public static final String URL_RIDE_SOS = BASE_URL + "rideSOS";
    public static final String URL_RIDE_PAYMENT = BASE_URL + "ridePayment";
    public static final String URL_TERMS_COND = BASE_URL + "getCms";
    public static final String GET_CITIES = BASE_URL + "getCities";
    public static final String URL_REMOVE_PROMO_CODE = BASE_URL + "rideRemovePromotionCode";
    public static final String START_RIDE = BASE_URL + "rideSendTrackLink";
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static final String UPDATE_LOCATION = BASE_URL + "userLocationUpdate";
    public static final String GET_MY_TICKET = BASE_URL + "getMyTickets";
    public static final String GET_SUPPORT_TYPE = BASE_URL + "supportGetTypes";
    public static final String CREATE_TICKET = BASE_URL + "ticketCreate";
    public static final String GET_FAQ_TYPES = BASE_URL + "faqGetTypes";
    public static final String GET_PAYMENT_METHODS = BASE_URL + "getPaymentMethods";
    public static final String GET_PayUBiz_Hash= BASE_URL + "getPayuBizHashes";
    public static final String GetAppVersion= BASE_NEW + "getAppVersion";
    //public static final String ADD_MONEY= BASE_URL + "addMoney";


    public static boolean CHECK_GPS = true;

    public static boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            Toast.makeText(c, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static Bitmap setMarkerPin(Context context, int pin) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(pin);
        Bitmap bitmap = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, 70, 70, false);
    }
}

