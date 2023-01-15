package com.tdms.mahyco.nxg.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseUtils {

    public static String convertMillisToString(){
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        return mydate;
    }


    public static void writeLog(){
        try

        {

            Process process = Runtime.getRuntime().exec("tdmslogcat -d");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                log.append(line);
                log.append("\n");
            }

            //Convert log to string
            final String logString = new String(log.toString());

            //Create txt file in SD Card
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() +File.separator + "Log File");

            if(!dir.exists())
            {
                dir.mkdirs();
            }

            File file = new File(dir, "logcat.txt");

            //To write logcat in text file
            FileOutputStream fout = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fout);

            //Writing the string to file
            osw.write(logString);
            osw.flush();
            osw.close();
        }
        catch(FileNotFoundException e)
        {
            Log.d("MSG","DATA:"+e.getMessage());
        }
        catch(IOException e)
        {
            Log.d("MSG","DATA:"+e.getMessage());
        }


        /*try{
            File filename = new File(Environment.getExternalStorageDirectory()+"/tdmslog.log");
            filename.createNewFile();
            String cmd = "logcat -d -f"+filename.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        }
        catch (Exception e){
            Log.d("Msg","Error : "+e.getMessage());
        }*/
    }

    public static boolean isValidUsernamePass(String name, String password){
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

    public static boolean isValidUsername(String name){
        boolean result= false;
        String expressionUserName = "^[a-zA-Z0-9]{2,25}$";
        String inputStr = name;
        Pattern pattern = Pattern.compile(expressionUserName);//, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()){
            result =  true;
        }
        Log.d("isValidUsername","Name : "+name+" Result:"+result);
        return result;
    }

}
