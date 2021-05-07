package com.tecacet.fluidbatch;

import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;

public class FlatFileWriterBuilder<T> {

    private final ClassPathOrFileResourceSupplier resourceSupplier = new ClassPathOrFileResourceSupplier();
    private final FlatFileItemWriter<T> writer = new FlatFileItemWriter<>();
    private final BeanWrapperFieldExtractor<T> fieldExtractor = new BeanWrapperFieldExtractor<>();
    private final DelimitedLineAggregator<T> lineAggregator = new DelimitedLineAggregator<>();

    public FlatFileWriterBuilder<T> setDelimiter(String delimiter) {
        lineAggregator.setDelimiter(delimiter);
        return this;
    }

    public FlatFileWriterBuilder<T> setProperties(String[] properties) {
        fieldExtractor.setNames(properties);
        return this;
    }

    public FlatFileWriterBuilder<T> setHeader(String header) {
        writer.setHeaderCallback(w -> w.write(header));
        return this;
    }

    public FlatFileWriterBuilder<T> setHeader(String[] header) {
        writer.setHeaderCallback(w -> w.write(lineAggregator.doAggregate(header)));
        return this;
    }

    public <S> FlatFileWriterBuilder<T> setResource(String resourceName) {
        writer.setResource(resourceSupplier.getResource(resourceName));
        return this;
    }

    public FlatFileItemWriter<T> build() {
        return writer;
    }

    public FlatFileItemWriter<T> getWriter() {
        return writer;
    }

    public BeanWrapperFieldExtractor<T> getFieldExtractor() {
        return fieldExtractor;
    }

    public DelimitedLineAggregator<T> getLineAggregator() {
        return lineAggregator;
    }
}
