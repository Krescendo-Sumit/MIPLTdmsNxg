package com.tdms.mahyco.nxg.ReportDashboard;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tdms.mahyco.nxg.CommonExecution;
import com.tdms.mahyco.nxg.Config;
import com.tdms.mahyco.nxg.Messageclass;
import com.tdms.mahyco.nxg.R;
import com.tdms.mahyco.nxg.TravelManagement.GeneralMaster;
import com.tdms.mahyco.nxg.databaseHelper;
import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.HttpUtils;
import com.tdms.mahyco.nxg.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MDOweeklyPlanReport extends AppCompatActivity {
    String returnstring;
    //private TableMainLayout tmain,tmain2,tmain3,tmain4 ;
    public RelativeLayout RelativeLayout1, RelativeLayout2, RelativeLayout3, RelativeLayout4;
    public Button btnReport;
    public TextView lbl, lblheader, mdoName, lblweek, lblMonth;

    public ProgressDialog dialog, pd;
    private TextView recyclableTextView;
    public Messageclass msclass;
    public CommonExecution cx;
    Config config;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private int mYear, mMonth, mDay;
    private SimpleDateFormat dateFormatter;
    Calendar dateSelected = Calendar.getInstance();
    private DatePickerDialog datePickerDialog;
    private WebView wb1;
    private Context context;
    private Spinner spMonth, spweek, spmdo;

    databaseHelper mDatabase;
    String userCode;
    Prefs mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdoweekly_plan_report);
//        RelativeLayout1 = (RelativeLayout)findViewById(R.id.RelativeLayout1);
//        RelativeLayout2 = (RelativeLayout)findViewById(R.id.RelativeLayout2);
//        RelativeLayout3 = (RelativeLayout)findViewById(R.id.RelativeLayout3);
//        RelativeLayout4 = (RelativeLayout)findViewById(R.id.RelativeLayout4);
//        lbl  = (TextView) findViewById(R.id.lbl);
//        lblheader = (TextView) findViewById(R.id.lblheader);
//        lblweek = (TextView) findViewById(R.id.lblweek);
//        lblMonth = (TextView) findViewById(R.id.lblMonth);
//        mdoName= (TextView) findViewById(R.id.mdoName);
//        spMonth=(Spinner) findViewById(R.id.spMonth);
//        spweek=(Spinner) findViewById(R.id.spweek);
//        spmdo=(Spinner) findViewById(R.id.spmdo);

        context = this;
        mPref = Prefs.with(context);
        // getSupportActionBar().hide(); //<< this
        getSupportActionBar().setTitle("My Travel Report");
        config = new Config(this); //Here the context is passing
        msclass = new Messageclass(this);
        mDatabase = new databaseHelper(this);
        cx = new CommonExecution(this);
        btnReport = (Button) findViewById(R.id.btnReport);
        // txtDate=(EditText) findViewById(R.id.txtDate);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        //mdoName.setText("MDO Name: "+);
        // mdoName.setText("MDO Name: "+pref.getString("Displayname",null));
        userCode = getUserCode();
        if (getIntent().getStringExtra("ReportType").equals("Innovation")) {
            //lblheader.setText("MY TRAVEL");

            bindInnovationDatata();
        }


        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(TextUtils.isEmpty(txtDate.getText()))
                {
                    msclass.showMessage("Please enter date");
                    txtDate.clearFocus();
                    return;
                } */

                if (getIntent().getStringExtra("ReportType").equals("RetailerandDistributor")) {
                    bindretaileranddistributortag();
                } else {
                    if (getIntent().getStringExtra("ReportType").equals("Innovation")) {
                        bindInnovationDatata();
                    } else {
                        binddata();
                    }
                }
            }
        });


        //  bindMonth();
    }

    private void setDateTimeField(View v) {
        final EditText txt = (EditText) v;
        Calendar newCalendar = dateSelected;


        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);
                txt.setText(dateFormatter.format(dateSelected.getTime()));
            }


        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                txt.setText("");
            }
        });
        datePickerDialog.show();
        // txt.setText(dateFormatter.format(dateSelected.getTime()));


    }

    //    private  void bindMonth() {
