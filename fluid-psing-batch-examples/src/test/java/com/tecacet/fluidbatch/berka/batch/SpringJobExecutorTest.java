package com.tecacet.fluidbatch.berka.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.tecacet.fluidbatch.JobExecutor;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

@SpringBootTest
@ActiveProfiles("test")
class SpringJobExecutorTest {

    @Autowired
    private JobExecutor jobExecutor;

    @Test
    void execute() throws JobExecutionException {
        JobExecution jobExecution =
                jobExecutor.execute("simpleTaskletJob",
                        Collections.singletonMap("tableName", "demo_transaction"));
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }

    @Test
    void executeMissingJob() {
        try {
            jobExecutor.execute("missingJob",
                    Collections.singletonMap("tableName", "demo_transaction"));
            fail();
        } catch (JobExecutionException jee) {
            assertEquals("There is no job named missingJob", jee.getMessage());
        }
    }
}
