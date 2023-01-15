package com.tdms.mahyco.nxg;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.BaseUtils;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.HttpUtils;
import com.tdms.mahyco.nxg.utils.MultiSelectionSpinner;
import com.tdms.mahyco.nxg.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//import com.trial.mahyco.trail.Config;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadData extends Fragment implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {


    public DownloadData() {
        // Required empty public constructor
    }

    Prefs mPref;
    String type = null;
    databaseHelper databaseHelper1;
    public Button btnDownload, btnDownloadFeedbackData;
    public TextView txtStaff, txtTFA;
    ProgressDialog mprogresss;
    public Messageclass msclass;
    public CommonExecution cx;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Config config;
    MultiSelectionSpinner ddlSesion;
    MultiSelectionSpinner ddlYear;
    private Toolbar toolbar;
    String userCode;
    RelativeLayout rel_data;
    //    public ProgressBar da;
    String year;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPref = Prefs.with(getActivity());
        // Inflate the layout for this fragment
        //Declear veriable from here Rahul Dhande
        msclass = new Messageclass(this.getContext());
        // dialog = new ProgressDialog(getActivity());         //dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //  dialog.setMessage("Loading. Please wait...");
        cx = new CommonExecution(this.getContext());
        config = new Config(this.getContext()); //Here the context is passing
        databaseHelper1 = new databaseHelper(this.getContext());
        // pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        //  editor = pref.edit(this.getContext());
        ////Declear veriable End here Rahul Dhande
        View rootView = inflater.inflate(R.layout.fragment_download_data, container, false);
//97191040
        mprogresss = new ProgressDialog(getActivity());
        mprogresss.setCancelable(false);

        ddlYear = (MultiSelectionSpinner) rootView.findViewById(R.id.ddlYear);
        rel_data = (RelativeLayout) rootView.findViewById(R.id.rel_data);
        ddlSesion = (MultiSelectionSpinner) rootView.findViewById(R.id.ddlSesion);
        Spinner TrailStage = (Spinner) rootView.findViewById(R.id.ddlStage);
        Spinner TrailCode = (Spinner) rootView.findViewById(R.id.ddlTrailCode);
        btnDownload = (Button) rootView.findViewById(R.id.btnDownloadData);
        btnDownloadFeedbackData = (Button) rootView.findViewById(R.id.btnDownloadFeedbackData);
        txtStaff = (TextView) rootView.findViewById(R.id.txtStaff);
        txtTFA = (TextView) rootView.findViewById(R.id.txtTFA);
        //trail type Bind With Spinner
        //ArrayAdapter<String> ddlTrailType = new ArrayAdapter<String>(getActivity(),
        // android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.TrailTypelist));
        //ddlTrailType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Commented on 1sth Sept 2021
        /*START Comment*/
        /*List<String> ddlTrailType = Arrays.asList(getResources().getStringArray(R.array.TrailTypelist));
        List<String> ddlTraillist = Arrays.asList(getResources().getStringArray(R.array.Traillist));
        ddlYear.setItems(ddlTrailType);
        ddlYear.setListener(this);
        ddlSesion.setItems(ddlTraillist);
        ddlSesion.setListener(this);*/
        //End Comment

        //Crop Bind With Spinner
        ArrayAdapter<String> ddlCroptype = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Crop));
        ddlCroptype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        ddlSesion.setAdapter(ddlCroptype);
