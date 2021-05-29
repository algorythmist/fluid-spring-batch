package com.tecacet.fluidbatch.berka.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tecacet.fluidbatch.JobExecutor;
import com.tecacet.fluidbatch.berka.entity.AccountEntity;
import com.tecacet.fluidbatch.berka.entity.ClientEntity;
import com.tecacet.fluidbatch.berka.repository.AccountRepository;
import com.tecacet.fluidbatch.berka.repository.ClientRepository;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@DirtiesContext
class BerkaImportTest {

    @Autowired
    private JobExecutor jobExecutor;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void testClient() throws JobExecutionException {
        JobExecution jobExecution =
                jobExecutor.execute("clientImportJob", Collections.emptyMap());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        List<ClientEntity> clients = clientRepository.findAll()
                .stream().sorted(Comparator.comparing(ClientEntity::getBirthDate))
                .collect(Collectors.toList());
        assertEquals(5369, clients.size());
        ClientEntity client = clients.get(100);
        assertEquals(ClientEntity.Gender.MALE, client.getGender());
        assertEquals(LocalDate.of(1919, 9, 23), client.getBirthDate());
        assertEquals("Frydek - Mistek", client.getDistrict());
    }

    @Test
    void testAccount() throws JobExecutionException {
        JobExecution jobExecution =
                jobExecutor.execute("accountImportJob", Collections.emptyMap());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        List<AccountEntity> accounts = accountRepository.findAll();
        assertEquals(4500, accounts.size());
    }

    //TODO
    //@Test
    void testClientAccount() throws JobExecutionException {
        JobExecution jobExecution =
                jobExecutor.execute("clientAccountImportJob", Collections.emptyMap());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    }

}
