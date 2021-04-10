package com.tecacet.fluidbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpringJobExecutor implements JobExecutor {

    private final JobLauncher jobLauncher;
    private final Map<String, ? extends Job> jobsByName;


    public SpringJobExecutor(JobLauncher jobLauncher, List<? extends Job> jobBeans) {
        this.jobLauncher = jobLauncher;
        jobsByName = jobBeans.stream().collect(Collectors.toMap(Job::getName, Function.identity()));
    }

    @Override
    public JobExecution execute(String jobName, Map<String, Object> jobParameters)
            throws JobExecutionException {
        Job job = jobsByName.get(jobName);
        if (job == null) {
            throw new JobExecutionException("There is no job named " + jobName);
        }
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParameters.forEach((k, v) -> addParameter(jobParametersBuilder, k, v));
        jobParametersBuilder.addLong("currentTime", System.currentTimeMillis());
        return jobLauncher.run(job, jobParametersBuilder.toJobParameters());
    }

    private void addParameter(JobParametersBuilder jobParametersBuilder, String key, Object value) {
        if (value == null) {
            //TODO: skip null?
            jobParametersBuilder.addString(key, null);
            return;
        }
        if (value.getClass().isAssignableFrom(double.class) ||
                value.getClass().isAssignableFrom(Double.class)) {
            jobParametersBuilder.addDouble(key, (Double) value);
            return;
        }
        if (value.getClass().isAssignableFrom(long.class) ||
                value.getClass().isAssignableFrom(Long.class)) {
            jobParametersBuilder.addLong(key, (Long) value);
            return;
        }
        if (value.getClass().isAssignableFrom(Date.class)) {
            jobParametersBuilder.addDate(key, (Date) value);
            return;
        }
        jobParametersBuilder.addString(key, value.toString());
    }
}
