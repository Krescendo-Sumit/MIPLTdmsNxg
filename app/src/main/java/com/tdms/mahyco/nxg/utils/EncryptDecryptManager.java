package com.tdms.mahyco.nxg.utils;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecryptManager {

    private static String uIdPass = "Mahyco@123";

    public static String encryptData(String value, String password){
        String result="";
        try{
            String dataEncrypted = encrypt(password.getBytes("UTF-16LE"), (value).getBytes("UTF-16LE"));
            result = dataEncrypted;
        }
        catch (Exception e){
            Log.d("Exception","MSG:"+e.getMessage());
        }
        return result;
    }

    public static String decryptData(String value, String password){
        String result="";
        try{
            String dataDecrypted = decrypt(password, Base64.decode(value.getBytes("UTF-16LE"), Base64.DEFAULT));
            result = dataDecrypted;
        }
        catch (Exception e){
            Log.d("Exception","MSG:"+e.getMessage());
        }
        return result;
    }

    public static String encrypt(byte[] key, byte[] clear) throws Exception
    {
        /*<***GIS VAPT 2021 UPDATE 13 Dec 2021 ***>*/
        MessageDigest md = MessageDigest.getInstance("md5");
        byte[] digestOfPassword = md.digest(key);
        SecretKeySpec skeySpec = new SecretKeySpec(digestOfPassword, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS7Padding");
        byte[] iv = new byte[128 / 8];  /*Added later as CTR require param*/
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,ivSpec);
        byte[] encrypted = cipher.doFinal(clear);
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String key, byte[] encrypted) throws Exception
    {
        /*<***GIS VAPT 2021 UPDATE*** 13 Dec 2021 >*/
        MessageDigest md = MessageDigest.getInstance("md5");
        byte[] digestOfPassword = md.digest(key.getBytes("UTF-16LE"));
        SecretKeySpec skeySpec = new SecretKeySpec(digestOfPassword, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS7Padding");
        byte[] iv = new byte[128 / 8]; /*Added later as CTR require param*/
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec,ivSpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, "UTF-16LE");
    }

    public static String encryptStringData(String id){
        try {
            String result = encryptData(id, uIdPass);
            return result;
        }
        catch (Exception e){
            Log.d("Msg",e.getMessage());
        }
        return "";
    }

    public static String decryptStringData(String id){
        try{
            String result = decryptData(id,uIdPass);
            return result;
        }
        catch (Exception e){
            Log.d("Msg",e.getMessage());
        }
        return "";
    }


   /* public static String makeMD5WithRandom(String data){
        final int min = 10000000;
        final int max = 99999999;
        final int random = new Random().nextInt((max - min) + 1) + min;
        String result = md5(data + random);
        return result;
    }*/

    /*public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (Exception ex) {
        }
        return "";
    }*/
}
