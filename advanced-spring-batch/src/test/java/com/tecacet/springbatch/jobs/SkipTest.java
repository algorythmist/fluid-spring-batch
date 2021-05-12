package com.tecacet.springbatch.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tecacet.springbatch.dao.BankTransactionDao;
import com.tecacet.springbatch.dto.BankTransaction;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SkipTest {

    @Autowired
    private JobLauncher jobLauncher;


    @Autowired
    private Job simpleTransactionImportJob;

    @Autowired
    private BankTransactionDao bankTransactionDao;

    @Test
    void testTransactionImport() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameters parameters = builder
                .addString("scriptFilename", "create_transaction_table.sql")
                .addString("filename", "skip_example.csv")
                .addString("tableName", "bank_transaction")
                .toJobParameters();
        JobExecution execution = jobLauncher.run(simpleTransactionImportJob, parameters);
        assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
        List<StepExecution> stepExecutions = new ArrayList<>(execution.getStepExecutions());
        assertEquals(2, stepExecutions.size());
        StepExecution stepExecution = stepExecutions.get(1);
        assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());
        System.out.println(stepExecution);
        assertEquals(19, stepExecution.getReadCount());
        assertEquals(16, stepExecution.getWriteCount());
        assertEquals(3, stepExecution.getReadSkipCount());
        assertEquals(1, stepExecution.getProcessSkipCount());
        assertEquals(4, stepExecution.getSkipCount());
        assertEquals(1, stepExecution.getCommitCount());
        assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());

        List<BankTransaction> transactions = bankTransactionDao.findByAccountId("2504");
        assertEquals(16, transactions.size());
    }

}
