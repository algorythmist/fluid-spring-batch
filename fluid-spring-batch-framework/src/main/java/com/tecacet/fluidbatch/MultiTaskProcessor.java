package com.tecacet.fluidbatch;

import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class MultiTaskProcessor<I, O> implements ItemProcessor<I, O> {

    private final List<Function> mappers;

    public MultiTaskProcessor(
            Function<I, ?> first,
            Function... rest) {
        this.mappers = new ArrayList<>();
        this.mappers.add(first);
        mappers.addAll(Arrays.asList(rest));
    }

    @Override
    public O process(I input) {
        Object current = input;
        for (Function mapper : mappers) {
            current = mapper.apply(current);
            if (current == null) {
                return null;
            }
        }
        return (O) current;
    }
}
