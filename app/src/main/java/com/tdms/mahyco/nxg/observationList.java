package com.tdms.mahyco.nxg;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class observationList extends AppCompatActivity {

    String name;
    int distance;
    boolean selected = false;

    public observationList(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
class PlanetAdapter extends ArrayAdapter<observationList> {

    private List<observationList> planetList;
    private Context context;

    public PlanetAdapter(List<observationList> planetList, Context context) {
        super(context, R.layout.list_item, planetList);
        this.planetList = planetList;
        this.context = context;
    }

    private static class PlanetHolder {
        public TextView planetName;
        public CheckBox chkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        PlanetHolder holder = new PlanetHolder();
        try {
            if (convertView == null) {


                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item, null);

                holder.planetName = (TextView) v.findViewById(R.id.txtObservation);
                holder.chkBox = (CheckBox) v.findViewById(R.id.chk_box);

                // holder.chkBox.setOnCheckedChangeListener(observationList.class);

            } else {
                holder = (PlanetHolder) v.getTag();
            }

            observationList p = planetList.get(position);
            holder.planetName.setText(p.getName());
            holder.chkBox.setChecked(p.isSelected());
            holder.chkBox.setTag(p);

            //return v;
        } catch (Exception e) {
            Log.d("Msg",e.getMessage());
        }
        return v;
    }
}
