package com.tdms.mahyco.nxg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

/// Screen to show Sowing and Tagging Details
public class trailDetails_ListAdaptor extends RecyclerView.Adapter<trailDetails_ListAdaptor.DataObjectHolder> {
    private List<TrailReportModel> objects = new ArrayList<TrailReportModel>();
    private Context context;
    private LayoutInflater layoutInflater;
    int imageResourceId = 0;
    FragmentManager fragmentManager;

    /*Create Object*/
    Prefs mPref;

    int year;
    int season;
    int crop;
    int trialType;
    String sownStatus="";

    public trailDetails_ListAdaptor(List<TrailReportModel> getlist, Context context, FragmentManager fragmentManager,
                                    int year, int season, int crop, int trialType) {
        this.context = context;
        this.objects = getlist;
        this.layoutInflater = LayoutInflater.from(context);
        this.fragmentManager = fragmentManager;
        /*Initialize*/
        mPref = Prefs.with(context);
        this.year = year;
        this.season = season;
        this.crop = crop;
        this.trialType = trialType;
    }

    @NonNull
    @Override
    public trailDetails_ListAdaptor.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trail_details, parent, false);

        return new trailDetails_ListAdaptor.DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull trailDetails_ListAdaptor.DataObjectHolder holder, @SuppressLint("RecyclerView") final int position) {

        Log.d("TrialDetail", "Position : " + position + " TrialCode : " + objects.get(position).getTrailcode() + " Tagged : " + objects.get(position).getTagged());
        Log.d("TrialDetail", "TAGGED TYPE " + objects.get(position).getTagType());
        Log.d("OBJECT", "POSITION : " + position + "  " + objects.get(position).toString());
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.Whitecl));
        holder.txt_LocationDetails.setText(objects.get(position).getLocation());
        holder.txt_TrialCodedetails.setText(objects.get(position).getTrailcode());
        holder.txt_TrialTagDetails.setText(objects.get(position).getTagged());
        holder.tv_tag_type.setText(objects.get(position).getTagType());
        holder.txt_TrialSegmentDetails.setText(objects.get(position).getTxt_TrialSegmentDetails());

        if (holder.txt_TrialTagDetails.getText().equals("T")) {
            holder.btnPLD.setVisibility(View.VISIBLE);
            holder.btnNotSown.setVisibility(View.GONE);
            sownStatus = AppConstant.PLD;
        }
        else{
            holder.btnPLD.setVisibility(View.GONE);
            holder.btnNotSown.setVisibility(View.VISIBLE);
            sownStatus = AppConstant.NOT_SOWN;
        }

        /*String tagType = holder.tv_tag_type.getText().toString();
        String taggeed = holder.txt_TrialTagDetails.getText().toString();
        String trialCode = holder.txt_TrialCodedetails.getText().toString();
*/
        String tagTypeDis = holder.tv_tag_type.getText().toString();
        if (tagTypeDis != null) {
            if (tagTypeDis.equals(AppConstant.NURSERY_TAG)) {
                holder.txt_TrialTagDetails.setTextColor(context.getResources().getColor(R.color.blue));
            }
        }
        holder.btnPLD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trialCode = holder.txt_TrialCodedetails.getText().toString();
                Intent i = new Intent(context, pld_not_sown.class);
                i.putExtra("Trail_code", trialCode);
                i.putExtra(AppConstant.SOWN_STATUS, sownStatus);
                context.startActivity(i);
            }
        });
        holder.btnNotSown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trialCode = holder.txt_TrialCodedetails.getText().toString();
                Intent i = new Intent(context, pld_not_sown.class);
                i.putExtra("Trail_code", trialCode);
                i.putExtra(AppConstant.SOWN_STATUS, sownStatus);
                context.startActivity(i);
            }
        });
        holder.rel_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseHelper databaseHelper1  = new databaseHelper(context);
                if(databaseHelper1.isNotSownExist(holder.txt_TrialCodedetails.getText().toString())){
                    Toast.makeText(context,"Plot Not Sown",Toast.LENGTH_LONG).show();
                    return;
                }

                /*SAVE User selected options*/
                saveDataForReference(year,season,crop,trialType);

                String tagType = holder.tv_tag_type.getText().toString();
                String taggeed = holder.txt_TrialTagDetails.getText().toString();
                String trialCode = holder.txt_TrialCodedetails.getText().toString();
                Log.d("ITEM_CLICKED", "POS:" + position + " TAG_TYPE :" + tagType+" taggeed : "+taggeed);
                if (taggeed == null) {
                    Log.d("ITEM_CLICKED","1 if");

                    /*Added on 19th July 2021*/
                    Intent openIntent = new Intent(context, SowingActivityNew.class);
                    Bundle data1 = new Bundle();//create bundle instance
                    data1.putString("keyCode", trialCode);
                    openIntent.putExtra("DATA",data1);
                    context.startActivity(openIntent);

                    /*Commented on 19th July 2021*/
                    /*Bundle data1 = new Bundle();//create bundle instance
                    data1.putString("keyCode", trialCode);//put string to pass with a key value
                    Fragment mFragment = new sowing();
                    mFragment.setArguments(data1);
                    fragmentManager.beginTransaction().replace(R.id.FragmentContainer, mFragment).commit();*/

                    /*Commented on 19th April 2021*/
                    /*Bundle data1 = new Bundle();//create bundle instance
                    data1.putString("keyCode", trialCode);//put string to pass with a key value
                    data1.putString(AppConstant.TAG_TYPE, AppConstant.NO_TAG);//put string to pass with a key value
                    Fragment mFragment = new SelectObsOpnTrialFrag();
                    mFragment.setArguments(data1);
                    fragmentManager.beginTransaction().replace(R.id.FragmentContainer, mFragment).commit();*/

                } else if (taggeed != null && tagType != null) {
                    Log.d("ITEM_CLICKED","2 else if");
                    if (tagType.equals(AppConstant.NURSERY_TAG)) {
                        Log.d("ITEM_CLICKED","3 if");

                        /*Added on 19th July 2021*/
                        Intent openIntent = new Intent(context, SowingActivityNew.class);
                        Bundle data1 = new Bundle();//create bundle instance
                        data1.putString("keyCode", trialCode);//put string to pass with a key value
                        data1.putString(AppConstant.TAG_TYPE, AppConstant.NURSERY_TAG);//put string to pass with a key value
                        openIntent.putExtra("DATA",data1);
                        context.startActivity(openIntent);

                        /*Commented on 19th July 2021*/
                        /*Bundle data1 = new Bundle();//create bundle instance
                        data1.putString("keyCode", trialCode);//put string to pass with a key value
                        data1.putString(AppConstant.TAG_TYPE, AppConstant.NURSERY_TAG);//put string to pass with a key value
                        Fragment mFragment = new sowing();
                        mFragment.setArguments(data1);
                        fragmentManager.beginTransaction().replace(R.id.FragmentContainer, mFragment).commit();*/

                        /*Commented on 19th April 2021*/
                        /*Bundle data1 = new Bundle();//create bundle instance
                        data1.putString("keyCode", trialCode);//put string to pass with a key value
                        data1.putString(AppConstant.TAG_TYPE, AppConstant.NURSERY_TAG);//put string to pass with a key value
                        Fragment mFragment = new SelectObsOpnTrialFrag();
                        mFragment.setArguments(data1);
                        fragmentManager.beginTransaction().replace(R.id.FragmentContainer, mFragment).commit();*/

                    } else if (tagType.equals(AppConstant.FULL_TAG)){
                        Log.d("ITEM_CLICKED","4 else");
                        Intent i = new Intent(context, alreadyTag.class);
                        i.putExtra("Trail_code", trialCode);
                        context.startActivity(i);
                    }
                    else if (tagType.equals(AppConstant.NO_TAG)) {
                       Log.d("ITEM_CLICKED","3 if");

                        /*Added on 19th July 2021*/
                        Intent openIntent = new Intent(context, SowingActivityNew.class);
                        Bundle data1 = new Bundle();//create bundle instance
                        data1.putString("keyCode", trialCode);//put string to pass with a key value
                        data1.putString(AppConstant.TAG_TYPE, AppConstant.NO_TAG);//put string to pass with a key value
                        openIntent.putExtra("DATA",data1);
                        context.startActivity(openIntent);

                        /*Commented on 19th July 2021*/
                        /*Bundle data1 = new Bundle();//create bundle instance
                        data1.putString("keyCode", trialCode);//put string to pass with a key value
                        data1.putString(AppConstant.TAG_TYPE, AppConstant.NO_TAG);//put string to pass with a key value
                        Fragment mFragment = new sowing();
                        mFragment.setArguments(data1);
                        fragmentManager.beginTransaction().replace(R.id.FragmentContainer, mFragment).commit();*/

                        /*Commented on 19th April 2021*/
                        /*Bundle data1 = new Bundle();//create bundle instance
                        data1.putString("keyCode", trialCode);//put string to pass with a key value
                        data1.putString(AppConstant.TAG_TYPE, AppConstant.NO_aTAG);//put string to pass with a key value
                        Fragment mFragment = new SelectObsOpnTrialFrag();
                        mFragment.setArguments(data1);
                        fragmentManager.beginTransaction().replace(R.id.FragmentContainer, mFragment).commit();*/
                    }
                    else{
                        Log.d("ITEM_CLICKED","5 else");
                        Intent i = new Intent(context, alreadyTag.class);
                        i.putExtra("Trail_code", trialCode);
                        context.startActivity(i);
                    }
                } else {
                    Log.d("ITEM_CLICKED","6 else");
                    Intent i = new Intent(context, alreadyTag.class);
                    i.putExtra("Trail_code", trialCode);
                    context.startActivity(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {
        private LinearLayout rel_top;
        private CardView cardView;
        private TextView txt_LocationDetails;
        private TextView txt_TrialCodedetails;
        private TextView txt_TrialTagDetails;
        private TextView txt_TrialSegmentDetails;
        private TextView tv_tag_type;
        private Button btnPLD;
        private Button btnNotSown;
        private TextView noorder;
        private RecyclerView recycler;
        ImageView arrow;
        private RelativeLayout rel_booked_by;
        private LinearLayout linOrders;

        public DataObjectHolder(View view) {
            super(view);
            rel_top = (LinearLayout) view.findViewById(R.id.rel_top);
            cardView = (CardView) view.findViewById(R.id.card_view);
            txt_TrialCodedetails = (TextView) view.findViewById(R.id.txt_TrialCodedetails);
            txt_LocationDetails = (TextView) view.findViewById(R.id.txt_LocationDetails);
            txt_TrialTagDetails = (TextView) view.findViewById(R.id.txt_TrialTagDetails);
            txt_TrialSegmentDetails = (TextView) view.findViewById(R.id.txt_TrialSegmentDetails);
            tv_tag_type = (TextView) view.findViewById(R.id.tv_tag_type);
            btnPLD = (Button) view.findViewById(R.id.btnPLD);
            btnNotSown = (Button) view.findViewById(R.id.btnNotSown);
            //txt_Location= (TextView) view.findViewById(R.id.txt_Location);


        }
    }

    void saveDataForReference(int year, int season, int crop,int trialType){
        mPref.saveInt(Constants.SELECTED_YEAR,year);
        mPref.saveInt(Constants.SELECTED_SEASON,season);
        mPref.saveInt(Constants.SELECTED_CROP,crop);
        mPref.saveInt(Constants.SELECTED_TRAIL_TYPE,trialType);
        int year1 = mPref.getInt(Constants.SELECTED_YEAR,0);
        int season1 = mPref.getInt(Constants.SELECTED_SEASON,0);
        int crop1 = mPref.getInt(Constants.SELECTED_CROP,0);
        int trailType1 = mPref.getInt(Constants.SELECTED_TRAIL_TYPE,0);
        Log.d("SAVED DATA Adaptor","Year : "+year1);
        Log.d("SAVED DATA Adaptor","Season : "+season1);
        Log.d("SAVED DATA Adaptor","Crop : "+crop1);
        Log.d("SAVED DATA Adaptor","Trail Type : "+trailType1);
    }

}
