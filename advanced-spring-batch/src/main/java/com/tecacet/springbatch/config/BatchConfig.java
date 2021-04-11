package com.tecacet.springbatch.config;

import com.tecacet.springbatch.dao.BankTransactionRowMapper;
import com.tecacet.springbatch.dto.BankTransaction;
import com.tecacet.springbatch.dto.MonthlyCashFlow;
import com.tecacet.springbatch.jobs.BankTransactionProcessor;
import com.tecacet.springbatch.jobs.CashFlowProcessor;
import com.tecacet.springbatch.jobs.ExecuteScriptTasklet;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ExecuteScriptTasklet executeScriptTasklet;

    @Autowired
    private BankTransactionProcessor transactionProcessor;

    @Autowired
    private CashFlowProcessor cashFlowProcessor;

    @Bean(name = "executeScriptJob")
    Job executeScriptJob(JobBuilderFactory jobBuilderFactory,
            Step executeScriptStep) {
        return jobBuilderFactory.get("executeScriptJob")
                .flow(executeScriptStep)
                .end()
                .build();
    }

    @Bean
    Step executeScriptStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("executeScriptTasklet")
                .tasklet(executeScriptTasklet)
                .build();
    }


    @Bean
    Job transactionImportJob(JobBuilderFactory jobBuilderFactory,
            Step executeScriptStep,
            Step importTransactionsStep,
            Step aggregateTransactionsStep) {
        return jobBuilderFactory.get("transactionImportJob")
                .flow(executeScriptStep)
                .next(importTransactionsStep)
                .next(aggregateTransactionsStep)
                .end()
                .build();
    }

    @Bean
    Step importTransactionsStep(StepBuilderFactory stepBuilderFactory,
            FlatFileItemReader<BankTransaction> transactionFileReader,
            JdbcBatchItemWriter<BankTransaction> transactionBatchWriter) {
        return stepBuilderFactory.get("importTransactionsStep")
                .<BankTransaction, BankTransaction>chunk(100)
                .reader(transactionFileReader)
                .processor(transactionProcessor)
                .writer(transactionBatchWriter)
                .faultTolerant()
                .skipLimit(10)
                .skip(UnsupportedTemporalTypeException.class)
                .build();
    }

    @Bean
    @StepScope
    FlatFileItemReader<BankTransaction> transactionFileReader(@Value("#{jobParameters['filename']}") String filename) {
        String[] properties = new String[] {"X", "transactionId", "accountId", "date", "type", "X", "amount"};

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setIncludedFields(IntStream.range(0, properties.length).toArray());
        lineTokenizer.setNames(properties);
        BeanWrapperFieldSetMapper<BankTransaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setStrict(false);
        fieldSetMapper.setTargetType(BankTransaction.class);
        fieldSetMapper.setCustomEditors(getCustomEditors());
        DefaultLineMapper<BankTransaction> lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(lineTokenizer);
        Resource resource = new ClassPathResource(filename);
        FlatFileItemReader<BankTransaction> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper);
        reader.setResource(resource);
        return reader;
    }

    private Map<Class<?>, PropertyEditor> getCustomEditors() {
        Map<Class<?>, PropertyEditor> editors = new HashMap<>();
        editors.put(LocalDate.class, new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                super.setValue(LocalDate.parse(text));
            }
        });
        return editors;
    }

    @Bean
    @StepScope
    JdbcBatchItemWriter<BankTransaction> transactionBatchWriter(@Value("#{jobParameters['tableName']}") String tableName) {
        String[] columns = {"transaction_id", "account_id", "transaction_type", "transaction_date", "transaction_amount"};
        String[] properties = {"transactionId", "accountId", "typeAsString", "date", "amount"};
        return new JdbcBatchItemWriterBuilder()
                .beanMapped()
                .sql(buildInsertSql(tableName, columns, properties))
                .dataSource(dataSource)
                .build();
    }

    private static String buildInsertSql(String tableName,
            String[] columns,
            String[] properties) {
        return "INSERT INTO " + tableName + " (" +
                String.join(",", columns) +
                ") VALUES (" +
                String.join(",", Arrays.stream(properties).map(p -> ":" + p).toArray(String[]::new)) +
                ")";
    }

    @Bean
    Step aggregateTransactionsStep(StepBuilderFactory stepBuilderFactory,
            ItemReader<BankTransaction> transactionItemReader,
            ItemWriter<MonthlyCashFlow> cashFlowWriter) {
        return stepBuilderFactory.get("importTransactionsStep")
                .<BankTransaction, MonthlyCashFlow>chunk(100)
                .reader(transactionItemReader)
                .processor(cashFlowProcessor)
                .writer(cashFlowWriter)
                .faultTolerant()
                .skipLimit(5)
                .skip(UnsupportedTemporalTypeException.class)
                .build();
    }

    @Bean
    @StepScope
    ItemReader<BankTransaction> transactionItemReader(@Value("#{jobParameters['accountId']}") String accountId) {
        return buildReader(
                "select *",
                "from bank_transaction",
                "where account_id = " + accountId,
                "transaction_date");
    }

    private ItemReader<BankTransaction> buildReader(
            String select, String from, String where, String sortColumn) {
        return new JdbcPagingItemReaderBuilder<BankTransaction>()
                .name("TransactionReader")
                .dataSource(dataSource)
                .pageSize(50)
                .selectClause(select)
                .fromClause(from)
                .whereClause(where)
                .sortKeys(Collections.singletonMap(sortColumn, Order.DESCENDING))
                .rowMapper(new BankTransactionRowMapper())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<MonthlyCashFlow> cashFlowWriter(@Value("#{jobParameters['outputFile']}") String outputFile) {
        //Create writer instance
        FlatFileItemWriter<MonthlyCashFlow> writer = new FlatFileItemWriter<>();

        //Set output file location
        writer.setResource(new FileSystemResource(outputFile));

        writer.setAppendAllowed(false);
        writer.setHeaderCallback(w -> w.write("Year,Month,Cash Flow"));
        //Name field values sequence based on object properties
        writer.setLineAggregator(new DelimitedLineAggregator<MonthlyCashFlow>() {
            {

                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<MonthlyCashFlow>() {
                    {
                        setNames(new String[] {"year", "month", "netAmount"});
                    }
                });
            }
        });
        return writer;
    }
}
