package com.tdms.mahyco.nxg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tdms.mahyco.nxg.utils.AppConstant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SowingActivityNew extends AppCompatActivity  implements LocationListener {

    EditText txtFname, txtFvillage, txtFmobile, txtStartnote, txtArea, txtGeoVillage;
    //txtSowingDate, /*Removed on 17th Feb 2021*/
    TextView Trialcodedata, txtNoRows, txtRowLength, txtRRSpecing, txtPPSpacing, txtReplication, txtTotEntry, txtLocation, txtplotstart, txtPlotSize, txtnurseryDate, txtProduct;
    TextView lblRowLength, lblRRSpecing, lblPPSpacing, lblReplication, lblPlotSize, lbl4, lbl5, lbl6, lbl7, lbl11, lbl12, lblProduct;
    databaseHelper databaseHelper1;
    public Messageclass msclass;
    String txtReplec, NoRows, RowLength, RRSpecing, PPSpacing, Replication, TotEntry, Fname, Fvillage, Fmobile, FstartNote, Fsowingdate, Location, plotstart, Plotend, trial_type, PlotSize, nursery, nurseryDate, product;
    Button Btnsave, BtnFieldTag, BtnFieldTagT, btnSaveT; /*Added on 15 Feb 2021*/ /*BtnUpdate removed on 17th Feb 2021*/
    public String userCode;
    public Context cx;
    LocationManager locationManager;
    LinearLayout layoutTransplant;

    private double longitude;
    private double latitude;
    String getTagType = "";
    EditText tvTransplantDate, txtGeoVillageT, txtFarmerNameT, txtVillageT, txtContactT, txtStartNoteT;
    String TFName = "", TFVillage = "", TFGeoLocation = "", TFMobile = "", TFStartNote = "", TFTransplantDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sowing_new);
        setUI();
    }

    private void setUI(){
        getSupportActionBar().setTitle("Sowing & Transplanting Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            final Calendar myCalendar = Calendar.getInstance();
            //txtSowingDate = (EditText) rootView.findViewById(R.id.txtSowingDate); /*removed on 17th Feb 2021*/
            txtFname = (EditText) findViewById(R.id.txtFarmerName);
            txtFvillage = (EditText)findViewById(R.id.txtVillage);
            txtFmobile = (EditText) findViewById(R.id.txtContact);
            txtStartnote = (EditText)findViewById(R.id.txtStartNote);
            txtArea = (EditText) findViewById(R.id.txtArea);
            txtGeoVillage = (EditText)findViewById(R.id.txtGeoVillage);
            txtnurseryDate = (EditText)findViewById(R.id.txtNursaryDate);
            layoutTransplant = (LinearLayout) findViewById(R.id.layout_transplant);
            tvTransplantDate = (EditText)findViewById(R.id.tv_transplant_date);
            txtGeoVillageT = (EditText)findViewById(R.id.txtGeoVillageT);
            txtFarmerNameT = (EditText)findViewById(R.id.txtFarmerNameT);
            txtVillageT = (EditText)findViewById(R.id.txtVillageT);
            txtContactT = (EditText)findViewById(R.id.txtContactT);
            txtStartNoteT = (EditText)findViewById(R.id.txtStartNoteT);

            txtNoRows = (TextView) findViewById(R.id.txtrow);
            txtRowLength = (TextView) findViewById(R.id.txtrowlenght);//
            txtRRSpecing = (TextView) findViewById(R.id.txtrrSpecing);//
            txtPPSpacing = (TextView) findViewById(R.id.txtppSpecing);//
            txtReplication = (TextView) findViewById(R.id.txtreplec); //
            txtTotEntry = (TextView) findViewById(R.id.txttotEntry);
            txtLocation = (TextView) findViewById(R.id.txtLocation);
            txtplotstart = (TextView) findViewById(R.id.txtplotstart);
            txtPlotSize = (TextView) findViewById(R.id.txtPlotSize); //
            txtProduct = (TextView) findViewById(R.id.txtProduct);

            lbl4 = (TextView) findViewById(R.id.lbl4);
            lbl5 = (TextView) findViewById(R.id.lbl5);
            lbl6 = (TextView) findViewById(R.id.lbl6);
            lbl7 = (TextView) findViewById(R.id.lbl7);
            lbl11 = (TextView) findViewById(R.id.lbl11);
            lbl12 = (TextView) findViewById(R.id.lbl12);

            lblRowLength = (TextView)findViewById(R.id.lblrowlenght);
            lblRRSpecing = (TextView) findViewById(R.id.lblrrSpecing);
            lblPPSpacing = (TextView) findViewById(R.id.lblppSpecing);
            lblReplication = (TextView) findViewById(R.id.lblreplec);
            lblPlotSize = (TextView)findViewById(R.id.lblPlotSize);
            lblProduct = (TextView) findViewById(R.id.lblProduct);

            Trialcodedata = (TextView)findViewById(R.id.txtTrialCode);
            //txtNoReplecation=(TextView)rootView.findViewById(R.id.txtNoReplecation);
            //txtPlotNo=(TextView)rootView.findViewById(R.id.txtPlot);

            databaseHelper1 = new databaseHelper(this);
            msclass = new Messageclass(this);
            cx = this;

            Btnsave = (Button)findViewById(R.id.btnSave);
            BtnFieldTag = (Button) findViewById(R.id.BtnFieldTag);
            //BtnUpdate = (Button) rootView.findViewById(R.id.btnUpdate); /*removed on 17th Feb 2021*/
            BtnFieldTagT = (Button) findViewById(R.id.BtnFieldTagT);
            btnSaveT = (Button) findViewById(R.id.btnSaveT);

            //Get Data
            if(getIntent()!=null) {
                Bundle data = getIntent().getBundleExtra("DATA");
                String getArgument = data.getString("keyCode");
                getTagType = data.getString(AppConstant.TAG_TYPE);
                Log.d("FieldAreaTag", "DATA RECEIVED : TrialCode : " + getArgument + " TAG TYPE : " + getTagType);
                Trialcodedata.setText(getArgument);
            }
            // Get Location
            getLocation();
            /// End Location
            Cursor data1 = databaseHelper1.fetchusercode();

            if (data1.getCount() == 0) {
                msclass.showMessage("No Data Available... ");
            } else {
                data1.moveToFirst();
                if (data1 != null) {
                    do {
                        userCode = data1.getString((data1.getColumnIndex("user_code")));
                    /*String userCodeEncrypt = data1.getString((data1.getColumnIndex("user_code")));
                    String userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("userCode", "userCode" + userCode);*/
                    } while (data1.moveToNext());

                }
                data1.close();
            }

            Cursor data = databaseHelper1.fetchData(Trialcodedata.getText().toString());

            if (data.getCount() == 0) {
                msclass.showMessage("No Data Available... ");
            } else {
                data.moveToFirst();
                if (data != null) {
                    do {
                        //txtNoReplecation = data.getInt(0);
                        txtReplec = data.getString(data.getColumnIndex("REPLECATION"));
                        //PLOTNO=data.getString(data.getColumnIndex("PLOTSTART"));
                        //txtNoReplecation.setText(data.getColumnIndex("REPLECATION"));
                        NoRows = data.getString(data.getColumnIndex("NoRows"));
                        RowLength = data.getString(data.getColumnIndex("RowLength"));
                        RRSpecing = data.getString(data.getColumnIndex("RRSpecing"));
                        PPSpacing = data.getString(data.getColumnIndex("PPSpacing"));
                        TotEntry = data.getString(data.getColumnIndex("ENTRY"));
                        Location = data.getString(data.getColumnIndex("Location"));
                        plotstart = data.getString(data.getColumnIndex("StartPlotNo"));
                        Plotend = data.getString(data.getColumnIndex("EndPlotNo"));
                        trial_type = data.getString(data.getColumnIndex("Trail_Type"));
                        PlotSize = data.getString(data.getColumnIndex("PlotSize"));
                        nursery = data.getString(data.getColumnIndex("nursery"));
                        product = data.getString((data.getColumnIndex("Product")));

                    } while (data.moveToNext());
                    txtReplication.setText(txtReplec);
                    txtNoRows.setText(NoRows);
                    txtRowLength.setText(RowLength);
                    txtRRSpecing.setText(RRSpecing);
                    txtPPSpacing.setText(PPSpacing);
                    txtTotEntry.setText(TotEntry);
                    txtLocation.setText(Location);
                    txtplotstart.setText(plotstart + "  To  " + Plotend);
                    txtPlotSize.setText(PlotSize);
                    txtProduct.setText(product);

                }
                data.close();
            }

            Log.d("Rajshri", "Display nursery data show : " + nursery + " nurserdate =" + nurseryDate);

            if (nursery.equals("N") || getTagType.equals(AppConstant.NO_TAG)) {
                txtnurseryDate.setVisibility(View.VISIBLE);
                layoutTransplant.setVisibility(View.GONE);
                txtnurseryDate.setHint("Select Sowing Date");
                //txtSowingDate.setHint("Select Sowing Date"); /*removed on 17th Feb 2021*/
            } else {
                layoutTransplant.setVisibility(View.VISIBLE); /*Rajshri Added on  10 Feb 2021*/
                //txtSowingDate.setVisibility(View.GONE); /*removed on 17th Feb 2021*/
                txtnurseryDate.setVisibility(View.VISIBLE);
            }

            if (trial_type.equals("DEM")) {
                txtProduct.setVisibility(View.VISIBLE);
                lbl12.setVisibility(View.VISIBLE);
                lblProduct.setVisibility(View.VISIBLE);
            }

            if (trial_type.equals("MET")) {
                lblRowLength.setVisibility(View.GONE);
                lblRRSpecing.setVisibility(View.VISIBLE);
                lblPPSpacing.setVisibility(View.VISIBLE);
                lblReplication.setVisibility(View.VISIBLE);
                lblPlotSize.setVisibility(View.GONE);

                lbl4.setVisibility(View.GONE);
                lbl5.setVisibility(View.VISIBLE);
                lbl6.setVisibility(View.VISIBLE);
                lbl7.setVisibility(View.VISIBLE);
                lbl11.setVisibility(View.GONE);

                txtRowLength.setVisibility(View.GONE);//
                txtRRSpecing.setVisibility(View.VISIBLE);//
                txtPPSpacing.setVisibility(View.VISIBLE);//
                txtReplication.setVisibility(View.VISIBLE); //
                txtPlotSize.setVisibility(View.GONE); //
            } else {
                lblRowLength.setVisibility(View.VISIBLE);
                lblRRSpecing.setVisibility(View.GONE);
                lblPPSpacing.setVisibility(View.GONE);
                lblReplication.setVisibility(View.GONE);
                lblPlotSize.setVisibility(View.VISIBLE);


                lbl4.setVisibility(View.VISIBLE);
                lbl5.setVisibility(View.GONE);
                lbl6.setVisibility(View.GONE);
                lbl7.setVisibility(View.GONE);
                lbl11.setVisibility(View.VISIBLE);

                txtRowLength.setVisibility(View.VISIBLE);//
                txtRRSpecing.setVisibility(View.GONE);//
                txtPPSpacing.setVisibility(View.GONE);//
                txtReplication.setVisibility(View.GONE); //
                txtPlotSize.setVisibility(View.VISIBLE); //
            }

            Cursor Farmerdata = databaseHelper1.fetchFarmerData(Trialcodedata.getText().toString());
            String GeoLocation = "";
            if (Farmerdata.getCount() == 0) {
                //msclass.showMessage("No Data Available... ");
            } else {
                Farmerdata.moveToFirst();
                if (Farmerdata != null) {
                    do {
                        //Fname ,Fvillage ,Fmobile ,FstartNote ,Fsowingdate
                        //txtNoReplecation = data.getInt(0);
                        Fname = Farmerdata.getString(Farmerdata.getColumnIndex("Fname"));
                        Fvillage = Farmerdata.getString(Farmerdata.getColumnIndex("Fvillage"));
                        Fmobile = Farmerdata.getString(Farmerdata.getColumnIndex("Fmobile"));
                        FstartNote = Farmerdata.getString(Farmerdata.getColumnIndex("FstartNote"));
                        Fsowingdate = Farmerdata.getString(Farmerdata.getColumnIndex("Fsowingdate"));
                        nurseryDate = Farmerdata.getString(Farmerdata.getColumnIndex("nurseryDate"));
                        GeoLocation = Farmerdata.getString(Farmerdata.getColumnIndex("GeoLocation"));

                        TFName = Farmerdata.getString(Farmerdata.getColumnIndex("TFName"));
                        TFVillage = Farmerdata.getString(Farmerdata.getColumnIndex("TFVillage"));
                        TFGeoLocation = Farmerdata.getString(Farmerdata.getColumnIndex("TFGeoLocation"));
                        TFMobile = Farmerdata.getString(Farmerdata.getColumnIndex("TFMobile"));
                        TFStartNote = Farmerdata.getString(Farmerdata.getColumnIndex("TFStartNote"));
                        TFTransplantDate = Farmerdata.getString(Farmerdata.getColumnIndex("TFTransplantDate"));

                        Log.d("MSG", "------------------------------Sowing Date--------------------------------------");
                        Log.d("Rajshri", "Display nursery sowing date : " + Fsowingdate);
                        Log.d("Rajshri", "Display transplanting date : " + nurseryDate);
                        Log.d("Rajshri", "Display Fname : " + Fname);
                        Log.d("Rajshri", "Display Fvillage : " + Fvillage);
                        Log.d("Rajshri", "Display Fmobile : " + Fmobile);
                        Log.d("Rajshri", "Display FstartNote : " + FstartNote);
                        Log.d("Rajshri", "Display GeoLocation : " + GeoLocation);
                        Log.d("MSG", "------------------------------Transplant Date--------------------------------------");
                        Log.d("Rajshri", "Display TFname : " + TFName);
                        Log.d("Rajshri", "Display TFvillage : " + TFVillage);
                        Log.d("Rajshri", "Display TFmobile : " + TFGeoLocation);
                        Log.d("Rajshri", "Display TnurseryDate : " + TFMobile);
                        Log.d("Rajshri", "Display TTransplantdate : " + TFStartNote);
                        Log.d("Rajshri", "Display TFTransplantDate : " + TFTransplantDate);

                    } while (Farmerdata.moveToNext());
                    txtFname.setText(Fname);
                    txtFvillage.setText(Fvillage);
                    txtFmobile.setText(Fmobile);
                    txtStartnote.setText(FstartNote);
                    txtnurseryDate.setText(Fsowingdate);
                    //txtnurseryDate.setText(nurseryDate); /*Removed 24 Feb 2021*/
                    /*removed on 17th Feb 2021*/
                    //txtSowingDate.setText(nurseryDate); //Fsowingdate Note: 1 Dec 2020 changed as per Report requirements by Niteen
                    //txtnurseryDate.setText(Fsowingdate); //nurseryDate Note: 1 Dec 2020 changed as per Report requirements by Niteen

                    if (getTagType != null) {
                        if (getTagType.equalsIgnoreCase(AppConstant.NURSERY_TAG)) {
                            txtGeoVillage.setText(GeoLocation);
                        }
                    }

                    /*txtnurseryDate.setHint("Select Sowing Date");*/
                /*if (nursery.equals("N")) {
                    txtSowingDate.setText(Fsowingdate); //Fsowingdate Note: 2 Dec 2020 changed as per Report requirements by Niteen
                }*/

                    Btnsave.setEnabled(false);
                    BtnFieldTag.setEnabled(true);

                    /*Removed on 17th Feb 2021*/
                /*if (txtnurseryDate.length() > 0) {
                    BtnUpdate.setVisibility(View.VISIBLE);
                }*/
                }
                Farmerdata.close();
            }

            //End

            /*Removed on 17 Feb 2021*/
        /*final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                // txtSowingDate.setText(dayOfMonth + "/" + monthOfYear+1 + "/" + year);
                txtSowingDate.setText(sdformat.format(myCalendar.getTime()));
                // txtnurseryDate.setText(sdformat.format(myCalendar.getTime()));
                // txtSowingDate.setText(new StringBuilder().append(dayOfMonth).append("-").append(monthOfYear + 1).append("-").append(year).append(""));
            }
        };*/

            final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // TODO Auto-generated method stub

                    String myFormat = "dd-MM-yyyy"; //In which you need put here
                    SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    //updateLabel();
                    // txtSowingDate.setText(dayOfMonth + "/" + monthOfYear+1 + "/" + year);
                    txtnurseryDate.setText(sdformat.format(myCalendar.getTime()));
                    // txtSowingDate.setText(new StringBuilder().append(dayOfMonth).append("-").append(monthOfYear + 1).append("-").append(year).append(""));
                }

            };

            final DatePickerDialog.OnDateSetListener dateTransplant = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // TODO Auto-generated method stub

                    String myFormat = "dd-MM-yyyy"; //In which you need put here
                    SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    //updateLabel();
                    // txtSowingDate.setText(dayOfMonth + "/" + monthOfYear+1 + "/" + year);
                    tvTransplantDate.setText(sdformat.format(myCalendar.getTime()));
                    // txtSowingDate.setText(new StringBuilder().append(dayOfMonth).append("-").append(monthOfYear + 1).append("-").append(year).append(""));
                }

            };

            /*Removed on 17th Feb 2021*/
        /*txtSowingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });*/


            txtnurseryDate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(SowingActivityNew.this, date1, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            tvTransplantDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(SowingActivityNew.this, dateTransplant, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            Btnsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validation() == true) {
                        Log.d("Rajshri", "transplanting date : " + txtnurseryDate.getText().toString());
                        String geoLocation = txtGeoVillage.getText().toString();
                        Log.d("Rajshri", "Geo Location : " + geoLocation);
                        /*if (geoLocation.equals("") || geoLocation.equals(null)) {
                            txtGeoVillage.setText("12345 Bilaspur Chattisghar");
                        }*/

                        boolean result = databaseHelper1.InsertFarmerData(txtFname.getText().toString(), txtFvillage.getText().toString(),
                                txtFmobile.getText().toString(), txtStartnote.getText().toString(), "",
                                Trialcodedata.getText().toString(), "0", userCode, txtGeoVillage.getText().toString(), txtnurseryDate.getText().toString());
                        if (result) {
                            msclass.showMessage("Data Save Sucessfully");
                            Btnsave.setEnabled(false);
                            BtnFieldTag.setEnabled(true);
                        } else {
                            msclass.showMessage("Data Not Save");
                        }

                        //Note: 2 Dec 2020 changed as per Report requirements by Niteen
                    /*Log.d("Rajshri", "nursery data:" + nursery);
                    if (nursery.equals("N")) {
                        //txtnurseryDate.setVisibility(View.GONE);
                        //Log.d("Rajshri", "nursery sowing date : " + txtSowingDate.getText().toString());
                        Log.d("Rajshri", "transplanting date : " + txtnurseryDate.getText().toString());
                        boolean result = databaseHelper1.InsertFarmerData(txtFname.getText().toString(), txtFvillage.getText().toString(),
                                txtFmobile.getText().toString(), txtStartnote.getText().toString(), "",
                                Trialcodedata.getText().toString(), "0", userCode, txtGeoVillage.getText().toString(), txtnurseryDate.getText().toString());
                        if (result) {
                            msclass.showMessage("Data Save Sucessfully");
                            Btnsave.setEnabled(false);
                            BtnFieldTag.setEnabled(true);
                        } else {
                            msclass.showMessage("Data Not Save");
                        }

                    } else {
                        //Log.d("Rajshri", "nursery sowing date : " + txtSowingDate.getText().toString());
                        Log.d("Rajshri", "transplanting date : " + txtnurseryDate.getText().toString());
                        boolean result = databaseHelper1.InsertFarmerData(txtFname.getText().toString(), txtFvillage.getText().toString(),
                                txtFmobile.getText().toString(), txtStartnote.getText().toString(), txtnurseryDate.getText().toString(),
                                Trialcodedata.getText().toString(), "0", userCode, txtGeoVillage.getText().toString(), txtnurseryDate.getText().toString());
                        if (result) {
                            //databaseHelper1.updatePlot(Integer.parseInt(txtPlotNo.getText().toString()), Trialcodedata.getText().toString());
                            msclass.showMessage("Data Save Sucessfully");
                            Btnsave.setEnabled(false);
                            BtnFieldTag.setEnabled(true);
                            //Intent openIntent = new Intent(getActivity(), newAreaTag.class);
                            // openIntent.putExtra("Trail_code", Trialcodedata.getText().toString());
                            //  startActivity(openIntent);

                        } else {
                            msclass.showMessage("Data Not Save");
                        }
                    }*/
                    }
                }
            });

            /*Removed on 17th Feb 2021*/
        /*BtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String searchQuery1 = "update FarmerInfodata set Fsowingdate = '"+txtnurseryDate.getText().toString()+"' where TRIL_CODE='"+Trialcodedata.getText().toString()+"' ";
                //String searchQuery1 = "update FarmerInfodata set Fsowingdate = '"+txtSowingDate.getText().toString()+"' where TRIL_CODE='"+Trialcodedata.getText().toString()+"' ";
                //Fsowingdate Note: 1 Dec 2020 changed as per Report requirements by Niteen

                *//*String searchQuery1 = "update FarmerInfodata set Fsowingdate = '"+txtnurseryDate.getText().toString()+"' AND nurseryDate = '"+
                        txtSowingDate.getText().toString()+"' where TRIL_CODE='"+Trialcodedata.getText().toString()+"' ";
                databaseHelper1.runQuery(searchQuery1);*//*

                Log.d("Rajshri", "Update Fsowingdate = " + txtnurseryDate.getText().toString());
                Log.d("Rajshri", "Update nurseryDate = " + txtSowingDate.getText().toString());

                //Note: 2 Dec 2020 changed as per Report requirements by Niteen
                ContentValues values = new ContentValues();
                if (nursery.equals("N")) {
                    values.put("Fsowingdate", txtSowingDate.getText().toString());
                    values.put("nurseryDate", "");
                } else {
                    //Note: 1 Dec 2020 changed as per Report requirements by Niteen
                    values.put("Fsowingdate", txtnurseryDate.getText().toString());
                    values.put("nurseryDate", txtSowingDate.getText().toString());

                }
                databaseHelper1.getDatabase().update("FarmerInfodata", values, "TRIL_CODE=?", new String[]{Trialcodedata.getText().toString()});
                msclass.showMessage("Data Update Sucessfully");
                BtnFieldTag.setEnabled(true);
            }
        });*/

            btnSaveT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validationT() == true) {
                        Log.d("Rajshri", "transplanting date : " + txtnurseryDate.getText().toString());

                        String geoLocationT = txtGeoVillageT.getText().toString();
                        Log.d("Rajshri", "Geo Location : " + geoLocationT);
                        /*if (geoLocationT.equals("") || geoLocationT.equals(null)) {
                            txtGeoVillageT.setText("987654 Bilaspur Chattisghar");
                        }*/

                        boolean result = databaseHelper1.InsertFarmerDataTransplant(txtFarmerNameT.getText().toString(),
                                txtVillageT.getText().toString(),
                                txtGeoVillageT.getText().toString(),
                                txtContactT.getText().toString(),
                                txtStartNoteT.getText().toString(),
                                tvTransplantDate.getText().toString(),
                                Trialcodedata.getText().toString(),
                                userCode);
                        if (result) {
                            msclass.showMessage("Data Save Sucessfully");
                            btnSaveT.setEnabled(false);
                            BtnFieldTagT.setEnabled(true);
                        } else {
                            msclass.showMessage("Data Not Save");
                        }
                    }
                }
            });

            BtnFieldTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  Bundle data1 = new Bundle();//create bundle instance
                    //  data1.putString("keyCode", Trialcodedata.getText().toString());//put string to pass with a key value
                    Intent openIntent = new Intent(SowingActivityNew.this, newAreaTag.class);
                    openIntent.putExtra("Trail_code", Trialcodedata.getText().toString());
                    if (nursery.equalsIgnoreCase("Y")) {
                        openIntent.putExtra(AppConstant.TAG_TYPE, AppConstant.NURSERY_TAG);
                    } else {
                        openIntent.putExtra(AppConstant.TAG_TYPE, AppConstant.FULL_TAG);
                    }
                    finish(); /*Added on 19th July 2021*/
                    startActivity(openIntent);


                }
            });

            BtnFieldTagT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  Bundle data1 = new Bundle();//create bundle instance
                    //  data1.putString("keyCode", Trialcodedata.getText().toString());//put string to pass with a key value
                    Intent openIntent = new Intent(SowingActivityNew.this, newAreaTag.class);
                    openIntent.putExtra("Trail_code", Trialcodedata.getText().toString());
                    openIntent.putExtra(AppConstant.TAG_TYPE, AppConstant.FULL_TAG);
                    finish(); /*Added on 19th July 2021*/
                    startActivity(openIntent);
                }
            });

            Log.d("Rajshri", "Display nursery sowing date : " + txtnurseryDate.getText().toString());
            //Log.d("Rajshri", "Display transplanting date : " + txtSowingDate.getText().toString());

            if (getTagType != null) {
                if (getTagType.equalsIgnoreCase(AppConstant.NURSERY_TAG)) {
                    Btnsave.setEnabled(false);
                    //BtnUpdate.setEnabled(false);
                    BtnFieldTag.setEnabled(false);

                    txtFname.setEnabled(false);
                    txtFvillage.setEnabled(false);
                    txtGeoVillage.setEnabled(false);
                    txtFmobile.setEnabled(false);
                    txtStartnote.setEnabled(false);
                    txtnurseryDate.setEnabled(false);
                    Log.d("Rajshri", "NURSERY DATE : " + Fsowingdate);
                    txtnurseryDate.setText(Fsowingdate);
                    Log.d("Rajshri", "GeoLocation : " + GeoLocation);
                    txtGeoVillage.setText(GeoLocation);
                    // txtSowingDate.setEnabled(false);
                    txtFname.setText(Fname);
                    txtFvillage.setText(Fvillage);
                    txtFmobile.setText(Fmobile);
                    txtStartnote.setText(FstartNote);
                    txtFname.setTextColor(getResources().getColor(R.color.black));
                    txtFvillage.setTextColor(getResources().getColor(R.color.black));
                    txtGeoVillage.setTextColor(getResources().getColor(R.color.black));
                    txtFmobile.setTextColor(getResources().getColor(R.color.black));
                    txtStartnote.setTextColor(getResources().getColor(R.color.black));
                    txtnurseryDate.setTextColor(getResources().getColor(R.color.black));
                    //txtSowingDate.setTextColor(getResources().getColor(R.color.black));
                }
            } else {
                layoutTransplant.setVisibility(View.GONE);
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

    private boolean validation() {
        boolean flag = true;
        if (txtFname.getText().length() == 0) {
            msclass.showMessage("Please Enter Farmer name ");
            return false;

        }
        if (txtFvillage.getText().length() == 0) {
            msclass.showMessage("Please enter Village");
            return false;
        }

        if (txtFmobile.getText().length() == 0 || txtFmobile.getText().length() < 10) {
            msclass.showMessage("Please Enter 10 digit Mobile No");
            return false;
        }

        if (nursery.equals("N")) {
            if (txtnurseryDate.getText().toString().equals("")) {
                msclass.showMessage("Please Select Sowing Date");
                return false;
            }
        } else if (nursery.equals("Y")) {
            if (txtnurseryDate.getText().toString().equals("")) {
                msclass.showMessage("Please Select Nursery Sowing Date");
                return false;
            }
        }

        return true;
    }

    private boolean validationT() {
        boolean flag = true;
        if (txtFarmerNameT.getText().length() == 0) {
            msclass.showMessage("Please Enter Farmer name ");
            return false;
        }
        if (txtVillageT.getText().length() == 0) {
            msclass.showMessage("Please enter Village");
            return false;
        }
        if (txtContactT.getText().length() == 0 || txtContactT.getText().length() < 10) {
            msclass.showMessage("Please Enter 10 digit Mobile No");
            return false;
        }
        if (tvTransplantDate.getText().toString().equals("")) {
            msclass.showMessage("Please Select Transplanting Date");
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) this;
        androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Sowing & Transplanting Details");
        getLocation();
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        // getLocation();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!getTagType.equalsIgnoreCase(AppConstant.NURSERY_TAG)) {
                txtGeoVillage.setText("" + addresses.get(0).getAddressLine(0) + "");
            }
            txtGeoVillageT.setText("" + addresses.get(0).getAddressLine(0) + "");
        } catch (Exception e) {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

}