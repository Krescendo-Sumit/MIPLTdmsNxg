package com.tdms.mahyco.nxg.TravelManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.crash.FirebaseCrash;
import com.tdms.mahyco.nxg.CommonExecution;
import com.tdms.mahyco.nxg.Messageclass;
import com.tdms.mahyco.nxg.R;
import com.tdms.mahyco.nxg.databaseHelper;
import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.FileUtilImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;


public class addtravel extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener, ResultCallback
{
    View parentHolder;
    public Spinner spDist, spTaluka, spVillage, spCropType, spProductName, spMyactvity,spComment;
    public Button btnstUpdate,btnTakephoto;
    private String state;
    private String dist,taluka,village,Imagepath1,usercode;
    ProgressDialog dialog;
    private databaseHelper mDatabase;
    public Messageclass msclass;
    SharedPreferences pref, locdata;
    SharedPreferences.Editor editor, locedit;
    int imageselect;
    Context context;
    CheckBox chktag;
    File photoFile = null;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView ivImage;
    EditText txtpalce;
    private static final String IMAGE_DIRECTORY_NAME = "VISITPHOTO";
    SharedPreferences preferences;
    LinearLayout my_linear_layout1;
    CommonExecution cx;
    private  static String cordinate="";
    private static String address="" ;
    // Location updates intervals in sec
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private static int UPDATE_INTERVAL = 2000;//10000; // 10 sec
    private static int FATEST_INTERVAL = 2000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
     private Location mCurrentLocation;
    private String mLastUpdateTime;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;

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


    public addtravel() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = new databaseHelper(this.getActivity());
        msclass = new Messageclass(this.getActivity());
        context=this.getActivity();
        cx=new CommonExecution(context);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parentHolder = inflater.inflate(R.layout.fragment_addtravel, container,
                false);
        preferences = this.getActivity().getSharedPreferences("MyPref", 0);
        editor = preferences.edit();

