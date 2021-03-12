package com.tecacet.fluidbatch.berka.config;

import com.tecacet.fluidbatch.berka.dto.BerkaAccount;
import com.tecacet.fluidbatch.berka.dto.BerkaClient;
import com.tecacet.fluidbatch.berka.dto.BerkaDisponent;
import com.tecacet.fluidbatch.berka.dto.BerkaTransaction;
import com.tecacet.fluidbatch.berka.etl.AccountProcessor;
import com.tecacet.fluidbatch.berka.etl.BerkaJobExecutionListener;
import com.tecacet.fluidbatch.berka.etl.ClientAccountProcessor;
import com.tecacet.fluidbatch.berka.etl.ClientProcessor;
import com.tecacet.fluidbatch.berka.etl.TransactionProcessor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.Collections;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class BerkaConfig {

    @Autowired
    @Qualifier("secondaryDataSource")
    private DataSource berkaDataSource;


    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private ClientAccountProcessor clientAccountProcessor;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Bean
    SqlPagingQueryProviderFactoryBean transactionQueryProvider() {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(berkaDataSource);
        provider.setSelectClause("SELECT *");
        provider.setFromClause("FROM trans");
        provider.setSortKey("date");
        return provider;
    }

    @Bean
    ItemReader<BerkaClient> clientItemReader() {
        return buildReader(BerkaClient.class,
                "select client_id as id, gender, birth_date , d.A2 as district",
                "from client c inner join district d on d.district_id  = c.district_id ",
                "client_id");
    }


    @SuppressWarnings("rawtypes")
    @Bean
    public JpaItemWriter jpaItemWriter() {
        JpaItemWriter writer = new JpaItemWriter();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    Job clientImportJob() {
        return buildBerkaTransformation("clientImport",
                clientItemReader(),
                new ClientProcessor());

    }

    @Bean
    ItemReader<BerkaAccount> accountItemReader() {
        return buildReader(BerkaAccount.class,
                "select account_id, frequency, `date` , d.A2 as district ",
                "from account a inner join district d on d.district_id = a.district_id ",
                "account_id");
    }

    @Bean
    ItemReader<BerkaTransaction> transactionItemReader() {
        return buildReader(BerkaTransaction.class,
                "select *", "from trans", "date");
    }

    @Bean
    Job accountImportJob() {
        return buildBerkaTransformation("accountImport",
                accountItemReader(), new AccountProcessor());
    }

    @Bean
    ItemReader<BerkaDisponent> disponentItemReader() {
        return buildReader(BerkaDisponent.class, "select * ", "from disp", "disp_id");
    }

    @Bean
    Job clientAccountImportJob() {
        return buildBerkaTransformation("clientAccountImport",
                disponentItemReader(), clientAccountProcessor);
    }

    @Bean
    Job berkaEtlJob() {
        Step loadClients = createStep("clientImportStep",
                clientItemReader(),
                new ClientProcessor());
        Step loadAccounts = createStep("accountImportStep",
                accountItemReader(),
                new AccountProcessor());
        Step loadClientAccounts = createStep("clientAccountImportStep",
                disponentItemReader(),
                clientAccountProcessor);
        Step loadTransactions = createStep("transactionImportStep",
                transactionItemReader(),
                transactionProcessor);
        return jobBuilderFactory.get("berkaEtlJob")
                .listener(new BerkaJobExecutionListener())
                .start(loadClients)
                .next(loadAccounts)
                .next(loadClientAccounts)
                .next(loadTransactions)
                .build();
    }

    private <I> ItemReader<I> buildReader(Class<I> clazz,
            String select, String from, String sortColumn) {
        return new JdbcPagingItemReaderBuilder<I>()
                .name(clazz.getName() + "Reader")
                .dataSource(berkaDataSource)
                .pageSize(1000)
                .selectClause(select)
                .fromClause(from)
                .sortKeys(Collections.singletonMap(sortColumn, Order.DESCENDING))
                .rowMapper(new BeanPropertyRowMapper<>(clazz))
                .build();
    }

    private <I, O> Job buildBerkaTransformation(String name,
            ItemReader<I> reader,
            ItemProcessor<I, O> processor) {
        Step step = createStep(name, reader, processor);
        return jobBuilderFactory.get(name + "Job")
                .flow(step)
                .end().build();
    }

    private <I, O> Step createStep(String name,
            ItemReader<I> reader,
            ItemProcessor<I, O> processor) {
        return createStep(name, reader, processor, null);
    }

    private <I, O> Step createStep(String name,
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            StepExecutionListener listener) {
        SimpleStepBuilder stepBuilder = stepBuilderFactory.get(name)
                .<I, O>chunk(1000)
                .reader(reader)
                .processor(processor)
                .writer(jpaItemWriter());
        if (listener != null) {
            stepBuilder.listener(listener);
        }
        return stepBuilder.build();
    }


}
