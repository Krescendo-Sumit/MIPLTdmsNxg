package com.tdms.mahyco.nxg;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.MultiSelectionSpinner;
import com.tdms.mahyco.nxg.utils.Prefs;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FieldAreaTag extends Fragment implements AdapterView.OnItemSelectedListener, MultiSelectionSpinner.OnMultipleItemsSelectedListener {


    public FieldAreaTag() {
        // Required empty public constructor
    }

    Spinner ddlSesion;
    Spinner ddlYear;
    public Spinner ddlCrop, ddlTrailType;
    public ListView list;
    databaseHelper databaseHelper1;
    public Messageclass msclass;
    public String selectedCrop;
    TrailReportModel trailReportModel;
    public RecyclerView recyclerList;
    trailDetails_ListAdaptor trailDetails_ListAdaptor;

    /*Create Object*/
    Prefs mPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_field_area_tag, container, false);
        ddlCrop = (Spinner) rootView.findViewById(R.id.ddlCrop);
        ddlTrailType = (Spinner) rootView.findViewById(R.id.ddlTrailType);
        list = (ListView) rootView.findViewById(R.id.list);
        databaseHelper1 = new databaseHelper(this.getContext());
        msclass = new Messageclass(this.getContext());

        /*Initialize*/
        mPref = Prefs.with(getActivity());
        saveDataForReference(0,0,0,0);

        recyclerList = (RecyclerView) rootView.findViewById(R.id.recycler1);

        /*Changes done on 1st Sept 2021, for updated tag*/
        List<String> ddYearlist = databaseHelper1.getSelectedYearList();//Arrays.asList(getResources().getStringArray(R.array.TrailTypelist));
        List<String> ddSessionList = databaseHelper1.getSelectedSeasonList();//Arrays.asList(getResources().getStringArray(R.array.Traillist));

        LinearLayoutManager lllm = new LinearLayoutManager(this.getContext());
        lllm.setOrientation(LinearLayoutManager.VERTICAL);
        ddlYear = (Spinner) rootView.findViewById(R.id.ddlYear);
        ddlSesion = (Spinner) rootView.findViewById(R.id.ddlSesion);

        recyclerList.setLayoutManager(lllm);
        // list.setBackgroundColor(Color.rgb(246,246,246));
