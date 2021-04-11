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

- How to access parameters in steps
- How to make jobs rerunnable

### Database for testing
Used to have an In-memory job store which was a pain for configure.
Now it has been removed and the recommendation is to use an in-memory
database like https://www.h2database.com or http://hsqldb.org


## List

- Simple Jobs: use tasklets

- When/how to skip failed instances
- How to set chunk size for commits
- multiple data sources



