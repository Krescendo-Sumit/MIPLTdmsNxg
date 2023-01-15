package com.tdms.mahyco.nxg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.multidex.BuildConfig;

import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.DecimalDigitsInputFilter;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.FileUtilImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import com.ceylonlabs.imageviewpopup.ImagePopup;

public class Testimonial extends AppCompatActivity implements LocationListener {
    ListView list;
    ListAdapter adapter;
    Toolbar toolbar;
    public Messageclass msclass;
    public CommonExecution cx;
    String imageFileName;
    //public  cx.getCrop gcrop;
    Config config;
    ProgressDialog dialog;
    public LinearLayout my_linear_layout1;
    public ScrollView container;
    SharedPreferences locdata;
    SharedPreferences.Editor editor, locedit;
    databaseHelper databaseHelper1;
    private double longitude;
    private double latitude;
    LocationManager locationManager;
    public String userCode;
    public TextView txtFname, txtTrialCode, txtProduct, txtLocation, txtSowing, txtPlotNo, txtENDPlotNo, txtPlotNo1, lblCordinate, txtStartPlotNo;
    public Button Photo, save, next, previous, btnGo;
    public String last;
    EditText txtEnerPlot;
    public String SERVER = "http://farm.mahyco.com/IPM.ashx";
    private static final String AUTHORITY =
            BuildConfig.APPLICATION_ID + ".provider";
    private static final String IMAGE_DIRECTORY_NAME = "VISITPHOTO";
    File photoFile = null;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView ivImage;
    private String imagepath1;
    FragmentManager fragmentManager;

