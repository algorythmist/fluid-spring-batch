package com.tecacet.fluidbatch.examples.config;

import com.tecacet.fluidbatch.FlatFileReaderBuilder;
import com.tecacet.fluidbatch.examples.dto.BankTransaction;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class CSVImportConfig {

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Bean
    ItemReader<BankTransaction> itemReader() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("trans_id", "transId");
        headerMap.put("type", "type");
        headerMap.put("amount", "amount");
        headerMap.put("date", "date");
        return FlatFileReaderBuilder.getInstance(BankTransaction.class)
                .setHeaderMap(headerMap)
                .setResource("account_2504.csv")
                .registerConverter(LocalDate.class, LocalDate::parse)
                .build();
    }

    @Bean
    Step importStep(ItemReader<BankTransaction> itemReader) {
        return stepBuilderFactory.get("importStep").<BankTransaction, BankTransaction>chunk(100)
                .reader(itemReader)
                .writer(System.out::println).build();
    }

    @Bean
    Job importJob(Step importStep) {
        return jobBuilderFactory.get("importJob").flow(importStep).end().build();
    }
}
