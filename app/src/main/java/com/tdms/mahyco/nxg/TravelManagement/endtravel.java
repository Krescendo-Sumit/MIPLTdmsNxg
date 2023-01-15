package com.tdms.mahyco.nxg.TravelManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.crash.FirebaseCrash;
import com.tdms.mahyco.nxg.CommonExecution;
import com.tdms.mahyco.nxg.Config;
import com.tdms.mahyco.nxg.Messageclass;
import com.tdms.mahyco.nxg.R;
import com.tdms.mahyco.nxg.databaseHelper;
import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.FileUtilImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import static android.content.ContentValues.TAG;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class endtravel extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener, ResultCallback
        {
    View parentHolder;
    public SearchableSpinner spvehicletype,spDist,spState, spTaluka, spVillage, spCropType, spProductName, spMyactvity, spComment;
    public Button btnstUpdate, btnTakephoto;
    private String state;
    EditText txtlocation;

    private String dist, taluka, village, Imagepath1;
    private databaseHelper mDatabase;
    public Messageclass msclass;
    SharedPreferences pref, locdata;
    SharedPreferences.Editor editor, locedit;
    int imageselect;
    Context context;
    CheckBox chktag;
    Config confing;
    CommonExecution cx;
    public static int startreading=0;

    File photoFile = null;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView ivImage;
    private static final String IMAGE_DIRECTORY_NAME = "VISITPHOTO";
    SharedPreferences preferences;
    public   static String cordinate="";
    public static String address="" ;
    EditText txtkm;
    String userCode;
//Rohit given
Location location;
private static final long INTERVAL = 1000 * 5;
private static final long FASTEST_INTERVAL = 1000 * 20;
        boolean IsGPSEnabled = false;
private LocationRequest locationRequest;
private GoogleApiClient googleApiClient;
private FusedLocationProviderApi fusedLocationProviderApi = FusedLocationApi;
        boolean fusedlocationRecieved;
        boolean GpsEnabled;
        int REQUEST_CHECK_SETTINGS=101;
            private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    public endtravel() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = new databaseHelper(this.getActivity());
        msclass = new Messageclass(this.getActivity());
        context = this.getActivity();
        confing=new Config(context);
        cx=new CommonExecution(context);

        if (checkPlayServices()) {

        }
        else
        {
            msclass.showMessage("This device google play services not supported for Devices location");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        parentHolder = inflater.inflate(R.layout.fragment_endtravel, container,
                false);
        preferences = this.getActivity().getSharedPreferences("MyPref", 0);
        editor = preferences.edit();
        TextView lblwelcome = (TextView) parentHolder.findViewById(R.id.lblwelcome);
        lblwelcome.setText("NAME: " + preferences.getString("Displayname", null));
        spState=(SearchableSpinner) parentHolder.findViewById(R.id.spState);
        spvehicletype= (SearchableSpinner)parentHolder.findViewById(R.id.spvehicletype);
        spDist = (SearchableSpinner) parentHolder.findViewById(R.id.spDist);
        spTaluka = (SearchableSpinner) parentHolder.findViewById(R.id.spTaluka);
       // spVillage = (Spinn) parentHolder.findViewById(R.id.spVillage);
        btnstUpdate = (Button) parentHolder.findViewById(R.id.btnstUpdate);
        btnTakephoto = (Button) parentHolder.findViewById(R.id.btnTakephoto);
        ivImage = (ImageView) parentHolder.findViewById(R.id.ivImage);
        txtkm=(EditText) parentHolder.findViewById(R.id.txtEnd);
        chktag=(CheckBox) parentHolder.findViewById(R.id.chktag);
        txtlocation=(EditText) parentHolder.findViewById(R.id.txtlocation);

        TextView lbltime = (TextView) parentHolder.findViewById(R.id.lbltime);

        Date entrydate = new Date();
        String InTime = new SimpleDateFormat("dd-MM-yyyy").format(entrydate);

        lbltime.setText("END TOUR FOR THE DAY- "+InTime);
        pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
       // userCode= pref.getString("UserID", null);
        userCode = getUserCode();

        //BindState();

        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GeneralMaster gm = (GeneralMaster) parent.getSelectedItem();
                try {
                    state= URLEncoder.encode(gm.Code().trim(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.d("Msg",e.getMessage());
                }

                //BindDist(state);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spDist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GeneralMaster gm = (GeneralMaster) parent.getSelectedItem();
                try {
                    dist = gm.Code().trim();
                } catch (Exception e) {
                    Log.d("Msg",e.getMessage());
                }

                {

                    //BindTaluka(dist);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        spTaluka.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GeneralMaster gm = (GeneralMaster) parent.getSelectedItem();
                try {
                    taluka = gm.Code().trim();// URLEncoder.encode(gm.Code().trim(), "UTF-8");
                } catch (Exception e) {
                    Log.d("Msg",e.getMessage());
                }

                {



                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        btnstUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                saveStarttravel();

            }
        });
        btnTakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                }
                imageselect = 1;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        captureImage();
                    } else {
                        captureImage2();
                    }
                } catch (Exception ex) {
                    Log.d("Msg",ex.getMessage());
                    msclass.showMessage("Something went wrong, please try again later");
                }
            }
        });
