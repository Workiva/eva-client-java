# Clojure Documentation

This guide will go over how to use Eva Client Java using Clojure.

<!-- toc -->

- [Creating a Connection](#creating-a-connection)
- [Transact](#transact)
- [Query](#query)
- [Pull](#pull)
- [Invoke](#invoke)
- [to-tx-eid](#to-tx-eid)
- [ident](#ident)
- [entid](#entid)
- [Inline Functions](#inline-functions)
  * [Query](#query-1)
  * [latestT](#latestt)
  * [entid](#entid-1)
  * [ident](#ident-1)
  * [first](#first)
  * [ffirst](#ffirst)

<!-- tocstop -->

## Creating a Connection

The first thing you need to do is create a connection. You can create a connection by passing a map to the `connect` function.

```clj
(ns eva101.core
    (:require [eva.client.core :as eva]))
    
(def conn (eva/connect {:tenant "tenant"
							 :category "category"
							 :label "label" }))
```

## Transact

You can then transact as you normally would:
```clj
(eva/transact conn [[:db/add (eva/tempid :db.part/user) :book/title "First Book"]]) 
```

## Query

Once you've transacted some data, you'll be able to perform queries to retrieve that data. The first step in being able to query is getting your `Database`.
```clj
(def db (eva/db conn))
```

This will get you a database that you can query against the latest state with. If you wish to query against an older version of the database you can do that as so:

```clj
(def historical-db (eva/as-of db 123))
```

Now that you have the `Database` that you wish to query against, you can perform your queries.
```clj
(eva/q '[:find ?title :in $ :where [?b :book/title ?title]], db)
```

You can also pass arguments to your queries:
```clj
(eva/q [:find ?title :in $ ?year :where 
			[?b :book/year_published ?year]
			[?b :book/title ?title]], db, 2017)
```

## Pull

Pull is also done the same as you normally do with the Eva API.

```clj
(eva/pull db '[*] 8796093023236)
```



## Invoke

It is also possible to invoke functions with the Eva Client Service through invoke.

```clj
(eva/invoke db :db.fn/cas db 0 :db/doc "The default database partition." "Testing")
```

## to-tx-eid

Given a transaction id, you can get the entity id of that transaction

```clj
(eva/to-tx-eid db 1)
```

## ident

Ident gives you the ability to get the ident of an entity, with a given entity id.

```clj
(eva/ident db 1)
```

## entid

This does the reverse of ident, it gives you the ability to get an entity id with a given ident.

```clj
(eva/entid db :db.fn/cas)
```

## Inline Functions

In the cases where you want to chain together Eva Client functions, you might have to do multiple requests.
To cut down on this you might be able to use inline functions. Inline functions can be passed as arguments to Eva Client functions
and will be evaluated by the client service.

For example, if you wanted to create a `Database` to query with and wanted a specific tx id for the `as-of` you would 
normally have to perform a query to first to get that `tx-id` before creating your `Database` object. With an inline function 
however you could do the same thing without having to make that request.

```clj
(ns examples.eva101.core
  (:require [com.workiva.eva.client.inline-function :as inline]))
  
(def inline-as-of
	(inline/ffirst
		(inline/q '[:find ?tx :in $ ?t :where [?b :book/title ?t ?tx]] db "First Book")))
	
(def historicalDb (eva/as-of db inline-as-of))
```

In this example, we create an `InlineFunction` that will perform a query to get the tx that we need for our `as-of` value.
Since this query would return a value that looked like this `[[ 123 ]]`, we need to use another function to pull that tx out of the nested vectors.
This is why our inline query is wrapped in the `InlineFunction` `ffirst` so that we can get the value inside the nested vectors.

The inline functions that are currently supported are:

### Query

Performs a query

```clj
(inline/q '[:find ?tx :in $ ?t :where [?b :book/title ?t ?tx]] db "First Book")
```

### latestT

Gets the latest tx id on a `Connection`

```clj
(inline/latest-t conn)
```

### entid

Determines the entity id of an ident

```clj
(inline/entid db :db.part/tx)
```

### ident

Determines the ident of an entity id.

```clj
(inline/ident db 1)
```

### first

Returns the first item in a collection.

```clj
inline/first(
  (inline/q '[:find ?ident :in $ eid :where [?eid :db/ident ?ident]] db))
```

### ffirst

Returns the first item of the first item in a collection. This is equivalent to wrapping a `first` in another `first`. 

```clj
(inline/ffirst
  (inline/q '[:find ?tx :in $ ?t :where [?b :book/title ?t ?tx]] db "First Book"))
```
