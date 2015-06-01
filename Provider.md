# Provider #

The TM2O service is designed as a modulare and flexible service, which allows to access remote semantic data using a special content provider. As an alternative, the semantic data can be stored within an in-process topic map.

## Local Content Provider ##

The local content provider store the semantic data in a memory based topic map. The topic map is protected and handled by the [MaJorToM Topic Maps Engine](http://code.google.com/p/majortom). To do so, the user should provide a topic map file, which contains the semantic information which should be mapped as OData. The file type will be estimated automatically.

## Remote Content Provider ##

The remote content provider fetches any semantic information from an external MaJorToM server. The communcation is done by using TMQL and a JTMQR messages. See the documentation of MaJorToM server and [TMQL4J](http://code.google.com/p/tmql) for more information or contact the developer.