package com.tecacet.fluidbatch.berka.etl;

import com.tecacet.fluidbatch.berka.dto.BerkaDisponent;
import com.tecacet.fluidbatch.berka.entity.AccountEntity;
import com.tecacet.fluidbatch.berka.entity.ClientAccountEntity;
import com.tecacet.fluidbatch.berka.entity.ClientEntity;
import com.tecacet.fluidbatch.berka.repository.AccountRepository;
import com.tecacet.fluidbatch.berka.repository.ClientRepository;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class ClientAccountProcessor implements ItemProcessor<BerkaDisponent, ClientAccountEntity> {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    private ExecutionContext context;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        context = jobExecution.getExecutionContext();
        List<ClientEntity> clients = clientRepository.findAll();
        List<AccountEntity> accounts = accountRepository.findAll();
        context.put("clients", clients.stream().collect(Collectors.toMap(ClientEntity::getId, Function.identity())));
        context.put("accounts", accounts.stream().collect(Collectors.toMap(AccountEntity::getId, Function.identity())));
    }

    @Override
    public ClientAccountEntity process(BerkaDisponent disponent) {
        Map<Long, AccountEntity> accounts = (Map<Long, AccountEntity>) context.get("accounts");
        Map<Long, ClientEntity> clients = (Map<Long, ClientEntity>) context.get("clients");
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
