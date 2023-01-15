package com.tdms.mahyco.nxg;

/**
 * Created by rohit on 24-04-2018.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.cardview.widget.CardView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.DataObjectHolder> {

    private List<ReportModel> objects = new ArrayList<ReportModel>();

    private Context context;
    private LayoutInflater layoutInflater;
    int imageResourceId = 0;

    public ReportListAdapter(List<ReportModel> getlist, Context context) {
        this.context = context;
        this.objects = getlist;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getItemCount() {
        return objects.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_report_details, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {


        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.Whitecl));

        holder.txt_trialcodeValue.setText(objects.get(position).getTrail_code());
        holder.txt_Observation.setText(objects.get(position).getObsesrvationValue());
        holder.txt_FarmerName.setText(objects.get(position).getFarmerName());
        holder.txt_Location.setText(objects.get(position).getLocation());

        holder.rel_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, detail_report.class);
               i.putExtra("trailCode", objects.get(position).getTrail_code());


               context.startActivity(i);
                  }
              });


    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {
        private LinearLayout rel_top;
        private RelativeLayout rel_booked_by;
        private CardView cardView;
        private LinearLayout linOrders;
        private TextView txt_trialcodeValue;
        private TextView txt_Observation;
        private TextView txt_FarmerName;
        private TextView txt_Location;
        private TextView price;
        private TextView noorder;
        private RecyclerView recycler;
        ImageView arrow;

        public DataObjectHolder(View view) {
            super(view);
            rel_top = (LinearLayout) view.findViewById(R.id.rel_top);
            cardView = (CardView) view.findViewById(R.id.card_view);
            txt_trialcodeValue = (TextView) view.findViewById(R.id.txt_trialcodeValue);
            txt_Observation = (TextView) view.findViewById(R.id.txt_Observation);
            txt_FarmerName= (TextView) view.findViewById(R.id.txt_FarmerName);
            txt_Location= (TextView) view.findViewById(R.id.txt_Location);


        }
    }
}
