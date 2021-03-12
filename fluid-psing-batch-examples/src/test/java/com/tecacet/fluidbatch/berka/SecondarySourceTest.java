package com.tecacet.fluidbatch.berka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

@SpringBootTest
@ActiveProfiles("test")
class SecondarySourceTest {

    @Resource(name = "secondaryDataSource")
    DataSource dataSource;

    @Test
    void test() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        List accounts = template.queryForList("select * from account");
        assertEquals(4500, accounts.size());
    }
}
