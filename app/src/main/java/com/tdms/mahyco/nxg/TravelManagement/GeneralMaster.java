package com.tdms.mahyco.nxg.TravelManagement;

/**
 * Created by 97260828 on 01-12-2017.
 */

public class GeneralMaster {
    private final String Desc;
    private final String code;

    public GeneralMaster(String code, String Desc){
        this.code = code;
        this.Desc = Desc;
    }
    public String Desc() {
        return Desc;
    }

    public String Code() {
        return code;
    }
    @Override
    public String toString() {
        return Desc;
    }
}
