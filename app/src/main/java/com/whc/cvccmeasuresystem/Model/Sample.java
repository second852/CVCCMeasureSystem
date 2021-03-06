package com.whc.cvccmeasuresystem.Model;


import android.util.Log;

import java.util.Objects;

/**
 * Created by Wang on 2018/12/4.
 */

public class Sample {

    private int ID;//0 DB id
    private String name;//1 sample name
    private String ionType;//2 solution type Common-SolutionType
    private int fileID;//3 file measure id
    private String location;
    private String limitHighVoltage;
    private String limitLowVoltage;
    private String slope;
    private String intercept;
    private String standardDeviation;
    private String differenceX;
    private String differenceY;
    private String R;
    private String unit;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getR() {
        return R;
    }

    public void setR(String r) {
        R = r;
    }

    public String getIntercept() {
        return intercept;
    }

    public void setIntercept(String intercept) {
        this.intercept = intercept;
    }

    public String getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(String standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public String getDifferenceX() {
        return differenceX;
    }

    public void setDifferenceX(String differenceX) {
        this.differenceX = differenceX;
    }

    public String getDifferenceY() {
        return differenceY;
    }

    public void setDifferenceY(String differenceY) {
        this.differenceY = differenceY;
    }

    public String getSlope() {
        return slope;
    }

    public void setSlope(String slope) {
        this.slope = slope;
    }

    public String getLimitHighVoltage() {
        return limitHighVoltage;
    }

    public void setLimitHighVoltage(String limitHighVoltage) {
        this.limitHighVoltage = limitHighVoltage;
    }

    public String getLimitLowVoltage() {
        return limitLowVoltage;
    }

    public void setLimitLowVoltage(String limitLowVoltage) {
        this.limitLowVoltage = limitLowVoltage;
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

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getIonType() {
        return ionType;
    }

    public void setIonType(String ionType) {
        this.ionType = ionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sample sample = (Sample) o;
        return ID == sample.getID();
    }

    @Override
    public int hashCode() {

        return Objects.hash(ID);
    }
}
