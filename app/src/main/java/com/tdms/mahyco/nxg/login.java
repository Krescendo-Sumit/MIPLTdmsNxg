package com.tdms.mahyco.nxg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.GetAuthToken;
import com.tdms.mahyco.nxg.utils.Prefs;
import com.tdms.mahyco.nxg.utils.RootedDeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class login extends AppCompatActivity {
    private TextView txtView, txtentermobile, txtEnterotp, lbltype, txtRegister, txtUpdate, txtForget;
    private EditText otp;
    private CardView btnLogin, btnRegistr, btnUpdate;
    String type = null;

    ProgressDialog dialog;
    public String langcode = "";
    SharedPreferences pref;
    String imeiNumber;
    SharedPreferences.Editor editor;
    ProgressDialog progressdialog, bar;
    private ProgressDialog mProgressDialog;
    SharedPreferences locdata;
    public Messageclass msclass;
    private Boolean exit = false;
    public CommonExecution cx;
    databaseHelper databaseHelper1;
    Config config;
    public String userCode;
    public String userMail;
    public static final int Progress_Dialog_Progress = 0;
    Dialog dialog1;
    private BroadcastReceiver receiver;
    private long enqueue;
    private DownloadManager dm;
    String imeicheck;
    boolean loginState;

    private static final int RC_SIGN_IN = 1000;
    GoogleSignInClient mGoogleSignInClient;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    Prefs mPref;

    CountDownTimer cTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = Prefs.with(this);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        MainApplication.LOGIN_ATTEMPT = 0;


        // Bundle extras = getIntent().getExtras();
        // if (extras!= null)
        //{
        //   type= extras.getString("TYPE");
        //}
        //lbltype=(TextView)findViewById(R.id.lbltype);
        //lbltype.setText(type);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        btnLogin = (CardView) findViewById(R.id.btnLogin);
        btnRegistr = (CardView) findViewById(R.id.btnRegister);
        btnUpdate = (CardView) findViewById(R.id.btnUpdate);
        txtentermobile = (TextView) findViewById(R.id.txtentermobile);
        txtEnterotp = (TextView) findViewById(R.id.txtEnterotp);

        txtRegister = (TextView) findViewById(R.id.txtRegister);
        txtUpdate = (TextView) findViewById(R.id.txtUpdate);
        txtForget = (TextView) findViewById(R.id.txtForget);
        msclass = new Messageclass(this);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        cx = new CommonExecution(this);
        config = new Config(this); //Here the context is passing
        databaseHelper1 = new databaseHelper(this);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        showLocationDialog();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOGIN_ATTEMPT", MainApplication.LOGIN_ATTEMPT + " button click");
                if (MainApplication.LOGIN_ATTEMPT < 3) {
                    if (validation() == true) {
                        LoginRequest();
                    }
                } else {
                    btnLogin.setEnabled(false);
                    msclass.showMessage("Account locked, Please try after sometime");
                    startTimerForAccountLock();
                }
            }
        });
        /* Open Login activity for Register BP,SA,SO*/
        // txtView= (TextView) findViewById(R.id.textView8);
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenRegister();
            }
        });
        txtForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenForget();
            }
        });
        Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount() == 0) {

        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    //userCode = data.getString((data.getColumnIndex("user_code")));
                    String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                    userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("userCode", "userCode" + userCode);
                } while (data.moveToNext());

            }
            data.close();
        }

        //For Update App
        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent promptInstall = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.newtrail.mahyco.trail"));
                startActivity(promptInstall);
                // downloadcall();
            }
        });


        /*TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        imeiNumber = tm.getDeviceId();
        */
    }

    private void showLocationDialog() {
        String isLocationBoxShown = mPref.getString(AppConstant.IS_LOCATION_BOX_SHOWN, "0");
        Log.d("isLocationBoxShown","READ isLocationBoxShown value :: "+isLocationBoxShown);
        if (isLocationBoxShown.equalsIgnoreCase("0")) {
            AlertDialog.Builder locationDlg = new AlertDialog.Builder(login.this);
            locationDlg.setTitle("Use Your Location");
            ImageView showImage = new ImageView(login.this);
            showImage.setImageResource(R.drawable.location_msg);
            locationDlg.setView(showImage);
            locationDlg.setCancelable(false);
            locationDlg.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    mPref.save(AppConstant.IS_LOCATION_BOX_SHOWN, "1");
                    Log.d("isLocationBoxShown","SAVE isLocationBoxShown value :: "+mPref.getString(AppConstant.IS_LOCATION_BOX_SHOWN, "0"));
                    //requestLocationAccess();
                    checkAndRequestPermissions();
                }
            });
            locationDlg.show();
        }
    }

    private void startTimerForAccountLock() {
        Log.d("LOGIN_ATTEMPT", MainApplication.LOGIN_ATTEMPT + " start timer");
        final TextView tvTimer = (TextView) findViewById(R.id.tv_timer);
        tvTimer.setVisibility(View.VISIBLE);
        cTimer = new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {

                long sec = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                Log.d("Click", "onTick: " + sec);
                String text = String.format(Locale.getDefault(), "Account locked %02d min: %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                tvTimer.setText(text);
            }

            public void onFinish() {
                btnLogin.setEnabled(true);
                MainApplication.LOGIN_ATTEMPT = 0;
                tvTimer.setText("Ready for login");
                tvTimer.setVisibility(View.GONE);
                Log.d("Login", "MyApplication.LOGIN_ATTEMPT : " + MainApplication.LOGIN_ATTEMPT);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean validation() {
        boolean flag = true;
        if (txtentermobile.getText().length() == 0) {
            msclass.showMessage("Please enter user name ");
            return false;

        }
        if (txtEnterotp.getText().length() == 0) {
            msclass.showMessage("Please  enter password");
            return false;
        }
        if (!isValidUsernamePass(txtentermobile.getText().toString(), txtEnterotp.getText().toString())) {
            MainApplication.LOGIN_ATTEMPT = MainApplication.LOGIN_ATTEMPT + 1;
            msclass.showMessage("User name and password not correct ,please try again");
            return false;
        }
        return true;
    }

    private boolean isValidUsernamePass(String name, String password) {
        boolean result = false;
        String expressionUserName = "^[a-zA-Z0-9]{2,25}$";

        //"([a-zA-Z0-9]$)"; --^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\s).*$
        // String expressionPassord = "(?=.*[!@#$%^&*-])(?=.*[0-9])(?=.*[A-Z]).{8,20}$";
        String expressionPassord = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

        String inputStr = name;
        String inputPass = password;
        Pattern pattern = Pattern.compile(expressionUserName);//, Pattern.CASE_INSENSITIVE);
        Pattern patterninputPass = Pattern.compile(expressionPassord);//, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        Matcher matcherPass = patterninputPass.matcher(inputPass);
        if (matcher.matches())
        //if (usernameValidator.validate(name))
        {
            if (matcherPass.matches()) {
                result = true;
            }
        }
        return result;
    }

    public void OpenLogin() {
        Intent openIntent = new Intent(this, new_main_menu.class);
        startActivity(openIntent);
    }

    public void OpenRegister() {
        Intent openIntent = new Intent(this, register_user.class);
        startActivity(openIntent);
    }

    public void OpenForget() {
        Intent openIntent = new Intent(this, forget_password.class);
        startActivity(openIntent);
    }

    public void LoginRequest() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String username = txtentermobile.getText().toString().trim();
        String pass = txtEnterotp.getText().toString().trim();
        // String lang = spnlanguage.getSelectedItem().toString().trim();
        dialog.setMessage("Loading. Please wait...");
        dialog.show();
        logincheck(username.trim(), pass.trim());
        //  new LoginReq().execute(SERVER,username,pass);
    }

    private void logincheck(String username, String pass) {
//        if (config.NetworkConnection()) {
        try {
            String encryptedUserCode = EncryptDecryptManager.encryptStringData(username.trim());
            String encryptedPassword = EncryptDecryptManager.encryptStringData(pass.trim());
            String searchQuery = "select  *  from UserMaster where User_pwd='" + encryptedPassword + "' and user_code='" + encryptedUserCode + "'";
            //  String searchQuery = "SELECT  *  FROM UserMaster  ";
            SQLiteDatabase database = new databaseHelper(this).getReadableDatabase();
            Cursor cursor = database.rawQuery(searchQuery, null);
            //Cursor cursor = mDatabase.getReadableDatabase().rawQuery(searchQuery, null);
            int count = cursor.getCount();
            Log.d("VAPT", "UserMaster countL------------: " + count);

            if (count > 0) {

                String token = "";
                String fmsg = "";
                String expires = "";
                Log.d("VAPT", "Calling GET TOKEN API URL------------: " + AppConstant.TOKEN_URL);
                Log.d("VAPT", "username: " + username);
                Log.d("VAPT", "pass: " + pass);
                imeiNumber = pref.getString("uuid","");
                Log.d("VAPT", "imeiNumber: " + imeiNumber);
                String strAuthToken = new GetAuthToken(username, pass.trim(), imeiNumber).execute().get(); //,pass.trim()
                JSONObject jsonObject = new JSONObject(strAuthToken);
                Log.d("VAPT", "TOKEN Response: " + strAuthToken);
                if (jsonObject.has("access_token")) {
                    token = jsonObject.get("access_token").toString();
                    fmsg = jsonObject.get("finalmessage").toString();
                    expires = jsonObject.get("expiretimeinmilli").toString();
                    Log.d("VAPT", "access_token: " + token);
                    Log.d("VAPT", "finalmessage: " + fmsg);
                    Log.d("VAPT", "expires: " + expires);
                    Log.d("token", token);
                    if (token != null && token != "") {
                        mPref.save(AppConstant.finalmessage, fmsg);
                        mPref.save(AppConstant.ACCESS_TOKEN_TAG, token);
                        mPref.save(AppConstant.ACCESS_TOKEN_EXPIRY, expires);
                    } else {
                        Log.d("VAPT", "NULL access_token");
                    }
                } else {
                    Log.d("VAPT", "NO access_token");
                }

                // msclass.showMessage(cursor.getString(1));

                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    //editor.putString("UserID", cursor.getString(1));
                    //editor.putString("Pass", cursor.getString(0));
                    /*String encryptedUserCode1 = EncryptDecryptManager.encryptStringData(cursor.getString(1));
                    String encryptedPassword1 = EncryptDecryptManager.encryptStringData(cursor.getString(0));*/

                    mPref.save(AppConstant.USER_CODE_PREF, cursor.getString(1));
                    mPref.save(AppConstant.USER_PASSWORD_PREF, cursor.getString(0));

                    editor.putString("Displayname", cursor.getString(3));
                    //editor.putString("IMEI", getDeviceIMEI());
                    editor.commit();
                    cursor.moveToNext();
                }

                cursor.close();
                dialog.dismiss();
                Bundle data = new Bundle();
                data.putString(AppConstant.USER_CODE, txtentermobile.getText().toString());
                data.putString(AppConstant.PASSWORD, txtEnterotp.getText().toString());
                data.putString(AppConstant.IMEI, imeiNumber);
                txtEnterotp.setText("");
                txtentermobile.setText("");
                Intent intent = new Intent(login.this, home.class);
                intent.putExtra(AppConstant.LOGIN_DATA, data);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                MainApplication.LOGIN_ATTEMPT = MainApplication.LOGIN_ATTEMPT + 1;
                msclass.showMessage("User name and password not correct ,please try again");
                dialog.dismiss();
            }
        } catch (Exception ex) {
            Log.d("Msg", "VATPT Err : " + ex.getMessage());
            msclass.showMessage(this.getString(R.string.action_api_exception));
        }
        dialog.dismiss();

    }



    private boolean checkAndRequestPermissions() {
       /* int storagePermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);*/
        /*int READ_PHONE_STATE = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE);*/

        int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        int USE_FINGERPRINT = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.USE_FINGERPRINT);
        int INTERNET = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET);

        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_SMS);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (INTERNET != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }

        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);

        /*if (READ_PHONE_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }*/

        if (USE_FINGERPRINT != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.USE_FINGERPRINT);
        }

        /*if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }*/

        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.SEND_SMS);
        }
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECEIVE_SMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.d("rht", "onStart: " + account);
        //updateUI(account);
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                // Preferences.saveBool(getApplicationContext(), Preferences.KEY_IS_LOGGED_IN, true);


                Map<String, Object> user = new HashMap<>();
                user.put("email", account.getEmail());
                user.put("familyName", account.getFamilyName());
                user.put("fullName", account.getDisplayName());
                user.put("givenName", account.getGivenName());
                //user.put("loginTime", getDate());


                // txtData.setText(account.getDisplayName() + "::::" + account.getId() + "::::" + account.getIdToken() + "::::" + account.getEmail());
                // Toast.makeText(this, "acc::"+account.toString(), Toast.LENGTH_SHORT).show();
            }
            Log.d("rht", "handleSignInResult: " + account.toString());
            Toast.makeText(this, "acc::" + account.toString(), Toast.LENGTH_SHORT).show();


            if (!(account.toString().isEmpty())) {

                Log.d("rht", "mailUser: " + account.getEmail().toString());
                Log.d("rht", "Error: " + account.getServerAuthCode());

                // String email="rahul.dhande@mahyco.com";
                // UserRegisterGoogle(email);
                UserRegisterGoogle(account.getEmail().toString());

            } else {
                msclass.showMessage("Something went Wrong");
            }
            // Signed in successfully, show authenticated UI.
            // updateUI(account);
        } catch (ApiException e) {
            e.getMessage();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("rht", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
            Log.d("rht", "handleSignInResult: " + e.getMessage());
        }
    }


    private boolean UserRegisterGoogle(String userMail) {
        if (config.NetworkConnection()) {
            dialog.setMessage("Loading....");
            dialog.show();
            String str = null;
            boolean fl = false;
            try {
                str = cx.new BreederMasterGoogleData(userMail).execute().get();

                if (str.contains("False")) {
                    msclass.showMessage("You are not authorised. Kindly register");
                    dialog.dismiss();
                } else {
                    JSONObject object = new JSONObject(str);
                    Log.d("", "DataWe" + object);
                    JSONArray jArray = object.getJSONArray("Table");
                    Log.d("", "DataWe" + jArray);

                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject jObject = jArray.getJSONObject(0);

                        Log.d("", "DataWe" + jObject);
//                        if(jObject.getString("IMEI")!=(imeiNumber)) {
//
//                            msclass.showMessage("Already Registered on other device");//show specific response msg from api
//                            dialog.dismiss();
//
//                        }
                        databaseHelper1.deleledata("UserMaster", "");
                        fl = databaseHelper1.InsertUserRegistrationGoogle(jObject.getString("USER_CODE").toString().toUpperCase(), jObject.getString("USER_NAME").toString(), jObject.getString("USER_PWD").toString(), jObject.getString("USER_ROLE").toString(), jObject.getString("IMEI").toString(), userMail);
                        editor.putString("Displayname", jObject.getString("USER_NAME").toString());

                        editor.putString("USER_IMEINO", jObject.getString("IMEI"));


                        //}
                        //else
                        // {
                        //    msclass.showMessage("This user mobile device IMEI No and User id not match .please check IMEI_no");
                        //    dialog.dismiss();
                        ////    return false;
                        //}
                    }
                    String userCodeEncrypt = EncryptDecryptManager.encryptStringData(txtentermobile.getText().toString());
                    editor.putString("USER_CODE", userCodeEncrypt);
                    editor.commit();

                    if (fl == true) {
                        //    msclass.showMessage("User Registration successfully");
                        dialog.dismiss();
                        loginState = true;
                        txtEnterotp.setText("");
                        txtentermobile.setText("");
                        editor.putBoolean("loginState", loginState);
                        Intent intent = new Intent(getApplicationContext(), home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                        // finish();

                    } else {
                        msclass.showMessage("Registration  not done");
                        return false;
                    }


                }
            } catch (InterruptedException e) {
                Log.d("Msg", e.getMessage());
            } catch (ExecutionException e) {
                Log.d("Msg", e.getMessage());
            } catch (JSONException e) {
                Log.d("Msg", e.getMessage());
            }

        } else {
            msclass.showMessage("Internet network not available.");
            dialog.dismiss();
            return false;
        }
        return true;
    }

    protected void onResume() {
        super.onResume();
        if (new RootedDeviceUtils().isDeviceRooted()) {
            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
        }
    }

    public void showAlertDialogAndExitApp(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(login.this).create();
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
}
