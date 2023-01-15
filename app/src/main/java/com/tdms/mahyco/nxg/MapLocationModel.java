package com.tdms.mahyco.nxg;


import java.io.Serializable;

/**
 * Created by rohit on 6/20/17.
 */
public class MapLocationModel implements Serializable {


    private String trialType;
    private String trialCode;
    private String crop;
    private String village;
    private String sesion;
    private String entry;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    private String year;

    public String getSesion() {
        return sesion;
    }

    public void setSesion(String sesion) {
        this.sesion = sesion;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getReplecation() {
        return replecation;
    }

    public void setReplecation(String replecation) {
        this.replecation = replecation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNursery() {
        return nursery;
    }

    public void setNursery(String nursery) {
        this.nursery = nursery;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getFmobile() {
        return fmobile;
    }

    public void setFmobile(String fmobile) {
        this.fmobile = fmobile;
    }

    public String getFsowingdate() {
        return fsowingdate;
    }

    public void setFsowingdate(String fsowingdate) {
        this.fsowingdate = fsowingdate;
    }

    public String getFarea() {
        return farea;
    }

    public void setFarea(String farea) {
        this.farea = farea;
    }

    private String replecation;
    private String location;
    private String nursery;
    private String segment;
    private String fmobile;
    private String fsowingdate;
    private String farea;



    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    private String distance;

    public String getTrialCode() {
        return trialCode;
    }

    public void setTrialCode(String trialCode) {
        this.trialCode = trialCode;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    private String serverUsersGEODetailsId;

    private String lattitude;

    private String longitude;
    private String dateTime;

    public boolean isInRange() {
        return isInRange;
    }

    public void setInRange(boolean inRange) {
        isInRange = inRange;
    }

    boolean isInRange;

    public String getTrialType() {
        return trialType;
    }

    public void setTrialType(String trialType) {
        this.trialType = trialType;
    }

    public String getServerUsersGEODetailsId() {
        return serverUsersGEODetailsId;
    }

    public void setServerUsersGEODetailsId(String serverUsersGEODetailsId) {
        this.serverUsersGEODetailsId = serverUsersGEODetailsId;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "lat :" + lattitude + " long :" + longitude;
    }


}

