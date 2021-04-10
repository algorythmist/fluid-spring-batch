package com.tecacet.fluidbatch.berka.etl;

import com.tecacet.fluidbatch.berka.dto.BerkaAccount;
import com.tecacet.fluidbatch.berka.entity.AccountEntity;

import org.springframework.batch.item.ItemProcessor;


public class AccountProcessor implements ItemProcessor<BerkaAccount, AccountEntity> {

    @Override
    public AccountEntity process(BerkaAccount berkaAccount) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(berkaAccount.getAccountId());
        accountEntity.setDateOpened(berkaAccount.getDate());
        accountEntity.setStatementFrequency(decodeFrequency(berkaAccount.getFrequency()));
        accountEntity.setDistrict(berkaAccount.getDistrict());
        return accountEntity;
    }

    private AccountEntity.StatementFrequency decodeFrequency(String text) {
        String frequency = text.toUpperCase();
        switch (frequency) {
            case "POPLATEK MESICNE":
                return AccountEntity.StatementFrequency.MONTHLY;
            case "POPLATEK TYDNE":
                return AccountEntity.StatementFrequency.WEEKLY;
            case "POPLATEK PO OBRATU":
            default:
                return AccountEntity.StatementFrequency.AFTER_TRANSACTION;
        }
    }
}
