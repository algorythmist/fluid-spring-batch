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

@SpringBootTest
@ActiveProfiles("test")
class CSVImportTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importJob;

    @Test
    void jobTest() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameters parameters = builder.toJobParameters();
        JobExecution execution = jobLauncher.run(importJob, parameters);
        assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
        StepExecution stepExecution = execution.getStepExecutions().stream().findFirst().get();
        assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());
        assertEquals(340, stepExecution.getReadCount());
        assertEquals(340, stepExecution.getWriteCount());
    }

}
