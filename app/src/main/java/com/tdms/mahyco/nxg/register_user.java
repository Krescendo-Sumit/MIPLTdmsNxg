package com.tdms.mahyco.nxg;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.BaseUtils;
import com.tdms.mahyco.nxg.utils.DeviceIDUtils;
import com.tdms.mahyco.nxg.utils.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
//import com.trial.mahyco.trail.Config;


public class register_user extends AppCompatActivity {
    private TextView txtView, txtentermobile, txtEnterotp, lbltype;
    private EditText otp;
    private CardView btnLogin, btnOtp;
    String type = null;
    databaseHelper databaseHelper1;
    //public Button btnLogin;
    ProgressDialog dialog;
    public Messageclass msclass;
    public CommonExecution cx;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Config config;
    String imeiNumber;
    TelephonyManager tel;
    Prefs mPref;

    public static final int REQUEST_CODE_PHONE_STATE_READ = 100;
    //private int checkedPermission = PackageManager.PERMISSION_DENIED;
    TextView tvRequestObj,lblReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        getSupportActionBar().setTitle("Register User");

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

        Log.d("VAPT_IMEI", "imei: " + imeiNumber);*/

        btnLogin = (CardView) findViewById(R.id.btnLogin);
        txtentermobile = (TextView) findViewById(R.id.txtentermobile);
        txtEnterotp = (TextView) findViewById(R.id.txtEnterotp);
        tvRequestObj = (TextView) findViewById(R.id.tv_request_obj);
        lblReason = (TextView) findViewById(R.id.lbl_reason);
        msclass = new Messageclass(this);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        cx = new CommonExecution(this);
        config = new Config(this); //Here the context is passing
        databaseHelper1 = new databaseHelper(this);
        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

       /* if (Build.VERSION.SDK_INT >= 23 && checkedPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            checkedPermission = PackageManager.PERMISSION_GRANTED;
        }
        int readPhone = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        if(readPhone == PackageManager.PERMISSION_GRANTED){
            checkedPermission = PackageManager.PERMISSION_GRANTED;
        }*/

        //getUUID();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation() == true) {
                    dialog.setMessage("Loading. Please wait...");
                    dialog.show();

