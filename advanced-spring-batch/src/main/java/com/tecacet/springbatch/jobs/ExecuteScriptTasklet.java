package com.tecacet.springbatch.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Tasklet that executes a Jdbc Script stored in a file
 */
@Component
public class ExecuteScriptTasklet implements Tasklet {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ExecuteScriptTasklet(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        String scriptFilename = chunkContext.getStepContext().getStepExecution()
                .getJobParameters().getString("scriptFilename");
        if (scriptFilename == null) {
            throw new IOException("Parameter 'scriptFilename' is not defined.");
        }
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource(scriptFilename).getFile());
        if (!file.exists()) {
            throw new IOException(String.format("File %s does not exist.", file.getName()));
        }
        String content = new String(Files.readAllBytes(file.toPath()));
        logger.info(content);
        jdbcTemplate.execute(content);
        return RepeatStatus.FINISHED;
    }
}