//        mprogresss = (ProgressDialog) rootView.findViewById(R.id.progressBar2);
        // mprogresss.setVisibility(View.INVISIBLE);

        Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount() == 0) {

        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    //userCode = data.getString((data.getColumnIndex("user_code")));
                    String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                    userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("userCode", "userCode: " + userCode);
                } while (data.moveToNext());

            }
            data.close();
            //String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCode);
            //txtStaff.setText(userCodeDecrypt);
            txtStaff.setText(userCode);
            // txtStaff.setEnabled(false);
        }


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {

                    mprogresss.setMessage("Retrieving Data ");
                    mprogresss.show();


//                    mprogresss.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            return true;
//                        }
//                    });
                    // getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    // rel_data.setEnabled(false);
                    //  rel_data.setClickable(false);
                   /* new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dowork();
                           // startapp();

                            ;
                        }
                    }).start();*/
                    //DownloadData();

                    /*Network check added */
                    try {
                        Log.d("XXX XDownload", "Call Download " + "Time: " + BaseUtils.convertMillisToString());
                        if (Config.NetworkConnection()) {

                            /*Added on 1st Sept, set flag 1 for selected year and season for future reference*/
                            String yearData = ddlYear.getSelectedItemsAsString().replace(" ", "");
                            String seasonData = ddlSesion.getSelectedItemsAsString().replace(" ", "");
                            ArrayList<String> selectedYear = new ArrayList<>(Arrays.asList(yearData.split(",")));
                            ArrayList<String> selectedSeason = new ArrayList<>(Arrays.asList(seasonData.split(",")));
                            int i=0,j=0;
                            for(i=0;i<selectedYear.size();i++){
                                databaseHelper1.updateSelectedYear(selectedYear.get(i));
                            }
                            for(j=0;j<selectedSeason.size();j++){
                                databaseHelper1.updateSelectedSeason(selectedSeason.get(j));
                            }
                            Log.d("XXX XDownload","Selected Year : "+yearData);
                            Log.d("XXX XDownload","Selected Season : " +seasonData);
                            new BreederDataDownload(2, ddlYear.getSelectedItemsAsString().replace(" ", ""),
                                    ddlSesion.getSelectedItemsAsString().replace(" ", ""), txtStaff.getText().toString(),
                                    txtTFA.getText().toString()).execute();
                        } else {
                            Toast.makeText(getActivity(), "Internet network not available.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.d("Msg", e.getMessage());
                    }


                }// else Toast.makeText(getActivity(), "False", Toast.LENGTH_SHORT).show();

            }
        });
        //     getActivity().setProgressBarIndeterminateVisibility(true);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                dowork();

            }
        }).start();*/

        btnDownloadFeedbackData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mprogresss.setMessage("Retrieving Data ");
                mprogresss.show();
                Date date = new Date();
                Log.d("XXX XDownload", "Call Feedback Download " + "Time: " + BaseUtils.convertMillisToString());
                //Toast.makeText(getActivity(),"Downloading feedback data",Toast.LENGTH_SHORT).show();
                try {
                    if (Config.NetworkConnection()) {
                        new BreederDataDownloadFeedback(2, ddlYear.getSelectedItemsAsString().replace(" ", ""),
                                ddlSesion.getSelectedItemsAsString().replace(" ", ""), txtStaff.getText().toString(),
                                txtTFA.getText().toString()).execute();
                    } else {
                        Toast.makeText(getActivity(), "Internet network not available.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            }
        });

        if (Config.NetworkConnection()) {
            int flagCountYear = databaseHelper1.getSelectedYearList().size();
            int flagCountSeason = databaseHelper1.getSelectedSeasonList().size();
            if(flagCountYear == 0 && flagCountSeason == 0){
                Log.d("YearSeason","Call API");
                getYearAndSeason();
            }
            else{
                Log.d("YearSeason","No need to Call API");
                //SET Values to Spinner
                List<String> ddlTrailType = databaseHelper1.getYearList();
                ddlYear.setItems(ddlTrailType);
                ddlYear.setListener(DownloadData.this);
                List<String> ddlTraillist = databaseHelper1.getSeasonList();
                ddlSesion.setItems(ddlTraillist);
                ddlSesion.setListener(DownloadData.this);
            }
        } else {
            Toast.makeText(getActivity(), "Internet network not available.", Toast.LENGTH_SHORT).show();
        }

        isUploaded();
        return rootView;

    }

    private void dowork() {
//mprogresss.showContextMenu();
//mprogresss.setIndeterminate(true);
        for (int progress = 0; progress < 100; progress += 10) {
            try {
                Thread.sleep(300);
                //
                //
                // mprogresss.setProgress(progress);

            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
            }

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Download Data");
    }

    private Context getApplicationContext() {
        return null;
    }

    //Start Functions and other code from here

    private boolean validation() {
        boolean flag = true;
        //  Log.d("rohitt", "validation: " + ddlYear.getSelectedItemsAsString());
        if (ddlYear.getSelectedItemsAsString().equals("")) {
            msclass.showMessage("Please Select Year ");
            return false;

        }
        if (ddlSesion.getSelectedItemsAsString().equals("")) {
            msclass.showMessage("Please Select Season");
            return false;
        }
        if (!isUploaded()) {


            final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle("Trial Data Management");
            alertDialog.setMessage("Please Upload Data first");
            alertDialog.setCanceledOnTouchOutside(false);

            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                    home.isBackNavigationAllowed = true;

                    UploadData mainMenu = new UploadData();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.FragmentContainer, mainMenu).addToBackStack("fragBack");
                    fragmentTransaction.commit();


                }
            });

            alertDialog.show();
            return false;
        }

        return flag;
    }


    //int count=0;

    private boolean DownloadData(String str) {
        boolean fl = false;
        try {
            //  str = cx.new BreederDataDownload(2, ddlYear.getSelectedItem().toString(), ddlSesion.getSelectedItem().toString(), txtStaff.getText().toString(), txtTFA.getText().toString()).execute().get();

            Log.d("Response", "Full response : " + str);

            JSONObject object = new JSONObject(str);

            String success = object.getString("success");
            JSONObject message = object.getJSONObject("message"); /*Changed on 6 Jan 2021*/

            Log.d("XXX XDownload", "Sucess : " + success);
            Log.d("XXX XDownload", "Message : " + message);

            if (success.equalsIgnoreCase("true")) {

                Log.d("XXX XDownload", "DB INSERT STARTED " + "Time: " + BaseUtils.convertMillisToString());

                // mprogresss.setVisibility(View.VISIBLE);
                databaseHelper1.deleledata("TrailCodeData", "");
                databaseHelper1.deleledata1("ObservationMaster", "");
                databaseHelper1.deleledata1("tagdata1", "");
                databaseHelper1.deleledata1("FarmerInfodata", "");
                databaseHelper1.deleledata1("DownloadedObservation", "");
                databaseHelper1.deleledata1("Observationtaken", "");
                databaseHelper1.deleledata1("TrialWiseDownloadedObservation", ""); /*Updated on 21 July 2021*/

                /*databaseHelper1.deleteTable("TrailCodeData"); //
                databaseHelper1.deleteTable("ObservationMaster");
                databaseHelper1.deleteTable("tagdata1");
                databaseHelper1.deleteTable("FarmerInfodata");
                databaseHelper1.deleteTable("DownloadedObservation");
                databaseHelper1.deleteTable("Observationtaken");*/

                /*JSONArray jArray = object.getJSONArray("Table"); // For Trial code Download
                JSONArray jArray1 = object.getJSONArray("Table1"); //For Observation Download
                JSONArray jArray2 = object.getJSONArray("Table2"); //For TagData Download
                JSONArray jArray3 = object.getJSONArray("Table3"); //For Sowing Data Download
                JSONArray jArray4 = object.getJSONArray("Table4"); //For Fill Observation Data Download*/  /*Changed on 6 Jan 2021*/

                JSONArray jArray = message.getJSONArray("Table"); // For Trial code Download
                JSONArray jArray1 = message.getJSONArray("Table1"); //For Observation Download
                JSONArray jArray2 = message.getJSONArray("Table2"); //For TagData Download
                JSONArray jArray3 = message.getJSONArray("Table3"); //For Sowing Data Download
                JSONArray jArray4 = message.getJSONArray("Table4"); //For Fill Observation Data Download

                Log.d("XDownload", "Trial code Download jArray Main length ===========>" + jArray.length());
                Log.d("XDownload", "Observation Download jArray1  Table1 Main length ===========>" + jArray1.length());
                Log.d("XDownload", "For TagData Download jArray2 Table2 Main length ===========>" + jArray2.length());
                Log.d("XDownload", "Sowing Data Download jArray3 Table3 Main length ===========>" + jArray3.length());
                Log.d("XDownload", "For Fill Observation Data Download jArray4 Table4 Main length ===========>" + jArray4.length());

                /*databaseHelper1.getDatabase().beginTransaction();*/

                /*Is CHANGED in Backend ON 19 April 2021 SPDM1, SPDM2 add to STAFF_CODE:SPDM2 & SPDM1 is additional field*/
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    //if(jObject.getString("IMEI").toString().equals(msclass.getDeviceIMEI())) {
                    //Log.d("XDownload", +i+" Table Trial code: " + jObject);
                    fl = databaseHelper1.InsertDownloadData(jObject.getString("T_YEAR").toString(), jObject.getString("T_SESION").toString(), jObject.getString("TRIL_CODE").toString(), jObject.getString("Trail_Type").toString(), jObject.getString("CROP").toString(), jObject.getString("ZONE").toString(), jObject.getString("STATE").toString(), jObject.getString("DISTRICT").toString(), jObject.getString("TEHSIL").toString(), jObject.getString("ENTRY").toString(), jObject.getString("REPLECATION").toString(), jObject.getString("SPDM2").toString(), jObject.getString("NoRows").toString(), jObject.getString("RowLength").toString(), jObject.getString("RRSpecing").toString(), jObject.getString("PPSpacing").toString(), jObject.getString("StartPlotNo").toString(), jObject.getString("EndPlotNo").toString(), jObject.getString("Location").toString(), jObject.getString("PlotSize").toString(), jObject.getString("nursery").toString(), jObject.getString("segment").toString(), jObject.getString("Product").toString(), jObject.getString("SPDM1").toString());
                }
               /* databaseHelper1.getDatabase().setTransactionSuccessful();
                databaseHelper1.getDatabase().endTransaction();
                databaseHelper1.getDatabase().close();*/

                /*databaseHelper1.getDatabase().beginTransaction();*/
                for (int i = 0; i < jArray1.length(); i++) {
                    JSONObject jObject = jArray1.getJSONObject(i);
                    //if(jObject.getString("IMEI").toString().equals(msclass.getDeviceIMEI())) {
                    //Log.d("XDownload", +i+" Table1 Download Observation response : " + jObject);
                    fl = databaseHelper1.InsertObservation(jObject.getString("Crop").toString(), jObject.getString("VariableID").toString(), jObject.getString("Name").toString(), jObject.getString("S_M").toString(), jObject.getString("Discription").toString(), jObject.getString("Abbreviation").toString(), jObject.getString("O_Group").toString(), jObject.getString("Variable_type").toString(), jObject.getString("Scale").toString(), jObject.getString("Scale_type").toString(), jObject.getString("Value1").toString(), jObject.getString("Value2").toString(), jObject.getString("Value3").toString(), jObject.getString("Value4").toString(), jObject.getString("Value5").toString(),
                            jObject.getString("Crop_Stage").toString(), jObject.getString("OYT").toString(), jObject.getString("ST").toString(), jObject.getString("MLT").toString(), jObject.getString("PET").toString(), jObject.getString("Demo").toString());
                }
                /*databaseHelper1.getDatabase().setTransactionSuccessful();
                databaseHelper1.getDatabase().endTransaction();
                databaseHelper1.getDatabase().close();*/

                /*databaseHelper1.getDatabase().beginTransaction();*/
                for (int i = 0; i < jArray2.length(); i++) {
                    JSONObject jObject = jArray2.getJSONObject(i);
                    //if(jObject.getString("IMEI").toString().equals(msclass.getDeviceIMEI())) {
                    //Log.d("XDownload", +i+" Table2 TagData Download response : " + jObject);
                    fl = databaseHelper1.DownladTagdata(jObject.getString("usercode").toString(), jObject.getString("TRIL_CODE").toString(), jObject.getString("coordinates").toString(), jObject.getString("address").toString(), jObject.getString("entrydate").toString(), jObject.getString("flag").toString());
                }
                /*databaseHelper1.getDatabase().setTransactionSuccessful();
                databaseHelper1.getDatabase().endTransaction();
                databaseHelper1.getDatabase().close();*/

                /* databaseHelper1.getDatabase().beginTransaction();*/
                for (int i = 0; i < jArray3.length(); i++) {
                    JSONObject jObject = jArray3.getJSONObject(i);
                    //if(jObject.getString("IMEI").toString().equals(msclass.getDeviceIMEI())) {
                    //Log.d("XDownload", +i+" Table3 Sowing Data Download response : " + jObject);
                    fl = databaseHelper1.DownladFarmerInfodata(jObject.getString("FarmerName").toString(), jObject.getString("FVillage").toString(), jObject.getString("Fmobile").toString(), jObject.getString("FstartNote").toString(), jObject.getString("FSowingDate").toString(), jObject.getString("Trialcode").toString(), jObject.getString("FArea").toString(), jObject.getString("Flag").toString(), jObject.getString("usercode").toString(), jObject.getString("GeoLocation").toString(), jObject.getString("nurseryDate").toString());
                }
                /*databaseHelper1.getDatabase().setTransactionSuccessful();
                databaseHelper1.getDatabase().endTransaction();
                databaseHelper1.getDatabase().close();*/

                /*databaseHelper1.getDatabase().beginTransaction();*/
                for (int i = 0; i < jArray4.length(); i++) {
                    JSONObject jObject = jArray4.getJSONObject(i);
                    //if(jObject.getString("IMEI").toString().equals(msclass.getDeviceIMEI())) {
                    //Log.d("XDownload", +i+" Table4 Fill Observation Data Download response : " + jObject);
                    fl = databaseHelper1.DownloadFillObservation(jObject.getString("VariableID").toString(), jObject.getString("TRIAL_CODE").toString(), jObject.getString("PlotNo").toString(), jObject.getString("Value1").toString(), jObject.getString("Value2").toString(), jObject.getString("Value3").toString(), jObject.getString("Value4").toString(), jObject.getString("Value5").toString(), jObject.getString("usercode"), jObject.getString("ValueSPM"));
                }
               /* databaseHelper1.getDatabase().setTransactionSuccessful();
                databaseHelper1.getDatabase().endTransaction();
                databaseHelper1.getDatabase().close();*/

                return true;
            } else {
                msclass.showMessage("Data not available");
                // mprogresss.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            Log.d("Msg", e.getMessage());
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
        }
        Log.d("XXX XDownload", "DB INSERT END " + "Time: " + BaseUtils.convertMillisToString());

        return fl;
    }


    //Check for Data uploaded or not
    public boolean isUploaded() {

        String userRole = "";
        Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount() == 0) {
            userRole = "0";


        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    userRole = data.getString((data.getColumnIndex("USER_ROLE")));


                    Log.d("Role", "RoleMainMenu" + userRole);
                } while (data.moveToNext());

            }
            data.close();


        }


        String searchQueryFarmerInfodata = "select  *  from FarmerInfodata inner join tagdata1 on FarmerInfodata.TRIL_CODE=tagdata1.TRIL_CODE where tagdata1.flag='T' and tagdata1.Upload='U'";
        Cursor cursorFarmerInfodata = databaseHelper1.getReadableDatabase().rawQuery(searchQueryFarmerInfodata, null);
        int count = cursorFarmerInfodata.getCount();
        cursorFarmerInfodata.close();

        String searchQueryObservationtaken = "select  distinct PlotNo  from Observationtaken where flag='0'";
        Cursor cursorObservationtaken = databaseHelper1.getReadableDatabase().rawQuery(searchQueryObservationtaken, null);
        int count1 = cursorObservationtaken.getCount();
        cursorObservationtaken.close();


        String searchQueryPLDNotSown = "select * from PLDNotSown where rowSyncStatus='0'";
        Cursor cursorPLDNotSown = databaseHelper1.getReadableDatabase().rawQuery(searchQueryPLDNotSown, null);
        int count2 = cursorPLDNotSown.getCount();
        cursorPLDNotSown.close();


        String searchQueryFeedbackTaken = "select * from FeedbackTaken where isSyncedStatus='0' and isSubmitted='1' ";
        Cursor cursorFeedbackTaken = databaseHelper1.getReadableDatabase().rawQuery(searchQueryFeedbackTaken, null);
        int count3;
        if (userRole.equals("8")) {
            count3 = cursorFeedbackTaken.getCount();
        } else {

            count3 = 0;

        }
        cursorFeedbackTaken.close();


        Log.d("Count digits", "Count digits" + count + count1 + count2 + count3);

        if (count > 0) {
            return false;
        }

        if (count1 > 0) {
            return false;


        }
        if (count2 > 0) {

            return false;

        }
        if (count3 > 0) {
            return false;
        }
        return true;
    }


    @Override
    public void selectedIndices(List<Integer> indices) {
        Log.d("rohitt", "selectedIndices: " + indices);
    }

    @Override
    public void selectedStrings(List<String> strings) {


        String string = strings.toString();
        string = string.replace(" ", "");
        string = string.replace("[", "").replace("]", "");
        Log.d("rohitt1", "selectedIndices: " + string);

    }


    public class BreederDataDownload extends AsyncTask<String, String, String> {

        private int action;
        private String ddlyear;
        private String ddlsession;
        private String usercode;
        private String TFA;

        String returnstring;

        public BreederDataDownload(int action, String ddlyear, String ddlsession, String usercode, String TFA) {
            this.action = action;
            this.ddlyear = ddlyear;
            this.ddlsession = ddlsession;
            this.usercode = usercode;
            this.TFA = TFA;
        }

        protected void onPreExecute() {
            // dialog.setMessage("Loading....");
            //dialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                Log.d("VAPT", "Usercode : " + userCode);
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();
                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt); //userCode
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("action", action);
                jsonParam.put("ddlyear", ddlyear);
                jsonParam.put("ddlsession", ddlsession);
                jsonParam.put("TFA", TFA);
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));

                jsonboj.put("Table", jsonParam);
                Log.d("VAPT", "DOWNLOAD URL : " + AppConstant.INSERT_DOWNLOAD_DATA_URL);
                Log.d("VAPT", "Json object : " + jsonParam);
                return HttpUtils.POSTJSON(AppConstant.INSERT_DOWNLOAD_DATA_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));


            } catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("XXX XDownload", "Download successfully " + "Time: " + BaseUtils.convertMillisToString());
            Log.d("VAPT", "Download Response String : " + s);
            try {
                //cx.redirecttoRegisterActivity(s,getActivity()); //If authorization fails redirect to login
                new Datadownload(s).execute();
            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
            }


        }
    }


    public class Datadownload extends AsyncTask<String, String, String> {

        private String s;
        private boolean result = false;

        public Datadownload(String s) {
            this.s = s;

        }

        @Override
        protected String doInBackground(String... strings) {
            result = DownloadData(s);
            return s;
        }


        @Override
        protected void onPostExecute(String s) {
            mprogresss.dismiss();
            /*boolean f1 = DownloadData(s);
            if (f1) {*/
            Log.d("VAPT", "String result = " + s);
            //msclass.showMessage("Result :"+result);
            if (result) {
                msclass.showMessage("Data Download successfully");
                Log.d("btnDownloadFeedbackData", "CALL btnDownloadFeedbackData");
                btnDownloadFeedbackData.callOnClick();
            } else {
                msclass.showMessage("Data Not Downloaded");
                btnDownloadFeedbackData.callOnClick();
            }
        }
    }

    public class BreederDataDownloadFeedback extends AsyncTask<String, String, String> {

        private int action;
        private String ddlyear;
        private String ddlsession;
        private String usercode;
        private String TFA;

        String returnstring;

        public BreederDataDownloadFeedback(int action, String ddlyear, String ddlsession, String usercode, String TFA) {
            this.action = action;
            this.ddlyear = ddlyear;
            this.ddlsession = ddlsession;
            this.usercode = usercode;
            this.TFA = TFA;
        }

        protected void onPreExecute() {
            // dialog.setMessage("Loading....");
            //dialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                Log.d("VAPT", "Usercode : " + userCode);
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();
                //String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                //String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCode);
                //jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("userCode", userCode);
                //jsonParam.put("action", action);
                jsonParam.put("year", ddlyear);
                jsonParam.put("season", ddlsession);
                // jsonParam.put("TFA", TFA);
                /*jsonParam.put("access_token", "");
                jsonParam.put("finalmessage", "");*/
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
                jsonboj.put("Table", jsonParam);
                Log.d("VAPT", "Feedback download URL : " + AppConstant.DOWNLOAD_TRIAL_FEEDBACK_URL);
                Log.d("VAPT", "Json object : " + jsonboj);
                /*return HttpUtils.POSTJSON(AppConstant.DOWNLOAD_FEEDBACK_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));*/
                return HttpUtils.POSTJSON(AppConstant.DOWNLOAD_TRIAL_FEEDBACK_URL,
                        jsonboj, "");


            } catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //cx.redirecttoRegisterActivity(s,getActivity()); //If authorization fails redirect to login
                //Log.d("VAPT","Response String : "+s);
                Log.d("VAPT", "XXX Feedback Download successfully Response Time : " + BaseUtils.convertMillisToString());
                new AsyncDownloadDataFeedback(s).execute();
            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
            }

        }
    }

    private class AsyncDownloadDataFeedback extends AsyncTask<String, String, String> {

        private String s;
        boolean result = false;

        public AsyncDownloadDataFeedback(String s) {
            this.s = s;
        }

        @Override
        protected String doInBackground(String... strings) {
            result = DownloadDataFeedback(s);
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            //cx.redirecttoRegisterActivity(s,getActivity()); //If authorization fails redirect to login
            mprogresss.dismiss();

            /*boolean f1 = DownloadDataFeedback(s);
            if (f1) {*/
            if (result) {
                msclass.showMessage("Feedback Data Download successfully");
            } else {
                msclass.showMessage("Feedback Data Not Downloaded");
            }

        }
    }

    private boolean DownloadDataFeedback(String str) {
        boolean fl = false;
        try {
            if (str.contains("true")) {
                Log.d("XXX XDownload", "Feedback DB INSERT Started " + "Time: " + BaseUtils.convertMillisToString());

                databaseHelper1.deleledata("trial_feedback_table", "");
                //databaseHelper1.deleteTable("trial_feedback_table");

                /*databaseHelper1.getDatabase().beginTransaction();
                 */
                JSONObject object = new JSONObject(str);
                JSONArray jArray = object.getJSONArray("data");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    fl = databaseHelper1.InsertFeedbackTrial(
                            jObject.getString("T_YEAR").toString(),
                            jObject.getString("T_SESION").toString(),
                            jObject.getString("Crop").toString(),
                            jObject.getString("Trail_Type").toString(),
                            jObject.getString("TRIL_CODE").toString(),
                            jObject.getString("Location").toString(),
                            jObject.getString("segment").toString(),
                            jObject.getString("PlotNo").toString(),
                            jObject.getString("FBMID").toString(),
                            jObject.getString("FBStaffCode").toString(),
                            jObject.getString("DAS").toString(),
                            jObject.getString("CurrentStage").toString(),
                            jObject.getString("Village").toString()
                    );
                }
                /*databaseHelper1.getDatabase().setTransactionSuccessful();
                databaseHelper1.getDatabase().endTransaction();
                databaseHelper1.getDatabase().close();*/

                /*int count = databaseHelper1.readTrialFeedbackCount();
                Log.d("Feedback","Data Added count="+count);*/
                //Toast.makeText(getActivity(),"Data Added count="+count,Toast.LENGTH_SHORT).show();

            } else {
                msclass.showMessage("Data not available");
            }
        } catch (Exception e) {
            Log.d("MSG", "MSG:" + e.getMessage());
        }
        Log.d("XXX XDownload", "Feedback DB INSERT ended " + "Time: " + BaseUtils.convertMillisToString());
        return fl;
    }

    /*26th Aug 2021
     * 2 APIs added to get Year and Season from back end */
    private void getYearAndSeason() {
        try {
            mprogresss.setMessage("Retrieving Data ");
            mprogresss.show();
            new GetYearData().execute();
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
        }
    }

    public class GetYearData extends AsyncTask<String, String, String> {
        public GetYearData() {
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return HttpUtils.GET(AppConstant.TDMS_GET_YEAR);
            } catch (Exception ex) {
                Log.d("GET YEAR", "Error : " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mprogresss.dismiss();
            Log.d("XXX XDownload", "Get Year Response successfully " + "Time: " + BaseUtils.convertMillisToString());
            Log.d("VAPT", "Get Year Response String : " + s);
            try {
                //Parse Json and Add to data base table
                if (s != null && !s.equals("")) {
                    //STANDARD RESPONSE {"success":true,"Table":["2019","2020","2021"]}
                    JSONObject object = new JSONObject(s);
                    String success = object.getString("success");
                    if (success.equals("true")) {
                        databaseHelper1.deleteTable("year_table");
                        JSONArray jArray = object.getJSONArray("Table");
                        int i = 0;
                        for (i = 0; i < jArray.length(); i++) {
                            String value = jArray.getString(i);
                            Log.d("Insert Year", value);
                            if (value != null && !value.equals("")) {
                                databaseHelper1.insertYear(value);
                            }
                        }
                        //SET Values to Spinner
                        List<String> ddlTrailType = databaseHelper1.getYearList();//Arrays.asList(getResources().getStringArray(R.array.TrailTypelist));
                        ddlYear.setItems(ddlTrailType);
                        ddlYear.setListener(DownloadData.this);
                    }
                }
                Log.d("Calling", "GetSeasonData");
                if (Config.NetworkConnection()) {
                    mprogresss.setMessage("Retrieving Data ");
                    mprogresss.show();
                    new GetSeasonData().execute();
                } else {
                    Toast.makeText(getActivity(), "Internet network not available.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
            }
        }
    }

    public class GetSeasonData extends AsyncTask<String, String, String> {
        public GetSeasonData() {
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return HttpUtils.GET(AppConstant.TDMS_GET_SEASON);
            } catch (Exception ex) {
                Log.d("GET Season", "Error : " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mprogresss.dismiss();
            Log.d("XXX XDownload", "Get Season Response successfully " + "Time: " + BaseUtils.convertMillisToString());
            Log.d("VAPT", "Get Season Response String : " + s);
            try {
                //Parse Json and Add to data base table
                if (s != null && !s.equals("")) {
                    //{"success":true,"Table":[{"Text":"K","Value":"K","Selected":false},{"Text":"R","Value":"R","Selected":false},{"Text":"S","Value":"S","Selected":false}]}
                    JSONObject object = new JSONObject(s);
                    String success = object.getString("success");
                    if (success.equals("true")) {
                        databaseHelper1.deleteTable("season_table");
                        JSONArray jArray = object.getJSONArray("Table");
                        int i = 0;
                        for (i = 0; i < jArray.length(); i++) {
                            JSONObject obj = jArray.getJSONObject(i);
                            String value = obj.getString("Value");
                            if (value != null && !value.equals("")) {
                                Log.d("Insert Year", value);
                                databaseHelper1.insertSeason(value);
                            }
                            //SET Values to Spinner
                            List<String> ddlTraillist = databaseHelper1.getSeasonList();//Arrays.asList(getResources().getStringArray(R.array.Traillist));
                            ddlSesion.setItems(ddlTraillist);
                            ddlSesion.setListener(DownloadData.this);
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
            }
        }
    }

}
