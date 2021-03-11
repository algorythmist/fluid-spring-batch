package com.tecacet.fluidbatch.berka.etl;


import com.tecacet.fluidbatch.berka.dto.Transaction;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.temporal.UnsupportedTemporalTypeException;

@Component
@StepScope
public class TransactionProcessor implements ItemProcessor<Transaction, Transaction> {

    private int count = 0;

    @Override
    public Transaction process(Transaction transaction) throws UnsupportedTemporalTypeException {
        count++;
        if (count == 110) {
            throw new UnsupportedTemporalTypeException("You screwed up!");
        }

        return transaction;
    }
}
