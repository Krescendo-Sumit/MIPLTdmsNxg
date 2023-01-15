package com.tdms.mahyco.nxg;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class newAreaTag extends AppCompatActivity implements LocationListener,
        OnMapReadyCallback {

    Button getLocationBtn, deleteLocationBtn, BtnAreaSave;
    TextView locationText, locationArea, lblarea, lbltable;
    public WebView wb1;
    databaseHelper databaseHelper1;
    public String userCode;
    public String flag;
    public TextView TRIL_CODE;
    private double longitude;
    private double latitude;
    private GoogleMap mMap;
    private ArrayList<LatLng> arrayPoints = null;
    private Criteria criteria;
    private String provider;
    public Messageclass msclass;
    LocationManager locationManager;
    String tagType = "";

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide(); /*Commented on 19th July 2021*/
        setContentView(R.layout.activity_new_area_tag);

        /*Added on 19th July 2021*/
        getSupportActionBar().setTitle("Tag Area");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getLocationBtn = (Button) findViewById(R.id.getLocationBtn);
        deleteLocationBtn = (Button) findViewById(R.id.deleteLocationBtn);
        BtnAreaSave = (Button) findViewById(R.id.BtnAreaSave);
        locationText = (TextView) findViewById(R.id.locationText);
        locationArea = (TextView) findViewById(R.id.locationArea);
        lbltable = (TextView) findViewById(R.id.lbltable);
        wb1 = (WebView) findViewById(R.id.wb1);
        databaseHelper1 = new databaseHelper(this);
        TRIL_CODE = (TextView) findViewById(R.id.txtTrialCode);
        lblarea = (TextView) findViewById(R.id.lblarea);
        arrayPoints = new ArrayList<LatLng>();

        msclass = new Messageclass(this);
        Intent i = getIntent();
        String name = i.getStringExtra("Trail_code");
        tagType = i.getStringExtra(AppConstant.TAG_TYPE);
        TRIL_CODE.setText(name);

        Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount() == 0) {
            msclass.showMessage("No Data Available... ");
        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    //userCode=data.getString((data.getColumnIndex("user_code")));
                    String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                    userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("userCode", "userCode" + userCode);
                } while (data.moveToNext());

            }
            data.close();
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        Criteria criteria = new Criteria();
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);


        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        getLocation();

        //if (tagType.equalsIgnoreCase(AppConstant.FULL_TAG)) { /*TODO check with testing*/
        createmap();
        //}

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                getLocationNetwork(); /*Added 1st April 2021 TODO Testing for VIVO Phone issue*/
                createmap();
                try {
                    if (locationText.length() == 0) {
                        getLocationNetwork();
                        Toast.makeText(newAreaTag.this, "Please check GPS Co-Ordinate", Toast.LENGTH_SHORT).show();
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String entrydate = sdf.format(new Date());
                        String userCodeEncrypt = EncryptDecryptManager.encryptStringData(userCode);
                        Log.d("TAG_DATA", "user code : " + userCode);
                        Log.d("TAG_DATA", "Encrypted user code : " + userCodeEncrypt);
                        // boolean fl = databaseHelper1.insertagdata(userCode.toString(), locationText.getText().toString(), locationArea.getText().toString(), entrydate, TRIL_CODE.getText().toString(),tagType);
                        boolean fl = databaseHelper1.insertagdata(userCodeEncrypt, locationText.getText().toString(), locationArea.getText().toString(), entrydate, TRIL_CODE.getText().toString(), tagType);
                        createmap();
                        Toast.makeText(newAreaTag.this, "Save Input data successfully", Toast.LENGTH_SHORT).show();
                        createmap();

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                } catch (Exception ex) {
                    Toast.makeText(newAreaTag.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        deleteLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //databaseHelper1.deleledata("tagdata","");
                String deleteQuery = "delete from tagdata where flag='T' and TRIL_CODE='" + TRIL_CODE.getText().toString() + "' ";
                databaseHelper1.deleteQuery(deleteQuery);

                String searchQuery1 = "update TrailCodeData set Tagged = '' where TRIL_CODE='" + TRIL_CODE.getText().toString() + "' ";
                databaseHelper1.runQuery(searchQuery1);

                Toast.makeText(newAreaTag.this, "All Tag Data Clear", Toast.LENGTH_LONG).show();

                locationText.setText("");
                locationArea.setText("");
                lblarea.setText("");
                getLocation();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        /*int count = databaseHelper1.getTagDataCount(TRIL_CODE.getText().toString(), tagType);
        Log.d("TAG_DATA", "TAGTYPE Regular TAG_DATA COUNT : " + count + "tagType = " + tagType);

        int count1 = databaseHelper1.getTagDataCount(TRIL_CODE.getText().toString(), AppConstant.NURSERY_TAG);
        Log.d("TAG_DATA", "TAGTYPE NURSERY_TAG TAG_DATA COUNT : " + count1);

        int count2 = databaseHelper1.getTagDataCount(TRIL_CODE.getText().toString(), AppConstant.FULL_TAG);
        Log.d("TAG_DATA", "TAGTYPE NURSERY_TAG TAG_DATA COUNT : " + count2);*/

        BtnAreaSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count = databaseHelper1.getTagDataCount(TRIL_CODE.getText().toString(), tagType);
                Log.d("TAG_COUNT", "BtnAreaSave count : " + count);
                if (count >= 5) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String entrydate = sdf.format(new Date());

                    String userCodeEncrypt = EncryptDecryptManager.encryptStringData(userCode);
                    Log.d("TAG_DATA", "user code : " + userCode);
                    Log.d("TAG_DATA", "Encrypted user code : " + userCodeEncrypt);

                    String searchQuery = "update FarmerInfodata set FArea = '" + lblarea.getText().toString() + "' where TRIL_CODE='" + TRIL_CODE.getText().toString() + "' ";
                    databaseHelper1.runQuery(searchQuery);

                    String searchQuery1 = "update TrailCodeData set Tagged = 'T' where TRIL_CODE='" + TRIL_CODE.getText().toString() + "' ";
                    databaseHelper1.runQuery(searchQuery1);

                    String cordinates = lbltable.getText().toString();
                    cordinates = cordinates.replaceAll("\n", "");
                    //boolean fl = databaseHelper1.insertagdata1(userCode.toString(), cordinates, locationArea.getText().toString(), entrydate, TRIL_CODE.getText().toString(),tagType);
                    boolean fl = databaseHelper1.insertagdata1(userCodeEncrypt, cordinates, locationArea.getText().toString(), entrydate, TRIL_CODE.getText().toString(), tagType);
                    Toast.makeText(newAreaTag.this, "Tag Area Save Successfully", Toast.LENGTH_LONG).show();

                    /*Added on 19th July 2021*/
                    finish();

                /*if(tagType.equals(AppConstant.NURSERY_TAG)){
                    databaseHelper1.updateTagType(TRIL_CODE.getText().toString());
                }*/ /*NOT Needed as list sorted locally*/

                    /*TODO change to Trial Screen*/
                    /*Intent intent = new Intent(newAreaTag.this, home.class);
                    intent.putExtra(AppConstant.FROM_INTENT,AppConstant.TAG_AREA);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);*/
                    //finish();
                } else {
                    Toast.makeText(newAreaTag.this, "Minimum 5 Tag Area required", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            //locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

        } catch (SecurityException e) {
            Log.d("Msg", e.getMessage());
        }
    }

    void getLocationNetwork() {
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Log.d("Msg", e.getMessage());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
      //Old Code Start
        //  locationText.setText(+location.getLatitude() + "-" + location.getLongitude());
       //  == Oldecode End
        //New code Start
        locationText.setText(+location.getLatitude() + "~" + location.getLongitude());
      // newCode End
        // Toast.makeText(newAreaTag.this, "before location change", Toast.LENGTH_LONG).show();
        getLocation();
        //Toast.makeText(newAreaTag.this, "Location Changed", Toast.LENGTH_LONG).show();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationArea.setText("" + addresses.get(0).getAddressLine(0) + "");
        } catch (Exception e) {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(newAreaTag.this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void createmap() {
        try {
            arrayPoints.clear();
            JSONObject object = new JSONObject();
            try {
                object.put("Table1", getResults());
                Toast.makeText(newAreaTag.this, ""+getResults(), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Log.d("Msg", e.getMessage());
            }
            JSONArray jArray = null;//new JSONArray(result);
            String polygonPoints = "";

            try {
                jArray = object.getJSONArray("Table1");

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String[] cordi = jObject.getString("coordinates").split(",");
                    String[] words = jObject.getString("coordinates").split("~");

                    // lbltable.setText(jObject.getString("coordinates"));


                    int arraySize = cordi.length;
                    for (int i1 = 0; i1 < arraySize; i1++) {
                        lbltable.append(cordi[i1]);
                        lbltable.append(","); //+ tagType + ",");
                        lbltable.append("\n");
                    }


                    // polygonPoints = polygonPoints +  " new google.maps.LatLng(" + (Double.parseDouble(words[0].toString()) +", " +Double.parseDouble(words[1].toString() + ")" & ",";
                    polygonPoints = polygonPoints + " new google.maps.LatLng(" + Double.parseDouble(words[0].toString()) + ", " + Double.parseDouble(words[1].toString()) + ")" + ",";

                    arrayPoints.add(new LatLng(Double.parseDouble(words[0].toString()), Double.parseDouble(words[1].toString())));
                    latitude = Double.parseDouble(words[0].toString());
                    longitude = Double.parseDouble(words[1].toString());


                } // End Loop
            } catch (JSONException e) {
                Log.d("Msg", e.getMessage());
            }


            // mMap.clear();
            LatLng latLng = new LatLng(latitude, longitude);

            if (arrayPoints.size() > 0) {

                SphericalUtil.computeArea(arrayPoints);
                Double ar = Math.round((SphericalUtil.computeArea(arrayPoints) * 0.00024711) * 100.0) / 100.0;//area(arrayPoints);
                lblarea.setText("" + ar.toString());
            }
            // msclass.showMessage(ar.toString());

            String HtmlCode = "<!DOCTYPE html><html>  <head>    <meta name='viewport' content='initial-scale=1.0, user-scalable=no'>    <meta charset='utf-8'>    <title>Polygon Arrays</title>    <style>      html, body, #map-canvas {        height: 100%;        margin: 0px;        padding: 0px      }    </style>";
            HtmlCode = HtmlCode + "<script src='https://maps.googleapis.com/maps/api/js?key=AIzaSyC6v5-2uaq_wusHDktM9ILcqIrlPtnZgEk&sensor=false'>" +
                    "</script>    <script> var Map;var infoWindow;function initialize() { " +
                    " var mapOptions = {    Zoom: 15,   " +
                    " center: new google.maps.LatLng(" + latitude + " , " + longitude + ") };";
            HtmlCode = HtmlCode + "var bermudaTriangle; " +
                    " Map = new google.maps.Map(document.getElementById('map-canvas'),  " +
                    "    mapOptions);  " +
                    "var triangleCoords = [     " + polygonPoints + "   ];";
            HtmlCode = HtmlCode + "bermudaTriangle = new google.maps.Polygon({    paths: triangleCoords, strokeColor: '#FF0000',    strokeOpacity: 0.8,    strokeWeight: 3,    fillColor: '#FF0000',    fillOpacity: 0.35  });";
            HtmlCode = HtmlCode + "bermudaTriangle.setMap(Map); google.maps.event.addListener(bermudaTriangle, 'click', showArrays);    infoWindow = new google.maps.InfoWindow();}";
            HtmlCode = HtmlCode + "function showArrays(event) {  var vertices = this.getPath();  var contentString = '<b>My polygon</b><br>' +    'Clicked location: <br>' + event.latLng.lat() + ',' + event.latLng.lng() +      '<br>';  for (var i =0; i < vertices.getLength(); i++) {    var xy = vertices.getAt(i);    contentString += '<br>' + 'Coordinate ' + i + ':<br>' + xy.lat() + ',' +        xy.lng();  }";
            HtmlCode = HtmlCode + "infoWindow.setContent(contentString);  infoWindow.setPosition(event.latLng);  infoWindow.open(Map);}google.maps.event.addDomListener(window, 'load', initialize);    </script>  </head>  <body>    <div id='map-canvas'></div>   </body></html>";

            wb1.getSettings().setJavaScriptEnabled(true);
            wb1.loadData(HtmlCode, "text/html", null);
        }catch(Exception e)
        {
            Toast.makeText(newAreaTag.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private JSONArray getResults() {
        String myTable = "Table1";//Set name of your table
        String searchQuery = "SELECT  coordinates  FROM tagdata where flag='T' and TRIL_CODE='" + TRIL_CODE.getText().toString() + "' and tagType='" + tagType + "'";
        Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
        JSONArray resultSet = new JSONArray();

        if (resultSet == null) {

        } else {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i) != null) {
                        try {
                            if (cursor.getString(i) != null) {
                                Log.d("TAG_NAME", cursor.getString(i));
                                rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                            } else {
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        } catch (Exception e) {
                            Log.d("TAG_NAME", e.getMessage());
                        }
                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return resultSet;
    }

}
