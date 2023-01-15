package com.tdms.mahyco.nxg.model;

public class TrialFeedbackSummaryModel {
    private String trialCode;
    private String location;
    private String village;
    private String segment;
    private String dateOfVisit;
    private String plotNo;
    private String rank;
    private String comment;
    private String trialRating;
    private String isSynced;
    private String isSubmitted;
    private String FBStaffCode;
    private String dateOfVisitSinglePlot;

    public String getDateOfVisitSinglePlot() {
        return dateOfVisitSinglePlot;
    }

    public void setDateOfVisitSinglePlot(String dateOfVisitSinglePlot) {
        this.dateOfVisitSinglePlot = dateOfVisitSinglePlot;
    }

    public String getFBStaffCode() {
        return FBStaffCode;
    }

    public void setFBStaffCode(String FBStaffCode) {
        this.FBStaffCode = FBStaffCode;
    }

    public String getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(String isSynced) {
        this.isSynced = isSynced;
    }

    public String getIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(String isSubmitted) {
        this.isSubmitted = isSubmitted;
    }
    public String getTrialCode() {
        return trialCode;
    }

    public void setTrialCode(String trialCode) {
        this.trialCode = trialCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getDateOfVisit() {
        return dateOfVisit;
    }

    public void setDateOfVisit(String dateOfVisit) {
        this.dateOfVisit = dateOfVisit;
    }

    public String getPlotNo() {
        return plotNo;
    }

    public void setPlotNo(String plotNo) {
        this.plotNo = plotNo;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTrialRating() {
        return trialRating;
    }

    public void setTrialRating(String trialRating) {
        this.trialRating = trialRating;
    }


    @Override
    public String toString() {
        return "TrialFeedbackSummaryModel{" +
                "trialCode='" + trialCode + '\'' +
                ", location='" + location + '\'' +
                ", village='" + village + '\'' +
                ", segment='" + segment + '\'' +
                ", dateOfVisit='" + dateOfVisit + '\'' +
                ", plotNo='" + plotNo + '\'' +
                ", rank='" + rank + '\'' +
                ", comment='" + comment + '\'' +
                ", trialRating='" + trialRating + '\'' +
                ", isSynced='" + isSynced + '\'' +
                ", isSubmitted='" + isSubmitted + '\'' +
                ", FBStaffCode='" + FBStaffCode + '\'' +
                ", dateOfVisitSinglePlot='" + dateOfVisitSinglePlot + '\'' +
                '}';
    }
}
