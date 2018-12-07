package com.whc.cvccmeasuresystem.Model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;

public class Solution implements Serializable{

    private int ID;//DB ID
    private String number;// measure times
    private String concentration;// pH7 for 7
    private Timestamp time;//Time of read voltage
    private Integer voltage;// read Voltage
    private int sampleID;//  measure Sample

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

}
