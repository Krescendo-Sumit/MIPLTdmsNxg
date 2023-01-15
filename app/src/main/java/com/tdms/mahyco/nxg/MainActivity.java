package com.tdms.mahyco.nxg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.Prefs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public android.widget.ProgressBar mprogresss;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    databaseHelper databaseHelper1;
    TextView txtVersionCode;
    Prefs mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setTitle("Mahyco TDMS");
//        getSupportActionBar().setSubtitle("Trial Data Management System");
        databaseHelper1 = new databaseHelper(this);
        txtVersionCode = (TextView) findViewById(R.id.tv_version_code);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            txtVersionCode.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("MSG", e.getMessage());
        }
        mprogresss = (ProgressBar) findViewById(R.id.progressBar);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        new Thread(new Runnable() {
            @Override
            public void run() {
                dowork();
                startapp();
                finish();
            }
        }).start();
        mPref = Prefs.with(this);
    }

    private void dowork() {

        for (int progress = 0; progress < 100; progress += 10) {
            try {
                Thread.sleep(300);//300
                mprogresss.setProgress(progress);
            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
            }

        }
    }

    private void startapp() {
        String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, null);
        String accessTockenExpiry = mPref.getString(AppConstant.ACCESS_TOKEN_EXPIRY, null);
        Log.d("Launch home", "HOME__________________________" + userCodeEncrypt);
        Log.d("Launch home", "HOME Access Expiry date__________________________" + accessTockenExpiry);
        /*mPref.save(AppConstant.ACCESS_TOKEN_EXPIRY,"01-01-2021 17:43:46");
        Log.d("Launch home", "HOME Access After change Expiry date__________________________" + mPref.getString(AppConstant.ACCESS_TOKEN_EXPIRY, null));*/
        //28-12-2020 17:43:46
        SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy HH:mm:ss"); /*TODO LIVE FORMAT*/ /*RCBU Test format date for Session*/
       // SimpleDateFormat format = new SimpleDateFormat("mm/dd/yyyy HH:mm:ss"); /*TODO TEST FORMAT OLD*/
        Date today = new Date();
        Date expireAccess = null;
        if (accessTockenExpiry != null) {
            try {
                expireAccess = format.parse(accessTockenExpiry);
            } catch (ParseException e) {
                Log.d("Msg", "DateParse:" + e.getMessage());
            }
        }
        if (accessTockenExpiry != null && today.before(expireAccess)) {
            Log.d("Launch home", "HOME__________________________LOGIN as TOKEN EXPIRED__________________________today.before(expireAccess):" + today.before(expireAccess));
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
        } else if (userCodeEncrypt != null) {
            String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
            Log.d("Launch home", "HOME__________________________ LOGIN as Already Logged In");
            Log.d("Launch home", "HOME__________________________" + userCodeDecrypt);
            if (userCodeDecrypt != null) {
                Log.d("Launch home", "HOME__________________________");
                Intent intent = new Intent(this, home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            Log.d("Launch home", "LOGIN__________________________");
            Log.d("Launch home", "HOME__________________________LOGIN as Fresh login");
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
        }

        /*if (pref.getString("UserID", null) != null||pref.getString("USER_EMAIL", null) != null) {
            Intent intent = new Intent(this, home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
        }*/


//        if (pref.getBoolean("loginState",false)) {
//            Intent intent = new Intent(this, home.class);
//            startActivity(intent);
//        }else {
//            Intent intent = new Intent(this, login.class);
//            startActivity(intent);
//        }
    }


    String getUserID() {
        databaseHelper databaseHelper1 = new databaseHelper(this);
        Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount() == 0) {

        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    //String userCode = data.getString((data.getColumnIndex("user_code")));
                    String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                    String userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("userCode", "userCode" + userCode);
                    String userRole = data.getString((data.getColumnIndex("USER_ROLE")));
                    Log.d("Remark", "USER_ROLE : " + userRole);
                    return userCode;
                } while (data.moveToNext());

            }
            data.close();
        }
        return "";
    }

}
