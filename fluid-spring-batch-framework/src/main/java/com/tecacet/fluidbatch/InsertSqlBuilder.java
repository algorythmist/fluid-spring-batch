package com.tecacet.fluidbatch;

import java.util.Arrays;

public class InsertSqlBuilder {

    public static String buildInsertSql(String tableName,
            String[] columns,
            String[] properties) {
        return "INSERT INTO " + tableName + " (" +
                String.join(",", columns) +
                ") VALUES (" +
                String.join(",", Arrays.stream(properties).map(p -> ":" + p).toArray(String[]::new)) +
                ")";
    }
}
