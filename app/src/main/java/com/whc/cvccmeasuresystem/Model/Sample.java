package com.whc.cvccmeasuresystem.Model;

import java.util.Objects;

/**
 * Created by Wang on 2018/12/4.
 */

public class Sample {

    private int ID;//0 DB id
    private String name;//1 sample name
    private String ionType;//2 solution type Common-SolutionType
    private int fileID;//3 file measure id
    private String slope;
    private String limitHighVoltage;
    private String limitLowVoltage;

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

        if (ID != sample.ID) return false;
        if (fileID != sample.fileID) return false;
        if (name != null ? !name.equals(sample.name) : sample.name != null) return false;
        return ionType != null ? ionType.equals(sample.ionType) : sample.ionType == null;
    }

    @Override
    public int hashCode() {
        int result = ID;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (ionType != null ? ionType.hashCode() : 0);
        result = 31 * result + fileID;
        return result;
    }
}
