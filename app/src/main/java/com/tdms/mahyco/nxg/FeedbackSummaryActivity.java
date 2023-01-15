package com.tdms.mahyco.nxg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tdms.mahyco.nxg.adapters.SummaryLister;
import com.tdms.mahyco.nxg.adapters.TrialFeedbackSummaryListAdapter;
import com.tdms.mahyco.nxg.model.TrialFeedbackSummaryModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FeedbackSummaryActivity extends AppCompatActivity implements SummaryLister {

    EditText edVisitData;
    Spinner spinnerRankFeedback;
    String feedbackRating = "";
    Button btnFeedbackSave, btnFeedbackBack;
    TextView tvTrialCode, tvLocation, tvVillage, tvSegment;
    databaseHelper databaseHelper1;
    ArrayList<TrialFeedbackSummaryModel> summaryModelList;
    RecyclerView rvTrialFeedback;
    public Messageclass msclass;
    SummaryLister summaryLister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_summary);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Feedback Summary");
        }

        summaryModelList = new ArrayList<TrialFeedbackSummaryModel>();

        edVisitData = (EditText) findViewById(R.id.ed_visit_date);
        spinnerRankFeedback = (Spinner) findViewById(R.id.spinnerRankFeedback);
        btnFeedbackSave = (Button) findViewById(R.id.btnSaveFeedbackSummary);
        btnFeedbackBack = (Button) findViewById(R.id.btnBackSummary);

        rvTrialFeedback = (RecyclerView) findViewById(R.id.rv_trial_feedback);
        LinearLayoutManager lllm = new LinearLayoutManager(this);
        lllm.setOrientation(LinearLayoutManager.VERTICAL);
        rvTrialFeedback.setLayoutManager(lllm);

        tvTrialCode = (TextView) findViewById(R.id.lblTrialCode);
        tvLocation = (TextView) findViewById(R.id.lblLocation);
        tvVillage = (TextView) findViewById(R.id.lblVillage);
        tvSegment = (TextView) findViewById(R.id.lblSegment);
        databaseHelper1 = new databaseHelper(this);
        msclass = new Messageclass(this);

        Intent i = getIntent();
        String trailCode = i.getStringExtra("trailCode");
        Log.d("Data", "Trial Code: " + trailCode);
        //Toast.makeText(this, "Trial Code: " + trailCode, Toast.LENGTH_SHORT).show();
        tvTrialCode.setText(trailCode);
        summaryLister = this;
        loadSpinnerData();

        if (edVisitData.getText().toString().equalsIgnoreCase("")) {
            final Calendar myCalendar = Calendar.getInstance();
            loadDate(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
        }

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                loadDate(year, monthOfYear, dayOfMonth);
            }

        };

        edVisitData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar myCalendar = Calendar.getInstance();
                new DatePickerDialog(FeedbackSummaryActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btnFeedbackSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(FeedbackSummaryActivity.this, "Trial Selected :" + spinnerRankFeedback.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();
                if (spinnerRankFeedback.getSelectedItemPosition() == 0) {
                    msclass.showMessage("Please select trial rating for saving feedback");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackSummaryActivity.this);
                    builder.setTitle("Are You Sure You want save feedback");
                    builder.setMessage("Your data will get saved for upload");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            databaseHelper databaseHelper1 = new databaseHelper(FeedbackSummaryActivity.this);
                            String trialRating = spinnerRankFeedback.getSelectedItem().toString();
                            databaseHelper1.saveTrialFeedback(tvTrialCode.getText().toString(), trialRating, edVisitData.getText().toString());
                            //databaseHelper1.showFeedResult(tvTrialCode.getText().toString());
                            databaseHelper1.close();
                            dialog.cancel();
                            alertSave();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.show();
                }
            }
        });

        btnFeedbackBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "BACK", Toast.LENGTH_LONG).show();
                alertBack();
            }
        });

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        edVisitData.setText(day + "-" + (month + 1) + "-" + year);

        //Call on last for setting data
        setData();
    }

    public void loadDate(int year, int monthOfYear, int dayOfMonth) {
        final Calendar myCalendar = Calendar.getInstance();
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        edVisitData.setText(sdformat.format(myCalendar.getTime()));
        edVisitData.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        alertBack();
    }

    public void alertBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are You Sure You want to go back?");
        builder.setMessage("Your data will be lost.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FeedbackSummaryActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Toast.makeText(getApplicationContext(), "BACK", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    public void alertSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Feedback");
        builder.setMessage("Feedback saved successfully");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //FeedbackSummaryActivity.super.onBackPressed();
                finish();
            }
        });
       /* builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "BACK", Toast.LENGTH_LONG).show();
            }
        });*/
        builder.show();
    }

    private void loadSpinnerData() {
        List<String> ddRankingList = Arrays.asList(getResources().getStringArray(R.array.NewRatingFeedback));
        ArrayAdapter<String> ddYearlistAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                ddRankingList);
        ddYearlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerRankFeedback.setAdapter(ddYearlistAdapter);

        spinnerRankFeedback.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//28032019
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    feedbackRating = spinnerRankFeedback.getSelectedItem().toString();
                    //Toast.makeText(getApplicationContext(), feedbackRating, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.d("MSG", e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setData() {
        summaryModelList.clear();
        final Cursor FeedbackTrialData = databaseHelper1.getTrialFeedbackSummary(tvTrialCode.getText().toString());
        if (FeedbackTrialData != null && FeedbackTrialData.getCount() > 0) {
            FeedbackTrialData.moveToFirst();
            do {

                TrialFeedbackSummaryModel model = new TrialFeedbackSummaryModel();
                model.setTrialCode(tvTrialCode.getText().toString());

                if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Location")) != null) {
                    String Location = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Location")).toString();
                    Log.d("curserFeedback data", "Location : " + Location);
                    model.setLocation(Location);
                }

                if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Village")) != null) {
                    String Village = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Village")).toString();
                    Log.d("curserFeedback data", "Village : " + Village);
                    model.setVillage(Village);
                }

                if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("segment")) != null) {
                    String segment = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("segment")).toString();
                    Log.d("curserFeedback data", "segment : " + segment);
                    model.setSegment(segment);
                }

                if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("dateOfRating")) != null) {
                    String dateOfRating = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("dateOfRating")).toString();
                    Log.d("curserFeedback data", "dateOfRating : " + dateOfRating);
                    model.setDateOfVisit(dateOfRating);
                }

                if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("PlotNo")) != null) {
                    String PlotNo = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("PlotNo")).toString();
                    Log.d("curserFeedback data", "PlotNo : " + PlotNo);
                    model.setPlotNo(PlotNo);
                }

                if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Remarks")) != null) {
                    String Remarks = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Remarks")).toString();
                    Log.d("curserFeedback data", "Remarks : " + Remarks);
                    model.setComment(Remarks);
                }

                //Rating mean individual ranks
                if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Rating")) != null) {
                    String Rating = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Rating")).toString();
                    Log.d("curserFeedback data", "Rating : " + Rating);
                    model.setRank(Rating);
                }


                if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("TrialRating")) != null) {
                    //int TrialRating = Integer.parseInt(FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("TrialRating")));
                    String TrialRating = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("TrialRating"));
                    Log.d("curserFeedback data", "TrialRating : " + TrialRating);
                    //spinnerRankFeedback.setSelection(getSpinnerTrialFeedbackIndex(TrialRating));
                    model.setTrialRating("" + TrialRating);
                }

                if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("isSubmitted")) != null) {
                    String isSubmitted = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("isSubmitted"));
                    Log.d("curserFeedback data", "isSubmitted : " + isSubmitted);
                    model.setIsSubmitted(isSubmitted);
                }

                if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("isSyncedStatus")) != null) {
                    String isSyncedStatus = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("isSyncedStatus"));
                    Log.d("curserFeedback data", "isSyncedStatus : " + isSyncedStatus);
                    model.setIsSynced(isSyncedStatus);
                }
                summaryModelList.add(model);
            }
            while (FeedbackTrialData.moveToNext());
        }
        if (summaryModelList != null && summaryModelList.size() > 0) {
            tvLocation.setText(summaryModelList.get(0).getLocation());
            tvVillage.setText(summaryModelList.get(0).getVillage());
            tvSegment.setText(summaryModelList.get(0).getSegment());
            String dateEd = summaryModelList.get(0).getDateOfVisit();
            if (dateEd != null) {
                if (!dateEd.equalsIgnoreCase("") && !dateEd.isEmpty()) {
                    edVisitData.setText(summaryModelList.get(0).getDateOfVisit());
                }
            }
            spinnerRankFeedback.setSelection(getSpinnerTrialFeedbackIndex(summaryModelList.get(0).getTrialRating()));
            String isSynched = summaryModelList.get(0).getIsSynced();
            if (isSynched != null && !isSynched.equalsIgnoreCase("")) {
                if (isSynched.equalsIgnoreCase("1")) {
                    //Toast.makeText(FeedbackSummaryActivity.this,"Is Synched = "+isSynched,Toast.LENGTH_SHORT).show();
                    showAlertFeedbackGiven();
                }
            }
        }
        FeedbackTrialData.close();

        Log.d("Survey", "summaryModelList Size:" + summaryModelList.size());
        Log.d("Survey", "summaryModelList Data:" + summaryModelList.toString());

        TrialFeedbackSummaryListAdapter trial_listAdaptor = new TrialFeedbackSummaryListAdapter(summaryModelList, FeedbackSummaryActivity.this, summaryLister);
        rvTrialFeedback.setAdapter(trial_listAdaptor);
    }

    private int getSpinnerTrialFeedbackIndex(String trialRating) {
        Log.d("getSpinnerTrialFeedback", "1 trialRating=" + trialRating);
        int index = 0;
        if (trialRating == null) {
            Log.d("getSpinnerTrialFeedback", "2 index=" + index);
            return index;
        } else {
            if (trialRating.equalsIgnoreCase("Poor")) {
                index = 1;
            } else if (trialRating.equalsIgnoreCase("Average")) {
                index = 2;
            } else if (trialRating.equalsIgnoreCase("Good")) {
                index = 3;
            } else if (trialRating.equalsIgnoreCase("Very Good")) {
                index = 4;
            } else if (trialRating.equalsIgnoreCase("Excellent")) {
                index = 5;
            } else {
                index = 0;
            }
        }
        Log.d("getSpinnerTrialFeedback", "3 index=" + index);
        return index;
    }

    @Override
    public void onSummaryAlertCallback() {
        setData();
    }

    private void showAlertFeedbackGiven() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Feedback Given"); //Feedback Uploaded
        builder.setMessage("Feedback already submitted");// given and uploaded to server.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FeedbackSummaryActivity.super.onBackPressed();
            }
        });
        builder.show();
    }


}