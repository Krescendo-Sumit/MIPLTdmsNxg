package com.tdms.mahyco.nxg;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;

import com.tdms.mahyco.nxg.model.FeedbackReportModel;
import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class databaseHelper extends SQLiteOpenHelper {
    private static final String COLUMN_ID = "_id";
    private static final int SQLITE_DB_VERSION = 38; //Updated on 24 August 2021
    public static final String DATABASE_FILE_PATH = "/sdcard";
    private static final String DATABASE_NAME = "BREEDERAPP.db";
    private static final String Table1 = "TrailCodeData";
    private static final String Table2 = "UserMaster";
    private static final String FarmerInfo = "FarmerInfodata";
    private static final String VillageMaster = "VillageMaster";
    private static final String ObservationMaster = "ObservationMaster";
    private static final String DownloadedObservation = "DownloadedObservation";
    private static final String TrialWiseDownloadedObservation = "TrialWiseDownloadedObservation";
    private static final String Observationtaken = "Observationtaken";
    private static final String FeedbackTaken = "FeedbackTaken";//28032019
    private static final String tagdata = "tagdata";
    private static final String tagdata1 = "tagdata1";
    private static final String PLD_NOT_SOWN = "PLDNotSown";
    private static final String IMEINo = "IMEINo";
    private static final String TABLE_START_TRAVEL = "mdo_starttravel";
    private static final String TABLE_ADD_PLACE = "mdo_addplace";
    private static final String TABLE_END_TRAVEL = "mdo_endtravel";
    private static final String TRIAL_FEEDBACK_TABLE = "trial_feedback_table"; //Added by Rajshri 14 Oct 2020
    private static final String YEAR_TABLE = "year_table"; //Added by Rajshri 26th Aug 2021
    private static final String SEASON_TABLE = "season_table"; //Added by Rajshri 26th Aug 2021

    private static databaseHelper sInstance;

    public static synchronized databaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new databaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public databaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SQLITE_DB_VERSION);
        // SQLiteDatabase.openOrCreateDatabase("/sdcard/"+DATABASE_NAME,null);
        //SQLiteDatabase db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Table1 + "(T_YEAR TEXT,T_SESION TEXT,TRIL_CODE TEXT,Trail_Type TEXT,CROP TEXT,ZONE TEXT,STATE TEXT ,DISTRICT TEXT,TEHSIL TEXT,ENTRY TEXT,REPLECATION TEXT,SPDM2 TEXT,NoRows TEXT,RowLength TEXT,RRSpecing TEXT,PPSpacing TEXT,StartPlotNo TEXT,EndPlotNo TEXT, Location TEXT,Tagged TEXT,PlotSize TEXT,nursery TEXT,segment text,Product text,SPDM1 TEXT)");
        db.execSQL("CREATE TABLE " + Table2 + "(user_code TEXT,IMEI_No TEXT,User_pwd TEXT,USER_ROLE TEXT,Breeder_name TEXT,USER_EMAIL TEXT)");
        /*Edited on 17 Feb 2021*/
        db.execSQL("CREATE TABLE " + FarmerInfo + "(Fname TEXT,Fvillage TEXT,Fmobile TEXT,FstartNote TEXT,Fsowingdate TEXT,TRIL_CODE TEXT, FArea TEXT,flag TEXT,usercode text,GeoLocation text,nurseryDate text,TFName text,TFVillage text, TFGeoLocation text, TFMobile text, TFStartNote text, TFTransplantDate text)");
        db.execSQL("CREATE TABLE " + tagdata + "(userCode TEXT,coordinates TEXT,address TEXT,entrydate TEXT,flag text,TRIL_CODE TEXT,Uplaod Text, tagType Text)");
        db.execSQL("CREATE TABLE " + tagdata1 + "(userCode TEXT,coordinates TEXT,address TEXT,entrydate TEXT,flag text,TRIL_CODE TEXT,Upload Text ,tagType Text)");
        //db.execSQL("CREATE TABLE "+VillageMaster+"(state_code TEXT,State TEXT,district TEXT,dist_code TEXT,taluka text,taluka_code TEXT,village TEXT,village_code text )");


        db.execSQL("CREATE TABLE " + ObservationMaster + "(Crop TEXT,VariableID TEXT,Name TEXT,S_M TEXT,Discription TEXT,Abbreviation TEXT,O_Group TEXT,Variable_type TEXT,Scale TEXT,Scale_type TEXT,Value1 TEXT,Value2 TEXT,Value3 TEXT,Value4 TEXT,Value5 TEXT,Crop_Stage TEXT,OYT TEXT,ST TEXT,MLT TEXT,PET TEXT,Demo TEXT,flag text )");
        db.execSQL("CREATE TABLE " + DownloadedObservation + "(Crop TEXT,VariableID TEXT,Name TEXT,S_M TEXT,Discription TEXT,Abbreviation TEXT,O_Group TEXT,Variable_type TEXT,Scale TEXT,Scale_type TEXT,Value1 TEXT,Value2 TEXT,Value3 TEXT,Value4 TEXT,Value5 TEXT,flag text )");
        db.execSQL("CREATE TABLE " + TrialWiseDownloadedObservation + "(TRIAL_CODE TEXT,Crop TEXT,VariableID TEXT,Name TEXT,S_M TEXT,Discription TEXT,Abbreviation TEXT,O_Group TEXT,Variable_type TEXT,Scale TEXT,Scale_type TEXT,Value1 TEXT,Value2 TEXT,Value3 TEXT,Value4 TEXT,Value5 TEXT,flag text )");
        /*Updated on 12 Aug 2021 for Cotton Special scale_type sending field named as  ValueSPM TEXT */
        db.execSQL("CREATE TABLE " + Observationtaken + "(VariableID TEXT,TRIAL_CODE TEXT,PlotNo TEXT,Value1 TEXT,Value2 TEXT,Value3 TEXT,Value4 TEXT,Value5 TEXT,Date TEXT,Image TEXT,ImageName TEXT,ImageSyncStatus TEXT,flag text,usercode text,cordinate text , ValueSPM TEXT)");
        db.execSQL("CREATE TABLE " + FeedbackTaken + "(Date TEXT,TRIAL_CODE TEXT,PlotNo TEXT,Ranking TEXT,isSyncedStatus TEXT,Rating TEXT not null,Remarks TEXT,isSubmitted TEXT)");//28032019

        //Added by Rajshri 15 Oct 2020 //Updated on 6th May 2021
        db.execSQL("CREATE TABLE " + TRIAL_FEEDBACK_TABLE + "(T_YEAR TEXT,T_SESION TEXT,Crop TEXT,Trail_Type TEXT,TRIL_CODE TEXT,Location TEXT,segment TEXT,PlotNo TEXT,FBMID TEXT,FBStaffCode TEXT,Village TEXT,CurrentStage TEXT,DAS TEXT,Rating TEXT,Remarks TEXT,isSyncedStatus TEXT,isSubmitted TEXT,TrialRating TEXT,dateOfRating TEXT,dateOfVisitSinglePlot TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + PLD_NOT_SOWN + "(id  INTEGER PRIMARY KEY AUTOINCREMENT,userCode text,trialCode TEXT,status TEXT,remark TEXT,date TEXT,imageName TEXT,imagePath TEXT,imageSyncStatus TEXT,rowSyncStatus TEXT )");
        String CREATE_START_TRAVEL = "CREATE    TABLE " + TABLE_START_TRAVEL + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,mdocode TEXT,coordinate TEXT,startaddress TEXT ,startdate TEXT," +
                "dist TEXT,taluka TEXT," +
                "village TEXT,imgname TEXT,imgpath TEXT,Status TEXT,txtkm TEXT,place TEXT,imgstatus TEXT,vehicletype TEXT)";
        db.execSQL(CREATE_START_TRAVEL);
        String CREATE_TABLE_ADD_PLACE = "CREATE    TABLE " + TABLE_ADD_PLACE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,mdocode TEXT,place TEXT,coordinate TEXT,startaddress TEXT,date TEXT,imgname TEXT,imgpath TEXT,Status TEXT)";
        db.execSQL(CREATE_TABLE_ADD_PLACE);
        String CREATE_TABLE_END_TRAVEL = "CREATE    TABLE " + TABLE_END_TRAVEL + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,mdocode TEXT,coordinate TEXT,startaddress TEXT ,enddate TEXT," +
                "dist TEXT,taluka TEXT," +
                "village TEXT,imgname TEXT,imgpath TEXT,Status TEXT,txtkm TEXT,place TEXT,imgstatus TEXT,vehicletype TEXT)";
        db.execSQL(CREATE_TABLE_END_TRAVEL);


        /*Change START Added in 26th Aug 2021*/
        String CREATE_TABLE_YEAR = "CREATE TABLE " + YEAR_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,name TEXT,flag TEXT)";
        db.execSQL(CREATE_TABLE_YEAR);
        String CREATE_TABLE_SEASON = "CREATE TABLE " + SEASON_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,name TEXT,flag TEXT)";
        db.execSQL(CREATE_TABLE_SEASON);
        /*Change END*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table1);
        db.execSQL("DROP TABLE IF EXISTS " + Table2);
        db.execSQL("DROP TABLE IF EXISTS " + FarmerInfo);
        db.execSQL("DROP TABLE IF EXISTS " + tagdata);
        db.execSQL("DROP TABLE IF EXISTS " + tagdata1);
        db.execSQL("DROP TABLE IF EXISTS " + VillageMaster);
        db.execSQL("DROP TABLE IF EXISTS " + ObservationMaster);
        db.execSQL("DROP TABLE IF EXISTS " + DownloadedObservation);
        db.execSQL("DROP TABLE IF EXISTS " + Observationtaken);
        db.execSQL("DROP TABLE IF EXISTS " + FeedbackTaken);//28032019

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_START_TRAVEL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADD_PLACE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_END_TRAVEL);
        db.execSQL("DROP TABLE IF EXISTS " + PLD_NOT_SOWN);

        /*23 April 2021 Added as missed in last updates*/
        db.execSQL("DROP TABLE IF EXISTS " + TRIAL_FEEDBACK_TABLE);

        /*14 July 2021 Added for Observation task*/
        db.execSQL("DROP TABLE IF EXISTS " + TrialWiseDownloadedObservation);

        /*26 Aug 2021 for SRS task*/
        db.execSQL("DROP TABLE IF EXISTS " + YEAR_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SEASON_TABLE);

        onCreate(db);
    }

    public boolean isTableExists(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        boolean isExist = false;
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        db.close();
        return isExist;
    }

    public void deleteTable(String TABLE_NAME) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public void deleledata(String TABLE_NAME, String Where) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from  " + TABLE_NAME + "  " + Where);
        db.close();
    }

    public void deleledata1(String TABLE_NAME, String Where) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from  " + TABLE_NAME + "  " + Where);
        //db.execSQL("delete from "+ TABLE_NAME +"  "+Where);
        db.close();
    }

    public boolean InsertUserRegistration(String UserCode, String DisplayName, String User_pwd, String User_Role, String IMEINo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("User_pwd", User_pwd);
        contentValues.put("user_code", UserCode);
        contentValues.put("USER_ROLE", User_Role);

        contentValues.put("IMEI_No", IMEINo);
        contentValues.put("Breeder_name", DisplayName);
        db.insert("UserMaster", null, contentValues);
        db.close();
        return true;
    }

    public boolean InsertUserRegistrationNew(String DisplayName, String User_Role, String User_pwd, String UserCode) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("IMEI_No", IMEINo);
        contentValues.put("Breeder_name", DisplayName);
        contentValues.put("USER_ROLE", User_Role);
        String encryptedUserCode = EncryptDecryptManager.encryptStringData(UserCode);
        String encryptedPassword = EncryptDecryptManager.encryptStringData(User_pwd);
        contentValues.put("user_code", encryptedUserCode);
        contentValues.put("User_pwd", encryptedPassword);
        Log.d("VAPT", "InsertUserRegistrationNew : " + contentValues.toString());
        db.insert("UserMaster", null, contentValues);
        db.close();
        return true;
    }

    public boolean InsertUserRegistrationGoogle(String UserCode, String DisplayName, String User_pwd, String User_Role, String IMEINo, String usermail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String encryptedUserCode = EncryptDecryptManager.encryptStringData(UserCode);
        String encryptedPassword = EncryptDecryptManager.encryptStringData(User_pwd);
        contentValues.put("user_code", encryptedUserCode);
        contentValues.put("User_pwd", encryptedPassword);
        contentValues.put("USER_ROLE", User_Role);
        contentValues.put("IMEI_No", IMEINo);
        contentValues.put("Breeder_name", DisplayName);
        contentValues.put("USER_EMAIL", usermail);
        Log.d("VAPT", "InsertUserRegistrationGoogle : " + contentValues.toString());
        db.insert("UserMaster", null, contentValues);
        db.close();
        return true;
    }

    public boolean InsertDownloadData(String T_YEAR, String T_SESION, String TRIL_CODE, String Trail_Type, String CROP, String ZONE, String STATE, String DISTRICT, String TEHSIL, String ENTRY, String REPLECATION, String SPDM2, String NoRows, String RowLength, String RRSpecing, String PPSpacing, String StartPlotNo, String EndPlotNo, String Location, String PlotSize, String nursery, String segment, String Product, String SPDM1) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("T_YEAR", T_YEAR);
        contentValues.put("T_SESION", T_SESION);
        contentValues.put("TRIL_CODE", TRIL_CODE);
        contentValues.put("Trail_Type", Trail_Type);
        contentValues.put("CROP", CROP);
        contentValues.put("ZONE", ZONE);
        contentValues.put("STATE", STATE);
        contentValues.put("DISTRICT", DISTRICT);
        contentValues.put("TEHSIL", TEHSIL);
        contentValues.put("ENTRY", ENTRY);
        contentValues.put("REPLECATION", REPLECATION);
        contentValues.put("SPDM2", SPDM2);
        contentValues.put("NoRows", NoRows);
        contentValues.put("RowLength", RowLength);
        contentValues.put("RRSpecing", RRSpecing);
        contentValues.put("PPSpacing", PPSpacing);
        contentValues.put("StartPlotNo", StartPlotNo);
        contentValues.put("EndPlotNo", EndPlotNo);
        contentValues.put("Location", Location);
        contentValues.put("Tagged", " ");
        contentValues.put("PlotSize", PlotSize);
        contentValues.put("nursery", nursery);

        contentValues.put("segment", segment);
        contentValues.put("Product", Product);
        contentValues.put("SPDM1", SPDM1);

        db.insert("TrailCodeData", null, contentValues);
        db.close();
        return true;
    }

    public boolean InsertObservation(String Crop, String VariableID, String Name, String S_M, String Discription, String Abbreviation, String O_Group, String Variable_type, String Scale, String Scale_type, String Value1, String Value2, String Value3, String Value4, String Value5, String cropStage, String oyt, String st, String mlt, String pet, String demo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Crop", Crop);
        contentValues.put("VariableID", VariableID);
        contentValues.put("Name", Name);
        contentValues.put("S_M", S_M);
        contentValues.put("Discription", Discription);
        contentValues.put("Abbreviation", Abbreviation);
        contentValues.put("O_Group", O_Group);
        contentValues.put("Variable_type", Variable_type);
        contentValues.put("Scale", Scale);
        contentValues.put("Scale_type", Scale_type);
        contentValues.put("Value1", Value1);
        contentValues.put("Value2", Value2);
        contentValues.put("Value3", Value3);
        contentValues.put("Value4", Value4);
        contentValues.put("Value5", Value5);
        contentValues.put("Crop_Stage", cropStage);
        contentValues.put("OYT", oyt);
        contentValues.put("ST", st);
        contentValues.put("MLT", mlt);
        contentValues.put("PET", pet);
        contentValues.put("Demo", demo);
        contentValues.put("flag", 0);
        db.insert("ObservationMaster", null, contentValues);
        db.close();
        return true;

    }


    //------------------//28/03/2019--------------------------//
    public boolean insertFeedback(String date, String Trial_Code, String Plot_No, String Ranking, String Is_Synced, String Rating, String Remarks, String Is_Submitted) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DATE", date);
        contentValues.put("TRIAL_CODE", Trial_Code);
        contentValues.put("PlotNo", Plot_No);
        contentValues.put("Ranking", Ranking);
        contentValues.put("isSyncedStatus", Is_Synced);
        contentValues.put("Rating", Rating);
        contentValues.put("Remarks", Remarks);
        contentValues.put("isSubmitted", Is_Submitted);
        db.insert("FeedbackTaken", null, contentValues);
        db.close();
        return true;
    }

    ///---------//28032019//-------------------------//
    public void updateFeedback(String date, String Trial_Code, String Plot_No, String Ranking, String Is_Synced, String Rating, String Remarks, String Is_Submitted) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DATE", date);
        contentValues.put("isSyncedStatus", Is_Synced);
        contentValues.put("Ranking", Ranking);
        contentValues.put("Rating", Rating);
        contentValues.put("Remarks", Remarks);
        contentValues.put("isSubmitted", Is_Submitted);
        db.update(FeedbackTaken, contentValues, "TRIAL_CODE='" + Trial_Code + "' AND " + "PlotNo='" + Plot_No + "'", null);
        db.close();
    }

    public void updateFeedbackAtLast(String date, String Trial_Code, String Is_Submitted) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("isSubmitted", Is_Submitted);
        db.update(FeedbackTaken, contentValues, "TRIAL_CODE='" + Trial_Code + "' AND " + "DATE='" + date + "'", null);
        db.close();
    }

    public long getFeedbackCount(String date, String Trial_Code, String Plot_No) {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, FeedbackTaken, " DATE='" + date + "' and " + "TRIAL_CODE='" + Trial_Code + "' AND " + "PlotNo='" + Plot_No + "'", null);
        db.close();
        return count;
    }

    public long getFeedbackCountBack(String date, String Trial_Code, String Is_Submitted) { /*Updated on 26th May*/
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "select * from trial_feedback_table where isSyncedStatus='0' and isSubmitted='1' and TRIL_CODE='" + Trial_Code + "'";
        Cursor cursor = db.rawQuery(searchQuery, null);
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        db.close();
        Log.d(databaseHelper.class.getName(), "getFeedbackCountBack : " + count);
        return count;
    }

    public boolean isSubStageAlreadySelected(String trialCode, String variableId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM TrialWiseDownloadedObservation where TRIAL_CODE='" + trialCode + "' AND VariableID='" + variableId + "'";
        Cursor data = db.rawQuery(query, null);
        Log.d("TrialData", "isSubStageAlreadySelected Cursor length :  " + data.getCount());

        Log.d("TrialData", "isSubStageAlreadySelected DATA query: " + query);
        if (data.getCount() == 0) {
            Log.d("TrialData", "isSubStageAlreadySelected NO Records");
            return false;
        } else {
            Log.d("TrialData", "isSubStageAlreadySelected Record Already exist");
            return true;
        }
    }


    public void getTrialWiseDownloadedObservation() { /*Updated on 15th July 2021*/
        SQLiteDatabase db = getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM TrialWiseDownloadedObservation", null);
        Log.d("TrialData", "DATA : " + data.toString());
        if (data.getCount() == 0) {
            Log.d("TrialData", "NO Records");
        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    String TRIAL_CODE = data.getString((data.getColumnIndex("TRIAL_CODE")));
                    Log.d("TRIAL_CODE", "TRIAL_CODE:" + TRIAL_CODE);

                    String Crop = data.getString((data.getColumnIndex("Crop")));
                    Log.d("Crop", "Crop:" + Crop);

                    String VariableID = data.getString((data.getColumnIndex("VariableID")));
                    Log.d("VariableID", "VariableID:" + VariableID);

                    String flag = data.getString((data.getColumnIndex("flag")));
                    Log.d("flag", "flag:" + flag);
                } while (data.moveToNext());
            }
            data.close();
        }
    }


    public Cursor getFeedbackData(String plotNo, String trialCode, String date) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("select Ranking, Rating, Remarks,PlotNo from FeedbackTaken where TRIAL_CODE='" + trialCode + "' AND  PlotNo='" + plotNo + "'AND DATE='" + date + "'", null);

        return mCursor;
    }
