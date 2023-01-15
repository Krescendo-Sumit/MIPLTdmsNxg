package com.mahyco.feedbacklib.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mahyco.feedbacklib.R;
import com.mahyco.feedbacklib.adapter.QuestionAnswerAdapter;
import com.mahyco.feedbacklib.adapter.QuestionAnswerListener;
import com.mahyco.feedbacklib.apis.GetFeedbackService;
import com.mahyco.feedbacklib.apis.RetrofitClientInstance;
import com.mahyco.feedbacklib.model.ModelQNS;
import com.mahyco.feedbacklib.model.feedquesresponse.FeedbackResponseModel;
import com.mahyco.feedbacklib.model.submitfeedresponse.SubmitFeedbackResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogFeedback implements QuestionAnswerListener {
    Context mContext;
    boolean flagIsToastShown = false;
    ArrayList<ModelQNS> modelQNSArrayList = new ArrayList<ModelQNS>();
    RecyclerView recyclerView;
    AlertDialog alertAdd;
    LinearLayout layoutFeedOne;
    LinearLayout layoutFeedTwo;
    LinearLayout llProgressBar;
    String packageName;
    String userId;

    public DialogFeedback(Context context, String packageName, String userId) {
        mContext = context;
        this.packageName = packageName;
        this.userId = userId;
    }

    public void showFeedbackDialog() {
        alertAdd = new AlertDialog.Builder(mContext).create();
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View view = factory.inflate(R.layout.dialog_feedback, null);
        //Button btnPostpone = (Button) view.findViewById(R.id.btn_postpone);
        Button btnStartNow = (Button) view.findViewById(R.id.btn_start_now);
        Button btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        layoutFeedOne = (LinearLayout) view.findViewById(R.id.feedbk_layout_one);
        layoutFeedTwo = (LinearLayout) view.findViewById(R.id.feedbk_layout_two);
        llProgressBar = (LinearLayout) view.findViewById(R.id.llProgressBar);
        layoutFeedOne.setVisibility(View.VISIBLE);
        layoutFeedTwo.setVisibility(View.GONE);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_ques_ans);

        /*btnPostpone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertAdd.cancel();
            }
        });*/
        btnStartNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,"Start now",Toast.LENGTH_LONG).show();
                //Log.d("INTERNET","Network check : "+isNetworkAvailable(mContext));
                if (isNetworkAvailable(mContext)) {
                    llProgressBar.setVisibility(View.VISIBLE);
                    getQuestionsList();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    alertAdd.dismiss();
                }
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("INTERNET","Network check : "+isNetworkAvailable(mContext));
                if (isNetworkAvailable(mContext)) {
                    flagIsToastShown = false;
                    int len = modelQNSArrayList.size();
                    for (int i = 0; i < len; i++) {
                        if (modelQNSArrayList.get(i).getRating() == null) {
                            Toast.makeText(mContext, "Please answer question number " + (i), Toast.LENGTH_LONG).show();// + " \n" +modelQNSArrayList.get(i).getQuestion() , Toast.LENGTH_LONG).show();
                            flagIsToastShown = true;
                            break;
                        }
                    }
                    if (flagIsToastShown == false) {
                        sendDataToServer();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    alertAdd.dismiss();
                }
            }
        });


        alertAdd.setCancelable(false);
        alertAdd.setView(view);
        alertAdd.show();
    }

    private void sendDataToServer() {
        llProgressBar.setVisibility(View.VISIBLE);
        GetFeedbackService service = RetrofitClientInstance.getRetrofitInstance().create(GetFeedbackService.class);
        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        int len = modelQNSArrayList.size();
        //Log.d("Data","LEN :"+len);
        try {
            for (int i = 0; i < len; i++) {
                if (modelQNSArrayList.get(i).getqType().equalsIgnoreCase("Heading")) {
                    continue;
                }
                JsonObject object = new JsonObject();
                object.addProperty("user_id", userId);
                object.addProperty("ques_id", modelQNSArrayList.get(i).getQuesId());
                object.addProperty("rating", modelQNSArrayList.get(i).getRating());
                object.addProperty("packageName", modelQNSArrayList.get(i).getPackageName());

                array.add(object);
            }
            jsonObject.add("ques_ans", array);
        } catch (Exception e) {
            //Log.d("Msg", e.getMessage());
        }
        Log.d("Request", "Request obj: " + jsonObject.toString());
        Call<SubmitFeedbackResponse> call = service.postFeedback(jsonObject);
        call.enqueue(new Callback<SubmitFeedbackResponse>() {
            @Override
            public void onResponse(Call<SubmitFeedbackResponse> call, Response<SubmitFeedbackResponse> response) {
                llProgressBar.setVisibility(View.GONE);
                //Log.d("Response", "RESPONSE : Success:" + response.body());
                //Log.d("Response", "RESPONSE : Success:" + response.toString());
                if (response.body().getSuccess()) {
                    Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent("FeedbackResponse");
                    intent.putExtra("IS_FEEDBACK_GIVEN", true);
                    intent.setComponent(null);
                    mContext.sendBroadcast(intent);
                }
                if (alertAdd != null) {
                    alertAdd.cancel();
                }
            }

            @Override
            public void onFailure(Call<SubmitFeedbackResponse> call, Throwable t) {
                //Log.d("Response", "RESPONSE : Success" + t.getLocalizedMessage());
                Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
                if (alertAdd != null) {
                    alertAdd.cancel();
                }
            }
        });

    }

    private void getQuestionsList() {
        GetFeedbackService service = RetrofitClientInstance.getRetrofitInstance().create(GetFeedbackService.class);
        Log.d("Response", "getQuestionsList RESPONSE for packageName : " +packageName);
        Call<FeedbackResponseModel> call = service.getAllQuestion(packageName);

        call.enqueue(new Callback<FeedbackResponseModel>() {
            @Override
            public void onResponse(Call<FeedbackResponseModel> call, Response<FeedbackResponseModel> response) {
                Log.d("Response", "RESPONSE : " + response.body().getData());
                ArrayList<FeedbackResponseModel.FeedBackData> feedBackDataArrayList = response.body().getData();
                Log.d("Response", "RESPONSE SIZE INNER: " + feedBackDataArrayList.size());
                if(feedBackDataArrayList != null){
                    setDATA(feedBackDataArrayList);
                }
                else{
                    Toast.makeText(mContext,"Question list null",Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext,"Response : "+response.body().getData(),Toast.LENGTH_LONG).show();
                    if (alertAdd != null) {
                        alertAdd.cancel();
                    }
                }
            }

            @Override
            public void onFailure(Call<FeedbackResponseModel> call, Throwable t) {
                //Toast.makeText(mContext, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(mContext, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                llProgressBar.setVisibility(View.GONE);
                alertAdd.dismiss();
            }
        });
    }

    @Override
    public void onListChange(ArrayList<ModelQNS> list) {
        //Log.d("Response", "onListChange ==========");
        /*for (int i = 0; i < list.size(); i++) {
            Log.d("i:" + i, " Data : " + list.get(i).toString());
        }*/
        modelQNSArrayList = list;
    }

    private void setDATA(ArrayList<FeedbackResponseModel.FeedBackData> questionList) {
        try {
            //Log.d("Question List", ":\n" + questionList.toString());
            int len = questionList.size();
            int srNo = 0;
            modelQNSArrayList.clear();
            for (int i = 0; i < len; i++) {
                ModelQNS modelQNS = new ModelQNS();
                if (!questionList.get(i).getQuestionType().equalsIgnoreCase("Heading")) {
                    srNo = srNo + 1;
                    modelQNS.setSrNo("" + srNo);
                } else {
                    modelQNS.setSrNo("0");
                }
                modelQNS.setPackageName("" + packageName);
                //modelQNS.setUserId("" + questionList.get(i).getQuestionId()); /*Commented on 9 july*/
                modelQNS.setQuestion(""+questionList.get(i).getQuestion());
                modelQNS.setQuesId("" + questionList.get(i).getQuestionId());
                modelQNS.setUserId(userId);
                modelQNS.setqType(""+questionList.get(i).getQuestionType());
                //Log.d("Model Data", "Data :" + i + ": " + modelQNS.toString());
                if (questionList.get(i).getQuestionType().equalsIgnoreCase("Heading")) {
                    modelQNS.setRating("Heading");
                }
                modelQNSArrayList.add(modelQNS);
            }
            QuestionAnswerAdapter mAdapter = new QuestionAnswerAdapter(mContext, modelQNSArrayList, alertAdd);
            mAdapter.setListener(this);
            recyclerView.setAdapter(mAdapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);

            layoutFeedOne.setVisibility(View.GONE);
            layoutFeedTwo.setVisibility(View.VISIBLE);
            llProgressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
        }

    }

    private boolean isNetworkAvailable(Context mContext) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}

