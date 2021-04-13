package com.hse.organazer_client.entities.dto;

import com.hse.organazer_client.entities.Drug;

public class addDrugToMedKitDto {
    drugSimpleDto drug;
    String username;

    public addDrugToMedKitDto(drugSimpleDto drug, String username) {
        this.drug = drug;
        this.username = username;
    }

    public drugSimpleDto getDrug() {
        return drug;
    }

    public void setDrug(drugSimpleDto drug) {
        this.drug = drug;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
