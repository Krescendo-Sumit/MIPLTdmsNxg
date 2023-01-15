package com.tdms.mahyco.nxg.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdms.mahyco.nxg.R;
import com.tdms.mahyco.nxg.databaseHelper;
import com.tdms.mahyco.nxg.model.TrialFeedbackSummaryModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TrialFeedbackSummaryListAdapter extends RecyclerView.Adapter<TrialFeedbackSummaryListAdapter.SummaryVH> {
    private List<TrialFeedbackSummaryModel> objects = new ArrayList<TrialFeedbackSummaryModel>();
    private Context context;
    SummaryLister summaryLister;

    public TrialFeedbackSummaryListAdapter(ArrayList<TrialFeedbackSummaryModel> summaryModelArrayList, Context context, SummaryLister summaryLister) {
        this.context = context;
        this.objects = summaryModelArrayList;
        this.summaryLister = summaryLister;
    }

    @NonNull
    @Override
    public TrialFeedbackSummaryListAdapter.SummaryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback_summary, parent, false);
        return new TrialFeedbackSummaryListAdapter.SummaryVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrialFeedbackSummaryListAdapter.SummaryVH holder, @SuppressLint("RecyclerView") final int position) {
        Log.d("Data", "Data : " + position + " : " + objects.get(position).toString());

        holder.tvPlotNo.setText(objects.get(position).getPlotNo());
        holder.tvRank.setText(objects.get(position).getRank());
        holder.tvComment.setText(objects.get(position).getComment());

        holder.ivEditFeedbackSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Edit record", Toast.LENGTH_SHORT).show();
                showEditDialog(context, objects.get(position).getTrialCode(), objects.get(position).getPlotNo(), summaryLister, objects.get(position).getRank(), objects.get(position).getComment());
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


    public class SummaryVH extends RecyclerView.ViewHolder {
        TextView tvPlotNo, tvRank, tvComment;
        ImageView ivEditFeedbackSummary;

        public SummaryVH(View view) {
            super(view);
            tvPlotNo = (TextView) view.findViewById(R.id.tv_plot_no_summary);
            tvRank = (TextView) view.findViewById(R.id.tv_rank);
            tvComment = (TextView) view.findViewById(R.id.tv_comment);
            ivEditFeedbackSummary = (ImageView) view.findViewById(R.id.img_edit_feedback);

        }
    }

    public void showEditDialog(final Context activity, final String trialCode, final String plotNo, final SummaryLister summaryLister, String rank, String commentData) {
        final databaseHelper databaseHelper1 = new databaseHelper(activity);
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_alert_feedback_summary_item);

        final TextView tvPlotNo = (TextView) dialog.findViewById(R.id.lblPlotNoAlert);
        final TextView txtTrialCodeAlert = (TextView) dialog.findViewById(R.id.txtTrialCodeAlert);
        tvPlotNo.setText(plotNo);
        txtTrialCodeAlert.setText(trialCode);
        final EditText comment = (EditText) dialog.findViewById(R.id.remarks_alert);
        if(commentData!=null){
            if(!commentData.equalsIgnoreCase("null"))
            comment.setText("" + commentData);
        }
        final Spinner ddlspinnerRank = (Spinner) dialog.findViewById(R.id.spinner_rate_alert);
        List<String> ddRankingList = Arrays.asList(activity.getResources().getStringArray(R.array.Ranking));
        ArrayAdapter<String> ddYearlistAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item,
                ddRankingList);
        ddYearlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        ddlspinnerRank.setAdapter(ddYearlistAdapter);

        if (rank != null) {
            if (!rank.equalsIgnoreCase("Select Rank")) {
                int rankSet = Integer.parseInt(rank);
                ddlspinnerRank.setSelection(rankSet);
            }
        }

        Button dialogButtonSave = (Button) dialog.findViewById(R.id.btn_alert_save);
        Button dialogButtonBack = (Button) dialog.findViewById(R.id.btn_alert_back);
        dialogButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Is_Synced = "0";
                //String Is_Submitted = "0";
                String Is_Submitted = "1";
                databaseHelper1.getWritableDatabase();

                Date entrydate = new Date();
                String InTime = new SimpleDateFormat("dd-MM-yyyy").format(entrydate);

                if(ddlspinnerRank.getSelectedItem().toString().equalsIgnoreCase("Select rank")){
                        Toast.makeText(context,"Please select rank",Toast.LENGTH_LONG).show();
                        return;
                }
                String commentdata = comment.getText().toString();
                if(commentdata.equalsIgnoreCase("") || commentdata==null){
                    comment.setError("Please enter comment");
                    return;
                }
                else{
                    comment.setError(null);
                }

                databaseHelper1.updateTrialFeedback(trialCode, plotNo, Is_Synced.toString(), ddlspinnerRank.getSelectedItem().toString(), comment.getText().toString(), Is_Submitted.toString(), InTime);
                databaseHelper1.close();
                if (summaryLister != null)
                    summaryLister.onSummaryAlertCallback();
                Toast.makeText(activity, "Record updated successfully", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    interface RefreshLister {

    }
}