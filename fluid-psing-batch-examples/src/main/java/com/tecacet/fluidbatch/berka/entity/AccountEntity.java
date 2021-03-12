package com.tecacet.fluidbatch.berka.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "account")
@Getter
@Setter
@ToString
public class AccountEntity {

    public enum StatementFrequency {
        WEEKLY, MONTHLY, AFTER_TRANSACTION
    }

    @Id
    private long id;

    private LocalDate dateOpened;

    private StatementFrequency statementFrequency;

    private String district;
}
