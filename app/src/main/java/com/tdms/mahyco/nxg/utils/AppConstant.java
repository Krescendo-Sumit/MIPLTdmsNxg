package com.tdms.mahyco.nxg.utils;

import android.net.Uri;

public class AppConstant {

    public static String queryImageUrl = "";
    public static String Imagename="";
    public static String Imagename2="";
    public static String Imagename3="";
    public static String Imagename4="";
    public static Uri imageUri = null;

    public static final String PRE_KEY = "com.newtrail.mahyco.trail.utils.PREFERENCE_FILE_KEY";
    public static String ACCESS_TOKEN_TAG="ACCESSTOKEN";
    public static String ACCESS_TOKEN_EXPIRY="ACCESS_TOKEN_EXPIRY";
    public static String USER_CODE_TAG="USERCODE";
    public static String USER_CODE_PREF="USERCODE";
    public static String USER_PASSWORD_PREF="USERCODE";
    public static String finalmessage="finalmessage";
    public static String LOGIN_DATA="login_data";
    public static String USER_CODE="usercode";
    public static String PASSWORD="password";
    public static String IMEI="imei";
    public static String RANDOM_CODE="random_code";
    public static String TAG_TYPE="tag_type";
    public static String NURSERY_TAG ="nursery_tag";
    public static String FULL_TAG ="full_tag";
    public static String NO_TAG ="no_tag";
    public static String FROM_INTENT ="from_intent";
    public static String TAG_AREA ="tag_area";
    public static String SOWN_STATUS ="upload";
    public static String PLD ="pld";
    public static String NOT_SOWN ="not_sown";
    public static String UPLOAD ="upload";

    public static String LOCAL_CHECK_ISFEED_GIVEN ="local_check_isFeed_given";

    public static String BASE_URL = "https://mipltdmsmobileapp.mahyco.com/";   /*LIVE URL*/
    //public static String BASE_URL = "https://tdmsrcbutest.mahyco.com/";  /*TEST URL NEW RCBU*/
    //public static String BASE_URL = "https://saftdmsrcbutest.mahyco.com/";  /*VAPT URL 2021*/

    /*
    public static String BASE_URL = "https://gistdmsapi.mahyco.com/";  //TEST URL Old

    TDMSRCBU_Test
    DbName :- BreederMobileAppTest
    Url :- https://tdmsrcbutest.mahyco.com

    TDMS Live
    DbName:-  BreederMobileApp
    Url :- https://tdmsmobileapi.mahyco.com

    */

    //public static String BASE_URL = "https://tdmsrcbutest.mahyco.com";

    public static String TOKEN_URL = BASE_URL +"token";
   public static String BREEDER_VERIFY_USER_URL= BASE_URL + "api/registration/breederVerifyUser"; //username is used other places userCode
   public static String INSERT_DOWNLOAD_DATA_URL= BASE_URL + "api/breaderdata/insertDownloadData";
   public static String FEEDBACK_TAKEN_DATA_URL = BASE_URL + "api/breaderdata/FeedbackTaken";
   public static String BREEDER_VERIFY_USER_EMAIL_URL= BASE_URL + "api/breaderdata/breederVerifyUserEmail";
   public static String PLD_NOT_SOWN_URL= BASE_URL + "api/breaderdata/PLDNotSown";
   public static String UPLOAD_IMAGES_URL= BASE_URL + "api/breaderdata/UploadImages";
   public static String GET_MDO_VISIT_PLAN_REPORT= BASE_URL + "api/breaderdata/getmdoVISITPlanReport"; //userId is used other places userCode
   public static String MDO_TRAVEL_DATA= BASE_URL + "api/breaderdata/MDO_TravelData"; //Changed for TDMS app
   public static String BREEDER_FARMER_DATA_INSERT_URL= BASE_URL + "api/breaderdata/BreederFarmerDataInsert";
   public static String TAG_DATA_URL= BASE_URL + "api/breaderdata/TagData";
   public static String UPLOAD_OBSERVATION_URL= BASE_URL +"api/breaderdata/UploadObservation";
   public static String DOWNLOAD_TRIAL_FEEDBACK_URL = BASE_URL + "api/feedback/downloadfeedbackdata";
   public static String UPLOAD_TRIAL_FEEDBACK_URL = BASE_URL +"api/feedback/uploadfeedbackData";
   public static String TDMS_RESET_USER = BASE_URL +"api/registration/passhistorydelete";

