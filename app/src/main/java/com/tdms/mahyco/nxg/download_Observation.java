package com.tdms.mahyco.nxg;


import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class download_Observation extends Fragment implements android.widget.CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {


    public download_Observation() {
        // Required empty public constructor
    }

    public Spinner ddlCrop, ddlTehsil,ddlSesion,ddlYear;;
    public ListView list;
    databaseHelper databaseHelper1;
    public Messageclass msclass;
    public String selectedCrop;
    trailDetails_ListAdaptor trailDetails_ListAdaptor;
    ListView lv;
    ArrayList<observationList> planetList;
    PlanetAdapter plAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_download__observation, container, false);

        ddlYear = (Spinner) rootView.findViewById(R.id.ddlYear);
        ddlSesion = (Spinner) rootView.findViewById(R.id.ddlSesion);
        ddlCrop = (Spinner) rootView.findViewById(R.id.ddlCrop);
       // ddlTehsil = (Spinner) rootView.findViewById(R.id.ddlTeshil);
        list=(ListView)rootView.findViewById(R.id.list);
        databaseHelper1 = new databaseHelper(this.getContext());
        msclass = new Messageclass(this.getContext());
        lv = (ListView)rootView.findViewById(R.id.listview);
        ddlCrop.setOnItemSelectedListener(this);
        ddlSesion.setOnItemSelectedListener(this);
        ddlYear.setOnItemSelectedListener(this);
        loadSpinnerData();
        ddlYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String label = ddlCrop.getSelectedItem().toString();
                    String year = ddlYear.getSelectedItem().toString();
                    String session = ddlSesion.getSelectedItem().toString();

                    //}
                } catch (Exception e) {
                    Log.d("Msg",e.getMessage());
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
                    String session = ddlSesion.getSelectedItem().toString();
                    String year = ddlYear.getSelectedItem().toString();

                    //}
                } catch (Exception e) {
                    Log.d("Msg",e.getMessage());
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

        Log.d("loadSpinnerData","Size : "+lables.size());
        Log.d("loadSpinnerData","DATA : "+lables.toString());

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlCrop.setAdapter(dataAdapter);
    }
@Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String label = parent.getItemAtPosition(position).toString();
        //final ArrayList<String> planetList=new ArrayList<observationList>();
        planetList = new ArrayList<observationList>();
        Cursor data = databaseHelper1.GetSelectedObservation(label,"");
        try {

            if (data.getCount() == 0) {
                msclass.showMessage("No Data Available... ");
            } else {
                while (data.moveToNext()) {
                    planetList.add(new observationList(data.getString(0)));
                    plAdapter = new PlanetAdapter(planetList, this.getContext());
                    lv.setAdapter(plAdapter);
                } data.close();
            }
        }
        catch(Exception e) {
            Log.d("Msg",e.getMessage());
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


    }
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
