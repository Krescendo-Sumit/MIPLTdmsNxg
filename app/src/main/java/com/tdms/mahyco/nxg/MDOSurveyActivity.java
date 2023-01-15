package com.tdms.mahyco.nxg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.WebViewClientImpl;

public class MDOSurveyActivity extends AppCompatActivity {

    //Prefs mPref;

    //Prefs mPref;
    public Messageclass msclass;
    public CommonExecution cx;
    //private SqliteDatabase mDatabase;
    ProgressBar progressBar;
    RelativeLayout relPRogress;
    ConstraintLayout container;
    private long mLastClickTime = 0;
    private Handler handler = new Handler();
    private Context context;
    String urlWebView = "https://das.mahyco.com?EmpCode=97190469";
    Config config;
    WebView webview ;
   // String usercode;
    /*SharedPreferences pref;
    SharedPreferences.Editor editor;*/
   databaseHelper databaseHelper1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_d_o_survey);
        getSupportActionBar().hide();
        initUI();
    }
  /*  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webview.canGoBack()) {
            this.webview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }*/
    /**
     * Method to initialize the elements
     */
    private void initUI() {
        context = this;
        cx = new CommonExecution(this);
        /*mPref = Prefs.with(this);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();*/
        msclass = new Messageclass(this);
        //mDatabase = new SqliteDatabase(this);
        config = new Config(this); //Here the context is passing
        progressBar = (ProgressBar) findViewById(R.id.myProgress);
        relPRogress = (RelativeLayout) findViewById(R.id.relPRogress);
        // usercode= mPref.getString(AppConstant.USER_CODE_TAG, "");
        container =  findViewById(R.id.container);
        webview =  findViewById(R.id.webView);
        //usercode= pref.getString("UserID", null);
        String usercode= getUserID();
        Log.d("TDMS","MDO_Survey:: UserID :: "+usercode);
        //String userCodeDecrypt = EncryptDecryptManager.decryptStringData(usercode);
        //Log.d("TDMS","MDO_Survey:: Decrypted UserID :: "+userCodeDecrypt);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // disable Web SQL
        webview.setWebChromeClient(new WebChromeClient());

        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        webview.setWebViewClient(webViewClient);
        //webview.loadUrl(urlWebView+ "userCode=" + usercode);
        webview.loadUrl("https://besurvey.mahyco.com/Login/IndexMobile?tfacode="+usercode);
    }

    String getUserID(){
        databaseHelper1 = new databaseHelper(this);
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
                    return userCode;
                } while (data.moveToNext());

            }
            data.close();
        }
        return "";
    }
}
