package com.tecacet.fluidbatch.berka.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tecacet.fluidbatch.JobExecutor;
import com.tecacet.fluidbatch.berka.entity.ClientEntity;
import com.tecacet.fluidbatch.berka.repository.ClientRepository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

//TODO: cleanup
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
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        List<ClientEntity> clients = clientRepository.findAll();
        assertEquals(5369, clients.size());
        System.out.println(clients.get(100));
    }

    @Test
    void testAccount() throws JobExecutionException {
        JobExecution jobExecution =
                jobExecutor.execute("accountImportJob", Collections.emptyMap());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        //TODO
    }

    @Test
    void testClientAccount() throws JobExecutionException {
        JobExecution jobExecution =
                jobExecutor.execute("clientAccountImportJob", Collections.emptyMap());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        //TODO
    }

}
