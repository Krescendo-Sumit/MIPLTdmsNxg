package com.tdms.mahyco.nxg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.tdms.mahyco.nxg.ReportDashboard.ReportDashboard;
import com.tdms.mahyco.nxg.TravelManagement.MyTravel;
import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.BaseUtils;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.Prefs;
import com.tdms.mahyco.nxg.utils.RootedDeviceUtils;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView = null;

    public static boolean isBackNavigationAllowed = true;
    String userRole = "";
    String userCode = "";
    DrawerLayout drawerLayout;
    databaseHelper databaseHelper1;
    Toolbar toolbar = null;
    SharedPreferences pref;
    boolean loginState;
    SharedPreferences.Editor editor;
    Prefs mPref;
    public Messageclass msclass;
    Dialog dialog1;
    String currentVersion, latestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        databaseHelper1 = new databaseHelper(this);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        Cursor data = databaseHelper1.fetchusercode();
        loginState = pref.getBoolean("loginState", false);
        msclass = new Messageclass(this);
        if (data.getCount() == 0) {

        } else {
            data.moveToFirst();
            if (data != null) {
                do {

                    userRole = data.getString((data.getColumnIndex("USER_ROLE")));
                    Log.d("USER", "Role : " + userRole);
                    String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                    userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("USER", "userCode : " + userCode);
                } while (data.moveToNext());

            }
            data.close();
            mPref = Prefs.with(this);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        String fromIntent = getIntent().getStringExtra(AppConstant.FROM_INTENT);
        Log.d("HOME", "fromIntent : " + fromIntent);

        if (getIntent() != null && fromIntent != null && fromIntent.equalsIgnoreCase(AppConstant.TAG_AREA)) {
            isBackNavigationAllowed = true;
            FieldAreaTag mainMenu = new FieldAreaTag();
            //FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.FragmentContainer, mainMenu).addToBackStack("fragBack");
            fragmentTransaction.commit();
            //end
        } else if (getIntent() != null && fromIntent != null && fromIntent.equalsIgnoreCase(AppConstant.UPLOAD)) {
            isBackNavigationAllowed = true;
            UploadData mainMenu = new UploadData();
            //FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.FragmentContainer, mainMenu).addToBackStack("fragBack");
            fragmentTransaction.commit();
            //end
        } else {
            //Set Fragment on initial Rahul Dhande
            //MainMenu mainMenu=new MainMenu();
            //isBackNavigationAllowed=false;
            new_main_menu new_main_menu = new new_main_menu();
            fragmentTransaction.replace(R.id.FragmentContainer, new_main_menu);
            fragmentTransaction.commit();
            //end
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Main Menu");
        //getSupportActionBar().setTitle("Main Menu TDMS VER:"+BuildConfig.VERSION_NAME);
        //getSupportActionBar().setTitle("Trial Data Management: " + BuildConfig.VERSION_NAME);
        getSupportActionBar().setTitle("Trial Data Management: " + MainApplication.getVersionName(this));

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_title);
        //navUsername.setText("Trial Data Management: " + BuildConfig.VERSION_NAME);
        navUsername.setText("Trial Data Management: " + MainApplication.getVersionName(this));

        Menu nav_Menu = navigationView.getMenu();


        if (userRole.equals("1")) { //Admin

            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_download).setVisible(true);
            nav_Menu.findItem(R.id.nav_DwdObservation).setVisible(false); /*Removed on 22 April 2021*/
            nav_Menu.findItem(R.id.nav_area).setVisible(true);
            nav_Menu.findItem(R.id.nav_observation).setVisible(true);
            nav_Menu.findItem(R.id.nav_upload).setVisible(true);
            nav_Menu.findItem(R.id.nav_report).setVisible(true);
            nav_Menu.findItem(R.id.nav_my_travel).setVisible(true);
            nav_Menu.findItem(R.id.nav_map).setVisible(true);
            nav_Menu.findItem(R.id.nav_reset_user).setVisible(true); /*Remark : 15 Jan 2021*/
            nav_Menu.findItem(R.id.nav_exit).setVisible(true);


        } else if (userRole.equals("2")) { //SPDM


            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_download).setVisible(true);
            nav_Menu.findItem(R.id.nav_DwdObservation).setVisible(false); /*Removed on 22 April 2021*/
            nav_Menu.findItem(R.id.nav_area).setVisible(true);
            nav_Menu.findItem(R.id.nav_observation).setVisible(true);
            nav_Menu.findItem(R.id.nav_upload).setVisible(true);
            nav_Menu.findItem(R.id.nav_report).setVisible(true);
            nav_Menu.findItem(R.id.nav_my_travel).setVisible(true);
            nav_Menu.findItem(R.id.nav_map).setVisible(false);
            nav_Menu.findItem(R.id.nav_reset_user).setVisible(true); /*Remark : 29 July 2021*/
            nav_Menu.findItem(R.id.nav_exit).setVisible(true);


        } else if (userRole.equals("3")) { //ZPDM


            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_download).setVisible(true);
            nav_Menu.findItem(R.id.nav_DwdObservation).setVisible(false); /*Removed on 22 April 2021*/
            nav_Menu.findItem(R.id.nav_area).setVisible(true);
            nav_Menu.findItem(R.id.nav_observation).setVisible(true);
            nav_Menu.findItem(R.id.nav_upload).setVisible(true);
            nav_Menu.findItem(R.id.nav_report).setVisible(true);
            nav_Menu.findItem(R.id.nav_my_travel).setVisible(true);
            nav_Menu.findItem(R.id.nav_map).setVisible(true);
            nav_Menu.findItem(R.id.nav_reset_user).setVisible(true); /*Remark : 29 July 2021*/
            nav_Menu.findItem(R.id.nav_exit).setVisible(true);


        } else if (userRole.equals("4")) {

            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_download).setVisible(true);
            nav_Menu.findItem(R.id.nav_DwdObservation).setVisible(false);
            nav_Menu.findItem(R.id.nav_area).setVisible(false);
            nav_Menu.findItem(R.id.nav_observation).setVisible(false);
            nav_Menu.findItem(R.id.nav_upload).setVisible(true);
            nav_Menu.findItem(R.id.nav_report).setVisible(true);
            nav_Menu.findItem(R.id.nav_my_travel).setVisible(false);
            nav_Menu.findItem(R.id.nav_map).setVisible(true);
            nav_Menu.findItem(R.id.nav_reset_user).setVisible(false); /*Remark : 15 Jan 2021*/
            nav_Menu.findItem(R.id.nav_exit).setVisible(true);


        } else if (userRole.equals("5")) { //SPDM


            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_download).setVisible(true);
            nav_Menu.findItem(R.id.nav_DwdObservation).setVisible(false);
            nav_Menu.findItem(R.id.nav_area).setVisible(false);
            nav_Menu.findItem(R.id.nav_observation).setVisible(false);
            nav_Menu.findItem(R.id.nav_upload).setVisible(true);
            nav_Menu.findItem(R.id.nav_report).setVisible(true);
            nav_Menu.findItem(R.id.nav_my_travel).setVisible(false);
            nav_Menu.findItem(R.id.nav_map).setVisible(true);
            nav_Menu.findItem(R.id.nav_reset_user).setVisible(false); /*Remark : 15 Jan 2021*/
            nav_Menu.findItem(R.id.nav_exit).setVisible(true);


        } else if (userRole.equals("6")) { //TFA


            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_download).setVisible(true);
            nav_Menu.findItem(R.id.nav_DwdObservation).setVisible(false); /*Removed on 22 April 2021*/
            nav_Menu.findItem(R.id.nav_area).setVisible(true);
            nav_Menu.findItem(R.id.nav_observation).setVisible(true);
            nav_Menu.findItem(R.id.nav_upload).setVisible(true);
            nav_Menu.findItem(R.id.nav_report).setVisible(true);
            nav_Menu.findItem(R.id.nav_my_travel).setVisible(true);
            nav_Menu.findItem(R.id.nav_map).setVisible(false);
            nav_Menu.findItem(R.id.nav_reset_user).setVisible(false); /*Remark : 15 Jan 2021*/
            nav_Menu.findItem(R.id.nav_exit).setVisible(true);

        } else if (userRole.equals("7")) {


            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_download).setVisible(true);
            nav_Menu.findItem(R.id.nav_DwdObservation).setVisible(false); /*Removed on 22 April 2021*/
            nav_Menu.findItem(R.id.nav_area).setVisible(false);
            nav_Menu.findItem(R.id.nav_observation).setVisible(false);
            nav_Menu.findItem(R.id.nav_upload).setVisible(true);
            nav_Menu.findItem(R.id.nav_report).setVisible(true);
            nav_Menu.findItem(R.id.nav_my_travel).setVisible(false);
            nav_Menu.findItem(R.id.nav_map).setVisible(true);
            nav_Menu.findItem(R.id.nav_reset_user).setVisible(false); /*Remark : 15 Jan 2021*/
            nav_Menu.findItem(R.id.nav_exit).setVisible(true);


        } else {

            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_download).setVisible(true);
            nav_Menu.findItem(R.id.nav_DwdObservation).setVisible(false); /*Removed on 22 April 2021*/
            nav_Menu.findItem(R.id.nav_area).setVisible(true);
            nav_Menu.findItem(R.id.nav_observation).setVisible(true);
            nav_Menu.findItem(R.id.nav_upload).setVisible(true);
            nav_Menu.findItem(R.id.nav_report).setVisible(true);
            nav_Menu.findItem(R.id.nav_my_travel).setVisible(true);
            nav_Menu.findItem(R.id.nav_map).setVisible(true);
            nav_Menu.findItem(R.id.nav_reset_user).setVisible(false); /*Remark : 15 Jan 2021*/
            nav_Menu.findItem(R.id.nav_exit).setVisible(true);

        }

        //Sample test crash
        // testCrash();

        if (isUploadPending()) {
            showNotificationForUpload();
        }

        //fetchNewTrialCodeData();

        /*TODO Uncomment when App Feedback Module required.*/
        //showUserFeedbackScreen(userCode); /*Commented on 29th July 2021 as Feedback module not required*/


        Config config = new Config(home.this); //Here the context is passing
        if (config.NetworkConnection()) {
            /*USED TO CHECK New Update on Google Play Store*/
            getCurrentVersion();
            new home.GetLatestVersion().execute(); /*TODO: Remove later for version check*/
        }
    }

    /*private void showUserFeedbackScreen(String userId) {
        Prefs mPref = Prefs.with(home.this);
        boolean isFeedDone = mPref.getBoolean(AppConstant.LOCAL_CHECK_ISFEED_GIVEN, false);
        Log.d("TDMS_NXG", "GET PREF SAVED AS : " + mPref.getBoolean(AppConstant.LOCAL_CHECK_ISFEED_GIVEN, false));
        if (isFeedDone == false) {
            // userfeedback
            String json = "";
            String packageName = "com.newtrail.mahyco.trail";
            Config config = new Config(home.this); //Here the context is passing
            try {
                if (config.NetworkConnection()) {
                    if (userId != null && !userId.isEmpty() && !userId.equals("")) {
                        CommonExecution cxx = new CommonExecution(this);
                        json = cxx.new BreederMasterDataIsFeedGiven(1, userId, packageName).execute().get();
                        Log.d("IsFeed", "User data str :" + json);
                        JSONObject obj = new JSONObject(json);
                        String IsFeedbackGiven = obj.getString("IsFeedbackGiven");
                        if (IsFeedbackGiven.equalsIgnoreCase("False")) {
                           showFeedbackScreen(userId);
                        } else {
                           saveFeedStatusLocally();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("IsFeed", "Msg: " + e.getMessage());
            }
        }
    }*/


    private class GetLatestVersion extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                String urlOfAppFromPlayStore = "https://play.google.com/store/apps/details?id=com.newtrail.mahyco.trail";
                Document doc = Jsoup.connect(urlOfAppFromPlayStore).get();
                latestVersion = doc.getElementsByClass("htlgb").get(6).text();

            } catch (Exception e) {
                Log.d("Msg", e.getMessage());

            }

            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    if (!isFinishing()) { //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                        showUpdateDialog();
                    } else {
                        Log.d("GetLatestVersion","NO POP UP");
                    }
                }
            } else
                // background.start();
                super.onPostExecute(jsonObject);
        }
    }

     private void getCurrentVersion() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            Log.d("Msg", e1.getMessage());
        }
        currentVersion = pInfo.versionName;
    }


    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("A New Update is Available, Upload existing data & Update App");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.newtrail.mahyco.trail")));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //background.start();
            }
        });

        builder.setCancelable(false);
        dialog1 = builder.show();
    }

    private void fetchNewTrialCodeData() {
        Cursor data = databaseHelper1.fetchTrialCodeData();
        loginState = pref.getBoolean("loginState", false);
        msclass = new Messageclass(this);
        if (data.getCount() == 0) {
            Log.d("TrialCodeData", "fetchNewTrialCodeData NO RECORDS TO SHOW");
        } else {
            data.moveToFirst();
            int i = 0;
            if (data != null) {
                do {
                    i++;
                    String TRIL_CODE = data.getString((data.getColumnIndex("TRIL_CODE")));
                    String SPDM2 = data.getString((data.getColumnIndex("SPDM2")));
                    String SPDM1 = data.getString((data.getColumnIndex("SPDM1")));
                    String product = data.getString((data.getColumnIndex("Product")));
                    String T_YEAR = data.getString((data.getColumnIndex("T_YEAR")));
                    String trialtype = data.getString((data.getColumnIndex("Trail_Type")));
                    String nursery = data.getString((data.getColumnIndex("nursery")));
                    Log.d("TrialCodeData", "fetchNewTrialCodeData Entry : " + i + " TRIL_CODE: " + TRIL_CODE + " SPDM2:" + SPDM2 + " SPDM1:" + SPDM1 + " Product:" + product + " T_YEAR:" + T_YEAR + " Trial_Type:" + trialtype + " nursery:" + nursery);

                } while (data.moveToNext());

            }
            data.close();
            mPref = Prefs.with(this);
        }
    }


    private boolean isUploadPending() {
        boolean result = false;
        result = UploadDataRecordsCount.isUploadRecordsPending(this, databaseHelper1);
        return result;
    }

    private void showNotificationForUpload() {
        //Toast.makeText(this,"Show notification",Toast.LENGTH_SHORT).show();
        String CHANNEL_ID = "101";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, home.class);
        intent.putExtra(AppConstant.FROM_INTENT, AppConstant.UPLOAD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Trial Data Management")
                .setContentText("Upload Pending Data to Server")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(101, builder.build());

    }


    /*Commented on 29th July 2021, Uncomment when App Feedback Module required*/
    /*private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                boolean isFeedbackGiven = intent.getBooleanExtra("IS_FEEDBACK_GIVEN", false);
                if (isFeedbackGiven == true) {
                    //mDatabase.insertUserFeedback(userId, "1");
                    //Toast.makeText(home.this, "IS_FEEDBACK_GIVEN: " + isFeedbackGiven, Toast.LENGTH_LONG).show();
                    Log.d("TDMS_NXG", "IS_FEEDBACK_GIVEN : " + isFeedbackGiven);
                    if (isFeedbackGiven) {
                        saveFeedStatusLocally();
                    }
                }
            }
        }
    };*/

    /*Commented on 29th July 2021, Uncomment when App Feedback Module required*/
    /*private void saveFeedStatusLocally() {
        Prefs mPref = Prefs.with(home.this);
        mPref.saveBoolean(AppConstant.LOCAL_CHECK_ISFEED_GIVEN, true);
        Log.d("TDMS_NXG", "PREF SAVED AS : " + mPref.getBoolean(AppConstant.LOCAL_CHECK_ISFEED_GIVEN, false));
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*Commented on 29th July 2021, Uncomment when App Feedback Module required*/
        /*try {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            Log.d("Receiver", "Registered : " + e.getMessage());
        }*/
    }

    /*Commented on 29th July 2021, Uncomment when App Feedback Module required*/
    /*private void showFeedbackScreen(String userCode) {
        String packageName = "com.newtrail.mahyco.trail"; //BuildConfig.APPLICATION_ID;
        DialogFeedback feedbackDialog = new DialogFeedback(home.this, packageName, userCode);
        feedbackDialog.showFeedbackDialog();
        IntentFilter filter = new IntentFilter();
        filter.addAction("FeedbackResponse");
        registerReceiver(receiver, filter);
    }*/

    void testCrash() {
        try {
            Button crashButton = new Button(this);
            crashButton.setText("Crash!");
            crashButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    throw new RuntimeException("Test Crash"); // Force a crash
                }
            });

            addContentView(crashButton, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (Exception e) {
            Log.d("MSG", "Message:" + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!isBackNavigationAllowed) {
                logOutAlert().show();
            } else {
                super.onBackPressed();
            }
        }

    }

    private AlertDialog logOutAlert() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit from app?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        loginState = false;
                        editor.putBoolean("loginState", loginState);
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            //Set Fragment on initial Rahul Dhande
            // MainMenu mainMenu = new MainMenu();
            // android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            // fragmentTransaction.replace(R.id.FragmentContainer,mainMenu).addToBackStack("fragBack");
            // fragmentTransaction.commit();

            new_main_menu new_main_menu = new new_main_menu();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.FragmentContainer, new_main_menu).addToBackStack("fragBack");
            fragmentTransaction.commit();
            //end
        } else if (id == R.id.nav_download) {
            //Set Fragment on initial Rahul Dhande
            isBackNavigationAllowed = true;
            DownloadData mainMenu = new DownloadData();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.FragmentContainer, mainMenu).addToBackStack("fragBack");
            fragmentTransaction.commit();

            //end
        } else if (id == R.id.nav_DwdObservation) {
            //Set Fragment on initial Rahul Dhande
            isBackNavigationAllowed = true;
            new_Download_Observation mainMenu = new new_Download_Observation();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.FragmentContainer, mainMenu).addToBackStack("fragBack");
            fragmentTransaction.commit();

            //end
        } else if (id == R.id.nav_area) {
            //Set Fragment on initial Rahul Dhande
            isBackNavigationAllowed = true;
            FieldAreaTag mainMenu = new FieldAreaTag();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.FragmentContainer, mainMenu).addToBackStack("fragBack");
            fragmentTransaction.commit();

            //end
        } else if (id == R.id.nav_observation) {
            //Set Fragment on initial Rahul Dhande
            isBackNavigationAllowed = true;
            Observation mainMenu = new Observation();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.FragmentContainer, mainMenu).addToBackStack("fragBack");
            fragmentTransaction.commit();

            //end
        } else if (id == R.id.nav_validation) {
            isBackNavigationAllowed = true;
            //Set Fragment on initial Rahul Dhande
            Validation mainMenu = new Validation();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.FragmentContainer, mainMenu).addToBackStack("fragBack");
            fragmentTransaction.commit();

            //end
        } else if (id == R.id.nav_upload) {
            isBackNavigationAllowed = true;
            //Set Fragment on initial Rahul Dhande
            UploadData mainMenu = new UploadData();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.FragmentContainer, mainMenu).addToBackStack("fragBack");
            fragmentTransaction.commit();
            //end
        } else if (id == R.id.nav_report) {
            //Set Fragment on initial Rahul Dhande
            Intent i = new Intent(this, ReportDashboard.class);
            startActivity(i);
            //end
        } else if (id == R.id.nav_my_travel) {
            //Set Fragment on initial Rahul Dhande
            Intent i = new Intent(this, MyTravel.class);
            startActivity(i);
            //end
        } else if (id == R.id.nav_db) {
            Intent i = new Intent(this, AndroidDatabaseManager.class);
            startActivity(i);
        } else if (id == R.id.nav_map) {
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_reset_user) {
            showResetUserDialog();
        } else if (id == R.id.nav_exit) {
            // editor.putString("UserID", null);
            //Toast.makeText(this,"Logout here",Toast.LENGTH_SHORT).show();
            //databaseHelper1.deleledata("UserMaster", "");
            /*mPref.removeAll();
            editor.clear();
            editor.commit();
            mPref.save(AppConstant.USER_CODE_PREF, null);
            mPref.save(AppConstant.USER_PASSWORD_PREF, null);
            mPref.save(AppConstant.finalmessage, null);
            mPref.save(AppConstant.ACCESS_TOKEN_TAG, null);
            editor.putString("Displayname", null);

            //Set Fragment on initial Rahul Dhande
            Logout mainMenu = new Logout();
            Intent openIntent = new Intent(this, login.class);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(openIntent);*/
            //end

            //LOGOUT START

            try {
                Prefs mPref = Prefs.with(home.this);
                CommonExecution cxx = new CommonExecution(home.this);
                String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                String json = "";
                if (userCodeDecrypt != null && !userCodeDecrypt.equals("")) {
                    json = cxx.new ResetUserLogin(1, userCodeDecrypt).execute().get();
                } else {
                    Toast.makeText(home.this, "Invalid User Data", Toast.LENGTH_SHORT).show();
                }
                Log.d("MSG", "ON LOGOUT From menu DATA response:" + json);


            } catch (Exception e) {
                Log.d("MSG", "DATA:" + e.getMessage());
            }

            databaseHelper databaseHelper1 = new databaseHelper(home.this);
            String searchQuery1 = "delete from UserMaster ";
            databaseHelper1.runQuery(searchQuery1);

            SharedPreferences pref;
            SharedPreferences.Editor editor;
            pref = home.this.getSharedPreferences("MyPref", 0); // 0 - for private mode
            editor = pref.edit();
            editor.clear();
            editor.commit();

            //Clear all data
            Prefs mPref = Prefs.with(home.this);
            mPref.removeAll();
            editor.clear();
            editor.commit();
            mPref.save(AppConstant.USER_CODE_PREF, null);
            mPref.save(AppConstant.USER_PASSWORD_PREF, null);
            mPref.save(AppConstant.finalmessage, null);
            mPref.save(AppConstant.ACCESS_TOKEN_TAG, null);
            mPref.save(AppConstant.ACCESS_TOKEN_EXPIRY, null);
            editor.putString("Displayname", null);

            //Logout logout1 = new Logout();
            Intent openIntent = new Intent(home.this, login.class);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(openIntent);

            //LOGOUT END
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new RootedDeviceUtils().isDeviceRooted()) {
            //Toast.makeText(home.this,"Device rooted",Toast.LENGTH_LONG).show();
            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
        } else {
            //Toast.makeText(home.this,"Device NOT rooted",Toast.LENGTH_LONG).show();
            Log.d("Rooted", "Device NOT rooted");
        }
    }

    public void showAlertDialogAndExitApp(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(home.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialog.show();
    }

    public void showResetUserDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        dialog.getWindow().setLayout(width, height);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Reset Employee Login Data");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_alert_reset_user);
        final EditText edtEmpCode = (EditText) dialog.findViewById(R.id.edt_emp_code);
        final Button btnResetUser = (Button) dialog.findViewById(R.id.btn_alert_reset);
        final Button btnBack = (Button) dialog.findViewById(R.id.btn_alert_back1);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        btnResetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(home.this,"Reset User",Toast.LENGTH_SHORT).show();
                String codeEmp = edtEmpCode.getText().toString();
                if (checkEmpCode(codeEmp)) {
                    try {
                        AlertDialog resetDataBox = null;
                        AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
                        builder.setTitle("Exit");
                        builder.setMessage("Are you sure you want to reset user data");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String strResponse = callResetAPI(codeEmp);

                                if (!strResponse.equals(null) && !strResponse.equals("")) {
                                    Log.d("ResetUserLogin", "User data str :" + strResponse);
                                    try {
                                        JSONObject obj = new JSONObject(strResponse);
                                        String msg = obj.getString("msg");
                                        msclass.showMessage(msg);
                                        dialog.dismiss();
                                        dialog.cancel();
                                        //Toast.makeText(home.this, msg, Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Log.d("Data", "MSG: " + e.getMessage());
                                    }
                                }
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dialog.cancel();
                            }
                        });
                        resetDataBox = builder
                                .create();
                        resetDataBox.show();

                    } catch (Exception e) {
                        Log.d("DATA", "Msg : " + e.getMessage());
                    }
                } else {
                    edtEmpCode.setError("Please enter valid user name ");
                }
            }
        });
        dialog.show();
    }

    boolean checkEmpCode(String codeEmp) {
        boolean result = false;
        String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
        String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
        if (codeEmp.length() == 0 || codeEmp.length() < 4) {
            result = false;
        } else if (codeEmp.equalsIgnoreCase(userCodeDecrypt)) {
            result = false;
        } else if (!BaseUtils.isValidUsername(codeEmp)) {
            result = false;
        } else {
            result = true;
        }
        Log.d("checkEmpCode", "Name : " + codeEmp + " Result:" + result);
        return result;
    }

    String callResetAPI(String codeEmp) {
        try {
            CommonExecution cxx = new CommonExecution(this);
            String json = "";
            if (codeEmp != null && !codeEmp.equals("")) {
                json = cxx.new ResetUserLogin(1, codeEmp).execute().get();
            } else {
                Toast.makeText(home.this, "Invalid User Data", Toast.LENGTH_SHORT).show();
            }
            Log.d("MSG", "DATA respone:" + json);
            return json;
        } catch (Exception e) {
            Log.d("MSG", "DATA:" + e.getMessage());
        }
        return "";
    }


}
