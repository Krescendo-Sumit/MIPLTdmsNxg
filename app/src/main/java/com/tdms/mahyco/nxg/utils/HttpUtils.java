package com.tdms.mahyco.nxg.utils;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//import geotagging.mahyco.UploadData;
//import geotagging.mahyco.UserRegister;
//import geotagging.mahyco.myActivityRecording.preSeasonActivity.CropSeminarActivity;

public class HttpUtils {


    private final static HttpClient mHhttpclient = new DefaultHttpClient();


    /**
     * <P>Method is used to post the json data to server and get the response</P>
     * @param url
     * @param obj
     * @param accesstoken
     * @return
     */
    public static String POSTJSON( String url, JSONObject obj, String accesstoken){
        Log.i("VAPT","JSONPOSTBEGIN"+ "Beginning of JSON POST");
        Log.d("VAPT","JsonObject : "+obj.toString());
        Log.d("VAPT","accesstoken"+accesstoken);
        InputStream inputStream = null;
        String result = "";
        try{
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Authorization", "Bearer " + accesstoken);
           // StringEntity se = new StringEntity(obj.toString().trim());
            //se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            byte[] vart= obj.toString().trim().getBytes("UTF-8");
            String utf=obj.toString().trim();//convertStringToUTF8(obj.toString().trim());
          //  String normal=convertUTF8ToString(utf);
           // String  out=vart.toString()
            StringEntity se = new StringEntity(utf);
            post.setEntity(se);

            //post.setEntity(new UrlEncodedFormEntity(( obj.toString() ),"UTF-8"));


            HttpResponse httpResponse = mHhttpclient.execute(post);
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            //StatusLine statusLine = httpResponse.getStatusLine();
            // convert inputstream to string
            if(inputStream != null){
                Log.d("VAPT", "inputStream :"+inputStream);
                result = convertInputStreamToString(inputStream);
                Log.d("VAPT", "result :"+result);
            }
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            Log.d("InputStream", e.getMessage());
        }
        Log.i("JSONPOSTEND", "End of JSON data post methos...");
        return result;
    }
    // convert UTF-8 to internal Java String format
    public static String convertUTF8ToString(String s) {
        String out = null;
        try {
            //out = new String(s.getBytes("ISO-8859-1"), "UTF-8"); // Working
            out = new String(s.getBytes( "UTF-8"));
        } catch (Exception e) {
            return null;
        }
        return out;
    }

    // convert internal Java String format to UTF-8
    public static String convertStringToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (Exception e) {
            return null;
        }
        return out;
    }

    public static String POSTJSONcontext(Context contex, String url, JSONObject obj, String accesstoken){
        Log.i("JSONPOSTBEGIN", "Beginning of JSON POST");
        Log.d("JsonObject",obj.toString());
        InputStream inputStream = null;
        final Context contex1;
        contex1=contex;
        String result = "";
        try{
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("Authorization", "Bearer " + accesstoken);
           // post.setHeader("Authorization", "Bearer " + "");
           // StringEntity se = new StringEntity(obj.toString());
           // se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            //post.setEntity(se);
            String utf=convertStringToUTF8(obj.toString().trim());
            //  String normal=convertUTF8ToString(utf);
            // String  out=vart.toString()
            StringEntity se = new StringEntity(utf);
            post.setEntity(se);

            HttpResponse httpResponse = mHhttpclient.execute(post);
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if(inputStream != null){
                result = convertInputStreamToString(inputStream);
            }
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        Log.i("JSONPOSTEND", "End of JSON data post methos...");
        return result;
    }



    public static String POSTData(String function, String urls, int action,
                                  JSONObject jsonObject) {

        // String encodeData = Base64.encodeToString(objAsBytes, Base64.DEFAULT);
        HttpClient httpclient = new DefaultHttpClient();
        StringBuilder builder = new StringBuilder();
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>(2);
        postParameters.add(new BasicNameValuePair("Type", function));
        postParameters.add(new BasicNameValuePair("InRequest", jsonObject.toString()));
        String Urlpath = urls + "?action=" + action ;
        Log.d("url", "image" + Urlpath);
        Log.d("parameters", "params " + postParameters);
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

        } catch (Exception e) {


        }
        return builder.toString();
    }

    public static String POSTDatabyte(String function, String urls, int action,
                                  JSONObject jsonObject) {
        StringBuilder builder = new StringBuilder();
        try
        {
            byte[] objAsBytes = jsonObject.toString().getBytes("UTF-8");
        String encodeData = Base64.encodeToString(objAsBytes, Base64.DEFAULT);
        HttpClient httpclient = new DefaultHttpClient();

        List<NameValuePair> postParameters = new ArrayList<NameValuePair>(2);
        postParameters.add(new BasicNameValuePair("Type", function));
        postParameters.add(new BasicNameValuePair("InRequest", encodeData));
        String Urlpath = urls + "?action=" + action ;
        Log.d("url", "image" + Urlpath);
        Log.d("parameters", "params " + postParameters);
        HttpPost httppost = new HttpPost(Urlpath);
        httppost.addHeader("Content-type", "application/x-www-form-urlencoded");


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

        } catch (Exception e) {


        }
        return builder.toString();
    }


    public static String POST(String url, List<NameValuePair> mParams){
        InputStream inputStream = null;
        String result = "";
        try{

            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(( mParams ),"UTF-8"));
            HttpResponse httpResponse = mHhttpclient.execute(post);
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if(inputStream != null){
                result = convertInputStreamToString(inputStream);
            }
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * <P>Method to convert inpuyt stream to string</P>
     * @param inputStream
     * @return
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(inputStream))) {

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            //print result
            System.out.println(response.toString());

            return  response.toString();
        }
    }

    /*Added on 26th Aug 2021*/
    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try{
            HttpGet get = new HttpGet(url);
            HttpResponse httpResponse = mHhttpclient.execute(get);
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if(inputStream != null){
                result = convertInputStreamToString(inputStream);
            }
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }


}
