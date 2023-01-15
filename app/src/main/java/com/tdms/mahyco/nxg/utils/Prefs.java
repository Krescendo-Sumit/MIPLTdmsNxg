package com.tdms.mahyco.nxg.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Prefs {

        private static final String TAG = "geotagging.mahyco.app.Prefs";

        static Prefs singleton = null;

        static SharedPreferences preferences;

        static SharedPreferences.Editor editor;


        @SuppressLint("CommitPrefEdits")
        public Prefs(Context context) {
            preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }

        public static Prefs with(Context context) {
            if (singleton == null) {
                singleton = new Builder(context).build();
            }
            return singleton;
        }


        /**
         * <P>Method to save the string values</P>
         * @param key
         * @param value
         */
        public void save(String key, String value) {
            editor.putString(key, value).apply();
        }


        /**
         * <P>Method to get the saved string values</P>
         * @param key
         * @param defValue
         * @return
         */
        public String getString(String key, String defValue) {

            try {
                return preferences.getString(key, defValue);
            }catch (Exception exc){
                Log.d("Data",exc.getMessage());
                return "";
            }
        }

    /**
     * <P>Method to save the string values</P>
     * @param key
     * @param value
     */
    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }


    /**
     * <P>Method to get the saved string values</P>
     * @param key
     * @param defValue
     * @return
     */
    public boolean getBoolean(String key, boolean defValue) {

        try {
            return preferences.getBoolean(key, defValue);
        }catch (Exception exc){
            Log.d("Data",exc.getMessage());
            return false;
        }
    }

    /**
     * <P>Method to save the string values</P>
     * @param key
     * @param value
     */
    public void saveInt(String key, int value) {
        editor.putInt(key, value).apply();
    }


    /**
     * <P>Method to get the saved string values</P>
     * @param key
     * @param defValue
     * @return
     */
    public int getInt(String key, int defValue) {

        try {
            return preferences.getInt(key, defValue);
        }catch (Exception exc){
            Log.d("Data",exc.getMessage());
            return 0;
        }
    }


        public void removeAll() {
            editor.clear();
            editor.apply();
        }


        private static class Builder {

            private final Context context;

            public Builder(Context context) {
                if (context == null) {
                    throw new IllegalArgumentException("Context must not be null.");
                }
                this.context = context.getApplicationContext();
            }

            /**
             * <p>Method that creates an instance of geotagging.mahyco.app.Prefs</p>
             *
             * @return an instance of cgeotagging.mahyco.app.Prefs
             */
            public Prefs build() {
                return new Prefs(context);
            }
        }
}

