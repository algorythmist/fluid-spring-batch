package com.tecacet.springbatch.jobs;

import com.tecacet.springbatch.dto.BankTransaction;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@StepScope
public class BankTransactionProcessor implements ItemProcessor<BankTransaction, BankTransaction> {

    @Override
    public BankTransaction process(BankTransaction transaction) throws IllegalArgumentException {
        if ("BOO".equals(transaction.getBank())) {
            return null; //Skip these transactions
        }
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            //amount is debit or credit but cannot be negative
            throw new IllegalArgumentException("Transaction amount cannot be negative.");
        }
        return transaction;
    }
}
