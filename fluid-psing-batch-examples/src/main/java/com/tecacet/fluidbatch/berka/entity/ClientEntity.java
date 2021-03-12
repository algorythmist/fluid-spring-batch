package com.tecacet.fluidbatch.berka.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "client")
@Getter
@Setter
@ToString
public class ClientEntity {

    public enum Gender {MALE, FEMALE}

    @Id
    private long id;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;

    private String district;
}
