package com.tecacet.fluidbatch.examples.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BerkaTransaction {

    private long transId;
    private long accountId;
    private LocalDate date;
    private String type;
    private String operation;
    private BigDecimal amount;
    private BigDecimal balance;
    private String k_symbol;
    private String bank;
    private String account;
}
