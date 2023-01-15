package com.tdms.mahyco.nxg;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MainApplication extends Application {

    public static int LOGIN_ATTEMPT = 0;


    public static String getVersionName(Context context){
        String versionName="";
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        }
        catch (Exception e){
            Log.d("TDM","MSG : "+e.getMessage());
        }return versionName;
    }
}
