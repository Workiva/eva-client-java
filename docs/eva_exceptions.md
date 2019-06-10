# EvaException's in Detail

## Why?

There is the potential for secrets, customer data, etc to be attached to an exception thrown by the EVA transactors or Peer library.  Therefore everything must be sanitized by default to curb the potential for accidental logging.

However, of course said data on the exception is vital for debugging anomalous events or controlling application logic. Therefore, any `EvaException` can be turned into it's unsanitized and full version.

## Examples

### Non-Transaction Function Exception Handling (Queries, etc)

```java
// Example 1 - Non-Transaction Function Exception Handling
try {
    Database db = conn.db();
    // Error Details - Empty :in clause, this will cause a spec failure
    Object result = Client.query("[:find ?b :in :where [?b :db/doc _]]", db);
} catch (com.workiva.eva.client.exceptions.EvaException ex) {
    // The exception will be sanitized to start with, which means no data, no message.
    logger.info(String.format("Exceptions Data - %s", ex.getData().toString()));
    logger.info(String.format("Exceptions Message - %s", ex.getMessage()));
    // However there is still information that we can pull out of a sanitized exception
    // such as the EvaErrorCode
    if (ex.getErrorCode() == EvaErrorCode.INCORRECT_QUERY_SYNTAX) {
        // If your query is sensitive and can't be logged, you could perhaps associate your query
        // to a unique identifier and log that so it can be traced back
        throw new RuntimeException("Query #1001 failed due to invalid syntax");
    } else {
        // We can safely log the exception as it is sanitized
        logger.error("Uncaught Error Occurred from Query #1001", ex);
    }
}
```

### Non-Custom Transaction Function Exception Handling

```java
// Example 2 - Transaction Function Exception Handling
try {
    Object result = conn.transact("[[:db.fn/cas 0 :db/doc \"The default database partition.\" \"Testing\"]]");
    // Error Details - This fails the compare and swap assertion
    result = conn.transact("[[:db.fn/cas 0 :db/doc \"The default database partition.\" \"Testing\"]]");
} catch (com.workiva.eva.client.exceptions.EvaException ex) {
    // The exception will be sanitized to start with, which means no data, no message.
    logger.info(String.format("Exceptions Data - %s", ex.getData().toString()));
    logger.info(String.format("Exceptions Message - %s", ex.getMessage()));
    // However there is still information that we can pull out of a sanitized exception
    // such as the EvaErrorCode
    if (ex.getErrorCode() == EvaErrorCode.TRANSACTION_FUNCTION_EXCEPTION) {
        // Let's check the data from the transaction function exception by getting the unsanitized version
        IPersistentMap causeData = ex.getUnsanitized().getCause().getData();
        if (causeData.valAt(Keyword.intern("found")).equals("Testing")) {
            logger.info("CAS Error Occurred as expected due to the test example");
        } else {
            // If your transaction is sensitive and can't be logged, you could perhaps associate it
            // to a unique identifier and log that so it can be traced back
            throw new RuntimeException("Transaction #2001 failed due to invalid syntax");
        }
    } else {
        // We can safely log the exception as it is sanitized
        logger.error("Uncaught Error Occurred from Transaction #2001", ex);
    }
}
```

### Custom Transaction Function Exception Handling

```java
// Example 3 - Custom Transaction Functions
try {
    // Note the use of ex-info - http://clojuredocs.org/clojure.core/ex-info
    // this can be used to add useful metadata to the exception in the event it is thrown
    Map result = conn.transact("[{:db/id #db/id [:db.part/user -1]\n" +
            "    :db/ident :example-3\n" +
            "    :db/doc \"Function that throws an exception\"\n" +
            "    :db/fn #db/fn\n" +
            "             {:lang \"clojure\"\n" +
            "              :params [db]\n" +
            "              :code (throw (ex-info \"Test Error\" {:application/error :test/exception}))}}]");
    result = conn.transact("[[:example-3]]");
} catch (com.workiva.eva.client.exceptions.EvaException ex) {
    // The exception will be sanitized to start with, which means no data, no message.
    logger.info(String.format("Exceptions Data - %s", ex.getData().toString()));
    logger.info(String.format("Exceptions Message - %s", ex.getMessage()));
    // However there is still information that we can pull out of a sanitized exception
    // such as the EvaErrorCode
    if (ex.getErrorCode() == EvaErrorCode.TRANSACTION_FUNCTION_EXCEPTION) {
        // Let's check the data from the transaction function exception by getting the unsanitized version
        IPersistentMap causeData = ex.getUnsanitized().getCause().getData();
        // By switching on the errorType, we can correlate exceptions easily to our specific application's logic
        String errorType = ((Keyword) causeData.valAt(Keyword.intern("application", "error"))).toString();
        switch (errorType) {
            case ":test/exception":
                throw new RuntimeException("Exception occurred as expected");
            default:
                logger.error("Unknown transaction function occurred in #2002", ex);
                throw new RuntimeException("Unknown transaction function occurred in #2002");
        }
    } else {
        // We can safely log the exception as it is sanitized
        logger.error("Uncaught Error Occurred from Transaction #2002", ex);
    }
}
```