    public static String IS_LOCATION_BOX_SHOWN ="is_location_box_show";

    /*Added on 25th May 2021*/
    public static String IS_FEEDBACK_GIVEN ="https://feedbackapi.mahyco.com/api/Feedback/isfeedbackGiven";
    /*Added on 18th June 2021*/
    public static String IS_FEEDBACK_NEW ="https://feedbackapi.mahyco.com/api/Feedback/isfeedbacknew";

    /*Added on 26th Aug 2021*/
    public static String TDMS_GET_YEAR = BASE_URL + "api/registration/getyear";
    public static String TDMS_GET_SEASON = BASE_URL + "api/registration/getseason";

    /*OLD URLS Commented(for eg./////) on 9th Dec 2020 */
    /////public static String TOKEN_URL="https://gishrtdmsapi.mahyco.com/token"; //username is used other places userCode
    /////public static String BREEDER_VERIFY_USER_URL="https://gishrtdmsapi.mahyco.com/api/registration/breederVerifyUser"; //username is used other places userCode
    /////public static String INSERT_DOWNLOAD_DATA_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/insertDownloadData";
    /////public static String FEEDBACK_TAKEN_DATA_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/FeedbackTaken";
    /////public static String BREEDER_VERIFY_USER_EMAIL_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/breederVerifyUserEmail";
    /////public static String PLD_NOT_SOWN_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/PLDNotSown";
    /////public static String UPLOAD_IMAGES_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/UploadImages";
    /////public static String GET_MDO_VISIT_PLAN_REPORT="https://gishrtdmsapi.mahyco.com/api/breaderdata/getmdoVISITPlanReport"; //userId is used other places userCode
    /////public static String MDO_TRAVEL_DATA="https://gishrtdmsapi.mahyco.com/api/breaderdata/MDO_TravelData"; //Changed for TDMS app
    /////public static String BREEDER_FARMER_DATA_INSERT_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/BreederFarmerDataInsert";
    /////public static String TAG_DATA_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/TagData";
    /////public static String UPLOAD_OBSERVATION_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/UploadObservation";

    /////public static String DOWNLOAD_TRIAL_FEEDBACK_URL ="https://gishrtdmsapi.mahyco.com/api/feedback/downloadfeedbackdata";
    /////public static String UPLOAD_TRIAL_FEEDBACK_URL ="https://gishrtdmsapi.mahyco.com/api/feedback/uploadfeedbackData";
    //public static String IS_FEEDBACK_GIVEN ="https://feedbackapi.mahyco.com/api/Feedback/isfeedbackGiven";

    /*UNSED URLS*/
   //public static String BREEDER_VERIFY_USER_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/breederVerifyUser";
   //public static String MDO_TRAVEL_DATA="https://gismobmyactivitynxg.mahyco.com/api/postSeason/MDO_TravelData";
   //public static String GET_MDO_VISIT_PLAN_REPORT="https://gismobmyactivitynxg.mahyco.com/api/postSeason/getmdoVISITPlanReport"; //userId is used other places userCode, this api changed for TDMS app
    public static String HR_DATA_UPLOAD_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/HR_Data_Upload";
   public static String HR_DATA_UPLOADIOS_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/HR_Data_UploadIOS";
   public static String HR_VERIFY_USER_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/HR_VerifyUser";
   public static String HR_VERIFY_USER_EMAIL_URL="https://gishrtdmsapi.mahyco.com/api/breaderdata/HRVerifyUserEmail";
}
