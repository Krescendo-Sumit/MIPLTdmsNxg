package com.tdms.mahyco.nxg;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.Prefs;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenu extends Fragment {


    public MainMenu() {
        // Required empty public constructor
    }


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        //getActivity().setTitle("Trial Data Management:"+BuildConfig.VERSION_NAME);
        getActivity().setTitle("Trial Data Management:"+MainApplication.getVersionName(getActivity()));
        //Button Id Capture
        Button button=(Button)rootView.findViewById(R.id.btnDownload);
        Button fieldarea=(Button)rootView.findViewById(R.id.btnAreaTag);
        Button DownloadObservation =(Button)rootView.findViewById(R.id.btnDownloadObservation);
        Button observation=(Button)rootView.findViewById(R.id.btnObservation);
        Button validation=(Button)rootView.findViewById(R.id.btnValidation);
        Button uploaddata=(Button)rootView.findViewById(R.id.btnUploadData);
        Button logout=(Button)rootView.findViewById(R.id.btnLogout);

        Button btnShowDB=(Button)rootView.findViewById(R.id.btnShowDB);
        Button btnShowReport=(Button)rootView.findViewById(R.id.btnShowReport);


        //For Download Data Button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadData downloadData=new DownloadData();
                FragmentManager fragmentManager=getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.FragmentContainer,downloadData,downloadData.getTag())
                        .addToBackStack("fragBack").commit();
            }
        });

        //Download Data Button end


        //For Download Observation Button
        DownloadObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_Download_Observation downloadObservation =new new_Download_Observation();
                FragmentManager fragmentManager=getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.FragmentContainer,downloadObservation,downloadObservation.getTag())
                        .addToBackStack("fragBack").commit();
            }
        });

        //Download Observation Button end

        //For Field Area  Button
        fieldarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FieldAreaTag fieldAreaTag=new FieldAreaTag();
                FragmentManager fragmentManager=getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.FragmentContainer,fieldAreaTag,fieldAreaTag.getTag())
                        .addToBackStack("fragBack").commit();
            }
        });

        //Field Area Button end

        //For Observation Button
        observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Observation observation1=new Observation();
                FragmentManager fragmentManager=getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.FragmentContainer,observation1,observation1.getTag())
                        .addToBackStack("fragBack").commit();
            }
        });

        //For observation Button end

        //For Validation Data Button
        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validation validation1=new Validation();
                FragmentManager fragmentManager=getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.FragmentContainer,validation1,validation1.getTag())
                        .addToBackStack("fragBack").commit();
            }
        });

        //Validation Data Button end

        //For Upload Data Button
        uploaddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData uploadData=new UploadData();
                FragmentManager fragmentManager=getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.FragmentContainer,uploadData,uploadData.getTag())
                        .addToBackStack("fragBack").commit();
            }
        });

        //Upload Data Button end


        //For Logout Button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Logout logout1=new Logout();
                Intent openIntent=new Intent(getContext(),login.class);
                startActivity(openIntent);*/

                //LOGOUT

                try {
                    Prefs mPref = Prefs.with(getContext());
                    CommonExecution cxx = new CommonExecution(getContext());
                    String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                    String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    if(userCodeDecrypt!=null && !userCodeDecrypt.equals("")){
                        String json = cxx.new ResetUserLogin(1, userCodeDecrypt).execute().get();
                        Log.d("MSG", "ON LOGOUT From menu DATA response:" + json);
                    }
                    else{
                        Toast.makeText(getContext(),"Invalid User Data",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d("MSG", "DATA:" + e.getMessage());
                }

                databaseHelper databaseHelper1 = new databaseHelper(getContext());
                String searchQuery1 = "delete from UserMaster ";
                databaseHelper1.runQuery(searchQuery1);

                SharedPreferences pref;
                SharedPreferences.Editor editor;
                pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                editor = pref.edit();
                editor.clear();
                editor.commit();

                //Clear all data
                Prefs mPref = Prefs.with(getContext());
                mPref.removeAll();
                editor.clear();
                editor.commit();
                mPref.save(AppConstant.USER_CODE_PREF, null);
                mPref.save(AppConstant.USER_PASSWORD_PREF, null);
                mPref.save(AppConstant.finalmessage, null);
                mPref.save(AppConstant.ACCESS_TOKEN_TAG, null);
                mPref.save(AppConstant.ACCESS_TOKEN_EXPIRY, null);
                editor.putString("Displayname", null);

                Intent openIntent = new Intent(getContext(), login.class);
                openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(openIntent);
            }
        });

        //Logout  Button end

        //For Download Data Button
        btnShowDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),AndroidDatabaseManager.class);
                startActivity(i);
            }
        });

        //For Download Data Button
        btnShowReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ReportActivity.class);
                startActivity(i);
            }
        });
        return rootView;


    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
       // actionBar.setTitle("Main Menu");
        //actionBar.setTitle("Main Menu TDMS VER:"+BuildConfig.VERSION_NAME);
        //actionBar.setTitle("Trial Data Management: "+ BuildConfig.VERSION_NAME);
        actionBar.setTitle("Trial Data Management: "+ MainApplication.getVersionName(getActivity()));

    }

}
