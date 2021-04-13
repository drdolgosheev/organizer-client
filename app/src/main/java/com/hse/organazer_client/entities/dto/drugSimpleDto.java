package com.hse.organazer_client.entities.dto;

public class drugSimpleDto {
    String name;
    String barcode;
    String description;
    String prodDate;
    String expDate;
    String numOfPills;
    String numOfPillsPerDay;
    String startTakePillsTime;
    String takePillsInterval;

    public drugSimpleDto(){}

    public drugSimpleDto(String name, String barcode, String description, String prodDate, String expDate,
                Integer numOfPills, Integer numOfPillsPerDay,
                String startTakePillsTime, Integer takePillsInterval) {
        this.name = name;
        this.barcode = barcode;
        this.description = description;
        this.prodDate = prodDate;
        this.expDate = expDate;
        this.numOfPills = String.valueOf(numOfPills);
        this.numOfPillsPerDay = String.valueOf(numOfPillsPerDay);
        this.startTakePillsTime = startTakePillsTime;
        this.takePillsInterval = String.valueOf(takePillsInterval);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProdDate() {
        return prodDate;
    }

    public void setProdDate(String prodDate) {
        this.prodDate = prodDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public Integer getNumOfPills() {
        return Integer.valueOf(numOfPills);
    }

    public void setNumOfPills(Integer numOfPills) {
        this.numOfPills = String.valueOf(numOfPills);
    }

    public Integer getNumOfPillsPerDay() {
        return Integer.valueOf(numOfPillsPerDay);
    }

    public void setNumOfPillsPerDay(Integer numOfPillsPerDay) {
        this.numOfPillsPerDay = String.valueOf(numOfPillsPerDay);
    }

    public String getStartTakePillsTime() {
        return startTakePillsTime;
    }

    public void setStartTakePillsTime(String startTakePillsTime) {
        this.startTakePillsTime = startTakePillsTime;
    }

    public Integer getTakePillsInterval() {
        return Integer.valueOf(takePillsInterval);
    }

    public void setTakePillsInterval(Integer takePillsInterval) {
        this.takePillsInterval = String.valueOf(takePillsInterval);
    }
}
