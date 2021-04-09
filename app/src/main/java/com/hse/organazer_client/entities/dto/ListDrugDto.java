package com.hse.organazer_client.entities.dto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hse.organazer_client.entities.Drug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListDrugDto {
    List<Drug> drugs = new ArrayList<>();

    public ListDrugDto(){}

    public ListDrugDto(List<Drug> drugs) {
        this.drugs = drugs;
    }

    public List<Drug> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<Drug> drugs) {
        this.drugs = drugs;
    }
}
