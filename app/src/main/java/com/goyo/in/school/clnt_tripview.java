package com.goyo.in.school;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.goyo.in.ModelClasses.MarkerItem;
import com.goyo.in.ModelClasses.MyKidsModel;
import com.goyo.in.ModelClasses.model_tripdata;
import com.goyo.in.ModelClasses.vts_vh_model;
import com.goyo.in.R;
import com.goyo.in.SocketClient.SC_IOApplication;
import com.goyo.in.Utils.Global;
import com.goyo.in.classes.CustomClusterRenderer;
import com.goyo.in.googlemap.LatLngInterpolator;
import com.goyo.in.googlemap.MarkerAnimation;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class clnt_tripview extends AppCompatActivity implements OnMapReadyCallback {

    //UI
    TextView tvSpeed, tvLastloc, txtBatch;
    //socket
    private Socket mSocket;
    private boolean isSocConnected = false;

    //googel map related variables
    private GoogleMap mMap;
    private boolean isRecenter = true;
    private List<HashMap<String, Marker>> driverOnMap = new ArrayList<HashMap<String, Marker>>();
    private String tripid = "0";
    private String vhid = "0", studid = "";
    private List<model_tripdata> lstMytripdata;
    private float tilt = 0;
    private float zoom = 17f;
    private boolean upward = true;
    private String status = "0";
    //font
    Typeface tf;

    //views
    View kid1, kid2, kid3, kid4, kid5;
    TextView txtk1, txtk2, txtk3, txtk4, txtk5;
    ImageView imgk1, imgk2, imgk3, imgk4, imgk5;
    String strVehicles;
    List<String> vharr = new ArrayList<>();

    private class SelectedChild {

        public SelectedChild(LatLng _childLatLng, String _vehicle, View view) {
            this.child = _childLatLng;
            this.vehicle = _vehicle;
            this.view = view;
        }

        LatLng child;
        String vehicle;
        View view;

    }

    SelectedChild selctedChild;

    private void setSelectedVh(LatLng _childLatLng, String _vehicle, View view) {
        if (selctedChild != null) {
            ((CheckBox) selctedChild.view.findViewById(R.id.chkactive)).setChecked(false);
        }
        try {

            selctedChild = null;
            selctedChild = new SelectedChild(_childLatLng, _vehicle, view);
            ((CheckBox) view.findViewById(R.id.chkactive)).setChecked(true);

            int i = vharr.indexOf(_vehicle);
            if (i == -1) return;
            vts_vh_model m = vhs.get(i);
            boundtwo(selctedChild.child, new LatLng(m.lat, m.lon));

        } catch (Exception ex) {
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clnt_tripview);
        addCustomFont();
        setTitle("Trip View");
        initUI();
        getBundle();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //Initialize
    private void initUI() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        kid1 = findViewById(R.id.kid1);
        kid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyKidsModel mod = (MyKidsModel) kid1.getTag();
                if (mod == null) return;
                setSelectedVh(new LatLng(mod.loc.lat, mod.loc.lon), mod.imei, kid1);
            }
        });
        kid2 = findViewById(R.id.kid2);

        kid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyKidsModel mod = (MyKidsModel) kid2.getTag();
                if (mod == null) return;
                setSelectedVh(new LatLng(mod.loc.lat, mod.loc.lon), mod.imei, kid2);
            }
        });
        kid3 = findViewById(R.id.kid3);
        kid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyKidsModel mod = (MyKidsModel) kid3.getTag();
                if (mod == null) return;
                setSelectedVh(new LatLng(mod.loc.lat, mod.loc.lon), mod.imei, kid3);
            }
        });
        kid4 = findViewById(R.id.kid4);
        kid4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyKidsModel mod = (MyKidsModel) kid4.getTag();
                if (mod == null) return;
                setSelectedVh(new LatLng(mod.loc.lat, mod.loc.lon), mod.imei, kid4);
            }
        });
        kid5 = findViewById(R.id.kid5);
        kid5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyKidsModel mod = (MyKidsModel) kid5.getTag();
                if (mod == null) return;
                setSelectedVh(new LatLng(mod.loc.lat, mod.loc.lon), mod.imei, kid5);
            }
        });

        txtk1 = (TextView) kid1.findViewById(R.id.txtName);
        txtk2 = (TextView) kid2.findViewById(R.id.txtName);
        txtk3 = (TextView) kid3.findViewById(R.id.txtName);
        txtk4 = (TextView) kid4.findViewById(R.id.txtName);
        txtk5 = (TextView) kid5.findViewById(R.id.txtName);


        imgk1 = (ImageView) kid1.findViewById(R.id.img_icon);
        imgk2 = (ImageView) kid2.findViewById(R.id.img_icon);
        imgk3 = (ImageView) kid3.findViewById(R.id.img_icon);
        imgk4 = (ImageView) kid4.findViewById(R.id.img_icon);
        imgk5 = (ImageView) kid5.findViewById(R.id.img_icon);


