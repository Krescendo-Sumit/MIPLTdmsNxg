package com.tdms.mahyco.nxg;


/**
 * Created by akash on 04-02-2019.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


import java.util.List;


public class MapUserAdapter extends RecyclerView.Adapter<MapUserAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MapLocationModel item);
    }

  //  private final OnItemClickListener listener;
    RecyclerView mRecyclerView;
    Context mContext;
    private List<MapLocationModel> mList;
    GoogleMap mMap;
    LatLng currentLocation;


    public MapUserAdapter(Context context, List<MapLocationModel> mList, GoogleMap mMap, LatLng latLng) {

        this.mContext = context;
        this.mList = mList;
        this.mMap = mMap;
      //  this.listener = listener;
        this.currentLocation = latLng;
        // setHasStableIds(true);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_map_items, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.name.setText(mList.get(position).getName());
        viewHolder.txtTrialType.setText(mList.get(position).getTrialType().toUpperCase());
        viewHolder.txtcrop.setText(mList.get(position).getCrop());
        viewHolder.txtVillage.setText(mList.get(position).getVillage());

        viewHolder.txtTrialCode.setText(mList.get(position).getTrialCode());



        viewHolder.txtsesion.setText(mList.get(position).getSesion());
        viewHolder.txtyear.setText(mList.get(position).getYear());
        viewHolder.txtentry.setText(mList.get(position).getEntry());
        viewHolder.txtreplecation.setText(mList.get(position).getReplecation());
        viewHolder.txtlocation.setText(mList.get(position).getLocation());
        viewHolder.txtnursery.setText(mList.get(position).getNursery());
        viewHolder.txtsegment.setText(mList.get(position).getSegment());
        viewHolder.txtfmobile.setText(mList.get(position).getFmobile());
        viewHolder.txtsowingdate.setText(mList.get(position).getFsowingdate());
        viewHolder.textfarea.setText(mList.get(position).getFarea());
        viewHolder.distance.setText(mList.get(position).getDistance()+" KM");
       // viewHolder.bind(mList.get(position), listener);

//        if (mList.get(position).isInRange()) {
//            viewHolder.cardView.setVisibility(View.VISIBLE);
//        } else
//            viewHolder.cardView.setVisibility(View.GONE);


        viewHolder.direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+String.valueOf(currentLocation.latitude)+","
                                +String.valueOf(currentLocation.longitude)+
                                "&daddr="+mList.get(position).getLattitude().trim()+","
                                +mList.get(position).getLongitude().trim()));
                                mContext.startActivity(intent);

//                Intent intent = new Intent(mContext, MapDirection.class);
//                intent.putExtra("latitude", mList.get(position).getLattitude().trim());
//                intent.putExtra("longitude", mList.get(position).getLongitude().trim());
//                intent.putExtra("name", mList.get(position).getName().trim());
//                intent.putExtra("distancebetween",mList.get(position).getDistance().trim());
//                intent.putExtra("currentlat", String.valueOf(currentLocation.latitude));
//                intent.putExtra("currentlng", String.valueOf(currentLocation.longitude));
//                mContext.startActivity(intent);


            }

        });
        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {


                ((MapsActivity) mContext).moveOnMarker(new LatLng(Double.parseDouble(mList.get(position).getLattitude()), Double.parseDouble(mList.get(position).getLongitude())));


            }

        });
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView txtTrialType;
        private TextView txtVillage;
        private TextView txtTrialCode;
        private TextView distance;
        private TextView txtcrop;
        private TextView txtsesion,txtyear,txtentry,txtreplecation,txtlocation,
                txtnursery,txtsegment,txtfmobile,txtsowingdate,textfarea;
        private CardView cardView;
        private ImageView direction;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card);

            direction = (ImageView) view.findViewById(R.id.direction);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
            name = (TextView) view.findViewById(R.id.name);
            txtTrialType = (TextView) view.findViewById(R.id.txtTrialType);
            txtVillage = (TextView) view.findViewById(R.id.txtVillage);
            txtTrialCode = (TextView) view.findViewById(R.id.txtTrialCode);
            txtcrop = (TextView) view.findViewById(R.id.txtcrop);
            txtsesion=(TextView) view.findViewById(R.id.txtsesion);
            txtyear=(TextView) view.findViewById(R.id.txtyear);
            txtentry=(TextView) view.findViewById(R.id.txtentry);
            txtreplecation=(TextView) view.findViewById(R.id.txtreplecation);
            distance=(TextView) view.findViewById(R.id.distance);
            txtlocation=(TextView) view.findViewById(R.id.txtlocation);
            txtnursery=(TextView) view.findViewById(R.id.txtnursery);
            txtsegment=(TextView) view.findViewById(R.id.txtsegment);
            txtfmobile=(TextView) view.findViewById(R.id.txtfmobile);
            txtsowingdate=(TextView) view.findViewById(R.id.txtsowingdate);
            textfarea=(TextView) view.findViewById(R.id.textfarea);

        }

        public void bind(final MapLocationModel item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }


    }


}






