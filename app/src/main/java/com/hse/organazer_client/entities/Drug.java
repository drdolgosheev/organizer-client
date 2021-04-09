package com.hse.organazer_client.entities;

import java.util.Date;

public class Drug {
    String name;
    String barcode;
    String description;
    Date prodDate;
    Date expDate;
    Integer numOfPills;
    Integer numOfPillsPerDay;
    Date startTakePillsTime;
    Integer takePillsInterval;

    public Drug(){}

    public Drug(String name, String barcode, String description, Date prodDate, Date expDate,
                Integer numOfPills, Integer numOfPillsPerDay,
                Date startTakePillsTime, Integer takePillsInterval) {
        this.name = name;
        this.barcode = barcode;
        this.description = description;
        this.prodDate = prodDate;
        this.expDate = expDate;
        this.numOfPills = numOfPills;
        this.numOfPillsPerDay = numOfPillsPerDay;
        this.startTakePillsTime = startTakePillsTime;
        this.takePillsInterval = takePillsInterval;
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

    public Date getProdDate() {
        return prodDate;
    }

    public void setProdDate(Date prodDate) {
        this.prodDate = prodDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Integer getNumOfPills() {
        return numOfPills;
    }

    public void setNumOfPills(Integer numOfPills) {
        this.numOfPills = numOfPills;
    }

    public Integer getNumOfPillsPerDay() {
        return numOfPillsPerDay;
    }

    public void setNumOfPillsPerDay(Integer numOfPillsPerDay) {
        this.numOfPillsPerDay = numOfPillsPerDay;
    }

    public Date getStartTakePillsTime() {
        return startTakePillsTime;
    }

    public void setStartTakePillsTime(Date startTakePillsTime) {
        this.startTakePillsTime = startTakePillsTime;
    }

    public Integer getTakePillsInterval() {
        return takePillsInterval;
    }

    public void setTakePillsInterval(Integer takePillsInterval) {
        this.takePillsInterval = takePillsInterval;
    }
}
