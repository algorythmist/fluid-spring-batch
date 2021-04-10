package com.tecacet.fluidbatch.berka.etl;


import com.tecacet.fluidbatch.berka.dto.DemoTransaction;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.temporal.UnsupportedTemporalTypeException;

@Component
@StepScope
public class DemoTransactionProcessor implements ItemProcessor<DemoTransaction, DemoTransaction> {

    private int count = 0;

    @Override
    public DemoTransaction process(DemoTransaction transaction) throws UnsupportedTemporalTypeException {
        count++;
        if (count == 110) {
            throw new UnsupportedTemporalTypeException("This record failed!");
        }
        return transaction;
    }
}
