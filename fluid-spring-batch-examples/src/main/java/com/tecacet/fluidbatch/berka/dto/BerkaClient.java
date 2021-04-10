package com.tecacet.fluidbatch.berka.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BerkaClient {

    private long id;
    private String gender;
    private LocalDate birthDate;
    private String district;
}
