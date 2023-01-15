package com.tdms.mahyco.nxg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tdms.mahyco.nxg.utils.AppConstant;

import java.util.ArrayList;
import java.util.List;

public class trial_ListAdaptor extends RecyclerView.Adapter<trial_ListAdaptor.DataObjectHolder> {
    private List<TrailReportModel> objects = new ArrayList<TrailReportModel>();

    private Context context;
    private LayoutInflater layoutInflater;
    int imageResourceId = 0;
    boolean isFromObservation;
    boolean isFromSurvey;
    String crop = "";
    databaseHelper databaseHelper1;

    public trial_ListAdaptor(List<TrailReportModel> getlist, Context context, boolean isFromObservation, boolean isFromSurvey) {
        this.context = context;
        this.objects = getlist;
        this.isFromObservation = isFromObservation;
        this.isFromSurvey = isFromSurvey;
        this.layoutInflater = LayoutInflater.from(context);
        this.crop = "";
    }

    public trial_ListAdaptor(List<TrailReportModel> getlist, Context context, boolean isFromObservation, boolean isFromSurvey, String crop) {
        this.context = context;
        this.objects = getlist;
        this.isFromObservation = isFromObservation;
        this.isFromSurvey = isFromSurvey;
        this.layoutInflater = LayoutInflater.from(context);
        this.crop = crop;
    }

