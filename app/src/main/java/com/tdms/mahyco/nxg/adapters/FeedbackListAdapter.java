package com.tdms.mahyco.nxg.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tdms.mahyco.nxg.FeedbackSummaryActivity;
import com.tdms.mahyco.nxg.R;
import com.tdms.mahyco.nxg.databaseHelper;
import com.tdms.mahyco.nxg.model.FeedbackReportModel;

import java.util.ArrayList;
import java.util.List;

public class FeedbackListAdapter extends RecyclerView.Adapter<FeedbackListAdapter.DataObjectHolder> {
    private List<FeedbackReportModel> objects = new ArrayList<FeedbackReportModel>();

    private Context context;
    private LayoutInflater layoutInflater;
    int imageResourceId = 0;
    boolean isFromObservation;
    boolean isFromSurvey;

    public FeedbackListAdapter(List<FeedbackReportModel> getlist, Context context, boolean isFromObservation, boolean isFromSurvey) {
        this.context = context;
        this.objects = getlist;
        this.isFromObservation = isFromObservation;
        this.isFromSurvey = isFromSurvey;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FeedbackListAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);

        return new FeedbackListAdapter.DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackListAdapter.DataObjectHolder holder, @SuppressLint("RecyclerView") final int position) {
        Log.d("Data", "Data : " + position + " : " + objects.get(position).toString());
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.Whitecl));

        holder.txtDAS.setText(objects.get(position).getDas() + " Days");
        holder.txt_LocationDetails.setText(objects.get(position).getLocation());
        holder.txt_TrialCodedetails.setText(objects.get(position).getTrailcode());
        holder.txtCurrentStage.setText(objects.get(position).getCurrentStage());
        holder.txt_TrialSegmentDetails.setText(objects.get(position).getTxt_TrialSegmentDetails());
        if (objects.get(position).getVillage() != null) {
            holder.txtVillage.setText(objects.get(position).getVillage());//+objects.get(position).getLocation());
        }
        /*if(objects.get(position).getTxt_TrialSegmentDetails()!=null){
            holder.txtDAS.setText(objects.get(position).getTxt_TrialSegmentDetails());//+objects.get(position).getTxt_TrialSegmentDetails());
        }*/

        /*if (holder.txt_TrialTagDetails.getText().equals("T")) {
            holder.btnPLD.setVisibility(View.GONE);
        }*/
        holder.rel_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if ( isFromObservation){
                    Intent i = new Intent(context, Testimonial.class);
                    i.putExtra("trailCode", objects.get(position).getTrailcode());
                    context.startActivity(i);
                }else if(isFromSurvey){

                    Intent i = new Intent(context, survey.class);
                    i.putExtra("trailCode", objects.get(position).getTrailcode());
                    context.startActivity(i);

                }else {*/
                /* }*/
                databaseHelper databaseHelper1 = new databaseHelper(context);
                String trailCode = objects.get(position).getTrailcode();
                String startPlot = databaseHelper1.getStartPlotTrialFeedback(trailCode);

                boolean isFeedGiven = databaseHelper1.isTrialFeedbacKGiven(trailCode, startPlot);
                Log.d("Survey", "isFeedGiven = " + isFeedGiven);
                if (isFeedGiven) {
                    showAlertFeedbackGiven(context);
                } else {
                    //Toast.makeText(context,"Trial Feedback not given",Toast.LENGTH_SHORT).show();
                    /*Intent i = new Intent(context, FeedbackDetailsActivity.class);
                    i.putExtra("trailCode", trailCode);
                    context.startActivity(i);*/

                    /*Changed on 14th March 2022 Remark:As suggested by TDMS team*/
                    Intent i = new Intent(context, FeedbackSummaryActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.putExtra("trailCode", trailCode);
                    context.startActivity(i);
                }
            }
        });
    }

    private void showAlertFeedbackGiven(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Feedback Given"); //Feedback Uploaded
        builder.setMessage("Feedback already submitted");// given and uploaded to server.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //FeedbackDetailsActivity.super.onBackPressed();
            }
        });
        builder.show();
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
        private TextView txtCurrentStage;
        private TextView txt_TrialSegmentDetails;
        private TextView txtDAS, txtVillage;
        //private Button btnPLD;
        private TextView noorder;
        private RecyclerView recycler;
        ImageView arrow;

        public DataObjectHolder(View view) {
            super(view);
            rel_top = (LinearLayout) view.findViewById(R.id.rel_top);
            cardView = (CardView) view.findViewById(R.id.card_view);
            txt_TrialCodedetails = (TextView) view.findViewById(R.id.txt_TrialCodedetails);
            txt_LocationDetails = (TextView) view.findViewById(R.id.txt_LocationDetails);
            txtCurrentStage = (TextView) view.findViewById(R.id.txt_current_stage);
            txt_TrialSegmentDetails = (TextView) view.findViewById(R.id.txt_TrialSegmentDetails);
            txtDAS = (TextView) view.findViewById(R.id.txt_das_details);
            txtVillage = (TextView) view.findViewById(R.id.txt_village_details);
            //btnPLD = (Button) view.findViewById(R.id.btnPLD);

        }
    }
}
