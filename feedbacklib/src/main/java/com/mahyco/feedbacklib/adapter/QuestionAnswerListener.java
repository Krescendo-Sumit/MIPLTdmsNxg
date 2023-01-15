package com.mahyco.feedbacklib.adapter;

import com.mahyco.feedbacklib.model.ModelQNS;

import java.util.ArrayList;

public interface QuestionAnswerListener {
    void onListChange(ArrayList<ModelQNS> list);
}
