package com.tecacet.springbatch.dto;

import java.math.BigDecimal;

public class MonthlyCashFlow {

    private final int year;
    private final int month;
    private final BigDecimal netAmount;

    public MonthlyCashFlow(int year, int month, BigDecimal netAmount) {
        this.year = year;
        this.month = month;
        this.netAmount = netAmount;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }
}
