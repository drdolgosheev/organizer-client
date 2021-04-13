package com.hse.organazer_client.entities.dto;

public class SingleBooleanDto {
    private Boolean predict;

    public SingleBooleanDto() {}

    public SingleBooleanDto(Boolean predict) {
        this.predict = predict;
    }

    public Boolean getPredict() {
        return predict;
    }

    public void setPredict(Boolean predict) {
        this.predict = predict;
    }
}
