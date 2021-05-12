package com.tecacet.springbatch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BankTransaction {

    public enum Type {
        CREDIT, DEBIT
    }


    private String transactionId;

    private String accountId;

    private LocalDate date;

    private Type type;

    private BigDecimal amount;

    private String bank;

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Type getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTypeAsString() {
        return type.toString();
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBank() { return bank; }

    public void setBank(String bank) { this.bank = bank; }
}
