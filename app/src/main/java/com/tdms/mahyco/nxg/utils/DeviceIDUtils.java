package com.tdms.mahyco.nxg.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Random;

public class DeviceIDUtils {

    public static String getDeviceIdData(Context context, String userID) {
        String deviceId="";

        try{
            //String firebaseID = FirebaseInstanceId.getInstance().getId();
            String firebaseID = "comnewtrailmahycotrail";
            //String firebaseID = "com.newtrail.mahyco.trail";
            //String firebaseID = "bbbb98765432100";
            String randomCode = readRandomNumber(context);
            if(randomCode.equals("")){
                int code = DeviceIDUtils.genRandomNumber();
                deviceId = firebaseID + userID + code;
                saveRandomNumber(context,(""+code));
                String randomCodeAfterSave = readRandomNumber(context);
                Log.d("VAPT","randomCodeAfterSave  : "+randomCodeAfterSave);
            }
            else{
                deviceId = firebaseID + userID + randomCode;
            }
        }
        catch (Exception e){
            Log.d("Msg",e.getMessage());
        }
        Log.d("VAPT","RETURN DEVICE_ID : "+deviceId);
        return deviceId;

        /*try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                *//*deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);*//*
                String reliableIdentifier = FirebaseInstanceId.getInstance().getId();
                deviceId = reliableIdentifier;
                Log.d("VAPT","android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ");
                Log.d("VAPT", "TDMS 1 DEVICE_ID : " + deviceId);
            } else {
                final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephony.getDeviceId() != null) {
                    deviceId = mTelephony.getDeviceId();
                    Log.d("VAPT", "TDMS 2 DEVICE_ID : " + deviceId);
                } else {
                    deviceId = Settings.Secure.getString(
                            context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    Log.d("VAPT", "TDMS 3 ANDROID_ID : " +deviceId);
                }
            }
        }
        catch (Exception e){
            Log.d("MSG",e.getMessage());
        }*/
    }

    static int genRandomNumber() {
        Random r = new Random( System.currentTimeMillis() );
        int num =  ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        Log.d("VAPT","genRandomNumber Random ID : "+num);
        return num;
    }

    static void saveRandomNumber(Context context, String code){
        SharedPreferences sharedPref = context.getSharedPreferences(AppConstant.PRE_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AppConstant.RANDOM_CODE, code);
        editor.apply();
        Log.d("VAPT","saveRandomNumber : "+code);
    }

    static String readRandomNumber(Context context){
        String code = "";
        SharedPreferences sharedPref = context.getSharedPreferences(AppConstant.PRE_KEY,Context.MODE_PRIVATE);
        code = sharedPref.getString(AppConstant.RANDOM_CODE, "");
        Log.d("VAPT","readRandomNumber : "+code);
        return code;
    }
}
