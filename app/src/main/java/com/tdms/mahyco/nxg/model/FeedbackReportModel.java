package com.tdms.mahyco.nxg.model;

public class FeedbackReportModel {

    public FeedbackReportModel() {
    }

    private String txt_TrialSegmentDetails;
    private String Trailcode;
    private String Location;
    private String Tagged;
    private String village;
    private String currentStage;
    private String das;

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentState) {
        this.currentStage = currentState;
    }

    public String getDas() {
        return das;
    }

    public void setDas(String das) {
        this.das = das;
    }

    public String getTrailcode() {
        return Trailcode;
    }

    public void setTrailcode(String trailcode) {
        Trailcode = trailcode;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getTagged() {
        return Tagged;
    }

    public void setTagged(String tagged) {
        Tagged = tagged;
    }

    public String getTxt_TrialSegmentDetails() {
        return txt_TrialSegmentDetails;
    }

    public void setTxt_TrialSegmentDetails(String txt_TrialSegmentDetails) {
        this.txt_TrialSegmentDetails = txt_TrialSegmentDetails;
    }

    @Override
    public String toString() {
        return "FeedbackReportModel{" +
                "txt_TrialSegmentDetails='" + txt_TrialSegmentDetails + '\'' +
                ", Trailcode='" + Trailcode + '\'' +
                ", Location='" + Location + '\'' +
                ", Tagged='" + Tagged + '\'' +
                ", village='" + village + '\'' +
                ", currentState='" + currentStage + '\'' +
                ", das='" + das + '\'' +
                '}';
    }
}
