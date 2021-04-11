package com.tecacet.springbatch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BankTransaction {

    private String transactionId;

    private String accountId;

    private LocalDate date;

    private String type;

    private BigDecimal amount;

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
