package com.tecacet.fluidbatch.berka.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "account_transaction")
@Getter
@Setter
@ToString
public class TransactionEntity {

    public enum Type { DEBIT, CREDIT }

    public enum Operation {
        CASH_DEPOSIT,
        CASH_WITHDRAWAL,
        CC_PAYMENT,
        FROM_BANK,
        TO_BANK,
        OTHER
    }

    public enum Category {
        INSURANCE_PAYMENT,
        HOUSEHOLD_PAYMENT,
        STATEMENT_PAYMENT,
        PENSION_PAYMENT,
        LOAN_PAYMENT,
        INTEREST,
        OVERDRAFT_FEE,
        OTHER
    }

    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @Column(name = "transaction_date")
    private LocalDate date;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "transaction_amount")
    private BigDecimal amount;

    @Column(name = "balance_amount")
    private BigDecimal balance;

    private String bank;
}
