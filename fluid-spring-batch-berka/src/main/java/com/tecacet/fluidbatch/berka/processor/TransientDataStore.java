package com.tecacet.fluidbatch.berka.processor;

import com.tecacet.fluidbatch.berka.entity.AccountEntity;
import com.tecacet.fluidbatch.berka.entity.ClientEntity;
import com.tecacet.fluidbatch.berka.repository.AccountRepository;
import com.tecacet.fluidbatch.berka.repository.ClientRepository;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@JobScope
@RequiredArgsConstructor
public class TransientDataStore {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    private Map<Long, ClientEntity> clients;
    private Map<Long, AccountEntity> accounts;

    public void storeData() {
        List<ClientEntity> clients = clientRepository.findAll();
        List<AccountEntity> accounts = accountRepository.findAll();
        this.clients = clients.stream().collect(Collectors.toMap(ClientEntity::getId, Function.identity()));
        this.accounts =  accounts.stream().collect(Collectors.toMap(AccountEntity::getId, Function.identity()));
    }

    public Map<Long, ClientEntity> getClients() {
        return clients;
    }

    public Map<Long, AccountEntity> getAccounts() {
        return accounts;
    }
}
