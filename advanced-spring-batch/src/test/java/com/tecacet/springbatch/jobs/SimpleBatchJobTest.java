package com.tecacet.springbatch.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.tecacet.springbatch.dao.BankTransactionDao;

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
public class SimpleBatchJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private BankTransactionDao bankTransactionDao;

    @Autowired
    private Job executeScriptJob;

    @Test
    void testExecuteScriptJob() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameters parameters1 = builder
                .addString("scriptFilename", "create_transaction_table.sql")
                .toJobParameters();
        JobExecution execution = jobLauncher.run(executeScriptJob, parameters1);
        System.out.println(execution);
        List<StepExecution> stepExecutions = new ArrayList<>(execution.getStepExecutions());
        assertEquals(1, stepExecutions.size());
        StepExecution stepExecution = stepExecutions.get(0);

        assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
        assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());

        //Note that rerunning the job fails
        try {
            JobParameters parameters2 = new JobParametersBuilder()
                    .addString("scriptFilename", "create_transaction_table.sql")
                    .toJobParameters();
            jobLauncher.run(executeScriptJob, parameters2);
            fail();
        } catch (JobInstanceAlreadyCompleteException e) {
            String message = e.getMessage();
            assertTrue(message.contains("A job instance already exists and is complete for parameters="));
        }
    }

    @Test
    public void testRerunable()
            throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameters parameters1 = builder
                .addString("scriptFilename", "create_transaction_table.sql")
                .addLong("currentTime", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution1 = jobLauncher.run(executeScriptJob, parameters1);
        assertEquals(ExitStatus.COMPLETED, execution1.getExitStatus());

        JobParameters parameters2 = new JobParametersBuilder()
                .addString("scriptFilename", "create_transaction_table.sql")
                .addLong("currentTime", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution2 = jobLauncher.run(executeScriptJob, parameters2);
        assertEquals(ExitStatus.COMPLETED, execution2.getExitStatus());

    }


}
