package com.tecacet.fluidbatch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

class MultiTaskProcessorTest {

    @Test
    void process() throws Exception {

        ItemProcessor<String, Long> itemProcessor = new MultiTaskProcessor<>(
                text -> text.toLowerCase(),
                text -> ((String) text).getBytes(),
                bytes -> new BigInteger((byte[]) bytes),
                bi -> ((BigInteger) bi).longValue()
        );
        long value = itemProcessor.process("secret");
        assertEquals(126879297332596L, value);

    }

    @Test
    void processWithNull() throws Exception {
        ItemProcessor<String, Long> itemProcessor = new MultiTaskProcessor<>(
                String::toLowerCase,
                text -> null,
                bytes -> new BigInteger((byte[]) bytes),
                bi -> ((BigInteger) bi).longValue()
        );
        assertNull(itemProcessor.process("secret"));
    }
}
