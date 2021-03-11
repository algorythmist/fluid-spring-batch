package com.tecacet.fluidbatch.berka.entity;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction")
@Getter
public class TransactionEntity {

    public enum Type { DEBIT, CREDIT }

    @Id
    private UUID id;

    private String transactionIdentifier;

    private String accountNumber;

    @Column(name = "transaction_date")
    private LocalDate date;
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(name = "transaction_amount")
    private BigDecimal amount;

}