    @NonNull
    @Override
    public trial_ListAdaptor.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trail_details, parent, false);

        databaseHelper1 = new databaseHelper(context);

        return new trial_ListAdaptor.DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull trial_ListAdaptor.DataObjectHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.Whitecl));
        holder.txt_LocationDetails.setText(objects.get(position).getLocation());
        holder.txt_TrialCodedetails.setText(objects.get(position).getTrailcode());
        holder.txt_TrialTagDetails.setText(objects.get(position).getTagged());
        holder.txt_TrialSegmentDetails.setText(objects.get(position).getTxt_TrialSegmentDetails());

        holder.btnNotSown.setVisibility(View.GONE);

        if (holder.txt_TrialTagDetails.getText().equals("T")) {
            holder.btnPLD.setVisibility(View.GONE);
        }
        holder.rel_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OBSERVATION", "onClick isFromObservation : " + isFromObservation);
                Log.d("OBSERVATION", "onClick isFromSurvey : " + isFromSurvey);
                Log.d("OBSERVATION", "onClick CROP : " + crop);

                if (isFromObservation) {
                    /*Intent i = new Intent(context, Testimonial.class);
                    i.putExtra("trailCode", objects.get(position).getTrailcode());
                    context.startActivity(i);*/
                    /*Added on 19 April 2021*/
                    Intent i = new Intent(context, SelectObsOpnTrialActivity.class); //SelectObsOpnTrialActivity.class
                    i.putExtra("keyCode", objects.get(position).getTrailcode());
                    i.putExtra(AppConstant.TAG_TYPE, objects.get(position).getTagType());
                    i.putExtra("TrialDetails", objects.get(position).getTagged());
                    i.putExtra("SegmentDetails", objects.get(position).getTxt_TrialSegmentDetails());
                    i.putExtra("Location", objects.get(position).getLocation());
                    i.putExtra("selectedCrop", crop);
                    context.startActivity(i);

                } else if (isFromSurvey) {
                    Intent i = new Intent(context, survey.class);
                    i.putExtra("trailCode", objects.get(position).getTrailcode());
                    context.startActivity(i);

                } else {
                    /*Intent i = new Intent(context, FeedbackDetailsActivity.class);
                    i.putExtra("trailCode", objects.get(position).getTrailcode());
                    context.startActivity(i);*/

                    /*Changed on 14th March 2022 Remark:As suggested by TDMS team*/
                    Intent i = new Intent(context, FeedbackSummaryActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.putExtra("trailCode", objects.get(position).getTrailcode());
                    context.startActivity(i);
                }
            }
        });


        /*Added on 23rd April 2021*/
        String trialCode = objects.get(position).getTrailcode().toString();
        int result = databaseHelper1.observationStatus(trialCode);
        Log.d("OBS ADAPTER","TRIA LIST ADAPTER TEXT COLOR RESULT : "+result);
        if (result == 1) { /*Trial Downloaded for Feedback*/
            /*Label*/
            holder.tvTcL.setTextColor(context.getResources().getColor(R.color.red));
            holder.tvLocl.setTextColor(context.getResources().getColor(R.color.red));
            holder.tvSegL.setTextColor(context.getResources().getColor(R.color.red));
            holder.tvTagL.setTextColor(context.getResources().getColor(R.color.red));
            /*Value*/
            holder.txt_LocationDetails.setTextColor(context.getResources().getColor(R.color.red));
            holder.txt_TrialCodedetails.setTextColor(context.getResources().getColor(R.color.red));
            holder.txt_TrialTagDetails.setTextColor(context.getResources().getColor(R.color.red));
            holder.txt_TrialSegmentDetails.setTextColor(context.getResources().getColor(R.color.red));
        } else if (result == 2) { /*Trial Plot data Saved but not uploaded*/
            /*Label*/
            holder.tvTcL.setTextColor(context.getResources().getColor(R.color.green));
            holder.tvLocl.setTextColor(context.getResources().getColor(R.color.green));
            holder.tvSegL.setTextColor(context.getResources().getColor(R.color.green));
            holder.tvTagL.setTextColor(context.getResources().getColor(R.color.green));
            /*Value*/
            holder.txt_LocationDetails.setTextColor(context.getResources().getColor(R.color.green));
            holder.txt_TrialCodedetails.setTextColor(context.getResources().getColor(R.color.green));
            holder.txt_TrialTagDetails.setTextColor(context.getResources().getColor(R.color.green));
            holder.txt_TrialSegmentDetails.setTextColor(context.getResources().getColor(R.color.green));
        } else if (result == 3) { /*Trial Uploaded completely*/
            /*Label*/
            holder.tvTcL.setTextColor(context.getResources().getColor(R.color.orange));
            holder.tvLocl.setTextColor(context.getResources().getColor(R.color.orange));
            holder.tvSegL.setTextColor(context.getResources().getColor(R.color.orange));
            holder.tvTagL.setTextColor(context.getResources().getColor(R.color.orange));
            /*Value*/
            holder.txt_LocationDetails.setTextColor(context.getResources().getColor(R.color.orange));
            holder.txt_TrialCodedetails.setTextColor(context.getResources().getColor(R.color.orange));
            holder.txt_TrialTagDetails.setTextColor(context.getResources().getColor(R.color.orange));
            holder.txt_TrialSegmentDetails.setTextColor(context.getResources().getColor(R.color.orange));
        } else {
            /*NO TEXT COLOR CHANGE AS NO ATTEMPT MADE FOR OBSERVATION*/
        }
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
        private RelativeLayout rel_booked_by;
        private CardView cardView;
        private LinearLayout linOrders;
        private TextView txt_LocationDetails;
        private TextView txt_TrialCodedetails;
        private TextView txt_TrialTagDetails;
        private TextView txt_TrialSegmentDetails;
        private Button btnPLD,btnNotSown;
        private TextView noorder;
        private RecyclerView recycler;
        TextView tvTcL, tvLocl, tvSegL, tvTagL;
        ImageView arrow;

        public DataObjectHolder(View view) {
            super(view);
            rel_top = (LinearLayout) view.findViewById(R.id.rel_top);
            cardView = (CardView) view.findViewById(R.id.card_view);
            txt_TrialCodedetails = (TextView) view.findViewById(R.id.txt_TrialCodedetails);
            txt_LocationDetails = (TextView) view.findViewById(R.id.txt_LocationDetails);
            txt_TrialTagDetails = (TextView) view.findViewById(R.id.txt_TrialTagDetails);
            txt_TrialSegmentDetails = (TextView) view.findViewById(R.id.txt_TrialSegmentDetails);
            tvTcL = (TextView) view.findViewById(R.id.tv_tcl);
            tvLocl = (TextView) view.findViewById(R.id.tv_locl);
            tvSegL = (TextView) view.findViewById(R.id.tv_tagl);
            tvTagL = (TextView) view.findViewById(R.id.tv_segl);
            btnPLD = (Button) view.findViewById(R.id.btnPLD);
            btnNotSown = (Button) view.findViewById(R.id.btnNotSown);

        }
    }
}
