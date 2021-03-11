package com.tecacet.fluidbatch.berka.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class Transaction {

    private String transactionId;

    private String accountId;

    private LocalDate date;

    private String type;

    private BigDecimal amount;

}
