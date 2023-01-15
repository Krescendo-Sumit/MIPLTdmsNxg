package com.tdms.mahyco.nxg;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CommonExecution {


    Context context;
    String returnstring;
    ProgressDialog dialog;
    Prefs mPref;

    public CommonExecution(Context context) {
        this.context = context;
        dialog = new ProgressDialog(this.context);
        //dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPref = Prefs.with(context);
    }

    public static void setBlinkingTextview(ImageView tv, long milliseconds, long offset) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(milliseconds); //You can manage the blinking time with this parameter
        anim.setStartOffset(offset);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        tv.startAnimation(anim);
    }

    public class BreederMasterData extends AsyncTask<String, String, String> {

        private int action;
        private String username;
        private String password;
        private String imei;


        public BreederMasterData(int action, String username, String password, String imei) {
            this.action = action;
            this.username = username;
            this.password = password;
            this.imei = imei;
        }

        protected void onPreExecute() {
            dialog.setMessage("Loading....");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {
            String response="";
            try {
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", username);
                jsonParam.put("action", "1");//action);
                jsonParam.put("imei", imei);
                /*jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG,""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage,""));*/
                jsonParam.put("password", password); //added later 10th Aug 2020
                jsonboj.put("Table", jsonParam);
                Log.d("VAPT_Request","API::"+AppConstant.BREEDER_VERIFY_USER_URL);
                Log.d("VAPT_Request","JSON OBJECT::"+jsonboj);
                Log.d("VAPT_Request","Params : username:"+username+" action:"+action+" password:"+password+" imei:"+imei);
                Log.d("VAPT_Request","ACCESS_TOKEN_TAG:"+mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                response = HttpUtils.POSTJSON(AppConstant.BREEDER_VERIFY_USER_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));
                Log.d("VAPT_RESPONSE","RESPONSE : "+response);
                return response;
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            redirecttoRegisterActivity(s,context); //If authorization fails redirect to login
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    public class BreederMasterGoogleData extends AsyncTask<String, String, String> {


        private String email;

        public BreederMasterGoogleData(String email) {
            this.email = email;

        }

        protected void onPreExecute() {
            dialog.setMessage("Loading....");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {
            String response="";
            try {
                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();

                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("from", "breederVerifyUserEmail");
                jsonParam.put("userMail", email);
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG,""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage,""));
                jsonboj.put("Table", jsonParam);
                response = HttpUtils.POSTJSON(AppConstant.BREEDER_VERIFY_USER_EMAIL_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));


            } catch (Exception ex) {
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            redirecttoRegisterActivity(s,context); //If authorization fails redirect to login
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    public void bindimage(ImageView img, String Path) {
        try {
            File imgFile = new File(Path);

            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                img.setImageBitmap(myBitmap);

            }
        } catch (Exception ex) {
            Log.d("Msg",ex.getMessage());
        }
    }


    //// TO download Data from year , session, staffcode

    public class BreederDataDownload extends AsyncTask<String, String, String> {

        private int action;
        private String ddlyear;
        private String ddlsession;
        private String usercode;
        private String TFA;


        public BreederDataDownload(int action, String ddlyear, String ddlsession, String usercode, String TFA) {
            this.action = action;
            this.ddlyear = ddlyear;
            this.ddlsession = ddlsession;
            this.usercode = usercode;
            this.TFA = TFA;
        }

        protected void onPreExecute() {
            dialog.setMessage("Loading....");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {
            try {

                JSONObject jsonboj = new JSONObject();
                JSONObject jsonParam = new JSONObject();
                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                jsonParam.put("userCode", userCodeDecrypt);
                jsonParam.put("action", action);
                jsonParam.put("ddlyear", ddlyear);
                jsonParam.put("ddlsession", ddlsession);

                jsonParam.put("TFA", TFA);
                jsonParam.put("access_token", mPref.getString(AppConstant.ACCESS_TOKEN_TAG,""));
                jsonParam.put("finalmessage", mPref.getString(AppConstant.finalmessage,""));

                jsonboj.put("Table", jsonParam);
                return HttpUtils.POSTJSON(AppConstant.INSERT_DOWNLOAD_DATA_URL,
                        jsonboj, mPref.getString(AppConstant.ACCESS_TOKEN_TAG, ""));


            } catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            redirecttoRegisterActivity(s,context); //If authorization fails redirect to login
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }


    public void redirecttoRegisterActivity(String result, final Context context )
    {
        final SharedPreferences.Editor editor;
        SharedPreferences pref;
        pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        if (result.toLowerCase().contains("Authorization has been denied for this request.")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("MyActivity");
            builder.setMessage("Your login session is  expired, please register user again.");
            builder.setCancelable(false);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mPref.save(AppConstant.USER_CODE_PREF,null);
                    mPref.save(AppConstant.USER_PASSWORD_PREF,null);
                    editor.putString("Displayname", null);
                    Intent intent1 = new Intent(context,register_user.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent1);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public class BreederMasterDataIsFeedGiven extends AsyncTask<String, String, String> {

        private int action;
        private String userCode;
        private String packageName;

        public BreederMasterDataIsFeedGiven(int action, String userCode,String packageName) {
            this.action = action;
            this.userCode = userCode;
            this.packageName = packageName;
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpClient httpclient = new DefaultHttpClient();
            StringBuilder builder = new StringBuilder();
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>(2);
            //postParameters.add(new BasicNameValuePair("from", "insertbreederData"));
            //String Urlpath1 = Constants.IS_FEEDBACK_GIVEN + "?UserCode=" + userCode;
            String Urlpath1 = AppConstant.IS_FEEDBACK_NEW + "?UserCode=" + userCode +"&packageName="+packageName;
            Log.d("Is FeedbackGiven","Urlpath1 :"+Urlpath1);

            HttpPost httppost = new HttpPost(Urlpath1);
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
                    returnstring = builder.toString();
                }
            } catch (UnsupportedEncodingException e) {
                Log.d("MSG",e.getMessage());
                returnstring = e.getMessage().toString();
            } catch (ClientProtocolException e) {
                Log.d("MSG",e.getMessage());
                Log.d("MSG",e.getMessage());
            } catch (Exception e) {
                Log.d("MSG",e.getMessage());
                returnstring = e.getMessage().toString();
            }
            Log.d("Is FeedbackGiven","Return String :"+builder.toString());
            return builder.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public class ResetUserLogin extends AsyncTask<String, String, String> {

        private int action;
        private String userCode;

        public ResetUserLogin(int action, String userCode) {
            this.action = action;
            this.userCode = userCode;
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpClient httpclient = new DefaultHttpClient();
            StringBuilder builder = new StringBuilder();
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>(2);
            postParameters.add(new BasicNameValuePair("from", "insertbreederData"));
            String Urlpath1 = AppConstant.TDMS_RESET_USER + "?UserCode=" + userCode;
            Log.d("Is FeedbackGiven","Urlpath1 :"+Urlpath1);

            HttpPost httppost = new HttpPost(Urlpath1);
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
                    returnstring = builder.toString();
                }
            } catch (UnsupportedEncodingException e) {
                Log.d("MSG",e.getMessage());
                returnstring = e.getMessage().toString();
            } catch (ClientProtocolException e) {
                Log.d("MSG",e.getMessage());
                Log.d("MSG",e.getMessage());
            } catch (Exception e) {
                Log.d("MSG",e.getMessage());
                returnstring = e.getMessage().toString();
            }
            Log.d("ResetUserLogin","Return String :"+builder.toString());
            return builder.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}
