package com.tdms.mahyco.nxg;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadDataRecordsCount {

    public static boolean isUploadRecordsPending(Context context, databaseHelper databaseHelper1) {

        boolean result = false;

        //Tested on 28 April 2021 : DATE MATCHING: NO NOTIFICATION
        result = isPendingSowingTag(databaseHelper1);
        if (result) {
            return result;
        }

        /*Tested on on 28 April 2021 : WITH NOTIFICATION && 29 April DATE NOT MATCHING NOTIFY*/
        result = isPendingPlotData(databaseHelper1);
        if (result) {
            return result;
        }

        /* Tested on on 28 April 2021 : Tested with NO DATA && DATE MATCHING: NO NOTIFICATION && 29 April DATE NOT MATCHING NOTIFY*/
        result = isPendingObservationImages(databaseHelper1);
        if (result) {
            return result;
        }

        //Tested on on 28 April 2021 "isPLDSownRecord NO DATA" && DATE MATCHING: NO NOTIFICATION && DATE NOT MATCHING NOTIFY(with 2 Dates)
        result = isPLDSownRecord(databaseHelper1);
        if (result) {
            return result;
        }

        //Tested on on 28 April 2021 " NO DATA" && DATE MATCHING: NO NOTIFICATION && 29 April DATE NOT MATCHING NOTIFY
        result = isPendingMyTravelData(databaseHelper1);
        if (result) {
            return result;
        }

        //Tested on on 28 April 2021 " NO DATA"
        result = isPendingFeedbackOld(databaseHelper1);
        if (result) {
            return result;
        }


        //Tested on on 28 April 2021 " NO DATA"
        result = isPendingFeedbackNew(databaseHelper1);
        if (result) {
            return result;
        }


        return result;
    }

    private static boolean isPendingSowingTag(databaseHelper databaseHelper1) {
        boolean result = false;
        /*Pending Sowing & TAG data*/
        String searchQuery = "select  *  from FarmerInfodata inner join tagdata1 on FarmerInfodata.TRIL_CODE=tagdata1.TRIL_CODE where tagdata1.flag='T' and tagdata1.Upload='U'";
        Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
        int count = cursor.getCount();

        cursor.moveToFirst();
        if (count > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(new Date());
            Log.d(" data", "UploadDataRecordsCount isPendingSowingTag currentDate : " + currentDate);
            do {
                if (cursor.getString(cursor.getColumnIndex("entrydate")) != null) {
                    String entrydate = cursor.getString(cursor.getColumnIndex("entrydate")).toString();
                    Log.d(" data", "UploadDataRecordsCount isPendingSowingTag Full Date : " + entrydate);

                    String[] splitStr = entrydate.trim().split("\\s+");
                    Log.d(" data", "UploadDataRecordsCount isPendingSowingTag ONLY DATE : " + splitStr[0]);
                    if (!currentDate.equalsIgnoreCase(splitStr[0])) {
                        Log.d(" data", "UploadDataRecordsCount isPendingSowingTag DATE NOT MATCHING NOTIFY");
                        result = true;
                        return result;
                    } else {
                        Log.d(" data", "UploadDataRecordsCount isPendingSowingTag DATE MATCHING: NO NOTIFICATION");
                        result = false;
                        return result;
                    }
                }

            } while (cursor.moveToNext());
        } else {
            Log.d(" data", "UploadDataRecordsCount isPendingSowingTag Date : NO DATA");
        }

        cursor.close();
        if (count > 0) {
            result = true;
            return result;
        }
        return result;
    }

    private static boolean isPendingPlotData(databaseHelper databaseHelper1) {
        /*Pending Plot data*/
        boolean result = false;
        String searchQuery1 = "select distinct PlotNo, Date from Observationtaken where flag='0'";
        Cursor cursor1 = databaseHelper1.getReadableDatabase().rawQuery(searchQuery1, null);
        int count1 = cursor1.getCount();

        cursor1.moveToFirst();
        if (count1 > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(new Date());
            Log.d(" data", "UploadDataRecordsCount isPendingPlotData currentDate : " + currentDate);
            do {
                if (cursor1.getString(cursor1.getColumnIndex("Date")) != null) {
                    String entrydate = cursor1.getString(cursor1.getColumnIndex("Date")).toString();
                    Log.d(" data", "UploadDataRecordsCount isPendingPlotData FULL Date : " + entrydate);

                    String[] splitStr = entrydate.trim().split("\\s+");
                    Log.d(" data", "UploadDataRecordsCount isPendingPlotData ONLY DATE : " + splitStr[0]);
                    if (!currentDate.equalsIgnoreCase(splitStr[0])) {
                        Log.d(" data", "UploadDataRecordsCount isPendingPlotData DATE NOT MATCHING NOTIFY");
                        result = true;
                        return result;
                    } else {
                        Log.d(" data", "UploadDataRecordsCount isPendingPlotData DATE MATCHING: NO NOTIFICATION");
                        result = false;
                        return result;
                    }
                }

            } while (cursor1.moveToNext());
        } else {
            Log.d(" data", "UploadDataRecordsCount isPendingPlotData Date : NO DATA");
        }

        cursor1.close();
        if (count1 > 0) {
            result = true;
            return result;
        }
        return result;
    }

    private static boolean isPendingObservationImages(databaseHelper databaseHelper1) {
        /*Pending Observation taken images*/
        boolean result = false;
        String searchQuery2 = "select  DISTINCT Image,ImageName,Date  from Observationtaken where " +
                "(Image IS NOT NULL AND Image!='' ) AND ImageSyncStatus='0'";
        Cursor cursor2 = databaseHelper1.getReadableDatabase().rawQuery(searchQuery2, null);
        int count2 = cursor2.getCount();

        cursor2.moveToFirst();
        if (count2 > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(new Date());
            Log.d(" data", "UploadDataRecordsCount isPendingObservationImages currentDate : " + currentDate);
            do {
                if (cursor2.getString(cursor2.getColumnIndex("Date")) != null) {
                    String entrydate = cursor2.getString(cursor2.getColumnIndex("Date")).toString();
                    Log.d(" data", "UploadDataRecordsCount isPendingObservationImages FULL Date : " + entrydate);

                    String[] splitStr = entrydate.trim().split("\\s+");
                    Log.d(" data", "UploadDataRecordsCount isPendingObservationImages ONLY DATE : " + splitStr[0]);
                    if (!currentDate.equalsIgnoreCase(splitStr[0])) {
                        Log.d(" data", "UploadDataRecordsCount isPendingObservationImages DATE NOT MATCHING NOTIFY");
                        result = true;
                        return result;
                    } else {
                        Log.d(" data", "UploadDataRecordsCount isPendingObservationImages DATE MATCHING: NO NOTIFICATION");
                        result = false;
                        return result;
                    }
                }

            } while (cursor2.moveToNext());
        } else {
            Log.d(" data", "UploadDataRecordsCount isPendingObservationImages NO DATA");
        }

        cursor2.close();
        if (count2 > 0) {
            result = true;
            return result;
        }
        return result;
    }

    private static boolean isPLDSownRecord(databaseHelper databaseHelper1) {
        /*Pending PLD Sown record*/
        boolean result = false;
        int totalcount = 0;
        String searchQuery3 = "";
        int count3 = 0;
        searchQuery3 = "select * from PLDNotSown where rowSyncStatus='0'";
        Cursor cursor3 = databaseHelper1.getReadableDatabase().rawQuery(searchQuery3, null);
        count3 = cursor3.getCount();

        cursor3.moveToFirst();
        if (count3 > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = sdf.format(new Date());
            Log.d(" data", "UploadDataRecordsCount isPLDSownRecord currentDate : " + currentDate);
            do {
                if (cursor3.getString(cursor3.getColumnIndex("date")) != null) {
                    String entrydate = cursor3.getString(cursor3.getColumnIndex("date")).toString();
                    Log.d(" data", "UploadDataRecordsCount isPLDSownRecord FULL Date : " + entrydate);

                    String[] splitStr = entrydate.trim().split("\\s+");
                    Log.d(" data", "UploadDataRecordsCount isPLDSownRecord ONLY DATE : " + splitStr[0]);
                    if (!currentDate.equalsIgnoreCase(splitStr[0])) {
                        Log.d(" data", "UploadDataRecordsCount isPLDSownRecord DATE NOT MATCHING NOTIFY");
                        result = true;
                        return result;
                    } else {
                        Log.d(" data", "UploadDataRecordsCount isPLDSownRecord DATE MATCHING: NO NOTIFICATION");
                        result = false;
                        return result;
                    }
                }

            } while (cursor3.moveToNext());
        } else {
            Log.d(" data", "UploadDataRecordsCount isPLDSownRecord NO DATA");
        }

        cursor3.close();
        totalcount = totalcount + count3;
        if (totalcount > 0) {
            result = true;
            return result;
        }
        return result;
    }

    private static boolean isPendingMyTravelData(databaseHelper databaseHelper1) {
        /*Pending My Travel Data*/
        boolean result = false;
        int count4 = 0;
        String searchQuery4 = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        Log.d(" data", "UploadDataRecordsCount isPendingMyTravelData currentDate : " + currentDate);

        searchQuery4 = "select * from mdo_starttravel where Status='0'";
        Cursor cursor4 = databaseHelper1.getReadableDatabase().rawQuery(searchQuery4, null);
        count4 = cursor4.getCount();

        cursor4.moveToFirst();
        if (count4 > 0) {
            do {
                if (cursor4.getString(cursor4.getColumnIndex("startdate")) != null) {
                    String entrydate = cursor4.getString(cursor4.getColumnIndex("startdate")).toString();
                    Log.d(" data", "UploadDataRecordsCount START TRAVEL isPendingMyTravelData FULL Date : " + entrydate);

                    String[] splitStr = entrydate.trim().split("\\s+");
                    Log.d(" data", "UploadDataRecordsCount  START TRAVEL isPendingMyTravelData ONLY DATE : " + splitStr[0]);
                    if (!currentDate.equalsIgnoreCase(splitStr[0])) {
                        Log.d(" data", "UploadDataRecordsCount  START TRAVEL isPendingMyTravelData DATE NOT MATCHING NOTIFY");
                        result = true;
                        return result;
                    } else {
                        Log.d(" data", "UploadDataRecordsCount  START TRAVEL isPendingMyTravelData DATE MATCHING: NO NOTIFICATION");
                        result = false;
                        return result;
                    }

                }

            } while (cursor4.moveToNext());
        } else {
            Log.d(" data", "UploadDataRecordsCount START TRAVEL isPendingMyTravelData NO DATA");
        }

        cursor4.close();

        searchQuery4 = "select * from mdo_addplace where Status='0'";
        cursor4 = databaseHelper1.getReadableDatabase().rawQuery(searchQuery4, null);
        count4 = count4 + cursor4.getCount();

        cursor4.moveToFirst();
        if (cursor4.getCount() > 0) {
            do {
                if (cursor4.getString(cursor4.getColumnIndex("date")) != null) {
                    String entrydate = cursor4.getString(cursor4.getColumnIndex("date")).toString();
                    Log.d(" data", "UploadDataRecordsCount ADD PLACE isPendingMyTravelData FULL Date : " + entrydate);

                    String[] splitStr = entrydate.trim().split("\\s+");
                    Log.d(" data", "UploadDataRecordsCount ADD PLACE isPendingMyTravelData ONLY DATE : " + splitStr[0]);
                    if (!currentDate.equalsIgnoreCase(splitStr[0])) {
                        Log.d(" data", "UploadDataRecordsCount ADD PLACE isPendingMyTravelData DATE NOT MATCHING NOTIFY");
                        result = true;
                        return result;
                    } else {
                        Log.d(" data", "UploadDataRecordsCount ADD PLACE isPendingMyTravelData DATE MATCHING: NO NOTIFICATION");
                        result = false;
                        return result;
                    }
                }

            } while (cursor4.moveToNext());
        } else {
            Log.d(" data", "UploadDataRecordsCount ADD PLACE isPendingMyTravelData NO DATA");
        }

        cursor4.close();

        searchQuery4 = "select * from mdo_endtravel where Status='0'";
        cursor4 = databaseHelper1.getReadableDatabase().rawQuery(searchQuery4, null);
        count4 = count4 + cursor4.getCount();

        cursor4.moveToFirst();
        if (cursor4.getCount() > 0) {
            do {
                if (cursor4.getString(cursor4.getColumnIndex("enddate")) != null) {
                    String entrydate = cursor4.getString(cursor4.getColumnIndex("enddate")).toString();
                    Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingMyTravelData FULL Date : " + entrydate);

                    String[] splitStr = entrydate.trim().split("\\s+");
                    Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingMyTravelData ONLY DATE : " + splitStr[0]);
                    if (!currentDate.equalsIgnoreCase(splitStr[0])) {
                        Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingMyTravelData DATE NOT MATCHING NOTIFY");
                        result = true;
                        return result;
                    } else {
                        Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingMyTravelData DATE MATCHING: NO NOTIFICATION");
                        result = false;
                        return result;
                    }
                }

            } while (cursor4.moveToNext());
        } else {
            Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingMyTravelData NO DATA");
        }
        cursor4.close();

        if (count4 > 0) {
            result = true;
            return result;
        }
        return result;
    }

    private static boolean isPendingFeedbackOld(databaseHelper databaseHelper1) {
        boolean result = false;
        int totalcount = 0;
        String searchQuery = "";
        int count2 = 0;
        searchQuery = "select * from FeedbackTaken where isSyncedStatus='0' and isSubmitted='1' ";
        Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
        count2 = cursor.getCount();

        cursor.moveToFirst();
        if (count2 > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = sdf.format(new Date());
            Log.d(" data", "UploadDataRecordsCount isPendingFeedbackOld currentDate : " + currentDate);

            do {
                if (cursor.getString(cursor.getColumnIndex("Date")) != null) {
                    String entrydate = cursor.getString(cursor.getColumnIndex("Date")).toString();
                    Log.d(" data", "UploadDataRecordsCount isPendingFeedbackOld FULL Date : " + entrydate);

                    String[] splitStr = entrydate.trim().split("\\s+");
                    Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingFeedbackOld ONLY DATE : " + splitStr[0]);
                    if (!currentDate.equalsIgnoreCase(splitStr[0])) {
                        Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingFeedbackOld DATE NOT MATCHING NOTIFY");
                        result = true;
                        return result;
                    } else {
                        Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingFeedbackOld DATE MATCHING: NO NOTIFICATION");
                        result = false;
                        return result;
                    }
                }

            } while (cursor.moveToNext());
        } else {
            Log.d(" data", "UploadDataRecordsCount isPendingFeedbackOld NO DATA");
        }

        cursor.close();
        totalcount = totalcount + count2;
        if (totalcount > 0) {
            result = true;
            return result;
        }
        return result;
    }

    private static boolean isPendingFeedbackNew(databaseHelper databaseHelper1) {
        /*Is pending feedback new*/
        boolean result = false;
        String searchQuery = "";
        //int count2 = 0;
        searchQuery = "select * from trial_feedback_table where isSyncedStatus='0' and isSubmitted='1' ";
        Cursor cursor = databaseHelper1.getReadableDatabase().rawQuery(searchQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String currentDate = sdf.format(new Date());
            Log.d(" data", "UploadDataRecordsCount isPendingFeedbackNew currentDate : " + currentDate);

            do {
                if (cursor.getString(cursor.getColumnIndex("dateOfRating")) != null) {
                    String entrydate = cursor.getString(cursor.getColumnIndex("dateOfRating")).toString();
                    Log.d(" data", "UploadDataRecordsCount isPendingFeedbackNew FULL Date : " + entrydate);

                    String[] splitStr = entrydate.trim().split("\\s+");
                    Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingFeedbackNew ONLY DATE : " + splitStr[0]);
                    if (!currentDate.equalsIgnoreCase(splitStr[0])) {
                        Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingFeedbackNew DATE NOT MATCHING NOTIFY");
                        result = true;
                        return result;
                    } else {
                        Log.d(" data", "UploadDataRecordsCount END TRAVEL isPendingFeedbackNew DATE MATCHING: NO NOTIFICATION");
                        result = false;
                        return result;
                    }
                }

            } while (cursor.moveToNext());
        } else {
            Log.d(" data", "UploadDataRecordsCount isPendingFeedbackNew NO DATA");
        }

        cursor.close();

        if (cursor != null && cursor.getCount() > 0) {
            result = true;
            return result;
        }
        return result;
    }
}
