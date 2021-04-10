package com.tecacet.fluidbatch.berka.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class DemoTransactionImportJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job transactionImportJob;

    @Test
    void jobTest() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameters parameters = builder
                .addString("filename", "account_2504.csv")
                .addString("tableName", "demo_transaction")
                .toJobParameters();
        JobExecution execution = jobLauncher.run(transactionImportJob, parameters);

        List<StepExecution> stepExecutions = new ArrayList<>(execution.getStepExecutions());
        assertEquals(1, stepExecutions.size());
        StepExecution stepExecution = stepExecutions.get(0);

        assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
        assertEquals(340, stepExecution.getReadCount());
        assertEquals(339, stepExecution.getWriteCount());
        assertEquals(1, stepExecution.getSkipCount());
        assertEquals(4, stepExecution.getCommitCount());
        assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());

    }
}
