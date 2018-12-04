package com.whc.cvccmeasuresystem.Model;

import java.security.Timestamp;

public class Solution {

    private int ID;//DB ID
    private String type;// solution type Common-SolutionType
    private Timestamp beginTime;//begin measure Time
    private Timestamp endTime;// end measure Time

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}
