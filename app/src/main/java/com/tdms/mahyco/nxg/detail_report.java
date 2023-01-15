package com.tdms.mahyco.nxg;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class detail_report extends AppCompatActivity {
    public Spinner ddlCrop, ddlTehsil;
    public ListView list;
    databaseHelper databaseHelper1;
    public Messageclass msclass;
    public String selectedCrop;
    public RecyclerView recyclerList;
    detailReportListAdaptor detailReportListAdaptor;
    String label,name;
    String TAG="detail_report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_report);
        databaseHelper1 = new databaseHelper(this);
        recyclerList = (RecyclerView) findViewById(R.id.recycler);

        LinearLayoutManager lllm = new LinearLayoutManager(this);
        lllm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerList.setLayoutManager(lllm);
        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            label =(String) bundle.get("trailCode");

          //  Intent i = getIntent();
          //  name = i.getStringExtra("trialCode");

        }
        //  String label = parent.getItemAtPosition(position).toString();
        ArrayList<detailReportModel> mlist = databaseHelper1.getDetailReport(label);
        Log.d(TAG, "onCreate: "+mlist.toString()+":::::"+label);
        detailReportListAdaptor = new detailReportListAdaptor(mlist, detail_report.this);
        recyclerList.setAdapter(detailReportListAdaptor);
    }

}
