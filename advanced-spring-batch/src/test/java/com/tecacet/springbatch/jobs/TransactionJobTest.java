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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private BankTransactionDao bankTransactionDao;

    @Autowired
    private Job transactionImportJob;

    @Test
    void testTransactionImport() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameters parameters = builder
                .addString("scriptFilename", "create_transaction_table.sql")
                .addString("filename", "account_2504.csv")
                .addString("tableName", "bank_transaction")
                .addString("accountId", "2504")
                .addString("outputFile", "cash_flow.csv")
                .toJobParameters();
        JobExecution execution = jobLauncher.run(transactionImportJob, parameters);
        assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
        List<StepExecution> stepExecutions = new ArrayList<>(execution.getStepExecutions());
        assertEquals(3, stepExecutions.size());
        StepExecution stepExecution = stepExecutions.get(1);
        assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());
        assertEquals(340, stepExecution.getReadCount());
        assertEquals(339, stepExecution.getWriteCount());
        assertEquals(1, stepExecution.getSkipCount());
        assertEquals(4, stepExecution.getCommitCount());
        assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());

        List<BankTransaction> transactions = bankTransactionDao.findByAccountId("2504");
        assertEquals(339, transactions.size());
        BankTransaction transaction = transactions.get(42);
        assertEquals("2504", transaction.getAccountId());
        assertEquals(BankTransaction.Type.DEBIT, transaction.getType());
        assertEquals(LocalDate.of(1994, 9, 5), transaction.getDate());
        assertEquals(1930.0, transaction.getAmount().doubleValue(), 0.0001);


    }
}
