# Architecture #

On top of the architecture is a Java RESTful web service which uses the [odata4j library](http://code.google.com/p/odata4j/). This service provides the HTTP interface to handle client requests and result transformation into JSON or Atompub. The OData service is obliged to provide a service description. Usually, the producer generates the OData content directly by accessing the real data source. For improved flexibility we extend the service architecture by a content provider, which uses TMQL queries to fetch data sets needed for the current request or for meta-data extraction.

![http://tm2o.googlecode.com/files/architecture.png](http://tm2o.googlecode.com/files/architecture.png)

The queries are executed by the Java-based Topic Maps query engine [TMQL4J](http://code.google.com/p/tmql/). The usage of queries enables a decoupled architecture which in turn allows accessing distributed data sources. The mapping of the query results is realized by the TM2O module, which transforms the Topic Maps content to the EDM.

The data storage for the underlying topic maps is realized by the Topic Maps Engine [majortom](http://code.google.com/p/majortom), which provides an abstraction layer on top of the real storage paradigm, like relational databases or memory. The whole webservice is running in a [majortom webserver](http://code.google.com/p/majortom-server) which is based on the [Spring Framework](http://www.springsource.org/).