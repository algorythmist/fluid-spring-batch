package com.tecacet.springbatch.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class SpringBatchTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job executeScriptJob;

    @Autowired
    private Job transactionImportJob;

    @Test
    void testExecuteScriptJob() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameters parameters1 = builder
                .addString("scriptFilename","create_transaction_table.sql")
                .toJobParameters();
        JobExecution execution = jobLauncher.run(executeScriptJob, parameters1);
        System.out.println(execution);
        List<StepExecution> stepExecutions = new ArrayList<>(execution.getStepExecutions());
        assertEquals(1, stepExecutions.size());
        StepExecution stepExecution = stepExecutions.get(0);

        assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
        assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());

    }

    @Test
    void testTransactionImport()
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameters parameters = builder
                .addString("scriptFilename","create_transaction_table.sql")
                .addString("filename", "account_2504.csv")
                .addString("tableName", "bank_transaction")
                .toJobParameters();
        JobExecution execution = jobLauncher.run(transactionImportJob, parameters);

        List<StepExecution> stepExecutions = new ArrayList<>(execution.getStepExecutions());
        assertEquals(2, stepExecutions.size());
        StepExecution stepExecution = stepExecutions.get(0);

        assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
    }
}
