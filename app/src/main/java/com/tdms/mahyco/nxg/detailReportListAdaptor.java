package com.tdms.mahyco.nxg;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class detailReportListAdaptor extends RecyclerView.Adapter<detailReportListAdaptor.DataObjectHolder> {

    private List<detailReportModel> objects = new ArrayList<detailReportModel>();

    private Context context;
    private LayoutInflater layoutInflater;
    int imageResourceId = 0;

    public detailReportListAdaptor(List<detailReportModel> getlist, Context context) {
        this.context = context;
        this.objects = getlist;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public detailReportListAdaptor.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_detail_report, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull detailReportListAdaptor.DataObjectHolder holder, int position) {

        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.Whitecl));

        holder.txt_PlotNo.setText(objects.get(position).getPlotNo());
        holder.txt_TotalObservation1.setText(objects.get(position).getObsesrvationTaken());

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
        private TextView txt_PlotNo;
        private TextView txt_TotalObservation1;
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
            txt_PlotNo = (TextView) view.findViewById(R.id.txt_PlotNo);
            txt_TotalObservation1 = (TextView) view.findViewById(R.id.txt_TotalObservation1);
           // txt_FarmerName= (TextView) view.findViewById(R.id.txt_FarmerName);
            //txt_Location= (TextView) view.findViewById(R.id.txt_Location);


        }
    }
}
