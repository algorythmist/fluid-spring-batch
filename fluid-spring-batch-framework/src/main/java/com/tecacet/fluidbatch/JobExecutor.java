package com.tecacet.fluidbatch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;

import java.util.Map;

public interface JobExecutor {

    JobExecution execute(String jobName, Map<String, Object> jobParameters) throws JobExecutionException;
}
