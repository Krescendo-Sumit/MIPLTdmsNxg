package com.tdms.mahyco.nxg;

public class TrailReportModel {
    TrailReportModel() {
    }

    private String txt_TrialSegmentDetails;
    private String Trailcode;
    private String Location;
    private String Tagged;
    private String TagType;

    public String getTxt_TrialSegmentDetails() {
        return txt_TrialSegmentDetails;
    }

    public void setTxt_TrialSegmentDetails(String txt_TrialSegmentDetails) {
        this.txt_TrialSegmentDetails = txt_TrialSegmentDetails;
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

    public String getTagType() {
        return TagType;
    }

    public void setTagType(String tagType) {
        TagType = tagType;
    }

    @Override
    public String toString() {
        return "TrailReportModel{" +
                "txt_TrialSegmentDetails='" + txt_TrialSegmentDetails + '\'' +
                ", Trailcode='" + Trailcode + '\'' +
                ", Location='" + Location + '\'' +
                ", Tagged='" + Tagged + '\'' +
                ", TagType='" + TagType + '\'' +
                '}';
    }
}
