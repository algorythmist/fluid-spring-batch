package com.tecacet.springbatch.jobs;

import com.tecacet.springbatch.dto.BankTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

public class TransactionImportSkipListener implements SkipListener<BankTransaction, BankTransaction> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onSkipInRead(Throwable throwable) {
        logger.warn("Line skipped on read", throwable);
    }

    @Override
    public void onSkipInWrite(BankTransaction bankTransaction, Throwable throwable) {
        logger.warn("Bean skipped on write", throwable);
    }

    @Override
    public void onSkipInProcess(BankTransaction bankTransaction, Throwable throwable) {
        logger.warn("Bean skipped on process", throwable);
    }

}
