package com.tecacet.fluidbatch.berka.transforms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tecacet.fluidbatch.JobExecutor;
import com.tecacet.fluidbatch.berka.entity.ClientEntity;
import com.tecacet.fluidbatch.berka.repository.ClientRepository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

@Disabled
@SpringBootTest
class BerkaImportTest {

    @Autowired
    private JobExecutor jobExecutor;

    @Autowired
    private ClientRepository clientRepository;


    @Test
    void testClient() throws JobExecutionException {
        JobExecution jobExecution =
                jobExecutor.execute("clientImportJob", Collections.emptyMap());
        System.out.println(jobExecution);

        List<ClientEntity> clients = clientRepository.findAll();
        assertEquals(5369, clients.size());
        System.out.println(clients.get(100));
    }

    @Test
    void testAccount() throws JobExecutionException {
        JobExecution jobExecution =
                jobExecutor.execute("accountImportJob", Collections.emptyMap());
        System.out.println(jobExecution);
    }

    @Test
    void testClientAccount() throws JobExecutionException {
        JobExecution jobExecution =
                jobExecutor.execute("clientAccountImportJob", Collections.emptyMap());
        System.out.println(jobExecution);
    }

    @Test
    void berkaEtlJob() throws JobExecutionException {
        JobExecution jobExecution =
                jobExecutor.execute("berkaEtlJob", Collections.emptyMap());
        System.out.println(jobExecution);
    }

}
