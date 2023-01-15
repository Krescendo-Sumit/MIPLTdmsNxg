package com.tdms.mahyco.nxg;

import android.content.Intent;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class forget_password extends AppCompatActivity {
    databaseHelper databaseHelper1;
    Config config;
    public String userCode;
    private TextView txtView,lbltype,txtRegister,txtUpdate,txtForget;
    public EditText txtentermobile,txtEnterotp;
    private CardView btnLogin;
    public Messageclass msclass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().setTitle("Update Password");
        databaseHelper1=new databaseHelper(this);
        msclass=new Messageclass(this);
        txtentermobile=(EditText) findViewById(R.id.txtentermobile);
        txtEnterotp=(EditText) findViewById(R.id.txtEnterotp);
        btnLogin= (CardView)findViewById(R.id.btnLogin);
        Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount()==0)
        {

        }else {
            data.moveToFirst();
            if(data!=null)
            {
                do
                {
                    //userCode=data.getString((data.getColumnIndex("user_code")));
                    String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                    userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("userCode", "userCode" + userCode);
                }while(data.moveToNext());

            }
            data.close();
            String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCode);
            txtentermobile.setText(userCodeDecrypt);
            txtEnterotp.requestFocus();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation() == true) {
                    String encryptedUserCode = EncryptDecryptManager.encryptStringData(txtentermobile.getText().toString());
                    String encryptedPassword = EncryptDecryptManager.encryptStringData( txtEnterotp.getText().toString());
                    String InsertQuery = "update UserMaster set User_pwd = '" + encryptedPassword + "' where user_code = '" + encryptedUserCode + "' ";
                    databaseHelper1.runQuery(InsertQuery);
                    Log.d("VAPT","forgot pass query : "+InsertQuery.toString());
                    msclass.showMessage("Password Updated successfully");
                    txtEnterotp.setText("");
                    txtentermobile.setText("");
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return;
                }
            }
        });
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
        if(!isValidUsernamePass(txtentermobile.getText().toString(),txtEnterotp.getText().toString())) {
            msclass.showMessage("Please set secure  password") ;
            return false;
        }
        return true;
    }

    private boolean isValidUsernamePass(String name, String password){
        boolean result= false;
        String expressionUserName = "^[a-zA-Z0-9]{2,25}$";

        //"([a-zA-Z0-9]$)"; --^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\s).*$
        // String expressionPassord = "(?=.*[!@#$%^&*-])(?=.*[0-9])(?=.*[A-Z]).{8,20}$";
        String expressionPassord = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

        String inputStr = name;
        String  inputPass = password;
        Pattern pattern = Pattern.compile(expressionUserName);//, Pattern.CASE_INSENSITIVE);
        Pattern patterninputPass = Pattern.compile(expressionPassord);//, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        Matcher matcherPass = patterninputPass.matcher(inputPass);
        if (matcher.matches())
        //if (usernameValidator.validate(name))
        {
            if(matcherPass.matches()){
                result = true;
            }
        }
        return result;
    }
}
