package com.whc.cvccmeasuresystem.Model;

import java.sql.Timestamp;

public class HumidityVO {

    private Long id;
    private String name;
    private Timestamp beginTime;
    private Integer baseVoltage;
    private Integer overVoltage;
    private boolean isLight;
    private int fileID;//3 file measure id

    public boolean isLight() {
        return isLight;
    }

    public void setLight(boolean light) {
        isLight = light;
    }

    public Integer getBaseVoltage() {
        return baseVoltage;
    }

    public void setBaseVoltage(Integer baseVoltage) {
        this.baseVoltage = baseVoltage;
    }

    public Integer getOverVoltage() {
        return overVoltage;
    }

    public void setOverVoltage(Integer overVoltage) {
        this.overVoltage = overVoltage;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public Timestamp getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
    }
}
