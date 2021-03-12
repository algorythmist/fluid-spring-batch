package com.tecacet.fluidbatch.berka.etl;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BerkaJobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Starting Berka ETL Job");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Berka ETL Job completed with status: "+jobExecution.getStatus());
    }
}
