package com.tecacet.fluidbatch.berka.etl;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class TruncateTableTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TruncateTableTasklet(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        String tableName = chunkContext.getStepContext().getStepExecution()
                .getJobParameters().getString("tableName");
        jdbcTemplate.execute("DELETE FROM " + tableName);
        return RepeatStatus.FINISHED;
    }
}
