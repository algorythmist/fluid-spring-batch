package com.tecacet.fluidbatch.berka.processor;

import com.tecacet.fluidbatch.berka.dto.BerkaDisponent;
import com.tecacet.fluidbatch.berka.entity.AccountEntity;
import com.tecacet.fluidbatch.berka.entity.ClientAccountEntity;
import com.tecacet.fluidbatch.berka.entity.ClientEntity;
import com.tecacet.fluidbatch.berka.repository.AccountRepository;
import com.tecacet.fluidbatch.berka.repository.ClientRepository;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class ClientAccountProcessor implements ItemProcessor<BerkaDisponent, ClientAccountEntity> {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Autowired
    private TransientDataStore transientDataStore;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        transientDataStore.storeData();
    }

    @Override
    public ClientAccountEntity process(BerkaDisponent disponent) {
        Map<Long, AccountEntity> accounts = transientDataStore.getAccounts();
        Map<Long, ClientEntity> clients = transientDataStore.getClients();
        ClientAccountEntity clientAccount = new ClientAccountEntity();
        AccountEntity account = accounts.get(disponent.getAccountId());
        ClientEntity client = clients.get(disponent.getClientId());
        if (account == null || client == null) {
            log.error("Missing client or account for " + disponent.getDispId());
            return null;
        }
        clientAccount.setAccount(account);
        clientAccount.setClient(client);
        clientAccount.setId(disponent.getDispId());
        clientAccount.setOwnerType(ClientAccountEntity.OwnerType.valueOf(disponent.getType()));
        return clientAccount;
    }
}
