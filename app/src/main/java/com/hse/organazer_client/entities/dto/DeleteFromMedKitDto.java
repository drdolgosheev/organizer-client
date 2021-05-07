package com.hse.organazer_client.entities.dto;

public class DeleteFromMedKitDto {
    String username;
    String barcode;

    public DeleteFromMedKitDto(){}

    public DeleteFromMedKitDto(String username, String barcode) {
        this.username = username;
        this.barcode = barcode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
