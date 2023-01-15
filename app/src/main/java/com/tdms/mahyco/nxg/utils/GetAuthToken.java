package com.tdms.mahyco.nxg.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class GetAuthToken extends AsyncTask<String, String, String>
{
    private String username;
    private String password;
    private String imeiNumber;

    public GetAuthToken(String username ,String password,String imeiNumber){
        this.username = username;
        this.password = password;
        this.imeiNumber = imeiNumber;
    }

    @Override
    protected String doInBackground(String... strings) {
        /*try {
            password = URLDecoder.decode(password, "UTF-8");
        } catch (UnsupportedEncodingException e){
        }*/
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>(2);
        postParameters.add(new BasicNameValuePair("grant_type", "password"));
        postParameters.add(new BasicNameValuePair("username", username));
        postParameters.add(new BasicNameValuePair("password",password));
        postParameters.add(new BasicNameValuePair("imei",imeiNumber));
        postParameters.add(new BasicNameValuePair("action","1"));
        return HttpUtils.POST(AppConstant.TOKEN_URL,postParameters);
    }

    protected void onPostExecute(String result) {

        Log.d("VAPT","Auth token result: "+result);
    }

}


