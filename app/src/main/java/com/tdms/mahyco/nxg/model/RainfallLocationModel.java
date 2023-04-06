package com.tdms.mahyco.nxg.model;

public class RainfallLocationModel {

    public int getLocationId() {
        return LocationId;
    }

    public void setLocationId(int locationId) {
        LocationId = locationId;
    }

    public String getLocationTitle() {
        return LocationTitle;
    }

    public void setLocationTitle(String locationTitle) {
        LocationTitle = locationTitle;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public int getContryId() {
        return ContryId;
    }

    public void setContryId(int contryId) {
        ContryId = contryId;
    }

    public String getRegionId() {
        return RegionId;
    }

    public void setRegionId(String regionId) {
        RegionId = regionId;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    public int getParentId() {
        return ParentId;
    }

    public void setParentId(int parentId) {
        ParentId = parentId;
    }

    int LocationId;//": 3,
    String LocationTitle;//": "Bantare",
    String Status;//":true,
    String CreatedBy;//": "97260897",
    String CreatedDate;//": "2023-01-10",
    int ContryId;//": 1,
    String RegionId;//": "SZ",
    String RegionName;//": "South Zone",
            int ParentId;//":10

    public String toString(){
        return LocationTitle;
    }

}
