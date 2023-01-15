package com.tdms.mahyco.nxg;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import com.ceylonlabs.imageviewpopup.ImagePopup;

public class FeedbackDetailsActivity extends AppCompatActivity {

    public Messageclass msclass;
    public CommonExecution cx;
    Spinner ddlspinnerRank;//28032019
    Config config;
    ProgressDialog dialog;
    public ScrollView container;
    SharedPreferences locdata;
    SharedPreferences.Editor editor, locedit;
    databaseHelper databaseHelper1;
    public String userCode;
    public TextView txtTrialCode;// txtPlotNo, txtENDPlotNo, txtPlotNo1;
    TextView txtMainPlotNo, txtStartPlotNo, txtEndPlotNo;
    public Button save, next, back, btnGo, btnNextOnly;//28032019
    public String last;
    EditText txtEnerPlot, remarks;
    String ratingVal;
    String Ranking;
    EditText edDateOfVisit;
    Button btnFeedbackSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_details);

        //getSupportActionBar().setTitle("Feedback");//28032019
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Feedback Details");
        }

        config = new Config(this); //Here the context is passing
        msclass = new Messageclass(this);
        cx = new CommonExecution(this);
        databaseHelper1 = new databaseHelper(this);
        container = (ScrollView) findViewById(R.id.container);
        dialog = new ProgressDialog(this);
        final MediaPlayer MP = MediaPlayer.create(this, R.raw.ting);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        locdata = getApplicationContext().getSharedPreferences("locdata", 0); // 0 - for private mode
        locedit = locdata.edit();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        txtTrialCode = (TextView) findViewById(R.id.txtTrialCode);
        txtMainPlotNo = (TextView) findViewById(R.id.txt_main_plot_no);
        txtStartPlotNo = (TextView) findViewById(R.id.txt_main_start_plot_no);
        txtEndPlotNo = (TextView) findViewById(R.id.txt_main_end_plot_no);
       /* txtENDPlotNo = (TextView) findViewById(R.id.txtENDPlotNo);
        txtPlotNo1 = (TextView) findViewById(R.id.txtPlotNo1);*/
        // final ImagePopup imagePopup = new ImagePopup(this);
        edDateOfVisit = (EditText) findViewById(R.id.tv_date_of_visit);
        save = (Button) findViewById(R.id.btnSaveObs);
        next = (Button) findViewById(R.id.btnNextObs);
        back = (Button) findViewById(R.id.btnBack);//28032019
        btnGo = (Button) findViewById(R.id.btnGo);
        btnFeedbackSummary = (Button) findViewById(R.id.btnSaveFeedbackSummary);
        btnNextOnly = (Button) findViewById(R.id.btnNextOnly);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//28032019
                String s = txtEnerPlot.getText().toString();
                if (s != null && !s.isEmpty() && !s.equalsIgnoreCase("")) {

                    if (databaseHelper1.checkTrialFeedbackExist(txtTrialCode.getText().toString(), s)) {

                        String GoPlotNo = txtEnerPlot.getText().toString().trim();

                        int UpdatePlot;
                        UpdatePlot = Integer.parseInt(GoPlotNo.toString());

                        String plot1 = txtMainPlotNo.getText().toString();
                        Log.d("testimonial", "onClick: " + plot1);
                        if (UpdatePlot < Integer.parseInt(txtStartPlotNo.getText().toString())) {
                            msclass.showMessage("This Plot No Is Not Available. \nPlease Check Plot No You Enter");
                        } else if (UpdatePlot > Integer.parseInt(txtEndPlotNo.getText().toString())) {
                            msclass.showMessage("This Plot No Is Not Available.  \nPlease Check Plot No You Enter");
                        } else {
                            txtMainPlotNo.setText(String.valueOf(UpdatePlot));//28032019
                            txtEnerPlot.setText("");//28032019
                            //   BindData(finalCrop, false);
                        }
                        Toast.makeText(FeedbackDetailsActivity.this, "Call Go =>", Toast.LENGTH_SHORT).show();
                        getValues();
                    } else {
                        msclass.showMessage("This Plot No Is Not Available. \nPlease Check Plot No You Enter");
                    }
                } else {
                    msclass.showMessage("Please Enter valid Plot No");
                }

            }
        });
        ddlspinnerRank = (Spinner) findViewById(R.id.spinnerRank);//28032019
        remarks = (EditText) findViewById(R.id.remarks);//28032019

        txtEnerPlot = (EditText) findViewById(R.id.txtEnerPlot);

        loadSpinnerData();//28032019
        ddlspinnerRank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//28032019
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Ranking = ddlspinnerRank.getSelectedItem().toString();
                    //Toast.makeText(getApplicationContext(), Ranking, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        addListenerOnButtonClick();

        Intent i = getIntent();
        String trailCode = i.getStringExtra("trailCode");
        txtTrialCode.setText(trailCode);
        String crop = null;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        final Cursor data = databaseHelper1.fetchusercode();

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

        String startPlot = databaseHelper1.getStartPlotTrialFeedback(trailCode);
        String endPlot = databaseHelper1.getEndPlotTrialFeedback(trailCode);
        Log.d("Plot", "Start Plot : " + startPlot);
        Log.d("Plot", "End Plot : " + endPlot);
        //boolean result = databaseHelper1.checkTrialFeedbackExist(trailCode, "201");
        //Log.d("Plot", "CheckIfTrialExist : " + result);
        txtMainPlotNo.setText(startPlot);
        txtStartPlotNo.setText(startPlot);
        txtEndPlotNo.setText(endPlot);

        /*boolean isFeedGiven = databaseHelper1.isTrialFeedbacKGiven(trailCode,startPlot);
        Log.d("Survey","isFeedGiven = "+isFeedGiven);
        if(isFeedGiven){
            showAlertFeedbackGiven();
        }*/
        try {
            getValues();
        } catch (Exception e) {
            Log.d("getValues", "MSG : " + e.getMessage());
        }
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//28032019

                String remarksGiven = remarks.getText().toString();
                if (remarksGiven.equalsIgnoreCase("")) {
                    remarks.setError("Please give remark to go next");
                    return;
                } else {
                    remarks.setError(null);
                }

                if (ddlspinnerRank.getSelectedItem().toString().equalsIgnoreCase("Select Rank")) {
                    Toast.makeText(FeedbackDetailsActivity.this, "Please select ranking", Toast.LENGTH_LONG).show();
                    return;
                }

                //setRatingVal();
               /* SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                final String entrydate = sdf.format(new Date());*/

                int feedbackNumber = (int) databaseHelper1.getTrialFeedbackCount(txtTrialCode.getText().toString(), txtMainPlotNo.getText().toString());
                Log.d("Survey", "Trial feedback count: " + feedbackNumber);

                Log.d("Survey", "\"Insert or Update : Spinner data:" + ddlspinnerRank.getSelectedItem().toString() + " Comment : " + remarks.getText().toString());

                databaseHelper1.close();

                String Is_Synced = "0";
                //String Is_Submitted = "0";
                String Is_Submitted = "1";


                if (feedbackNumber == 0) {
                    databaseHelper1.updateTrialFeedback(txtTrialCode.getText().toString(), txtMainPlotNo.getText().toString(), Is_Synced.toString(), ddlspinnerRank.getSelectedItem().toString(), remarks.getText().toString(), Is_Submitted.toString(), edDateOfVisit.getText().toString());
                } else {
                    databaseHelper1.updateTrialFeedback(txtTrialCode.getText().toString(), txtMainPlotNo.getText().toString(), Is_Synced.toString(), ddlspinnerRank.getSelectedItem().toString(), remarks.getText().toString(), Is_Submitted.toString(), edDateOfVisit.getText().toString());
                }
                try {
                    int UpdatePlot;
                    UpdatePlot = Integer.parseInt(txtMainPlotNo.getText().toString()) + Integer.parseInt("1");
                    if (UpdatePlot > Integer.parseInt(txtEndPlotNo.getText().toString())) {

                        /*DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        *//*databaseHelper1.updateTrialFeedbackAtLast(
                                                txtTrialCode.getText().toString(), "1");*//*
                         *//*FeedbackDetailsActivity.super.onBackPressed();*//*
                                        //Update last feedback
                                        String Is_Synced = "0";
                                        //String Is_Submitted = "0";
                                        String Is_Submitted = "1";
                                        databaseHelper1.updateTrialFeedback(txtTrialCode.getText().toString(), txtMainPlotNo.getText().toString(), Is_Synced.toString(), ddlspinnerRank.getSelectedItem().toString(), remarks.getText().toString(), Is_Submitted.toString());

                                        finish();
                                        String trialCode = "" + txtTrialCode.getText();
                                        Intent i = new Intent(FeedbackDetailsActivity.this, FeedbackSummaryActivity.class);
                                        i.putExtra("trailCode", trialCode);
                                        startActivity(i);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        alert2();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackDetailsActivity.this);
                        builder.setTitle("Feedback");
                        builder.setMessage("This is the last plot, are you sure to submit data? Yes for submit and No for deleting the record")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();*/

                        String Is_Synced1 = "0";
                        String Is_Submitted1 = "1";
                        databaseHelper1.updateTrialFeedback(txtTrialCode.getText().toString(), txtMainPlotNo.getText().toString(), Is_Synced1.toString(), ddlspinnerRank.getSelectedItem().toString(), remarks.getText().toString(), Is_Submitted1.toString(), edDateOfVisit.getText().toString());

                        finish();
                        String trialCode = "" + txtTrialCode.getText();
                        Intent i = new Intent(FeedbackDetailsActivity.this, FeedbackSummaryActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra("trailCode", trialCode);
                        startActivity(i);

                    } else {
                        txtMainPlotNo.setText(String.valueOf(UpdatePlot));
                    }
                    ddlspinnerRank.setSelection(0);
                    remarks.setText("");
                    getValues();
                    MP.start();
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }

            }

        });

        btnNextOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int UpdatePlot;
                    UpdatePlot = Integer.parseInt(txtMainPlotNo.getText().toString()) + Integer.parseInt("1");
                    if (UpdatePlot > Integer.parseInt(txtEndPlotNo.getText().toString())) {
                        finish();
                        String trialCode = "" + txtTrialCode.getText();
                        Intent i = new Intent(FeedbackDetailsActivity.this, FeedbackSummaryActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra("trailCode", trialCode);
                        startActivity(i);

                    } else {
                        txtMainPlotNo.setText(String.valueOf(UpdatePlot));
                    }
                    ddlspinnerRank.setSelection(0);
                    remarks.setText("");
                    getValues();
                    //MP.start();
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            }
        });


            /*txtPlotNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(FeedbackDetailsActivity.this, AndroidDatabaseManager.class);

                    startActivity(intent);
                }
            });
*/
        back.setOnClickListener(new View.OnClickListener() {//28032019
            @Override
            public void onClick(View v) {//28032019
                int UpdatePlot;
                UpdatePlot = Integer.parseInt(txtMainPlotNo.getText().toString()) - Integer.parseInt("1");

                if (UpdatePlot < Integer.parseInt(txtStartPlotNo.getText().toString())) {
                    msclass.showMessage("This is Starting Plot You Cant Go Previous ");
                } else {
                    txtMainPlotNo.setText(String.valueOf(UpdatePlot));

                }
                getValues();
            }
        });

        /*Added on 26th May 2021*/
        btnFeedbackSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int feedbackNumber = (int) databaseHelper1.getTrialFeedbackCount(txtTrialCode.getText().toString(), txtMainPlotNo.getText().toString());
                String remarksGiven = remarks.getText().toString();
                Log.d("btnFeedbackSummary", "Trial feedback count: " + feedbackNumber);
                Log.d("btnFeedbackSummary", "Trial remarksGiven: " + remarksGiven);
                Log.d("btnFeedbackSummary", "Trial Rank: " + ddlspinnerRank.getSelectedItem().toString());

                openFeedbackSummery();

                /*Commented as suggested by Niteen on 23rd Feb 2022*/
                /*if (ddlspinnerRank.getSelectedItem().toString().equalsIgnoreCase("Select Rank")) {
                    Toast.makeText(FeedbackDetailsActivity.this, "Please select ranking", Toast.LENGTH_LONG).show();
                    return;
                } else if (remarksGiven.equalsIgnoreCase("")) {
                    remarks.setError("Please give remark");
                    return;
                } else if (feedbackNumber > 0) {
                    remarks.setError(null);
                    openFeedbackSummery();
                }*/
            }
        });

        /*Added on 6th May 2021*/
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateTransplant = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edDateOfVisit.setText(sdformat.format(myCalendar.getTime()));
            }
        };

        edDateOfVisit.setKeyListener(null);
        edDateOfVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(FeedbackDetailsActivity.this, dateTransplant, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        setDateForPlotVisit();

    }

    private void setDateForPlotVisit() {
       /* final Calendar myCalendar = Calendar.getInstance();
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
        Calendar c = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, c.get(Calendar.YEAR));
        myCalendar.set(Calendar.MONTH, c.get(Calendar.MONTH));
        myCalendar.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_WEEK));
        Log.d("onCreate","DATE : "+sdformat.format(myCalendar.getTime()));
        edDateOfVisit.setText(sdformat.format(myCalendar.getTime()));*/
        try {
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            Log.d("FeedbackDetailsActivity", "setDateForPlotVisit DATE : " + formattedDate);
            edDateOfVisit.setText(formattedDate);
        } catch (Exception e) {
            Log.d("Date", "MSG : " + e.getMessage());
        }
    }

    private void showAlertFeedbackGiven() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Feedback Given"); //Feedback Uploaded
        builder.setMessage("Feedback already submitted");// given and uploaded to server.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FeedbackDetailsActivity.super.onBackPressed();
            }
        });
        builder.show();
    }

    private Boolean validationSuccess() {//28032019
        if (remarks.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Please enter remarks", Toast.LENGTH_SHORT).show();
            return false;
        }


//        if(ddlspinnerRank.getSelectedItemPosition()==0){
//            Toast.makeText(getApplicationContext(),"Please select ranking",Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    private void loadSpinnerData() {//28032019

        List<String> ddRankingList = Arrays.asList(getResources().getStringArray(R.array.Ranking));
        ArrayAdapter<String> ddYearlistAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                ddRankingList);
        ddYearlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlspinnerRank.setAdapter(ddYearlistAdapter);

    }


    @Override
    public void onBackPressed() {
        //28032019
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        final String entrydate = sdf.format(new Date());
        int feedbackNumber = (int) databaseHelper1.getFeedbackCountBack(entrydate.toString(), txtTrialCode.getText().toString(), "0");
        Log.d(FeedbackDetailsActivity.class.getName(), "feedbackNumber = " + feedbackNumber);
        databaseHelper1.close();
        if (feedbackNumber > 0) {
            openFeedbackSummery();
        } else {
            this.finish();
        }
    }

    public void loadData() {

    }


    //////------------------------------28032019-----------------------------//////
    public void getValues() {
        /*SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final String entrydate = sdf.format(new Date());*/
        final Cursor FeedbackData = databaseHelper1.getTrialFeedbackData(txtMainPlotNo.getText().toString(), txtTrialCode.getText().toString());//, entrydate.toString());

        Log.d("curserFeedback data", "FeedbackData count=>" + FeedbackData.getCount());
        //Toast.makeText(FeedbackDetailsActivity.this, "Go =>" + FeedbackData.toString(), Toast.LENGTH_SHORT).show();

        ratingVal = "0";
        if (FeedbackData.getCount() == 0) {
            remarks.setText("");
            ddlspinnerRank.setSelection(0);
            setDateForPlotVisit();
        } else {
            edDateOfVisit.setText("");
            FeedbackData.moveToFirst();
            if (FeedbackData != null) {
                do {
                    if (FeedbackData.getString(FeedbackData.getColumnIndex("Remarks")) != null) {
                        String remarksData = FeedbackData.getString(FeedbackData.getColumnIndex("Remarks")).toString();
                        Log.d("curserFeedback data", "remarksData : " + remarksData);
                        remarks.setText(remarksData);
                    }

                    if (FeedbackData.getString(FeedbackData.getColumnIndex("Rating")) != null) {
                        int ratingData = Integer.parseInt(FeedbackData.getString(FeedbackData.getColumnIndex("Rating")));
                        Log.d("curserFeedback data", "ratingData : " + ratingData);
                        ddlspinnerRank.setSelection(ratingData);
                    }

                    if (FeedbackData.getString(FeedbackData.getColumnIndex("dateOfVisitSinglePlot")) != null
                            && !FeedbackData.getString(FeedbackData.getColumnIndex("dateOfVisitSinglePlot")).equalsIgnoreCase("")) {
                        String dateOfVisitSinglePlot = FeedbackData.getString(FeedbackData.getColumnIndex("dateOfVisitSinglePlot"));
                        Log.d("curserFeedback data", "dateOfVisitSinglePlot : " + dateOfVisitSinglePlot);
                        edDateOfVisit.setText(dateOfVisitSinglePlot);
                    } else {
                        setDateForPlotVisit();
                    }

                } while (FeedbackData.moveToNext());

            }
            FeedbackData.close();

        }
    }


    public void openFeedbackSummery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Open Trial Feedback Summery");
        builder.setMessage("Do you want to submit Trial Feedback Summery?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                save.performClick();
                finish();
                String trialCode = "" + txtTrialCode.getText();
                Intent i = new Intent(FeedbackDetailsActivity.this, FeedbackSummaryActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("trailCode", trialCode);
                startActivity(i);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*Do nothing as suggested by Niteen*/
            }
        });
        builder.show();
    }

    //////------------------------------28032019-----------------------------//////
    public void alert2() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final String entrydate = sdf.format(new Date());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are You Sure You want to go back?");
        builder.setMessage("Your data will be lost.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*databaseHelper1.deleledata("trial_feedback_table", " WHERE " + "TRIL_CODE='" + txtTrialCode.getText().toString() + "' AND " +
                        "isSubmitted='" + "0" + "' AND " + "isSyncedStatus='" + "0" + "'");*/
                //databaseHelper1.updateTrialFeedback( txtTrialCode.getText().toString(), txtMainPlotNo.getText().toString(), Is_Synced.toString(), ratingVal.toString(), remarks.getText().toString(), Is_Submitted.toString());
                //TODO need to know if want to reset entries on back press
                FeedbackDetailsActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Integer.parseInt(txtMainPlotNo.getText().toString()) < Integer.parseInt(txtEndPlotNo.getText().toString())) {
                    txtMainPlotNo.setText(txtEndPlotNo.getText().toString());
                    FeedbackDetailsActivity.super.onBackPressed();
                }
            }
        });
        builder.show();
    }
    //////------------------------------28032019-----------------------------//////

}
