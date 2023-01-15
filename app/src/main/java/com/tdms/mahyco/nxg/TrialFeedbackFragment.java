package com.tdms.mahyco.nxg;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.tdms.mahyco.nxg.adapters.FeedbackListAdapter;
import com.tdms.mahyco.nxg.model.FeedbackReportModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

public class TrialFeedbackFragment extends Fragment implements AdapterView.OnItemSelectedListener{


    public Spinner ddlCrop, ddlTrailType;
    Spinner ddlSesion;
    Spinner ddlYear;
    public ListView list;
    databaseHelper databaseHelper1;
    public Messageclass msclass;
    public String selectedCrop;
    public RecyclerView recyclerList;
    FeedbackListAdapter trial_listAdaptor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView= inflater.inflate(R.layout.fragment_feedback, container, false);

        ddlCrop = (Spinner) rootView.findViewById(R.id.ddlCrop);
        ddlSesion = (Spinner) rootView.findViewById(R.id.ddlSesion);
        ddlYear = (Spinner) rootView.findViewById(R.id.ddlYear);
        //ddlTehsil = (Spinner) rootView.findViewById(R.id.ddlTeshil);
        list=(ListView)rootView.findViewById(R.id.listview);
        ddlTrailType = (Spinner) rootView.findViewById(R.id.ddlTrailType);
        databaseHelper1 = new databaseHelper(this.getContext());
        msclass = new Messageclass(this.getContext());
        recyclerList = (RecyclerView)rootView.findViewById(R.id.recycler1);

        LinearLayoutManager lllm = new LinearLayoutManager(this.getContext());
        lllm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerList.setLayoutManager(lllm);
        ddlCrop.setOnItemSelectedListener(this);
        ddlSesion.setOnItemSelectedListener(this);
        ddlYear.setOnItemSelectedListener(this);
        // Loading spinner data from database
        loadSpinnerData();
        // loadTrailType();
        ddlTrailType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String year = ddlYear.getSelectedItem().toString();
                    String session = ddlSesion.getSelectedItem().toString();
                    String label = ddlCrop.getSelectedItem().toString();
                    String type=ddlTrailType.getSelectedItem().toString();
                    ArrayList<FeedbackReportModel> mlist = databaseHelper1.getTrialFeedbackData(label, type, year, session);
                    //Toast.makeText(getActivity(),"2 List size:"+mlist.size(),Toast.LENGTH_SHORT).show();
                    trial_listAdaptor = new FeedbackListAdapter(mlist, getActivity(),false,false);
                    recyclerList.setAdapter(trial_listAdaptor);
                } catch (Exception e) {
                    Log.d("MSG",e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ddlYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {

                    String label = ddlCrop.getSelectedItem().toString();
                    String type = ddlTrailType.getSelectedItem().toString();
                    String year = ddlYear.getSelectedItem().toString();
                    String session = ddlSesion.getSelectedItem().toString();
                    ArrayList<FeedbackReportModel> mlist = databaseHelper1.getTrialFeedbackData(label, type, year, session);
                    //Toast.makeText(getActivity(),"3 List size:"+mlist.size(),Toast.LENGTH_SHORT).show();
                    trial_listAdaptor = new FeedbackListAdapter(mlist, getActivity(),false,false);
                    recyclerList.setAdapter(trial_listAdaptor);
                    //}
                } catch (Exception e) {
                    Log.d("MSG",e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ddlSesion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {

                    String label = ddlCrop.getSelectedItem().toString();
                    String type = ddlTrailType.getSelectedItem().toString();
                    String year = ddlYear.getSelectedItem().toString();
                    String session = ddlSesion.getSelectedItem().toString();
                    ArrayList<FeedbackReportModel> mlist = databaseHelper1.getTrialFeedbackData(label, type, year, session);
                    //Toast.makeText(getActivity(),"1 List size:"+mlist.size(),Toast.LENGTH_SHORT).show();
                    trial_listAdaptor = new FeedbackListAdapter(mlist, getActivity(),false,false);
                    recyclerList.setAdapter(trial_listAdaptor);
                    //}
                } catch (Exception e) {
                    Log.d("MSG",e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;

    }
    private void loadTrailType() {
        // database handler
        // Spinner Drop down elements
        List<String> lables = databaseHelper1.getTrailTypeForFeedback(ddlCrop.getSelectedItem().toString());

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlTrailType.setAdapter(dataAdapter1);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Feedback");
    }
    private void loadSpinnerData() {
        // database handler
        // Spinner Drop down elements
        List<String> lables = databaseHelper1.getCropforTrialFeedback();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlCrop.setAdapter(dataAdapter);

        /*Changes done on 1st Sept 2021, for updated tag*/
        List<String> ddYearlist = databaseHelper1.getSelectedYearList();//Arrays.asList(getResources().getStringArray(R.array.TrailTypelist));
        List<String> ddSessionList = databaseHelper1.getSelectedSeasonList();//Arrays.asList(getResources().getStringArray(R.array.Traillist));

        ArrayAdapter<String> ddYearlistAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                ddYearlist);

        // Drop down layout style - list view with radio button
        ddYearlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlYear.setAdapter(ddYearlistAdapter);
        ArrayAdapter<String> ddSessionListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                ddSessionList);

        // Drop down layout style - list view with radio button
        ddSessionListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlSesion.setAdapter(ddSessionListAdapter);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        loadTrailType();
        String label = ddlCrop.getSelectedItem().toString();
        String type=ddlTrailType.getSelectedItem().toString();
        ArrayList<FeedbackReportModel> mlist = databaseHelper1.getTrailDetailstagFeedbackData(label);
        trial_listAdaptor = new FeedbackListAdapter(mlist, getActivity(),false,false);
        recyclerList.setAdapter(trial_listAdaptor);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
