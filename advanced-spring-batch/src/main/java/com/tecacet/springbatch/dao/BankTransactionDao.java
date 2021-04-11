package com.tecacet.springbatch.dao;

import com.tecacet.springbatch.dto.BankTransaction;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BankTransactionDao {

    private final JdbcTemplate jdbcTemplate;

    public BankTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String QUERY_BY_ACCOUNT_ID = "SELECT * FROM BANK_TRANSACTION WHERE account_id = %s";

    public List<BankTransaction> findByAccountId(String accountId) {
        String query = String.format(QUERY_BY_ACCOUNT_ID, accountId);
        return jdbcTemplate.query(
                query,
                (resultSet, i) -> {
                    BankTransaction bankTransaction = new BankTransaction();
                    bankTransaction.setTransactionId(resultSet.getString("transaction_id"));
                    bankTransaction.setAccountId(resultSet.getString("account_id"));
                    bankTransaction.setAmount(resultSet.getBigDecimal("transaction_amount"));
                    String type = resultSet.getString("transaction_type");
                    bankTransaction.setType(BankTransaction.Type.valueOf(type));
                    bankTransaction.setDate(LocalDate.parse(resultSet.getString("transaction_date")));
                    return bankTransaction;
                });
    }
}