//
//        ddlYear.setItems(ddYearlist);
//        ddlYear.setListener(this);
//        ddlSesion.setItems(ddSessionList);
//        ddlSesion.setListener(this);
        //Bind ListVIew From Database


        //END Bind ListVIew From Database

        ddlCrop.setOnItemSelectedListener(this);
        ddlSesion.setOnItemSelectedListener(this);
        ddlYear.setOnItemSelectedListener(this);
        //ddlTrailType.setOnItemSelectedListener(this);

        // Loading spinner data from database
        /*Commented on 19th July 2021*/
        loadSpinnerData();
        // loadTrailType();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Passing Click ListView Data and open activity
                String selectedFromList = (list.getItemAtPosition(position).toString());

                String[] parts = selectedFromList.split("       -");
                String first = parts[0];//"hello"
                String second = parts[1];

                String[] parts1 = second.split("   ");
                String third = parts1[2];

                if (third.equals("T")) {

                    Log.d("AlreadyTagged","CALL FieldAreaTag 1");
                    Intent openIntent = new Intent(getActivity(), alreadyTag.class);
                    openIntent.putExtra("Trail_code", first.toString());
                    startActivity(openIntent);
                } else {
                    Bundle data1 = new Bundle();//create bundle instance
                    data1.putString("keyCode", first);//put string to pass with a key value
                    sowing sowing = new sowing();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.FragmentContainer, sowing, sowing.getTag())
                            .addToBackStack("fragBack").commit();
                    sowing.setArguments(data1);
                    //End
                }

                }
        });


        ddlTrailType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {

                    String label = ddlCrop.getSelectedItem().toString();
                    String type = ddlTrailType.getSelectedItem().toString();
                    String year = ddlYear.getSelectedItem().toString();
                    String session = ddlSesion.getSelectedItem().toString();

                    ArrayList<TrailReportModel> mlist = databaseHelper1.getTrailDetailsType(label, type, year, session);
                    ArrayList<TrailReportModel> sList = sortList(mlist);
                    trailDetails_ListAdaptor = new trailDetails_ListAdaptor(sList, getActivity(), getFragmentManager(),
                        ddlYear.getSelectedItemPosition(),ddlSesion.getSelectedItemPosition(),ddlCrop.getSelectedItemPosition(),ddlTrailType.getSelectedItemPosition());
                    recyclerList.setAdapter(trailDetails_ListAdaptor);
                    //}
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
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
                    ArrayList<TrailReportModel> mlist = databaseHelper1.getTrailDetailsType(label, type, year, session);
                    ArrayList<TrailReportModel> sList = sortList(mlist);
                    trailDetails_ListAdaptor = new trailDetails_ListAdaptor(sList, getActivity(), getFragmentManager(),
                            ddlYear.getSelectedItemPosition(),ddlSesion.getSelectedItemPosition(),ddlCrop.getSelectedItemPosition(),ddlTrailType.getSelectedItemPosition());
                    recyclerList.setAdapter(trailDetails_ListAdaptor);
                    //}
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
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
                    ArrayList<TrailReportModel> mlist = databaseHelper1.getTrailDetailsType(label, type, year, session);
                    ArrayList<TrailReportModel> sList = sortList(mlist);
                    trailDetails_ListAdaptor = new trailDetails_ListAdaptor(sList, getActivity(), getFragmentManager(),
                            ddlYear.getSelectedItemPosition(),ddlSesion.getSelectedItemPosition(),ddlCrop.getSelectedItemPosition(),ddlTrailType.getSelectedItemPosition());
                    recyclerList.setAdapter(trailDetails_ListAdaptor);
                    //}
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    void saveDataForReference(int year, int season, int crop,int trialType){
        mPref.saveInt(Constants.SELECTED_YEAR,year);
        mPref.saveInt(Constants.SELECTED_SEASON,season);
        mPref.saveInt(Constants.SELECTED_CROP,crop);
        mPref.saveInt(Constants.SELECTED_TRAIL_TYPE,trialType);
        int year1 = mPref.getInt(Constants.SELECTED_YEAR,0);
        int season1 = mPref.getInt(Constants.SELECTED_SEASON,0);
        int crop1 = mPref.getInt(Constants.SELECTED_CROP,0);
        int trailType1 = mPref.getInt(Constants.SELECTED_TRAIL_TYPE,0);
        Log.d("SAVED DATA","Year : "+year1);
        Log.d("SAVED DATA","Season : "+season1);
        Log.d("SAVED DATA","Crop : "+crop1);
        Log.d("SAVED DATA","Trail Type : "+trailType1);
    }

    private ArrayList<TrailReportModel> sortList(ArrayList<TrailReportModel> mList) {
        ArrayList<String> fullTagTrialCodes = databaseHelper1.getFullTagTrialCodes();
        ArrayList<TrailReportModel> sortedList = new ArrayList<TrailReportModel>();
        if (fullTagTrialCodes==null || fullTagTrialCodes.size() == 0) {
            sortedList = mList;
        } else {
            //int i = 0;
            for (int i = 0; i < mList.size(); i++) {
                TrailReportModel model = mList.get(i);
                if (fullTagTrialCodes.contains(model.getTrailcode())) {
                    if (model.getTagType().equals(AppConstant.NURSERY_TAG)) {
                        continue;
                    }
                    else{
                        sortedList.add(model);
                    }
                } else {
                    sortedList.add(model);
                }
            }
        }
        Log.d("SORTED","SORTED_LIST SIZE : "+sortedList.size());
        Log.d("SORTED","SORTED_LIST RECORDS-----------------------------------------------------------------------");
        for(int i=0;i<sortedList.size();i++){
            Log.d("DATA","SORTED DATA Records: "+sortedList.get(i));
        }
        return sortedList;
    }

    private void loadSpinnerData() {
        // database handler
        // Spinner Drop down elements
        List<String> lables = databaseHelper1.getCrop();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lables);

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

    private void loadTrailType() {
        // database handler
        // Spinner Drop down elements
        List<String> lables = databaseHelper1.getTrailType(ddlCrop.getSelectedItem().toString());

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlTrailType.setAdapter(dataAdapter1);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            loadTrailType();
            String label = parent.getItemAtPosition(position).toString();
            ArrayList<TrailReportModel> mlist = databaseHelper1.getTrailDetails(label);
            trailDetails_ListAdaptor = new trailDetails_ListAdaptor(mlist, this.getContext(), getFragmentManager(),
                    ddlYear.getSelectedItemPosition(),ddlSesion.getSelectedItemPosition(),ddlCrop.getSelectedItemPosition(),ddlTrailType.getSelectedItemPosition());
            recyclerList.setAdapter(trailDetails_ListAdaptor);
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Sowing & Transplanting");

        /*Added on 19th July 2021*/
        int year = mPref.getInt(Constants.SELECTED_YEAR,0);
        int season = mPref.getInt(Constants.SELECTED_SEASON,0);
        int crop = mPref.getInt(Constants.SELECTED_CROP,0);
        int trailType = mPref.getInt(Constants.SELECTED_TRAIL_TYPE,0);
        Log.d("OnResume SAVED DATA","Year : "+year);
        Log.d("OnResume SAVED DATA","Season : "+season);
        Log.d("OnResume SAVED DATA","Crop : "+crop);
        Log.d("OnResume SAVED DATA","Trail Type : "+trailType);
        ddlYear.setSelection(year);
        ddlSesion.setSelection(season);
        ddlCrop.setSelection(crop);
        ddlTrailType.setSelection(trailType);
        // Loading spinner data from database
        //loadSpinnerData();
        refreshList();
    }

    private void refreshList(){
        try {
            String label = ddlCrop.getSelectedItem().toString();
            String type = ddlTrailType.getSelectedItem().toString();
            String year = ddlYear.getSelectedItem().toString();
            String session = ddlSesion.getSelectedItem().toString();

            ArrayList<TrailReportModel> mlist = databaseHelper1.getTrailDetailsType(label, type, year, session);
            ArrayList<TrailReportModel> sList = sortList(mlist);
            trailDetails_ListAdaptor = new trailDetails_ListAdaptor(sList, getActivity(), getFragmentManager(),
                    ddlYear.getSelectedItemPosition(),ddlSesion.getSelectedItemPosition(),ddlCrop.getSelectedItemPosition(),ddlTrailType.getSelectedItemPosition());
            recyclerList.setAdapter(trailDetails_ListAdaptor);
            //}
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
        }
    }

    @Override
    public void selectedIndices(List<Integer> indices) {
    }

    @Override
    public void selectedStrings(List<String> strings) {

    }
}
