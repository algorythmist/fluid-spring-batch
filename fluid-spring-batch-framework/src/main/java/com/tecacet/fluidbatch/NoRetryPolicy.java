package com.tecacet.fluidbatch;

import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;

public class NoRetryPolicy extends SimpleRetryPolicy {

    @Override
    public boolean canRetry(RetryContext context) {
        return false;
    }

}