        dialog = new ProgressDialog(this.getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        TextView lblwelcome=(TextView)parentHolder.findViewById(R.id.lblwelcome);
        lblwelcome.setText("NAME: "+preferences.getString("Displayname",null));
        txtpalce = (EditText) parentHolder.findViewById(R.id.txtpalce);
        chktag=(CheckBox) parentHolder.findViewById(R.id.chktag);
        btnstUpdate=(Button)parentHolder.findViewById(R.id.btnstUpdate);
        btnTakephoto=(Button)parentHolder.findViewById(R.id.btnTakephoto);
        ivImage=(ImageView) parentHolder.findViewById(R.id.ivImage);
        my_linear_layout1 = (LinearLayout)parentHolder.findViewById(R.id.my_linear_layout1);
        pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        usercode =getUserCode();;

        btnstUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                saveStarttravel();

            }
        });
        btnTakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the function to select image from album
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED)
                {
                }
                imageselect=1;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        captureImage();
                    } else {
                        captureImage2();
                    }
                }
                catch (Exception ex) {
                    Log.d("Msg",ex.getMessage());
                    msclass.showMessage("Something went wrong, please try again later");
                }
            }
        });

      /*  boolean flag2 = mDatabase.isTableExists("mdo_addplace");
        if (flag2 == false) {
            mDatabase.CreateTable6();
        }*/



        return parentHolder;
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




    private void updateLocationUI() {
        try {
            if (mCurrentLocation != null) {


                starttravel.address =getCompleteAddressString(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                starttravel.cordinate=mCurrentLocation.getLatitude() + "-" + mCurrentLocation.getLongitude();
                endtravel.address =getCompleteAddressString(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                endtravel.cordinate=mCurrentLocation.getLatitude() + "-" + mCurrentLocation.getLongitude();

                cordinate = mCurrentLocation.getLatitude() + "-" + mCurrentLocation.getLongitude();
                address = getCompleteAddressString(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            }
        }
        catch(Exception ex)
        {
            msclass.showMessage("Function Name-updateLocationUI "+ex.toString());
        }


    }







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
        }
        catch (Exception ex) {
            Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
    }
    private void captureImage()
    {

        try {

            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA}, 0);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    try {
                        if (imageselect==1) {
                            photoFile = createImageFile();

                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(context,
                                        "com.newtrial.mahyco.trail"+ ".provider",
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
        }

        catch (Exception ex)
        {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg",ex.getMessage());
            dialog.dismiss();
        }
    }
    private File createImageFile4() //  throws IOException
    {    File mediaFile=null;
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
        }
        catch (Exception ex) {
            Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
        return mediaFile;
    }
    private File createImageFile()
    {
        // Create an image file name
        File image=null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        }
        catch (Exception ex)
        {
            Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
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
        }
        catch (Exception e) {
            Log.d("Msg",e.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
    }
    private void onCaptureImageResult(Intent data) {


        try {
            if (imageselect == 1) {

                try {
                    /*BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize =2;

                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(),options);
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
                    AppConstant.Imagename=this.getClass().getSimpleName()+usercode+String.valueOf(entrydate.getTime()) ;
                    FileUtilImage.compressImageFile( AppConstant.queryImageUrl, AppConstant.imageUri,
                            getActivity(),AppConstant.Imagename);
                    // need to set commpress image path
                    Imagepath1 = FileUtilImage.savefilepath;// photoFile.getAbsolutePath();  old ssave
                    ivImage.setImageBitmap(myBitmap);
                    //************** 30 Dec 2020
                    /*------------------------------End -----------------------*/
                }
                catch (Exception e) {
                    msclass.showMessage("Something went wrong, please try again later");
                    Log.d("Msg",e.getMessage());
                }
                //end
            }

        }
        catch (Exception e) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg",e.getMessage());
        }

    }

    private void onSelectFromGalleryResult(Intent data) {


        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), data.getData());
            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
            }
        }

        if (imageselect==1) {
            ivImage.setImageBitmap(bm);
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
                    userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);  *//*changed on 6th Jan 2021*//*
                    Log.d("userCode", "Start travel userCode decrypt" + userCode);*/
                } while (data1.moveToNext());

            }
            data1.close();
        }
        return userCode;
    }

    public void saveStarttravel()
    {
        try
        {
            if (turnGPSOn() == false) {
                msclass.showMessage("GPS is not enabled,Please on GPS");
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            } else {
                if (validation() == true) {


                try {


                    Savedata("", state, dist, taluka, village);
                }
                catch (Exception ex) {
                    Log.d("Msg",ex.getMessage());
                    msclass.showMessage("Something went wrong, please try again later");
                }
                 }
            }
        }
        catch (Exception ex) {
            Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");

        }


    }
    private boolean validation()
    {
        try {
            boolean flag = true;
           if (txtpalce.getText().length() == 0) {
                msclass.showMessage("Please enter location(place).");
                return false;

            }
            if(chktag.isChecked() ==false)
            {
                msclass.showMessage("Please select geo tag");
                return false;

            }
            if(cordinate.length()==0)
            {
                msclass.showMessage(" GPS Location not found ,please check GPS location setting .");
                return false;
            }


        }catch (Exception ex)
        {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg",ex.getMessage());

        }
        return true;
    }

    private  void Savedata(final String myactvity, String state, String dist, String taluka, String village)
    {
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date d=new Date();
            String strdate=dateFormat.format(d);
            JSONObject object2 = new JSONObject();
            object2.put("Table2", mDatabase.getResults("select  * from mdo_starttravel  " +
                    "where strftime( '%Y-%m-%d', startdate)='"+strdate+"' and mdocode='"+usercode+"'"));
            JSONArray jArray2 = object2.getJSONArray("Table2");//new JSONArray(result);
            int startreading=0;
            if(jArray2.length()>0) {
                Date entrydate = new Date();
                final String InTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entrydate);

                final String Tempimagepath1;
                final String Imagename = "";
              //  final String Imagename = "STravel" + usercode + String.valueOf(entrydate.getTime());
                Tempimagepath1 = Imagepath1;
                //Tempimagepath1 = ""; /*Changed on 6th Jan 2021*/

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getActivity());
                // Setting Dialog Title
                alertDialog.setTitle("Trial Data Management");
                // Setting Dialog Message
                alertDialog.setMessage("Are you sure to save this data ");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do do my action here
                        String crop = "";
                        String product = "";
                        boolean fl = mDatabase.insertAddplace(usercode,
                                txtpalce.getText().toString(), cordinate, address, InTime, Imagename, Tempimagepath1);
                        if (fl == true) {
                            try {
                                msclass.showMessage("Save Input data successfully");
                                txtpalce.setText("");
                            } catch (Exception ex) {
                                msclass.showMessage("Something went wrong, please try again later");
                                Log.d("MSG","MSG : "+ ex.getMessage());
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
            }
            else
            {
                msclass.showMessage("Tour is not started ,please fill tour started data.");
            }

        }
        catch (Exception ex)
        {   Log.d("Msg",ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
            //msclass.showMessage(ex.getMessage());
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

    private boolean turnGPSOn(){
        boolean flag=true;

        return  flag;
    }



}
