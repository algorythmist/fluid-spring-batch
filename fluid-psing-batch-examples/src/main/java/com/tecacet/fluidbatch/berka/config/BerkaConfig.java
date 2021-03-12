package com.tecacet.fluidbatch.berka.config;

import com.tecacet.fluidbatch.berka.dto.BerkaAccount;
import com.tecacet.fluidbatch.berka.dto.BerkaDisponent;
import com.tecacet.fluidbatch.berka.etl.AccountProcessor;
import com.tecacet.fluidbatch.berka.etl.ClientAccountProcessor;
import com.tecacet.fluidbatch.berka.etl.ClientProcessor;
import com.tecacet.fluidbatch.berka.dto.BerkaClient;
import com.tecacet.fluidbatch.berka.dto.Transaction;
import com.tecacet.fluidbatch.berka.repository.ClientRepository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
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
    ItemReader<Transaction> transactionItemReader(PagingQueryProvider transactionQueryProvider) {
        return new JdbcPagingItemReaderBuilder<Transaction>()
                .name("transactionItemReader")
                .dataSource(berkaDataSource)
                .pageSize(1000)
                .queryProvider(transactionQueryProvider)
                .rowMapper(new BeanPropertyRowMapper<>(Transaction.class))
                .build();
    }

    @Bean
    ItemReader<BerkaClient> clientItemReader() {
        return new JdbcPagingItemReaderBuilder<BerkaClient>()
                .name("clientItemReader")
                .dataSource(berkaDataSource)
                .pageSize(1000)
                .selectClause("select client_id as id, gender, birth_date , d.A2 as district")
                .fromClause("from client c inner join district d on d.district_id  = c.district_id ")
                .sortKeys(Collections.singletonMap("client_id", Order.DESCENDING))
                .rowMapper(new BeanPropertyRowMapper<>(BerkaClient.class))
                .build();
    }


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
        Step step = stepBuilderFactory.get(name + "Step")
                .<I, O>chunk(1000)
                .reader(reader)
                .processor(processor)
                .writer(jpaItemWriter())
                .build();
        return jobBuilderFactory.get(name + "Job")
                .flow(step)
                .end().build();
    }


}