//        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
//        tvSpeed.setTypeface(tf);
//        tvLastloc = (TextView) findViewById(R.id.tvLastloc);
//        txtBatch = (TextView) findViewById(R.id.txtBatch);
        //showTimeSpeed("0", "------");


//        ListView listView1 = (ListView) findViewById(R.id.lstkids);
//
//        String[] items = { "Milk", "Butter", "Yogurt", "Toothpaste", "Ice Cream" };
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.layout_icon_text,R.id.txtName, items);
//
//        listView1.setAdapter(adapter);

    }

    private void showTimeSpeed(String speed, String Time) {
        tvSpeed.setText(speed + " Km/h");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {

            tvLastloc.setText(toLocalDateString(Time));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String toLocalDateString(String utcTimeStamp) {
        Date utcDate = new Date(utcTimeStamp);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        df.setTimeZone(TimeZone.getTimeZone("IN"));
        return df.format(utcDate);
    }

    private void googleMapInit() {
        SupportMapFragment mMap1 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMap1.getMapAsync(this);
        //animator = new Animator();

    }


    private ClusterManager<MarkerItem> mClusterManager;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;//get map object after ready
        //check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setTrafficEnabled(true);
        setUpClusterer();
        addMapListner();
        getKidsOnTrip();
        // getLastKnownLocation();
        //if (!status.equals("2"))

    }

    private void setUpClusterer() {
        // Position the map.
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MarkerItem>(this, mMap);

        final CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, mClusterManager);
        // ClusterRenderer clusterRenderer = new ClusterRenderer(this, mMap, mClusterManager); // not needed to use clusterManager.setRenderer method since i made it in constructor
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.


        mClusterManager.setRenderer(renderer);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MarkerItem>() {
            @Override
            public boolean onClusterClick(Cluster<MarkerItem> cluster) {
                // Toast.makeText(clnt_tripview.this, "Cluster click", Toast.LENGTH_SHORT).show();

                return false;
            }
        });

        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<MarkerItem>() {
                    @Override
                    public boolean onClusterItemClick(MarkerItem markerItem) {
                        Toast.makeText(clnt_tripview.this, "Cluster item click", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });


        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MarkerItem>() {
            @Override
            public void onClusterItemInfoWindowClick(MarkerItem markerItem) {
                Toast.makeText(clnt_tripview.this, "Clicked info window: " + markerItem.getTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Add cluster items (markers) to the cluster manager.
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mMap.setOnMarkerClickListener(mClusterManager);
    }


//    public class ClusterRenderer extends DefaultClusterRenderer<MarkerItem> {
//        private final IconGenerator mClusterIconGenerator;
//        Context mContext;
//        ClusterManager clusterManager;
//        public ClusterRenderer(Context context, GoogleMap map, ClusterManager<MarkerItem> clusterManager) {
//            super(context, map, clusterManager);
//            this.mClusterIconGenerator  = new IconGenerator(context.getApplicationContext());
//            this.mContext = context;
//            clusterManager.setRenderer(this);
//        }
//
//
//        @Override
//        protected void onBeforeClusterItemRendered(MarkerItem markerItem, MarkerOptions markerOptions) {
//            if (markerItem.getIcon() != null) {
//                markerOptions.icon(markerItem.getIcon()); //Here you retrieve BitmapDescriptor from ClusterItem and set it as marker icon
//            }
//            mClusterIconGenerator.setBackground(
//                    ContextCompat.getDrawable(mContext, R.drawable.background_circle));
//            mClusterIconGenerator.setTextAppearance(R.color.white);
//            final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
//
//            markerOptions.visible(true);
//        }
//    }


    private void addMapListner() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                isRecenter = false;
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //TODO: Any custom actions

                return false;
            }
        });


    }

    //google map helptes


    private void addMarkerEntry(String id, Marker marker) {
        HashMap<String, Marker> entry = new HashMap<String, Marker>();
        entry.put(id, marker);
        driverOnMap.add(entry);
    }

    private boolean markerExitsts(String id) {
        for (int i = 0; i <= driverOnMap.size() - 1; i++) {
            HashMap<String, Marker> l = driverOnMap.get(i);
            if (l.get(id).equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addMarkerToMap(LatLng latLng, String title, String snippet, String Id, String bearing, String speed, String sertm) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus1))
                .zIndex(1.0f)
                .flat(true)
        );
        addMarkerEntry(Id, marker);
        moveMarker(marker, latLng.latitude + "", latLng.longitude + "", bearing, speed, sertm);
    }


    private boolean removeCreawFromMap(String id) {
        int searchListLength = driverOnMap.size();
        for (int i = 0; i < searchListLength; i++) {
            if (driverOnMap.get(i).containsKey(id)) {
                Marker mrk = driverOnMap.get(i).get(id);
                mrk.remove();
                driverOnMap.get(i).remove(id);
                return true;
            }
        }
        return false;
    }

    public void navigateToPoint(LatLng latLng, float tilt, float bearing, float zoom, boolean animate) {
        CameraPosition position =
                new CameraPosition.Builder().target(latLng)
                        .zoom(zoom)
                        .bearing(bearing)
                        .tilt(tilt)
                        .build();

        changeCameraPosition(position, animate);


    }

    private void changeCameraPosition(CameraPosition cameraPosition, boolean animate) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        if (animate) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }

    }

    //pub sub socket client
    private void SocketClient() {
        SC_IOApplication app = new SC_IOApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("msgd", onNewMessage);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSocket.connect();
            }
        }, 1500);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Connected", Toast.LENGTH_LONG).show();
                    if (!isSocConnected) {
                        //if(null!=mUsername)

                        // mSocket.emit("register", tripid);
                        isSocConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.i(TAG, "diconnected");
                    isSocConnected = false;
                    /*Toast.makeText(getApplicationContext(),
                            "Disconnect", Toast.LENGTH_LONG).show();*/
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.e(TAG, "Error connecting");
                    /*Toast.makeText(getApplicationContext(),
                            "Unable to connect server!", Toast.LENGTH_LONG).show();*/
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;*/
                    try {

                        JSONObject data = ((JSONObject) args[0]);
                        Decoder(data);

//
//                        if (data.get("evt").equals("regreq")) {
//                            mSocket.emit("reg_v", strVehicles.replace("\"", ""));
//                        } else if (data.get("evt").equals("registered")) {
//                            //JSONObject objTrp = (JSONObject) data.get("data");
//                            Toast.makeText(getApplicationContext(),
//                                    "registered", Toast.LENGTH_LONG).show();
//                        } else if (data.get("evt").equals("data")) {
//                            JSONObject objTrp = (JSONObject) data.get("data");
//                            Decoder(objTrp);
//                            // trackMarker(objTrp);
//                            /*Toast.makeText(getApplicationContext(),
//                                    d, Toast.LENGTH_LONG).show();*/
//                        }
////                        else if (data.get("evt").equals("stop")) {
////                           // JSONObject objTrp = (JSONObject) data.get("data");
////                            Toast.makeText(clnt_tripview.this, "Trip End", Toast.LENGTH_LONG).show();
////                        }

                    } catch (Exception e) {
                        //Log.e(TAG, e.getMessage());
                        return;
                    }
                }
            });
        }
    };

    private void trackMarker(JSONObject objTrp) throws JSONException {
        String trpid = objTrp.get("tripid").toString();
        String lat = objTrp.get("lat").toString();
        String lon = objTrp.get("lon").toString();
        String speed = objTrp.get("speed").toString();
        String bearing = objTrp.get("bearing").toString();
        String servertm = objTrp.get("sertm").toString();


        for (int i = 0; i <= driverOnMap.size() - 1; i++) {
            HashMap<String, Marker> l = driverOnMap.get(i);
            if (l.containsKey(trpid)) {
                Marker mrk = l.get(trpid);
                moveMarker(mrk, lon, lat, bearing, speed, servertm);
                return;
            }
        }
        addMarkerToMap(new LatLng(Double.parseDouble(lon), Double.parseDouble(lat)), trpid.toString(), "", trpid, bearing, speed, servertm);

    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        tripid = bundle.get("tripid").toString();
        status = bundle.get("status").toString();
        vhid = bundle.get("vhid").toString();
        studid = bundle.get("stdid").toString();
        googleMapInit();

    }

    private void updateMap() {
        if (lstMytripdata.size() > 0) {
            model_tripdata mtrp = lstMytripdata.get(0);
            for (int i = 0; i <= driverOnMap.size() - 1; i++) {
                HashMap<String, Marker> l = driverOnMap.get(i);
                if (l.containsKey(mtrp.tripid)) {
                    Marker mrk = l.get(mtrp.tripid);
                    moveMarker(mrk, mtrp.loc[0].toString(), mtrp.loc[1].toString(), mtrp.bearing, mtrp.speed, mtrp.sertm);
                    return;
                }
            }

            addMarkerToMap(new LatLng(Double.parseDouble(mtrp.loc[0]), Double.parseDouble(mtrp.loc[1])), mtrp.tripid.toString(), "", mtrp.tripid, mtrp.bearing, mtrp.speed, mtrp.sertm);

        }
    }

    private void moveMarker(Marker mrk, String lat, String lon, String bearing, String speed, String Servertm) {

        LatLng latlon = new LatLng(Double.parseDouble(lon), Double.parseDouble(lat));
        mrk.setRotation(Float.parseFloat(bearing));
        LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
        MarkerAnimation.animateMarker(mrk, latlon, latLngInterpolator);
        if (isRecenter) {
            navigateToPoint(latlon, this.tilt, Float.parseFloat(bearing), this.zoom, true);
        }
        //showTimeSpeed(speed, Servertm);

    }


    private void getLastKnownLocation() {
        JsonObject json = new JsonObject();
        json.addProperty("ismob", "true");
        json.addProperty("vhids", vhid);
        Ion.with(this)
                .load(Global.urls.getlastknownloc.value)

                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<model_tripdata>>() {
                            }.getType();
                            //JsonElement k = result.get("data");
                            lstMytripdata = (List<model_tripdata>) gson.fromJson(result.get("data"), listType);
                            updateMap();
                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }

                    }
                });
    }


    private void getLastKnownLocation_New() {
        JsonParser jsonParser = new JsonParser();
        JsonObject json = new JsonObject();
        json.add("vhids", jsonParser.parse("[" + strVehicles + "]"));
        Ion.with(this)
                .load(Global.urls.getvahicleupdates.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        try {
                            if (result != null) Log.v("result", result.toString());
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<vts_vh_model>>() {
                            }.getType();
                            List mod = (List) gson.fromJson(result.get("data"), listType);
                            updateLastStatus(mod);
                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);
                        //SHP.set(context, SHP.ids.lastsynctime, common.dateandtime(context, dateformt));
                    }
                });
    }


    private void updateLastStatus(List<vts_vh_model> mod) {
        int size = mod.size();
        int selcpos = -1;
        //String zoomid = SHP.get(this, SHP.ids.selectedvh, "").toString();
        // SHP.set(this, SHP.ids.selectedvh, "");
        for (int i = 0; i < size; i++) {
            vts_vh_model vhm = mod.get(i);
            int k = vharr.indexOf(vhm.vhid);
            if (k > -1) {
                vts_vh_model a = vhs.get(k);
                Log.v("test- ere", k + " " + a.vno);
                a.acc = vhm.acc;
                a.gsmsig = vhm.gsmsig;
                a.btr = vhm.btr;
                a.actvt = vhm.actvt;
                a.btrst = vhm.btrst;
                a.sertm = vhm.sertm;
                a.gpstm = vhm.gpstm;
                a.speed = vhm.speed;
                a.alwspeed = vhm.alwspeed;
                a.lstspd = vhm.lstspd;
                a.lstspdtm = vhm.lstspdtm;
                a.oe = vhm.oe;
                a.d1 = vhm.d1;
                LatLng l = null;
                if (vhm.loc != null && vhm.loc.length > 0) {
                    a.lat = vhm.loc[1];
                    a.lon = vhm.loc[0];
                    a.islststsAvail = true;
                    a.loc = vhm.loc;
                    l = new LatLng(a.lat, a.lon);
                    a.bearing = vhm.bearing;
                } else {
                    l = new LatLng(0, 0);
                }

                Marker m = mMap.addMarker(new MarkerOptions().position(l).title(vhm.vno)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                        .anchor(0.5f, 0.5f));
                m.setTag(false);

                //a.polyline = showCurvedPolyline(new LatLng(a.toloc[0], a.toloc[1]), l, 0.3);

                a.vhmarker = m;

                //                ClusterMarkerItem offsetItem = new ClusterMarkerItem(l.latitude, l.longitude);//
                //       mClusterManager.addItem(offsetItem);
                //

            }
            if (defaultcheckd != null) {
                defaultcheckd.performClick();
            }
        }
        SocketClient();

    }


    private void Decoder(JSONObject data) {
        String evt = null;
        try {
            evt = data.getString("evt");
            switch (evt) {
                case "data": {
                    final JSONObject _ld = data.getJSONObject("data");
                    clnt_tripview.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateList(_ld);
                        }
                    });
                }
                break;
                case "regreq": {
                    if (vhs.size() > 0) {
                        mSocket.emit("reg_v", strVehicles.replace("\"", ""));
                    }
                    //getLastStatus(false);
                }
                break;
                case "registered": {
                    clnt_tripview.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(clnt_tripview.this, "Registered", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Marker> mrkers = new ArrayList<>();

    private void updateList(JSONObject _ld) {
        try {
            String vhid = _ld.getString("vhid");
            int i = vharr.indexOf(vhid);
            if (i == -1) return;
            vts_vh_model m = vhs.get(i);
            m.sertm = _ld.getString("sertm");
            switch (_ld.getString("actvt")) {
                case "hrtbt": {
                    m.acc = _ld.getString("acc");
                    m.btr = _ld.getString("btr");
                    m.gsmsig = _ld.getString("gsmsig");
                    m.btrst = _ld.getString("btrst");
                }
                break;
                case "loc": {
                    LatLng lastLoc = null;
                    if (m.lon != null && m.lon != 0) {
                        lastLoc = new LatLng(m.lat, m.lon);
                    }
                    try {
                        JSONArray ar = _ld.getJSONArray("loc");// lat long in array
                        m.lon = ar.getDouble(0);
                        m.lat = ar.getDouble(1);
                    } catch (JSONException e) {
                    }
                    m.bearing = _ld.getDouble("bearing");// vehicle bearing m.
                    //sat = _ld.getInt("sat"); // satlite counts
                    m.speed = _ld.getInt("speed");//nspeed of vehicle
                    m.gpstm = _ld.getString("gpstm"); // gps device time
                    if (_ld.getBoolean("isp")) {//is speed voilated flag
                        m.lstspdtm = _ld.getString("lstspdtm"); // last speed voilated time
                        m.lstspd = _ld.getInt("lstspd");//last voilated speed in integer
                    }
                    if (_ld.has("d1")) {
                        m.d1 = _ld.getInt("d1");
                    }
                    if (_ld.has("alwspeed")) {
                        m.alwspeed = _ld.getInt("alwspeed");//allow speed
                    } else {
                        m.alwspeed = 0;
                    }

                    Marker _m = m.vhmarker;
//                    if (i <= mrkers.size() - 1) {
//                        _m = mrkers.get(i);
//                    }
                    Double bearing = m.bearing;
                    if (lastLoc != null) {
                        LatLng currentLoc = new LatLng(m.lat, m.lon);
                        bearing = bearingBetweenLocations(lastLoc, currentLoc);
                    }
                    if (_m != null) {


                        moveMarker(vhid, _m, m.lat, m.lon, bearing, m);


                    }
                    ;
//                    if (FollowCamera.equals(vhid)) {
//                        updatePanel(m, "onchange");
//                    }
                }
                break;
                case "evt": {
                }
                break;
                case "login": {
                    //adapter.notifyItemChanged(i);
                }
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void boundtwo(LatLng l1, LatLng l2) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        boolean isicons = true;

        builder.include(l1);
        builder.include(l2);
        if (isicons) {            /**initialize the padding for map boundary*/
            int padding = 50;            /**create the bounds from latlngBuilder to set into map camera*/
            LatLngBounds bounds = builder.build();            /**create the camera with bounds and padding to set into map*/
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
    }


//    Calculate Distance:-
//    float distance;
//    Location locationA=new Location("A");
//            locationA.setLatitude(lat);
//            locationA.setLongitude(lng);
//
//    Location locationB = new Location("B");
//            locationB.setLatitude(lat);
//            locationB.setLongitude(lng);
//
//    distance = locationA.distanceTo(locationB)/1000;
//
//    LatLng From = new LatLng(lat,lng);
//    LatLng To = new LatLng(lat,lng);
//
//    Calculate Time:-
//    int speedIs1KmMinute = 100;
//    float estimatedDriveTimeInMinutes = distance / speedIs1KmMinute;
//               Toast.makeText(this,String.valueOf(distance+
//            "Km"),Toast.LENGTH_SHORT).show();
//            Toast.makeText(this,String.valueOf(estimatedDriveTimeInMinutes+" Time"),Toast.LENGTH_SHORT).show();

    private void moveMarker(String vhid, Marker m, Double lat, Double lon, Double bearing, vts_vh_model vtsm) {

        if (selctedChild != null && selctedChild.vehicle.equals(vtsm.vhid)) {
            boundtwo(selctedChild.child, new LatLng(vtsm.lat, vtsm.lon));
        }

        LatLng l = new LatLng(lat, lon);
        rotateMarker(m, bearing.floatValue());
//        if (vtsm.polyline != null) {
//            vtsm.polyline.remove();
//        }
//        vtsm.polyline = showCurvedPolyline(new LatLng(vtsm.toloc[0], vtsm.toloc[1]), l, 0.3);

        LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
        MarkerAnimation.animateMarker(m, l, latLngInterpolator);

//        if (FollowCamera.equals(vhid)) {
//            CameraPosition position1 = new CameraPosition.Builder().target(l).zoom(17).build();
//            changeCameraPosition(position1, true);
//        }
    }


    boolean isMarkerRotating = false;
    final Handler handler = new Handler();

    private void rotateMarker(final Marker marker, final float toRotation) {
        if (!(boolean) marker.getTag()) {
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 500;
            final Interpolator interpolator = new LinearInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    marker.setTag(true);
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    float rot = t * toRotation + (1 - t) * startRotation;
                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        handler.postDelayed(this, 16);
                    } else {
                        marker.setTag(false);
                    }
                }
            });
        }
    }

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;
        double dLon = (long2 - long1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return brng;
    }


    List<vts_vh_model> vhs;
    vts_vh_model vhmod;
    View defaultcheckd = null;
    private void getKidsOnTrip() {

        vhs = new ArrayList<>();

        JsonObject json = new JsonObject();
        json.addProperty("uid", Global.getUserID(this));
        json.addProperty("tripid", tripid);
        json.addProperty("flag", "kidsontrip_new");
        Ion.with(this)
                .load(Global.urls.getmykids.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            // JSONObject jsnobject = new JSONObject(jsond);


                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<MyKidsModel>>() {
                            }.getType();
                            List<MyKidsModel> lstmykidsd = (List<MyKidsModel>) gson.fromJson(result.get("data"), listType);
                            MyKidsModel _d;
                            if (lstmykidsd.size() > 0) {
                                _d = lstmykidsd.get(0);
                                vharr.add(_d.imei);
                                kid1.setVisibility(View.VISIBLE);
                                txtk1.setText(_d.Name);
                                if(studid.equals((_d.StudId+"")) ){
                                    defaultcheckd = kid1;
                                }
                                addvehiclestomodel(_d);
                                if (_d.studphoto != null)
                                    Glide.with(clnt_tripview.this).load(Global.IMAGES_URL + "/" + _d.studphoto).dontAnimate().placeholder(R.drawable.ic_mykids).into(imgk1);
                                addMarker(kid1, _d);

                            }
                            if (lstmykidsd.size() > 1) {
                                _d = lstmykidsd.get(1);
                                kid2.setVisibility(View.VISIBLE);
                                if(studid.equals((_d.StudId+"")) ){
                                    defaultcheckd = kid2;
                                }
                                if (vharr.indexOf(_d.imei) == -1) {
                                    vharr.add(_d.imei);
                                    addvehiclestomodel(_d);
                                }
                                txtk2.setText(_d.Name);
                                if (_d.studphoto != null)
                                    Glide.with(clnt_tripview.this).load(Global.IMAGES_URL + "/" + _d.studphoto).dontAnimate().placeholder(R.drawable.ic_mykids).into(imgk2);
                                addMarker(kid2, _d);
                            }
                            if (lstmykidsd.size() > 2) {
                                _d = lstmykidsd.get(2);
                                if(studid.equals((_d.StudId+"")) ){
                                    defaultcheckd = kid3;
                                }
                                kid3.setVisibility(View.VISIBLE);
                                txtk3.setText(_d.Name);
                                if (vharr.indexOf(_d.imei) == -1) {
                                    vharr.add(_d.imei);
                                    addvehiclestomodel(_d);
                                }
                                addMarker(kid3, _d);
                                if (_d.studphoto != null)
                                    Glide.with(clnt_tripview.this).load(Global.IMAGES_URL + "/" + _d.studphoto).dontAnimate().placeholder(R.drawable.ic_mykids).into(imgk3);
                            }
                            if (lstmykidsd.size() > 3) {
                                _d = lstmykidsd.get(3);
                                kid4.setVisibility(View.VISIBLE);
                                if(studid.equals((_d.StudId+"")) ){
                                    defaultcheckd = kid4;
                                }
                                if (vharr.indexOf(_d.imei) == -1) {
                                    vharr.add(_d.imei);
                                    addvehiclestomodel(_d);
                                }
                                txtk4.setText(_d.Name);
                                addMarker(kid4, _d);
                                if (_d.studphoto != null)
                                    Glide.with(clnt_tripview.this).load(Global.IMAGES_URL + "/" + _d.studphoto).dontAnimate().placeholder(R.drawable.ic_mykids).into(imgk4);
                            }
                            if (lstmykidsd.size() > 4) {
                                _d = lstmykidsd.get(4);
                                if(studid.equals((_d.StudId+"")) ){
                                    defaultcheckd = kid5;
                                }
                                if (vharr.indexOf(_d.imei) == -1) {
                                    vharr.add(_d.imei);
                                    addvehiclestomodel(_d);
                                }
                                kid5.setVisibility(View.VISIBLE);
                                txtk5.setText(_d.Name);
                                addMarker(kid5, _d);
                                if (_d.studphoto != null)
                                    Glide.with(clnt_tripview.this).load(Global.IMAGES_URL + "/" + _d.studphoto).dontAnimate().placeholder(R.drawable.ic_mykids).into(imgk5);
                            }
                            strVehicles = TextUtils.join("\",\"", vharr);
                            strVehicles = "\"" + strVehicles + "\"";
                            getLastKnownLocation_New();

                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                    }
                });


    }


    private void addvehiclestomodel(MyKidsModel _d) {
        vhmod = new vts_vh_model();
        vhmod.vhid = _d.imei;
        vhmod.vno = _d.vhno;
        vhmod.toloc = new Double[]{
                _d.loc.lat, _d.loc.lon
        };
        vhs.add(vhmod);
    }

    Bitmap bmp;

    private void addMarker(final View kid, final MyKidsModel child) {
        final LatLng latLng = new LatLng(child.loc.lat, child.loc.lon);
        kid.setTag(child);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                try {
                    if (child.studphoto != null) {
                        url = new URL(Global.IMAGES_URL + "/" + child.studphoto);
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } else {
                        bmp = drawableToBitmap(getResources().getDrawable(R.drawable.ic_mykids));
                    }


                } catch (Exception e) {
                    bmp = drawableToBitmap(getResources().getDrawable(R.drawable.ic_mykids));
                    e.printStackTrace();
                }

                bmp = Bitmap.createScaledBitmap(bmp, 60, 60, false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MarkerItem i = new MarkerItem(new MarkerOptions().position(latLng)
                                .title(child.Name)
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp)));

                        mClusterManager.addItem(i);
                        mClusterManager.cluster();
                        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                    }
                });
            }
        });
        thread.start();


    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    //add font
    private void addCustomFont() {
        tf = Typeface.createFromAsset(getAssets(), "fonts/digital.ttf");
    }


    private Polyline showCurvedPolyline(LatLng p1, LatLng p2, double k) {
        //Calculate distance and heading between two points
        double d = SphericalUtil.computeDistanceBetween(p1, p2);
        double h = SphericalUtil.computeHeading(p1, p2);

        //Midpoint position
        LatLng p = SphericalUtil.computeOffset(p1, d * 0.5, h);

        //Apply some mathematics to calculate position of the circle center
        double x = (1 - k * k) * d * 0.5 / (2 * k);
        double r = (1 + k * k) * d * 0.5 / (2 * k);

        LatLng c = SphericalUtil.computeOffset(p, x, h + 90.0);

        //Polyline options
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(20));

        //Calculate heading between circle center and two points
        double h1 = SphericalUtil.computeHeading(c, p1);
        double h2 = SphericalUtil.computeHeading(c, p2);

        //Calculate positions of points on circle border and add them to polyline options
        int numpoints = 100;
        double step = (h2 - h1) / numpoints;

        for (int i = 0; i < numpoints; i++) {
            LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
            options.add(pi);
        }

        //Draw polyline
        return mMap.addPolyline(options.width(5).color(R.color.polyline).geodesic(false).pattern(pattern));
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off("msgd", onNewMessage);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }
}