//        Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH)+1;
//
//        List<GeneralMaster> gm = new ArrayList<GeneralMaster>();
//        gm.add(new GeneralMaster("1","Jan"));
//        gm.add(new GeneralMaster("2","Feb"));
//        gm.add(new GeneralMaster("3","Mar"));
//        gm.add(new GeneralMaster("4","Apr"));
//        gm.add(new GeneralMaster("5","May"));
//        gm.add(new GeneralMaster("6","Jun"));
//        gm.add(new GeneralMaster("7","Jul"));
//        gm.add(new GeneralMaster("8","Aug"));
//        gm.add(new GeneralMaster("9","Sep"));
//        gm.add(new GeneralMaster("10","Oct"));
//        gm.add(new GeneralMaster("11","Nov"));
//        gm.add(new GeneralMaster("12","Dec"));
//
//        ArrayAdapter<GeneralMaster> adapter = new ArrayAdapter<GeneralMaster>
//                (this,android.R.layout.simple_spinner_dropdown_item, gm);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spMonth.setAdapter(adapter);
//        spMonth.setSelection(config.getIndexonvalue(spMonth, String.valueOf(month)));
//
//        //+++++++++
//
//        List<GeneralMaster> gm2 = new ArrayList<GeneralMaster>();
//        gm2.add(new GeneralMaster("All","All"));
//
//        gm2.add(new GeneralMaster("Week1","Week1"));
//        gm2.add(new GeneralMaster("Week2","Week2"));
//        gm2.add(new GeneralMaster("Week3","Week3"));
//        gm2.add(new GeneralMaster("Week4","Week4"));
//        gm2.add(new GeneralMaster("Week5","Week5"));
//
//     ArrayAdapter<GeneralMaster> adapter2 = new ArrayAdapter<GeneralMaster>
//                (this,android.R.layout.simple_spinner_dropdown_item, gm2);
//        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spweek.setAdapter(adapter2);
//        // GET MDO LISt
//        new GetMDOVISITPlanReport(3,pref.getString("UserID",null),"","","",MDOweeklyPlanReport.this).execute();
//
//
//    }



    public void bindmdo(String result) {
        try {
            JSONObject object = new JSONObject(result);
            //// Table 1 Data display
            JSONArray jArray = object.getJSONArray("Table");

            List<GeneralMaster> gm2 = new ArrayList<GeneralMaster>();
            for (int i = 0; i < jArray.length(); i++) {

                JSONObject jObject = jArray.getJSONObject(i);
                gm2.add(new GeneralMaster(jObject.getString("user_code").toString(), jObject.getString("MDO_name").toString()));

            }
            ArrayAdapter<GeneralMaster> adapter2 = new ArrayAdapter<GeneralMaster>
                    (this, android.R.layout.simple_spinner_dropdown_item, gm2);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spmdo.setAdapter(adapter2);


        } catch (JSONException e) {
            Log.d("Msg",e.getMessage());
            msclass.showMessage("Something went wrong, please try again later");

        } catch (Exception e) {
            Log.d("Msg",e.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
            // dialog.dismiss();
        }
    }

    public void bindretaileranddistributortag() {
        if (config.NetworkConnection()) {
            String str = null;
            boolean fl = false;
            try {
                lbl.setText("");

                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                new GetMDOVISITPlanReport(9, userCodeDecrypt, "", spweek.getSelectedItem().toString(), "", MDOweeklyPlanReport.this).execute();

            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
                msclass.showMessage("Something went wrong, please try again later");
                // dialog.dismiss();
            }
        } else {
            msclass.showMessage("Internet network not available.");
            //  dialog.dismiss();

        }

    }

    public void bindInnovationDatata() {
        binddataintoWEBVIEWInnovation();
    }

    public void binddata() {
        // dialog.setMessage("Loading....");
        // dialog.show();
       /* RelativeLayout1.removeAllViews();
        RelativeLayout2.removeAllViews();
        RelativeLayout3.removeAllViews();
        RelativeLayout4.removeAllViews();
        tmain=new TableMainLayout(this,RelativeLayout1,headers,consize);
        tmain2=new TableMainLayout(this,RelativeLayout2,headers2,consize);
        tmain3=new TableMainLayout(this,RelativeLayout3,headers3,consize);
        tmain4=new TableMainLayout(this,RelativeLayout4,headers4,consize);
        addHeaders();
        tmain.resizeBodyTableRowHeight();
        tmain2.resizeBodyTableRowHeight();
        tmain3.resizeBodyTableRowHeight();
        tmain4.resizeBodyTableRowHeight(); */

        binddataintoWEBVIEW();
    }

    public void binddataintoWEBVIEWInnovation() {
        if (config.NetworkConnection()) {
            String str = null;
            boolean fl = false;
            try {
             //   lbl.setText("MY TRAVEL REPORT");
                // new GetMDOVISITPlanReport(1,pref.getString("UserID",null),dt.Code().toString(),"",MDOweeklyPlanReport.this).execute();
                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                new GetMDOVISITPlanReport(10, userCodeDecrypt, "","", "", MDOweeklyPlanReport.this).execute();


            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
                msclass.showMessage("Something went wrong, please try again later");
                // dialog.dismiss();
            }
        } else {
            msclass.showMessage("Internet network not available.");
            //  dialog.dismiss();

        }

    }

    public void binddataintoWEBVIEW() {
        if (config.NetworkConnection()) {
            String str = null;
            boolean fl = false;
            try {
                GeneralMaster dt = (GeneralMaster) spMonth.getSelectedItem();
                GeneralMaster mdt = (GeneralMaster) spmdo.getSelectedItem();

                if (getIntent().getStringExtra("ReportType").equals("WeeklyPlan")) {
                    lbl.setText("MDO Weekly Visit Plan details Report Month of :\n" + spMonth.getSelectedItem());
                    // new GetMDOVISITPlanReport(1,pref.getString("UserID",null),dt.Code().toString(),"",MDOweeklyPlanReport.this).execute();
                    String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                    String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    new GetMDOVISITPlanReport(4, userCodeDecrypt, dt.Code().toString(), spweek.getSelectedItem().toString(), mdt.Code(), MDOweeklyPlanReport.this).execute();

                }
                if (getIntent().getStringExtra("ReportType").equals("PlannedVsActualSummary")) {
                    lbl.setText("MDO Planned Vs Actual Summary Report Month of : " + spMonth.getSelectedItem());
                    // new GetMDOVISITPlanReport(2,pref.getString("UserID",null),dt.Code().toString(),"",mdt.Code(),MDOweeklyPlanReport.this).execute();
                    String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                    String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    new GetMDOVISITPlanReport(5, userCodeDecrypt, dt.Code().toString(), spweek.getSelectedItem().toString(), mdt.Code(), MDOweeklyPlanReport.this).execute();

                }
                if (getIntent().getStringExtra("ReportType").equals("ActivityProgress")) {
                    lbl.setText("Activity Progress Report Month of : " + spMonth.getSelectedItem());
                    String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                    String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    new GetMDOVISITPlanReport(6,userCodeDecrypt, dt.Code().toString(), spweek.getSelectedItem().toString(), mdt.Code(), MDOweeklyPlanReport.this).execute();


                }
            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
                msclass.showMessage("Something went wrong, please try again later");
                // dialog.dismiss();
            }
        } else {
            msclass.showMessage("Internet network not available.");
            //  dialog.dismiss();

        }

    }

    public void bindresultdata(String result) {
        try {
            //wb1.loadData(myHtmlString, "text/html", null);
            //wb1.loadData(result, "text/html", null);
            wb1 = (WebView) findViewById(R.id.wb1);
            WebSettings webSettings = wb1.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // wb1.loadData(result, "text/html", null);
            wb1.loadDataWithBaseURL(null, result, null, "utf-8", null);

        } catch (Exception e) {
            Log.d("Msg",e.getMessage());
            //msclass.showMessage(e.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
            // dialog.dismiss();
        }
    }


    public class GetMDOVISITPlanReport extends AsyncTask<String, String, String> {

        private int action;
        private String retailerfirm, name, mobileno, seedlicense;
        private String password, week, Month, usercode, MDOcode;
        // private ProgressDialog p;
        private Context ctx;

        public GetMDOVISITPlanReport(int action, String usercode, String Month, String week, String MDOcode, Context ctx) {
            this.action = action;
            this.usercode = usercode;
            this.Month = Month;
            this.week = week;
            this.MDOcode = MDOcode;
            // this.p=new ProgressDialog(ctx);
            this.ctx = ctx;

        }

        protected void onPreExecute() {
            super.onPreExecute();
           /* p.setMessage("Loading  ..... ");
            p.setIndeterminate(false);
            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            p.setCancelable(false);
            p.show(); */
            pd = new ProgressDialog(context);
            pd.setTitle("Processing...");
            pd.setMessage("Please wait.");
            pd.setCancelable(false);
            // pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();

        }

        @Override
        protected String doInBackground(String... urls) {
            String response="";
            try {
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();
                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                jsonParam.put("userId", userCodeDecrypt);
                jsonParam.put("action", action);
                jsonParam.put("taluka", "All");
                jsonParam.put("month", "");
                jsonParam.put("week", "All");
                jsonParam.put("year", "All");
                jsonParam.put("mdocode", "");
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG,""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage,""));
                jsonboj.put("Table", jsonParam);
                String accessToken = mPref.getString(AppConstant.ACCESS_TOKEN_TAG,"");
                response =  HttpUtils.POSTJSON(AppConstant.GET_MDO_VISIT_PLAN_REPORT,
                        jsonboj, accessToken);

                Log.d("VAPT","URL : "+AppConstant.GET_MDO_VISIT_PLAN_REPORT);
                Log.d("VAPT","Json Object : "+jsonParam);
                Log.d("VAPT","Response : "+response);
                return response;
            } catch (Exception ex) {

            }
            return null;
        }

        protected void onPostExecute(String result) {
            cx.redirecttoRegisterActivity(result,context); //If authorization fails redirect to login
            super.onPostExecute(result);
            try {

//                if (action == 60 || action == 50 || action == 40 || action == 4 || action == 2 || action == 6 || action == 5) {
//                    // txtDate.setText("");
//                    // wb1.clearHistory();//clearView();
//                    // wb1.destroy();
//                    if (Build.VERSION.SDK_INT < 18) {
//                        // wb1.clearView();
//                    } else {
//                        // wb1.loadUrl("about:blank");
//                    }
//                    Toast.makeText(ctx, "Report Download completed", Toast.LENGTH_SHORT).show();
//                    pd.dismiss();
//                    bindresultdata(result);
//                }
//                if (action == 3) //GET MDO Details
//                {
//                    bindmdo(result);
//                }
                if (action == 8 || action == 9|| action == 10) //GET MDO Details
                {
                    bindresultdata(result);
                }
                pd.dismiss();
            } catch (Exception e) {
                Log.d("Msg",e.getMessage());

                pd.dismiss();
            }

        }
    }

    public String getUserCode() {
        Cursor data1 = mDatabase.fetchusercode();
        String userCode = "";
        if (data1.getCount() == 0) {
            msclass.showMessage("No Data Available... ");
        } else {
            data1.moveToFirst();
            if (data1 != null) {
                do {
                    userCode = data1.getString((data1.getColumnIndex("user_code")));
                } while (data1.moveToNext());

            }
            data1.close();
        }
        return userCode;
    }


}