///---------------------------------------------------------end----------------------/////


    /*Rajshri Oct 2020
     * ------------------------------Feedback module start------------------------------------*/

    public boolean insertTrialFeedback(String Trial_Code, String Plot_No, String Is_Synced, String Rating, String Remarks, String Is_Submitted) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TRIL_CODE", Trial_Code);
        contentValues.put("PlotNo", Plot_No);
        contentValues.put("isSyncedStatus", Is_Synced);
        contentValues.put("Rating", Rating);
        contentValues.put("Remarks", Remarks);
        contentValues.put("isSubmitted", Is_Submitted);
        db.insert("trial_feedback_table", null, contentValues);
        db.close();
        return true;
    }

    public void updateTrialFeedbackAtLast(String Trial_Code, String Is_Submitted) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("isSubmitted", Is_Submitted);
        db.update("trial_feedback_table", contentValues, "TRIL_CODE='" + Trial_Code + "'", null);
        db.close();
    }

    public void saveTrialFeedback(String Trial_Code, String trialRating, String dateOfRating) {
        Log.d("saveTrialFeedback", "Trial_Code : " + Trial_Code + " trialRating=" + trialRating + " dateOfRating=" + dateOfRating);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TrialRating", trialRating);
        contentValues.put("dateOfRating", dateOfRating);
        //db.update("trial_feedback_table", contentValues, "TRIL_CODE = ?", new String[]{Trial_Code});
        db.update("trial_feedback_table", contentValues, "TRIL_CODE='" + Trial_Code + "'", null);
        db.close();
    }

    public void showFeedResult(String trialCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor FeedbackTrialData = db.rawQuery("select * from trial_feedback_table where TRIL_CODE='" + trialCode + "'  order by PlotNo ASC", null);
        FeedbackTrialData.moveToFirst();
        int i = 0;
        do {
            Log.d("showFeedResult", "Row : " + i);
            i++;
            String PlotNo = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("PlotNo")).toString();
            Log.d("showFeedResult data", "PlotNo : " + PlotNo);
            String dateOfRating = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("dateOfRating")).toString();
            Log.d("showFeedResult data", "dateOfRating : " + dateOfRating);
            if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Remarks")) != null) {
                String Remarks = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Remarks")).toString();
                Log.d("showFeedResult data", "Remarks : " + Remarks);
            }
            if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Rating")) != null) {
                String Rating = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("Rating")).toString();
                Log.d("showFeedResult data", "Rating : " + Rating);
            }
            if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("TrialRating")) != null) {
                String trialRating = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("TrialRating")).toString();
                Log.d("showFeedResult data", "Trial Rating : " + trialRating);
            }
            if (FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("dateOfVisitSinglePlot")) != null) {
                String dateOfVisitSinglePlot = FeedbackTrialData.getString(FeedbackTrialData.getColumnIndex("dateOfVisitSinglePlot")).toString();
                Log.d("showFeedResult data", "dateOfVisitSinglePlot : " + dateOfVisitSinglePlot);
            }
        } while (FeedbackTrialData.moveToNext());
    }


    ///---------//28032019//-------------------------//
    public void updateTrialFeedback(String Trial_Code, String Plot_No, String Is_Synced, String Rating, String Remarks, String Is_Submitted, String dateOfVisitSinglePlot) {
        Log.d("updateTrialFeedback", "Trial_Code=" + Trial_Code + " Plot_No=" + Plot_No + " Is_Synced=" + Is_Synced + " Rating=" + Rating + " Remarks=" + Remarks + " Is_Submitted=" + Is_Submitted + " dateOfVisitSinglePlot:" + dateOfVisitSinglePlot);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("isSyncedStatus", Is_Synced);
        contentValues.put("Rating", Rating);
        contentValues.put("Remarks", Remarks);
        contentValues.put("isSubmitted", Is_Submitted);
        contentValues.put("isSubmitted", Is_Submitted);
        contentValues.put("dateOfVisitSinglePlot", dateOfVisitSinglePlot); /*Added on 6th May 2021*/
        db.update("trial_feedback_table", contentValues, "TRIL_CODE = ? AND PlotNo= ?", new String[]{Trial_Code, Plot_No});
        //db.update("trial_feedback_table", contentValues, "TRIL_CODE='" + Trial_Code + "' AND " + "PlotNo='" + Plot_No + "'", null);
        db.close();
    }

    /*public void resetTrialFeedback(String Trial_Code,String Is_Synced, String Rating, String Remarks, String Is_Submitted) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("isSyncedStatus", Is_Synced);
        contentValues.put("Rating", Rating);
        contentValues.put("Remarks", Remarks);
        contentValues.put("isSubmitted", Is_Submitted);
        db.update("trial_feedback_table", contentValues, "TRIL_CODE='" + Trial_Code + "' AND " + "PlotNo='" + Plot_No + "'", null);
        db.close();
    }*/

    public long getTrialFeedbackCount(String Trial_Code, String Plot_No) {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "trial_feedback_table", "TRIL_CODE='" + Trial_Code + "' AND " + "PlotNo='" + Plot_No + "'", null);
        db.close();
        return count;
    }

    public boolean isTrialFeedbacKGiven(String trialCode, String plotNo) {
        boolean result = false;
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("select TrialRating from trial_feedback_table where TRIL_CODE='" + trialCode + "' AND  PlotNo='" + plotNo + "'", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            if (mCursor.getString(mCursor.getColumnIndex("TrialRating")) != null) {
                String TrialRating = mCursor.getString(mCursor.getColumnIndex("TrialRating")).toString();
                Log.d("isFeedGiven", "TrialRating : " + TrialRating);
                if (!TrialRating.equalsIgnoreCase("") && !TrialRating.isEmpty()) {
                    result = true;
                    Log.d("isFeedGiven", "TrialRating : " + TrialRating + " Return:" + result);
                    return result;
                }
            }
        }
        Log.d("isFeedGiven", "End Return:" + result);
        return result;
        /*boolean result = false;
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("select isSyncedStatus from trial_feedback_table where TRIL_CODE='" + trialCode + "' AND  PlotNo='" + plotNo + "'", null);
        if(mCursor!=null){
            mCursor.moveToFirst();
            if (mCursor.getString(mCursor.getColumnIndex("isSyncedStatus")) != null){
                String isSyncedStatus = mCursor.getString(mCursor.getColumnIndex("isSyncedStatus")).toString();
                Log.d("isFeedGiven","isSyncedStatus : "+isSyncedStatus);
                if(isSyncedStatus.equalsIgnoreCase("1")){
                    result =true;
                    Log.d("isFeedGiven","isSyncedStatus : "+isSyncedStatus+" Return:"+result);
                    return result;
                }
            }
            }
        Log.d("isFeedGiven","End Return:"+result);
        return result;*/
    }

    public Cursor getTrialFeedbackData(String plotNo, String trialCode) {
        Log.d("getTrialFeedbackData", "Plot No : " + plotNo + " Trial Code: " + trialCode);
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("select Rating,Remarks,PlotNo,dateOfVisitSinglePlot from trial_feedback_table where TRIL_CODE='" + trialCode + "' AND  PlotNo='" + plotNo + "'", null);
        Log.d("curserFeedback", "Cursor count : " + mCursor.getCount());
        return mCursor;
    }

    public Cursor getTrialFeedbackSummary(String trialCode) {
        Log.d("getTrialFeedbackSummary", "trialCode : " + trialCode);
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("select Location,Village,segment,dateOfRating,TrialRating,PlotNo,Rating,Remarks,isSyncedStatus,isSubmitted from trial_feedback_table where TRIL_CODE='" + trialCode + "'  order by PlotNo ASC", null);
        return mCursor;
    }

    public ArrayList<FeedbackReportModel> getTrialFeedbackData(String CROP, String Type, String year, String session) {
        ArrayList<FeedbackReportModel> list;
        list = new ArrayList<FeedbackReportModel>(); //TrailCodeData.TEHSIL
        String selectQuery = "SELECT DISTINCT " +
                "trial_feedback_table.TRIL_CODE," +
                "trial_feedback_table.Location," +
                "trial_feedback_table.Village," +
                "trial_feedback_table.segment," +
                "trial_feedback_table.CurrentStage," +
                "trial_feedback_table.DAS"
                + " FROM trial_feedback_table  where CROP='" + CROP + "' and Trail_Type='" + Type + "' " +
                "AND T_YEAR='" + year + "' " +
                "AND T_SESION='" + session + "' ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //JSONObject jobj = new JSONObject();
                FeedbackReportModel reportModel = new FeedbackReportModel();
                try {
                    reportModel.setTrailcode(cursor.getString(0));
                    reportModel.setLocation(cursor.getString(1));
                    reportModel.setVillage(cursor.getString(2));
                    reportModel.setTxt_TrialSegmentDetails(cursor.getString(3));
                    reportModel.setCurrentStage(cursor.getString(4));
                    reportModel.setDas(cursor.getString(5));
                } catch (Exception e) {
                    Log.d("MSG", e.getMessage());
                }
                list.add(reportModel);
            } while (cursor.moveToNext());
        }
        return list;
    }


    public boolean InsertFeedbackTrial(String T_YEAR, String T_SESION, String Crop, String Trail_Type, String TRIL_CODE, String Location, String segment, String PlotNo, String FBMID, String FBStaffCode, String DAS, String CurrentStage, String Village) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("T_YEAR", T_YEAR);
        contentValues.put("T_SESION", T_SESION);
        contentValues.put("Crop", Crop);
        contentValues.put("Trail_Type", Trail_Type);
        contentValues.put("TRIL_CODE", TRIL_CODE);
        contentValues.put("Location", Location);
        contentValues.put("segment", segment);
        contentValues.put("PlotNo", PlotNo);
        contentValues.put("FBMID", FBMID);
        contentValues.put("FBStaffCode", FBStaffCode);
        contentValues.put("Village", Village);
        contentValues.put("CurrentStage", CurrentStage);
        contentValues.put("DAS", DAS);
        db.insert("trial_feedback_table", null, contentValues);
        db.close();
        return true;
    }

    public int readTrialFeedbackCount() {
        String countQuery = "SELECT  * FROM trial_feedback_table";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        return count;
    }

    public String getStartPlotTrialFeedback(String trialCode) {
        String startPlotNo = "";
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "SELECT  " +
                "trial_feedback_table.PlotNo" +
                " FROM trial_feedback_table where TRIL_CODE='" + trialCode + "' order by PlotNo ASC";
        Cursor mCursor = db.rawQuery(selectQuery, null);
        mCursor.moveToFirst();
        startPlotNo = mCursor.getString(0);
        return startPlotNo;
    }

    public String getEndPlotTrialFeedback(String trialCode) {
        String endPlotNo = "";
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "SELECT " +
                "trial_feedback_table.PlotNo" +
                " FROM trial_feedback_table where TRIL_CODE='" + trialCode + "' order by PlotNo DESC";
        Cursor mCursor = db.rawQuery(selectQuery, null);
        mCursor.moveToFirst();
        endPlotNo = mCursor.getString(0);
        return endPlotNo;
    }

    public boolean checkTrialFeedbackExist(String trialCode, String plotNo) {
        boolean result = false;
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "SELECT " +
                "trial_feedback_table.PlotNo" +
                " FROM trial_feedback_table where TRIL_CODE='" + trialCode + "' and PlotNo='" + plotNo + "'";
        Cursor mCursor = db.rawQuery(selectQuery, null);
        if (mCursor.getCount() > 0) {
            result = true;
        }
        Log.d("checkTrialFeedbackExist", "Result : " + result);
        return result;
    }

    public List<String> getTrailTypeForFeedback(String crop) {
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  distinct Trail_Type from trial_feedback_table where CROP='" + crop + "' order by crop";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        // labels.add("Select");
        if (cursor.moveToFirst()) {
            do {

                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> getCropforTrialFeedback() {
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  distinct crop from trial_feedback_table  order by crop";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }
    /*----------------------------------------End-------------------------------------------------------------------*/

    public boolean insertPLDNotSown(String userCode, String trialCode,
                                    String status, String remark, String date,
                                    String imageName, String imagePath, String imageSyncStatus, String rowSyncStatus
    ) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("id", id);
        contentValues.put("userCode", userCode);
        contentValues.put("trialCode", trialCode);
        contentValues.put("status", status);
        contentValues.put("remark", remark);
        contentValues.put("date", date);
        contentValues.put("imageName", imageName);
        contentValues.put("imagePath", imagePath);
        contentValues.put("imageSyncStatus", imageSyncStatus);
        contentValues.put("rowSyncStatus", imageSyncStatus);
        db.insert("PLDNotSown", null, contentValues);
        db.close();
        return true;
    }

    /*Added on 31st March 2022*/
    public boolean isNotSownExist(String trialCode){
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM PLDNotSown where trialCode='" + trialCode.trim() + "'", null);
        if (mCursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }
    }


    public Cursor fetchPLDDATA(String TRIL_CODE) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM PLDNotSown where trialCode='" + TRIL_CODE.trim() + "'", null);
        return mCursor;
    }

    public boolean DownloadFillObservation(String VariableID, String TRIAL_CODE, String PlotNo, String Value1, String Value2, String Value3, String Value4, String Value5, String usercode, String valueSPM) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("VariableID", VariableID);
        contentValues.put("TRIAL_CODE", TRIAL_CODE);
        contentValues.put("PlotNo", PlotNo);
        contentValues.put("Value1", Value1);
        contentValues.put("Value2", Value2);
        contentValues.put("Value3", Value3);
        contentValues.put("Value4", Value4);
        contentValues.put("Value5", Value5);
        contentValues.put("usercode", usercode);
        contentValues.put("ValueSPM", valueSPM);
        contentValues.put("flag", 1);
        db.insert("Observationtaken", null, contentValues);
        db.close();
        return true;
    }


    public boolean DownladTagdata(String usercode, String TRIL_CODE, String coordinates, String address, String entrydate, String flag) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("usercode", usercode);
        contentValues.put("TRIL_CODE", TRIL_CODE);
        contentValues.put("coordinates", coordinates);
        contentValues.put("address", address);
        contentValues.put("entrydate", entrydate);
        contentValues.put("flag", flag);
        db.insert("tagdata1", null, contentValues);
        db.close();
        return true;
    }

    public boolean DownladFarmerInfodata(String Fname, String Fvillage, String Fmobile, String FStartNote, String Fsowingdate, String TRIL_CODE, String FArea, String flag, String usercode, String GeoLocation, String nursaryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Fname", Fname);
        contentValues.put("Fvillage", Fvillage);
        contentValues.put("Fmobile", Fmobile);
        contentValues.put("FStartNote", FStartNote);
        contentValues.put("Fsowingdate", Fsowingdate);
        contentValues.put("TRIL_CODE", TRIL_CODE);
        contentValues.put("FArea", FArea);
        contentValues.put("flag", flag);
        contentValues.put("usercode", usercode);
        contentValues.put("GeoLocation", GeoLocation);
        contentValues.put("nurseryDate", nursaryDate);
        db.insert("FarmerInfodata", null, contentValues);
        db.close();
        return true;
    }

    public Cursor Getlistcontent() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor data = db.rawQuery("SELECT DISTINCT TRIL_CODE,Location,Tagged  FROM " + Table1, null);

        // Cursor data= db.rawQuery("SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata.flag  FROM TrailCodeData left outer join tagdata on trailcodedata.TRIL_CODE=tagdata.TRIL_CODE",null);
        return data;
    }

    public Cursor GetSelectedlist(String crop) {
        SQLiteDatabase db = getWritableDatabase();


        Cursor data = db.rawQuery("SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag  FROM TrailCodeData left join tagdata1 on (TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + crop + "'", null);
        // Cursor data= db.rawQuery("SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata.flag  FROM TrailCodeData left outer join tagdata on trailcodedata.TRIL_CODE=tagdata.TRIL_CODE",null);
        return data;

    }

    public Cursor GetSelectedlistwithType(String crop, String Type) {
        SQLiteDatabase db = getWritableDatabase();


        Cursor data = db.rawQuery("SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag  FROM TrailCodeData left join tagdata1 on (TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + crop + "' and Trail_Type='" + Type + "'", null);
        // Cursor data= db.rawQuery("SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata.flag  FROM TrailCodeData left outer join tagdata on trailcodedata.TRIL_CODE=tagdata.TRIL_CODE",null);
        return data;

    }

    public Cursor GetSelectedObservation(String Crop, String Crop_Stage) {
        SQLiteDatabase db = getWritableDatabase();
        Log.d("GetSelectedObservation", "DATA Crop:" + Crop + " Crop_Stage:" + Crop_Stage);
        Cursor data = db.rawQuery("SELECT VariableID,Name  FROM ObservationMaster where flag='0' and Crop='" + Crop + "' and Crop_Stage='" + Crop_Stage + "' order by VariableID", null);
        // Cursor data= db.rawQuery("SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata.flag  FROM TrailCodeData left outer join tagdata on trailcodedata.TRIL_CODE=tagdata.TRIL_CODE",null);
        return data;
    }

    public Cursor GetTagSelectedlist(String crop) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor data = db.rawQuery("SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag  FROM TrailCodeData left join tagdata1 on (TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + crop + "' and tagdata1.flag='T'", null);
        // Cursor data= db.rawQuery("SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata.flag  FROM TrailCodeData left outer join tagdata on trailcodedata.TRIL_CODE=tagdata.TRIL_CODE",null);
        return data;
    }

    public Cursor GetTagSelectedlistwithType(String crop, String Type) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor data = db.rawQuery("SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag  FROM TrailCodeData left join tagdata1 on (TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + crop + "' and tagdata1.flag='T'and Trail_Type='" + Type + "'", null);
        // Cursor data= db.rawQuery("SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata.flag  FROM TrailCodeData left outer join tagdata on trailcodedata.TRIL_CODE=tagdata.TRIL_CODE",null);
        return data;
    }

    public Cursor fetchData(String TRIL_CODE) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor mCursor = db.rawQuery("SELECT * FROM TrailCodeData where TRIL_CODE='" + TRIL_CODE.trim() + "'", null);
        return mCursor;
    }

    public Cursor getObservationData(String plotNo, String trialCode, String VariableID) {
        SQLiteDatabase db = getWritableDatabase();

     /*   Cursor mCursor= db.rawQuery("select Crop,DownloadedObservation.VariableID,Name,S_M,Discription,Scale,Scale_type," +
                "Observationtaken.Value1,Observationtaken.value2,Observationtaken.Value3,Observationtaken.Value4," +
                "Observationtaken.value5," +
                "TRIAL_CODE,PlotNo from DownloadedObservation inner join " +
                "Observationtaken on (DownloadedObservation.VariableID=Observationtaken.VariableID) " +
                "where Observationtaken.TRIAL_CODE='"+trialCode+"' " +
                "AND Observationtaken.PlotNo='"+plotNo+"' and Observationtaken.VariableID='"+VariableID+"'" , null);
       */
        String query = "select * from Observationtaken  where TRIAL_CODE='" + trialCode + "' " +
                "AND PlotNo='" + plotNo + "' and VariableID='" + VariableID + "' order by Date desc LIMIT 1";
        Log.d("QUERY", "String : " + query);
        Cursor mCursor = db.rawQuery(query, null);
        return mCursor;
    }

    public Cursor getObservationDiscription(String variableID) {
        SQLiteDatabase db = getWritableDatabase();
        //Cursor mCursor = db.rawQuery("select Name,Discription,Value1,Value2 from DownloadedObservation  where VariableID='" + variableID + "' ", null);
        /*Changed on 21 July 2021*/
        Cursor mCursor = db.rawQuery("select Name,Discription,Value1,Value2 from TrialWiseDownloadedObservation  where VariableID='" + variableID + "' ", null);
        return mCursor;
    }

    public Cursor fetchFarmerData(String TRIL_CODE) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("select distinct TRIL_CODE,Fname,Fvillage,Fmobile,FstartNote,Fsowingdate,nurseryDate,GeoLocation,TFName,TFVillage,TFGeoLocation,TFMobile,TFStartNote,TFTransplantDate from FarmerInfodata where TRIL_CODE='" + TRIL_CODE.trim() + "'", null);
        return mCursor;
    }

    public Cursor Forobservation(String TRIL_CODE) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("select crop,Fname,Fvillage,Product,Fsowingdate,StartPlotNo,EndPlotNo,nurseryDate,nursery," +
                "TFName,TFVillage,TFGeoLocation,TFMobile,TFStartNote,TFTransplantDate " +
                "from trailcodedata inner join Farmerinfodata on" +
                " (trailcodedata.TRIL_CODE=Farmerinfodata.TRIL_CODE) where" +
                " Farmerinfodata.TRIL_CODE='" + TRIL_CODE.trim() + "'", null);
        return mCursor;
    }

    public Cursor getTrialDataForSowing(String TRIL_CODE) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("select * from FarmerInfodata where" +
                " Farmerinfodata.TRIL_CODE='" + TRIL_CODE.trim() + "'", null);
        return mCursor;
    }


    public Cursor ForTakenobservation(String TRIL_CODE) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("select * from Observationtaken  where TRIAL_CODE='" + TRIL_CODE.trim() + "'ORDER BY PlotNo DESC LIMIT 1", null);
        return mCursor;
    }

    /*Added on 15th July 2021*/
    public Cursor fetchTrialWiseDownloadedObservation(String crop, String trialCode) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "select * from TrialWiseDownloadedObservation where " +
                "Crop= '" + crop.trim() + "' AND TRIAL_CODE='" + trialCode.trim() + "'";
        Log.d("Testimonial", "fetchTrialWiseDownloadedObservation Query : " + query);
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor != null)
            Log.d("Testimonial", "fetchTrialWiseDownloadedObservation DATA COUNT : " + mCursor.getCount());
        return mCursor;
    }

    public Cursor fetchdownloadedObservation(String crop) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("select * from DownloadedObservation where " +
                "Crop= '" + crop.trim() + "'", null);
        return mCursor;
    }

    public Cursor fetchTrialCodeData() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM TrailCodeData", null);
        Log.d("VAPT", "fetchTrialCodeData : " + mCursor.toString());
        return mCursor;
    }

    public Cursor fetchusercode() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM UserMaster", null);
        Log.d("VAPT", "fetchusercode : " + mCursor.toString());
        return mCursor;
    }


    public boolean InsertFarmerData(String Fname, String Fvillage, String Fmobile, String FstartNote, String Fsowingdate, String TRIL_CODE, String area, String usercode, String GeoLocation, String txtnurseryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Fname", Fname);
        contentValues.put("Fvillage", Fvillage);
        contentValues.put("Fmobile", Fmobile);
        contentValues.put("FstartNote", FstartNote);
        contentValues.put("Fsowingdate", txtnurseryDate); //Fsowingdate); Note: 1 Dec 2020 changed as per Report requirements by Niteen
        contentValues.put("TRIL_CODE", TRIL_CODE);
        contentValues.put("FArea", area);
        contentValues.put("flag", 0);
        contentValues.put("usercode", usercode);
        contentValues.put("GeoLocation", GeoLocation);
        contentValues.put("nurseryDate", Fsowingdate); //txtnurseryDate); Note: 1 Dec 2020 changed as per Report requirements by Niteen
        Log.d("Rajshri", "DB Insert nursery sowing date : " + txtnurseryDate);
        Log.d("Rajshri", "DB Insert transplanting date : " + Fsowingdate);
        long result = db.insert("FarmerInfodata", null, contentValues);
        if (result == -1) {
            Log.d("Rajshri", "Insert FAILED");
            return false;
        } else {
            Log.d("Rajshri", "Insert Success");
            return true;
        }
    }

    /*Added 17th Feb 2021*/
    public boolean InsertFarmerDataTransplant(String TFName, String TFVillage, String TFGeoLocation, String TFMobile, String TFStartNote, String TFTransplantDate, String TRIAL_CODE, String userCode) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TFName", TFName);
        contentValues.put("TFVillage", TFVillage);
        contentValues.put("TFGeoLocation", TFGeoLocation);
        contentValues.put("TFMobile", TFMobile);
        contentValues.put("TFStartNote", TFStartNote);
        contentValues.put("TFTransplantDate", TFTransplantDate);
        //contentValues.put("TRIL_CODE", TRIAL_CODE);
        contentValues.put("flag", 0);
        contentValues.put("usercode", userCode);
        Log.d("INSERT_TDATA", "ADD VALUES");
        Log.d("INSERT_TDATA", "TFName:" + TFName);
        Log.d("INSERT_TDATA", "TFVillage:" + TFVillage);
        Log.d("INSERT_TDATA", "TFGeoLocation:" + TFGeoLocation);
        Log.d("INSERT_TDATA", "TFMobile:" + TFMobile);
        Log.d("INSERT_TDATA", "TFStartNote:" + TFStartNote);
        Log.d("INSERT_TDATA", "TFTransplantDate:" + TFTransplantDate);
        Log.d("INSERT_TDATA", "TRIL_CODE:" + TRIAL_CODE);
        Log.d("INSERT_TDATA", "usercode:" + userCode);
        //long result = db.update("FarmerInfodata", contentValues, "TRIL_CODE='" + TRIAL_CODE +"'", null);
        String strSQL = "UPDATE FarmerInfodata SET TFName ='" + TFName + "'," +
                "TFVillage='" + TFVillage + "'," +
                "TFGeoLocation='" + TFGeoLocation + "'," +
                "TFMobile='" + TFMobile + "'," +
                "TFStartNote='" + TFStartNote + "'," +
                "TFTransplantDate='" + TFTransplantDate + "'" + " WHERE TRIL_CODE = '" + TRIAL_CODE + "'";
        Log.d("INSERT_TDATA", "Update Query : " + strSQL);
        db.execSQL(strSQL);
        /*long result = db.update("FarmerInfodata", contentValues, "TRIL_CODE = ?", new String[]{"'" + TRIAL_CODE + "'"});
        if (result == -1) {
            Log.d("UPDATE","Failed");
            return false;
        }else {
            Log.d("UPDATE","Success");
        }return true;*/
        return true;
    }

    public boolean InsertObservation(String VariableID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Fname", VariableID);
        long result = db.insert("FarmerInfodata", null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public void updatePlot(int plotno, String Trialcode) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int Plotnos = plotno + 1;
        contentValues.put("PLOTSTART", Plotnos);
        db.update(Table1, contentValues, "TRIL_CODE='" + Trialcode + "'", null);
        db.close();
    }

    public JSONArray getResults(String Query) {
        String myTable = "Table1";//Set name of your table
        String searchQuery = Query;
        Cursor cursor = getReadableDatabase().rawQuery(searchQuery, null);
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (i == 19 || i == 21) {
                            if (cursor.getBlob(i) != null) {
                                rowObject.put(cursor.getColumnName(i), "");

                            } else {
                                rowObject.put(cursor.getColumnName(i), "");

                            }
                        } else {
                            if (cursor.getString(i) != null) {
                                if (cursor.getColumnName(i).equalsIgnoreCase("usercode")) {
                                    String userCodeDecrypt = EncryptDecryptManager.decryptStringData(cursor.getString(i));
                                    rowObject.put(cursor.getColumnName(i), userCodeDecrypt);
                                } else {
                                    Log.d("TAG_NAME", cursor.getString(i));
                                    rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                                }
                            } else {
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    public JSONArray getResultsPLD(String Query, String userCode) {
        String myTable = "Table1";//Set name of your table
        String searchQuery = Query;
        Cursor cursor = getReadableDatabase().rawQuery(searchQuery, null);
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (i == 19 || i == 21) {
                            if (cursor.getBlob(i) != null) {
                                rowObject.put(cursor.getColumnName(i), "");

                            } else {
                                rowObject.put(cursor.getColumnName(i), "");

                            }
                        } else {
                            if (cursor.getString(i) != null) {
                                if (cursor.getColumnName(i).equalsIgnoreCase("userCode")) {
                                    //String userCodeDecrypt = EncryptDecryptManager.decryptStringData(cursor.getString(i));
                                    rowObject.put(cursor.getColumnName(i), userCode);
                                } else {
                                    Log.d("TAG_NAME", cursor.getString(i));
                                    rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                                }
                            } else {
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    public JSONArray getResultsFeedback(String Query, String encryptedUserCode) {
        String usercode = EncryptDecryptManager.decryptStringData(encryptedUserCode);
        String myTable = "Table1";//Set name of your table
        String searchQuery = Query;
        Cursor cursor = getReadableDatabase().rawQuery(searchQuery, null);
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (i == 19 || i == 21) {
                            if (cursor.getBlob(i) != null) {
                                rowObject.put("usercode", usercode);
                                rowObject.put(cursor.getColumnName(i), "");

                            } else {
                                rowObject.put("usercode", usercode);
                                rowObject.put(cursor.getColumnName(i), "");

                            }
                        } else {
                            if (cursor.getString(i) != null) {

                                rowObject.put("usercode", usercode);
                                Log.d("TAG_NAME", cursor.getString(i));
                                rowObject.put(cursor.getColumnName(i), cursor.getString(i));

                            } else {
                                rowObject.put("usercode", usercode);
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }


    public JSONArray getResultsFromDB(String Query) {
        String myTable = "Table1";//Set name of your table
        String searchQuery = Query;
        Cursor cursor = getReadableDatabase().rawQuery(searchQuery, null);
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            if (cursor.getColumnName(i).equalsIgnoreCase("usercode")) {
                                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(cursor.getString(i));
                                rowObject.put(cursor.getColumnName(i), userCodeDecrypt);
                            } else {
                                Log.d("TAG_NAME", cursor.getString(i));
                                rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                            }
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }

                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    public int getTagDataCount(String TRIL_CODE, String tagType) {
        String selectQuery = "SELECT  * from tagdata where TRIL_CODE='" + TRIL_CODE + "' AND tagType='" + tagType + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        Log.d("TAG_DATA COUNT", "getTagDataCount COUNT : " + count);
        return count;
    }

    public boolean insertagdata(String userCode, String coordinates, String address, String entrydate, String TRIL_CODE, String tagType) {
        Log.d("insertagdata TAG_DATA", "USER_CODE : " + userCode
                + " \nCoordinates : " + coordinates
                + " \nAddress : " + address
                + " \nEntryDate : " + entrydate
                + " \nTRIL_CODE :" + TRIL_CODE
                + " \nTAG_TYPE : " + tagType);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.d("insertagdata", "insertagdata : TrialCode : " + TRIL_CODE + " TAG_TYPE : " + tagType);
        try {
            contentValues.put("userCode", userCode);
            contentValues.put("coordinates", coordinates);
            contentValues.put("address", address);
            contentValues.put("entrydate", entrydate);
            contentValues.put("TRIL_CODE", TRIL_CODE);
            contentValues.put("tagType", tagType);
            contentValues.put("flag", "T");
            contentValues.put("Uplaod", "U");
            long result = db.insert("tagdata", null, contentValues);
            if (result == -1)
                return false;
            else
                return true;
        } catch (Exception ex) {

        }

        return false;
    }

    public boolean insertagdata1(String userCode, String coordinates, String address, String entrydate, String TRIL_CODE, String tagType) {
        Log.d("TAG_DATA", "insertagdata1 : TrialCode : " + TRIL_CODE + " TAG_TYPE : " + tagType + " USERCODE:" + userCode);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userCode", userCode);
        contentValues.put("coordinates", coordinates);
        contentValues.put("address", address);
        contentValues.put("entrydate", entrydate);
        contentValues.put("TRIL_CODE", TRIL_CODE);
        contentValues.put("flag", "T"); /*17 Feb 2021*/
        contentValues.put("tagType", tagType);
        contentValues.put("Upload", "U");
        long result = db.insert("tagdata1", null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public void updateTagType(String trialCode) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tagType", AppConstant.FULL_TAG);
        Log.d("INSERT_TDATA", "TRIAL CODE : " + trialCode + " tagType:" + AppConstant.FULL_TAG);
        //long result = db.update("FarmerInfodata", contentValues, "TRIL_CODE='" + TRIAL_CODE +"'", null);
        // String strSQL = "UPDATE myTable SET Column1 = someValue WHERE columnId = "+ someValue;
        long result = db.update("FarmerInfodata", contentValues, "TRIL_CODE = ?", new String[]{"'" + trialCode + "'"});
        if (result == -1) {
            Log.d("DATA", "UPDATE Failed");
        } else {
            Log.d("DATA", "UPDATE Pass");
        }
    }

    public List<String> getObservationValue(String VariableID) {
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  Value1,Value2,Value3,Value4,Value5 from ObservationMaster where VariableID= '" + VariableID + "' ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add("Select");
                labels.add(cursor.getString(0));
                labels.add(cursor.getString(1));
                labels.add(cursor.getString(2));
                labels.add(cursor.getString(3));
                labels.add(cursor.getString(4));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    //
    public List<String> getCrop() {
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  distinct crop from TrailCodeData order by crop";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        //labels.add(("Select"));
        if (cursor.moveToFirst()) {
            do {

                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> getUniqueTrialType() {
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  distinct Trail_Type from TrailCodeData order by Trail_Type";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        //labels.add(("Select"));
        if (cursor.moveToFirst()) {
            do {

                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> getTrailType(String crop) {
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  distinct Trail_Type from TrailCodeData where CROP='" + crop + "' order by crop";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        // labels.add("Select");
        if (cursor.moveToFirst()) {
            do {

                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> getCropforObservation() {
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  distinct crop from TrailCodeData  order by crop";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> getObservationCrop() {
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "select distinct TrailCodeData.CROP from TrailCodeData inner join ObservationMaster on (TrailCodeData.CROP=ObservationMaster.Crop) order by TrailCodeData.CROP";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }


    public ArrayList<String> getStagesObservation(String crop) {
        ArrayList<String> stages = new ArrayList<String>();
        Log.d("getStagesObservation", "Crop : " + crop);

        // Select All Query
//        String selectQuery = "select distinct Crop_Stage from  ObservationMaster where Crop_Stage!='null'  order by Crop_Stage asc";
        String selectQuery = "select distinct Crop_Stage from  ObservationMaster where Crop='" + crop + "' AND Crop_Stage!='null' order by Crop_Stage asc";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                stages.add(cursor.getString(0).trim());
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return stages;
    }

    public void runQuery(String runQuery) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        db.execSQL(runQuery);
        db.close();
    }

    public void deleteQuery(String deleteQuery) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        db.execSQL(deleteQuery);
        db.close();
    }

    public SQLiteDatabase getDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        return db;
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }


    public ArrayList<TrailReportModel> getTrailDetailstag(String CROP) {
        ArrayList<TrailReportModel> list;
        list = new ArrayList<TrailReportModel>();

        String selectQuery = "SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag,TrailCodeData.segment " +
                " FROM TrailCodeData left join tagdata1 on (TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + CROP +
                "' and tagdata1.flag='T'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //JSONObject jobj = new JSONObject();
                TrailReportModel reportModel = new TrailReportModel();
                try {

                    reportModel.setTrailcode(cursor.getString(0));
                    reportModel.setLocation(cursor.getString(1));
                    reportModel.setTagged(cursor.getString(2));
                    reportModel.setTxt_TrialSegmentDetails(cursor.getString(3));
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
                list.add(reportModel);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<FeedbackReportModel> getTrailDetailstagFeedbackData(String CROP) {
        ArrayList<FeedbackReportModel> list;
        list = new ArrayList<FeedbackReportModel>();

        String selectQuery = "SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag,TrailCodeData.segment " +
                " FROM TrailCodeData left join tagdata1 on (TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + CROP +
                "' and tagdata1.flag='T'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //JSONObject jobj = new JSONObject();
                FeedbackReportModel reportModel = new FeedbackReportModel();
                try {

                    reportModel.setTrailcode(cursor.getString(0));
                    reportModel.setLocation(cursor.getString(1));
                    reportModel.setTagged(cursor.getString(2));
                    reportModel.setTxt_TrialSegmentDetails(cursor.getString(3));
                } catch (Exception e) {
                    Log.d("MSG", e.getMessage());
                }
                list.add(reportModel);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<TrailReportModel> getTrailDetailsTypetag(String CROP, String Type) {
        ArrayList<TrailReportModel> list;
        list = new ArrayList<TrailReportModel>();

        String selectQuery = "SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag,TrailCodeData.segment  FROM TrailCodeData left join tagdata1 on (TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + CROP + "' and tagdata1.flag='T'and Trail_Type='" + Type + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //JSONObject jobj = new JSONObject();
                TrailReportModel reportModel = new TrailReportModel();
                try {

                    reportModel.setTrailcode(cursor.getString(0));
                    reportModel.setLocation(cursor.getString(1));
                    reportModel.setTagged(cursor.getString(2));
                    reportModel.setTxt_TrialSegmentDetails(cursor.getString(3));
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
                list.add(reportModel);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<TrailReportModel> getObservation(String CROP, String Type, String year, String session) {
        ArrayList<TrailReportModel> list;
        list = new ArrayList<TrailReportModel>();

        String selectQuery = "SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag," +
                "TrailCodeData.segment  FROM TrailCodeData left join tagdata1 on " +
                "(TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + CROP + "' and Trail_Type='" + Type + "' " +
                "AND T_YEAR='" + year + "' " +
                "AND T_SESION='" + session + "' "
                + "AND tagdata1.flag='T'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //JSONObject jobj = new JSONObject();
                TrailReportModel reportModel = new TrailReportModel();
                try {

                    reportModel.setTrailcode(cursor.getString(0));
                    reportModel.setLocation(cursor.getString(1));
                    reportModel.setTagged(cursor.getString(2));
                    reportModel.setTxt_TrialSegmentDetails(cursor.getString(3));
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
                list.add(reportModel);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<FeedbackReportModel> getObservationFeedbackData(String CROP, String Type, String year, String session) {
        ArrayList<FeedbackReportModel> list;
        list = new ArrayList<FeedbackReportModel>(); //TrailCodeData.TEHSIL

        String selectQuery = "SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag," +
                "TrailCodeData.segment  FROM TrailCodeData left join tagdata1 on " +
                "(TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + CROP + "' and Trail_Type='" + Type + "' " +
                "AND T_YEAR='" + year + "' " +
                "AND T_SESION='" + session + "' "
                + "AND tagdata1.flag='T'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //JSONObject jobj = new JSONObject();
                FeedbackReportModel reportModel = new FeedbackReportModel();
                try {

                    reportModel.setTrailcode(cursor.getString(0));
                    reportModel.setLocation(cursor.getString(1));
                    reportModel.setTagged(cursor.getString(2));
                    reportModel.setTxt_TrialSegmentDetails(cursor.getString(3));
                } catch (Exception e) {
                    Log.d("MSG", e.getMessage());
                }
                list.add(reportModel);
            } while (cursor.moveToNext());
        }
        return list;
    }


    public ArrayList<TrailReportModel> getTrailDetails(String CROP) {
        ArrayList<TrailReportModel> list;
        list = new ArrayList<TrailReportModel>();

        String selectQuery = "SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag," +
                "TrailCodeData.segment  FROM TrailCodeData left join tagdata1 " +
                "on (TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + CROP + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //JSONObject jobj = new JSONObject();
                TrailReportModel reportModel = new TrailReportModel();
                try {

                    reportModel.setTrailcode(cursor.getString(0));
                    reportModel.setLocation(cursor.getString(1));
                    reportModel.setTagged(cursor.getString(2));
                    reportModel.setTxt_TrialSegmentDetails(cursor.getString(3));
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
                list.add(reportModel);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> getFullTagTrialCodes() {
        ArrayList<String> fullTagTrialCodes = new ArrayList<String>();

        String selectQuery = "SELECT DISTINCT tagdata1.TRIL_CODE FROM tagdata1 where tagdata1.tagType='" + AppConstant.FULL_TAG + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String trialCode = "";
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    trialCode = (cursor.getString(0));
                    Log.d("FULL TAG", "TRIL_CODE : " + trialCode);
                    fullTagTrialCodes.add(trialCode);
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            } while (cursor.moveToNext());
        }
        return fullTagTrialCodes;
    }

    public ArrayList<TrailReportModel> getTrailDetailsType(String CROP, String Type, String year, String session) {
        ArrayList<TrailReportModel> list;
        list = new ArrayList<TrailReportModel>();

        String selectQuery = "SELECT DISTINCT TrailCodeData.TRIL_CODE,TrailCodeData.Location,tagdata1.flag," +
                "TrailCodeData.segment, tagdata1.tagType FROM TrailCodeData left join tagdata1 on " +
                "(TrailCodeData.TRIL_CODE=tagdata1.TRIL_CODE) where CROP='" + CROP + "' and Trail_Type='" + Type + "' " +
                "AND T_YEAR='" + year + "' " +
                "AND T_SESION='" + session + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int i = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //JSONObject jobj = new JSONObject();
                TrailReportModel reportModel = new TrailReportModel();
                try {
                    reportModel.setTrailcode(cursor.getString(0));
                    reportModel.setLocation(cursor.getString(1));
                    String tagged = cursor.getString(2);
                    reportModel.setTagged(tagged);
                    reportModel.setTxt_TrialSegmentDetails(cursor.getString(3));
                    //reportModel.setTagType(cursor.getString(4)!=null?cursor.getString(4) :"NO_TAG");
                    Log.d("DATA", "OPPO TRIAL CODE :" + cursor.getString(0) + " Tagged : " + tagged + " tagged_type:" + cursor.getString(4));
                    if (tagged == null) {
                        reportModel.setTagType(AppConstant.NO_TAG);
                    } else if (cursor.getString(4) != null) {
                        reportModel.setTagType(cursor.getString(4));
                    } else {
                        if (tagged.equals("T")) {
                            reportModel.setTagType(AppConstant.FULL_TAG);
                        } else {
                            reportModel.setTagType(AppConstant.NO_TAG);
                        }
                    }
                    //reportModel.setTagType("");
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
                Log.d("RECORDS", "REPORT_MODEL :: " + reportModel.toString());
                i++;
                list.add(reportModel);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<ReportModel> getReport(String CROP) {
        ArrayList<ReportModel> list;
        list = new ArrayList<ReportModel>();

        String selectQuery = "select a.tril_code,c.Fname,a.Location,COUNT(b.Trial_code) as Observationcount from TrailCodeData" +
                " as a left join Observationtaken as b on a.TRIL_CODE = b.Trial_code left join FarmerInfodata as  c on " +
                "a.TRIL_CODE = c.TRIL_CODE where a.CROP='" + CROP + "' group by a.TRIL_CODE,a.Location,c.Fname";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //JSONObject jobj = new JSONObject();
                ReportModel reportModel = new ReportModel();
                try {

                    reportModel.setTrail_code(cursor.getString(0));
                    reportModel.setFarmerName(cursor.getString(1));
                    reportModel.setLocation(cursor.getString(2));
                    reportModel.setObsesrvationValue(cursor.getString(3));

                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
                list.add(reportModel);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<detailReportModel> getDetailReport(String TrailCode) {
        ArrayList<detailReportModel> list;
        list = new ArrayList<detailReportModel>();

        String selectQuery = "select PlotNo,count(PlotNo) from observationtaken where TRIAL_CODE='" + TrailCode + "' group by PlotNo";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //JSONObject jobj = new JSONObject();
                detailReportModel detailReportModel = new detailReportModel();
                try {

                    detailReportModel.setPlotNo(cursor.getString(0));
                    detailReportModel.setObsesrvationTaken(cursor.getString(1));

                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
                list.add(detailReportModel);
            } while (cursor.moveToNext());
        }
        return list;
    }


    public String getImageDatadetail(String path) {
        String myTable = "Table1";//Set name of your table
        String str = "";
        try {
            if (path != null || path.length() > 0) {
                str = path;//Base64.encodeToString(cursor.getBlob(cursor.getColumnIndex(colname)),Base64.DEFAULT);
                // rowObject.put(cursor.getColumnName(i), Base64.encodeToString(cursor.getBlob(i),Base64.DEFAULT));
                File f = new File(str);
                Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
                // original measurements
                int origWidth = b.getWidth();
                int origHeight = b.getHeight();
                final int destWidth = 200;//or the width you need
                if (origWidth > destWidth) {
                    // picture is wider than we want it, we calculate its target height
                    int destHeight = origHeight / (origWidth / destWidth);
                    // we create an scaled bitmap so it reduces the image, not just trim it
                    // Bitmap b2 = Bitmap.createScaledBitmap(b, 400, 350, false);
                    Bitmap b2 = compressImage(str);//scaleBitmap(b,400,400);


                    // 70 is the 0-100 quality percentage
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    b2.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    str = Base64.encodeToString(byteArray, Base64.DEFAULT);
                } else {
                    // 70 is the 0-100 quality percentage
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    str = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
            }


        } catch (Exception e) {
            Log.d("TAG_NAME", e.getMessage());
        }
        //  }


        return str;
    }


    //Start
    public static Bitmap compressImage(String str) {
        Bitmap scaledBitmap = null;
        try {
            File f = new File(str);


            BitmapFactory.Options options = new BitmapFactory.Options();
//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
            // float maxHeight = 816.0f;
            //float maxWidth = 612.0f;
            float maxHeight = 516.0f;
            float maxWidth = 412.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                //bmp = BitmapFactory.decodeFile(filePath, options);
                bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
            } catch (OutOfMemoryError exception) {
                Log.d("Msg", exception.getMessage());

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                Log.d("Msg", exception.getMessage());
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif = null;
            try {
                try {
                    exif = new ExifInterface(f.getAbsolutePath());
                } catch (IOException e) {
                    Log.d("Msg", e.getMessage());
                }

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
            }
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
        }
        return scaledBitmap;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        String debugTag = "MemoryInformation";
        // Image nin islenmeden onceki genislik ve yuksekligi
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(debugTag, "image height: " + height + "---image width: " + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(debugTag, "inSampleSize: " + inSampleSize);
        return inSampleSize;
    }

    public boolean InsertTravelTime(String mdocode, String coordinate, String startaddress, String startdate,
                                    String dist, String taluka, String village,
                                    String imgname, String imgpath, String txtkm, String place, String vehicletype) {


        boolean flag = false;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("mdocode", mdocode);
            contentValues.put("coordinate", coordinate);
            contentValues.put("startaddress", startaddress);
            contentValues.put("startdate", startdate);
            contentValues.put("coordinate", coordinate);
            contentValues.put("dist", dist);
            contentValues.put("taluka", taluka);
            contentValues.put("village", village);
            contentValues.put("imgname", imgname);
            contentValues.put("imgpath", imgpath);
            contentValues.put("txtkm", txtkm);
            contentValues.put("Status", "0");
            contentValues.put("place", place);
            contentValues.put("imgstatus", "0");
            contentValues.put("vehicletype", vehicletype);

            Log.d("VAPT", "MDOTravelDATA : INSERT : START TRAVEL-------------------------------------------------------------");
            Log.d("VAPT", "MDOTravelDATA : INSERT mdocode : " + mdocode);
            Log.d("VAPT", "MDOTravelDATA : INSERT place : " + place);
            Log.d("VAPT", "MDOTravelDATA : INSERT coordinate : " + coordinate);
            Log.d("VAPT", "MDOTravelDATA : INSERT startaddress : " + startaddress);
            Log.d("VAPT", "MDOTravelDATA : INSERT date : " + startdate);
            Log.d("VAPT", "MDOTravelDATA : INSERT imgname : " + imgname);
            Log.d("VAPT", "MDOTravelDATA : INSERT imgpath : " + imgpath);
            Log.d("VAPT", "MDOTravelDATA : INSERT Status : 0");
            Log.d("VAPT", "MDOTravelDATA : INSERT : END-------------------------------------------------------------");
            db.insert("mdo_starttravel", null, contentValues);
            db.close();
            flag = true;
        } catch (Exception ex) {
            flag = false;
        }
        return flag;
    }

    public boolean InsertendTravelTime(String mdocode, String coordinate, String startaddress, String enddate,
                                       String dist, String taluka, String village,
                                       String imgname, String imgpath, String txtkm, String place, String vehicletype) {


        boolean flag = false;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("mdocode", mdocode);
            contentValues.put("coordinate", coordinate);
            contentValues.put("startaddress", startaddress);
            contentValues.put("enddate", enddate);
            contentValues.put("coordinate", coordinate);
            contentValues.put("dist", dist);
            contentValues.put("taluka", taluka);
            contentValues.put("village", village);
            contentValues.put("imgname", imgname);
            contentValues.put("imgpath", imgpath);
            contentValues.put("txtkm", txtkm);
            contentValues.put("Status", "0");
            contentValues.put("place", place);
            contentValues.put("imgstatus", "0");
            contentValues.put("vehicletype", vehicletype);

            Log.d("VAPT", "MDOTravelDATA : INSERT : END TRAVEL-------------------------------------------------------------");
            Log.d("VAPT", "MDOTravelDATA : INSERT mdocode : " + mdocode);
            Log.d("VAPT", "MDOTravelDATA : INSERT place : " + place);
            Log.d("VAPT", "MDOTravelDATA : INSERT coordinate : " + coordinate);
            Log.d("VAPT", "MDOTravelDATA : INSERT startaddress : " + startaddress);
            Log.d("VAPT", "MDOTravelDATA : INSERT date : " + enddate);
            Log.d("VAPT", "MDOTravelDATA : INSERT imgname : " + imgname);
            Log.d("VAPT", "MDOTravelDATA : INSERT imgpath : " + imgpath);
            Log.d("VAPT", "MDOTravelDATA : INSERT Status : 0");
            Log.d("VAPT", "MDOTravelDATA : INSERT :END TRAVEL END-------------------------------------------------------------");

            db.insert("mdo_endtravel", null, contentValues);
            db.close();
            flag = true;
        } catch (Exception ex) {
            flag = false;
        }
        return flag;
    }

    public boolean insertAddplace(String mdocode, String place, String coordinate, String startaddress, String date,

                                  String imgname, String imgpath) {

        boolean flag = false;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("mdocode", mdocode);
            contentValues.put("place", place);
            contentValues.put("coordinate", coordinate);
            contentValues.put("startaddress", startaddress);
            contentValues.put("date", date);
            contentValues.put("imgname", imgname);
            contentValues.put("imgpath", imgpath);
            contentValues.put("Status", "0");
            Log.d("VAPT", "MDOTravelDATA : INSERT : ADD PLACE TRAVEL-------------------------------------------------------------");
            Log.d("VAPT", "MDOTravelDATA : INSERT mdocode : " + mdocode);
            Log.d("VAPT", "MDOTravelDATA : INSERT place : " + place);
            Log.d("VAPT", "MDOTravelDATA : INSERT coordinate : " + coordinate);
            Log.d("VAPT", "MDOTravelDATA : INSERT startaddress : " + startaddress);
            Log.d("VAPT", "MDOTravelDATA : INSERT date : " + date);
            Log.d("VAPT", "MDOTravelDATA : INSERT imgname : " + imgname);
            Log.d("VAPT", "MDOTravelDATA : INSERT imgpath : " + imgpath);
            Log.d("VAPT", "MDOTravelDATA : INSERT Status : 0");
            Log.d("VAPT", "MDOTravelDATA : INSERT ADD PLACE: END-------------------------------------------------------------");
            db.insert("mdo_addplace", null, contentValues);
            db.close();
            flag = true;
        } catch (Exception ex) {
            flag = false;
        }
        return flag;
    }

    public void deleterecord(String updatesting) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(updatesting);
        db.close();
    }

    public void Updatedata(String updatesting) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(updatesting);
    }

    public JSONArray getResultsForTravel(String Query, String usercode) {
        String myTable = "Table1";//Set name of your table
        String searchQuery = Query;
        Cursor cursor = getReadableDatabase().rawQuery(searchQuery, null);
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (i == 19 || i == 21) {
                            if (cursor.getBlob(i) != null) {
                                rowObject.put("mdocode", usercode);
                                rowObject.put(cursor.getColumnName(i), "");

                            } else {
                                rowObject.put("mdocode", usercode);
                                rowObject.put(cursor.getColumnName(i), "");

                            }
                        } else {
                            if (cursor.getString(i) != null) {

                                rowObject.put("mdocode", usercode);
                                Log.d("TAG_NAME", cursor.getString(i));
                                rowObject.put(cursor.getColumnName(i), cursor.getString(i));

                            } else {
                                rowObject.put("mdocode", usercode);
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    public int observationStatus(String trialCode) {
        int result = 0;
        String flag = "", trialCodeAccessed = "", plotNo = "";
        Cursor Farmerdata1 = ForTakenobservation(trialCode);
        int recordCount = Farmerdata1.getCount();
        if (recordCount == 0) {
            result = 1;
        } else {
            Farmerdata1.moveToFirst();
            if (Farmerdata1 != null) {
                do {
                    flag = Farmerdata1.getString((Farmerdata1.getColumnIndex("flag")));
                    plotNo = Farmerdata1.getString((Farmerdata1.getColumnIndex("PlotNo")));
                    trialCodeAccessed = Farmerdata1.getString((Farmerdata1.getColumnIndex("TRIAL_CODE")));

                } while (Farmerdata1.moveToNext());
            }
            Farmerdata1.close();
        }
        Log.d("OBSERVATION", "observationStatus flag: " + flag);
        Log.d("OBSERVATION", "observationStatus PlotNo: " + plotNo);
        Log.d("OBSERVATION", "observationStatus TRIAL_CODE: " + trialCodeAccessed);
        if (flag.equalsIgnoreCase("1")) {
            result = 3;
        } else if (recordCount > 0 && flag.equalsIgnoreCase("0")) {
            result = 2;
        }
        Log.d("OBSERVATION", "RETURN RESULT observationStatus result: " + result);
        return result;
    }

    //START Change 31st August 2021

    public boolean insertYear(String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("flag", "0");
        db.insert(YEAR_TABLE, null, contentValues);
        db.close();
        return true;
    }

    public boolean insertSeason(String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("flag", "0");
        db.insert(SEASON_TABLE, null, contentValues);
        db.close();
        return true;
    }

    public ArrayList<String> getYearList() {
        ArrayList<String> yearList = new ArrayList<>();
        String selectQuery = "select name from " + YEAR_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String year = "";
                try {
                    year = cursor.getString(0);
                    if (year != null && !year.equals(""))
                        yearList.add(year);
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            } while (cursor.moveToNext());
        }
        return yearList;
    }

    public ArrayList<String> getSeasonList() {
        ArrayList<String> seasonList = new ArrayList<>();
        String selectQuery = "select name from " + SEASON_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String season = "";
                try {
                    season = cursor.getString(0);
                    if (season != null && !season.equals(""))
                        seasonList.add(season);
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            } while (cursor.moveToNext());
        }
        return seasonList;
    }

    public void updateSelectedYear(String value) {
        Log.d("updateSelectedYear", "Update year list");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("flag", "1");
        db.update(YEAR_TABLE, contentValues, "name='" + value + "'", null);
        db.close();
    }

    public void updateSelectedSeason(String value) {
        Log.d("updateSelectedYear", "Update season list");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("flag", "1");
        db.update(SEASON_TABLE, contentValues, "name='" + value + "'", null);
        db.close();
    }

    public ArrayList<String> getSelectedYearList() {
        ArrayList<String> yearList = new ArrayList<>();
        String selectQuery = "select name from " + YEAR_TABLE + " where flag='1'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String year = "";
                try {
                    year = cursor.getString(0);
                    if (year != null && !year.equals(""))
                        yearList.add(year);
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            } while (cursor.moveToNext());
        }
        return yearList;
    }

    public ArrayList<String> getSelectedSeasonList() {
        ArrayList<String> seasonList = new ArrayList<>();
        String selectQuery = "select name from " + SEASON_TABLE + " where flag='1'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String season = "";
                try {
                    season = cursor.getString(0);
                    if (season != null && !season.equals(""))
                        seasonList.add(season);
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            } while (cursor.moveToNext());
        }
        return seasonList;
    }
    //END Change
}


