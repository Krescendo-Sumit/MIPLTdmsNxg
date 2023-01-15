package com.tdms.mahyco.nxg.ReportDashboard;

import android.content.Context;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tdms.mahyco.nxg.R;
import com.tdms.mahyco.nxg.ReportActivity;

import java.util.ArrayList;

/**
 * Created by rohit on 12/22/2015.
 */
public class ReportDashBoardAdapter extends RecyclerView.Adapter<ReportDashBoardAdapter.ViewHolder> {


    //Context
    private Context context;

    //Array List that would contain the urls and the titles for the images
    private ArrayList<String> images;
    private ArrayList<String> items;
    public ReportDashBoardAdapter(Context context,
                                  ArrayList<String> items, ArrayList<String> items2) //, int[] prgmImages)
    {
        // TODO Auto-generated constructor stub

        this.context = context;
        this.items = items;

    }

    @Override
    public ReportDashBoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_report_dashboard, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportDashBoardAdapter.ViewHolder holder, final int position) {
        try {

         //   Utility.setRegularFont(holder.tv, context);
            holder.tv.setText(items.get(position));





            holder.card.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (items.get(position).contains("TRAVEL")) {
                        Intent intent = new Intent(context, MDOweeklyPlanReport.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("ReportType", "Innovation");
                        context.startActivity(intent);

                    }
                    if (items.get(position).contains("OBSERVATION")) {

                         Intent i = new Intent(context, ReportActivity.class);
                        context.startActivity(i);

                    }

                }
            });






        } catch (Exception e) {
            Log.d("Msg",e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView img;

        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.textView1);
            img = (ImageView) itemView.findViewById(R.id.imageView1);
            card = (CardView) itemView.findViewById(R.id.card_view);

        }
    }


}
