package com.tecacet.fluidbatch.berka.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "client_account")
@Getter
@Setter
@ToString
public class ClientAccountEntity {

    public enum OwnerType {
        OWNER, DISPONENT
    }

    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    private OwnerType ownerType;
}
