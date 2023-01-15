package com.tdms.mahyco.nxg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tdms.mahyco.nxg.ReportDashboard.ReportDashboard;
import com.tdms.mahyco.nxg.TravelManagement.MyTravel;
import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.Prefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*Adapter for Home Grid Items*/

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {
    private ArrayList<MenuPojo> menuPojosList;
    private Context context;


    public MenuItemAdapter(Context context, ArrayList<MenuPojo> menupojo) {
        this.menuPojosList = menupojo;
        this.context = context;

    }

    @Override
    public MenuItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_item_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        viewHolder.txtmenu_name.setText(menuPojosList.get(position).getMenu_name());
        if (menuPojosList.get(position).getImage_id() != 0) {
            Picasso.get().load(menuPojosList.get(position).getImage_id()).into(viewHolder.imgMenu);
        }

        //START of menu null check
        if(menuPojosList.get(position).getMenu_name() != null) {

            if (menuPojosList.get(position).getMenu_name().equals("Download")) {

                //For Download Data Button
                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        home.isBackNavigationAllowed = true;
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        DownloadData downloadData = new DownloadData();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, downloadData, downloadData.getTag()).addToBackStack("fragBack").commit();


                    }
                });

                //Download Data Button end


                //For Download Observation Button
            } else if (menuPojosList.get(position).getMenu_name().equals("Sowing & Transplanting")) {

                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        home.isBackNavigationAllowed = true;

                        //Toast.makeText(context,"Sowing clicked",Toast.LENGTH_SHORT).show();

                        Log.d("CLICKED", "Sowing clicked");
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        FieldAreaTag fieldAreaTag = new FieldAreaTag();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, fieldAreaTag, fieldAreaTag.getTag()).addToBackStack("fragBack").commit();


                    }
                });

                //Field Area Button end

                //For Observation Button
            } else if (menuPojosList.get(position).getMenu_name().equals("Select Observation")) {


                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        home.isBackNavigationAllowed = true;


                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        new_Download_Observation downloadObservation = new new_Download_Observation();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, downloadObservation, downloadObservation.getTag()).addToBackStack("fragBack").commit();


                    }
                });
                //Download Observation Button end

                //For Field Area  Button

            } else if (menuPojosList.get(position).getMenu_name().equals("Observation")) {

                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        home.isBackNavigationAllowed = true;

                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Observation observation1 = new Observation();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, observation1, observation1.getTag()).addToBackStack("fragBack").commit();


                    }
                });

                //For observation Button end
//
//        //For Upload Data Button
            } else if (menuPojosList.get(position).getMenu_name().equals("My Travel")) {


                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, MyTravel.class);
                        context.startActivity(i);
                    }
                });
                //For Reporrt Button


            } else if (menuPojosList.get(position).getMenu_name().equals("Upload")) {

                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        home.isBackNavigationAllowed = true;

                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        UploadData uploadData = new UploadData();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, uploadData, uploadData.getTag()).addToBackStack("fragBack").commit();

                    }
                });


                //Upload Data Button end


                //Feedback
            } else if (menuPojosList.get(position).getMenu_name().equals("Report")) {


                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ReportDashboard.class);
                        // Intent i = new Intent(getActivity(), ReportActivity.class);
                        context.startActivity(i);
                    }
                });

                //For Map Button

            }
            //else if (menuPojosList.get(position).getMenu_name().equals("BE-Survey (VOTG)")) {
//
//
//
//            viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    home.isBackNavigationAllowed=true;
//
//                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                    SurveyFragment surveyFragment = new SurveyFragment();
//                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, surveyFragment,
//                            surveyFragment.getTag()).addToBackStack("fragBack").commit();
//                }
//            });
//
//            //For Map Button
//
//        }
            else if (menuPojosList.get(position).getMenu_name().equals("Feedback")) {
                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        home.isBackNavigationAllowed = true;

                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        TrialFeedbackFragment feedbackFragment = new TrialFeedbackFragment();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, feedbackFragment,
                                feedbackFragment.getTag()).addToBackStack("fragBack").commit();


                    }
                });


            } else if (menuPojosList.get(position).getMenu_name().equals("Map")) {
                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, MapsActivity.class);
                        context.startActivity(i);
                    }
                });

                //For Feedback Button

            } else if (menuPojosList.get(position).getMenu_name().equals("TFA Survey")) {
                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, MDOSurveyActivity.class);
                        context.startActivity(i);
                    }
                });
            } else if (menuPojosList.get(position).getMenu_name().equals("Logout")) {             //For Logout Button

                viewHolder.Crddownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //LOGOUT

                        try {
                            Prefs mPref = Prefs.with(context);
                            CommonExecution cxx = new CommonExecution(context);
                            String userCodeEncrypt = mPref.getString(AppConstant.USER_CODE_PREF, "");
                            String userCodeDecrypt = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                            if (userCodeDecrypt != null && !userCodeDecrypt.equals("")) {
                                String json = cxx.new ResetUserLogin(1, userCodeDecrypt).execute().get();
                                Log.d("MSG", "ON LOGOUT From menu DATA response:" + json);
                            } else {
                                Toast.makeText(context, "Invalid User Data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.d("MSG", "DATA:" + e.getMessage());
                        }

                        databaseHelper databaseHelper1 = new databaseHelper(context);
                        String searchQuery1 = "delete from UserMaster ";
                        databaseHelper1.runQuery(searchQuery1);

                        SharedPreferences pref;
                        SharedPreferences.Editor editor;
                        pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
                        editor = pref.edit();
                        editor.clear();
                        editor.commit();

                        //Clear all data
                        Prefs mPref = Prefs.with(context);
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
                        Intent openIntent = new Intent(context, login.class);
                        openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(openIntent);
                    }
                });

            }
        }//END of menu null check
    }

    @Override
    public int getItemCount() {
        return menuPojosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtmenu_name;
        private ImageView imgMenu;
        private CardView Crddownload;

        public ViewHolder(View view) {
            super(view);

            txtmenu_name = (TextView) view.findViewById(R.id.txtmenu_name);
            imgMenu = (ImageView) view.findViewById(R.id.imgMenu);
            Crddownload = (CardView) view.findViewById(R.id.Crddownload);
        }
    }

}