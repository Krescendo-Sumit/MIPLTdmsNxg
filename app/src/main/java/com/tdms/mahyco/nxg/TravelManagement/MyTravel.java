package com.tdms.mahyco.nxg.TravelManagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tdms.mahyco.nxg.CommonExecution;
import com.tdms.mahyco.nxg.Config;
import com.tdms.mahyco.nxg.R;
import com.tdms.mahyco.nxg.databaseHelper;
import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.HttpUtils;
import com.tdms.mahyco.nxg.utils.Prefs;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyTravel extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView lblwelcome;
    public Spinner spDist, spTaluka, spVillage, spCropType, spProductName, spMyactvity,spComment;
    private Context context;
    private databaseHelper mDatabase;
    public CommonExecution cx;
    Config config;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    ProgressDialog dialog;
    String usercode,password;
    private int[] tabIcons = {
            R.drawable.start,
            R.drawable.addtravel,
            R.drawable.end
    };
    ImageView backbtn;
    public String TAG="MyTravel";
    Prefs mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_travel);
        getSupportActionBar().hide(); //<< this
        context = this;
        try {
            cx = new CommonExecution(this);
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            preferences = getSharedPreferences("MyPref", 0);
            editor = preferences.edit();
          //  spDist = (Spinner) findViewById(R.id.spDist);
            //spTaluka = (Spinner) findViewById(R.id.spTaluka);
            //spVillage = (Spinner) findViewById(R.id.spVillage);
            mDatabase = new databaseHelper(this);
            config = new Config(this); //Here the context is passing
            //lblwelcome = (TextView) findViewById(R.id.lblwelcome);

            mViewPager = (ViewPager) findViewById(R.id.viewcontainer);
            backbtn = (ImageView) findViewById(R.id.backbtn);
            setupViewPager(mViewPager);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            setupTabIcons();
           // String[] myValues=mDatabase.getUserDetails();



           // userCode= myValues[0];
           // password= myValues[1];
           // Log.d(TAG, "onCreate: "+userCode+"::"+password);

            mPref = Prefs.with(context);

        }
        catch (Exception e) {
            Log.d(TAG, "onLocationChanged: "+e.toString());
            Log.d("Msg",e.getMessage());
            //  }
        }
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        UploadaImage2("mdo_starttravel");
        UploadaImage2("mdo_endtravel");


    }
    private void setupTabIcons() {
        try {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        }
            catch (Exception e) {
            Log.d(TAG, "setupTabIcons: "+e.toString());
            Log.d("Msg",e.getMessage());
            //  }
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new starttravel() , "Start Travel");
        adapter.addFragment(new addtravel() , "Add Places");
        adapter.addFragment(new endtravel() , "End Travel");


        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public  void UploadaImage2(String Functionname)
    {

        try {
            if (config.NetworkConnection()) {

                String str = null;
                String Imagestring1 = "";
                String Imagestring2 = "";
                String ImageName = "";
                String ImageName2 = "tt";
                String searchQuery = "select  *  from "+Functionname+" where imgstatus='0'";
                Cursor cursor = mDatabase.getReadableDatabase().rawQuery(searchQuery, null);
                int count = cursor.getCount();
                if (count > 0) {

                    try {

                        cursor.moveToFirst();
                        while (cursor.isAfterLast() == false)
                        {

                         byte[] objAsBytes = null;
                            JSONObject object = new JSONObject();
                            try {
                                ImageName = cursor.getString(cursor.getColumnIndex("imgname"));
                                Imagestring1 = mDatabase.getImageDatadetail(cursor.getString(cursor.getColumnIndex("imgpath")));

                            } catch (Exception e) {
                                Log.d("Msg",e.getMessage());
                            }
                            try {
                                objAsBytes = object.toString().getBytes("UTF-8");
                            } catch (Exception e) {
                                Toast.makeText(this, "Uplaoding"+e.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("Msg",e.getMessage());
                            }
                            new UploadImageData(Functionname,  Imagestring1, Imagestring2, ImageName, ImageName2,"t").execute("");//cx.MDOurlpath);

                            cursor.moveToNext();
                        }
                        cursor.close();

                    }
                    catch (Exception ex) {
                        Toast.makeText(this, "Uplaoding"+ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {

                }

            } else {


            }

        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Uplaoding"+ex.toString(), Toast.LENGTH_SHORT).show();
            Log.d("Msg",ex.getMessage());

        }

    }
    public class UploadImageData extends AsyncTask<String, String, String> {

        byte[] objAsBytes;
        String Imagestring1;
        String Imagestring2;
        String ImageName,ImageName2;
        String Funname,Intime;
        public UploadImageData(String Funname, String Imagestring1, String Imagestring2, String ImageName, String ImageName2, String Intime) {

            //this.IssueID=IssueID;
            this.objAsBytes=objAsBytes;
            this.Imagestring1 =Imagestring1;
            this.Imagestring2 =Imagestring2;
            this.ImageName=ImageName;
            this.ImageName2=ImageName2;
            this.Funname=Funname;
            this.Intime=Intime;

        }
        protected void onPreExecute() {


        }
        @Override
        protected String doInBackground(String... urls) {
            String response="";
            try {
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();

                String userCodeEncrypt = preferences.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("from", "UploadImages");
                jsonParam.put("input1", Imagestring1);
                jsonParam.put("input1", Imagestring2);
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG,""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage,""));
                jsonboj.put("Table", jsonParam);
                response = HttpUtils.POSTJSON(AppConstant.UPLOAD_IMAGES_URL,
                        jsonboj, preferences.getString(AppConstant.ACCESS_TOKEN_TAG, ""));


            } catch (Exception ex) {
            }
            return response;
        }
        protected void onPostExecute(String result) {
            try{
                cx.redirecttoRegisterActivity(result,context); //If authorization fails redirect to login

                String resultout=result.trim();
                if(resultout.contains("True")) {
                    if(Funname.equals("tagdatauploadMDONew_Testold")) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date d=new Date();
                        String strdate=dateFormat.format(d);
                        mDatabase.deleterecord("delete from TagData where strftime( '%Y-%m-%d', INTime)<>'"+strdate+"' and Status='1' and Img='1' ");
                        mDatabase.Updatedata("update TagData  set Status='1' where Status='0' ");
                     //   mDatabase.Updatedata("update Tempstockdata set status='2' where Status='1'" );

                    }
                    //if(Funname.equals("MDOFarmerMasterdataInsert")) {
                   //     mDatabase.deleterecord("delete from FarmerMaster");

                   // }
                    if(Funname.equals("UploadImages")) {
                        mDatabase.Updatedata("update TagData  set imgstatus='1' where Imgname='"+ImageName+"'");

                    }
                    if(Funname.equals("mdo_starttravel")) {
                        mDatabase.Updatedata("update mdo_starttravel  set imgstatus='1' where imgname='"+ImageName+"'");

                    }
                    if(Funname.equals("mdo_endtravel")) {
                        mDatabase.Updatedata("update mdo_endtravel  set imgstatus='1' where imgname='"+ImageName+"'");

                    }
                }




            }

            catch (Exception e) {
                Log.d("Msg",e.getMessage());


            }

        }
    }

}
