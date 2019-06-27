# Java

This guide will go over how to use Eva Client Java with Java.

<!-- toc -->

- [Creating a Connection](#creating-a-connection)
- [Transact](#transact)
- [Query](#query)
- [Pull](#pull)
- [Invoke](#invoke)
- [toTxEid](#totxeid)
- [ident](#ident)
- [Inline Functions](#inline-functions)
  * [Query](#query-1)
  * [latestT](#latestt)
  * [entid](#entid-1)
  * [ident](#ident-1)
  * [first](#first)
  * [ffirst](#ffirst)

<!-- tocstop -->

## Creating a Connection

The first thing you'll need to do is get a `Connection`. To get a connection, you need to create a connection configuration map.
A connection configuration map is a map that contains the following:

| Key | Example Value | Description                        |
|----------|-------------|-----------------------------------------|
| tenant | my-tenant | The tenant of the Eva database |
| category | my-catgory | The category of the Eva database |
| label | my-label | The label of the Eva database |

You can either create this map yourself as you normally would in Java for example:
```java
Map connectionConfig = new HashMap() {{
	put(Keyword.intern("tenant"), "my-tenant");
	put(Keyword.intern("category"), "my-category");
	put(Keyword.intern("label"), "my-label");
}};
```

Or you can use the utility function:
```java
Map connectionConfig = Util.createConnectionConfig("my-tenant", "my-category", "my-label");
																																																			
```

Once you have your connection configuration, we can use this to create a `Connection` that you can use to transact to Eva
or to get Eva databases that you can query with. To create a connection do the following:

```java
Connection conn = Peer.connect(connectionConfig);
```

## Transact

You can then the start transacting as you would normally with Eva:
```
conn.transact("[" +
                "[:db/add #db/id [:db.part/user] :book/title \"First Book\"]" +
                "[:db/add #db/id [:db.part/tx] :author/name \"Author Name\"]" +
              "]");
```

It should also be noted, that both transact and query can be passed datastructures instead of their string representations as well.

## Query

Before you do a query, you first have to create a `Database`.

```java
Database db = conn.db();
```

If you wanted to do a historical query, you can pass a tx id to dbAt to get a db at a point in time.

```java
Database db = conn.dbAt(123);
```

There may also be times where you don't know the tx id of the snapshot you want. 
In these cases you can create a `Database` that has an `as of` value of an inline function that will determine what tx id you need.


With your database, you are now able to query.

```java
List result = Peer.query("[:find ?title :in $ :where " +
                            "[?b :book/title ?title]]", db);
```

You can also pass arguments to your queries.

```java
List result = Peer.query("[:find ?title :in $ ?year :where " + 
														"[?b :book/year_published ?year]" +
														"[?b :book/title ?title]]", db, 2017);
```


## Pull

Pull is also done the same as you normally do with the Eva API.

```java
List result = db.pull("[*]", someEntityId);
```

## Invoke

It is also possible to invoke functions with the Eva Client Service through invoke.

```java
result = db.invoke(Keyword.intern("db.fn", "cas"), db, 0, Keyword.intern("db", "doc"), "The default database partition.", "Testing");
```

## toTxEid

Given a transaction id, you can get the entity id of that transaction

```java
result = Client.toTxEid(db, 1);
```

## ident

Ident gives you the ability to get the ident of an entity, with a given entity id.

```java
db.ident(42);
````

#### entid

This does the reverse of ident, it gives you the ability to get an entity id with a given ident.

```java
db.entid(Keyword.intern("db.fn", "cas");
```


## Inline Functions

In the cases where you want to chain together Eva Client functions, you might have to do multiple requests.
To cut down on this you might be able to use inline functions. Inline functions can be passed as arguments to Eva Client functions
and will be evaluated by the client service.

For example, if you wanted to create a `Database` to query with and wanted a specific tx id for the `as-of` you would 
normally have to perform a query to first to get that `tx-id` before creating your `Database` object. With an inline function 
however you could do the same thing without having to make that request.

```java
InlineFunction inlineAsOf = UtilFunctions.ffirst(
                PeerFunctions.query("[:find ?tx :in $ ?t :where [?b :book/title ?t ?tx]]", db, "First Book")
);

Database historicalDb = conn.dbAt(inlineAsOf);
```

In this example, we create an `InlineFunction` that will perform a query to get the tx that we need for our `as-of` value.
Since this query would return a value that looked like this `[[ 123 ]]`, we need to use another function to pull that tx out of the nested vectors.
This is why our inline query is wrapped in the `InlineFunction` `ffirst`. If you're familiar with Clojure you'll recognize this function.
If not, `ffirst` gets the first item out of the first item in a collection.

The inline functions that are currently supported are:

### Query

Performs a query

```java
InlineFunction query = PeerFunctions.query("[:find ?tx :in $ ?t :where [?b :book/title ?t ?tx]]", db, "First Book");
```

### latestT

Gets the latest tx id on a `Connection`

```java
InlineFunction latestT = ConnectionFunctions.latestT(conn);
```

### entid

Determines the entity id of an ident

```java
InlineFunction entid = DatabaseFunctions.entid(db, Keyword.intern("db.part", "tx"));
```

### ident

Determines the ident of an entity id.

```java
InlineFunction ident = DatabaseFunctions.entid(db, 1);
```

### first

Returns the first item in a collection.

```java
InlineFunction first = UtilFunctions.first(
  PeerFunctions.query("[:find ?ident :in $ eid :where [?eid :db/ident ?ident]]", db)      
);
```

### ffirst

Returns the first item of the first item in a collection. This is equivalent to wrapping a `first` in another `first`. 

```java
InlineFunction first = UtilFunctions.first(
  PeerFunctions.query("[:find ?tx :in $ ?t :where [?b :book/title ?t ?tx]]", db, "First Book")      
);
```