    /*Added on 11 August*/
    int totalEditText = 100;
    EditText edPlot[] = new EditText[totalEditText];
    boolean[] selectedPlot;
    ArrayList<Integer> plotList = new ArrayList<Integer>();
    String[] plotArray = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10", "P11", "P12", "P13", "P14", "P15",
            "P16", "P17", "P18", "P19", "P20", "P21", "P22", "P23", "P24", "P25", "P26",
            "P27", "P28", "P29", "P30", "P31", "P32", "P33", "P34", "P35", "P36", "P37",
            "P38", "P39", "P40", "P41", "P42", "P43", "P45", "P45", "P46", "P47", "P48",
            "P49", "P50", "P51", "P52", "P53", "P54", "P55", "P56", "P57", "P58", "P59",
            "P60", "P61", "P62", "P63", "P64", "P65", "P66", "P67", "P68", "P69", "P70",
            "P71", "P72", "P73", "P74", "P75", "P76", "P77", "P78", "P79", "P80",
            "P81", "P82", "P83", "P84", "P85", "P86", "P87", "P88", "P89", "P90", "P91",
            "P92", "P93", "P94", "P94", "P95", "P96", "P97", "P98", "P99", "P100"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonial);
        getSupportActionBar().setTitle("Observations ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); /*Added on 19 April 2021*/
        config = new Config(this); //Here the context is passing
        msclass = new Messageclass(this);
        cx = new CommonExecution(this);
        databaseHelper1 = new databaseHelper(this);
        my_linear_layout1 = (LinearLayout) findViewById(com.tdms.mahyco.nxg.R.id.my_linear_layout1);
        container = (ScrollView) findViewById(com.tdms.mahyco.nxg.R.id.container);
        dialog = new ProgressDialog(this);
        final MediaPlayer MP = MediaPlayer.create(this, R.raw.ting);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        locdata = getApplicationContext().getSharedPreferences("locdata", 0); // 0 - for private mode
        locedit = locdata.edit();
        // Bindtestimonial("");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //Bind Data To Lable
        txtTrialCode = (TextView) findViewById(R.id.txtTrialCode);
        txtFname = (TextView) findViewById(R.id.txtFname);
        txtProduct = (TextView) findViewById(R.id.txtProduct);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtSowing = (TextView) findViewById(R.id.txtSowing);
        txtPlotNo = (TextView) findViewById(R.id.txtPlotNo);
        txtENDPlotNo = (TextView) findViewById(R.id.txtENDPlotNo);
        txtPlotNo1 = (TextView) findViewById(R.id.txtPlotNo1);
        txtStartPlotNo = (TextView) findViewById(R.id.txtstartplot);
        lblCordinate = (TextView) findViewById(R.id.lblCordinate);
        // final ImagePopup imagePopup = new ImagePopup(this);
        Photo = (Button) findViewById(R.id.btnPhoto);
        save = (Button) findViewById(R.id.btnSaveObs);
        next = (Button) findViewById(R.id.btnNextObs);
        previous = (Button) findViewById(R.id.btnPreObs);
        btnGo = (Button) findViewById(R.id.btnGo);

        txtEnerPlot = (EditText) findViewById(R.id.txtEnerPlot);

        ivImage = (ImageView) findViewById(R.id.ivImage);

        Intent i = getIntent();
        String name = i.getStringExtra("trailCode");
        txtTrialCode.setText(name);
        String crop = null;
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

        Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount() == 0) {
            msclass.showMessage("No Data Available... ");
        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    //userCode = data.getString((data.getColumnIndex("user_code")));
                    String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                    userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("userCode", "userCode" + userCode);
                } while (data.moveToNext());

            }
            data.close();
        }
        try {

            Cursor Farmerdata1 = databaseHelper1.ForTakenobservation(txtTrialCode.getText().toString());
            if (Farmerdata1.getCount() == 0) {
                last = "";

            } else {
                Farmerdata1.moveToFirst();
                if (Farmerdata1 != null) {
                    do {
                        last = Farmerdata1.getString((Farmerdata1.getColumnIndex("PlotNo")));

                    } while (Farmerdata1.moveToNext());
                }
                Farmerdata1.close();
            }


            //// Data TO Show On Screen

            Cursor Farmerdata = databaseHelper1.Forobservation(txtTrialCode.getText().toString());
            if (Farmerdata.getCount() == 0) {
                //msclass.showMessage("No Data Available... ");
            } else {
                Farmerdata.moveToFirst();
                if (Farmerdata != null) {
                    do {
                        //Fname ,Fvillage ,Fmobile ,FstartNote ,Fsowingdate
                        //txtNoReplecation = data.getInt(0);
                        txtFname.setText(Farmerdata.getString(Farmerdata.getColumnIndex("Fname")));
                        txtProduct.setText(Farmerdata.getString(Farmerdata.getColumnIndex("Product")));
                        txtLocation.setText(Farmerdata.getString(Farmerdata.getColumnIndex("Fvillage")));
                        txtSowing.setText(Farmerdata.getString(Farmerdata.getColumnIndex("Fsowingdate")));
                        txtPlotNo.setText(Farmerdata.getString(Farmerdata.getColumnIndex("StartPlotNo")));
                        txtStartPlotNo.setText(Farmerdata.getString(Farmerdata.getColumnIndex("StartPlotNo")));

//                        if (last.equals("")) {
//                            txtPlotNo.setText(Farmerdata.getString(Farmerdata.getColumnIndex("StartPlotNo")));
//                        } else {
//                            txtPlotNo.setText(last);
//                        }
                        crop = Farmerdata.getString((Farmerdata.getColumnIndex("CROP")));
                        txtENDPlotNo.setText(Farmerdata.getString((Farmerdata.getColumnIndex("EndPlotNo"))));

                        Log.d("OBS", "BACK Pressed : START PLOT : " + txtStartPlotNo.getText().toString());
                        Log.d("OBS", "BACK Pressed : END PLOT : " + txtENDPlotNo.getText().toString());


                      /*As not required SRS 16 Jan 2022*/
                        ////Alert box
                        /*AlertDialog.Builder builder = new AlertDialog.Builder(Testimonial.this);
                        builder.setMessage("Farmer Name : " + "" + Farmerdata.getString(Farmerdata.getColumnIndex("Fname")) +
                                "\n\nVillage : " + "" + Farmerdata.getString(Farmerdata.getColumnIndex("Fvillage")) + "" +
                                "\n\nSowing Date : " + "" + Farmerdata.getString(Farmerdata.getColumnIndex("Fsowingdate")));
                        //  msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing, but close the dialog
                                // BindData(crop, true);
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                              Observation fragment = new Observation();
//                                              FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                              transaction.replace(R.id.FragmentContainer, fragment);
//                                              transaction.commit();
                            }
                        });*/
                        ////Alert box end


                    } while (Farmerdata.moveToNext());
                }
                Farmerdata.close();
            }
            //// Data TO Show On Screen End
            if (txtPlotNo.equals(txtENDPlotNo.getText().toString())) {
                msclass.showMessage("All Plot Observation Are taken ");
            }
            final String Tcode = txtTrialCode.getText().toString();
            //final String Plot = txtPlotNo.getText().toString();


            //End Bind Data To Lable
            BindData(crop, true);

            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Testimonial.this);
                    LayoutInflater inflater = Testimonial.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.alert_label_editor, null);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setPositiveButton("OK", null);
                    ImageView mImageView = (ImageView) dialogView.findViewById(R.id.img);
                    if (ivImage.getDrawable() != null) {
                        mImageView.setImageDrawable(ivImage.getDrawable());

                    }
                    AlertDialog alertDialog = dialogBuilder.create();

                    alertDialog.show();
                }
            });
            final String finalCrop = crop;
            btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // To Go Direct Plot No
                    String s = txtEnerPlot.getText().toString();
                    if (s != null && !s.isEmpty()) {

                        String GoPlotNo = txtEnerPlot.getText().toString().trim();
                        imageFileName = null;
                        imagepath1 = null;
                        ivImage.setImageBitmap(null);
                        int UpdatePlot;
                        UpdatePlot = Integer.parseInt(GoPlotNo.toString());

                        String plot1 = txtPlotNo.getText().toString();
                        Log.d("testimonial", "onClick: " + plot1);
                        if (UpdatePlot < Integer.parseInt(txtPlotNo.getText().toString())) {
                            msclass.showMessage("This Plot No Is Not Available. \nPlease Check Plot No You Enter");
                        } else if (UpdatePlot > Integer.parseInt(txtENDPlotNo.getText().toString())) {
                            msclass.showMessage("This Plot No Is Not Available.  \nPlease Check Plot No You Enter");
                        } else {
                            txtPlotNo.setText(String.valueOf(UpdatePlot));
                            txtEnerPlot.setText("");

                            BindData(finalCrop, false);
                        }

                    }


                }
            });

            Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        captureImage();
                    } else {
                        captureImage2();
                    }
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isImageSaved = false;
//                    if (ivImage.getDrawable() == null) {
//                        msclass.showMessage("Please Upload Image");
//                        return;
//                    }
                    // EditText ed;
                    //Spinner Spin;
                    if (validation() == true) {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String entrydate = sdf.format(new Date());
                        try {
                            int count = my_linear_layout1.getChildCount();
                            //EditText ed[] = new EditText[count];
                            for (int i = 0; i < count; i++) {
                                if (isImageSaved) {
                                    imageFileName = null;
                                    imagepath1 = null;

                                }
                                View row = my_linear_layout1.getChildAt(i);

                                TextView text = (TextView) row.findViewById(R.id.OBlblName);  // Lable to show Observation
                                TextView lbls = (TextView) row.findViewById(R.id.lbls);        // Lable For : to Show
                                TextView ObsVariable = (TextView) row.findViewById(R.id.ObsVariableID);  // Lable To Store Variable ID
                                TextView ObsordinalCondition = (TextView) row.findViewById(R.id.ObsordinalCondition); // TO find condition For Spinner
                                TextView ObsMultiple = (TextView) row.findViewById(R.id.ObsMultiple);// To find condition FOr Multiple Edit Box

                                String OBSCondition = ObsordinalCondition.getText().toString();
                                String ObsM = ObsMultiple.getText().toString();
                                String VariableID = ObsVariable.getText().toString();
                                Log.d("Testimonial", "onClick:VariableID " + VariableID);
                                Log.d("Testimonial", "onClick:OBSCondition " + OBSCondition);
                                Log.d("Testimonial", "onClick:ObsM : " + ObsM);
                                Cursor Farmerdata2 = databaseHelper1.getObservationData(txtPlotNo.getText().toString(),
                                        txtTrialCode.getText().toString(),
                                        VariableID);
                                int text1 = Farmerdata2.getCount();


                                // For remark 14-09-2020
                                if (OBSCondition.equals("Comment")) {
                                    Log.d("Remark", "OBSCondition : " + OBSCondition);
                                    EditText txtRemark = (EditText) row.findViewById(R.id.txtComment);
                                    String remark = txtRemark.getText().toString();
                                    Log.d("Remark", "Remark : " + remark);
                                    if (remark.matches("")) {
                                        Log.d("Remark", "Remark NULL");
                                    } else {
                                        Log.d("Remark", "Remark EXIST");
                                        if (text1 == 0) {
                                            String imgName = "";
                                            if (imageFileName != null && !imageFileName.contains("null")) {
                                                imgName = imageFileName;
                                            }
                                            String imgPath = "";
                                            if (imagepath1 != null && !imagepath1.contains("null")) {
                                                imgPath = imagepath1;
                                            }
                                            String InsertQuery = "insert into Observationtaken (VariableID,TRIAL_CODE,PlotNo,Value1,Date,Flag,usercode,cordinate,ImageName,Image,ImageSyncStatus)values ( '"
                                                    + VariableID + "' , '" + Tcode + "', '" + txtPlotNo.getText().toString()
                                                    + "', '" + remark + "','" + entrydate + "','0','" + userCode + "','" +
                                                    lblCordinate.getText().toString() + "'"

                                                    + ",'" + imgName + "','"
                                                    + imgPath + "','0')";

                                            Log.d("Remark", "Remark InsertQuery: " + InsertQuery);

                                            databaseHelper1.runQuery(InsertQuery);
                                            isImageSaved = true;
                                        } else {

                                        }
                                    }
                                }
                                // end for remark 14-09-2020
                                else if (OBSCondition.equals("ordinal")) {
                                    //This is For Spinner

                                    Spinner Spin = (Spinner) row.findViewById(R.id.SPNOBS);         //Spinner to Select Data

                                    String SPN = Spin.getSelectedItem().toString();
                                    String imgName = "";

                                    if (imageFileName != null && !imageFileName.contains("null")) {
                                        imgName = imageFileName;
                                    }
                                    String imgPath = "";
                                    if (imagepath1 != null && !imagepath1.contains("null")) {
                                        imgPath = imagepath1;
                                    }
                                    if (SPN.matches("Select")) {

                                    } else {
                                        if (text1 == 0) {
                                            String InsertQuery = "insert into Observationtaken (VariableID,TRIAL_CODE,PlotNo,Value1,Date,Flag,usercode,cordinate,ImageName,Image,ImageSyncStatus)values " +
                                                    "( '" + VariableID + "' , '" + Tcode + "', '" + txtPlotNo.getText().toString() + "', '" +
                                                    SPN + "','" + entrydate + "','0','" + userCode + "','" +
                                                    lblCordinate.getText().toString() + "'"

                                                    + ",'" + imgName + "','"
                                                    + imgPath + "','0')";
                                            databaseHelper1.runQuery(InsertQuery);
                                            isImageSaved = true;
                                        } else {

                                        }
                                    }
                                } else if (OBSCondition.equals("continuous")) {
                                    //This is for Edit box
                                    Log.d("onClick", "SAVE : OBSCondition: " + OBSCondition);
                                    Log.d("onClick", "SAVE : ObsM: " + OBSCondition);
                                    if (ObsM.equals("S")) {
                                        //This is For Single EditBox

                                        EditText OBSpn = (EditText) row.findViewById(R.id.OBSpn); // TextBox To Enter Field
                                        //String VariableID = ObsVariable.getText().toString();
                                        String OBSpn1 = OBSpn.getText().toString();

                                        if (OBSpn1.matches("")) {

                                        } else {
                                            if (text1 == 0) {
                                                String imgName = "";
                                                if (imageFileName != null && !imageFileName.contains("null")) {
                                                    imgName = imageFileName;
                                                }
                                                String imgPath = "";
                                                if (imagepath1 != null && !imagepath1.contains("null")) {
                                                    imgPath = imagepath1;
                                                }
                                                String InsertQuery = "insert into Observationtaken (VariableID,TRIAL_CODE,PlotNo,Value1,Date,Flag,usercode,cordinate,ImageName,Image,ImageSyncStatus)values ( '"
                                                        + VariableID + "' , '" + Tcode + "', '" + txtPlotNo.getText().toString()
                                                        + "', '" + OBSpn1 + "','" + entrydate + "','0','" + userCode + "','" +
                                                        lblCordinate.getText().toString() + "'"

                                                        + ",'" + imgName + "','"
                                                        + imgPath + "','0')";
                                                Log.d("TDMS", "SAVE Image & Details Query : " + InsertQuery);
                                                databaseHelper1.runQuery(InsertQuery);
                                                isImageSaved = true;
                                            } else {
                                                Log.d("TDMS", "NO Insert Query");
                                            }
                                        }
                                    } else if (ObsM.equals("M")) {
                                        //This is For MultiEditBox

                                        EditText txt1 = (EditText) row.findViewById(R.id.txt1);
                                        EditText txt2 = (EditText) row.findViewById(R.id.txt2);
                                        EditText txt3 = (EditText) row.findViewById(R.id.txt3);
                                        EditText txt4 = (EditText) row.findViewById(R.id.txt4);
                                        EditText txt5 = (EditText) row.findViewById(R.id.txt5);

                                        String OBtxt1 = txt1.getText().toString();
                                        String OBtxt2 = txt2.getText().toString();
                                        String OBtxt3 = txt3.getText().toString();
                                        String OBtxt4 = txt4.getText().toString();
                                        String OBtxt5 = txt5.getText().toString();
                                        if (OBtxt1.matches("") || OBtxt2.matches("") || OBtxt3.matches("") || OBtxt4.matches("") || OBtxt5.matches("")) {

                                        } else {
                                            //String VariableID = ObsVariable.getText().toString();
                                            if (text1 == 0) {
                                                String imgName = "";
                                                if (imageFileName != null && !imageFileName.contains("null")) {
                                                    imgName = imageFileName;
                                                }
                                                String imgPath = "";
                                                if (imagepath1 != null && !imagepath1.contains("null")) {
                                                    imgPath = imagepath1;
                                                }
                                                String InsertQuery = "insert into Observationtaken " +
                                                        "(VariableID,TRIAL_CODE,PlotNo,Value1," +
                                                        "Value2,Value3,Value4,Value5,Date,Flag,usercode,cordinate,ImageName,Image,ImageSyncStatus)values" +
                                                        " ( '" + VariableID + "' , '" + Tcode + "', '" +
                                                        txtPlotNo.getText().toString() + "', '" + OBtxt1 + "'" +
                                                        ", '" + OBtxt2 + "', '" + OBtxt3 + "', '" + OBtxt4 + "', '"
                                                        + OBtxt5 + "','" + entrydate + "','0','" + userCode + "','" +
                                                        lblCordinate.getText().toString() + "'"

                                                        + ",'" + imgName + "','"
                                                        + imgPath + "','0')";
                                                databaseHelper1.runQuery(InsertQuery);
                                                isImageSaved = true;

                                            } else {

                                            }


                                        }
                                    }
                                } else if (OBSCondition.equals("special_multiple")) { /*Updated on 10th August 2021*/
                                    /*Toast.makeText(Testimonial.this, "Scale :" + OBSCondition
                                            , Toast.LENGTH_SHORT).show();*/
                                    JSONObject objSPM = new JSONObject();
                                    JSONArray array = new JSONArray();
                                    int isJsonComplete = 0;
                                    //Create JSON Object START
                                    if (plotList.size() > 0) {
                                        for (i = 0; i < plotList.size(); i++) {
                                            Log.d("Edit Text Value ", "Tag:" + edPlot[i].getTag() + " Val:" + edPlot[i].getText());
                                            if (edPlot[i] != null) {
                                                if (edPlot[i].getText().toString().equals("")) {
                                                    //edPlot[i].requestFocus();
                                                    //isJsonComplete = 1;
                                                    //Toast.makeText(Testimonial.this, "Enter valid data", Toast.LENGTH_SHORT).show();
                                                    //break;
                                                    continue;
                                                } else {
                                                    JSONObject object = new JSONObject();
                                                    //Commented on 19th Aug 2021
                                                    //object.put(edPlot[i].getTag().toString(), edPlot[i].getText().toString());
                                                    /*Changes done as suggested by Junaid*/
                                                    object.put("PlotKey", edPlot[i].getTag().toString());
                                                    object.put("PlotValue", edPlot[i].getText().toString());
                                                    array.put(object);
                                                }
                                            }
                                        }
                                        objSPM.put("Table", array.toString());
                                        Log.d("SPM OBJ", "Complete Object : " + objSPM.toString());
                                        //Create JSON Object END

                                        /*SAVE START*/
                                        if (isJsonComplete == 0) {
                                            if (text1 == 0) {
                                                String imgName = "";
                                                if (imageFileName != null && !imageFileName.contains("null")) {
                                                    imgName = imageFileName;
                                                }
                                                String imgPath = "";
                                                if (imagepath1 != null && !imagepath1.contains("null")) {
                                                    imgPath = imagepath1;
                                                }
                                                String InsertQuery = "insert into Observationtaken " +
                                                        "(VariableID,TRIAL_CODE,PlotNo,Value1," +
                                                        "Value2,Value3,Value4,Value5,Date,Flag,usercode,cordinate,ImageName,Image,ImageSyncStatus,ValueSPM)values" +
                                                        " ( '" + VariableID + "' , '" + Tcode + "', '" +
                                                        txtPlotNo.getText().toString() + "', '" + "" + "'" +
                                                        ", '" + "" + "', '" + "" + "', '" + "" + "', '"
                                                        + "" + "','" + entrydate + "','0','" + userCode + "','" +
                                                        lblCordinate.getText().toString() + "'"
                                                        + ",'" + imgName + "','"
                                                        + imgPath + "','0','" + objSPM.toString() + "')";

                                                Log.d("SPM OBJ", "InsertQuery : " + InsertQuery);
                                                databaseHelper1.runQuery(InsertQuery);
                                                isImageSaved = true;

                                                //Clear data for future fresh use
                                                if (plotList != null && plotList.size() > 0) {
                                                    plotList.clear();
                                                }
                                            }
                                        }
                                    }
                                    /*SAVE END*/
                                }

                                // Save Observation End
                            }

                            if (imageFileName == null) {
                                ivImage.setImageDrawable(null);
                            }

                            //msclass.showMessage("Observation Saved ");

                            //               AlertDialog.Builder builder = new AlertDialog.Builder(Testimonial.this);

                            //               builder.setTitle("Mahyco PD");

                            //              builder.setMessage("Observation Saved.\n\nAre you Want to Record Next plot?");

                            //                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            //                   public void onClick(DialogInterface dialog, int which) {

                            // Do nothing, but close the dialog
                            int UpdatePlot;
                            UpdatePlot = Integer.parseInt(txtPlotNo.getText().toString()) + Integer.parseInt("1");
                            if (UpdatePlot > Integer.parseInt(txtENDPlotNo.getText().toString())) {
                                msclass.showMessage("This is Last Plot You Cant Go Next ");
                            } else {
                                txtPlotNo.setText(String.valueOf(UpdatePlot));
                                BindData(finalCrop, false);
                            }
                            //                       dialog.dismiss();
                            //                  }
                            //               });

                            //              builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            //                   @Override
                            //                   public void onClick(DialogInterface dialog, int which) {
//
                            // Do nothing
                            //                     dialog.dismiss();
                            //                 }
                            //              });

                            //              AlertDialog alert = builder.create();
                            //              alert.show();
                            MP.start();
                        } catch (Exception e) {
                            Log.d("Msg", e.getMessage());
                        }
                    }
                }
            });

            ////

            txtPlotNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(Testimonial.this, AndroidDatabaseManager.class);

                    startActivity(intent);
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int UpdatePlot;
                    UpdatePlot = Integer.parseInt(txtPlotNo.getText().toString()) + Integer.parseInt("1");
                    if (UpdatePlot > Integer.parseInt(txtENDPlotNo.getText().toString())) {
                        msclass.showMessage("This is Last Plot You Cant Go Next ");
                    } else {
                        txtPlotNo.setText(String.valueOf(UpdatePlot));
                        BindData(finalCrop, false);
                    }
                    // Intent intent = getIntent();
                    // finish();
                    //startActivity(intent);
                }
            });
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int UpdatePlot;
                    UpdatePlot = Integer.parseInt(txtPlotNo.getText().toString()) - Integer.parseInt("1");
                    Log.d("OBS", "BACK Pressed : UpdatePlot : " + UpdatePlot);
                    Log.d("OBS", "BACK Pressed : Start Plot : " + txtStartPlotNo.getText().toString());
                    Log.d("OBS", "BACK Pressed : txtPlotNo : " + txtPlotNo.getText().toString());
                    if (!txtStartPlotNo.getText().toString().equalsIgnoreCase("")) {
                        Log.d("OBS", "BACK Pressed : COMPARE UpdatePlot < Integer.parseInt(txtPlotNo1.getText().toString()) : " + (UpdatePlot < Integer.parseInt(txtStartPlotNo.getText().toString())));
                        if (UpdatePlot < Integer.parseInt(txtStartPlotNo.getText().toString())) {
                            msclass.showMessage("This is Starting Plot You Cant Go Previous ");
                        } else {
                            txtPlotNo.setText(String.valueOf(UpdatePlot));
                            BindData(finalCrop, false);

                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());

        }


    }


    private boolean validation() {
        boolean flag = true;
        try {
            int count = my_linear_layout1.getChildCount();
            //EditText ed[] = new EditText[count];
            for (int i = 0; i < count; i++) {
                View row = my_linear_layout1.getChildAt(i);

                TextView text = (TextView) row.findViewById(R.id.OBlblName);  // Lable to show Observation
                TextView lbls = (TextView) row.findViewById(R.id.lbls);        // Lable For : to Show
                TextView ObsVariable = (TextView) row.findViewById(R.id.ObsVariableID);  // Lable To Store Variable ID
                TextView ObsordinalCondition = (TextView) row.findViewById(R.id.ObsordinalCondition); // TO find condition For Spinner
                TextView ObsMultiple = (TextView) row.findViewById(R.id.ObsMultiple);// To find condition FOr Multiple Edit Box


                String OBSCondition = ObsordinalCondition.getText().toString();
                String ObsM = ObsMultiple.getText().toString();
                String VariableID = ObsVariable.getText().toString();
                if (OBSCondition.equals("ordinal")) {
                    //This is For Spinner

                    Spinner Spin = (Spinner) row.findViewById(R.id.SPNOBS);         //Spinner to Select Data
                    String SPN = Spin.getSelectedItem().toString();

                    if (SPN.equals("Select")) {
                        //msclass.showMessage("Please Select Observation Value ! ");
                        // return false;
                    }

                }
                //for remark 14-09-2020
                else if (OBSCondition.equals("Comment")) {
                    EditText txtcomment = (EditText) row.findViewById(R.id.txtComment);
                    String comment = txtcomment.getText().toString();
                }
                //end for remark 14-09-2020
                else if (OBSCondition.equals("continuous")) {
                    //This is for Edit box

                    if (ObsM.equals("S")) {
                        //This is For Single EditBox

                        EditText OBSpn = (EditText) row.findViewById(R.id.OBSpn); // TextBox To Enter Field
                        String OBSpn1 = OBSpn.getText().toString();
                        if (OBSpn1.equals("")) {
                            //msclass.showMessage("Please Enter Observation Value ! ");
                            //OBSpn.setError("Fill This");
                            //return false;
                        }
                        try {
                            int val = Integer.parseInt(OBSpn.getText().toString());
                            Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariable.getText().toString());
                            int text1 = Farmerdata3.getCount();
                            if (text1 != 0) {
                                Farmerdata3.moveToFirst();
                                if (Farmerdata3 != null) {
                                    do {
                                        int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                        int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                        if (val > max) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            OBSpn.setText("");
                                            OBSpn.setError("Fill This");
                                            return false;
                                            // Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        } else if (val < min) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            OBSpn.setText("");
                                            OBSpn.setError("Fill This");
                                            return false;
                                            //Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        }

                                    } while (Farmerdata3.moveToNext());
                                }
                            }
                        } catch (NumberFormatException ex) {
                        }
                    } else if (ObsM.equals("M")) {
                        //This is For MultiEditBox

                        EditText txt1 = (EditText) row.findViewById(R.id.txt1);
                        EditText txt2 = (EditText) row.findViewById(R.id.txt2);
                        EditText txt3 = (EditText) row.findViewById(R.id.txt3);
                        EditText txt4 = (EditText) row.findViewById(R.id.txt4);
                        EditText txt5 = (EditText) row.findViewById(R.id.txt5);

                        String OBtxt1 = txt1.getText().toString();
                        String OBtxt2 = txt2.getText().toString();
                        String OBtxt3 = txt3.getText().toString();
                        String OBtxt4 = txt4.getText().toString();
                        String OBtxt5 = txt5.getText().toString();

                        if (OBtxt1.equals("") || OBtxt2.equals("") || OBtxt3.equals("") || OBtxt4.equals("") || OBtxt5.equals("")) {
//                            msclass.showMessage("Please Enter All 5 Value For Observation ! ");
//                            txt1.setError("Fill All 5 Value");
//                            txt2.setError("Fill All 5 Value");
//                            txt3.setError("Fill All 5 Value");
//                            txt4.setError("Fill All 5 Value");
//                            txt5.setError("Fill All 5 Value");
//                            return false;
                        }

                        try {
                            int val = Integer.parseInt(txt1.getText().toString());
                            Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariable.getText().toString());
                            int text1 = Farmerdata3.getCount();
                            if (text1 != 0) {
                                Farmerdata3.moveToFirst();
                                if (Farmerdata3 != null) {
                                    do {
                                        int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                        int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                        if (val > max) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            txt1.setText("");
                                            txt1.setError("Fill This");
                                            return false;
                                            // Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        } else if (val < min) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            txt1.setText("");
                                            txt1.setError("Fill This");
                                            return false;
                                            //Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        }

                                    } while (Farmerdata3.moveToNext());
                                }
                            }
                        } catch (NumberFormatException ex) {
                        }

                        try {
                            int val = Integer.parseInt(txt2.getText().toString());
                            Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariable.getText().toString());
                            int text1 = Farmerdata3.getCount();
                            if (text1 != 0) {
                                Farmerdata3.moveToFirst();
                                if (Farmerdata3 != null) {
                                    do {
                                        int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                        int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                        if (val > max) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            txt2.setText("");
                                            txt2.setError("Fill This");
                                            return false;
                                            // Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        } else if (val < min) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            txt2.setText("");
                                            txt2.setError("Fill This");
                                            return false;
                                            //Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        }

                                    } while (Farmerdata3.moveToNext());
                                }
                            }
                        } catch (NumberFormatException ex) {
                        }

                        try {
                            int val = Integer.parseInt(txt3.getText().toString());
                            Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariable.getText().toString());
                            int text1 = Farmerdata3.getCount();
                            if (text1 != 0) {
                                Farmerdata3.moveToFirst();
                                if (Farmerdata3 != null) {
                                    do {
                                        int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                        int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                        if (val > max) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            txt3.setText("");
                                            txt3.setError("Fill This");
                                            return false;
                                            // Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        } else if (val < min) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            txt3.setText("");
                                            txt3.setError("Fill This");
                                            return false;
                                            //Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        }

                                    } while (Farmerdata3.moveToNext());
                                }
                            }
                        } catch (NumberFormatException ex) {
                        }

                        try {
                            int val = Integer.parseInt(txt4.getText().toString());
                            Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariable.getText().toString());
                            int text1 = Farmerdata3.getCount();
                            if (text1 != 0) {
                                Farmerdata3.moveToFirst();
                                if (Farmerdata3 != null) {
                                    do {
                                        int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                        int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                        if (val > max) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            txt4.setText("");
                                            txt4.setError("Fill This");
                                            return false;
                                            // Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        } else if (val < min) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            txt4.setText("");
                                            txt4.setError("Fill This");
                                            return false;
                                            //Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        }

                                    } while (Farmerdata3.moveToNext());
                                }
                            }
                        } catch (NumberFormatException ex) {
                        }

                        try {
                            int val = Integer.parseInt(txt5.getText().toString());
                            Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariable.getText().toString());
                            int text1 = Farmerdata3.getCount();
                            if (text1 != 0) {
                                Farmerdata3.moveToFirst();
                                if (Farmerdata3 != null) {
                                    do {
                                        int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                        int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                        if (val > max) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            txt5.setText("");
                                            txt5.setError("Fill This");
                                            return false;
                                            // Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        } else if (val < min) {

                                            msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                            txt5.setText("");
                                            txt5.setError("Fill This");
                                            return false;
                                            //Toast.makeText(Testimonial.this, "Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'", Toast.LENGTH_LONG).show();
                                        }

                                    } while (Farmerdata3.moveToNext());
                                }
                            }
                        } catch (NumberFormatException ex) {
                        }
                    }
                }

                // Save Observation End
            }
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
        }
        return true;
    }

    public void BindData(String crop, boolean isFromOncreate) {

        /*RESET Image Details*/
        imageFileName = null;
        imagepath1 = null;
        ivImage.setImageBitmap(null);

        //Cursor Farmerdata = databaseHelper1.fetchdownloadedObservation(crop);   /*Commented on 15th July 2021*/
        String trialCodeData = txtTrialCode.getText().toString();
        Log.d("OBS BINDDATA", "Crop: " + crop + " Trial Code:" + trialCodeData);
        Cursor Farmerdata = databaseHelper1.fetchTrialWiseDownloadedObservation(crop, trialCodeData);     /*Added on 15th July 2021*/
        int row = Farmerdata.getCount();
        if (Farmerdata.getCount() == 0) {
            // msclass.showMessage("First Download Observation For This Trial ");

        } else {
            try {
                if (!isFromOncreate) {
                    my_linear_layout1.removeAllViews();
                }

                Farmerdata.moveToFirst();
                if (Farmerdata != null) {
                    do {

                        View view2 = LayoutInflater.from(this).inflate(R.layout.observation_list, null);
                        ViewGroup parent = (ViewGroup) view2.getParent();
                        if (parent != null) {
                            parent.removeView(view2);
                        }
                        view2.setBackgroundColor(getResources().getColor(R.color.Whitecl));
                        TextView lblname = (TextView) view2.findViewById(com.tdms.mahyco.nxg.R.id.OBlblName);
                        final TextView ObsVariableID = (TextView) view2.findViewById(com.tdms.mahyco.nxg.R.id.ObsVariableID);
                        TextView ObsordinalCondition = (TextView) view2.findViewById(com.tdms.mahyco.nxg.R.id.ObsordinalCondition);
                        TextView ObsMultiple = (TextView) view2.findViewById(com.tdms.mahyco.nxg.R.id.ObsMultiple);

                        /*Added on 10th Aug 2021*/
                        LinearLayout layoutSpecialMultiple = (LinearLayout) view2.findViewById(R.id.layout_sp_mtl);
                        LinearLayout layoutSpTv = (LinearLayout) view2.findViewById(R.id.layout_sp_tv);
                        RadioButton rbSpMlAll = (RadioButton) view2.findViewById(R.id.rb_sp_ml_all);
                        RadioButton rbSpMlManual = (RadioButton) view2.findViewById(R.id.rb_sp_ml_manual);
                        RadioGroup rgSpMl = (RadioGroup) view2.findViewById(R.id.rg_sp_ml);
                        TextView tvManual = (TextView) view2.findViewById(R.id.tv_manual);

                        final EditText obsField = (EditText) view2.findViewById(R.id.OBSpn);
                        final EditText txt1 = (EditText) view2.findViewById(R.id.txt1);
                        final EditText txt2 = (EditText) view2.findViewById(com.tdms.mahyco.nxg.R.id.txt2);
                        final EditText txt3 = (EditText) view2.findViewById(com.tdms.mahyco.nxg.R.id.txt3);
                        final EditText txt4 = (EditText) view2.findViewById(com.tdms.mahyco.nxg.R.id.txt4);
                        final EditText txt5 = (EditText) view2.findViewById(com.tdms.mahyco.nxg.R.id.txt5);

                        /*Added on 18th Jan 2021*/
                        txt1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        txt2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        txt3.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        txt4.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        txt5.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                        /*Added on 8th Jan 2021*/
                        txt1.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 3)});
                        txt2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 3)});
                        txt3.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 3)});
                        txt4.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 3)});
                        txt5.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 3)});


                        //For Remark 14-09-2020
                        final EditText txtcommnetbox = (EditText) view2.findViewById(R.id.txtComment);

                        //End for remark 14-09-2020

                        Spinner SPNOBS = (Spinner) view2.findViewById(R.id.SPNOBS);
                        lblname.setText(Farmerdata.getString(Farmerdata.getColumnIndex("Name")));

                        ObsordinalCondition.setText(Farmerdata.getString(Farmerdata.getColumnIndex("Scale_type")));
                        ObsMultiple.setText(Farmerdata.getString(Farmerdata.getColumnIndex("S_M")));

                        String scale = Farmerdata.getString(Farmerdata.getColumnIndex("Scale_type"));
                        String multiple = Farmerdata.getString(Farmerdata.getColumnIndex("S_M"));
                        String plotNo = Config.getValue(Testimonial.this, Constants.KEY_PLOT_NO);
                        String trialCode = Config.getValue(Testimonial.this, Constants.KEY_TRIAL_NO);
                        ObsVariableID.setText(Farmerdata.getString(Farmerdata.getColumnIndex("VariableID")));

                        String varId = Farmerdata.getString(Farmerdata.getColumnIndex("VariableID"));

                        Cursor Farmerdata2 = databaseHelper1.getObservationData(txtPlotNo.getText().toString(),
                                txtTrialCode.getText().toString(),
                                varId);

                        int text = Farmerdata2.getCount();
                        Log.d("Remark", "Remark text: " + text);

                        Log.d("OBS BINDDATA", "Variable ID : " + varId);
                        Log.d("OBS BINDDATA", "Scale Type : " + scale);


                        //For remark 14-09-2020
                        if (scale.equals("Comment")) {
                            Log.d("Remark", "Remark scale: " + scale);
                            if (Farmerdata2 != null) {
                                if (text != 0) {
                                    Farmerdata2.moveToFirst();
                                    String comment = Farmerdata2.getString(Farmerdata2.getColumnIndex("Value1"));
                                    Log.d("Remark", "Remark Value DB: " + comment);
                                    txtcommnetbox.setText(comment);
                                }
                            }

                            txtcommnetbox.setVisibility(View.VISIBLE);
                            SPNOBS.setVisibility(View.GONE);
                            obsField.setVisibility(View.GONE);
                            txt1.setVisibility(View.GONE);
                            txt2.setVisibility(View.GONE);
                            txt3.setVisibility(View.GONE);
                            txt4.setVisibility(View.GONE);
                            txt5.setVisibility(View.GONE);
                            layoutSpecialMultiple.setVisibility(View.GONE); /*Added on 10th Aug 2021*/
                        }
                        //end remark 14-09-2020
                        /*START Remark Added on 21st July 2021*/
                        /*else if(scale.equals("continuous")){
                            Toast.makeText(Testimonial.this,"Condition not handled",Toast.LENGTH_SHORT).show();
                            Log.d("Continue", "Continue data SCALE : " + scale);
                            Log.d("Continue", "Continue data COUNT : " + Farmerdata.getCount());
                            Log.d("Continue", "Continue data TEXT : " + text);

                            SPNOBS.setVisibility(View.GONE);
                            obsField.setVisibility(View.GONE);
                            txt1.setVisibility(View.GONE);
                            txt2.setVisibility(View.GONE);
                            txt3.setVisibility(View.GONE);
                            txt4.setVisibility(View.GONE);
                            txt5.setVisibility(View.GONE);
                        }*/
                        /*START Remark Added on 21st July 2021*/
                        else if (scale.equals("ordinal")) {
                            //This is for spinner
                            txtcommnetbox.setVisibility(View.GONE);
                            SPNOBS.setVisibility(View.VISIBLE); /*Updated on 29 July 2021 */
                            obsField.setVisibility(View.GONE);
                            txt1.setVisibility(View.GONE);
                            txt2.setVisibility(View.GONE);
                            txt3.setVisibility(View.GONE);
                            txt4.setVisibility(View.GONE);
                            txt5.setVisibility(View.GONE);
                            layoutSpecialMultiple.setVisibility(View.GONE); /*Added on 10th Aug 2021*/
                            // String VariableID = Farmerdata.getString(Farmerdata.getColumnIndex("VariableID"));

                            //SPNOBS.setId(Integer.parseInt(VariableID));
                            // ObsVariableID.setText(VariableID);

                            List<String> lables = databaseHelper1.getObservationValue
                                    (Farmerdata.getString(Farmerdata.getColumnIndex("VariableID")));

                            // Creating adapter for spinner
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);

                            // Drop down layout style - list view with radio button
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // attaching data adapter to spinner
                            SPNOBS.setAdapter(dataAdapter);

                            if (text != 0) {
                                Farmerdata2.moveToFirst();
                                if (Farmerdata2 != null) {
                                    do {
                                        int pos = dataAdapter.getPosition(Farmerdata2.getString(Farmerdata2.getColumnIndex("Value1")));
                                        SPNOBS.setSelection(pos);
                                        //  save.setEnabled(false);
                                        SPNOBS.setEnabled(false);

                                    } while (Farmerdata2.moveToNext());
                                } else {
                                    //  save.setEnabled(true);

                                }
                            }
                        } else if (scale.equals("special_multiple")) { /*Updated on 10th August 2021*/
                            /*START -------------------- Special Multiple Case  */

                            txtcommnetbox.setVisibility(View.GONE);
                            SPNOBS.setVisibility(View.GONE);
                            obsField.setVisibility(View.GONE);
                            txt1.setVisibility(View.GONE);
                            txt2.setVisibility(View.GONE);
                            txt3.setVisibility(View.GONE);
                            txt4.setVisibility(View.GONE);
                            txt5.setVisibility(View.GONE);
                            layoutSpecialMultiple.setVisibility(View.VISIBLE); /*Added on 10th Aug 2021*/

                            //If Already Observation taken, then show DATA from existing TABLE
                            if (text != 0) {
                                rgSpMl.setVisibility(View.GONE); //Not required as data already set
                                Farmerdata2.moveToFirst();
                                String valueSPM = Farmerdata2.getString(Farmerdata2.getColumnIndex("ValueSPM"));
                                Log.d("ValueSPM", "ValueSPM Value DB: " + valueSPM);
                                if (valueSPM != null) {
                                    JSONObject obj = new JSONObject(valueSPM);
                                    String table = obj.getString("Table");
                                    Log.d("ValueSPM", "ValueSPM table data: " + table);
                                    JSONArray jsonArray = new JSONArray(table);
                                    Log.d("ValueSPM", "ValueSPM jsonArray length size: " + jsonArray.length());
                                    layoutSpTv.removeAllViews();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        edPlot[i] = new EditText(Testimonial.this);
                                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                                        /*String key = jsonObj.keys().next();
                                        String value = jsonObj.getString(key);*/ /*Changed as per junaid requirements 19th Aug 2021*/
                                        String key = jsonObj.getString("PlotKey");
                                        String value = jsonObj.getString("PlotValue");
                                        Log.i("Info", "Key: " + key + ", value: " + value);
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        edPlot[i].setHint(key);
                                        edPlot[i].setTag(value);
                                        edPlot[i].setText(value); /*Also set text to display user*/
                                        edPlot[i].setEnabled(false);
                                        edPlot[i].setBackground(getResources().getDrawable(R.drawable.border_style));
                                        edPlot[i].setTextColor(getResources().getColor(R.color.black));
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        lp.setMargins(5, 5, 5, 5);
                                        edPlot[i].setLayoutParams(lp);
                                        edPlot[i].setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                        edPlot[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                                        edPlot[i].setTypeface(Typeface.SANS_SERIF);
                                        layoutSpTv.addView(edPlot[i]);
                                    }
                                }
                            }
                            //If Observation NOT taken then, allow User to take DATA by creating runtime design
                            else {
                                rgSpMl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                        //tvManual.setVisibility(View.VISIBLE);
                                        if (checkedId == R.id.rb_sp_ml_all) {

                                            //Clear Data for fresh use
                                            if (selectedPlot != null && selectedPlot.length > 0)
                                                Arrays.fill(selectedPlot, false);
                                            if (plotList != null && plotList.size() > 0)
                                                plotList.clear();
                                            tvManual.setText("Select Plot");
                                            tvManual.setVisibility(View.GONE);

                                            layoutSpTv.removeAllViews();
                                            totalEditText = 100;
                                            for (int i = 0; i < totalEditText; i++) {
                                                Log.d("AddTextView", "i = " + i);
                                                edPlot[i] = new EditText(Testimonial.this);
                                                edPlot[i].setHint("P" + (i + 1));
                                                edPlot[i].setTag("P" + (i + 1));

                                                edPlot[i].setBackground(getResources().getDrawable(R.drawable.border_style));
                                                edPlot[i].setTextColor(getResources().getColor(R.color.black));
                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                lp.setMargins(5, 5, 5, 5);
                                                edPlot[i].setLayoutParams(lp);
                                                edPlot[i].setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                                edPlot[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                                                edPlot[i].setTypeface(Typeface.SANS_SERIF);
                                                layoutSpTv.addView(edPlot[i]);

                                                plotList.add(i);
                                                Collections.sort(plotList);
                                            }

                                        } else if (checkedId == R.id.rb_sp_ml_manual) {
                                            layoutSpTv.removeAllViews();
                                            tvManual.setVisibility(View.VISIBLE);

                                            //Clear Data for fresh use
                                            if (selectedPlot != null && selectedPlot.length > 0)
                                                Arrays.fill(selectedPlot, false);
                                            if (plotList != null && plotList.size() > 0)
                                                plotList.clear();
                                            tvManual.setVisibility(View.VISIBLE);

                                            tvManual.setText("Select Plot");

                                            selectedPlot = new boolean[plotArray.length];

                                            tvManual.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Testimonial.this);
                                                    builder.setTitle("Select Plot");
                                                    builder.setCancelable(false);
                                                    builder.setMultiChoiceItems(plotArray, selectedPlot, new DialogInterface.OnMultiChoiceClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                                            if (b) {
                                                                plotList.add(i);
                                                                Collections.sort(plotList);
                                                            } else {
                                                                plotList.remove(i);
                                                            }
                                                        }
                                                    });

                                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            layoutSpTv.removeAllViews(); /*To freshly add editText*/
                                                            StringBuilder stringBuilder = new StringBuilder();
                                                            for (int j = 0; j < plotList.size(); j++) {
                                                                stringBuilder.append(plotArray[plotList.get(j)]);
                                                                if (j != plotList.size() - 1) {
                                                                    stringBuilder.append(", ");
                                                                }
                                                            }
                                                            tvManual.setText(stringBuilder.toString());
                                                            try {
                                                                totalEditText = plotList.size();
                                                                /*Add Edit Text Runtime to layout*/
                                                                for (int k = 0; k < totalEditText; k++) {
                                                                    Log.d("AddTextView", "k = " + k + " Lbl : " + plotArray[plotList.get(k)]);
                                                                    edPlot[k] = new EditText(Testimonial.this);
                                                                    edPlot[k].setHint("" + plotArray[plotList.get(k)]); //"P" + k
                                                                    edPlot[k].setTag(plotArray[plotList.get(k)]); //"P" + k

                                                                    edPlot[k].setBackground(getResources().getDrawable(R.drawable.border_style));
                                                                    edPlot[k].setTextColor(getResources().getColor(R.color.black));
                                                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                                    lp.setMargins(5, 5, 5, 5);
                                                                    edPlot[k].setLayoutParams(lp);
                                                                    edPlot[k].setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                                                    edPlot[k].setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                                                                    edPlot[k].setTypeface(Typeface.SANS_SERIF);
                                                                    layoutSpTv.addView(edPlot[k]);
                                                                }
                                                            }
                                                            //catch
                                                            catch (Exception e) {
                                                                Log.d("MSG", "Error : " + e.getMessage());
                                                            }
                                                        }
                                                    });

                                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    });

                                                    builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            for (int j = 0; j < selectedPlot.length; j++) {
                                                                selectedPlot[j] = false;
                                                                plotList.clear();
                                                                tvManual.setText("Select Plot");
                                                            }
                                                        }
                                                    });

                                                    builder.show();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                            /*END -------------------- Special Multiple Case  */
                        } else {
                            // this is for textbox
                            if (multiple.equals("M")) {
                                if (text != 0) {
                                    Farmerdata2.moveToFirst();
                                    if (Farmerdata2 != null) {
                                        do {

                                            txt1.setText(Farmerdata2.getString(Farmerdata2.getColumnIndex("Value1")));
                                            txt2.setText(Farmerdata2.getString(Farmerdata2.getColumnIndex("Value2")));
                                            txt3.setText(Farmerdata2.getString(Farmerdata2.getColumnIndex("Value3")));
                                            txt4.setText(Farmerdata2.getString(Farmerdata2.getColumnIndex("Value4")));
                                            txt5.setText(Farmerdata2.getString(Farmerdata2.getColumnIndex("Value5")));
                                            // save.setEnabled(false);
                                            //txt1.setTextColor(Color.rgb(228,228,228));
                                            txt1.setKeyListener(null);
                                            //txt2.setTextColor(Color.rgb(228,228,228));
                                            txt2.setKeyListener(null);
                                            // txt3.setTextColor(Color.rgb(228,228,228));
                                            txt3.setKeyListener(null);
                                            //  txt4.setTextColor(Color.rgb(228,228,228));
                                            txt4.setKeyListener(null);
                                            // txt5.setTextColor(Color.rgb(228,228,228));
                                            txt5.setKeyListener(null);
                                            Log.d("rohitt", "BindData: " + Farmerdata2.getColumnIndex("Value1"));


                                            String dbImagePath =
                                                    Farmerdata2.getString(Farmerdata2.getColumnIndex("Image"));
                                            if (dbImagePath != null && !dbImagePath.contains("null")) {
                                                cx.bindimage(ivImage, dbImagePath);
                                            }
                                        } while (Farmerdata2.moveToNext());
                                    }
                                }
                                txt1.setVisibility(View.VISIBLE);
                                txt2.setVisibility(View.VISIBLE);
                                txt3.setVisibility(View.VISIBLE);
                                txt4.setVisibility(View.VISIBLE);
                                txt5.setVisibility(View.VISIBLE);
                                SPNOBS.setVisibility(View.GONE);
                                obsField.setVisibility(View.GONE);
                                layoutSpecialMultiple.setVisibility(View.GONE); /*Added on 10th Aug 2021*/
                            } else {
                                Log.d("TDMS", "Else working well");
                                Log.d("TDMS", "DATA text :" + text);

                                /*if (Farmerdata2 != null) {
                                    if (text != 0) {
                                        Farmerdata2.moveToFirst();
                                        String continueData = Farmerdata2.getString(Farmerdata2.getColumnIndex("Value1"));
                                        Log.d("Remark", "Remark Value DB: " + continueData);
                                        obsField.setText(continueData);
                                    }
                                }*/

                                if (text != 0) {
                                    Farmerdata2.moveToFirst();
                                    if (Farmerdata2 != null) {
                                        do {
                                            // String data = Farmerdata2.getString(Farmerdata2.getColumnIndex("Value1"));
                                            obsField.setText(Farmerdata2.getString(Farmerdata2.getColumnIndex("Value1")));
                                            // save.setEnabled(false);
                                            obsField.setEnabled(false);
                                            //obsField.setTextColor(Color.rgb(228,228,228));
                                            //obsField.setKeyListener(null);
                                            Log.d("rohitt1", "BindData: " + Farmerdata2.getColumnIndex("Value1"));

                                            String dbImagePath = Farmerdata2.getString(Farmerdata2.getColumnIndex("Image"));
                                            Log.d("TDMS", "IMAGE DATA : " + dbImagePath);
                                            if (dbImagePath != null && !dbImagePath.contains("null")) {
                                                cx.bindimage(ivImage, dbImagePath);
                                            }
                                        } while (Farmerdata2.moveToNext());
                                    }
                                }
                                obsField.setVisibility(View.VISIBLE);
                                txt1.setVisibility(View.GONE);
                                txt2.setVisibility(View.GONE);
                                txt3.setVisibility(View.GONE);
                                txt4.setVisibility(View.GONE);
                                txt5.setVisibility(View.GONE);
                                SPNOBS.setVisibility(View.GONE);
                                layoutSpecialMultiple.setVisibility(View.GONE); /*Added on 10th Aug 2021*/

                                //obsField.setId(Integer.parseInt(Farmerdata.getString(Farmerdata.getColumnIndex("VariableID"))));
                            }
                        }


                        my_linear_layout1.addView(view2);
                        //
                        lblname.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Testimonial.this);
                                LayoutInflater inflater = Testimonial.this.getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.alert_text_editor, null);
                                dialogBuilder.setView(dialogView);
                                dialogBuilder.setPositiveButton("OK", null);
                                TextView OBDetails = (TextView) dialogView.findViewById(R.id.textObs);
                                TextView textName = (TextView) dialogView.findViewById(R.id.textName);
                                // ImageView mImageView = (ImageView) dialogView.findViewById(R.id.img);
                                //if (ivImage.getDrawable() != null) {
                                Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariableID.getText().toString());
                                int text1 = Farmerdata3.getCount();
                                if (text1 != 0) {
                                    Farmerdata3.moveToFirst();
                                    if (Farmerdata3 != null) {
                                        do {
                                            textName.setText(Farmerdata3.getString(Farmerdata3.getColumnIndex("Name")));
                                            OBDetails.setText(Farmerdata3.getString(Farmerdata3.getColumnIndex("Discription")));

                                        } while (Farmerdata3.moveToNext());
                                    }
                                }
                                AlertDialog alertDialog = dialogBuilder.create();

                                alertDialog.show();
                            }
                        });


                        ////// For After textChange

                        obsField.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                try {
                                    int val = Integer.parseInt(s.toString());
                                    Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariableID.getText().toString());
                                    int text1 = Farmerdata3.getCount();
                                    if (text1 != 0) {
                                        Farmerdata3.moveToFirst();
                                        if (Farmerdata3 != null) {
                                            do {
                                                int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                                int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                                if (val > max) {

                                                    //msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                                    //obsField.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                } else if (val < min) {

                                                    // msclass.showMessage("Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'");
                                                    // obsField.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                }

                                            } while (Farmerdata3.moveToNext());
                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                }
                            }
                        });
                        ////


                        //////FOr Multi Textbox

                        txt1.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                try {
                                    int val = Integer.parseInt(s.toString());
                                    Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariableID.getText().toString());
                                    int text1 = Farmerdata3.getCount();
                                    if (text1 != 0) {
                                        Farmerdata3.moveToFirst();
                                        if (Farmerdata3 != null) {
                                            do {
                                                int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                                int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                                if (val > max) {

                                                    //msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                                                    // txt1.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                } else if (val < min) {

                                                    // msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                                                    //  txt1.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                }

                                            } while (Farmerdata3.moveToNext());


                                        }
                                    }

                                } catch (NumberFormatException ex) {
                                }
                            }
                        });


                        txt2.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                try {
                                    int val = Integer.parseInt(s.toString());
                                    Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariableID.getText().toString());
                                    int text1 = Farmerdata3.getCount();
                                    if (text1 != 0) {
                                        Farmerdata3.moveToFirst();
                                        if (Farmerdata3 != null) {
                                            do {
                                                int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                                int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                                if (val > max) {

                                                    // msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                                                    //txt2.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                } else if (val < min) {

                                                    // msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                                                    //txt2.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                }

                                            } while (Farmerdata3.moveToNext());


                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                }
                            }
                        });

                        txt3.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                try {
                                    int val = Integer.parseInt(txt3.getText().toString());
                                    Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariableID.getText().toString());
                                    int text1 = Farmerdata3.getCount();
                                    if (text1 != 0) {
                                        Farmerdata3.moveToFirst();
                                        if (Farmerdata3 != null) {
                                            do {
                                                int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                                int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                                if (val > max) {

                                                    //msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                                                    //txt3.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                } else if (val < min) {

                                                    //msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                                                    //txt3.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                }

                                            } while (Farmerdata3.moveToNext());


                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                }

                            }
                        });


                        txt4.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                try {
                                    int val = Integer.parseInt(s.toString());
                                    Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariableID.getText().toString());
                                    int text1 = Farmerdata3.getCount();
                                    if (text1 != 0) {
                                        Farmerdata3.moveToFirst();
                                        if (Farmerdata3 != null) {
                                            do {
                                                int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                                int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                                if (val > max) {

                                                    //msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                                                    //txt4.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                } else if (val < min) {

                                                    // msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                                                    // txt4.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                }

                                            } while (Farmerdata3.moveToNext());


                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                }

                            }
                        });


                        txt5.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                try {
                                    int val = Integer.parseInt(txt5.getText().toString());
                                    Cursor Farmerdata3 = databaseHelper1.getObservationDiscription(ObsVariableID.getText().toString());
                                    int text1 = Farmerdata3.getCount();
                                    if (text1 != 0) {
                                        Farmerdata3.moveToFirst();
                                        if (Farmerdata3 != null) {
                                            do {
                                                int min = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value1")));
                                                int max = Integer.parseInt(Farmerdata3.getString(Farmerdata3.getColumnIndex("Value2")));

                                                if (val > max) {
                                                    // msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                                                    // txt5.setText("");

                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                } else if (val < min) {

                                                    // msclass.showMessage("Value Must Be In Between"+" '"+min+"' " +"and"+" '"+max+"'");
                                                    //txt5.setText("");
                                                    Toast.makeText(Testimonial.this, "Value Must Be In Between" + " '" + min + "' " + "and" + " '" + max + "'", Toast.LENGTH_LONG).show();
                                                }

                                            } while (Farmerdata3.moveToNext());


                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                }

                            }
                        });

                    } while (Farmerdata.moveToNext());


                }
                Farmerdata.close();
            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
            }
        }

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

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        lblCordinate.setText(+location.getLatitude() + "-" + location.getLongitude());
        // Toast.makeText(newAreaTag.this, "before location change", Toast.LENGTH_LONG).show();
        getLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void captureImage() {

        try {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 0);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    try {

                        photoFile = createImageFile();
                        //displayMessage(getBaseContext(),photoFile.getAbsolutePath());
                        // Log.i("Mayank",photoFile.getAbsolutePath());

                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(this,
                                    "com.tdms.mahyco.nxg" + ".provider", photoFile);
                            //BuildConfig.APPLICATION_ID + ".provider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                        }


                    } catch (Exception ex) {
                        // Error occurred while creating the File
                        // displayMessage(getBaseContext(),ex.getMessage().toString());
                        //msclass.showMessage(ex.toString());
                        msclass.showMessage("Something went wrong, please try again later");
                        Log.d("Msg", ex.getMessage());
                    }


                } else {
                    //displayMessage(getBaseContext(),"Nullll");
                }
            }
        } catch (Exception ex) {
            // Error occurred while creating the File
            // displayMessage(getBaseContext(),ex.getMessage().toString());
            Log.d("Msg", ex.getMessage());
        }
    }

    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
    private void captureImage2() {

        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


            photoFile = createImageFile4();
            if (photoFile != null) {
                //displayMessage(getBaseContext(),photoFile.getAbsolutePath());
                Log.i("Mayank", photoFile.getAbsolutePath());
                Uri photoURI = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            }


        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
            // displayMessage(getBaseContext(),"Camera is not available."+e.toString());
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
                    //displayMessage(getBaseContext(),"Unable to create directory.");
                    return null;
                }
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } catch (Exception ex) {
            Log.d("Msg", ex.getMessage());
        }
        return mediaFile;
    }

    private File createImageFile() {
        // Create an image file name
        File image = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        } catch (Exception ex) {
            Log.d("Msg", ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
        // Save a file: path for use with ACTION_VIEW intents
        // mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onCaptureImageResult(data);//onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
            }
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
    }

    private void onCaptureImageResult(Intent data) {
        try {
            try {
                /*BitmapFactory.Options options = new BitmapFactory.Options();
                // options.inJustDecodeBounds = true;
                options.inSampleSize = 2;

                Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                // myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                imagepath1 = photoFile.getAbsolutePath();
                ivImage.setImageBitmap(myBitmap);*/

                /*------------------------------Start -----------------------*/
                BitmapFactory.Options options = new BitmapFactory.Options();
                // options.inJustDecodeBounds = true;
                options.inSampleSize = 2;
                Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                // myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                // this only get capture photo
                //************ 30 Dec 2020
                Date entrydate = new Date();
                String InTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entrydate);
                AppConstant.queryImageUrl = photoFile.getAbsolutePath();
                AppConstant.imageUri = Uri.fromFile(new File(AppConstant.queryImageUrl));
                AppConstant.Imagename = this.getClass().getSimpleName() + userCode + String.valueOf(entrydate.getTime());
                FileUtilImage.compressImageFile(AppConstant.queryImageUrl, AppConstant.imageUri,
                        this, AppConstant.Imagename);
                // need to set commpress image path
                imagepath1 = FileUtilImage.savefilepath;// photoFile.getAbsolutePath();  old ssave
                ivImage.setImageBitmap(myBitmap);
                //************** 30 Dec 2020
                /*------------------------------End -----------------------*/

            } catch (Exception e) {
                msclass.showMessage("Something went wrong, please try again later");
                Log.d("Msg", e.getMessage());
            }
            //end

        } catch (Exception e) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg", e.getMessage());
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
