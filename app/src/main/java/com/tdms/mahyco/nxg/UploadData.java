package com.tdms.mahyco.nxg;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.tdms.mahyco.nxg.model.TrialFeedbackSummaryModel;
import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.HttpUtils;
import com.tdms.mahyco.nxg.utils.Prefs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadData extends Fragment {

    Prefs mPref;

    public UploadData() {
        // Required empty public constructor
    }

    String type = null;
    databaseHelper databaseHelper1;
    ProgressDialog dialog;
    public Messageclass msclass;
    public CommonExecution cx;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public String userCode;

    Config config;
    Button btnUpload;
    Button btnUpload2;
    Button btnUploadPLDSown;
    Button btnUploadFeedback;
    TextView txttotalRemain, txttotalRemain1, tvUploadStatus;
    RadioButton rbtsowing, rbtobservation, rnd1, rnd2, rnd3, rndPLD, rbtobservationImage, rndFbk, rndFdbkNew;
    public Button btnUploadAllData; /*Added on 26 April 2021*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_upload_data, container, false);

        rbtsowing = (RadioButton) rootView.findViewById(R.id.rbtsowing);
        rbtobservation = (RadioButton) rootView.findViewById(R.id.rbtobservation);
        rbtobservationImage = (RadioButton) rootView.findViewById(R.id.rbtobservationImage);
        rnd1 = (RadioButton) rootView.findViewById(R.id.rnd1);
        rnd2 = (RadioButton) rootView.findViewById(R.id.rnd2);
        rnd3 = (RadioButton) rootView.findViewById(R.id.rnd3);
        rndPLD = (RadioButton) rootView.findViewById(R.id.rndPLD);
        rndFbk = (RadioButton) rootView.findViewById(R.id.rndFbk);
        rndFdbkNew = (RadioButton) rootView.findViewById(R.id.rndFdbkNew);

        btnUpload = (Button) rootView.findViewById(R.id.btnUpload);
        btnUpload2 = (Button) rootView.findViewById(R.id.btnUpload2);
        btnUploadPLDSown = (Button) rootView.findViewById(R.id.btnUploadPLDSown);
        btnUploadFeedback = (Button) rootView.findViewById(R.id.btnUploadFeedback);

        btnUploadAllData = (Button) rootView.findViewById(R.id.btnUploadAllData); /*Added on 26 April 2021*/

        //   txttotalRemain = (TextView) rootView.findViewById(R.id.txtRemain);
        // txttotalRemain1 = (TextView) rootView.findViewById(R.id.txtRemain1);
        //txtTagRemain=(TextView)rootView.findViewById(R.id.txtTagRemain);

        tvUploadStatus = (TextView)rootView.findViewById(R.id.tv_upload_status);

        msclass = new Messageclass(this.getContext());
        cx = new CommonExecution(this.getContext());
        config = new Config(this.getContext()); //Here the context is passing
        databaseHelper1 = new databaseHelper(this.getContext());
        dialog = new ProgressDialog(this.getContext());
        dialog.setTitle("Uploading Data");

        mPref = Prefs.with(getActivity());

        recordshow();
        recordshow1();

//        rbtobservation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rbtobservation.setChecked(true);
//                rbtsowing.setChecked(false);
//            }
//        });
//
//        rbtsowing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rbtsowing.setChecked(true);
//                rbtobservation.setChecked(false);
//            }
//        });

        final Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount() == 0) {
            msclass.showMessage("No Data Available... ");
        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    //userCode = data.getString((data.getColumnIndex("user_code")));
                    String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                    userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("userCode", "userCode" + userCode);
                } while (data.moveToNext());

            }
            data.close();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rbtsowing.isChecked()) {

                    ///Upload Tag And Sowing Data
                    UploadFarmerData("BreederFarmerDataInsert");
                    UploadTagData("TagData");
                } else if (rbtobservation.isChecked()) {
                    //Upload Observations
                    UploadObservation("UploadObservation");
                } else if (rbtobservationImage.isChecked()) {
                    //Upload Observations
                    new SyncDataAsync_Async("Observationtaken").execute();
                } else {
                    msclass.showMessage("Please Select Any One To Upload");
                }
            }
        });

        btnUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    boolean flag = false;
                    if (recordshowTravel() > 0) {
                        MDO_TravelData();
                        UploadaImage2("mdo_starttravel", "");
                        UploadaImage2("mdo_endtravel", "");

                    } else {
                        msclass.showMessage("Data not available for uploading");
                    }


                } catch (Exception ex) {
                    msclass.showMessage("Something went wrong, please try again later");
                    Log.d("Msg", ex.getMessage());

                }

            }
        });
        btnUploadPLDSown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    boolean flag = false;
                    if (recordshowPLD() > 0) {
                        new SyncDataAsync_Async("PLDNotSown").execute();
                    } else {
                        msclass.showMessage("Data not available for uploading");
                    }


                } catch (Exception ex) {
                    msclass.showMessage("Something went wrong, please try again later");
                    Log.d("Msg", ex.getMessage());

                }

            }
        });

        btnUploadFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rndFdbkNew.isChecked()) {
                    int dataLen = recordshowFeedbackNew();
                    if (dataLen > 0) {
                        //Toast.makeText(getActivity(), "Upload records = " + dataLen, Toast.LENGTH_SHORT).show();
                        try {
                            if (Config.NetworkConnection()) {
                                if (recordshowFeedbackNew() > 0) {
                                    dialog.setTitle("Uploading Data");
                                    dialog.show();
                                    new UploadTrialFeedbackToServer().execute();
                                } else {
                                    dialog.dismiss();
                                    msclass.showMessage("Data not available for Uploading ");
                                }
                            } else {
                                msclass.showMessage("Internet network not available.");
                                dialog.dismiss();
                            }
                        } catch (Exception e) {
                            Log.d("Msg", e.getMessage());
                        }
                    }
                } else {
                    try {

                        boolean flag = false;
                        if (recordshowFeedback() > 0) {
                            uploadFeedbackData("FeedbackTaken", "");//cx.Bredderurlpath);

                        } else {
                            msclass.showMessage("Data not available for uploading");
                        }


                    } catch (Exception ex) {
                        msclass.showMessage("Something went wrong, please try again later");
                        Log.d("MSG", ex.getMessage());

                    }
                }

            }
        });

        /*Added on 26 April 2021*/
        btnUploadAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (Config.NetworkConnection()) {

                        /*First button upload*/
                        if (rbtsowing.isChecked()) {
                            ///Upload Tag And Sowing Data
                            UploadFarmerData("BreederFarmerDataInsert");
                            UploadTagData("TagData");
                        }
                        if (rbtobservation.isChecked()) {
                            //Upload Observations
                            UploadObservation("UploadObservation");
                        }
                        if (rbtobservationImage.isChecked()) {
                            //Upload Observations
                            new SyncDataAsync_Async("Observationtaken").execute();
                        }

                        /*Second button click*/
                        if (recordshowTravel() > 0) {
                            MDO_TravelData();
                            UploadaImage2("mdo_starttravel", "");
                            UploadaImage2("mdo_endtravel", "");
                        }

                        /*Feedback button click*/
                        if (rndFdbkNew.isChecked()) {
                            int dataLen = recordshowFeedbackNew();
                            if (dataLen > 0) {
                                new UploadTrialFeedbackToServer().execute();
                            }
                        }
                        if (recordshowFeedback() > 0) {
                            uploadFeedbackData("FeedbackTaken", "");
                        }

                    } else {
                        msclass.showMessage("Internet network not available.");
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    Log.d("MSG", "MESSAGE  :" + e.getMessage());
                }
            }
        });

        recordshowPLD();
        recordshowTravel();
        recordshowFeedback();
        recordshowFeedbackNew();
        //  new ObservationTaken_Async("Observationtaken").execute();
        return rootView;
    }

    public void UploadaImage2(String Functionname, String apiUrl) {

        try {
            if (config.NetworkConnection()) {
                // dialog.setMessage("Loading....");
                //dialog.show();
                String str = null;
                String Imagestring1 = "";
                String Imagestring2 = "";
                String ImageName = "";
                String ImageName2 = "tt";
                // str = cx.new MDOMasterData(1, txtUsername.getText().toString(), txtPassword.getText().toString()).execute().get();
                String searchQuery = "select  *  from " + Functionname + " where imgstatus='0'";
                Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
                int count = cursor.getCount();
                if (count > 0) {

                    try {
                        tvUploadStatus.setText("Uploading MDO Travel Images, please wait..");

                        cursor.moveToFirst();
                        while (cursor.isAfterLast() == false) {

                            // for (int i=0; i<count;i++) {

                            //START
                            byte[] objAsBytes = null;//new byte[10000];
                            JSONObject object = new JSONObject();
                            try {
                                ImageName = cursor.getString(cursor.getColumnIndex("imgname"));
                                Imagestring1 = databaseHelper1.getImageDatadetail(cursor.getString(cursor.getColumnIndex("imgpath")));

                            } catch (Exception e) {
                                Log.d("Msg", e.getMessage());
                            }
                            try {
                                objAsBytes = object.toString().getBytes("UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                Log.d("Msg", e.getMessage());
                            }
                            new UploadImageData(Functionname, Imagestring1, ImageName, "t").execute(apiUrl);


                            cursor.moveToNext();
                        }
                        cursor.close();
                        //End
                  /* if(str.contains("True")) {

                       dialog.dismiss();
                       msclass.showMessage("Records Uploaded successfully");

                       recordshow();
                   }
                   else
                   {
                    msclass.showMessage("Something went wrong, please try again later");
                       dialog.dismiss();
                   }
                    */
                    } catch (Exception ex) {  // dialog.dismiss();
                        Log.d("Upload", "UploadaImage2 = " + ex.getMessage());
                        msclass.showMessage("Something went wrong, please try again later");

                    }
                } else {
                    //dialog.dismiss();
                    //msclass.showMessage("Image Data not available for Uploading ");
                    // dialog.dismiss();

                }

            } else {
                msclass.showMessage("Internet network not available.");
                //dialog.dismiss();
            }
            // dialog.dismiss();
        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg", ex.getMessage());

        }
    }

    public void uploadObservationImage(String Functionname, String apiUrl) {
        Log.d("uploadObservationImage","handleImageSyncResponse : Functionname:"+Functionname+" apiUrl:"+apiUrl);
        try {
            if (config.NetworkConnection()) {
                // dialog.setMessage("Loading....");
                //dialog.show();
                String str = null;
                String Imagestring1 = "";
                String Imagestring2 = "";
                String ImageName = "";
                String ImageName2 = "tt";
                // str = cx.new MDOMasterData(1, txtUsername.getText().toString(), txtPassword.getText().toString()).execute().get();
                String searchQuery = "select  DISTINCT Image,ImageName  from  Observationtaken  where " +
                        "(Image IS NOT NULL AND Image!='' ) AND ImageSyncStatus='0'";
                Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
                int count = cursor.getCount();
                if (count > 0) {

                    try {

                        cursor.moveToFirst();
                        while (cursor.isAfterLast() == false) {

                            // for (int i=0; i<count;i++) {

                            //START
                            byte[] objAsBytes = null;//new byte[10000];
                            JSONObject object = new JSONObject();
                            try {
                                ImageName = cursor.getString(cursor.getColumnIndex("ImageName"));
                                Imagestring1 = databaseHelper1.getImageDatadetail(cursor.getString(cursor.getColumnIndex("Image")));

                            } catch (Exception e) {
                                Log.d("Msg", e.getMessage());
                            }
                            try {
                                objAsBytes = object.toString().getBytes("UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                Log.d("Msg", e.getMessage());
                            }
                            //tvUploadStatus.setText("Uploading Observation Images taken, please wait..");
                            Log.d("uploadObservationImage","handleImageSyncResponse : Functionname:"+Functionname+" apiUrl:"+apiUrl+" ImageName:"+ImageName+" Imagestring1:"+Imagestring1);
                            syncSingleImage(Functionname, apiUrl, ImageName, Imagestring1);

                            cursor.moveToNext();
                        }
                        cursor.close();

                    } catch (Exception ex) {  // dialog.dismiss();
                        //msclass.showMessage("Something went wrong, please try again later");
                        /*Fatal Exception: java.lang.RuntimeException
                          An error occurred while executing doInBackground()
                          Crashlytics error fixed on 8th Dec 2021 by commenting dialog show*/
                        Log.d("MSG","Something went wrong, please try again later");
                    }
                } else {

                }

            } else {
                //msclass.showMessage("Internet network not available.");
                //dialog.dismiss();
                 /*Fatal Exception: java.lang.RuntimeException
                          An error occurred while executing doInBackground()
                          Crashlytics error fixed on 8th Dec 2021 by commenting dialog show*/
                Log.d("MSG","Internet network not available.");
            }
            // dialog.dismiss();
        } catch (Exception ex) {
            //msclass.showMessage("Something went wrong, please try again later");
             /*Fatal Exception: java.lang.RuntimeException
                          An error occurred while executing doInBackground()
                          Crashlytics error fixed on 8th Dec 2021 by commenting dialog show*/
            Log.d("Msg", ex.getMessage());
            Log.d("MSG","Something went wrong, please try again later");

        }
    }

    public void uploadPLDImage(String Functionname, String apiUrl) {

        try {
            if (config.NetworkConnection()) {
                // dialog.setMessage("Loading....");
                //dialog.show();
                String str = null;
                String Imagestring1 = "";
                String Imagestring2 = "";
                String ImageName = "";
                String ImageName2 = "tt";
                // str = cx.new MDOMasterData(1, txtUsername.getText().toString(), txtPassword.getText().toString()).execute().get();
                //String searchQuery = "select  DISTINCT imagePath,imageName  from  PLD_NOT_SOWN  where " + "(imagePath IS NOT NULL AND imagePath!='' ) AND imageSyncStatus='0'";
                String searchQuery = "select  DISTINCT imagePath,imageName  from  PLDNotSown  where " + "(imagePath IS NOT NULL AND imagePath!='' ) AND imageSyncStatus='0'";
                Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
                int count = cursor.getCount();
                if (count > 0) {
                    //tvUploadStatus.setText("Uploading PLD Images, please wait..");

                    try {

                        cursor.moveToFirst();
                        while (cursor.isAfterLast() == false) {

                            // for (int i=0; i<count;i++) {

                            //START
                            byte[] objAsBytes = null;//new byte[10000];
                            JSONObject object = new JSONObject();
                            try {
                                ImageName = cursor.getString(cursor.getColumnIndex("imageName"));
                                Imagestring1 = databaseHelper1.getImageDatadetail(cursor.getString(cursor.getColumnIndex("imagePath")));

                            } catch (Exception e) {
                                Log.d("Msg", e.getMessage());
                            }
                            try {
                                objAsBytes = object.toString().getBytes("UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                Log.d("Msg", e.getMessage());
                            }
                            syncSingleImage(Functionname, apiUrl, ImageName, Imagestring1);

                            cursor.moveToNext();
                        }
                        cursor.close();

                    } catch (Exception ex) {  // dialog.dismiss();
                        //msclass.showMessage("Something went wrong, please try again later");

                    }
                } else {

                }

            } else {
                Log.d("MSG", "Internet network not available.");
                //msclass.showMessage("Internet network not available.");
                //dialog.dismiss();
            }
            // dialog.dismiss();
        } catch (Exception ex) {
            //msclass.showMessage("Something went wrong, please try again later");
            Log.d("MSG", "Something went wrong, please try again later");
            Log.d("Msg", ex.getMessage());

        }
    }

    public void uploadPLDNotSownData(String Functionname, String apiUrl) {

        /*try {
            if (config.NetworkConnection()) {

                String Imagestring1 = "";
                String ImageName = "";
                String searchQuery = "select  *  from " + Functionname + " where rowSyncStatus='0'";
                Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
                int count = cursor.getCount();
                if (count > 0) {
                    //tvUploadStatus.setText("Uploading PLD Not Sown Images, please wait..");
                    try {

                        cursor.moveToFirst();
                        while (cursor.isAfterLast() == false) {

                            // for (int i=0; i<count;i++) {

                            //START
                            byte[] objAsBytes = null;//new byte[10000];
                            JSONObject object = new JSONObject();
                            try {
                                if (cursor.getString(cursor.getColumnIndex("imageName")) != null
                                        &&
                                        !cursor.getString(cursor.getColumnIndex("imageName")).contains("null")) {
                                    Imagestring1 = databaseHelper1.getImageDatadetail(cursor.getString(cursor.getColumnIndex("imagePath")));
                                    ImageName = cursor.getString(cursor.getColumnIndex("imageName"));

                                }
                                String userCode = cursor.getString(cursor.getColumnIndex("userCode"));
                                String trialCode = cursor.getString(cursor.getColumnIndex("trialCode"));
                                String status = cursor.getString(cursor.getColumnIndex("status"));
                                String remark = cursor.getString(cursor.getColumnIndex("remark"));
                                String date = cursor.getString(cursor.getColumnIndex("date"));
                                String id = cursor.getString(cursor.getColumnIndex("id"));
                                syncSingleImageAndData(Functionname, apiUrl, ImageName, Imagestring1, id, userCode, trialCode, status
                                        , remark, date);

                            } catch (Exception e) {
                                Log.d("Msg", e.getMessage());
                            }


                            cursor.moveToNext();
                        }
                        cursor.close();

                    } catch (Exception ex) {  // dialog.dismiss();
                        msclass.showMessage("Something went wrong, please try again later");

                    }
                } else {

                }

            } else {
                msclass.showMessage("Internet network not available.");
                //dialog.dismiss();
            }
            // dialog.dismiss();
        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg", ex.getMessage());
        }*/

        if (Config.NetworkConnection()) {
            Log.d("PLD_DATA","Inside uploadPLDNotSownData");
            //dialog.setMessage("Loading....");
            //dialog.show();
            String str = null;
            // str = cx.new MDOMasterData(1, txtUsername.getText().toString(), txtPassword.getText().toString()).execute().get();
            String searchQuery = "select  *  from PLDNotSown where rowSyncStatus='0'";
            Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            int count = cursor.getCount();

            if (count > 0) {
                Log.d("PLD_DATA","Inside uploadPLDNotSownData count > 0 :"+searchQuery);

                try {
                    byte[] objAsBytes = null;//new byte[10000];
                    JSONObject object = new JSONObject();
                    try {
                        object.put("Table1", databaseHelper1.getResultsPLD(searchQuery,userCode));
                        Log.d("PLD_DATA","OBJECT : "+object.toString());
                    } catch (JSONException e) {
                        Log.d("Msg", e.getMessage());
                    }
                    try {
                        objAsBytes = object.toString().getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.d("Msg", e.getMessage());
                    }
                    //dialog.setMessage("Loading. Please wait...");
                    //dialog.show();
                    //tvUploadStatus.setText("Uploading PLDNotSown Data please wait..");
                    Log.d("PLD_DATA","Calling UploadDataServerPLDNOTSOWN=============");
                    //str = new UploadDataServerPLDNOTSOWN(Functionname, objAsBytes).execute("").get();
                    str = UploadDataServerPLDNOTSOWN(Functionname, objAsBytes);//.execute("").get();
                    Log.d("PLD_DATA","Calling UploadDataServerPLDNOTSOWN RESULT :"+str);
                    //End
                    cursor.close();
                    //End
                    if (str.contains("True")) {

                       // dialog.dismiss();
                        //tvUploadStatus.setText("PLDNotSown Data Uploaded Successfully..");
                        //msclass.showMessage("Records Uploaded successfully");
                        String searchQuery1 = "update PLDNotSown set rowSyncStatus = '1' where rowSyncStatus='0'";
                        databaseHelper1.runQuery(searchQuery1);
                       // recordshowPLD();
                    } else {
                       // msclass.showMessage(str);
                        //dialog.dismiss();
                    }

                } catch (Exception ex) {
                    //dialog.dismiss();
                    //msclass.showMessage("Something went wrong, please try again later");

                }
            } else {
               // dialog.dismiss();
               // msclass.showMessage("Data not available for Uploading ");
                //dialog.dismiss();

            }

        } else {
            //msclass.showMessage("Internet network not available.");
            //dialog.dismiss();
        }
    }

    public void uploadFeedbackData(String Functionname, String apiUrl) {
        String str = "";
        try {
            if (Config.NetworkConnection()) {


                String searchQuery = " select * from " + Functionname + " where isSyncedStatus='0' and isSubmitted= '1' ";
                Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
                int count = cursor.getCount();
//

                JSONArray jsonArray = new JSONArray();
                if (count > 0) {
                    tvUploadStatus.setText("Uploading Feedback Data, please wait..");

                    try {

                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonArray = databaseHelper1.getResultsFeedback(searchQuery, userCode);
                            jsonObject.put("Table", jsonArray);
                            str = new UploadFeedbackData(Functionname, jsonObject).execute(apiUrl).get();

                        } catch (JSONException e) {
                            Log.d("Msg", e.getMessage());
                        }

                        cursor.close();
                        if (str.contains("True")) {


                            if (Functionname.equals("FeedbackTaken")) {


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    databaseHelper1.Updatedata("update FeedbackTaken  set isSyncedStatus='1'");
                                }


                            }
                            msclass.showMessage("Data uploaded successfully.");
                            tvUploadStatus.setText("Feedback Data Uploaded Successfully..");
                            recordshowFeedback();
                            dialog.dismiss();

                        } else {
                            msclass.showMessage(str + "-E");

                            dialog.dismiss();
                        }

                    } catch (Exception ex) {
                        Log.d("Msg", ex.getMessage());


                    }
                } else {
                    dialog.dismiss();
                    msclass.showMessage("Data not available for Uploading ");


                }
            } else {
                msclass.showMessage("Internet network not available.");
                dialog.dismiss();
            }
            dialog.dismiss();
        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg", ex.getMessage());

        }
    }

    public void MDO_TravelData() {
        //if(config.NetworkConnection()) {
        // dialog.setMessage("Loading. Please wait...");
        //dialog.show();
        String str = null;
        String Imagestring1 = "";
        String Imagestring2 = "";
        String ImageName = "";
        Cursor cursor = null;
        String searchQuery = "";
        int count = 0;
        searchQuery = "select * from mdo_starttravel where Status='0'";
        cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
        count = cursor.getCount();
        cursor.close();

        searchQuery = "select * from mdo_endtravel where Status='0'";
        cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
        count = count + cursor.getCount();
        cursor.close();

        searchQuery = "select * from mdo_addplace where Status='0'";
        cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
        count = count + cursor.getCount();
        cursor.close();

//        searchQuery = "select * from mdo_Retaileranddistributordata where Status='0'";
//        cursor = mDatabase.getReadableDatabase().rawQuery(searchQuery, null );
//        count=count+cursor.getCount();
//        cursor.close();


        if (count > 0) {
            try {
                //START
                byte[] objAsBytes = null;//new byte[10000];
                JSONObject object = new JSONObject();
                try {
                    searchQuery = "select * from mdo_starttravel where Status='0'";
                    object.put("Table1", databaseHelper1.getResultsForTravel(searchQuery, userCode));
                    searchQuery = "select * from mdo_endtravel where Status='0'";
                    object.put("Table2", databaseHelper1.getResultsForTravel(searchQuery, userCode));
                    searchQuery = "select * from mdo_addplace where Status='0'";
                    object.put("Table3", databaseHelper1.getResultsForTravel(searchQuery, userCode));
                    // searchQuery = "select * from mdo_Retaileranddistributordata where Status='0'";
                    object.put("Table4", new JSONArray());
                    //searchQuery = "select * from mdo_retailerproductdetail where Status='0'";
                    object.put("Table5", new JSONArray());
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
                try {
                    objAsBytes = object.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.d("Msg", e.getMessage());
                }

                Log.d("MDO_TravelData", "DATA objAsBytes:" + objAsBytes);
                Log.d("MDO_TravelData", "DATA Imagestring1:" + Imagestring1);
                Log.d("MDO_TravelData", "DATA Imagestring2:" + Imagestring2);
                Log.d("MDO_TravelData", "DATA ImageName:" + ImageName);

                new UploadDataServernew("MDO_TravelData", objAsBytes, Imagestring1, Imagestring2, ImageName, "").execute("");//cx.MDOurlpath);

            } catch (Exception ex) {
                // msclass.showMessage(ex.getMessage());
                Log.d("Upload", "MDO_TravelData = " + ex.getMessage());

            }
        } else {
            // msclass.showMessage("Uploading data not available");

        }

    }

    private int recordshowTravel() {
        int totalcount = 0;
        try {
            String searchQuery = "";


            int count2 = 0;
            searchQuery = "select * from mdo_starttravel where Status='0'";
            Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            count2 = cursor.getCount();
            cursor.close();
            totalcount = totalcount + count2;

            searchQuery = "select * from mdo_endtravel where Status='0'";
            cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            count2 = count2 + cursor.getCount();
            cursor.close();
            totalcount = totalcount + count2;
            searchQuery = "select * from mdo_addplace where Status='0'";
            cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            count2 = count2 + cursor.getCount();
            cursor.close();
            if (count2 > 0) {
                rnd1.setText("Pending my travel data= " + count2);
                rnd1.setEnabled(true);
            } else {
                rnd1.setText("Pending my travel data= 0");
                rnd1.setEnabled(false);
            }
            totalcount = totalcount + count2;
            count2 = 0;
            searchQuery = "select * from mdo_starttravel where imgstatus='0'";
            cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            count2 = count2 + cursor.getCount();
            cursor.close();
            searchQuery = "select * from mdo_endtravel where imgstatus='0'";
            cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            count2 = count2 + cursor.getCount();
            cursor.close();
            totalcount = totalcount + count2;
            if (count2 > 0) {
                rnd3.setText("My travel start and end vehicle reading images = " + count2);
                rnd3.setEnabled(true);
            } else {
                rnd3.setText("My travel start and end vehicle reading images = 0");
                rnd3.setEnabled(false);
            }

        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg", ex.getMessage());
            dialog.dismiss();
        }
        return totalcount;
    }

    private int recordshowPLD() {
        int totalcount = 0;
        try {
            String searchQuery = "";


            int count2 = 0;
            searchQuery = "select * from PLDNotSown where rowSyncStatus='0'";
            Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            count2 = cursor.getCount();
            cursor.close();
            totalcount = totalcount + count2;
            if (totalcount > 0) {
                rndPLD.setText("PLD Sown record  =" + totalcount);
                rndPLD.setEnabled(true);
            } else {
                rndPLD.setText("PLD Sown record  = 0");
                rndPLD.setEnabled(false);
                rndPLD.setChecked(false);
            }

        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg", ex.getMessage());
            dialog.dismiss();
        }
        return totalcount;
    }

    private int recordshowFeedback() {
        int totalcount = 0;
        try {
            String searchQuery = "";
            int count2 = 0;
            searchQuery = "select * from FeedbackTaken where isSyncedStatus='0' and isSubmitted='1' ";
            Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            count2 = cursor.getCount();
            cursor.close();
            totalcount = totalcount + count2;
            rndFbk.setText("Feedback records  =" + String.valueOf(totalcount));


        } catch (Exception ex) {
           // msclass.showMessage("Something went wrong, please try again later");
            rndFbk.setText("Feedback records  = 0");
            Log.d("Msg", ex.getMessage());
            dialog.dismiss();
        }
        return totalcount;
    }

    private int recordshowFeedbackNew() {
        int len = 0;
        ArrayList<TrialFeedbackSummaryModel> feedbackModelList;
        feedbackModelList = new ArrayList<TrialFeedbackSummaryModel>();
        feedbackModelList.clear();
        //String userCode = getUserID();
        Log.d("Survey", "FBStaffCode Data:" + userCode);

        try {
            String searchQuery = "";
            //int count2 = 0;
            searchQuery = "select * from trial_feedback_table where isSyncedStatus='0' and isSubmitted='1' ";
            Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            //count2 = cursor.getCount();

            if (cursor != null && cursor.getCount() > 0) {
                len = cursor.getCount();
                cursor.moveToFirst();
                do {

                    TrialFeedbackSummaryModel model = new TrialFeedbackSummaryModel();

                    if (cursor.getString(cursor.getColumnIndex("Location")) != null) {
                        String Location = cursor.getString(cursor.getColumnIndex("Location")).toString();
                        Log.d("curserFeedback data", "Location : " + Location);
                        model.setLocation(Location);
                    }

                    if (cursor.getString(cursor.getColumnIndex("Village")) != null) {
                        String Village = cursor.getString(cursor.getColumnIndex("Village")).toString();
                        Log.d("curserFeedback data", "Village : " + Village);
                        model.setVillage(Village);
                    }

                    if (cursor.getString(cursor.getColumnIndex("segment")) != null) {
                        String segment = cursor.getString(cursor.getColumnIndex("segment")).toString();
                        Log.d("curserFeedback data", "segment : " + segment);
                        model.setSegment(segment);
                    }

                    if (cursor.getString(cursor.getColumnIndex("dateOfRating")) != null) {
                        String dateOfRating = cursor.getString(cursor.getColumnIndex("dateOfRating")).toString();
                        Log.d("curserFeedback data", "dateOfRating : " + dateOfRating);
                        model.setDateOfVisit(dateOfRating);
                    }

                    if (cursor.getString(cursor.getColumnIndex("PlotNo")) != null) {
                        String PlotNo = cursor.getString(cursor.getColumnIndex("PlotNo")).toString();
                        Log.d("curserFeedback data", "PlotNo : " + PlotNo);
                        model.setPlotNo(PlotNo);
                    }

                    if (cursor.getString(cursor.getColumnIndex("Remarks")) != null) {
                        String Remarks = cursor.getString(cursor.getColumnIndex("Remarks")).toString();
                        Log.d("curserFeedback data", "Remarks : " + Remarks);
                        model.setComment(Remarks);
                    }

                    //Rating mean individual ranks
                    if (cursor.getString(cursor.getColumnIndex("Rating")) != null) {
                        String Rating = cursor.getString(cursor.getColumnIndex("Rating")).toString();
                        Log.d("curserFeedback data", "Rating : " + Rating);
                        model.setRank(Rating);
                    }


                    if (cursor.getString(cursor.getColumnIndex("TrialRating")) != null) {
                        //int TrialRating = Integer.parseInt(FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("TrialRating")));
                        String TrialRating = cursor.getString(cursor.getColumnIndex("TrialRating"));
                        Log.d("curserFeedback data", "TrialRating : " + TrialRating);
                        //spinnerRankFeedback.setSelection(getSpinnerTrialFeedbackIndex(TrialRating));
                        model.setTrialRating("" + TrialRating);
                    }

                    if (cursor.getString(cursor.getColumnIndex("isSubmitted")) != null) {
                        String isSubmitted = cursor.getString(cursor.getColumnIndex("isSubmitted"));
                        model.setIsSubmitted(isSubmitted);
                    }

                    if (cursor.getString(cursor.getColumnIndex("isSyncedStatus")) != null) {
                        String isSyncedStatus = cursor.getString(cursor.getColumnIndex("isSyncedStatus"));
                        model.setIsSynced(isSyncedStatus);
                    }

                    if (cursor.getString(cursor.getColumnIndex("TRIL_CODE")) != null) {
                        String TRIL_CODE = cursor.getString(cursor.getColumnIndex("TRIL_CODE"));
                        model.setTrialCode(TRIL_CODE);
                    }

                    if (cursor.getString(cursor.getColumnIndex("FBStaffCode")) != null) {
                        String FBStaffCode = cursor.getString(cursor.getColumnIndex("FBStaffCode"));
                        model.setFBStaffCode(FBStaffCode);
                    }

                    if (cursor.getString(cursor.getColumnIndex("dateOfVisitSinglePlot")) != null) {
                        String dateOfVisitSinglePlot = cursor.getString(cursor.getColumnIndex("dateOfVisitSinglePlot"));
                        model.setDateOfVisitSinglePlot(dateOfVisitSinglePlot);
                    }

                   /* if(userCode!=null && !userCode.equalsIgnoreCase("")){
                        model.setFBStaffCode(userCode);
                    }*/

                    feedbackModelList.add(model);
                    Log.d("Survey", "Feedback model:" + model.toString());
                } while (cursor.moveToNext());
            }
            Log.d("Survey", "Feedback data Size:" + feedbackModelList.size());
            Log.d("Survey", "Feedback Data:" + feedbackModelList.toString());

            cursor.close();
            int lenData = feedbackModelList.size();
            if (lenData > 0) {
                rndFdbkNew.setText("Trial Feedback records  =" + lenData);
                rndFdbkNew.setEnabled(true);
            } else {
                rndFdbkNew.setText("Trial Feedback records  = 0");
                rndFdbkNew.setEnabled(false);
            }

        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("MSG", ex.getMessage());
            dialog.dismiss();
        }
        return len;
    }

    private ArrayList<TrialFeedbackSummaryModel> getRecordsForTrialFeedback() {
        ArrayList<TrialFeedbackSummaryModel> feedbackModelList;
        feedbackModelList = new ArrayList<TrialFeedbackSummaryModel>();
        feedbackModelList.clear();
        //String userCode = getUserID();
        Log.d("Survey", "FBStaffCode Data:" + userCode);

        try {
            String searchQuery = "";
            //int count2 = 0;
            searchQuery = "select * from trial_feedback_table where isSyncedStatus='0' and isSubmitted='1' ";
            Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            //count2 = cursor.getCount();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    TrialFeedbackSummaryModel model = new TrialFeedbackSummaryModel();

                    if (cursor.getString(cursor.getColumnIndex("Location")) != null) {
                        String Location = cursor.getString(cursor.getColumnIndex("Location")).toString();
                        Log.d("curserFeedback data", "Location : " + Location);
                        model.setLocation(Location);
                    }

                    if (cursor.getString(cursor.getColumnIndex("Village")) != null) {
                        String Village = cursor.getString(cursor.getColumnIndex("Village")).toString();
                        Log.d("curserFeedback data", "Village : " + Village);
                        model.setVillage(Village);
                    }

                    if (cursor.getString(cursor.getColumnIndex("segment")) != null) {
                        String segment = cursor.getString(cursor.getColumnIndex("segment")).toString();
                        Log.d("curserFeedback data", "segment : " + segment);
                        model.setSegment(segment);
                    }

                    if (cursor.getString(cursor.getColumnIndex("dateOfRating")) != null) {
                        String dateOfRating = cursor.getString(cursor.getColumnIndex("dateOfRating")).toString();
                        Log.d("curserFeedback data", "dateOfRating : " + dateOfRating);
                        model.setDateOfVisit(dateOfRating);
                    }

                    if (cursor.getString(cursor.getColumnIndex("PlotNo")) != null) {
                        String PlotNo = cursor.getString(cursor.getColumnIndex("PlotNo")).toString();
                        Log.d("curserFeedback data", "PlotNo : " + PlotNo);
                        model.setPlotNo(PlotNo);
                    }

                    if (cursor.getString(cursor.getColumnIndex("Remarks")) != null) {
                        String Remarks = cursor.getString(cursor.getColumnIndex("Remarks")).toString();
                        Log.d("curserFeedback data", "Remarks : " + Remarks);
                        model.setComment(Remarks);
                    }

                    //Rating mean individual ranks
                    if (cursor.getString(cursor.getColumnIndex("Rating")) != null) {
                        String Rating = cursor.getString(cursor.getColumnIndex("Rating")).toString();
                        Log.d("curserFeedback data", "Rating : " + Rating);
                        model.setRank(Rating);
                    }


                    if (cursor.getString(cursor.getColumnIndex("TrialRating")) != null) {
                        //int TrialRating = Integer.parseInt(FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("TrialRating")));
                        String TrialRating = cursor.getString(cursor.getColumnIndex("TrialRating"));
                        Log.d("curserFeedback data", "TrialRating : " + TrialRating);
                        //spinnerRankFeedback.setSelection(getSpinnerTrialFeedbackIndex(TrialRating));
                        model.setTrialRating("" + TrialRating);
                    }

                    if (cursor.getString(cursor.getColumnIndex("isSubmitted")) != null) {
                        String isSubmitted = cursor.getString(cursor.getColumnIndex("isSubmitted"));
                        model.setIsSubmitted(isSubmitted);
                    }

                    if (cursor.getString(cursor.getColumnIndex("isSyncedStatus")) != null) {
                        String isSyncedStatus = cursor.getString(cursor.getColumnIndex("isSyncedStatus"));
                        model.setIsSynced(isSyncedStatus);
                    }

                    if (cursor.getString(cursor.getColumnIndex("TRIL_CODE")) != null) {
                        String TRIL_CODE = cursor.getString(cursor.getColumnIndex("TRIL_CODE"));
                        model.setTrialCode(TRIL_CODE);
                    }

                    if (cursor.getString(cursor.getColumnIndex("FBStaffCode")) != null) {
                        String FBStaffCode = cursor.getString(cursor.getColumnIndex("FBStaffCode"));
                        model.setFBStaffCode(FBStaffCode);
                    }

                    if (cursor.getString(cursor.getColumnIndex("dateOfVisitSinglePlot")) != null) {
                        String dateOfVisitSinglePlot = cursor.getString(cursor.getColumnIndex("dateOfVisitSinglePlot"));
                        model.setDateOfVisitSinglePlot(dateOfVisitSinglePlot);
                    }
                    else{
                        Log.d("Survey", "dateOfVisitSinglePlot NOT EXIST");
                    }

                   /* if(userCode!=null && !userCode.equalsIgnoreCase("")){
                        model.setFBStaffCode(userCode);
                    }*/

                    feedbackModelList.add(model);
                    Log.d("Survey", "Feedback model:" + model.toString());
                } while (cursor.moveToNext());
            }
            Log.d("Survey", "Feedback data Size:" + feedbackModelList.size());
            Log.d("Survey", "Feedback Data:" + feedbackModelList.toString());

            cursor.close();

        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("MSG", ex.getMessage());
            dialog.dismiss();
        }
        return feedbackModelList;
    }

    private String getUserID() {
        databaseHelper1 = new databaseHelper(getActivity());
        Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount() == 0) {

        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                    userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("userCode", "userCode" + userCode);
                    //String userCode = data.getString((data.getColumnIndex("user_code")));
                    return userCode;
                } while (data.moveToNext());

            }
            data.close();
        }
        return "";
    }

    private void recordshow() {
        String searchQuery = "select  *  from FarmerInfodata inner join tagdata1 on FarmerInfodata.TRIL_CODE=tagdata1.TRIL_CODE where tagdata1.flag='T' and tagdata1.Upload='U'";
        Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            rbtsowing.setText("Pending Sowing & Tag Data = " + count);
            rbtsowing.setEnabled(true);
        } else {
            rbtsowing.setText("Pending Sowing & Tag Data = 0");
            rbtsowing.setEnabled(false);
        }

    }

    private void updateDataFeedbackInDB(TrialFeedbackSummaryModel model) {
        databaseHelper1 = new databaseHelper(getActivity());
        String isSynched = "1"; //Update data as synched
        Date entrydate = new Date();
        String InTime = new SimpleDateFormat("dd-MM-yyyy").format(entrydate);
        databaseHelper1.updateTrialFeedback(model.getTrialCode(), model.getPlotNo(), isSynched, model.getRank(), model.getComment(), model.getIsSubmitted(),InTime);

    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Upload Data");
    }

    private void recordshow1() {
        String searchQuery = "select  distinct PlotNo  from Observationtaken where flag='0'";
        Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
        int count1 = cursor.getCount();
        cursor.close();
        if (count1 > 0) {
            rbtobservation.setText("Pending Plot To Upload = " + count1);
            rbtobservation.setEnabled(true);
        } else {
            rbtobservation.setText("Pending Plot To Upload = 0");
            rbtobservation.setEnabled(false);
        }
        String searchQuery1 = "select  DISTINCT Image,ImageName  from Observationtaken where " +
                "(Image IS NOT NULL AND Image!='' ) AND ImageSyncStatus='0'";
        Cursor cursor1 = databaseHelper1.getReadableDatabase().rawQuery(searchQuery1, null);
        int count = cursor1.getCount();
        cursor1.close();
        Log.d("recordshow1","handleImageSyncResponse : count: "+count);
        if (count > 0) {
            rbtobservationImage.setText("Pending Observation Images To Upload = " + count);
            rbtobservationImage.setEnabled(true);
        } else {
            rbtobservationImage.setText("Pending Observation Images To Upload = 0");
            rbtobservationImage.setEnabled(false);
        }
    }

    public void UploadFarmerData(String BreederFarmerDataInsert) {
        if (Config.NetworkConnection()) {
            dialog.setMessage("Loading....");
            dialog.show();
            String str = null;
            // str = cx.new MDOMasterData(1, txtUsername.getText().toString(), txtPassword.getText().toString()).execute().get();
            String searchQuery = "select  *  from FarmerInfodata where flag='0'";
            Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            int count = cursor.getCount();

            if (count > 0) {

                try {
                    byte[] objAsBytes = null;//new byte[10000];
                    JSONObject object = new JSONObject();
                    try {
                        object.put("Table1", databaseHelper1.getResults(searchQuery));

                    } catch (JSONException e) {
                        Log.d("Msg", e.getMessage());
                    }
                    try {
                        objAsBytes = object.toString().getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.d("Msg", e.getMessage());
                    }
                    dialog.setMessage("Loading. Please wait...");
                    dialog.show();
                    tvUploadStatus.setText("Uploading Farmer Data please wait..");
                    str = new UploadDataServer(BreederFarmerDataInsert, objAsBytes).execute("").get();
                    //End
                    cursor.close();
                    //End
                    if (str.contains("True")) {

                        dialog.dismiss();
                        tvUploadStatus.setText("Farmer Data Uploaded Successfully..");
                        msclass.showMessage("Records Uploaded successfully");
                        String searchQuery1 = "update FarmerInfodata set flag = '1' where flag='0'";
                        databaseHelper1.runQuery(searchQuery1);

                        recordshow();
                    } else {
                        msclass.showMessage(str);
                        dialog.dismiss();
                    }

                } catch (Exception ex) {
                    dialog.dismiss();
                    msclass.showMessage("Something went wrong, please try again later");

                }
            } else {
                dialog.dismiss();
                msclass.showMessage("Data not available for Uploading ");
                dialog.dismiss();

            }

        } else {
            msclass.showMessage("Internet network not available.");
            dialog.dismiss();
        }
        //dialog.dismiss();
    }

    // To Upload Obseervation On server
    public void UploadObservation(String UploadObservation) {
        if (config.NetworkConnection()) {
            dialog.setMessage("Loading....");
            dialog.show();
            String str = null;
            // str = cx.new MDOMasterData(1, txtUsername.getText().toString(), txtPassword.getText().toString()).execute().get();
            String searchQuery = "select  *  from Observationtaken where flag='0'";
            Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            int count = cursor.getCount();

            if (count > 0) {

                try {
                    byte[] objAsBytes = null;//new byte[10000];
                    JSONObject object = new JSONObject();
                    try {
                        object.put("Table1", databaseHelper1.getResults(searchQuery));

                    } catch (JSONException e) {
                        Log.d("Msg", e.getMessage());
                    }
                    try {
                        objAsBytes = object.toString().getBytes("UTF-8");

                        Log.d("Observation","objAsBytes : "+objAsBytes);

                    } catch (UnsupportedEncodingException e) {
                        Log.d("Msg", e.getMessage());
                    }
                    dialog.setMessage("Loading. Please wait...");
                    dialog.show();
                    tvUploadStatus.setText("Uploading Observation Data please wait..");

                    str = new UploadObservationData(UploadObservation, objAsBytes).execute("").get();

                    //End
                    cursor.close();
                    //End
                    if (str.contains("True")) {

                        dialog.dismiss();
                        msclass.showMessage("Records Uploaded successfully");
                        String searchQuery1 = "update observationtaken set flag = '1' where flag='0'";
                        databaseHelper1.runQuery(searchQuery1);

                        tvUploadStatus.setText("Observation Data Uploaded Successfully..");

                        recordshow1();
                    } else {
                        msclass.showMessage(str);
                        dialog.dismiss();
                    }

                } catch (Exception ex) {
                    dialog.dismiss();
                    msclass.showMessage("Something went wrong, please try again later");

                }
            } else {
                dialog.dismiss();
                msclass.showMessage("Data not available for Uploading ");
                dialog.dismiss();

            }

        } else {
            msclass.showMessage("Internet network not available.");
            dialog.dismiss();
        }
        //dialog.dismiss();
    }

    public void UploadTagData(String TagData) {
        if (config.NetworkConnection()) {
            dialog.setMessage("Loading....");
            dialog.show();
            String str = null;
            // str = cx.new MDOMasterData(1, txtUsername.getText().toString(), txtPassword.getText().toString()).execute().get();

            String searchQuery = "select  *  from tagdata1 where flag='T' and Upload='U'";
            Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
            int count = cursor.getCount();

            if (count > 0) {

                try {
                    byte[] objAsBytes = null;//new byte[10000];
                    JSONObject object = new JSONObject();
                    try {
                        object.put("Table1", databaseHelper1.getResults(searchQuery));
                        cursor.moveToFirst();
                        String userId = cursor.getString(cursor.getColumnIndex("userCode"));
                        Log.d("TAG_DATA", "SEND USERCODE : " + userId);
                    } catch (JSONException e) {
                        Log.d("Msg", e.getMessage());
                    }
                    try {
                        objAsBytes = object.toString().getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.d("Msg", e.getMessage());
                    }
                    dialog.setMessage("Loading. Please wait...");
                    dialog.show();
                    tvUploadStatus.setText("Uploading Tag Data please wait..");
                    str = new UploadDataServerTagData(TagData, objAsBytes).execute().get();
                    //End
                    cursor.close();
                    //End
                    if (str.contains("True")) {

                        dialog.dismiss();
                        //msclass.showMessage("Tag Data Uploaded successfully");

                        String searchQuery1 = "update tagdata set Uplaod = 'Y' where flag='T' and Uplaod='U'";
                        databaseHelper1.runQuery(searchQuery1);

                        String searchQuery2 = "update tagdata1 set Upload = 'Y' where flag='T'and Upload='U'";
                        databaseHelper1.runQuery(searchQuery2);

                        tvUploadStatus.setText("Tag Data Uploaded Successfully..");

                        recordshow();
                    } else {
                        msclass.showMessage(str);
                        dialog.dismiss();
                    }

                } catch (Exception ex) {
                    dialog.dismiss();
                    msclass.showMessage("Something went wrong, please try again later");

                }
            } else {
                dialog.dismiss();
                msclass.showMessage("Data not available for Uploading ");
                dialog.dismiss();

            }

        } else {
            msclass.showMessage("Internet network not available.");
            dialog.dismiss();
        }
        //dialog.dismiss();
    }
    //

    public synchronized void syncSingleImage(String function, String urls, String ImageName, String Imagestring1) {
        try {
            JSONObject jsonboj = new JSONObject();
            JSONObject jsonParam = new JSONObject();

            String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
            String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
            jsonParam.put("userCode", userCodeDecrypt);
            jsonParam.put("from", "UploadImages");
            jsonParam.put("input1", Imagestring1);
            jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
            jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
            jsonboj.put("Table", jsonParam);
            String response = HttpUtils.POSTJSON(AppConstant.UPLOAD_IMAGES_URL,
                    jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));

            handleImageSyncResponse(function, response, ImageName, "");

        } catch (Exception ex) {
        }
    }

    public synchronized void syncSingleImageAndData(String function, String urls, String ImageName
            , String Imagestring1
            , String id
            , String userCode
            , String trialCode
            , String status
            , String remark
            , String date
    ) {
        String response = "";
        try {
            JSONObject jsonboj = new JSONObject();
            JSONObject jsonParam = new JSONObject();
            String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
            String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
            jsonParam.put("userCode", userCodeDecrypt);
            jsonParam.put("from", "PLDNotSown");
            jsonParam.put("ImageName", ImageName);
            jsonParam.put("id", id);
            jsonParam.put("trialCode", trialCode);
            jsonParam.put("status", status);
            jsonParam.put("remark", remark);
            jsonParam.put("date", date);
            jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
            jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
            jsonboj.put("Table", jsonParam);
            Log.d("PLD", "URL : " + AppConstant.PLD_NOT_SOWN_URL);
            Log.d("PLD", "OBJECT : " + jsonboj);
            HttpUtils.POSTJSON(AppConstant.PLD_NOT_SOWN_URL,
                    jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));

            handleImageSyncResponse(function, response, ImageName, id);

        } catch (Exception ex) {
        }
    }


    class SyncDataAsync_Async extends AsyncTask<Void, Void, String> {
        //  ProgressDialog progressDialog;

        String tag;
        ProgressDialog progressDialog;


        public SyncDataAsync_Async(String tag) {
            this.tag = tag;
        }

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setMessage("Uploading data please wait");
            progressDialog.setCancelable(true);
            // progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            tvUploadStatus.setText("Uploading Images taken, please wait..");
        }

        @Override
        protected String doInBackground(Void... params) {
            if (tag.equals("Observationtaken"))
                //uploadObservationImage("UploadImages", "");//cx.Bredderurlpath);
                uploadObservationImage("Observationtaken", "");//cx.Bredderurlpath);
            else if (tag.equals("PLDNotSown")) {
                uploadPLDNotSownData("PLDNotSown", "");//cx.Bredderurlpath"");
                uploadPLDImage("PLDNotSown", "");//cx.Bredderurlpath);
            }
//            else if (tag.equals("FeedbackTaken")) {
//                uploadFeedbackData("FeedbackTaken", cx.Bredderurlpath);
//            }

            return "";
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)

        protected void onPostExecute(String result) {

            cx.redirecttoRegisterActivity(result, getActivity()); //If authorization fails redirect to login
            tvUploadStatus.setText("Images Uploaded Successfully..");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d("PLDNotSown","PLDNotSown onPostExecute : "+tag);
            if (tag.equals("PLDNotSown")) {
                recordshowPLD();
            }
        }
    }

    public class UploadDataServer extends AsyncTask<String, String, String> {

        byte[] objAsBytes;
        String Imagestring1;
        String Imagestring2;
        String ImageName;
        String Funname;

        public UploadDataServer(String Funname, byte[] objAsBytes) {

            //this.IssueID=IssueID;
            this.objAsBytes = objAsBytes;
            this.Imagestring1 = Imagestring1;
            this.Imagestring2 = Imagestring2;
            this.ImageName = ImageName;
            this.Funname = Funname;

        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                // encode image to base64 so that it can be picked by saveImage.php file
                String encodeImage = Base64.encodeToString(objAsBytes, Base64.DEFAULT);
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();
                Prefs mPref = Prefs.with(getActivity());
                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                Log.d("VAPT", "userCodeEncrypt :" + userCodeEncrypt);
                Log.d("VAPT", "userCodeDecrypt : " + userCodeDecrypt);
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("encodedData", encodeImage);
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
                jsonboj.put("Table", jsonParam);
                Log.d("VAPT", "Upload_Sowing URL : " + (AppConstant.BASE_URL + AppConstant.BREEDER_FARMER_DATA_INSERT_URL));
                Log.d("VAPT", "Upload Sowing OBJECT : " + jsonParam.toString());
                return HttpUtils.POSTJSON(AppConstant.BREEDER_FARMER_DATA_INSERT_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));

            } catch (Exception ex) {
            }
            return null;
        }

        protected void onPostExecute(String result) {
            String weatherInfo = "Weather Report  is: \n";
            try {
                cx.redirecttoRegisterActivity(result, getActivity()); //If authorization fails redirect to login

                String resultout = result.trim();
                if (resultout.equals("True")) {
                    // msclass.showMessage("Data uploaded successfully.");

                    if (Funname.equals("BreederFarmerDataInsert")) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = new Date();
                        String strdate = dateFormat.format(d);
                        // String searchQuery = "select * from TagData  where  strftime( '%Y-%m-%d', INTime)<>'"+strdate+"'  ";
                        // mDatabase.deleterecord("delete from TagData where strftime( '%Y-%m-%d', INTime)<>'"+strdate+"' ");
                        // mDatabase.Updatedata("update TagData  set Status='1' where Imgname='"+ImageName+"'");
                        recordshow();
                    }

                } else {
                    //msclass.showMessage(result + "error");
                    msclass.showMessage("Something went wrong, please try again later");
                }

                // dialog.dismiss();


            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
                msclass.showMessage("Something went wrong, please try again later");
                dialog.dismiss();
            }

        }
    }

    public class UploadDataServerTagData extends AsyncTask<String, String, String> {

        byte[] objAsBytes;
        String Imagestring1;
        String Imagestring2;
        String ImageName;
        String Funname;

        public UploadDataServerTagData(String Funname, byte[] objAsBytes) {

            //this.IssueID=IssueID;
            this.objAsBytes = objAsBytes;
            this.Imagestring1 = Imagestring1;
            this.Imagestring2 = Imagestring2;
            this.ImageName = ImageName;
            this.Funname = Funname;
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                // encode image to base64 so that it can be picked by saveImage.php file
                String encodeImage = Base64.encodeToString(objAsBytes, Base64.DEFAULT);
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();
                Prefs mPref = Prefs.with(getActivity());
                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("encodedData", encodeImage);
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
                jsonboj.put("Table", jsonParam);
                Log.d("TAG_DATA", "API : " + AppConstant.BASE_URL + AppConstant.TAG_DATA_URL);
                Log.d("TAG_DATA", "TABLE : " + jsonParam);
                return HttpUtils.POSTJSON(AppConstant.TAG_DATA_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));

            } catch (Exception ex) {
            }
            return null;
        }

        protected void onPostExecute(String result) {
            cx.redirecttoRegisterActivity(result, getActivity()); //If authorization fails redirect to login
            /*String weatherInfo = "Weather Report  is: \n";
            try {
                String resultout = result.trim();
                if (resultout.equals("True")) {
                    // msclass.showMessage("Data uploaded successfully.");

                    if (Funname.equals("BreederFarmerDataInsert")) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = new Date();
                        String strdate = dateFormat.format(d);
                        // String searchQuery = "select * from TagData  where  strftime( '%Y-%m-%d', INTime)<>'"+strdate+"'  ";
                        // mDatabase.deleterecord("delete from TagData where strftime( '%Y-%m-%d', INTime)<>'"+strdate+"' ");
                        // mDatabase.Updatedata("update TagData  set Status='1' where Imgname='"+ImageName+"'");
                        recordshow();
                    }

                } else {
                    msclass.showMessage(result + "error");
                }

                // dialog.dismiss();


            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
                msclass.showMessage(e.getMessage().toString());
                dialog.dismiss();
            }*/
        }
    }

    public class UploadObservationData extends AsyncTask<String, String, String> {

        byte[] objAsBytes;
        String Imagestring1;
        String Imagestring2;
        String ImageName;
        String Funname;

        public UploadObservationData(String Funname, byte[] objAsBytes) {

            //this.IssueID=IssueID;
            this.objAsBytes = objAsBytes;
            this.Imagestring1 = Imagestring1;
            this.Imagestring2 = Imagestring2;
            this.ImageName = ImageName;
            this.Funname = Funname;
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                // encode image to base64 so that it can be picked by saveImage.php file
                String encodeImage = Base64.encodeToString(objAsBytes, Base64.DEFAULT);
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();
                Prefs mPref = Prefs.with(getActivity());
                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("encodedData", encodeImage);
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
                jsonboj.put("Table", jsonParam);
                Log.d("OBSERVATION","Complete URL :"+AppConstant.UPLOAD_OBSERVATION_URL);
                Log.d("OBSERVATION","Complete OBJECT :"+jsonboj.toString());
                return HttpUtils.POSTJSON(AppConstant.UPLOAD_OBSERVATION_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));

            } catch (Exception ex) {
            }
            return null;
        }

        protected void onPostExecute(String result) {
            cx.redirecttoRegisterActivity(result, getActivity()); //If authorization fails redirect to login
            /*String weatherInfo = "Weather Report  is: \n";
            try {
                String resultout = result.trim();
                if (resultout.equals("True")) {
                    // msclass.showMessage("Data uploaded successfully.");

                    if (Funname.equals("BreederFarmerDataInsert")) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = new Date();
                        String strdate = dateFormat.format(d);
                        // String searchQuery = "select * from TagData  where  strftime( '%Y-%m-%d', INTime)<>'"+strdate+"'  ";
                        // mDatabase.deleterecord("delete from TagData where strftime( '%Y-%m-%d', INTime)<>'"+strdate+"' ");
                        // mDatabase.Updatedata("update TagData  set Status='1' where Imgname='"+ImageName+"'");
                        recordshow();
                    }

                } else {
                    msclass.showMessage(result + "error");
                }

                // dialog.dismiss();


            } catch (Exception e) {
                Log.d("Msg",e.getMessage());
                msclass.showMessage(e.getMessage().toString());
                dialog.dismiss();
            }*/

        }
    }

    public class UploadFeedbackData extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        JSONObject obj;
        String Funname;


        public UploadFeedbackData(String Funname, JSONObject obj) {

            this.obj = obj;
            this.Funname = Funname;
        }

        protected void onPreExecute() {

//            pd = new ProgressDialog(getActivity());
//            pd.setTitle("Data Uploading ...");
//            pd.setMessage("Please wait.");
//            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            pd.show();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            byte[] objAsBytes = null;//new byte[10000];
            try {
                objAsBytes = obj.toString().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.d("Msg", e.getMessage());
            }
            String encodeImage = Base64.encodeToString(objAsBytes, Base64.DEFAULT);

            try {

                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();

                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("from", "FeedbackTaken");
                jsonParam.put("FeedbackData", encodeImage);
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
                jsonboj.put("Table", jsonParam);
                return HttpUtils.POSTJSON(AppConstant.FEEDBACK_TAKEN_DATA_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));


            } catch (Exception ex) {

            }
            return null;
        }

        protected void onPostExecute(String result) {
            try {
                cx.redirecttoRegisterActivity(result, getActivity()); //If authorization fails redirect to login
                String resultout = result.trim();
                Log.d("Upload", "onPostExecute: " + resultout);
                dialog.dismiss();

            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
                dialog.dismiss();
            }

        }
    }

    public class UploadImageData extends AsyncTask<String, String, String> { //MyTravel
        ProgressDialog pd;
        byte[] objAsBytes;
        String Imagestring1;
        String Imagestring2;
        String ImageName, ImageName2;
        String Funname, Intime;

        public UploadImageData(String Funname, String Imagestring1, String ImageName, String Intime) {

            //this.IssueID=IssueID;
            this.objAsBytes = objAsBytes;
            this.Imagestring1 = Imagestring1;
            this.Imagestring2 = Imagestring2;
            this.ImageName = ImageName;
            this.ImageName2 = ImageName2;
            this.Funname = Funname;
            this.Intime = Intime;

        }

        protected void onPreExecute() {

            pd = new ProgressDialog(getActivity());
            pd.setTitle("Data Uploading ...");
            pd.setMessage("Please wait.");
            // pd.setCancelable(false);
            // pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();

                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("from", "UploadImages");
                //jsonParam.put("input1", Imagestring1); /*Changed on 8th Jan 2021*/

                //Required Object
                /*{
                    "Table": {
                    "access_token": "string",
                            "finalmessage": "string",
                            "userCode": "string",
                            "ImageName": "string",
                            "input": "string"
                }
                }*/

                jsonParam.put("ImageName", ImageName);
                if (Imagestring1.equalsIgnoreCase("") || Imagestring1.equals("") || Imagestring1.equals(null)) {
                    jsonParam.put("input", "NoImage");
                } else {
                    jsonParam.put("input", Imagestring1);
                }
                //jsonParam.put("input2", Imagestring2);
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
                jsonboj.put("Table", jsonParam);
                Log.d("ImageUpload", "------------------------------------------UPLOAD----------------------------------------");
                Log.d("ImageUpload", "Travel URL : " + AppConstant.UPLOAD_IMAGES_URL);
                Log.d("ImageUpload", "Travel jsonboj : " + jsonboj);

                return HttpUtils.POSTJSON(AppConstant.UPLOAD_IMAGES_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));


            } catch (Exception ex) {

            }

            // encodeImage = Base64.encodeToString(objAsBytes,Base64.DEFAULT);
            HttpClient httpclient = new DefaultHttpClient();
            StringBuilder builder = new StringBuilder();
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>(2);
            postParameters.add(new BasicNameValuePair("Type", "UploadImages"));
            //postParameters.add(new BasicNameValuePair("encodedData", encodeImage));
            postParameters.add(new BasicNameValuePair("input1", Imagestring1));
            postParameters.add(new BasicNameValuePair("input2", Imagestring2));

            //String Urlpath=urls[0];

            String Urlpath = urls[0] + "?ImageName=" + ImageName + "&ImageName2=" + ImageName2;
            Log.d("rohit", "doInBackground: " + Urlpath);
            Log.d("rohit", "doInBackground:params::: " + postParameters);
            HttpPost httppost = new HttpPost(Urlpath);
            httppost.addHeader("Content-type", "application/x-www-form-urlencoded");

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
                httppost.setEntity(formEntity);

                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line).append("\n");
                    }

                }
                pd.dismiss();
            } catch (ClientProtocolException e) {
                Log.d("Msg", e.getMessage());
                // msclass.showMessage(e.getMessage().toString());
                pd.dismiss();

            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
                // msclass. showMessage(e.getMessage().toString());
                pd.dismiss();
            }

            pd.dismiss();
            return builder.toString();
        }

        protected void onPostExecute(String result) {
            String weatherInfo = "Weather Report  is: \n";
            try {
                Log.d("ImageUpload", "Travel result : " + result);
                String resultout = result.trim();
                if (resultout.contains("True")) {
                    // msclass.showMessage("Data uploaded successfully.");
                    pd.dismiss();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = new Date();
                    String strdate = dateFormat.format(d);
                    if (Funname.equals("mdo_starttravel")) {
                        databaseHelper1.Updatedata("update mdo_starttravel  set imgstatus='1' where imgname='" + ImageName + "'");
                        pd.dismiss();
                    }
                    if (Funname.equals("mdo_endtravel")) {
                        databaseHelper1.Updatedata("update mdo_endtravel  set imgstatus='1' where imgname='" + ImageName + "'");
                        databaseHelper1.Updatedata("delete from mdo_starttravel  where  Status='1' and strftime( '%Y-%m-%d', startdate)<>'" + strdate + "'");
                        databaseHelper1.Updatedata("delete from  mdo_endtravel  where  Status='1' and strftime( '%Y-%m-%d', enddate)<>'" + strdate + "'");
                        databaseHelper1.Updatedata("delete from  mdo_addplace  where  Status='1' and strftime( '%Y-%m-%d', date)<>'" + strdate + "'");

                        pd.dismiss();
                    }
                    if (Funname.equals("Observationtaken")) {
                        databaseHelper1.Updatedata("update Observationtaken  set ImageSyncStatus='1' where ImageName='" + ImageName + "'");
                        pd.dismiss();
                    }

                } else {
                    //msclass.showMessage(result + "--E");
                    //cx.redirecttoRegisterActivity(result, getActivity()); //If authorization fails redirect to login
                    showUserMsg(result); /*Updated on 9th March 2021*/
                    pd.dismiss();
                }

                pd.dismiss();
                tvUploadStatus.setText("Images Uploaded Successfully..");
                recordshowTravel();
            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
                // msclass.showMessage(e.getMessage().toString());
                pd.dismiss();
            }

        }
    }

    public class UploadDataServernew extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        byte[] objAsBytes;
        String Imagestring1;
        String Imagestring2;
        String ImageName;
        String Funname, Intime;

        public UploadDataServernew(String Funname, byte[] objAsBytes, String Imagestring1, String Imagestring2, String ImageName, String Intime) {

            //this.IssueID=IssueID;
            this.objAsBytes = objAsBytes;
            this.Imagestring1 = Imagestring1;
            this.Imagestring2 = Imagestring2;
            this.ImageName = ImageName;
            this.Funname = Funname;
            this.Intime = Intime;

        }

        protected void onPreExecute() {

            pd = new ProgressDialog(getActivity());
            pd.setTitle("Data Uploading ...");
            pd.setMessage("Please wait.");
            tvUploadStatus.setText("Uploading MDO Travel Data, please wait..");
            // pd.setCancelable(false);
            // pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {

                String encodeImage = Base64.encodeToString(objAsBytes, Base64.DEFAULT);
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();
                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("encodedData", encodeImage);
                //jsonParam.put("encodedData", R.string.no_image2);
                Log.d("VAPT", "MDO_TravelData encodedData:" + encodeImage);
                Log.d("VAPT", "MDO_TravelData Imagestring1:" + Imagestring1);
                Log.d("VAPT", "MDO_TravelData Imagestring2:" + Imagestring2);
                jsonParam.put("input1", Imagestring1);
                jsonParam.put("input2", Imagestring2);


                /*if(Imagestring1.equals("") || Imagestring1.equals(null)){
                    Bitmap tempBMP = BitmapFactory.decodeResource(getResources(),R.drawable.image_not_found);

                   *//* ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    tempBMP.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    Log.d("VAPT", "MDO_TravelData IF :" +encoded);
                    jsonParam.put("input1", encoded);*//*
                    jsonParam.put("input1", R.string.no_image2);
                }
                else{
                    jsonParam.put("input1", Imagestring1);
                }

                if(Imagestring2.equals("") || Imagestring2.equals(null)){
                    Bitmap tempBMP = BitmapFactory.decodeResource(getResources(),R.drawable.image_not_found);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    tempBMP.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    Log.d("VAPT", "MDO_TravelData END :" +encoded);
                    //jsonParam.put("input2", encoded);
                    jsonParam.put("input2", R.string.no_image2);
                }
                else{
                    jsonParam.put("input2", Imagestring2);
                }*/

                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
                jsonboj.put("Table", jsonParam);
                String accessToken = mPref.getString(AppConstant.ACCESS_TOKEN_TAG, "");
                Log.d("VAPT", "MDO_TravelData MDO_Travel URL:" + AppConstant.MDO_TRAVEL_DATA);
                Log.d("VAPT", "MDO_TravelData MDO_Travel Json jsonboj:" + jsonboj);
                String response = HttpUtils.POSTJSON(AppConstant.MDO_TRAVEL_DATA,
                        jsonboj, accessToken);
                Log.d("VAPT", "MDO_TravelData MDO_Travel Response:" + response);
                return response;

            } catch (Exception ex) {
                Log.d("MSG", "DATA: " + ex.getMessage());

            }
            return null;
        }

        protected void onPostExecute(String result) {
            String weatherInfo = "Weather Report  is: \n";
            try {
                cx.redirecttoRegisterActivity(result, getActivity()); //If authorization fails redirect to login
                String resultout = result.trim();
                Log.d("Upload", "onPostExecute: " + resultout);
                pd.dismiss();
                if (resultout.contains("True")) {
                    // msclass.showMessage("Data uploaded successfully.");
                    pd.dismiss();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = new Date();
                    String strdate = dateFormat.format(d);

                    if (Funname.equals("MDO_TravelData")) {
                        databaseHelper1.Updatedata("update mdo_starttravel  set Status='1'");
                        databaseHelper1.Updatedata("update mdo_endtravel  set Status='1'");
                        databaseHelper1.Updatedata("update mdo_addplace  set Status='1'");
                        //   databaseHelper1.Updatedata("delete from mdo_starttravel  where  Status='1' and strftime( '%Y-%m-%d', startdate)<>'" + strdate + "'");
                        //   databaseHelper1.Updatedata("delete from  mdo_endtravel  where  Status='1' and strftime( '%Y-%m-%d', enddate)<>'" + strdate + "'");
                        //   databaseHelper1.Updatedata("delete from  mdo_addplace  where  Status='1' and strftime( '%Y-%m-%d', date)<>'" + strdate + "'");


                        //   pd.dismiss();
                    }
                    recordshowTravel();
                    msclass.showMessage("Data uploaded successfully.");
                    tvUploadStatus.setText("MDO Travel Data Uploaded Successfully..");
                    pd.dismiss();
                } else {
                    //msclass.showMessage(result + "--E");
                    //Log.d("Msg", "MDO_TravelData else part");
                    //msclass.showMessage("Something went wrong, please try again later");
                    showUserMsg(result); /*Updated on 9th March 2021*/
                    pd.dismiss();
                }
                pd.dismiss();

            } catch (Exception e) {
                Log.d("Msg", "MDO_TravelData Error : " + e.getMessage());
                // msclass.showMessage(e.getMessage().toString());
                pd.dismiss();
            }

        }
    }


    public void handleImageSyncResponse(String function, String resultout, String ImageName, String id) {
        Log.d("handleImageSyncResponse","function :"+ function +" resultout:"+resultout+ " ImageName:"+ImageName+ " id:"+id);

        if (function.equals("Observationtaken")) {
            if (resultout.contains("True")) {
                databaseHelper1.Updatedata("update Observationtaken  set ImageSyncStatus='1' where ImageName='" + ImageName + "'");
                recordshow1();
            } else {
                //msclass.showMessage(resultout + "Observationtaken--E");
                showUserMsg(resultout); /*Updated on 9th March 2021*/
            }
        }
        if (function.equals("PLDNotSown")) {
            if (resultout.contains("True")) {
                if (!ImageName.equals("")) {
                    databaseHelper1.Updatedata("update PLDNotSown  set imageSyncStatus='1',rowSyncStatus='1' where imageName='" + ImageName + "'");

                } else {
                    databaseHelper1.Updatedata("update PLDNotSown  set rowSyncStatus='1' where id='" + id + "'");

                }
            } else {
                //msclass.showMessage(resultout + "PLDNotSown--E");
                showUserMsg(resultout); /*Updated on 9th March 2021*/
            }
        }
        if (function.equals("FeedbackTaken")) {
            if (resultout.contains("True")) {
                if (!ImageName.equals("")) {
                    databaseHelper1.Updatedata("update FeedbackTaken  set imageSyncStatus='1',rowSyncStatus='1' where imageName='" + ImageName + "'");

                } else {
                    databaseHelper1.Updatedata("update FeedbackTaken  set rowSyncStatus='1' where id='" + id + "'");

                }

            } else {
                //msclass.showMessage(resultout + "FeedbackTaken--E");
                showUserMsg(resultout); /*Updated on 9th March 2021*/
            }
        }

        Log.d("rohitt", "syncSingleImage: " + resultout);
    }

    public class UploadMultipleImages extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        byte[] objAsBytes;
        String Imagestring1;
        String Imagestring2;
        String ImageName, ImageName2;
        String Funname, Intime;

        public UploadMultipleImages(String Funname, String Imagestring1, String Imagestring2, String ImageName, String ImageName2, String Intime) {

            //this.IssueID=IssueID;
            this.objAsBytes = objAsBytes;
            this.Imagestring1 = Imagestring1;
            this.Imagestring2 = Imagestring2;
            this.ImageName = ImageName;
            this.ImageName2 = ImageName2;
            this.Funname = Funname;
            this.Intime = Intime;

        }


        @Override
        protected String doInBackground(String... urls) {

            // encodeImage = Base64.encodeToString(objAsBytes,Base64.DEFAULT);
            HttpClient httpclient = new DefaultHttpClient();
            StringBuilder builder = new StringBuilder();
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>(2);
            postParameters.add(new BasicNameValuePair("from", "UploadImages"));
            //postParameters.add(new BasicNameValuePair("encodedData", encodeImage));
            postParameters.add(new BasicNameValuePair("input1", Imagestring1));
            postParameters.add(new BasicNameValuePair("input2", Imagestring2));

            //String Urlpath=urls[0];

            String Urlpath = urls[0] + "?ImageName=" + ImageName + "&ImageName2=" + ImageName2;
            Log.d("rohit", "doInBackground: " + Urlpath);
            Log.d("rohit", "doInBackground:params::: " + postParameters);
            HttpPost httppost = new HttpPost(Urlpath);
            httppost.addHeader("Content-type", "application/x-www-form-urlencoded");

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
                httppost.setEntity(formEntity);

                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line).append("\n");
                    }

                }

            } catch (ClientProtocolException e) {
                Log.d("Msg", e.getMessage());
                // msclass.showMessage(e.getMessage().toString());


            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
                // msclass. showMessage(e.getMessage().toString());

            }

            try {
                String resultout = builder.toString().trim();
                Log.d("Observationtaken","Observationtaken : resultout: "+resultout);

                if (resultout.contains("True")) {
                    if (Funname.equals("Observationtaken")) {
                        databaseHelper1.Updatedata("update Observationtaken  set ImageSyncStatus='1' where ImageName='" + ImageName + "'");
                        pd.dismiss();
                    }

                } else {
                    //msclass.showMessage(resultout + "--E");
                    Log.d("Observationtaken","Else Observationtaken : resultout: "+resultout);
                    showUserMsg(resultout); /*Updated on 9th March 2021*/
                }


            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
                // msclass.showMessage(e.getMessage().toString());

            }
            return builder.toString();
        }

   /* public class BreederUploadFeedbackNew extends AsyncTask<String, String, String> {
        String returnstring;
        ArrayList<TrialFeedbackSummaryModel> modelArrayList;

        BreederUploadFeedbackNew(ArrayList<TrialFeedbackSummaryModel> modelArrayList){
            this.modelArrayList = modelArrayList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result="";
            try {
                JSONObject jsonboj = new JSONObject();
                JSONArray jsonParam = new JSONArray();
                int len = modelArrayList.size(),i=0;
                for(i=0; i<len; i++){
                    JSONObject object = new JSONObject();
                    TrialFeedbackSummaryModel model = modelArrayList.get(i);
                    object.put("trialCode",model.getTrialCode());
                    object.put("location",model.getLocation());
                    object.put("village",model.getVillage());
                    object.put("segment",model.getSegment());
                    object.put("dateOfVisit",model.getDateOfVisit());
                    object.put("plotNo",model.getPlotNo());
                    object.put("FBStaffCode",model.getFBStaffCode());
                    object.put("rank",model.getRank());
                    object.put("comment",model.getComment());
                    object.put("trialRating",model.getTrialRating());
                    object.put("isSynced",model.getIsSynced());
                    object.put("isSubmitted",model.getIsSubmitted());
                    jsonParam.put(object);
                }
                jsonboj.put("Table", jsonParam);
            }
            catch (Exception e){
                    Log.d("MSG",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //TODO UPDATE data in db
        }
    }*/
    }

    public class UploadTrialFeedbackToServer extends AsyncTask<Void, Void, String> {

        UploadTrialFeedbackToServer() {
        }

        private String uploadFeedbackDataNew(String trial_feedback_table, String apiUrl) {
            String str = "";
            try {
                ArrayList<TrialFeedbackSummaryModel> modelArrayList = getRecordsForTrialFeedback();
                try {
                    JSONObject jsonboj = new JSONObject();
                    JSONArray jsonParam = new JSONArray();
                    int len = modelArrayList.size(), i = 0;
                    for (i = 0; i < len; i++) {
                        JSONObject object = new JSONObject();
                        TrialFeedbackSummaryModel model = modelArrayList.get(i);
                        object.put("trialCode", model.getTrialCode());
                        object.put("location", model.getLocation());
                        object.put("village", model.getVillage());
                        object.put("segment", model.getSegment());
                        object.put("dateOfVisit", model.getDateOfVisit());
                        object.put("plotNo", model.getPlotNo());
                        object.put("FBStaffCode", model.getFBStaffCode());
                        object.put("rank", model.getRank());
                        object.put("comment", model.getComment());
                        object.put("trialRating", model.getTrialRating());
                        object.put("isSynced", model.getIsSynced());
                        object.put("isSubmitted", model.getIsSubmitted());
                        object.put("dateOfVisitSinglePlot", model.getDateOfVisitSinglePlot());
                        jsonParam.put(object);
                    }
                    jsonboj.put("userCode", userCode);
                    jsonboj.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                    jsonboj.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));

                    jsonboj.put("Table", jsonParam);
                    //Log.d("Survey", "jsonboj = " + jsonboj.toString());
                    Log.d("UploadFeed","UploadTrialFeedbackToServer URL : "+AppConstant.UPLOAD_TRIAL_FEEDBACK_URL);
                    Log.d("UploadFeed","UploadTrialFeedbackToServer JSON OBJ : "+jsonboj);

                    str = HttpUtils.POSTJSON(AppConstant.UPLOAD_TRIAL_FEEDBACK_URL,
                            jsonboj, "");
                    Log.d("Survey", "str = " + str);
                    Log.d("UploadFeed","UploadTrialFeedbackToServer RESPONSE : "+str);
                    return str;
                } catch (Exception e) {
                    Log.d("MSG", e.getMessage());
                }
            } catch (Exception ex) {
                str = "false";
                Log.d("Msg", ex.getMessage());
            }
            return str;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvUploadStatus.setText("Uploading Trial Feedback, please wait..");
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = uploadFeedbackDataNew("trial_feedback_table", "");
            return result;
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            dialog.dismiss();
            Log.d("Survey", "onPostExecute str = " + str);
            Log.d("Survey", "str contains true = " + str.contains("true"));

            if (str.contains("true")) {
                Log.d("Survey", "str contains true inside = " + str.contains("true"));
                /*TODO DB Update*/
                //if (trial_feedback_table.equals("trial_feedback_table")) {
                /*for (int j = 0; j < modelArrayList.size(); j++) {
                    databaseHelper1.Updatedata("update trial_feedback_table set isSyncedStatus='1'");
                }*/
                //}

                try {
                    JSONObject jsnobject = new JSONObject(str);
                    JSONArray jsonArray = jsnobject.getJSONArray("data");
                    for (int j = 0; j < jsonArray.length(); j++) {
                        String trialCode = jsonArray.getString(j);
                        Log.d("Survey", "UPDATE TABLE : " + trialCode);
                        databaseHelper1.Updatedata("update trial_feedback_table set isSyncedStatus='1' WHERE TRIL_CODE='" + trialCode + "'");
                    }
                    tvUploadStatus.setText("Feedback Data Uploaded Successfully..");
                    msclass.showMessage("Data uploaded successfully.");
                    recordshowFeedbackNew();
                } catch (JSONException e) {
                    Log.d("Msg", "Msg:" + e.getMessage());
                }
            } else {
                msclass.showMessage("Something went wrong, please try again later");
                //msclass.showMessage(str + "-E");
            }
        }
    }


    private void showUserMsg(String result) {
        if (result.toLowerCase().contains("Authorization has been denied for this request.")) {
            cx.redirecttoRegisterActivity(result, getActivity()); //If authorization fails redirect to login
        } else {
            msclass.showMessage("Something went wrong, please try again later");
        }
    }


    /*Added on 31st March 2022*/
    private String UploadDataServerPLDNOTSOWN(String funName, byte[] objAsBytes){
        Log.d("PLD_DATA","Inside doInBackground UploadDataServerPLDNOTSOWN");

        try {
            Log.d("PLD_DATA","Inside UploadDataServer PLDNOTSOWN");
            Log.d("PLD_DATA","objAsBytes :"+objAsBytes);

            // encode image to base64 so that it can be picked by saveImage.php file
            String encodeImage = Base64.encodeToString(objAsBytes, Base64.DEFAULT);
            JSONObject jsonboj = new JSONObject();
            JSONObject jsonParam = new JSONObject();
            Prefs mPref = Prefs.with(getActivity());
            String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
            String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
            Log.d("VAPT", "userCodeEncrypt :" + userCodeEncrypt);
            Log.d("VAPT", "userCodeDecrypt : " + userCodeDecrypt);
            jsonParam.put("userCode", userCodeDecrypt);
            jsonParam.put("encodedData", encodeImage);
            jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
            jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
            jsonboj.put("Table", jsonParam);
            Log.d("VAPT", "Upload_UploadDataServer PLDNOTSOWN URL : " + (AppConstant.BASE_URL + AppConstant.PLD_NOT_SOWN_URL));
            Log.d("VAPT", "Upload UploadDataServer PLDNOTSOWN OBJECT : " + jsonboj.toString());
            return HttpUtils.POSTJSON(AppConstant.PLD_NOT_SOWN_URL,
                    jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));

        } catch (Exception ex) {
            Log.d("MSG","PLD_DATA : "+ex.getMessage());
        }
        return null;
    }
    /*public class UploadDataServerPLDNOTSOWN extends AsyncTask<String, String, String> {

        byte[] objAsBytes;
        String Imagestring1;
        String Imagestring2;
        String ImageName;
        String Funname;

        public UploadDataServerPLDNOTSOWN(String Funname, byte[] objAsBytes) {
            Log.d("PLD_DATA","Inside Constructor UploadDataServerPLDNOTSOWN");

            //this.IssueID=IssueID;
            this.objAsBytes = objAsBytes;
            this.Imagestring1 = Imagestring1;
            this.Imagestring2 = Imagestring2;
            this.ImageName = ImageName;
            this.Funname = Funname;

        }

        protected void onPreExecute() {
            Log.d("PLD_DATA","Inside onPreExecute UploadDataServerPLDNOTSOWN");

        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d("PLD_DATA","Inside doInBackground UploadDataServerPLDNOTSOWN");

            try {
                Log.d("PLD_DATA","Inside UploadDataServerPLDNOTSOWN");
                Log.d("PLD_DATA","objAsBytes :"+objAsBytes);

                // encode image to base64 so that it can be picked by saveImage.php file
                String encodeImage = Base64.encodeToString(objAsBytes, Base64.DEFAULT);
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();
                Prefs mPref = Prefs.with(getActivity());
                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                Log.d("VAPT", "userCodeEncrypt :" + userCodeEncrypt);
                Log.d("VAPT", "userCodeDecrypt : " + userCodeDecrypt);
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("encodedData", encodeImage);
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage, ""));
                jsonboj.put("Table", jsonParam);
                Log.d("VAPT", "Upload_UploadDataServerPLDNOTSOWN URL : " + (AppConstant.BASE_URL + AppConstant.PLD_NOT_SOWN_URL));
                Log.d("VAPT", "Upload UploadDataServerPLDNOTSOWN OBJECT : " + jsonParam.toString());
                return HttpUtils.POSTJSON(AppConstant.PLD_NOT_SOWN_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));

            } catch (Exception ex) {
                Log.d("MSG","PLD_DATA : "+ex.getMessage());
            }
            return null;
        }

        protected void onPostExecute(String result) {
            String weatherInfo = "Weather Report  is: \n";
            try {
                cx.redirecttoRegisterActivity(result, getActivity()); //If authorization fails redirect to login

                String resultout = result.trim();
                if (resultout.equals("True")) {
                    // msclass.showMessage("Data uploaded successfully.");
                } else {
                    //msclass.showMessage(result + "error");
                    msclass.showMessage("Something went wrong, please try again later");
                }

                // dialog.dismiss();


            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
                msclass.showMessage("Something went wrong, please try again later");
                dialog.dismiss();
            }

        }
    }*/

}
