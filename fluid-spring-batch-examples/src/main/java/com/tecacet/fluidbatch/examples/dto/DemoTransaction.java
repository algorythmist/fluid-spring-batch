package com.tecacet.fluidbatch.examples.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DemoTransaction {

    private String transactionId;

    private String accountId;

    private LocalDate date;

    private String type;

    private BigDecimal amount;

}
