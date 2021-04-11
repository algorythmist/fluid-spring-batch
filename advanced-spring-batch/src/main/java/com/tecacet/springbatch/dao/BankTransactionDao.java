package com.tecacet.springbatch.dao;

import com.tecacet.springbatch.dto.BankTransaction;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
        return jdbcTemplate.query(query, new BankTransactionRowMapper());
    }
}
