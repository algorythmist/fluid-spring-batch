package com.tecacet.fluidbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FluidBatchConfig {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private List<? extends Job> jobBeans;

    @Bean
    JobExecutor jobExecutor() {
        return new SpringJobExecutor(jobLauncher, jobBeans);
    }
}
