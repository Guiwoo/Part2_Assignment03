package com.guiwoo.stock_dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrapResult {
    private Company company;
    private List<Dividend> dividendEntities;
    public ScrapResult(){
        this.dividendEntities = new ArrayList<>();
    }
}
