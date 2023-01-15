package com.mahyco.feedbacklib.model;

public class ModelQNS {
    String userId;
    String quesId;
    String question;
    String rating;
    String qType;
    String packageName;

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    String srNo;

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getqType() {
        return qType;
    }

    public void setqType(String qType) {
        this.qType = qType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "ModelQNS{" +
                "userId='" + userId + '\'' +
                ", quesId='" + quesId + '\'' +
                ", question='" + question + '\'' +
                ", rating='" + rating + '\'' +
                ", qType='" + qType + '\'' +
                '}';
    }
}
