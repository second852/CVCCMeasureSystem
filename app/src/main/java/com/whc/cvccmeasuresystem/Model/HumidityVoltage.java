package com.whc.cvccmeasuresystem.Model;



public class HumidityVoltage {

    private Long id;
    private Integer voltage1;
    private Integer voltage2;
    private Integer voltage3;
    private Integer voltage4;
    private int fileId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVoltage1() {
        return voltage1;
    }

    public void setVoltage1(Integer voltage1) {
        this.voltage1 = voltage1;
    }

    public Integer getVoltage2() {
        return voltage2;
    }

    public void setVoltage2(Integer voltage2) {
        this.voltage2 = voltage2;
    }

    public Integer getVoltage3() {
        return voltage3;
    }

    public void setVoltage3(Integer voltage3) {
        this.voltage3 = voltage3;
    }

    public Integer getVoltage4() {
        return voltage4;
    }

    public void setVoltage4(Integer voltage4) {
        this.voltage4 = voltage4;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
}
