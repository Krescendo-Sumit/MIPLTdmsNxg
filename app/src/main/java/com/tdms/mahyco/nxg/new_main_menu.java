package com.tdms.mahyco.nxg;


import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class new_main_menu extends Fragment {

    public CardView Crddownload, CrdSelectOBS, CrdSowing, CrdObs, CrdUpload, CrdReport, myTravelCard, mapCard, CrdLogout, CrdFeedback;
    ScrollView scrollView;
    RecyclerView gridView;

    databaseHelper databaseHelper1;
    int[] images;

    public String menu_names[] = null;


    String userRole;

    public new_main_menu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_main_menu, container, false);

        gridView = (RecyclerView) rootView.findViewById(R.id.gridView);


        databaseHelper1 = new databaseHelper(getActivity());

        Cursor data = databaseHelper1.fetchusercode();

        if (data.getCount() == 0) {
            userRole="0";


        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    userRole = data.getString((data.getColumnIndex("USER_ROLE")));


                    Log.d("Role", "RoleMainMenu" + userRole);
                } while (data.moveToNext());

            }
            data.close();


        }
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridView.setLayoutManager(mGridLayoutManager);
        //gridView.setHasFixedSize(true);


        if (userRole.equals("1")) {   //Admin
            menu_names = new String[11]; //12
            images = new int[11]; //12
            menu_names[0] = "Download";
            //menu_names[1] = "Select Observation";/*menu_names[1] = "Select Observation"; Removed on 22 April 2021*/
            menu_names[1] = "Sowing & Transplanting";
            menu_names[2] = "Observation";
            menu_names[3] = "Upload";
            menu_names[4] = "My Travel";
            menu_names[5] = "Report";
            menu_names[6] = "Map";
            menu_names[7] = "Feedback";
            menu_names[8]="BE-Survey (VOTG)";
            menu_names[9] = "TFA Survey";
            menu_names[10] = "Logout";

            images[0] = R.drawable.ic_download;
            /*images[1] = R.drawable.ic_new_selectobservation;*/
            images[1] = R.drawable.ic_new_sowing;
            images[2] = R.drawable.ic_new_observation;
            images[3] = R.drawable.ic_new_upload;
            images[4] = R.drawable.mytravel_ic_new;
            images[5] = R.drawable.ic_new_report;
            images[6] = R.drawable.navigation;
            images[7] = R.drawable.ic_launcher_feedback;
            images[8]=R.drawable.ic_survey;
            images[9]=R.drawable.ic_survey;
            images[10] = R.drawable.ic_new_logout;


        } else if (userRole.equals("2")) {

            menu_names = new String[11]; //12
            images = new int[11];//12
            menu_names[0] = "Download";
            /*menu_names[1] = "Select Observation";*/ /*menu_names[1] = "Select Observation"; Removed on 22 April 2021*/
            menu_names[1] = "Sowing & Transplanting";
            menu_names[2] = "Observation";
            menu_names[3] = "Upload";
            menu_names[4] = "My Travel";
            menu_names[5] = "Report";
            menu_names[6] = "Map";
            menu_names[7] = "Feedback";
            menu_names[8]="BE-Survey (VOTG)";
            menu_names[9]="TFA Survey";
            menu_names[10] = "Logout";

            images[0] = R.drawable.ic_download;
            /*images[1] = R.drawable.ic_new_selectobservation;*/
            images[1] = R.drawable.ic_new_sowing;
            images[2] = R.drawable.ic_new_observation;
            images[3] = R.drawable.ic_new_upload;
            images[4] = R.drawable.mytravel_ic_new;
            images[5] = R.drawable.ic_new_report;
            images[6] = R.drawable.navigation;
            images[7] = R.drawable.ic_launcher_feedback;
            images[8]=R.drawable.ic_survey;
            images[9]=R.drawable.ic_survey;
            images[10] = R.drawable.ic_new_logout;

        } else if (userRole.equals("3")) {


            menu_names = new String[11]; //12
            images = new int[11]; //12
            menu_names[0] = "Download";
            /*menu_names[1] = "Select Observation";*/ /*menu_names[1] = "Select Observation"; Removed on 22 April 2021*/
            menu_names[1] = "Sowing & Transplanting";
            menu_names[2] = "Observation";
            menu_names[3] = "Upload";
            menu_names[4] = "My Travel";
            menu_names[5] = "Report";
            menu_names[6] = "Map";
            menu_names[7] = "Feedback";
            menu_names[8]="BE-Survey (VOTG)";
            menu_names[9]="TFA Survey";
            menu_names[10] = "Logout";

            images[0] = R.drawable.ic_download;
            /*images[1] = R.drawable.ic_new_selectobservation;*/
            images[1] = R.drawable.ic_new_sowing;
            images[2] = R.drawable.ic_new_observation;
            images[3] = R.drawable.ic_new_upload;
            images[4] = R.drawable.mytravel_ic_new;
            images[5] = R.drawable.ic_new_report;
            images[6] = R.drawable.navigation;
            images[7] = R.drawable.ic_launcher_feedback;
            images[8]=R.drawable.ic_survey;
            images[9]=R.drawable.ic_survey;
            images[10] = R.drawable.ic_new_logout;


        } else if (userRole.equals("4")) {


            menu_names = new String[8];
            images = new int[8];
            menu_names[0] = "Download";
            menu_names[1] = "Upload";
            menu_names[2] = "Report";
            menu_names[3] = "Map";
            menu_names[4] = "Feedback";
            menu_names[5]="BE-Survey (VOTG)";
            menu_names[6]="TFA Survey";
            menu_names[7] = "Logout";

            images[0] = R.drawable.ic_download;
            images[1] = R.drawable.ic_new_upload;
            images[2] = R.drawable.ic_new_report;
            images[3] = R.drawable.navigation;
            images[4] = R.drawable.ic_launcher_feedback;
            images[5]=R.drawable.ic_survey;
            images[6]=R.drawable.ic_survey;
            images[7] = R.drawable.ic_new_logout;


        } else if (userRole.equals("5")) {


            menu_names = new String[7];
            images = new int[7];
            menu_names[0] = "Download";
            menu_names[1] = "Upload";
            menu_names[2] = "Report";
            menu_names[3] = "Map";
            menu_names[4] = "Feedback";
            menu_names[5]="BE-Survey (VOTG)";
            menu_names[6]="TFA Survey";
            menu_names[7] = "Logout";

            images[0] = R.drawable.ic_download;
            images[1] = R.drawable.ic_new_upload;
            images[2] = R.drawable.ic_new_report;
            images[3] = R.drawable.navigation;
            images[4] = R.drawable.ic_launcher_feedback;
            images[5]=R.drawable.ic_survey;
            images[6]=R.drawable.ic_survey;
            images[7] = R.drawable.ic_new_logout;


        } else if (userRole.equals("6")) {

            menu_names = new String[9]; //10
            images = new int[9]; //10
            menu_names[0] = "Download";
            /*menu_names[1] = "Select Observation";*/ /*menu_names[1] = "Select Observation"; Removed on 22 April 2021*/
            menu_names[1] = "Sowing & Transplanting";
            menu_names[2] = "Observation";
            menu_names[3] = "Upload";
            menu_names[4] = "My Travel";
            menu_names[5] = "Report";
            menu_names[6]="BE-Survey (VOTG)";
            menu_names[7]="TFA Survey";
            menu_names[8] = "Logout";

            images[0] = R.drawable.ic_download;
            /*images[1] = R.drawable.ic_new_selectobservation;*/
            images[1] = R.drawable.ic_new_sowing;
            images[2] = R.drawable.ic_new_observation;
            images[3] = R.drawable.ic_new_upload;
            images[4] = R.drawable.mytravel_ic_new;
            images[5] = R.drawable.ic_new_report;
            images[6] = R.drawable.ic_survey;
            images[7] = R.drawable.ic_survey;
            images[8] = R.drawable.ic_new_logout;

        } else if (userRole.equals("7")) {
            menu_names = new String[8];
            images = new int[8];
            menu_names[0] = "Download";
            menu_names[1] = "Upload";
            menu_names[2] = "Report";
            menu_names[3] = "Map";
            menu_names[4] = "Feedback";
            menu_names[5]="BE-Survey (VOTG)";
            menu_names[5]="TFA Survey";
            menu_names[6] = "Logout";

            images[0] = R.drawable.ic_download;
            images[1] = R.drawable.ic_new_upload;
            images[2] = R.drawable.ic_new_report;
            images[3] = R.drawable.navigation;
            images[4] = R.drawable.ic_launcher_feedback;
            images[5]=R.drawable.ic_survey;
            images[6]=R.drawable.ic_survey;
            images[7] = R.drawable.ic_new_logout;
        }
        else if (userRole.equals("8")) {  //Stakeholder Version 1 as per SRS 18 Sept 2020
            menu_names = new String[5];
            images = new int[5];
            menu_names[0] = "Download";
            menu_names[1] = "Upload";
            menu_names[2] = "Map";
            menu_names[3] = "Feedback";
            menu_names[4] = "Logout";

            images[0] = R.drawable.ic_download;
            images[1] = R.drawable.ic_new_upload;
            images[2] = R.drawable.navigation;
            images[3] = R.drawable.ic_launcher_feedback;
            images[4] = R.drawable.ic_new_logout;
        }
        else if (userRole.equals("9")) { //Stakeholder Version 2 as per SRS 18 Sept 2020
            menu_names = new String[7];
            images = new int[7];
            menu_names[0] = "Download";
            menu_names[1] = "Upload";
            //menu_names[2] = "Select Observation"; /*menu_names[1] = "Select Observation"; Removed on 22 April 2021*/
            menu_names[2] = "Observation";
            menu_names[3] = "Map";
            menu_names[4] = "Feedback";
            menu_names[5] = "Logout";

            images[0] = R.drawable.ic_download;
            images[1] = R.drawable.ic_new_upload;
            /*images[2] = R.drawable.ic_new_selectobservation;*/
            images[2] = R.drawable.ic_new_observation;
            images[3] = R.drawable.navigation;
            images[4] = R.drawable.ic_launcher_feedback;
            images[5] = R.drawable.ic_new_logout;
        }
        else {

            menu_names = new String[12];
            images = new int[12];
            menu_names[0] = "Download";
            //menu_names[1] = "Select Observation"; /*menu_names[1] = "Select Observation"; Removed on 22 April 2021*/
            menu_names[1] = "Sowing & Transplanting";
            menu_names[2] = "Observation";
            menu_names[3] = "Upload";
            menu_names[4] = "My Travel";
            menu_names[5] = "Report";
            menu_names[6] = "Map";
            menu_names[7] = "Feedback";
            menu_names[8]="BE-Survey (VOTG)";
            menu_names[9]="TFA Survey";
            menu_names[10] = "Logout";

            images[0] = R.drawable.ic_download;
            /*images[1] = R.drawable.ic_new_selectobservation;*/
            images[1] = R.drawable.ic_new_sowing;
            images[2] = R.drawable.ic_new_observation;
            images[3] = R.drawable.ic_new_upload;
            images[4] = R.drawable.mytravel_ic_new;
            images[5] = R.drawable.ic_new_report;
            images[6] = R.drawable.navigation;
            images[7] = R.drawable.ic_launcher_feedback;
            images[8] = R.drawable.ic_survey;
            images[9] = R.drawable.ic_survey;
            images[10] = R.drawable.ic_new_logout;
        }


        initAdapter();
        home.isBackNavigationAllowed=false;
        return rootView;
    }
//Get data from menu item String arry and integer array and add it to menu item list
    private ArrayList<MenuPojo> prepareData() {

        ArrayList<MenuPojo> menu_item = new ArrayList<>();
        for (int i = 0; i < menu_names.length; i++) {
            MenuPojo menuPojo = new MenuPojo();
            menuPojo.setMenu_name(menu_names[i]);
            menuPojo.setImage_id(images[i]);
            menu_item.add(menuPojo);
        }
        return menu_item;
    }
//initialize menu adapter
    private void initAdapter() {
        ArrayList<MenuPojo> menuItems = prepareData();
        MenuItemAdapter adapter = new MenuItemAdapter(getActivity(), menuItems);
        gridView.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
        //actionBar.setTitle("Main Menu");
        //actionBar.setTitle("Main Menu TDMS VER:"+BuildConfig.VERSION_NAME);
        //actionBar.setTitle("Trial Data Management: "+ BuildConfig.VERSION_NAME);
        actionBar.setTitle("Trial Data Management: "+ MainApplication.getVersionName(getActivity()));
    }
}
