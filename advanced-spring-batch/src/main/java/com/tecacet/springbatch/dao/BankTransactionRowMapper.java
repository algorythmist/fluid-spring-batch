package com.tecacet.springbatch.dao;

import com.tecacet.springbatch.dto.BankTransaction;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class BankTransactionRowMapper implements RowMapper<BankTransaction> {

    @Override
    public BankTransaction mapRow(ResultSet resultSet, int i) throws SQLException {
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setTransactionId(resultSet.getString("transaction_id"));
        bankTransaction.setAccountId(resultSet.getString("account_id"));
        bankTransaction.setAmount(resultSet.getBigDecimal("transaction_amount"));
        String type = resultSet.getString("transaction_type");
        bankTransaction.setType(BankTransaction.Type.valueOf(type));
        bankTransaction.setDate(LocalDate.parse(resultSet.getString("transaction_date")));
        return bankTransaction;
    }
}
