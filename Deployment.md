# Deployment #

This wiki page explain the deployment process of the TM2O webservice.

## Dependencies ##

The TM2O service is designed as a Java web service which runs on a servlet container (e.g. [Apache Tomcat](http://tomcat.apache.org/download-70.cgi). To use the remote content provider of a TM2O web service the [MaJorToM Webserver](http://code.google.com/p/majortom-server) is required.

## Deploy the service ##

The service is provided by a web archive file (.war). At first this file has to be deployed in your web server (e.g. tomcat). There are two possibilities to do so - using the HTML manager or copy the file to working directory of tomcat. Please note that the HTML manager is an optional component. After the deployment the TM2O service is avalaibe under the following URL:

_your-web-server-root/tm2o_

![http://tm2o.googlecode.com/files/figure-start-screen.png](http://tm2o.googlecode.com/files/figure-start-screen.png)