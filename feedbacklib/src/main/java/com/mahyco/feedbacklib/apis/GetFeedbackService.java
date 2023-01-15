package com.mahyco.feedbacklib.apis;

import com.google.gson.JsonObject;
import com.mahyco.feedbacklib.model.feedquesresponse.FeedbackResponseModel;
import com.mahyco.feedbacklib.model.submitfeedresponse.SubmitFeedbackResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetFeedbackService {

    @GET("/api/Feedback/getFeedbackQuestion")
    Call<FeedbackResponseModel> getAllQuestion(@Query("packageName") String packageName);

    //@Headers({"Accept: application/json"})
    @POST("/api/Feedback/feedbackAnswer")
    @Headers({"Content-Type: application/json","Accept: application/json"})
    Call<SubmitFeedbackResponse> postFeedback(@Body JsonObject jsonObject);

//    -header 'Content-Type: application/json' --header 'Accept: application/json'
}
