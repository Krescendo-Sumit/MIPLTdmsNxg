package com.tdms.mahyco.nxg;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

public class survey extends AppCompatActivity {

    public RadioGroup rbtQ2,rbtQ5,rbtQ6,rbtQ7,rbtQ11a,rbtQ11b,rbtQ11c,rbtQ11d,rbtQ11e,rbtQ12;
    public TextView lblQ1,lblQ2,lblQ3,lblQ4,lblQ5,lblQ6,lblQ7,lblQ8,lblQ9,lblQ10,lblQ11,lblQ12,lblQ11a,lblQ11b,lblQ11c,lblQ11d,lblQ11e ;
    public Switch switchQ1,switchQ3,switchQ4,switchQ8,switchQ9;
    public Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        getSupportActionBar().setTitle("BE Survey VOTG");

        rbtQ2=(RadioGroup)findViewById(R.id.rbtQ2);
        rbtQ5=(RadioGroup)findViewById(R.id.rbtQ5);
        rbtQ6=(RadioGroup)findViewById(R.id.rbtQ6);
        rbtQ7=(RadioGroup)findViewById(R.id.rbtQ7);
        rbtQ11a=(RadioGroup)findViewById(R.id.rbtQ11a);
        rbtQ11b=(RadioGroup)findViewById(R.id.rbtQ11b);
        rbtQ11c=(RadioGroup)findViewById(R.id.rbtQ11c);
        rbtQ11d=(RadioGroup)findViewById(R.id.rbtQ11d);
        rbtQ11e=(RadioGroup)findViewById(R.id.rbtQ11e);
        rbtQ12=(RadioGroup)findViewById(R.id.rbtQ12);

        switchQ1=(Switch)findViewById(R.id.switchQ1);
        switchQ3=(Switch)findViewById(R.id.switchQ3);
        switchQ4=(Switch)findViewById(R.id.switchQ4);
        switchQ8=(Switch)findViewById(R.id.switchQ8);
        switchQ9=(Switch)findViewById(R.id.switchQ9);

        lblQ1=(TextView)findViewById(R.id.lblQ1);
        lblQ2=(TextView)findViewById(R.id.lblQ2);
        lblQ3=(TextView)findViewById(R.id.lblQ3);
        lblQ4=(TextView)findViewById(R.id.lblQ4);
        lblQ5=(TextView)findViewById(R.id.lblQ5);
        lblQ6=(TextView)findViewById(R.id.lblQ6);
        lblQ7=(TextView)findViewById(R.id.lblQ7);
        lblQ8=(TextView)findViewById(R.id.lblQ8);
        lblQ9=(TextView)findViewById(R.id.lblQ9);
        lblQ10=(TextView)findViewById(R.id.lblQ10);
        lblQ11=(TextView)findViewById(R.id.lblQ11);
        lblQ11a=(TextView)findViewById(R.id.lblQ11a);
        lblQ11b=(TextView)findViewById(R.id.lblQ11b);
        lblQ11c=(TextView)findViewById(R.id.lblQ11c);
        lblQ11d=(TextView)findViewById(R.id.lblQ11d);
        lblQ11e=(TextView)findViewById(R.id.lblQ11e);
        lblQ12=(TextView)findViewById(R.id.lblQ12);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);
    }


//    private boolean validation() {
//        boolean flag = true;
//        int selectedId=rbtQ2.getCheckedRadioButtonId();
//        //rbtQ2=(RadioButton)findViewById(selectedId);
//        if (rbtQ2.gets == 0) {
//           // msclass.showMessage("Please Enter Farmername ");
//            return false;
//
//        }
//        return true;
//    }
}
