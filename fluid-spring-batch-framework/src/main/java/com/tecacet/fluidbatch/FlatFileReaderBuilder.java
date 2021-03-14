package com.tecacet.fluidbatch;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.Resource;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

public class FlatFileReaderBuilder<T> {

    private final ClassPathOrFileResourceSupplier resourceSupplier = new ClassPathOrFileResourceSupplier();
    private final FlatFileItemReader<T> reader = new FlatFileItemReader<>();
    private final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
    private final DefaultLineMapper<T> lineMapper = new DefaultLineMapper<>();
    private final BeanWrapperFieldSetMapper<T> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    private final Map<Class<?>, PropertyEditor> editors = new HashMap<>();

    public static <T> FlatFileReaderBuilder<T> getInstance(Class<T> type) {
        return new FlatFileReaderBuilder<>(type);
    }

    protected FlatFileReaderBuilder(Class<T> type) {
        fieldSetMapper.setStrict(false);
        fieldSetMapper.setTargetType(type);
    }

    public FlatFileReaderBuilder<T> setSkipLines(int lines) {
        reader.setLinesToSkip(lines);
        return this;
    }

    public FlatFileReaderBuilder<T> setProperties(String[] properties) {
        lineTokenizer.setIncludedFields(IntStream.range(0, properties.length).toArray());
        lineTokenizer.setNames(properties);
        return this;
    }

    public FlatFileReaderBuilder<T> setProperties(String[] properties, int[] includedFields) {
        lineTokenizer.setIncludedFields(includedFields);
        lineTokenizer.setNames(properties);
        return this;
    }

    public FlatFileReaderBuilder<T> setHeaderMap(Map<String, String> columnToPropertyMapping) {
        reader.setLinesToSkip(1);
        reader.setSkippedLinesCallback(header -> {
            FieldSet fieldSet = lineTokenizer.tokenize(header);
            String[] values = fieldSet.getValues();
            String[] names = new String[columnToPropertyMapping.size()];
            int[] included = new int[columnToPropertyMapping.size()];
            int i = 0;
            for (int j = 0; j < values.length; j++) {
                String value = values[j];
                if (columnToPropertyMapping.containsKey(value)) {
                    names[i] = columnToPropertyMapping.get(value);
                    included[i] = j;
                    i++;
                }
            }
            lineTokenizer.setNames(names);
            lineTokenizer.setIncludedFields(included);
        });
        return this;
    }

    public FlatFileReaderBuilder<T> setDelimiter(String delimiter) {
        lineTokenizer.setDelimiter(delimiter);
        return this;
    }

    public FlatFileReaderBuilder<T> setFilename(String filename) {
        Resource resource = new ClassPathOrFileResourceSupplier().getResource(filename);
        reader.setResource(resource);
        return this;
    }

    public <S> FlatFileReaderBuilder<T> registerConverter(Class<?> type, Function<String, S> conversion) {
        editors.put(type, new PropertyEditorSupport(){
            @Override
            public void setAsText(String text) {
                super.setValue(conversion.apply(text));
            }
        });
        return this;
    }

    public <S> FlatFileReaderBuilder<T> setResource(Resource resource) {
        reader.setResource(resource);
        return this;
    }

    public <S> FlatFileReaderBuilder<T> setResource(String resourceName) {
        reader.setResource(resourceSupplier.getResource(resourceName));
        return this;
    }

    public FlatFileItemReader<T> build() {
        fieldSetMapper.setCustomEditors(editors);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(lineTokenizer);
        reader.setLineMapper(lineMapper);
        return reader;
    }
}