//rohit
//        boolean flag2 = mDatabase.isTableExists("mdo_endtravel");
//        if (flag2 == false) {
//            mDatabase.CreateTable9();
//        }
//        int cnt12=mDatabase.getnocoloumncount("Select * from mdo_endtravel");
//        if (cnt12==12)
//        {
//            mDatabase.getWritableDatabase().execSQL("ALTER TABLE mdo_endtravel ADD COLUMN  place TEXT");
//        }
        intialbinddata();
        return parentHolder;
    }
            private boolean checkPlayServices() {
                boolean flag=false;
                try {
                    int resultCode = GooglePlayServicesUtil
                            .isGooglePlayServicesAvailable(this.getActivity());
                    if (resultCode != ConnectionResult.SUCCESS) {
                        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                            GooglePlayServicesUtil.getErrorDialog(resultCode, this.getActivity(),
                                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
                        } else {
                            Toast.makeText(this.getActivity(),
                                    "This device is not supported.", Toast.LENGTH_LONG)
                                    .show();
                            getActivity().finish();
                        }
                        flag=false;
                    }
                    else
                    {
                        flag=true;
                    }
                }
                catch (Exception ex)
                {
                    flag=false;
                }
                return flag;
            }


            @Override
            public void onConnectionFailed(ConnectionResult arg0) {

            }
            @Override
            public void onConnected(Bundle arg0) {
                try {
                    LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        fusedlocationRecieved = false;
                        if (googleApiClient != null && googleApiClient.isConnected()) {
                            Log.d(TAG, "Fused api connected: ");
                            if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest,  this);
                        }

                    } else {
                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest);
                        builder.setAlwaysShow(true);
                        PendingResult result =
                                LocationServices.SettingsApi.checkLocationSettings(
                                        googleApiClient,
                                        builder.build()
                                );

                        result.setResultCallback(this);
                    }
                } catch (Exception e) {
                    Log.d("Msg",e.getMessage());
                }


            }

            @Override
            public void onConnectionSuspended(int arg0) {

            }
            @Override
            public synchronized void onLocationChanged(Location arg0) {

                try {
                    if (arg0 == null) {
                        return;
                    }
                    if (arg0.getLatitude() == 0 && arg0.getLongitude() == 0) {
                        return;
                    }
                    double lati = arg0.getLatitude();
                    double longi = arg0.getLongitude();
                    location=arg0;
                    Log.d(TAG, "onLocationChanged: "+ String.valueOf(longi));
                    cordinate = String.valueOf(lati)+"-"+ String.valueOf(longi);
                    address = getCompleteAddressString(lati, longi);


                } catch (Exception e) {
                    Log.d(TAG, "onLocationChanged: "+e.toString());
                    Log.d("Msg",e.getMessage());
                    //  }
                }

            }
            private synchronized void startFusedLocationService() {
                try {
                    LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        IsGPSEnabled = true;
                    } else {
                        IsGPSEnabled = false;
                    }
                    if (IsGPSEnabled) {
                        locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(INTERVAL);
                        locationRequest.setSmallestDisplacement(0f);
                        locationRequest.setFastestInterval(FASTEST_INTERVAL);
                        googleApiClient = new GoogleApiClient.Builder(this.getActivity())
                                .addApi(LocationServices.API).addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this).build();
                        try {
                            if (googleApiClient != null) {
                                googleApiClient.connect();
                            }
                        } catch (Exception e) {
                            Log.d("Msg",e.getMessage());
                        }
                        GpsEnabled = true;

                    } else {
                        GpsEnabled = false;
                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest);
                        builder.setAlwaysShow(true);
                        PendingResult result =
                                LocationServices.SettingsApi.checkLocationSettings(
                                        googleApiClient,
                                        builder.build()
                                );
                        result.setResultCallback(this);
                    }
                    Log.d(TAG, "startFusedLocationService: ");
                }
                catch (Exception ex)
                {
                    msclass.showMessage("Please on device GPS location");
                   // msclass.showMessage("Please on device GPS location.\n startFusedLocationService"+ex.getMessage());
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onResult(@NonNull Result result) {
                try {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            int st = getLocationMode();
                            if (st == 1 || st == 2) {
                                Toast.makeText(this.getActivity(), "onResult-Enable gps in High Accuracy only.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {

                                status.startResolutionForResult(this.getActivity(), REQUEST_CHECK_SETTINGS);

                            } catch (IntentSender.SendIntentException e)
                            {

                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are unavailable so not possible to show any dialog now
                            break;
                    }
                }
                catch(Exception ex)
                {
                    Toast.makeText(this.getActivity(), "Func-onResult"+ex.toString(), Toast.LENGTH_LONG).show();

                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public int getLocationMode() {
                int val = 0;
                try {
                    val = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
                    Log.d(TAG, "getLocationMode status: " + val);
                } catch (Settings.SettingNotFoundException e) {
                    Log.d("Msg",e.getMessage());
                }
                return val;

            }
            public void stopFusedApi() {
                try {
                    if (googleApiClient != null && (googleApiClient.isConnected() )) {
                        FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                        googleApiClient.disconnect();
                    }
                } catch (Exception ex) {
                    FirebaseCrash.report(ex);
                    Log.d("Msg",ex.getMessage()); // Ignore error

                    // ignore the exception
                } finally {

                    googleApiClient = null;
                    locationRequest = null;
                }
            }
            @Override
            public void onResume() {
                super.onResume();
                try {

                    startFusedLocationService();
                }
                catch(Exception ex)
                {
                    //msclass.showMessage("Funtion name :onresume"+ex.getMessage());
                    msclass.showMessage("Something went wrong, please try again later");
                }

            }
            @Override
            public void onPause() {
                super.onPause();

                try {
                    stopFusedApi();

                }
                catch (Exception ex)
                {
                    Log.d("Msg",ex.getMessage());
                }
            }

    public void intialbinddata()
    {
        spvehicletype.setAdapter(null);
        List<GeneralMaster> gm = new ArrayList<GeneralMaster>();
        gm.add(new GeneralMaster("0","SELECT VEHICLE TYPE"));
        gm.add(new GeneralMaster("1","COMPANY VEHICLE"));
        gm.add(new GeneralMaster("2","PERSONAL VEHICLE(4 wheeler)"));
        gm.add(new GeneralMaster("3","PERSONAL VEHICLE(2 wheeler)"));
        gm.add(new GeneralMaster("4","PUBLIC VEHICLE"));
        gm.add(new GeneralMaster("5","OTHER"));
        ArrayAdapter<GeneralMaster> adapter = new ArrayAdapter<GeneralMaster>
                (this.context,android.R.layout.simple_spinner_dropdown_item, gm);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spvehicletype.setAdapter(adapter);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date d=new Date();
            String strdate=dateFormat.format(d);

            JSONObject object = new JSONObject();
            object.put("Table1", mDatabase.getResults("select  * from mdo_endtravel  " +
                    "where strftime( '%Y-%m-%d', enddate)='"+strdate+"' and mdocode='"+ userCode +"'"));
            JSONArray jArray = object.getJSONArray("Table1");//new JSONArray(result);

            JSONObject object2 = new JSONObject();
            object2.put("Table2", mDatabase.getResults("select  * from mdo_starttravel  " +
                    "where strftime( '%Y-%m-%d', startdate)='"+strdate+"' and mdocode='"+ userCode +"'"));
            JSONArray jArray2 = object2.getJSONArray("Table2");//new JSONArray(result);
            startreading=0;
            if(jArray2.length()>0) {
                JSONObject jObject = jArray2.getJSONObject(0);
                if(jObject.getString("txtkm").toString()!="") {
                    startreading = Integer.valueOf(jObject.getString("txtkm").toString());
                }
                spvehicletype.setSelection(confing.getIndex(spvehicletype,jObject.getString("vehicletype").toString()));
            }
            else
            {
                btnstUpdate.setVisibility(View.INVISIBLE);
                msclass.showMessage("Tour is not started ,please fill tour started data.");
            }




            if(jArray.length()>0) {

                btnstUpdate.setVisibility(View.INVISIBLE);
               // msclass.showMessage("Tour ended.");
                msclass.showMessageandRedirectToHome("Tour has been completed for today.",true,context);


                JSONObject jObject = jArray.getJSONObject(0);

                //BindDist("");
               // spDist.setSelection(confing.getIndex(spDist, jObject.getString("dist").toString()));
                //BindTaluka(jObject.getString("dist").toString()) ;
               // spTaluka.setSelection(confing.getIndex(spTaluka, jObject.getString("taluka").toString()));
                txtkm.setText(jObject.getString("txtkm").toString());
                Imagepath1=jObject.getString("imgpath");
                cx.bindimage(ivImage,jObject.getString("imgpath"));
                txtlocation.setText(jObject.getString("place").toString());
            }

        }
        catch (Exception ex)
        {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg",ex.getMessage());

        }
    }

    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
    private void captureImage2() {

        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            if (imageselect == 1) {
                photoFile = createImageFile4();
                if (photoFile != null) {
                    Log.i("Mayank", photoFile.getAbsolutePath());
                    Uri photoURI = Uri.fromFile(photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                }
            }
        } catch (Exception ex) {
            Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
    }

    private void captureImage() {

        try {

            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA}, 0);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    try {
                        if (imageselect == 1) {
                            photoFile = createImageFile();

                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(context,
                                        "com.newtrail.mahyco.trail.provider",
                                        photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                            }
                        }

                    } catch (Exception ex) {

                        msclass.showMessage("Something went wrong, please try again later");
                        Log.d("Msg",ex.getMessage());
                    }


                } else {
                }
            }
        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg",ex.getMessage());

        }
    }

    private File createImageFile4() //  throws IOException
    {
        File mediaFile = null;
        try {
            // External sdcard location
            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    IMAGE_DIRECTORY_NAME);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } catch (Exception ex) {
            Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
        return mediaFile;
    }

    private File createImageFile() {
        // Create an image file name
        File image = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        } catch (Exception ex) {
            Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");        }

        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
            }
        } catch (Exception e) {
            Log.d("Msg",e.getMessage());
            msclass.showMessage("Something went wrong, please try again later");        }
    }

    private void onCaptureImageResult(Intent data) {


        try {
            if (imageselect == 1) {

                try {
                   /* BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;

                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                    Imagepath1 = photoFile.getAbsolutePath();
                    ivImage.setImageBitmap(myBitmap);*/

                    /*------------------------------Start -----------------------*/
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // options.inJustDecodeBounds = true;
                    options.inSampleSize =2;
                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(),options);
                    // myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    // this only get capture photo
                    //************ 30 Dec 2020
                    Date entrydate = new Date();
                    String  InTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entrydate);
                    AppConstant.queryImageUrl = photoFile.getAbsolutePath();
                    AppConstant.imageUri = Uri.fromFile(new File(AppConstant.queryImageUrl));
                    AppConstant.Imagename=this.getClass().getSimpleName()+userCode+String.valueOf(entrydate.getTime()) ;
                    FileUtilImage.compressImageFile( AppConstant.queryImageUrl, AppConstant.imageUri,
                            getActivity(),AppConstant.Imagename);
                    // need to set commpress image path
                    Imagepath1 = FileUtilImage.savefilepath;// photoFile.getAbsolutePath();  old ssave
                    ivImage.setImageBitmap(myBitmap);
                    //************** 30 Dec 2020
                    /*------------------------------End -----------------------*/


                } catch (Exception e) {
                    msclass.showMessage("Something went wrong, please try again later");
                    Log.d("Msg",e.getMessage());
                }
                //end
            }

        } catch (Exception e) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg",e.getMessage());
        }

    }

    private void onSelectFromGalleryResult(Intent data) {


        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), data.getData());
            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
            }
        }

        if (imageselect == 1) {
            ivImage.setImageBitmap(bm);
        }

    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";

        try {
            Geocoder geocoder = new Geocoder(this.getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                android.location.Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("MyCurrentloctionaddress", strReturnedAddress.toString());
            } else {
                Log.w("MyCurrentloctionaddress", "No Address returned!");
            }
        } catch (Exception e) {
            Log.d("Msg",e.getMessage());
            Log.w("MyCurrentloctionaddress", "Canont get Address!");
        }
        return strAdd;
    }

    private boolean validation()
    {
        try {
            boolean flag = true;
            GeneralMaster sv = (GeneralMaster) spvehicletype.getSelectedItem();
            String vehicletype =sv.Code().toString();
            if (sv.Code().equals("0")) {
                msclass.showMessage("Please select vehicle type");
                return false;
            }
//            if (spDist.getSelectedItem().toString().toLowerCase().equals("select district")) {
//                msclass.showMessage("Please select district");
//                return false;
//            }
//            if (spTaluka.getSelectedItem().toString().toLowerCase().equals("select taluka")) {
//                msclass.showMessage("Please Select Taluka");
//                return false;
//            }





            if(vehicletype.equals("2")||vehicletype.equals("3")) // Only for company vehicle code validation
            {
                if (ivImage.getDrawable() == null) {
                    msclass.showMessage("Please upload vehicle reading photo(km)");
                    return false;
                }

                if (txtkm.getText().length() == 0) {
                    msclass.showMessage("Please enter start reading (km).");
                    return false;

                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date d=new Date();
                String strdate=dateFormat.format(d);
                JSONObject object2 = new JSONObject();
                object2.put("Table2", mDatabase.getResults("select  * from mdo_starttravel  " +
                        "where strftime( '%Y-%m-%d', startdate)='"+strdate+"' and mdocode='"+ userCode +"'"));
                JSONArray jArray2 = object2.getJSONArray("Table2");//new JSONArray(result);
                if(jArray2.length()>0) {
                    JSONObject jObject = jArray2.getJSONObject(0);
                    startreading= Integer.valueOf(jObject.getString("txtkm").toString());
                }
                if(Integer.valueOf(txtkm.getText().toString()) < startreading ) {
                    msclass.showMessage("Your tour started KM reading is("+ String.valueOf(startreading)+")."
                            +"\n"+"Please enter tour ended km reading should be more than tour start reading.");
                    return false;
                }



            }


            if (txtlocation.getText().length() == 0) {
                msclass.showMessage("Please enter location/place.");
                return false;
            }
            if(chktag.isChecked()==false)
            {
                msclass.showMessage("Please check geo tag.");
                return false;
            }


        }catch (Exception ex)
        {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg",ex.getMessage());

        }
        return true;
    }

    public void saveStarttravel() {
        try {
            if (turnGPSOn() == false) {
                msclass.showMessage("GPS is not enabled,Please on GPS");
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            } else {
                if (validation() == true) {
               // GeneralMaster dt = (GeneralMaster) spDist.getSelectedItem();
              //  GeneralMaster tt = (GeneralMaster) spTaluka.getSelectedItem();

                try {

                 //   dist = dt.Code().trim();//URLEncoder.encode(dt.Code().trim(), "UTF-8");
                  //  taluka = tt.Code().trim();//URLEncoder.encode(tt.Code().trim(), "UTF-8");
                    Savedata("", state, dist, taluka, village);
                } catch (Exception ex) {
                    Log.d("Msg",ex.getMessage());
                    msclass.showMessage("Something went wrong, please try again later");
                }
                 }
            }
        } catch (Exception ex) {
            Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");

        }


    }

    private void Savedata(final String myactvity, String state, String dist, String taluka, String village) {
        try {
           // pref.getString("UserID", null);
            Date entrydate = new Date();
            final String InTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entrydate);

            final String Tempimagepath1;
            String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCode);
            final String Imagename = "endTravel" + userCodeDecrypt + String.valueOf(entrydate.getTime());
            //final String Imagename = "ERajshri";
            Tempimagepath1 = Imagepath1;

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getActivity());
            // Setting Dialog Title
            alertDialog.setTitle("Trial Data Management");
            // Setting Dialog Message
            alertDialog.setMessage("Are you sure to end this tour?");
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do do my action here
                    String vehicletype=spvehicletype.getSelectedItem().toString();

                    String crop = "";
                    String product = "";
                    String villagename="";//spVillage.getSelectedItem().toString();
                    boolean fl = mDatabase.InsertendTravelTime(userCode,
                            cordinate, address, InTime,
                            "","",
                            villagename,
                            Imagename,Tempimagepath1,txtkm.getText().toString(),
                            txtlocation.getText().toString(),vehicletype);


                    if (fl == true) {
                        try {
                            msclass.showMessageandRedirectToHome("Tour has been ended.",true,context);

                            txtkm.setText("");
                            txtlocation.setText("");
                            btnstUpdate.setVisibility(View.INVISIBLE);

                        } catch (Exception ex) {
                            msclass.showMessage("Something went wrong, please try again later");
                            Log.d("Msg",ex.getMessage());
                        }
                    } else {
                        msclass.showMessage("Please check entry data.");
                    }


                }

            });

            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // I do not need any action here you might

                    dialog.dismiss();

                }
            });

            AlertDialog alert = alertDialog.create();
            alert.show();
            final Button positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
            positiveButtonLL.weight = 10;
            positiveButtonLL.gravity = Gravity.CENTER;
            positiveButton.setLayoutParams(positiveButtonLL);
            //end

        } catch (Exception ex) {
            Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
    }

    private boolean turnGPSOn(){
        boolean flag=false;
        try {
            LocationManager locationManager=null;
            try {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
              }
            catch(SecurityException e) {
                Log.d("Msg",e.getMessage());
            }
            catch (Exception ex) {
                Log.d("Msg",ex.getMessage());
                msclass.showMessage("Something went wrong, please try again later");
            }
            boolean isGPSEnabled1 = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGPSEnabled && !isGPSEnabled1){

                flag=false;

            }
            else
            {
                flag=true;
            }

        }
        catch (Exception ex) {
            Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
        return  flag;
    }
            public void  BindState()
            {

                try {
                    spState.setAdapter(null);
                    String str = null;
                    try {
                        List<GeneralMaster> Croplist = new ArrayList<GeneralMaster>();
                        String searchQuery = "SELECT distinct state,state_code  FROM VillageLevelMaster order by state asc  ";
                        Cursor cursor = mDatabase.getReadableDatabase().rawQuery(searchQuery, null);
                        Croplist.add(new GeneralMaster("0",
                                "SELECT STATE"));

                        cursor.moveToFirst();
                        while (cursor.isAfterLast() == false) {
                            Croplist.add(new GeneralMaster(cursor.getString(1),
                                    cursor.getString(0).toUpperCase()));

                            cursor.moveToNext();
                        }
                        cursor.close();
                        ArrayAdapter<GeneralMaster> adapter = new ArrayAdapter<GeneralMaster>
                                (this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Croplist);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spState.setAdapter(adapter);
                    }catch (Exception e) {
                        Log.d("Msg",e.getMessage());

                    }

                }
                catch (Exception ex)
                {
                    msclass.showMessage("Something went wrong, please try again later");
                    Log.d("Msg",ex.getMessage());

                }

            }

    public void BindDist(String state) {
        try {
            spDist.setAdapter(null);

            String str = null;
            try {
                List<GeneralMaster> Croplist = new ArrayList<GeneralMaster>();
                String searchQuery = "SELECT distinct district,district_code  FROM VillageLevelMaster" +
                        " where state_code='"+state+"' order by district asc  ";

                Cursor cursor = mDatabase.getReadableDatabase().rawQuery(searchQuery, null);
                Croplist.add(new GeneralMaster("SELECT DISTRICT",
                        "SELECT DISTRICT"));

                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    Croplist.add(new GeneralMaster(cursor.getString(0),
                            cursor.getString(0).toUpperCase()));

                    cursor.moveToNext();
                }
                cursor.close();
                ArrayAdapter<GeneralMaster> adapter = new ArrayAdapter<GeneralMaster>
                        (this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Croplist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spDist.setAdapter(adapter);

            } catch (Exception e) {
                Log.d("Msg",e.getMessage());

            }

        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg",ex.getMessage());

        }


    }

    public void BindTaluka(String dist) {
        try {
            spTaluka.setAdapter(null);
            String str = null;
            try {


                List<GeneralMaster> Croplist = new ArrayList<GeneralMaster>();
                String searchQuery = "SELECT distinct taluka,taluka_code  FROM VillageLevelMaster where district='" + dist + "' order by taluka asc  ";
                Cursor cursor = mDatabase.getReadableDatabase().rawQuery(searchQuery, null);
                Croplist.add(new GeneralMaster("SELECT TALUKA",
                        "SELECT TALUKA"));

                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    Croplist.add(new GeneralMaster(cursor.getString(0),
                            cursor.getString(0).toUpperCase()));

                    cursor.moveToNext();
                }
                cursor.close();
                ArrayAdapter<GeneralMaster> adapter = new ArrayAdapter<GeneralMaster>
                        (this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Croplist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spTaluka.setAdapter(adapter);


            } catch (Exception ex) {
                msclass.showMessage("Something went wrong, please try again later");
                Log.d("Msg",ex.getMessage());
            }
        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg",ex.getMessage());
        }

    }

            public String getUserCode() {
                Cursor data1 = mDatabase.fetchusercode();
                String userCode = "";
                if (data1.getCount() == 0) {
                    msclass.showMessage("No Data Available... ");
                } else {
                    data1.moveToFirst();
                    if (data1 != null) {
                        do {
                            userCode = data1.getString((data1.getColumnIndex("user_code")));
                            /*String userCodeEncrypt = data1.getString((data1.getColumnIndex("user_code")));
                            userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt); *//*changed on 6th Jan 2021*//*
                            Log.d("userCode", "Start travel userCode decrypt" + userCode);*/
                        } while (data1.moveToNext());

                    }
                    data1.close();
                }
                return userCode;
            }


        }

