package com.goyo.in;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.in.FCM.MyFirebaseMessagingService;
import com.goyo.in.ModelClasses.MyKidsModel;
import com.goyo.in.ModelClasses.model_notification;
import com.goyo.in.Service.BGService;
import com.goyo.in.Utils.Constant;
import com.goyo.in.Utils.Global;
import com.goyo.in.Utils.Preferences;
import com.goyo.in.VolleyLibrary.RequestInterface;
import com.goyo.in.VolleyLibrary.VolleyRequestClass;
import com.goyo.in.VolleyLibrary.VolleyTAG;
import com.goyo.in.fragment.BookYourRideFragment;
import com.goyo.in.fragment.FAQFragment;
import com.goyo.in.fragment.FeedbackFragment;
import com.goyo.in.fragment.HomeFragment;
import com.goyo.in.fragment.MainWalletFragment;
import com.goyo.in.fragment.MessengerFood;
import com.goyo.in.fragment.MessengerMedicine;
import com.goyo.in.fragment.MessengerParcel;
import com.goyo.in.fragment.MyRidesFragment;
import com.goyo.in.fragment.MyTicketsFragment;
import com.goyo.in.fragment.NotificationsFragment;
import com.goyo.in.fragment.OrderGrocery;
import com.goyo.in.fragment.PromotionCodeFragment;
import com.goyo.in.fragment.ReferralCodeFragment;
import com.goyo.in.fragment.TariffCardFragment;
import com.goyo.in.fragment.TermsAndConditionsFragment;
import com.goyo.in.logger.Log;
import com.goyo.in.other.CircleTransform;
import com.goyo.in.school.MyKids;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.List;

