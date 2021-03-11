package com.tecacet.fluidbatch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InsertSqlBuilderTest {

    @Test
    void buildInsertSql() {
        String[] columns = {"transaction_id", "account_id", "transaction_type", "transaction_date", "transaction_amount"};
        String[] properties = {"transactionId", "accountId", "type", "date", "amount"};
        String sql = InsertSqlBuilder.buildInsertSql("transaction", columns, properties);
        assertEquals("INSERT INTO transaction (transaction_id,account_id,transaction_type,transaction_date,transaction_amount) VALUES (:transactionId,"
                        + ":accountId,:type,:date,:amount)",
                sql);
    }
}