                    String searchQuery1 = "delete from UserMaster ";
                    databaseHelper1.runQuery(searchQuery1);
                    UserRegisteration();
                }

            }
        });
        mPref = Prefs.with(this);


    }

    public void getUUID(String userCode) {
        Log.d("VAPT","getUUID DevicePref showDeviceInfo");
        String deviceId = DeviceIDUtils.getDeviceIdData(this,userCode);
        imeiNumber = deviceId;
        editor.putString("uuid", deviceId);
        editor.apply();
        Log.d("VAPT", "getUUID DevicePref ID/IMEI : DEVICE_ID : " + pref.getString("uuid",""));
    }

    private boolean validation() {
        boolean flag = true;
        if (txtentermobile.getText().length() == 0) {
            msclass.showMessage("Please enter user name ");
            return false;

        }
        if (txtEnterotp.getText().length() == 0) {
            msclass.showMessage("Please enter password");
            return false;
        }
        if(!BaseUtils.isValidUsernamePass(txtentermobile.getText().toString(),txtEnterotp.getText().toString())) {
            msclass.showMessage("Please set secure password") ;
            return false;
        }
        return true;
    }

    private boolean UserRegisteration() {
        if (config.NetworkConnection()) {
            dialog.setMessage("Loading....");
            dialog.show();
            String str = null;
            boolean fl = false;
            String token = "";
            String fmsg = "";
            try {
                getUUID(txtentermobile.getText().toString());
                Log.d("VAPT", "User code:" + txtentermobile.getText().toString());
                Log.d("VAPT", "User password:" + txtEnterotp.getText().toString());
                Log.d("VAPT", "Imei number:" + imeiNumber);

                String username = txtentermobile.getText().toString();
                String password = txtEnterotp.getText().toString();

                //Removed later  11 Aug 2020
                //Log.d("VAPT", "Call API Get Auth Token----------------------- ");
                /*String strAuthToken = new GetAuthToken(username, password, imeiNumber).execute().get();
                Log.d("VAPT", "Response Auth Token-------"+strAuthToken);

                JSONObject jsonObject = new JSONObject(strAuthToken);
                if (jsonObject.has("access_token")) {
                    token = jsonObject.get("access_token").toString();
                    fmsg = jsonObject.get("finalmessage").toString();
                    Log.d("token", token);
                    if (token != null && token != "") {
                        Log.d("VAPT", "access_token :" + token);
                        Log.d("VAPT", "finalmessage :" + fmsg);
                        mPref.save(AppConstant.finalmessage, fmsg);
                        mPref.save(AppConstant.ACCESS_TOKEN_TAG, token);
                    }
                }*/

                Log.d("VAPT", "Call API Registration----------------------- ");
                String data = "Unique ID : "+imeiNumber+"\n"+"User ID:"+txtentermobile.getText().toString()+"\n"+ "Password:"+txtEnterotp.getText().toString();
                tvRequestObj.setText(data);

                tvRequestObj.setVisibility(View.GONE);/*make it visible, when require response*/
                lblReason.setVisibility(View.GONE);

                str = cx.new BreederMasterData(1, txtentermobile.getText().toString(), txtEnterotp.getText().toString(), imeiNumber.trim()).execute().get();

                lblReason.setText("URL : "+AppConstant.BREEDER_VERIFY_USER_URL+"\n"+"Response:"+str);

                if (str.contains("true")) {
                    String usernameNew = "", roleNew = "";
                    JSONObject jsonObjectNew = new JSONObject(str);
                    if (jsonObjectNew.has("username")) {
                        usernameNew = jsonObjectNew.getString("username").toString().toUpperCase();
                    }
                    if (jsonObjectNew.has("userRole")) {
                        roleNew = jsonObjectNew.getString("userRole").toString().toUpperCase();
                    }
                    databaseHelper1.deleledata("UserMaster", "");
                    fl = databaseHelper1.InsertUserRegistrationNew(usernameNew, roleNew,txtEnterotp.getText().toString(),txtentermobile.getText().toString());
                    if (fl == true) {
                        dialog.dismiss();
                        //msclass.showMessage("User Registration successfully");
                        Toast.makeText(this,"User Registration successfully",Toast.LENGTH_SHORT).show();
                        txtEnterotp.setText("");
                        txtentermobile.setText("");
                        Intent intent = new Intent(getApplicationContext(), login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;
                    }
                }
                else {
                    dialog.dismiss();
                    //BaseUtils.writeLog();
                    msclass.showMessage("Registration not done");
                    return false;
                }

                /*if(str.contains("False"))
                {
                    msclass.showMessage("Registration Data not available");
                    dialog.dismiss();
                }
                else {
                    // msclass.showMessage(str);
                    JSONObject object = new JSONObject(str);
                    Log.d("","DataWe"+object);
                    JSONArray jArray = object.getJSONArray("Table");
                    Log.d("","DataWe"+jArray);

                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject jObject = jArray.getJSONObject(0);

                        Log.d("","DataWe"+jObject);
                        if(jObject.getString("IMEI")!=(imeiNumber)) {

                            msclass.showMessage("Already Registered on other device");//show specific response msg from api
                            dialog.dismiss();

                        }
                            databaseHelper1.deleledata("UserMaster", "");
                            *//*fl = databaseHelper1.InsertUserRegistration(
                                    jObject.getString("USER_CODE").toString().toUpperCase(),
                                    jObject.getString("USER_NAME").toString(),
                                    jObject.getString("USER_PWD").toString(),
                                    jObject.getString("USER_ROLE").toString(),
                                    jObject.getString("IMEI").toString());*//*
                        fl = databaseHelper1.InsertUserRegistration( //Removed username and password
                                txtentermobile.getText().toString().toUpperCase(),
                                jObject.getString("USER_NAME").toString(),
                                txtentermobile.getText().toString(),
                                jObject.getString("USER_ROLE").toString(),
                                jObject.getString("IMEI").toString());

                        editor.putString("Displayname", jObject.getString("USER_NAME").toString());

                        editor.putString("USER_IMEINO",jObject.getString("IMEI"));

                        //}
                        //else
                       // {
                        //    msclass.showMessage("This user mobile device IMEI No and User id not match .please check IMEI_no");
                        //    dialog.dismiss();
                        ////    return false;
                        //}


                    }
                    String userCodeEncrypt = EncryptDecryptManager.encryptUserID(txtentermobile.getText().toString());
                    editor.putString("USER_CODE", userCodeEncrypt);
                    editor.commit();

                    if (fl == true) {
                    //    msclass.showMessage("User Registration successfully");
                        dialog.dismiss();
                        txtEnterotp.setText("");
                        txtentermobile.setText("");
                        Intent intent= new Intent(getApplicationContext(),login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;
                        // finish();

                    } else {
                        msclass.showMessage("Registration  not done");
                        return false;
                    }
                }*/
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
        //BaseUtils.writeLog();
        return true;
    }


}

