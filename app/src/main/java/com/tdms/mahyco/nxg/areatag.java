package com.tdms.mahyco.nxg;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class areatag extends AppCompatActivity implements  LocationListener,
        OnMapReadyCallback{


    private double longitude;
    private double latitude;
    Button getLocationBtn,btnClear,btnCalulate,btnUpload;
    TextView locationText,lblarea,recyclableTextView,lblDetail;
    private ArrayList<LatLng> arrayPoints = null;
    LocationManager locationManager;
    public databaseHelper mDatabase;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public String usercode, imeino, address;
    public String coordinate="";
    public String area="0";
    public Date entrydate;
    public String endate;
    public WebView wb1;
    //Our Map
    private GoogleMap mMap;
    public RelativeLayout RelativeLayout1;
    private TableMainLayout tmain;
    int consize[] = {
            75,
            300,
            300};
    String headers[] = {
            "Sr.No",
            //"User Code",
            "Co-Ordinates",
            "Address"};
    public String SERVER = "http://cmr.mahyco.com/farmmechHandler.ashx";
    public String cardid;
    public Messageclass msclass;
    ProgressDialog dialog;
    // flag for GPS status
    boolean isGPSEnabled = false;
    public CommonExecution cx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areatag);


        getSupportActionBar().hide();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        getLocationBtn = (Button)findViewById(R.id.getLocationBtn);
        btnClear = (Button)findViewById(R.id.btnClear);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        btnCalulate = (Button)findViewById(R.id.btnCalulate);
        wb1 = (WebView) findViewById(R.id.wb1);
        locationText = (TextView)findViewById(R.id.locationText);
        RelativeLayout1 = (RelativeLayout) findViewById(R.id.RelativeLayout1);
        lblarea = (TextView)findViewById(R.id.lblarea);
        lblDetail= (TextView)findViewById(R.id.lblDetail);
        arrayPoints = new ArrayList<LatLng>();
        mDatabase = new databaseHelper(this);
        msclass=new Messageclass(this);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // mDatabase.openDataBase();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            /* @Override
             public void onClick(View v) {
                 getLocation();
                 savePunch();
                 binddata();
             }  */
            @Override
            public void onClick(View arg0) {
                // create class object
                // getting GPS status
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // check if GPS enabled
                if(isGPSEnabled){

                    getLocation();
                    savePunch();
                    binddata();
                    // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    showSettingsAlert();
                }

            }



        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.deleledata("punchdata","");
                binddata();
            }
        });
        btnCalulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploaddata();
            }
        });
        // SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //fm.getMapAsync(geotag.this);

        // mDatabase.deleledata("punchdata","");
        //lblDetail.setText("Card No :"+getIntent().getStringExtra("CardID") +" \n Name :"+getIntent().getStringExtra("name"));
        //cardid=getIntent().getStringExtra("CardID");

        String test = getIntent().getStringExtra("keyCode");
       // String getArgument = getArguments().getString("keyCode");
        lblDetail.setText(test);
      //  binddata();

        cx = new CommonExecution(this);

    }

    private JSONArray getResults()
    {
        String myTable = "Table1";//Set name of your table
        String searchQuery = "SELECT  UserCode as usercode ,coordinates,'IN' as cordianteFlag,entrydate as date,address as Location,entrydate as Tr_date  FROM punchdata where Flag=0 order by _id ";
        Cursor cursor = mDatabase.getReadableDatabase().rawQuery(searchQuery, null );
        JSONArray resultSet  = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    public void calculate()
    {


        arrayPoints.clear();
        JSONObject object = new JSONObject();
        try {
            object.put("Table1", getResults());
        } catch (JSONException e) {
            Log.d("Msg",e.getMessage());
        }
        JSONArray jArray = null;//new JSONArray(result);
        try {
            jArray = object.getJSONArray("Table1");

            for(int i=0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String[] words=jObject.getString("coordinates").split("-");
                arrayPoints.add(new LatLng(Double.parseDouble(words[0].toString()), Double.parseDouble(words[1].toString())));
            } // End Loop
        } catch (JSONException e) {
            Log.d("Msg",e.getMessage());
        }





        if(arrayPoints.size()>0) {


            SphericalUtil.computeArea(arrayPoints);
            Double ar = Math.round((SphericalUtil.computeArea(arrayPoints) * 0.00024711) * 100.0) / 100.0;//area(arrayPoints);
            lblarea.setText("Acres Value :" + ar.toString());
            area=ar.toString();
        }
        // msclass.showMessage(ar.toString());
    }



    public void uploaddata()
    {
        // String jsonstr=getResults().toString();
        String searchQuery = "SELECT  UserCode as usercode ,coordinates,'IN' as cordianteFlag,entrydate as date,address as Location,entrydate as Tr_date  FROM punchdata where Flag=0 order by _id ";
        Cursor cursor = mDatabase.getReadableDatabase().rawQuery(searchQuery, null );
        if (cursor.getCount()>0) {
            byte[] objAsBytes = null;//new byte[10000];
            JSONObject studentsObj = new JSONObject();
            try {
                studentsObj.put("Table1", getResults());

            } catch (JSONException e) {
                Log.d("Msg",e.getMessage());
            }

            try {
                objAsBytes = studentsObj.toString().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.d("Msg",e.getMessage());
            }
            // dialog.setMessage("Loading. Please wait...");
            // dialog.show();
            //new JobcardExecutionData(objAsBytes).execute(serverurlupload);
            try {
                String str = new JobcardExecutionData(5, objAsBytes).execute(SERVER, cardid, "", "", "", "NEW", "", area).get();

                if (str.contains("True")) {
                    msclass.showMessage("Tag Data uploaded Successully.");
                    mDatabase.deleledata("punchdata", "");
                    binddata();
                } else {
                    //msclass.showMessage(str);
                    msclass.showMessage("Tag data upload failed");
                }


            } catch (InterruptedException e) {
                Log.d("Msg",e.getMessage());
            } catch (ExecutionException e) {
                Log.d("Msg",e.getMessage());
            }
        }
        else
        {
            msclass.showMessage("GPS Co-Ordinate not  there.");
        }


    }
    public void savePunch()
    {

        //coordinate=lblLocation.getText().toString();
        address=locationText.getText().toString();
        usercode=pref.getString("11",null);
        imeino=pref.getString("11",null);
        try {
            if (coordinate.length() ==0)
            {
                Toast.makeText(this, "Please check GPS Co-Ordinate", Toast.LENGTH_SHORT).show();
            }
            else {

               // boolean fl = mDatabase.inserpunchdata(usercode, imeino, coordinate, address, endate);
                // mDatabase.deleledata("punchdata","");
                Toast.makeText(this, "Save Input data successfully", Toast.LENGTH_SHORT).show();
                coordinate="";
            }
            //binddata();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void binddata()
    {
        RelativeLayout1.removeAllViews();
        tmain=new TableMainLayout(this,RelativeLayout1,headers,consize);
        mDatabase = new databaseHelper(this);
        addHeaders();

        // tmain.resizeBodyTableRowHeight();
        // moveMap();
        createmap();

    }
    public void createmap()
    {
        arrayPoints.clear();
        JSONObject object = new JSONObject();
        try {
            object.put("Table1", getResults());
        } catch (JSONException e) {
            Log.d("Msg",e.getMessage());
        }
        JSONArray jArray = null;//new JSONArray(result);
        String polygonPoints="";

        try {
            jArray = object.getJSONArray("Table1");

            for(int i=0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String[] words=jObject.getString("coordinates").split("-");
                // polygonPoints = polygonPoints +  " new google.maps.LatLng(" + (Double.parseDouble(words[0].toString()) +", " +Double.parseDouble(words[1].toString() + ")" & ",";
                polygonPoints = polygonPoints +  " new google.maps.LatLng(" + Double.parseDouble(words[0].toString())+", " + Double.parseDouble(words[1].toString()) + ")" + ",";

                arrayPoints.add(new LatLng(Double.parseDouble(words[0].toString()), Double.parseDouble(words[1].toString())));
                latitude=Double.parseDouble(words[0].toString());
                longitude=Double.parseDouble(words[1].toString());
            } // End Loop
        } catch (JSONException e) {
            Log.d("Msg",e.getMessage());
        }


        // mMap.clear();
        LatLng latLng = new LatLng(latitude, longitude);

        if(arrayPoints.size()>0) {

            SphericalUtil.computeArea(arrayPoints);
            Double ar = Math.round((SphericalUtil.computeArea(arrayPoints) * 0.00024711) * 100.0) / 100.0;//area(arrayPoints);
            lblarea.setText("Acres Value :" + ar.toString());
        }
        // msclass.showMessage(ar.toString());

        String  HtmlCode ="<!DOCTYPE html><html>  <head>    <meta name='viewport' content='initial-scale=1.0, user-scalable=no'>    <meta charset='utf-8'>    <title>Polygon Arrays</title>    <style>      html, body, #map-canvas {        height: 100%;        margin: 0px;        padding: 0px      }    </style>";
        HtmlCode = HtmlCode +   "<script src='https://maps.googleapis.com/maps/api/js?key=AIzaSyC6v5-2uaq_wusHDktM9ILcqIrlPtnZgEk&sensor=false'>" +
                "</script>    <script> var Map;var infoWindow;function initialize() { " +
                " var mapOptions = {    Zoom: 15,   " +
                " center: new google.maps.LatLng(" + latitude + " , " +longitude + ") };";
        HtmlCode = HtmlCode + "var bermudaTriangle; " +
                " Map = new google.maps.Map(document.getElementById('map-canvas'),  " +
                "    mapOptions);  " +
                "var triangleCoords = [     " + polygonPoints +  "   ];";
        HtmlCode = HtmlCode + "bermudaTriangle = new google.maps.Polygon({    paths: triangleCoords, strokeColor: '#FF0000',    strokeOpacity: 0.8,    strokeWeight: 3,    fillColor: '#FF0000',    fillOpacity: 0.35  });";
        HtmlCode = HtmlCode + "bermudaTriangle.setMap(Map); google.maps.event.addListener(bermudaTriangle, 'click', showArrays);    infoWindow = new google.maps.InfoWindow();}" ;
        HtmlCode = HtmlCode + "function showArrays(event) {  var vertices = this.getPath();  var contentString = '<b>My polygon</b><br>' +    'Clicked location: <br>' + event.latLng.lat() + ',' + event.latLng.lng() +      '<br>';  for (var i =0; i < vertices.getLength(); i++) {    var xy = vertices.getAt(i);    contentString += '<br>' + 'Coordinate ' + i + ':<br>' + xy.lat() + ',' +        xy.lng();  }";
        HtmlCode = HtmlCode + "infoWindow.setContent(contentString);  infoWindow.setPosition(event.latLng);  infoWindow.open(Map);}google.maps.event.addDomListener(window, 'load', initialize);    </script>  </head>  <body>    <div id='map-canvas'></div>   </body></html>";

        wb1.getSettings().setJavaScriptEnabled(true);
        wb1.loadData(HtmlCode, "text/html", null);



    }
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, this);
        }
        catch(SecurityException e) {
            Log.d("Msg",e.getMessage());
        }
    }
    public TextView makeTableRowWithText(String text, int a) {
        TableRow.LayoutParams params3 = new TableRow.LayoutParams( tmain.consize[a],//70);
                80);
        params3.setMargins(2, 1, 0, 0);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        recyclableTextView = new TextView(this);
        recyclableTextView.setText(text);
        recyclableTextView.setBackgroundColor(Color.WHITE);
        recyclableTextView.setTextColor(Color.BLACK);
        recyclableTextView.setTextSize(10);
        recyclableTextView.setGravity(Gravity.CENTER);
        recyclableTextView.setLayoutParams(params3);
        recyclableTextView.setEllipsize(TextUtils.TruncateAt.END);;
        // recyclableTextView.setSingleLine(true);
        recyclableTextView.setHorizontallyScrolling(false);
        recyclableTextView.setMaxLines(2);
        // recyclableTextView.setVerticalScrollBarEnabled(true);
        return recyclableTextView;
    }
    public void addHeaders(){

        // RelativeLayout1.removeAllViews();
        TableRow row;
        TableRow row1;
        //rest of the table (within a scroll view)
        String selectQuery = "SELECT * FROM  punchdata where Flag=0 order by _id desc";
        Cursor cursor = mDatabase.getReadableDatabase().rawQuery(selectQuery,null);
        TableRow.LayoutParams params = new TableRow.LayoutParams( tmain.headerCellsWidth[0], RelativeLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 2, 0, 0);

        if(cursor.getCount() >0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                row1 = new TableRow(this);
                row = new TableRow(this);


                row.addView(makeTableRowWithText(cursor.getString(0), 0));
                //row.addView(makeTableRowWithText(cursor.getString(1), 1));
                row.addView(makeTableRowWithText(cursor.getString(cursor.getColumnIndex("coordinates")), 1));
                row.addView(makeTableRowWithText(cursor.getString(cursor.getColumnIndex("address")), 2));
                //row.addView(makeTableRowWithText(cursor.getString(cursor.getColumnIndex("entrydate")), 3));

                tmain.gnerateCD(row1,row);

            }
        }


    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    @Override
    public void onBackPressed(){
        // super.onBackPressed();
        // finish();
        //
       /* finish();
        Intent intent = new Intent(geotag.this, ListDetails.class);
        intent.putExtra("Header", "Field Measurement Details");
        intent.putExtra("ServiceName", "Field Measurement");
        intent.putExtra("Types", "GEOTag");
        intent.putExtra("funname", "GEOTag");
        startActivity(intent);*/
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.
        mMap = googleMap;
        binddata();

    }

    @Override
    public void onLocationChanged(Location location) {
        longitude=location.getLongitude();
        latitude=location.getLatitude();
        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        coordinate=location.getLatitude()+"-"+location.getLongitude();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText(addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(areatag.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    private class JobcardExecutionData extends AsyncTask<String, String, String> {

        private Bitmap image;
        private String IssueID;
        private String comments;
        private int action;
        byte[] objAsBytes;
        AlertDialog.Builder builder = new AlertDialog.Builder( areatag.this);
        public JobcardExecutionData(int action,byte[] objAsBytes) {

            this.action=action;
            this.objAsBytes=objAsBytes;

        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {

            HttpClient httpclient = new DefaultHttpClient();
            StringBuilder builder = new StringBuilder();
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>(2);
            postParameters.add(new BasicNameValuePair("from", "JobCardExecution"));
            String encodedData = Base64.encodeToString(objAsBytes,Base64.DEFAULT);
            postParameters.add(new BasicNameValuePair("encodedData", encodedData));

            String Urlpath=urls[0]+"?action="+action+"&cardid="+urls[1].toString()+"" +
                    "&operatorid="+urls[2].toString()+"" +
                    "&amount="+urls[3].toString()+"&" +
                    "repairemaintance="+urls[4].toString()+"" +
                    "&status="+urls[5].toString()+
                    "&type="+urls[6].toString()+
                    "&tagarea="+urls[7].toString()+"";

            HttpPost httppost = new HttpPost(Urlpath);
            httppost.addHeader("Content-type", "application/x-www-form-urlencoded");

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
                httppost.setEntity(formEntity);

                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line).append("\n");
                    }

                }
            } catch (ClientProtocolException e) {
                Log.d("Msg",e.getMessage());
                msclass.showMessage("Something went wrong, please try again later");
                dialog.dismiss();
            } catch (Exception e) {
                msclass.showMessage("Something went wrong, please try again later");
                Log.d("Msg",e.getMessage());
                dialog.dismiss();

            }
            // resp.setText("rtrtyr");

            return builder.toString();
        }

        protected void onPostExecute(String result) {
            String weatherInfo = "Weather Report  is: \n";
            try {
                cx.redirecttoRegisterActivity(result,areatag.this); //If authorization fails redirect to login

                // JSONObject jsonObject = new JSONObject(result);
                //// builder.setMessage("Comments Post Successfully");
                //builder.show();
                //bindGetCommentdata(result);
                if(result.contains("True"))
                {
                    // msclass.showMessage("Data saved successfully.");

                }
                else {
                    msclass.showMessage(result);
                }
                //   lblusername.setText(result) ;
                //dialog.dismiss();


            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
                msclass.showMessage("Something went wrong, please try again later");
                dialog.dismiss();
            }

        }
    }
}
