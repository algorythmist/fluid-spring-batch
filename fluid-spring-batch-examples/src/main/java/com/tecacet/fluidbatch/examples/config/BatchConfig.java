package com.tecacet.fluidbatch.examples.config;

import com.tecacet.fluidbatch.FlatFileReaderBuilder;
import com.tecacet.fluidbatch.FlatFileWriterBuilder;
import com.tecacet.fluidbatch.FluidBatchConfig;
import com.tecacet.fluidbatch.InsertSqlBuilder;
import com.tecacet.fluidbatch.examples.batch.DemoTransactionProcessor;
import com.tecacet.fluidbatch.examples.batch.TruncateTableTasklet;
import com.tecacet.fluidbatch.examples.dto.DemoTransaction;
import com.tecacet.fluidbatch.examples.dto.MonthlyCashFlow;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.temporal.UnsupportedTemporalTypeException;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Import(FluidBatchConfig.class)
public class BatchConfig {


    @Autowired
    private TruncateTableTasklet truncateTableTasklet;

    @Autowired
    private DemoTransactionProcessor transactionProcessor;

    @Autowired
    private DataSource dataSource;

    @Bean
    Step truncateTableStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("truncateTableStep")
                .tasklet(truncateTableTasklet)
                .build();
    }

    @Bean(name = "simpleTaskletJob")
    Job simpleTaskletJob(JobBuilderFactory jobBuilderFactory,
            Step truncateTableStep) {
        return jobBuilderFactory.get("simpleTaskletJob")
                .flow(truncateTableStep)
                .end()
                .build();
    }

    @Bean
    @StepScope
    FlatFileItemReader<DemoTransaction> transactionFileReader(@Value("#{jobParameters['filename']}") String filename) {
        return FlatFileReaderBuilder.getInstance(DemoTransaction.class)
                .setDelimiter(",")
                .setProperties(new String[] {"X", "transactionId", "accountId", "date", "type", "X", "amount"})
                .setFilename(filename)
                .setSkipLines(1)
                .registerConverter(LocalDate.class, LocalDate::parse)
                .build();
    }

    @Bean
    @StepScope
    JdbcBatchItemWriter<DemoTransaction> transactionBatchWriter(@Value("#{jobParameters['tableName']}") String tableName) {
        String[] columns = {"transaction_id", "account_id", "transaction_type", "transaction_date", "transaction_amount"};
        String[] properties = {"transactionId", "accountId", "type", "date", "amount"};
        return new JdbcBatchItemWriterBuilder()
                .beanMapped()
                .sql(InsertSqlBuilder.buildInsertSql(tableName, columns, properties))
                .dataSource(dataSource)
                .build();
    }

    @Bean
    Step importTransactionsStep(StepBuilderFactory stepBuilderFactory,
            FlatFileItemReader<DemoTransaction> transactionFileReader,
            JdbcBatchItemWriter<DemoTransaction> transactionBatchWriter) {
        return stepBuilderFactory.get("importTransactionsStep")
                .<DemoTransaction, DemoTransaction>chunk(100)
                .reader(transactionFileReader)
                .processor(transactionProcessor)
                .writer(transactionBatchWriter)
                .faultTolerant()
                .skipLimit(5)
                .skip(UnsupportedTemporalTypeException.class)
                .build();
    }

    @Bean
    Job transactionImportJob(JobBuilderFactory jobBuilderFactory,
            Step importTransactionsStep) {
        return jobBuilderFactory.get("transactionImportJob")
                .flow(importTransactionsStep)
                .end().build();
    }

    @Bean
    @StepScope
    FlatFileItemWriter<MonthlyCashFlow> cashFlowWriter(@Value("#{jobParameters['outputFile']}") String outputFile) {
        FlatFileWriterBuilder<MonthlyCashFlow> builder = new FlatFileWriterBuilder<>();
        builder.setHeader(new String[] {"Year", "Month", "Cash Flow"})
                .setProperties(new String[] {"year", "month", "netAmount"})
                .setResource(outputFile)
                .setDelimiter(",");
        FlatFileItemWriter<MonthlyCashFlow> writer = builder.getWriter();
        writer.setAppendAllowed(false);
        return builder.build();
    }
}
