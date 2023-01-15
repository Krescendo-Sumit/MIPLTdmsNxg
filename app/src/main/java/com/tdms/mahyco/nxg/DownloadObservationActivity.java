package com.tdms.mahyco.nxg;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DownloadObservationActivity extends AppCompatActivity {

    String trialCode = "", selectedCrop = "";

    public Spinner ddlTrailType, ddlStage; //Spinner ddlCrop,
    public ListView list;
    databaseHelper databaseHelper1;
    public Messageclass msclass;
    ListView lv;
    ArrayList<observationList> planetList;
    public LinearLayout my_linear_layout2;
    String stage;
    Button saveObs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_observation);
        getSupportActionBar().setTitle("Download Observations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUI();
    }

    private void setUI() {
        if (getIntent() != null) {
            trialCode = getIntent().getStringExtra("trailCode");
            selectedCrop = getIntent().getStringExtra("selectedCrop");
            Log.d("DownloadObservation", "DATA RECEIVED trailCode: " + trialCode);
            Log.d("DownloadObservation", "DATA RECEIVED selectedCrop: " + selectedCrop);
        }

        //ddlCrop = (Spinner) findViewById(R.id.ddlCrop);
        ddlStage = (Spinner) findViewById(R.id.ddlStage);
        ddlTrailType = (Spinner) findViewById(R.id.ddlTrailType);
        list = (ListView) findViewById(R.id.list);
        databaseHelper1 = new databaseHelper(this);
        msclass = new Messageclass(this);
        lv = (ListView) findViewById(R.id.listview);
        my_linear_layout2 = (LinearLayout) findViewById(com.tdms.mahyco.nxg.R.id.my_linear_layout2);
        //ddlCrop.setOnItemSelectedListener(this);
        //ddlStage.setOnItemSelectedListener(this);
        saveObs = (Button) findViewById(R.id.btnSaveObs);

        bindDataStages();

        //loadSpinnerData();


        saveObs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int count = my_linear_layout2.getChildCount();
                    //EditText ed[] = new EditText[count];
                    for (int i = 0; i < count; i++) {
                        View row = my_linear_layout2.getChildAt(i);
                        CheckBox chk = (CheckBox) row.findViewById(com.tdms.mahyco.nxg.R.id.chk_box);
                        TextView txtObservation = (TextView) row.findViewById(com.tdms.mahyco.nxg.R.id.txtObservation);
                        TextView chkID = (TextView) row.findViewById(com.tdms.mahyco.nxg.R.id.chkID);

                        if (chk.isChecked()) {

                            String VariableID = chkID.getText().toString();
                            String data = txtObservation.getText().toString();

                            //String InsertQuery = "insert into DownloadedObservation select Crop,VariableID,Name,S_M,Discription,Abbreviation,O_Group,Variable_type,Scale,Scale_type,Value1,Value2,Value3,Value4,Value5,flag from ObservationMaster where VariableID = '" + VariableID + "'  ";
                            String InsertQuery = "insert into TrialWiseDownloadedObservation select '" + trialCode + "' as TRIAL_CODE, Crop,VariableID,Name,S_M,Discription,Abbreviation,O_Group,Variable_type,Scale,Scale_type,Value1,Value2,Value3,Value4,Value5,flag from ObservationMaster where VariableID = '" + VariableID + "'  ";
                            Log.d(DownloadObservationActivity.class.getName(), "SAVE QUERY : " + InsertQuery);
                            databaseHelper1.runQuery(InsertQuery);

                            /*Commented on 15th July 2021*/
                            /*String update = "update ObservationMaster set flag='1' where VariableID = '" + VariableID + "'  ";
                            databaseHelper1.runQuery(update);*/

                            /*Commented on 15th July 2021 ONLY To Read Records*/
                            //databaseHelper1.getTrialWiseDownloadedObservation();
                        }

                    }
                    msclass.showMessage("Selected Observation Downloaded... ");
                    /*FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(new_Download_Observation.this).attach(new_Download_Observation.this).commit();*/
                    finish();
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            }
        });


        /*ddlCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCrop = parent.getItemAtPosition(position).toString();

                if (!selectedCrop.equals(getString(R.string.selectcrop))) {
                    ArrayList<String> stages = databaseHelper1.getStagesObservation(selectedCrop==null?"selectedCrop":selectedCrop);
                    stages.add(0, getString(R.string.selectstage));

                    Collections.sort(stages, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            return s1.compareToIgnoreCase(s2);
                        }
                    });
                    //Collections.sort(stages);

                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapterStages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stages);

                    // Drop down layout style - list view with radio button
                    dataAdapterStages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    ddlStage.setAdapter(dataAdapterStages);
                    if (stage != null && !stage.equals(getString(R.string.selectstage))) {
                        BindData(selectedCrop, stage);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        ddlStage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stage = parent.getItemAtPosition(position).toString();

                if (!selectedCrop.equals(getString(R.string.selectcrop))) {
                    BindData(selectedCrop, stage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindDataStages() {
        Log.d("bindDataStages", "Function called selectedCrop:" + selectedCrop);

        if (!selectedCrop.equals(getString(R.string.selectcrop))) {
            ArrayList<String> stages = databaseHelper1.getStagesObservation(selectedCrop == null ? "selectedCrop" : selectedCrop);
            stages.add(0, getString(R.string.selectstage));

            Collections.sort(stages, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            Log.d("bindDataStages", "Stages : " + stages.size());
            Log.d("bindDataStages", "Stages : " + stages.toString());

            //Collections.sort(stages);

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapterStages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stages);

            // Drop down layout style - list view with radio button
            dataAdapterStages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            ddlStage.setAdapter(dataAdapterStages);
            if (stage != null && !stage.equals(getString(R.string.selectstage))) {
                BindData(selectedCrop, stage);
            }

        }

    }

    private void loadSpinnerData() {
        // database handler
        // Spinner Drop down elements
        /*List<String> lables = databaseHelper1.getObservationCrop();
        lables.add(0, getString(R.string.selectcrop));
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        ddlCrop.setAdapter(dataAdapter);*/


        ArrayList<String> stages =
                new ArrayList<>();
        //      ArrayList<String> stages = databaseHelper1.getStagesObservation(selectedCrop==null?"selectedCrop":selectedCrop);
        stages.add(0, getString(R.string.selectstage));

        Collections.sort(stages, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        //Collections.sort(stages);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterStages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stages);

        // Drop down layout style - list view with radio button
        dataAdapterStages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlStage.setAdapter(dataAdapterStages);

    }

    public void BindData(String crop, String stage) {
        Log.d("BindData", "crop : " + crop + " stage:" + stage);
        my_linear_layout2.removeAllViews();

        Cursor Farmerdata = databaseHelper1.GetSelectedObservation(crop, stage);

        Log.d("BindData", "Cursor length :  " + Farmerdata.getCount());

        if (Farmerdata.getCount() == 0) {
            //msclass.showMessage("No Data Available... ");
        } else {
            try {
                Farmerdata.moveToFirst();
                if (Farmerdata != null) {
                    do {
                        String variableId = Farmerdata.getString(Farmerdata.getColumnIndex("VariableID"));
                        if (databaseHelper1.isSubStageAlreadySelected(trialCode,variableId)) { /*Added on 15th July 2021*/
                            continue;
                        } else {
                            View view2 = LayoutInflater.from(this).inflate(R.layout.list_item, null);
                            view2.setBackgroundColor(getResources().getColor(R.color.Whitecl));
                            //TextView title = (TextView) view2.findViewById(com.trial.mahyco.trail.R.id.title);
                            CheckBox chk = (CheckBox) view2.findViewById(R.id.chk_box);
                            TextView txtObservation = (TextView) view2.findViewById(R.id.txtObservation);
                            TextView chkID = (TextView) view2.findViewById(R.id.chkID);
                            txtObservation.setText(Farmerdata.getString(Farmerdata.getColumnIndex("Name")));
                            chkID.setText(Farmerdata.getString(Farmerdata.getColumnIndex("VariableID")));
                            my_linear_layout2.addView(view2);
                        }
                    } while (Farmerdata.moveToNext());
                }
                Farmerdata.close();
            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
            }
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