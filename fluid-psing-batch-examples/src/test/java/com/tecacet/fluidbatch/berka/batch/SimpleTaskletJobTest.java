package com.tecacet.fluidbatch.berka.batch;

import static org.junit.jupiter.api.Assertions.*;

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
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
public class SimpleTaskletJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job simpleTaskletJob;

    @Test
    void jobTest() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameters parameters1 = builder
                .addString("tableName", "transaction")
                .toJobParameters();
        JobExecution execution = jobLauncher.run(simpleTaskletJob, parameters1);

        List<StepExecution> stepExecutions = new ArrayList<>(execution.getStepExecutions());
        assertEquals(1, stepExecutions.size());
        StepExecution stepExecution = stepExecutions.get(0);

        assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
        assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());

        //rerun fails
        try {
            JobParameters parameters2 = new JobParametersBuilder()
                    .addString("tableName", "transaction")
                    .toJobParameters();
            jobLauncher.run(simpleTaskletJob, parameters2);
        } catch (JobInstanceAlreadyCompleteException e) {
            String message = e.getMessage();
            assertTrue(message.contains("A job instance already exists and is complete for parameters={tableName=transaction}"));
        }
    }

    @Test
    public void testRerunFixed()
            throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameters parameters1 = builder
                .addString("tableName", "transaction")
                .addLong("currentTime", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution1 = jobLauncher.run(simpleTaskletJob, parameters1);
        assertEquals(ExitStatus.COMPLETED, execution1.getExitStatus());

        JobParameters parameters2 = new JobParametersBuilder()
                .addString("tableName", "transaction")
                .addLong("currentTime", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution2 = jobLauncher.run(simpleTaskletJob, parameters2);
        assertEquals(ExitStatus.COMPLETED, execution2.getExitStatus());

    }

}
