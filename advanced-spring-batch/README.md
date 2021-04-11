# Advanced Spring Batch

### Configuration Properties

Spring Batch by default starts all jobs when the application starts. 
This is almost never the desired behavior. To disable it

```
#Do not auto start spring batch jobs
spring.batch.job.enabled=false
```

Creating Spring batch tables
The easiest way is to let Spring do it for you

```
#Initialize schema
spring.batch.initialize-schema=always
```

Otherwise, you can copy the table definitions from
[here](https://docs.spring.io/spring-batch/docs/3.0.x/reference/html/metaDataSchema.html)
and put them in a flyway script.

### Runtime Parameters

To pass parameters in a job 
```java
JobParametersBuilder builder = new JobParametersBuilder();
JobParameters parameters = builder
                .addString("scriptFilename", "create_transaction_table.sql")
                .addString("filename", "account_2504.csv")
                .toJobParameters();
JobExecution execution = jobLauncher.run(transactionImportJob, parameters);
```

To access the parameter in a Tasklet 

```java
 @Override
public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
    String scriptFilename = chunkContext.getStepContext().getStepExecution()
                .getJobParameters().getString("scriptFilename");
    if (scriptFilename == null) {
            throw new Exception("Parameter 'scriptFilename' is not defined.");
    }
```

To access a parameters in a Step:
```java
@Bean
@StepScope
FlatFileItemReader<BankTransaction> transactionFileReader(@Value("#{jobParameters['filename']}") String filename) {
```

NOTE: You must use @StepScope, otherwise the parameter will not be injected

Spring Batch by default will not execute the same job twice.
To make jobs rerunnable, you must pass a parameter that changes from job to job

```java
JobParametersBuilder builder = new JobParametersBuilder();
JobParameters parameters1 = builder
                .addString("scriptFilename", "create_transaction_table.sql")
                .addLong("currentTime", System.currentTimeMillis())
                .toJobParameters();
JobExecution execution1 = jobLauncher.run(executeScriptJob, parameters1);
```

### File imports
To skip columns
```java

DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
lineTokenizer.setDelimiter(",");
lineTokenizer.setIncludedFields(IntStream.range(0, properties.length).toArray());
lineTokenizer.setNames(new String[] {"X", "transactionId", "accountId", "date", "type", "X", "amount"});
BeanWrapperFieldSetMapper<BankTransaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
fieldSetMapper.setStrict(false);
```

Convert to desired data types
```java
fieldSetMapper.setCustomEditors(getCustomEditors());

private Map<Class<?>, PropertyEditor> getCustomEditors() {
        Map<Class<?>, PropertyEditor> editors = new HashMap<>();
        editors.put(LocalDate.class, new PropertyEditorSupport() {

    @Override
    public void setAsText(String text) {
        super.setValue(LocalDate.parse(text));
    }});
    return editors;
}
```

How to read from a system vs file resource

FileSystemResource vs ClasspathResource

### File Exports
How to set a header and specify properties
```java
  @Bean
    @StepScope
    public FlatFileItemWriter<MonthlyCashFlow> cashFlowWriter(@Value("#{jobParameters['outputFile']}") String outputFile) {
        //Create writer instance
        FlatFileItemWriter<MonthlyCashFlow> writer = new FlatFileItemWriter<>();

        //Set output file location
        writer.setResource(new FileSystemResource(outputFile));
        
        writer.setAppendAllowed(false);
        writer.setHeaderCallback(w -> w.write("Year,Month,Cash Flow"));
        //Name field values sequence based on object properties
        writer.setLineAggregator(new DelimitedLineAggregator<MonthlyCashFlow>() {
            {

                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<MonthlyCashFlow>() {
                    {
                        setNames(new String[] {"year", "month", "netAmount"});
                    }
                });
            }
        });
        return writer;
    }
```


### Job flow

How to skip failed instances instead of failing the whole job

```java
@Bean
Step importTransactionsStep(StepBuilderFactory stepBuilderFactory,
            FlatFileItemReader<BankTransaction> transactionFileReader,
            JdbcBatchItemWriter<BankTransaction> transactionBatchWriter) {
    return stepBuilderFactory.get("importTransactionsStep")
                .<BankTransaction, BankTransaction>chunk(100)
                .reader(transactionFileReader)
                .processor(transactionProcessor)
                .writer(transactionBatchWriter)
                .faultTolerant()
                .skipLimit(10)
                .skip(UnsupportedTemporalTypeException.class)
                .build();
}
```
- Map/Reduce




