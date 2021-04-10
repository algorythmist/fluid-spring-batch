package com.tecacet.fluidbatch.berka.processor;

import com.tecacet.fluidbatch.berka.dto.BerkaTransaction;
import com.tecacet.fluidbatch.berka.entity.AccountEntity;
import com.tecacet.fluidbatch.berka.entity.TransactionEntity;
import com.tecacet.fluidbatch.berka.repository.AccountRepository;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class TransactionProcessor implements ItemProcessor<BerkaTransaction, TransactionEntity> {

    private final AccountRepository accountRepository;

    @Autowired
    private TransientDataStore transientDataStore;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {

        if (transientDataStore.getAccounts().isEmpty()) {
            transientDataStore.storeData();
        }
    }

    @Override
    public TransactionEntity process(BerkaTransaction berkaTransaction) {
        Map<Long, AccountEntity> accounts = transientDataStore.getAccounts();
        AccountEntity account = accounts.get(berkaTransaction.getAccountId());
        if (account == null) {
            log.error("Missing account for " + berkaTransaction.getAccount());
            return null;
        }
        TransactionEntity transaction = new TransactionEntity();
        transaction.setId(berkaTransaction.getTransId());
        transaction.setAccount(account);
        transaction.setAmount(berkaTransaction.getAmount());
        transaction.setBalance(berkaTransaction.getBalance());
        transaction.setBank(berkaTransaction.getBank());
        transaction.setDate(berkaTransaction.getDate());
        return transaction;
    }

    private TransactionEntity.Type decodeType(String text) {
        String type = text.toUpperCase();
        switch (type) {
            case "PRIJEM":
                return TransactionEntity.Type.CREDIT;
            case "VYDAJ":
                return TransactionEntity.Type.DEBIT;
            default:
                throw new IllegalArgumentException("Unexpected transaction type " + type);
        }
    }

    private TransactionEntity.Operation decodeOperation(String text) {
        String operation = text.toUpperCase();
        switch (operation) {
            case "VYBER KARTOU":
                return TransactionEntity.Operation.CC_PAYMENT;
            case "VKLAD":
                return TransactionEntity.Operation.CASH_DEPOSIT;
            case "PREVOD Z UCTU":
                return TransactionEntity.Operation.TO_BANK;
            case "VYBER":
                return TransactionEntity.Operation.CASH_WITHDRAWAL;
            case "PREVOD NA UCET":
                return TransactionEntity.Operation.FROM_BANK;
            default:
                return TransactionEntity.Operation.OTHER;
        }
    }

    private TransactionEntity.Category decodeCategory(String text) {
        String category = text.toUpperCase();
        switch (category) {
            case "POJISTNE":
                return TransactionEntity.Category.INSURANCE_PAYMENT;
            case "SLUZBY":
                return TransactionEntity.Category.STATEMENT_PAYMENT;
            case "UROK":
                return TransactionEntity.Category.INTEREST;
            case "SANKC. UROK":
                return TransactionEntity.Category.OVERDRAFT_FEE;
            case "SIPO":
                return TransactionEntity.Category.HOUSEHOLD_PAYMENT;
            case "DUCHOD":
                return TransactionEntity.Category.PENSION_PAYMENT;
            case "UVER":
                return TransactionEntity.Category.LOAN_PAYMENT;
            default:
                return TransactionEntity.Category.OTHER;
        }
    }
}
