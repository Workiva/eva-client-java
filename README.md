# Eva Client Java

This library provides an easy way to interact with the [Eva Client Service](https://github.com/Workiva/eva-client-service).

This library should closely resemble [Eva's API](https://github.com/Workiva/eva).
If you are already familiar with [Eva](https://github.com/Workiva/eva) you should be able to quickly get going.

You can view language specific readmes here:

* [Eva Client Java via Java](docs/readme_java.md)
* [Eva Client Java via Clojure](docs/readme_clojure.md)

### Environment Variables

#### EVA_SANITIZE_EXCEPTIONS

By default, this library sanitizes exceptions that it gets back from the client service. These exceptions can be logged. To change whether or not your exceptions are sanitized, you can set the `EVA_SANITIZE_EXCEPTIONS` environment variable to false.  
When exceptions are sanitized the message is removed and the `getData()` method will return an empty map.  

> It should be noted that `EvaException`s have a `getUnsanitized()` method, that will allow you to get the original exception before it was sanitized.

For more information and examples on working with `EvaException`s [check out the following document](docs/eva_exceptions.md):

## Maintainers and Contributors

### Active Maintainers

- Daniel Harasymiw <daniel.harasymiw@workiva.com>

### Previous Contributors

- Daniel Harasymiw <daniel.harasymiw@workiva.com>
- Tyler Wilding <tyler.wilding@workiva.com>
