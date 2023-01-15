package com.tdms.mahyco.nxg;

class ReportModel {
    ReportModel() {
    }

    public String getTrail_code() {
        return Trail_code;
    }

    public void setTrail_code(String trail_code) {
        Trail_code = trail_code;
    }

    public String getObsesrvationValue() {
        return ObsesrvationValue;
    }

    public void setObsesrvationValue(String obsesrvationValue) {
        ObsesrvationValue = obsesrvationValue;
    }

    private String Trail_code;
    private String ObsesrvationValue;

    public String getFarmerName() {
        return FarmerName;
    }

    public void setFarmerName(String farmerName) {
        FarmerName = farmerName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    private String FarmerName;
    private String Location;
}
