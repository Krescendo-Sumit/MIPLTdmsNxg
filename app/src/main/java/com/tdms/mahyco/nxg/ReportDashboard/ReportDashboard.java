package com.tdms.mahyco.nxg.ReportDashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tdms.mahyco.nxg.CommonExecution;
import com.tdms.mahyco.nxg.Config;
import com.tdms.mahyco.nxg.Messageclass;
import com.tdms.mahyco.nxg.R;
import com.tdms.mahyco.nxg.databaseHelper;
import com.tdms.mahyco.nxg.utils.AnimationItem;
import com.tdms.mahyco.nxg.utils.ItemOffsetDecoration;

import java.util.ArrayList;


public class ReportDashboard extends AppCompatActivity {
    public Button btnshort, btndetailreport, btnsaleorderrpt, btnWeeklyRpt, btnretaileranddistributor, btnPVAReport, btnactivityProgress, btnInnovationrpt;
    ProgressDialog dialog;

    public Messageclass msclass;
    public CommonExecution cx;
    private databaseHelper mDatabase;
    TextView lblmsg;
    String returnstring;
    //public String MDOurlpath = "";
    Config config;
    SharedPreferences sp;
    RecyclerView gridView;
    String userRole;
    ArrayList<String> mlist = new ArrayList<>();
    ArrayList<String> mlist2 = new ArrayList<>();
    private AnimationItem[] mAnimationItems;
    ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_dashboard);
        //   getSupportActionBar().hide(); //<< this
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("Report Dashboard");
        //  getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        mDatabase = new databaseHelper(this);

        Cursor data = mDatabase.fetchusercode();

        if (data.getCount() == 0) {

        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    userRole = data.getString((data.getColumnIndex("USER_ROLE")));


                    Log.d("Role", "RoleReportActivityDASHBord" + userRole);
                } while (data.moveToNext());

            }
            data.close();


        }
        msclass = new Messageclass(this);
        cx = new CommonExecution(this);
        //MDOurlpath = cx.MDOurlpath;

        //  mDatabase = new SqliteDatabase(this);
        config = new Config(this); //Here the context is passing
        dialog = new ProgressDialog(this);
        //lblmsg=(TextView)findViewById(R.id.lblmsg) ;
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        sp = getApplicationContext().getSharedPreferences("MyPref", 0);

        setList();
        mAnimationItems = new AnimationItem[]{
                new AnimationItem("Slide from bottom", R.anim.grid_layout_animation_from_bottom)};

        gridView = (RecyclerView) findViewById(R.id.gridView);
        gridView.setLayoutManager(new GridLayoutManager(this, 1));

        showGrid();
        runLayoutAnimation(gridView, mAnimationItems[0]);
    }

    private void showGrid() {
        //Adding adapter to gridview
        try {
            final Context context = gridView.getContext();
            final int spacing = 4;


            gridView.setAdapter(new ReportDashBoardAdapter(this, mlist, mlist2));//,prgmImages));
            gridView.addItemDecoration(new ItemOffsetDecoration(spacing));

        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void setList() {
        mlist.clear();
        /*Start Null check */
        if (userRole != null) {
            if ((userRole.equals("1")) || (userRole.equals("2")) || (userRole.equals("3"))) {
                mlist.add("OBSERVATION REPORT ");
                mlist.add("MY TRAVEL REPORT");
            } else if ((userRole.equals("5")) || (userRole.equals("7"))) {
                mlist.add("OBSERVATION REPORT ");

                if ((userRole.equals("6")) || (userRole.equals("4")) || (userRole.equals("7")) || (userRole.equals("5"))) {
                    mlist.add("MY TRAVEL REPORT");
                }
            } else if ((userRole.equals("6")) || (userRole.equals("4"))) {

                mlist.add("MY TRAVEL REPORT");
                if ((userRole.equals("1")) || (userRole.equals("5"))) {
                    mlist.add("MY TRAVEL REPORT");
                }
            }
        }
        /*End Null check */
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, final AnimationItem item) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.grid_layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}
