package com.tecacet.fluidbatch;

import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.function.Function;

public class MultiTaskProcessor<I,O> implements ItemProcessor<I,O> {

    public MultiTaskProcessor(List<Function> mappers) {
        this.mappers = mappers;
    }

    private final List<Function> mappers;

    @Override
    public O process(I input) throws Exception {
        Object current = input;
        for (Function mapper: mappers) {
            current = mapper.apply(current);
            if (current == null) {
                return null;
            }
        }
        return (O) current;
    }
}
