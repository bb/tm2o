# OData Service #

After the deployment and configuration of the TM2O service, the web module provides a OData service, which can be used by any avalaible OData browsers like Microsoft© SQL Server© PowerPivot for Excel©.

The  OData service is avalaible under

_your-web-server-root/tm2o/odata.svc/{topic-map-id}_

Example: _http://localhost:8080/tm2o/odata.svc/bddddba8f4c25fa590be1250d41261_

## Topic Map Id ##

Any topic map stored in MaJorToM server or local get an identification number. In case of remote content providers the id of the topic map can be read under the following url:

_your-majortom-server-address/tm/topicmaps_

In case of local mode, the topic map id currently has no relevance an can be set to any number but may not missed.

## Format ##

The OData service delivers the data either in ATOMpub or in JSON format. Use the _format_ parameter to specify the desired format.

| Atom (default) | _your-web-server-root/tm2o/odata.svc/{topic-map-id}?$format=atom_ |
|:---------------|:------------------------------------------------------------------|
| JSON           | _your-web-server-root/tm2o/odata.svc/{topic-map-id}?$format=json_ |
