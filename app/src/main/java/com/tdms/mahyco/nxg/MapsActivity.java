package com.tdms.mahyco.nxg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arsy.maps_library.MapRadar;
import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener, AdapterView.OnItemSelectedListener {
    boolean isCanceled;
    MapUserAdapter adapter;
    RecyclerView mRecyclerView;
    public RecyclerView recyclerList;
    Spinner ddTrailtype, ddlCroptype;
    String selectedCrop;
    String selectedTrialType;
    private StaggeredGridLayoutManager mLayoutManager;
    TextView text;
    private GoogleMap mMap;
    private LatLng latLng = new LatLng(28.7938709, 77.1427639);
    private Context context;
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    public LocationTracker locationTrackObj;
    private MapRipple mapRipple;
    private MapRadar mapRadar;
    private Button startstoprippleBtn;
    private final int ANIMATION_TYPE_RIPPLE = 0;
    private final int ANIMATION_TYPE_RADAR = 1;
    float distanceInMeters;
    //    private int whichAnimationWasRunning = ANIMATION_TYPE_RIPPLE;
    Location location;
    Marker marker_initial;
    Marker marker_last;
    Marker marker_mid;
    databaseHelper databaseHelper1;
    List<MapLocationModel> mList = new ArrayList<>();
    Map<Marker, String> markers = new HashMap<>();
    CoordinatorLayout coordinateLayoutData;
    TextView txt_noRecords;
    int pos;
    SupportMapFragment mapFragment;
    MapLocationModel mapLocationModel12;
    ImageView imgArrowBottom;

    BottomSheetBehavior sheetBehavior;
    LinearLayout bottom_sheet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = this;
        ddTrailtype = (Spinner) findViewById(R.id.ddTrailtype);
        ddlCroptype = (Spinner) findViewById(R.id.ddlCroptype);

        databaseHelper1 = new databaseHelper(context);
       // Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);



        // Loading spinner data from database

        ddlCroptype.setOnItemSelectedListener(this);

        imgArrowBottom = (ImageView) findViewById(R.id.imgArrowBottom);
        bottom_sheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        mRecyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager lllm = new LinearLayoutManager(MapsActivity.this);
        lllm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lllm);
        coordinateLayoutData = (CoordinatorLayout) findViewById(R.id.coordinateLayoutData);
        txt_noRecords = (TextView) findViewById(R.id.txt_noRecords);
        text = findViewById(R.id.text);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setTitle("Map");

        loadSpinnerData();
        loadTrailType();
        CommonExecution.setBlinkingTextview(imgArrowBottom,1000,10);

        bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });


        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        imgArrowBottom.setRotation(90);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        imgArrowBottom.setRotation(270);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        ddTrailtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectedTrialType = ddTrailtype.getSelectedItem().toString();
                    if (!selectedTrialType.equals(getString(R.string.SelectTrialType)) &&
                            !selectedCrop.equals(getString(R.string.selectcrop))) {
                        mList.clear();
                        createList(" AND (TrailCodeData.CROP='" + selectedCrop +
                                "' AND TrailCodeData.Trail_Type='" + selectedTrialType + "') ");

                    } else if (!selectedTrialType.equals(getString(R.string.SelectTrialType))) {
                        mList.clear();
                        createList(" AND (TrailCodeData.Trail_Type='" + selectedTrialType + "') ");

                    } else if (!selectedCrop.equals(getString(R.string.selectcrop))) {
                        mList.clear();
                        createList(" AND (TrailCodeData.CROP='" + selectedCrop + "') ");

                    } else {
                        mList.clear();
                        createList("");
                    }
                    Log.d("rohitt", "onItemSelected: " + mList.size() + ":::" + selectedCrop + "::::" + selectedTrialType);
                } catch (Exception e) {
                    Log.d("Msg",e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ddlCroptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {

                    selectedCrop = ddlCroptype.getSelectedItem().toString();
                    if (!selectedTrialType.equals(getString(R.string.SelectTrialType)) &&
                            !selectedCrop.equals(getString(R.string.selectcrop))) {
                        mList.clear();
                        createList("AND (TrailCodeData.CROP='" + selectedCrop +
                                "' AND TrailCodeData.Trail_Type='" + selectedTrialType + "') ");

                    } else if (!selectedCrop.equals(getString(R.string.selectcrop))) {
                        mList.clear();
                        createList("AND (TrailCodeData.CROP='" + selectedCrop + "') ");

                    } else if (!selectedTrialType.equals(getString(R.string.SelectTrialType))) {
                        mList.clear();
                        createList("AND (TrailCodeData.Trail_Type='" + selectedTrialType + "') ");


                    } else {
                        mList.clear();
                        createList("");
                    }
                    Log.d("rohitt", "onItemSelected: " + mList.size() + ":::" + selectedCrop + "::::" + selectedTrialType);
                } catch (Exception e) {
                    Log.d("Msg",e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locationTrackObj = new LocationTracker(context);
        databaseHelper1 = new databaseHelper(context);
        if (!locationTrackObj.canGetLocation()) {
            locationTrackObj.showSettingsAlert();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
        }

//        text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LatLng latLng;
//                latLng = new LatLng(22.719568, 75.857727);
//                changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
//
//                        target(latLng)
//                        .
//
//                                zoom(50.5f)
//                        .
//
//                                bearing(300)
//                        .
//
//                                tilt(50)
//                        .
//
//                                build()));
//
//            }
//        });


    }


//    public void createList(String whereCondition) {
//
//        String searchQuery = "select tagdata1.coordinates,tagdata1.TRIL_CODE,TrailCodeData.CROP,TrailCodeData.Trail_Type,FarmerInfodata.Fname," +
//                "FarmerInfodata.Fvillage FROM tagdata1 " +
//                "LEFT JOIN TrailCodeData ON tagdata1.TRIL_CODE= TrailCodeData.TRIL_CODE " +
//                "LEFT JOIN FarmerInfodata ON tagdata1.TRIL_CODE= FarmerInfodata.TRIL_CODE " +
//                "where (tagdata1.coordinates IS NOT NULL AND tagdata1.coordinates !='' " + whereCondition + ") GROUP BY " +
//                "tagdata1.TRIL_CODE ";
//        Log.d("searchQuery", "createList: " + searchQuery);
//        Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
//
//
//        int count = cursor.getCount();
//
//        if (count > 0) {
//
//
//            JSONArray jsonArray = new JSONArray();
//            try {
//                jsonArray = databaseHelper1.getResultsFromDB(searchQuery);
//                for (int i = 0; i < jsonArray.length(); i++) {
//
//
//                    MapLocationModel mapLocationModel = new MapLocationModel();
//                    String[] coordinates = jsonArray.getJSONObject(i).getString("coordinates").split(",");
//                    String lat = coordinates[0].split("-")[0];
//                    String lng = coordinates[0].split("-")[1];
//                    mapLocationModel.setLattitude(lat);
//                    mapLocationModel.setLongitude(lng);
//                    mapLocationModel.setTrialCode(jsonArray.getJSONObject(i).getString("TRIL_CODE"));
//                    mapLocationModel.setTrialType(jsonArray.getJSONObject(i).getString("Trail_Type"));
//                    mapLocationModel.setCrop(jsonArray.getJSONObject(i).getString("CROP"));
//                    mapLocationModel.setName(jsonArray.getJSONObject(i).getString("Fname"));
//                    mapLocationModel.setVillage(jsonArray.getJSONObject(i).getString("Fvillage"));
//
//
//                    float dist = addMarkersInRange(location.getLatitude(), location.getLongitude(),
//                            Double.parseDouble(mapLocationModel.getLattitude()),
//                            Double.parseDouble(mapLocationModel.getLongitude()));
//
//
//                    if (dist <= Constants.KEY_METER_RANGE) {
//                        double d = distanceInMeters / 1000;
//
//                        String distanceInKM = String.valueOf(Math.round(d));
//                        mapLocationModel.setDistance(distanceInKM);
//                        mList.add(mapLocationModel);
//
//
//                    }
//
//
//                }
//                Log.d("rohitt", "createList: " + mList);
//                mRecyclerView.setVisibility(View.VISIBLE);
//                txt_noRecords.setVisibility(View.GONE);
////                mMap.clear();
////                initializeMap(mMap);
//                // mapFragment.getMapAsync(this);
//                mMap.clear();
//                renderMarkers();
//                addData(mList);
//            } catch (Exception e) {
//                coordinateLayoutData.setVisibility(View.GONE);
//                txt_noRecords.setVisibility(View.VISIBLE);
//                Log.d("Msg",e.getMessage());
//            }
//
//        } else {
//            mRecyclerView.setVisibility(View.GONE);
//            txt_noRecords.setVisibility(View.VISIBLE);
//
//        }
//
//    }
public void createList(String whereCondition) {

    String searchQuery = "select tagdata1.coordinates,tagdata1.TRIL_CODE,TrailCodeData.CROP,TrailCodeData.Trail_Type," +
            "TrailCodeData.T_SESION,TrailCodeData.T_YEAR,TrailCodeData.ENTRY,TrailCodeData.REPLECATION," +
            "TrailCodeData.Location,TrailCodeData.nursery,TrailCodeData.segment," +
            "FarmerInfodata.Fname," +
            "FarmerInfodata.Fmobile,FarmerInfodata.Fsowingdate,FarmerInfodata.FArea," +
            "FarmerInfodata.Fvillage FROM tagdata1 " +
            "LEFT JOIN TrailCodeData ON tagdata1.TRIL_CODE= TrailCodeData.TRIL_CODE " +
            "LEFT JOIN FarmerInfodata ON tagdata1.TRIL_CODE= FarmerInfodata.TRIL_CODE " +
            "where (tagdata1.coordinates IS NOT NULL AND tagdata1.coordinates !='' " + whereCondition + ") GROUP BY " +
            "tagdata1.TRIL_CODE ";
    Log.d("searchQuery", "createList: " + searchQuery);
    Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);


    int count = cursor.getCount();

    if (count > 0) {


        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = databaseHelper1.getResultsFromDB(searchQuery);
            for (int i = 0; i < jsonArray.length(); i++) {


                MapLocationModel mapLocationModel = new MapLocationModel();
                String[] coordinates = jsonArray.getJSONObject(i).getString("coordinates").split(",");
                String lat = coordinates[0].split("~")[0];
                String lng = coordinates[0].split("~")[1];
                mapLocationModel.setLattitude(lat);
                mapLocationModel.setLongitude(lng);
                mapLocationModel.setTrialCode(jsonArray.getJSONObject(i).getString("TRIL_CODE"));
                mapLocationModel.setTrialType(jsonArray.getJSONObject(i).getString("Trail_Type"));
                mapLocationModel.setCrop(jsonArray.getJSONObject(i).getString("CROP"));
                mapLocationModel.setName(jsonArray.getJSONObject(i).getString("Fname"));
                mapLocationModel.setVillage(jsonArray.getJSONObject(i).getString("Fvillage"));
                mapLocationModel.setFmobile(jsonArray.getJSONObject(i).getString("Fmobile"));
                mapLocationModel.setFsowingdate(jsonArray.getJSONObject(i).getString("Fsowingdate"));
                mapLocationModel.setFarea(jsonArray.getJSONObject(i).getString("FArea"));
                mapLocationModel.setSesion(jsonArray.getJSONObject(i).getString("T_SESION"));
                mapLocationModel.setYear(jsonArray.getJSONObject(i).getString("T_YEAR"));
                mapLocationModel.setEntry(jsonArray.getJSONObject(i).getString("ENTRY"));
                mapLocationModel.setReplecation(jsonArray.getJSONObject(i).getString("REPLECATION"));
                mapLocationModel.setLocation(jsonArray.getJSONObject(i).getString("Location"));
                mapLocationModel.setNursery(jsonArray.getJSONObject(i).getString("nursery"));
                mapLocationModel.setSegment(jsonArray.getJSONObject(i).getString("segment"));


                float dist = addMarkersInRange(location.getLatitude(), location.getLongitude(),
                        Double.parseDouble(mapLocationModel.getLattitude()),
                        Double.parseDouble(mapLocationModel.getLongitude()));


                if (dist <= Constants.KEY_METER_RANGE) {
                    double d = distanceInMeters / 1000;

                    String distanceInKM = String.valueOf(Math.round(d));
                    mapLocationModel.setDistance(distanceInKM);
                    mList.add(mapLocationModel);


                }


            }
            Log.d("rohitt", "createList: " + mList);
            mRecyclerView.setVisibility(View.VISIBLE);
            txt_noRecords.setVisibility(View.GONE);
//                mMap.clear();
//                initializeMap(mMap);
            // mapFragment.getMapAsync(this);
            mMap.clear();
            renderMarkers();
            addData(mList);
        } catch (Exception e) {
            coordinateLayoutData.setVisibility(View.GONE);
            txt_noRecords.setVisibility(View.VISIBLE);
            Log.d("Msg",e.getMessage());
        }

    } else {
        mRecyclerView.setVisibility(View.GONE);
        txt_noRecords.setVisibility(View.VISIBLE);

    }

}



    private void loadTrailType() {
        // database handler
        // Spinner Drop down elements
        List<String> lables = databaseHelper1.getUniqueTrialType();
        lables.add(0, getString(R.string.SelectTrialType));
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddTrailtype.setAdapter(dataAdapter1);
    }

    private void loadSpinnerData() {
        // database handler
        // Spinner Drop down elements
        List<String> lables = databaseHelper1.getCrop();
        lables.add(0, getString(R.string.selectcrop));
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlCroptype.setAdapter(dataAdapter);
    }


    public void addData(List<MapLocationModel> mList) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //if (adapter == null) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MapUserAdapter(this, mList, mMap, latLng));


//        } else {
//            adapter.notifyDataSetChanged();
//        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeMap(mMap);
                } else {
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                initializeMap(mMap);
            }
        } else {
            initializeMap(mMap);
        }
    }


    private void initializeMap(GoogleMap mMap) {
        if (mMap != null) {

            mMap.setOnMarkerClickListener(this);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
            mMap.setMyLocationEnabled(true);
            location = mMap.getMyLocation();
            if (location == null)
                location = locationTrackObj.getLocation();
            try {


                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(location.getLatitude(),
                        location.getLongitude()), 8));

                if (location != null)
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                else {
                    latLng = new LatLng(0.0, 0.0);
                }

                createList("");
                mapRipple = new MapRipple(mMap, latLng, context);

                mapRipple.withNumberOfRipples(3);
                mapRipple.withFillColor(Color.parseColor("#FFA3D2E4"));
                mapRipple.withStrokeColor(Color.BLACK);
                mapRipple.withStrokewidth(0);      // 10dp
                mapRipple.withDistance(30000);      // 2000 metres radius
                mapRipple.withRippleDuration(12000);    //12000ms
                mapRipple.withTransparency(0.5f);
                mapRipple.startRippleMapAnimation();
                //  renderMarkers();
            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
            }
        }
    }

    public void renderMarkers() {
        try {


            setStartStopAnimation(false);
            LatLng latLng1 = null;
            LatLng mLatLng = null;
            if (mList != null && mList.size() > 0) {
             for (int i = mList.size()-1; i >= 0 ; i--) {
               //     for (int i =0 ; i < mList.size() ; i++) {

//                    (int i = alist.size() - 1; i >= 0; i--)

                    if (mList.get(i).getLattitude() != null && mList.get(i).getLongitude() != null &&
                            !mList.get(i).getLattitude().equals("") && !mList.get(i).getLongitude().equals("")
                    ) {

                        // Instantiating the class MarkerOptions to plot marker on the map
                        MarkerOptions markerOptions = new MarkerOptions();
                        latLng1 = new LatLng(Double.parseDouble(mList.get(i).getLattitude()),
                                Double.parseDouble(mList.get(i).getLongitude()));
                        // Setting latitude and longitude of the marker position
                        markerOptions.position(latLng1);
                        // Setting titile of the infowindow of the marker

                        marker_last = mMap.addMarker(new MarkerOptions()
                                .position(latLng1)
                                // .snippet("End Point")

                                .title(mList.get(i).getName()));
                       // marker_last.setPosition(latLng1);
                        marker_last.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        Marker marker = marker_last;
                        marker_last.setTag(mList.get(i).getTrialCode());
                        markers.put(marker, markerOptions.getTitle());
                        marker_last.showInfoWindow();
                        // }


                    }

                }
            }
        } catch (Exception e) {
            Log.d("Msg",e.getMessage());
        }

    }

    private String formatNumber(double distance) {
        String unit = " m";
        if (distance < 1) {
            distance *= 1000;
            unit = " mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = " km";
        }


        return String.format("%.2f%s", distance, unit);
    }

    public float addMarkersInRange(double lat, double lng, double endLat, double endLng) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(lat);
        startPoint.setLongitude(lng);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(endLat);
        endPoint.setLongitude(endLng);


        distanceInMeters = startPoint.distanceTo(endPoint);

        return distanceInMeters;

        // return distanceInMeters / 1000;


    }

    public boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }

            return false;
        } else {
            return true;
        }
    }

    public int positionTrail(String code) {


        int j = 0;
        if (mList != null && mList.size() > 0) {


            for (j = 0; j < mList.size(); j++) {

                if (code.equals(mList.get(j).getTrialCode())) {


                    pos = j;
                    return j;


                }
            }

        }

        return j;

    }


    public void setStartStopAnimation(boolean setStart) {
        if (setStart) {
            mapRipple.startRippleMapAnimation();
        } else {
            //  if (mapRipple.isAnimationRunning())
            mapRipple.stopRippleMapAnimation();

        }

    }

    public void advancedRipple(View view) {
        mapRadar.stopRadarAnimation();
        mapRipple.stopRippleMapAnimation();
        mapRipple.withNumberOfRipples(3);
        mapRipple.withFillColor(Color.parseColor("#FFA3D2E4"));
        mapRipple.withStrokewidth(0);      //0dp
        mapRipple.startRippleMapAnimation();
        startstoprippleBtn.setText("Stop Animation");
        //whichAnimationWasRunning = ANIMATION_TYPE_RIPPLE;
    }

    public void radarAnimation(View view) {
        mapRipple.stopRippleMapAnimation();
        mapRadar.startRadarAnimation();
        startstoprippleBtn.setText("Stop Animation");
        // whichAnimationWasRunning = ANIMATION_TYPE_RADAR;
    }

    public void simpleRipple(View view) {
        mapRadar.stopRadarAnimation();
        mapRipple.stopRippleMapAnimation();
        mapRipple.withNumberOfRipples(1);
        mapRipple.withFillColor(Color.parseColor("#00000000"));
        mapRipple.withStrokeColor(Color.BLACK);
        mapRipple.withStrokewidth(10);      // 10dp
        mapRipple.startRippleMapAnimation();
        startstoprippleBtn.setText("Stop Animation");
        //  whichAnimationWasRunning = ANIMATION_TYPE_RIPPLE;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mapRipple.isAnimationRunning()) {
                mapRipple.stopRippleMapAnimation();
            }
            locationTrackObj.stopUsingGPS();
        } catch (Exception e) {
            Log.d("Msg",e.getMessage());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        loadTrailType();
//        String label = ddlCroptype.getSelectedItem().toString();
//        String type=ddTrailtype.getSelectedItem().toString();
//        ArrayList<TrailReportModel> mlist = databaseHelper1.getTrailDetailstag(label);
//        trial_listAdaptor = new trial_ListAdaptor(mlist, this);
//        recyclerList.setAdapter(trial_listAdaptor);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class LocationTracker implements LocationListener {

        private final Context mContext;

        // flag for GPS status
        private boolean isGPSEnabled = false;

        // flag for network status
        private boolean isNetworkEnabled = false;

        // flag for GPS status
        private boolean canGetLocation = false;

        private Location location; // location
        private double latitude; // latitude
        private double longitude; // longitude

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000; // 1 sec

        private final String TAG = "LocationTracker";
        // Declaring a Location Manager
        protected LocationManager locationManager;

        public LocationTracker(Context context) {
            this.mContext = context;
            getLocation();
        }

        @SuppressLint("MissingPermission")
        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(Context.LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                    this.canGetLocation = false;
                } else {
                    this.canGetLocation = true;
                    // First get location from Network Provider
                    if (isNetworkEnabled) {

                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
            }

            return location;
        }

        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app
         */
        public void stopUsingGPS() {
            if (locationManager != null) {
                locationManager.removeUpdates(LocationTracker.this);
            }
        }

        /**
         * Function to get latitude
         */
        public double getLatitude() {
            if (location != null) {
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         */
        public double getLongitude() {
            if (location != null) {
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/wifi enabled
         *
         * @return boolean
         */
        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        /**
         * Function to show settings alert dialog
         * On pressing Settings button will lauch Settings Options
         */
        public void showSettingsAlert() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS Settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Click on setting to enable and get location, please start app again after turning on GPS.");
            alertDialog.setCancelable(false);

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });


            // Showing Alert Message
            alertDialog.show();
        }

        Random rand = new Random();

        @Override
        public void onLocationChanged(Location location) {
            //            mapRipple.withNumberOfRipples(3);
            this.location = location;
//            Toast.makeText(context, "  " + location.getLatitude() + ",  " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            if (mapRipple != null && mapRipple.isAnimationRunning())
                mapRipple.withLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
//            if (mapRadar.isAnimationRunning())
//                mapRadar.withLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            location = getLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

    private void bounceMarkerOnclick(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().

                target(marker.getPosition())
                .

                        zoom(50.5f)
                .

                        bearing(300)
                .

                        tilt(50)
                .

                        build()));

        bounceMarkerOnclick(marker);
        String code = String.valueOf(marker.getTag());

        pos = positionTrail(code);
        MapLocationModel mapLocationModel = mList.get(pos);
        List<MapLocationModel> temp = new ArrayList<>();
        temp.clear();
        temp.add(mapLocationModel);
        mList.remove(pos);
        temp.addAll(mList);
        mList.clear();
        mList.addAll(temp);
        addData(mList);

        return false;
    }

    public void moveOnMarker(LatLng latLng) {
        // changeCamera(CameraUpdateFactory.newLatLngZoom(latLng,10.5f));
        Log.d("rohitt", "moveOnMarker: " + latLng);
        // CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(new LatLng(22.788, 75.7888), 10);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(yourLocation);

        //  build()));


    }

    @Override
    public void onCameraIdle() {
        isCanceled = false;  // Set to *not* clear the map when dragging starts again.


    }

    @Override
    public void onCameraMoveCanceled() {
        isCanceled = true;  // Set to clear the map when dragging starts again.

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {
        if (!isCanceled) {
            mMap.clear();
        }

    }

    private void changeCamera(CameraUpdate update) {
        changeCamera(update, null);
    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        //  if (mAnimateToggle.isChecked()) {
        // if (mCustomDurationToggle.isChecked()) {
        int duration = 1500;
        if (mMap != null)
            // The duration must be strictly positive so we make it at least 1.
            mMap.animateCamera(update, Math.max(duration, 1), callback);
        // } else {mMap.animateCamera(update, callback);}
        //  } else {
        //      mMap.moveCamera(update);
        // }
    }

}