import io.hypertrack.smart_scheduler.SmartScheduler;
import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private ImageView imgProfile;
    private Toolbar toolbar;
    public static int navItemIndex = 0;
    private String[] activityTitles;
    private Handler mHandler;
    private View navHeader;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private BroadcastReceiver mReceiveMessageFromNotification;
    boolean doubleBackToExitPressedOnce = false;
    private TextView txtName, edit_profile, actionbar_title, logout;
    public static final String TAG_HOME = "HOME";
    public static final String TAG_BOOK_YOUR_RIDE = "BOOK YOUR RIDE";
    public static final String TAG_MY_RIDES = "MY RIDES";
    public static final String TAG_TARIFF_CARD = "TARIFF CARD";
    public static final String TAG_PROMOTION_CODE = "PROMOTION CODE";
    public static final String TAG_REFERRAL_CODE = "REFERRAL CODE";
    public static final String TAG_MY_WALLET = "MY WALLET";
    public static final String TAG_NOTIFICATIONS = "NOTIFICATIONS";
    public static final String TAG_FEEDBACK = "FEEDBACK";
    public static final String TAG_TERMS_CONDITIONS = "TERMS AND CONDITIONS";
    public static final String TAG_MY_KIDS = "MY KID";
    public static final String TAG_MY_TICKET = "MY TICKET";
    public static final String TAG_FAQ = "FAQ";
    public static final String TAG_MESSENGER_FOOD = "FOOD";
    public static final String TAG_MESSENGER_MEDICINE = "MEDICINE";
    public static final String TAG_ORDER_GROCERY = "GROCERY";
    public static final String TAG_MESSENGER_PARCEL = "PARCEL";
    private AlertDialog.Builder builder;
    private String TAG = "MainActivity";
    public static String CURRENT_TAG = TAG_HOME;
    NotificationManager mNotificationManager;
    private FragmentManager mFragmentManager;

    BookYourRideFragment bookYourRideFragment;
    TermsAndConditionsFragment termsAndConditionsFragment;
    String latestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        edit_profile = (TextView) navHeader.findViewById(R.id.edit_profile);
        logout = (TextView) navHeader.findViewById(R.id.logout);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        //Checking App Version
        try {
            AppVerCheck();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        String menuFragment = getIntent().getStringExtra("from");
        String addMoney = getIntent().getStringExtra("addMoney");
        Log.d("#########", "addMoney : " + addMoney);

        if (menuFragment != null) {
            if (menuFragment.equals("notifServicePayment")) {
                setUpNavigationView();
                MainWalletFragment myWalletFragment = new MainWalletFragment();
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, myWalletFragment, CURRENT_TAG = TAG_MY_WALLET);
                navItemIndex = 5;
                activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                actionbar_title.setText(R.string.nav_my_wallet);
                CURRENT_TAG = TAG_MY_WALLET;
                fragmentTransaction.commitAllowingStateLoss();
            } else if (menuFragment.equals("notifServiceRideCancelCharge")) {
                setUpNavigationView();
                MainWalletFragment myWalletFragment = new MainWalletFragment();
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, myWalletFragment, CURRENT_TAG = TAG_MY_WALLET);
                navItemIndex = 5;
                activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                actionbar_title.setText(R.string.nav_my_wallet);
                CURRENT_TAG = TAG_MY_WALLET;
                fragmentTransaction.commitAllowingStateLoss();
            } else if (menuFragment.equals("notifyUser_Add_Money")) {
                setUpNavigationView();
                MainWalletFragment myWalletFragment = new MainWalletFragment();
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, myWalletFragment, CURRENT_TAG = TAG_MY_WALLET);
                navItemIndex = 5;
                activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                actionbar_title.setText(R.string.nav_my_wallet);
                CURRENT_TAG = TAG_MY_WALLET;
                fragmentTransaction.commitAllowingStateLoss();
            } else {

            }
        } else {
            if (addMoney != null) {
                if (addMoney.equals("sucessAddMoney")) {
                    setUpNavigationView();
                    MainWalletFragment myWalletFragment = new MainWalletFragment();
                    Fragment fragment = getHomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, myWalletFragment, CURRENT_TAG = TAG_MY_WALLET);
                    navItemIndex = 5;
                    activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                    actionbar_title.setText(R.string.nav_my_wallet);
                    CURRENT_TAG = TAG_MY_WALLET;
                    fragmentTransaction.commitAllowingStateLoss();
                }
            } else {
                activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                actionbar_title.setText(R.string.nav_home);
                setUpNavigationView();
                if (savedInstanceState == null) {
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    loadHomeFragment();
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }

        setUpNavigationView();

        edit_profile.setOnClickListener(this);
        logout.setOnClickListener(this);

        getMessageFromNotification();
//        rideCancelByDriverNotify();
        if (Constant.isOnline(MainActivity.this)) {
            getUserProfileAPI();
        }
//        GetLatestVersion task = new GetLatestVersion();
//        task.execute();

        if (Preferences.getValue_String(this, Preferences.IS_RATED).equals("1")) {
            showRatingDialog();
        }


        try {
            if (!isMyServiceRunning(BGService.class)) {
                Intent myService = new Intent(MainActivity.this, BGService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(myService);
                } else {
                    startService(myService);
                }
            }
        } catch (Exception ex) {

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    private void checkForPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            // Permission not yet granted. Use requestPermissions().
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            // Permission already granted.
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contact:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    checkForPhonePermission();
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return true;
                }
                Intent intenta = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+919081222121"));
                startActivity(intenta);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
        //respond to menu item selection
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (permissions[0].equalsIgnoreCase
                        (android.Manifest.permission.CALL_PHONE)
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.


                } else {
                    // Permission denied. Stop the app.
                    Toast.makeText(this,
                            "Permission Denied!",
                            Toast.LENGTH_SHORT).show();
                    // Disable the call button

                }
            }
        }
    }


    private void showRatingDialog() {
/*        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_rate_again);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate Ride");
        builder.setMessage("Do you want to rate your last ride ?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MainActivity.this, RateThisRide.class));
            }
        });
        builder.show();
    }

    private void rideCancelByDriverNotify() {
        mReceiveMessageFromNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                android.util.Log.d(TAG, "data: " + "app open notif COMPLETE RIDE main activity 1");
                if (intent.getAction().equals(MyFirebaseMessagingService.MESSAGE_NOTIFICATION)) {
                    android.util.Log.d(TAG, "data: " + "app open notif COMPLETE RIDE main activity 2");
                    if (intent.getExtras() != null) {
                        android.util.Log.d(TAG, "data: " + "app open notif COMPLETE RIDE main activity 3");
                        String mRideid = intent.getStringExtra("i_ride_id");
                        Intent in = new Intent(MainActivity.this, CompleteRide.class);
                        in.putExtra("i_ride_id", mRideid);
                        startActivity(in);
                    }
                }
            }
        };
    }


    private void getUserProfileAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_USER_PROFILE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(MainActivity.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        txtName.setText(jsonObject.getString("v_name"));
                        Preferences.setValue(getApplicationContext(), Preferences.USER_EMAIL, jsonObject.getString("v_email"));
                        Preferences.setValue(getApplicationContext(), Preferences.USER_MOBILE, jsonObject.getString("v_phone"));
                        Preferences.setValue(getApplicationContext(), Preferences.USER_IMAGE, jsonObject.getString("v_image"));

                        if (response.getJSONObject("data").getString("v_image").equals("")) {
                            imgProfile.setImageResource(R.drawable.no_user_white);
                        } else {
                            Glide.with(MainActivity.this).load(response.getJSONObject("data").getString("v_image"))
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .bitmapTransform(new CircleTransform(MainActivity.this))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imgProfile);
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void loadHomeFragment() {
        drawer.closeDrawers();
        setToolbarTitle();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                BookYourRideFragment bookYourRideFragment = new BookYourRideFragment();
//                Preferences.setValue(getApplicationContext(),"comeFromm","MyRides");
                return bookYourRideFragment;
            case 2:
                MyRidesFragment myRidesFragment = new MyRidesFragment();
                return myRidesFragment;
            case 3:
                MyKids mykidsFragment = new MyKids();
                return mykidsFragment;
            case 4:
                TariffCardFragment tariffCardFragment = new TariffCardFragment();
                return tariffCardFragment;
            case 5:
                PromotionCodeFragment promotionCodeFragment = new PromotionCodeFragment();
                return promotionCodeFragment;
            case 6:
                ReferralCodeFragment referralCodeFragment = new ReferralCodeFragment();
                return referralCodeFragment;
            case 7:
                MainWalletFragment myWalletFragment = new MainWalletFragment();
                return myWalletFragment;
            case 8:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
            case 9:
                FeedbackFragment feedbackFragment = new FeedbackFragment();
                return feedbackFragment;
            case 10:
                MyTicketsFragment myTicketsFragment = new MyTicketsFragment();
                return myTicketsFragment;
            case 11:
                FAQFragment faqFragment = new FAQFragment();
                return faqFragment;
            case 12:
                TermsAndConditionsFragment termsAndConditionsFragment = new TermsAndConditionsFragment();
                return termsAndConditionsFragment;
            case 13:
                MessengerFood messengerFood = new MessengerFood();
                return messengerFood;
            case 14:
                MessengerMedicine messengerMedicine = new MessengerMedicine();
                return messengerMedicine;
            case 15:
                OrderGrocery orderGrocery = new OrderGrocery();
                return orderGrocery;
            case 16:
                MessengerParcel messengerParcel = new MessengerParcel();
                return messengerParcel;
            default:
                return new BookYourRideFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }


    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        navItemIndex = 0;
                        actionbar_title.setText(R.string.nav_home);
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_book_my_ride:
                        navItemIndex = 1;
                        actionbar_title.setText(R.string.nav_book_my_ride);
                        CURRENT_TAG = TAG_BOOK_YOUR_RIDE;
                        break;
                    case R.id.nav_my_rides:
                        navItemIndex = 2;
                        actionbar_title.setText(R.string.nav_my_rides);
                        CURRENT_TAG = TAG_MY_RIDES;
                        break;
                    case R.id.nav_my_kids:
                        navItemIndex = 3;
                        actionbar_title.setText(R.string.nav_my_kids);
                        CURRENT_TAG = TAG_MY_KIDS;
                        break;

                    case R.id.nav_tariff_card:
                        navItemIndex = 4;
                        actionbar_title.setText(R.string.nav_tariff_card);
                        CURRENT_TAG = TAG_TARIFF_CARD;
                        break;
                    case R.id.nav_promotion_code:
                        navItemIndex = 5;
                        actionbar_title.setText(R.string.nav_promotion_code);
                        CURRENT_TAG = TAG_PROMOTION_CODE;
                        break;
                    case R.id.nav_referral_code:
                        navItemIndex = 6;
                        actionbar_title.setText(R.string.nav_referral_code);
                        CURRENT_TAG = TAG_REFERRAL_CODE;
                        break;
                    case R.id.nav_my_wallet:
                        navItemIndex = 7;
                        actionbar_title.setText(R.string.nav_my_wallet);
                        CURRENT_TAG = TAG_MY_WALLET;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 8;
                        actionbar_title.setText(R.string.nav_notifications);
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_feedback:
                        navItemIndex = 9;
                        actionbar_title.setText(R.string.nav_Feedback);
                        CURRENT_TAG = TAG_FEEDBACK;
                        break;
                    case R.id.nav_mytickets:
                        navItemIndex = 10;
                        actionbar_title.setText(R.string.nav_mytickets);
                        CURRENT_TAG = TAG_MY_TICKET;
                        break;
                    case R.id.nav_faq:
                        navItemIndex = 11;
                        actionbar_title.setText(R.string.nav_faq);
                        CURRENT_TAG = TAG_FAQ;
                        break;
                    case R.id.nav_terms_conditions:
                        navItemIndex = 12;
                        actionbar_title.setText(R.string.nav_terms);
                        CURRENT_TAG = TAG_TERMS_CONDITIONS;
                        break;

                    case R.id.nav_messenger_food:
                        navItemIndex = 13;
                        actionbar_title.setText(R.string.nav_messenger_food);
                        CURRENT_TAG = TAG_MESSENGER_FOOD;
                        break;
                    case R.id.nav_messenger_medicine:
                        navItemIndex = 14;
                        actionbar_title.setText(R.string.nav_messenger_medicine);
                        CURRENT_TAG = TAG_MESSENGER_MEDICINE;
                        break;
                    case R.id.nav_order_grocery:
                        navItemIndex = 15;
                        actionbar_title.setText(R.string.nav_order_grocery);
                        CURRENT_TAG = TAG_ORDER_GROCERY;
                        break;
                    case R.id.nav_messenger_parcel:
                        navItemIndex = 16;
                        actionbar_title.setText(R.string.nav_messenger_parcel);
                        CURRENT_TAG = TAG_MESSENGER_PARCEL;
                        break;

                    default:
                        navItemIndex = 0;
                        actionbar_title.setText(R.string.nav_home);
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                menuItem.setChecked(true);
                loadHomeFragment();
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (navItemIndex != 0) {
            HomeFragment homeFragment = new HomeFragment();
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, homeFragment, CURRENT_TAG = TAG_HOME);
            navItemIndex = 0;
            actionbar_title.setText(R.string.nav_home);
            CURRENT_TAG = TAG_HOME;
            navigationView.getMenu().getItem(0).setChecked(true);
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finishAffinity();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile:
                Intent mIntent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(mIntent);
                break;
            case R.id.logout:

                signOutGoogle();
                signOutFaceBook();
                if (Constant.isOnline(MainActivity.this)) {
                    user_logout();
                }
                break;
        }
    }


    private void signOutGoogle() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
        } catch (Exception ex) {

        }
    }


    private void signOutFaceBook() {
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception ex) {

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void user_logout() {
        if (Preferences.getValue_String(getApplicationContext(), Preferences.MOKE_LOGIN).equals("true")) {

            Preferences.setValue(getApplicationContext(), Preferences.USER_ID, "");
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            return;
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_LOGOUT).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(MainActivity.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Preferences.setValue(getApplicationContext(), Preferences.USER_ID, "");
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    } else if (message.equals("Not logged in.")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Preferences.setValue(getApplicationContext(), Preferences.USER_ID, "");
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void getMessageFromNotification() {
        mReceiveMessageFromNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                android.util.Log.d(TAG, "data: " + "app open notif START RIDE main activity 1");
                if (intent.getAction().equals(MyFirebaseMessagingService.MESSAGE_SUCCESS)) {
                    android.util.Log.d(TAG, "data: " + "app open notif START RIDE main activity 2");
                    if (intent.getExtras() != null) {
                        android.util.Log.d(TAG, "data: " + "app open notif START RIDE main activity 3");
                        String mRideid = intent.getStringExtra("i_ride_id");
                        Intent in = new Intent(MainActivity.this, StartRideActivity.class);
                        in.putExtra("i_ride_id", mRideid);
                        startActivity(in);
                        finish();
                    }
                } else if (intent.getAction().equals(MyFirebaseMessagingService.RIDE_CANCEL_BY_DRIVER)) {
                    android.util.Log.d(TAG, "data: " + "app open notif main activity");
                    if (intent.getExtras() != null) {
                        android.util.Log.d(TAG, "data: " + "app open notif main activity");
                        String mTitle = intent.getStringExtra("mTitle");
                        String mBody = intent.getStringExtra("mBody");
                        builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                        builder.setTitle(mTitle);
                        builder.setMessage(mBody);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        });
                        builder.show();
                    }

                } else if (intent.getAction().equals(MyFirebaseMessagingService.COMPLETE_RIDE)) {
                    android.util.Log.d(TAG, "data: " + "app open notif main activity");
                    if (intent.getExtras() != null) {
                        android.util.Log.d(TAG, "data: " + "app open notif main activity");
                        String mRideid = intent.getStringExtra("i_ride_id");
                        Intent in = new Intent(MainActivity.this, CompleteRide.class);
                        in.putExtra("i_ride_id", mRideid);
                        startActivity(in);
                    }
                }

            }
        };
    }

//    private void rideCancelByDriverNotify() {
//        mReceiveMessageFromNotification = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, final Intent intent) {
//                if (intent.getAction().equals(MyFirebaseMessagingService.RIDE_CANCEL_BY_DRIVER)) {
//                    if (intent.getExtras() != null) {
//                        android.util.Log.d(TAG, "data: " + "app open notif main activity");
//                        String mTitle = intent.getStringExtra("mTitle");
//                        String mBody = intent.getStringExtra("mBody");
//                        builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
//                        builder.setTitle(mTitle);
//                        builder.setMessage(mBody);
//                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                dialog.dismiss();
//                               Intent intent=new Intent(getApplicationContext(),MainActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                        builder.show();
//
//                    }
//                }
//            }
//        };
//    }


    @Override
    public void onPause() {
        super.onPause();
//        App.activityPaused();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiveMessageFromNotification);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Checking App Version
        try {
            AppVerCheck();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        App.activityResumed();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.MESSAGE_SUCCESS));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.MESAGE_ERROR));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.MESSAGE_NOTIFICATION));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.RIDE_CANCEL_BY_DRIVER));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.COMPLETE_RIDE));
        Log.e("#########", "Receiver : ");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    /*private void isDroverOpen()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);

        }else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public void initBookYourRideFragment()
    {
        bookYourRideFragment = BookYourRideFragment.newInstance();

        if(!bookYourRideFragment.isAdded())
        {
            mFragmentManager.beginTransaction().replace(R.id.frame,bookYourRideFragment).commit();
        }
    }



    public void initTwoFragment()
    {
        termsAndConditionsFragment = TermsAndConditionsFragment.newInstance();

        if(!termsAndConditionsFragment.isAdded())
        {
            mFragmentManager.beginTransaction().add(R.id.frame,termsAndConditionsFragment).addToBackStack(termsAndConditionsFragment.getClass().getName()).commit();
        }

    }*/

    private class GetLatestVersion extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String urlOfAppFromPlayStore = "https://play.google.com/store/apps/details?id=com.crest.goyo&hl=en";
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                Document doc = Jsoup.connect(urlOfAppFromPlayStore).get();
                latestVersion = doc.getElementsByAttributeValue("itemprop", "softwareVersion").first().text();
                android.util.Log.e("latestVersion", "latestVersion playstore: " + latestVersion);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (!GetThisVersion().equals(latestVersion)) {
                ShowUpdateDialog();
            }
        }
    }

    String GetThisVersion() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        assert pInfo != null;
        android.util.Log.e("latestVersion", "latestVersion system" + pInfo.versionName);
        return pInfo.versionName;
    }

    void ShowUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Available");
        builder.setMessage("Are you sure to update to new version?");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.in.in" + getPackageName() + "&hl=en")));
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void AppVerCheck() throws PackageManager.NameNotFoundException {
        final Integer VersionCode = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionCode;
        Ion.with(this)
                .load("GET", Constant.GetAppVersion)
                .addQuery("uid", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID))
                .addQuery("key", "mainver")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        try {
                            if (result != null) android.util.Log.v("result", result.toString());
                            int ver = result.get("data").getAsJsonArray().get(0).getAsJsonObject().get("val").getAsInt();
                            if (VersionCode < ver) {
                                new android.app.AlertDialog.Builder(MainActivity.this)
                                        .setTitle(R.string.update_head)
                                        .setCancelable(false)
                                        .setMessage(R.string.update_body)
                                        .setPositiveButton(R.string.update_yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.goyo.in")));
                                                } catch (ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.goyo.in")));
                                                }
                                            }
                                        })
                                        .setNegativeButton(R.string.update_no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                                intent.addCategory(Intent.CATEGORY_HOME);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                            }
                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                    }
                });
    }


}
