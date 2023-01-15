package com.tdms.mahyco.nxg;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.tdms.mahyco.nxg.utils.AppConstant;

public class SelectObsOpnTrialActivity extends AppCompatActivity {

    Button btnSelectObs, btnOpenTrial;
    private LinearLayout rel_top;
    private CardView cardView;
    private TextView txt_LocationDetails;
    private TextView txt_TrialCodedetails;
    private TextView txt_TrialTagDetails;
    private TextView txt_TrialSegmentDetails;
    private TextView tv_tag_type;
    private Button btnPLD;
    String trialCode="",tagType="", location="", trialDetails="", segmentDetails="",selectedCrop="";
    databaseHelper databaseHelper1;
    public Messageclass msclass;
    TextView tvFarmerName, tvFarmerVillage, tvDOS, tvDOT; /*24th Feb 2022*/
    LinearLayout llayoutFName, llayoutFarmerVillage, llDOS, llDOT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selctobsopentrial);
        getSupportActionBar().setTitle("Observations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUI();
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

    private void setUI(){
        msclass = new Messageclass(this);
        getSupportActionBar().setTitle("Observations Details");
        rel_top = (LinearLayout) findViewById(R.id.rel_top);
        cardView = (CardView) findViewById(R.id.card_view);
        txt_TrialCodedetails = (TextView) findViewById(R.id.txt_TrialCodedetails);
        txt_LocationDetails = (TextView) findViewById(R.id.txt_LocationDetails);
        txt_TrialTagDetails = (TextView) findViewById(R.id.txt_TrialTagDetails);
        txt_TrialSegmentDetails = (TextView) findViewById(R.id.txt_TrialSegmentDetails);
        llayoutFName = (LinearLayout) findViewById(R.id.layout_fname);
        llayoutFarmerVillage = (LinearLayout) findViewById(R.id.layout_fvillage);
        llDOS = (LinearLayout) findViewById(R.id.layout_dos);
        llDOT = (LinearLayout) findViewById(R.id.layout_dot);
        tv_tag_type = (TextView) findViewById(R.id.tv_tag_type);
        /*24th Feb 2022*/
        tvFarmerName = (TextView) findViewById(R.id.tv_farmer_name);
        tvFarmerVillage = (TextView) findViewById(R.id.tv_village_name);
        tvDOS = (TextView) findViewById(R.id.tv_dos);
        tvDOT = (TextView) findViewById(R.id.tv_dot);
        btnPLD = (Button) findViewById(R.id.btnPLD);
        btnOpenTrial = (Button) findViewById(R.id.btn_open_trial);
        btnSelectObs = (Button) findViewById(R.id.btn_select_obs);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.Whitecl));

        if(getIntent()!=null){
            //Get Data
            trialCode = getIntent().getStringExtra("keyCode");
            tagType = getIntent().getStringExtra(AppConstant.TAG_TYPE);
            location = getIntent().getStringExtra("Location");
            trialDetails = getIntent().getStringExtra("TrialDetails");
            segmentDetails = getIntent().getStringExtra("SegmentDetails");
            selectedCrop = getIntent().getStringExtra("selectedCrop");
            Log.d("SelectObsOpnTrialFrag", "DATA RECEIVED : TrialCode : " + trialCode + " TAG TYPE : " + tagType);
            Log.d("SelectObsOpnTrialFrag", "DATA RECEIVED : location : " + location + " trialDetails : " + trialDetails+ " segmentDetails : "+segmentDetails);
            Log.d("SelectObsOpnTrialFrag", "DATA RECEIVED : selectedCrop : " + selectedCrop);
            txt_TrialCodedetails.setText(trialCode);
            txt_TrialTagDetails.setText(trialDetails);
            txt_TrialSegmentDetails.setText(segmentDetails);
            txt_LocationDetails.setText(location);
            if (txt_TrialTagDetails.getText().equals("T")) {
                btnPLD.setVisibility(View.GONE);
            }
            btnPLD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String trialCode = txt_TrialCodedetails.getText().toString();
                    Intent i = new Intent(SelectObsOpnTrialActivity.this, pld_not_sown.class);
                    i.putExtra("Trail_code", trialCode);
                    startActivity(i);
                }
            });

            btnOpenTrial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SelectObsOpnTrialActivity.this, Testimonial.class);
                    i.putExtra("trailCode", trialCode);
                    startActivity(i);
                }
            });

            btnSelectObs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SelectObsOpnTrialActivity.this, DownloadObservationActivity.class);
                    i.putExtra("trailCode", trialCode);
                    i.putExtra("selectedCrop", selectedCrop);
                    startActivity(i);
                }
            });
        }

        databaseHelper1 = new databaseHelper(this);

        showDetailsNew();

    }

    private void showDetailsNew(){
        //// Data TO Show On Screen
        Cursor Farmerdata = databaseHelper1.Forobservation(trialCode);
        if (Farmerdata.getCount() == 0) {
        } else {
            Farmerdata.moveToFirst();
            if (Farmerdata != null) {
                do {
                    String fName = Farmerdata.getString(Farmerdata.getColumnIndex("Fname")) ;
                    String fVillage = Farmerdata.getString(Farmerdata.getColumnIndex("Fvillage"));
                    String dos = Farmerdata.getString(Farmerdata.getColumnIndex("Fsowingdate"));
                    String dot = Farmerdata.getString(Farmerdata.getColumnIndex("TFTransplantDate"));
                    if(fName!=null && !fName.equals("")){
                        tvFarmerName.setText(fName);
                    } else{
                        llayoutFName.setVisibility(View.GONE);
                    }
                    if(fVillage!=null && !fVillage.equals("")){
                        tvFarmerVillage.setText(fVillage);
                    }else{
                        llayoutFarmerVillage.setVisibility(View.GONE);
                    }
                    if(dos!=null && !dos.equals("")){
                        tvDOS.setText(dos);
                    } else{
                        llDOS.setVisibility(View.GONE);
                    }
                    if(dot!=null && !dot.equals("")){
                        tvDOT.setText(dot);
                    } else{
                        llDOT.setVisibility(View.GONE);
                    }
                } while (Farmerdata.moveToNext());
            }
            Farmerdata.close();
        }
    }
}
