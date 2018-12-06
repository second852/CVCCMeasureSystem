package com.whc.cvccmeasuresystem.Model;

import java.sql.Timestamp;

public class SaveFile {


    private int ID; // 0 DB id
    private String name;//1 file name
    private String measureType;//2 measure type Common-MeasureType
    private Timestamp statTime;//3 create file time
    private Timestamp endTime;//4 end file time
    private int userId;// 4 user ID



    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasureType() {
        return measureType;
    }

    public void setMeasureType(String measureType) {
        this.measureType = measureType;
    }

    public Timestamp getStatTime() {
        return statTime;
    }

    public void setStatTime(Timestamp statTime) {
        this.statTime = statTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}
