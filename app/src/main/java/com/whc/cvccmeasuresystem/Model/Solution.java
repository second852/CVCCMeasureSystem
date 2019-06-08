package com.whc.cvccmeasuresystem.Model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;

public class Solution implements Serializable{

    private int ID;//0 DB ID
    private String number;//1 measure times
    private String concentration;//2 pH7 for 7
    private Timestamp time;//3 Time of read voltage
    private Integer voltage;//4 read Voltage
    private int sampleID;//5  measure Sample
    private boolean noNormalV;//6 normal
    private String measureType;//7 measureType
    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getMeasureType() {
        return measureType;
    }

    public void setMeasureType(String measureType) {
        this.measureType = measureType;
    }

    public boolean isNoNormalV() {
        return noNormalV;
    }

    public void setNoNormalV(boolean noNormalV) {
        this.noNormalV = noNormalV;
    }


    public Solution(int sampleID) {
        this.sampleID = sampleID;
    }

    public Solution(String concentration, int sampleID) {
        this.concentration = concentration;
        this.sampleID = sampleID;
    }

    public Solution() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public Integer getVoltage() {
        return voltage;
    }

    public void setVoltage(Integer voltage) {
        this.voltage = voltage;
    }

    public int getSampleID() {
        return sampleID;
    }

    public void setSampleID(int sampleID) {
        this.sampleID = sampleID;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "ID=" + ID +
                ", concentration='" + concentration + '\'' +
                ", voltage=" + voltage +
                '}';
    }
}
