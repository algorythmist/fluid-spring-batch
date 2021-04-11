package com.tecacet.springbatch.jobs;

import com.tecacet.springbatch.dto.BankTransaction;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.temporal.UnsupportedTemporalTypeException;

@Component
@StepScope
public class BankTransactionProcessor implements ItemProcessor<BankTransaction, BankTransaction> {

    private int count = 0;

    @Override
    public BankTransaction process(BankTransaction transaction) throws UnsupportedTemporalTypeException {
        count++;
        if (count == 110) {
            throw new UnsupportedTemporalTypeException("This record failed!");
        }
        return transaction;
    }
}
