package com.tecacet.fluidbatch.berka.jobs;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tecacet.fluidbatch.JobExecutor;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

@Disabled
@SpringBootTest
@DirtiesContext
public class BerkaEtlTest {

    @Autowired
    private JobExecutor jobExecutor;

    @Test
    void testEtlJob() throws JobExecutionException {
        JobExecution jobExecution = jobExecutor.execute("berkaEtlJob", Collections.emptyMap());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    }

}
