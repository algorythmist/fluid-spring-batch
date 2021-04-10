package com.tecacet.fluidbatch.berka.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BerkaAccount {

    private long accountId;
    private String frequency;
    private LocalDate date;
    private String district;

}
