package com.tdms.mahyco.nxg;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.tdms.mahyco.nxg.model.RainfallLocationModel;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Rainfall extends AppCompatActivity {
    Context context;
    Spinner sp_location;
    ArrayAdapter adapter_location;
    EditText txt_readingdate, txt_readingcount, txtStaff;
    int mYear, mMonth, mDay;
    Button btnSave;
    String CONNTRYID = "";
    String LOCATIONID = "";
    String USERCODE = "";
    String APPCODE = "";
    String APPVERSION = "";
    String YEAR = "";
    String MONTH = "";
    String DAY = "";
    String ACTUALDATE = "";
    String READING = "";
    String CREATEDDATE = "";
    String userCode = "";
    databaseHelper db;
    String validatestr="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rainfall);
        context = Rainfall.this;
        init();
        try {
            db = new databaseHelper(context);
            adapter_location = new ArrayAdapter(context, R.layout.single_item, db.getLoc());
            sp_location.setAdapter(adapter_location);
            sp_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        RainfallLocationModel rainfallLocationModel = (RainfallLocationModel) parent.getItemAtPosition(position);
                        if (rainfallLocationModel != null) {
                            LOCATIONID = "" + rainfallLocationModel.getLocationId();
                            CONNTRYID = "" + rainfallLocationModel.getContryId();
                            Toast.makeText(context, "Loid : " + LOCATIONID + " " + CONNTRYID, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            Cursor data = db.fetchusercode();

            if (data.getCount() == 0) {

            } else {
                data.moveToFirst();
                if (data != null) {

                    do {
                        //userCode = data.getString((data.getColumnIndex("user_code")));
                        String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                        userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                        Log.d("userCode", "userCode: " + userCode);
                    } while (data.moveToNext());

                }
                data.close();
                //String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCode);
                //txtStaff.setText(userCodeDecrypt);
                USERCODE = userCode;
                txtStaff.setText(userCode);
                // txtStaff.setEnabled(false);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void init() {
        try {
            sp_location = findViewById(R.id.sp_location);
            txt_readingdate = findViewById(R.id.txt_readingdate);
            txt_readingcount = findViewById(R.id.txt_readingcount);
            txtStaff = findViewById(R.id.txtStaff);
            btnSave = findViewById(R.id.btnSave);

            txt_readingdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //To show current date in the datepicker
                    Calendar mcurrentDate = Calendar.getInstance();
                    mYear = mcurrentDate.get(Calendar.YEAR);
                    mMonth = mcurrentDate.get(Calendar.MONTH);
                    mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                    final DatePickerDialog mDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            // TODO Auto-generated method stub
                            /*      Your code   to get date and time    */

                            String ssm = "", ssd = "";
                            if ((selectedmonth + 1) < 10)
                                ssm = "0" + (selectedmonth + 1);
                            else
                                ssm = "" + (selectedmonth + 1);
                            if ((selectedday) < 10)
                                ssd = "0" + selectedday;
                            else
                                ssd = "" + selectedday;

                            String dd = ssd + "/" + (ssm) + "/" + selectedyear;
                            txt_readingdate.setText(dd);
                            YEAR = "" + selectedyear;
                            MONTH = "" + (ssm);
                            DAY = "" + ssd;
                            ACTUALDATE = selectedyear + "-" + ssm + "-" + ssd;
                        }
                    }, mYear, mMonth, mDay);
                    mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                    mDatePicker.setTitle("Rainfall reading date");
                    mDatePicker.show();

                }
            });

        } catch (Exception e) {

        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APPCODE = "" + BuildConfig.VERSION_CODE;
                APPVERSION = "" + BuildConfig.VERSION_NAME;
                READING = txt_readingcount.getText().toString().trim();
                CREATEDDATE = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                if(validation()) {

                    if (db.InsertRainfallMaster(CONNTRYID, LOCATIONID, USERCODE, APPCODE, APPVERSION, YEAR, MONTH, DAY, ACTUALDATE, READING, CREATEDDATE)) {
                        Toast.makeText(context, "Data saved locally.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Data not saved ", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    new AlertDialog.Builder(context)
                            .setMessage(validatestr)
                            .setTitle("Validation Error")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }

            }
        });

    }

    public boolean validation() {
        int cnt=0;
        validatestr="";
        if (CONNTRYID.toString().trim().equals("")){
            cnt++;
            validatestr+="Country id missing.\n";
        }
        if (LOCATIONID.toString().trim().equals("")) {
            cnt++;
            validatestr+="Location Not Selected.\n";
        }
        if (USERCODE.toString().trim().equals("")) {
            cnt++;
            validatestr+="User code not found.\n";
        }
        if (YEAR.toString().trim().equals("")) {
            cnt++;
            validatestr+="Year is missing.\n";
        }
        if (MONTH.toString().trim().equals("")) {
            cnt++;
            validatestr+="Month is missing.\n";
        }
        if (DAY.toString().trim().equals("")) {
            cnt++;
            validatestr+="Day is missing.\n";
        }
        if (ACTUALDATE.toString().trim().equals("")) {
            cnt++;
            validatestr+="Date is not selected.\n";
        }
        if (READING.toString().trim().equals("")) {
            cnt++;
            validatestr+="Reading Count missing.\n";
        }
        if (CREATEDDATE.toString().trim().equals("")) {
            cnt++;
            validatestr+="Created Date is missing.\n";
        }
        if(cnt==0)
            return  true;
        else
            return false;
    }
}