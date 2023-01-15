package com.tdms.mahyco.nxg;


import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class new_Download_Observation extends Fragment implements android.widget.CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {


    public new_Download_Observation() {
        // Required empty public constructor
    }

    public Spinner ddlCrop, ddlTrailType, ddlStage;
    public ListView list;
    databaseHelper databaseHelper1;
    public Messageclass msclass;
    public String selectedCrop;
    ListView lv;
    ArrayList<observationList> planetList;
    public LinearLayout my_linear_layout2;
    String stage;
    Button saveObs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new__download__observation, container, false);

        ddlCrop = (Spinner) rootView.findViewById(R.id.ddlCrop);
        ddlStage = (Spinner) rootView.findViewById(R.id.ddlStage);
        ddlTrailType = (Spinner) rootView.findViewById(R.id.ddlTrailType);
        list = (ListView) rootView.findViewById(R.id.list);
        databaseHelper1 = new databaseHelper(this.getContext());
        msclass = new Messageclass(this.getContext());
        lv = (ListView) rootView.findViewById(R.id.listview);
        my_linear_layout2 = (LinearLayout) rootView.findViewById(com.tdms.mahyco.nxg.R.id.my_linear_layout2);
        ddlCrop.setOnItemSelectedListener(this);
        ddlStage.setOnItemSelectedListener(this);
        saveObs = (Button) rootView.findViewById(R.id.btnSaveObs);
        loadSpinnerData();


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

                            String InsertQuery = "insert into DownloadedObservation select Crop,VariableID,Name,S_M,Discription,Abbreviation,O_Group,Variable_type,Scale,Scale_type,Value1,Value2,Value3,Value4,Value5,flag from ObservationMaster where VariableID = '" + VariableID + "'  ";
                            databaseHelper1.runQuery(InsertQuery);

                            String update = "update ObservationMaster set flag='1' where VariableID = '" + VariableID + "'  ";
                            databaseHelper1.runQuery(update);
                        }

                    }
                    msclass.showMessage("Selected Observation Downloaded... ");
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(new_Download_Observation.this).attach(new_Download_Observation.this).commit();
                } catch (Exception e) {
                    Log.d("Msg",e.getMessage());
                }
            }
        });


        ddlCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    ArrayAdapter<String> dataAdapterStages = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stages);

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
        });
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
        return rootView;
    }

    private void loadSpinnerData() {
        // database handler
        // Spinner Drop down elements
        List<String> lables = databaseHelper1.getObservationCrop();
        lables.add(0, getString(R.string.selectcrop));

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlCrop.setAdapter(dataAdapter);


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
        ArrayAdapter<String> dataAdapterStages = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stages);

        // Drop down layout style - list view with radio button
        dataAdapterStages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlStage.setAdapter(dataAdapterStages);

    }

    public void BindData(String crop, String stage) {
        my_linear_layout2.removeAllViews();
        Cursor Farmerdata = databaseHelper1.GetSelectedObservation(crop, stage);

        if (Farmerdata.getCount() == 0) {
            //msclass.showMessage("No Data Available... ");
        } else {
            try {
                Farmerdata.moveToFirst();
                if (Farmerdata != null) {
                    do {

                        View view2 = LayoutInflater.from(this.getActivity()).inflate(R.layout.list_item, null);
                        view2.setBackgroundColor(getResources().getColor(R.color.Whitecl));


                        //TextView title = (TextView) view2.findViewById(com.trial.mahyco.trail.R.id.title);
                        CheckBox chk = (CheckBox) view2.findViewById(R.id.chk_box);
                        TextView txtObservation = (TextView) view2.findViewById(R.id.txtObservation);
                        TextView chkID = (TextView) view2.findViewById(R.id.chkID);
                        txtObservation.setText(Farmerdata.getString(Farmerdata.getColumnIndex("Name")));
                        chkID.setText(Farmerdata.getString(Farmerdata.getColumnIndex("VariableID")));
                        my_linear_layout2.addView(view2);
                    } while (Farmerdata.moveToNext());
                }
                Farmerdata.close();
            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Download Observation");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String label = parent.getItemAtPosition(position).toString();


        //  BindData(label, stage);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


    }
}
