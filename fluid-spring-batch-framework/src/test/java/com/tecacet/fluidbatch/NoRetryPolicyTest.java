package com.tecacet.fluidbatch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NoRetryPolicyTest {

    @Test
    void canRetry() {
        assertFalse(new NoRetryPolicy().canRetry(null));
    }
}
