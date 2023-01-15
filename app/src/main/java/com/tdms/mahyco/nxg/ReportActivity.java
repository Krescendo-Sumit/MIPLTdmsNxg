package com.tdms.mahyco.nxg;

import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public Spinner ddlCrop, ddlTehsil;
    public ListView list;
    databaseHelper databaseHelper1;
    public Messageclass msclass;
    public String selectedCrop;
    public RecyclerView recyclerList;
    ReportListAdapter reportListAdapter;
    String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ddlCrop = (Spinner) findViewById(R.id.ddlCrop);
        databaseHelper1 = new databaseHelper(this);



        Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount()==0)
        {

        }else {
            data.moveToFirst();
            if(data!=null)
            {
                do
                {
                    userRole=data.getString((data.getColumnIndex("USER_ROLE")));


                    Log.d("Role","RoleReportActivity"+userRole);
                }while(data.moveToNext());

            }data.close();


        }
        ddlCrop.setOnItemSelectedListener(this);
        loadSpinnerData();

        recyclerList = (RecyclerView) findViewById(R.id.recycler);

        LinearLayoutManager lllm = new LinearLayoutManager(this);
        lllm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerList.setLayoutManager(lllm);
    }

    private void loadSpinnerData() {
        // database handler
        // Spinner Drop down elements
        List<String> lables = databaseHelper1.getCrop();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ddlCrop.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String label = parent.getItemAtPosition(position).toString();
        ArrayList<ReportModel> mlist = databaseHelper1.getReport(label);
        reportListAdapter = new ReportListAdapter(mlist, ReportActivity.this);
        recyclerList.setAdapter(reportListAdapter);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
