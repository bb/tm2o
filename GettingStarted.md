# Getting Started with TM2O #

Here you will find a short description how you can get started with the TM2O service.

## Deployment ##

In the first step you have to deploy the service at your webserver. Get all information about the deployment [here](http://code.google.com/p/tm2o/wiki/Deployment). Once you have deployed the service (i.e. at localhost:8080) you can access the service at

_http://localhost:8080/tm2o/_

If you successfully deployed the service you should see the tm2o start screen.

![http://tm2o.googlecode.com/files/figure-start-screen.png](http://tm2o.googlecode.com/files/figure-start-screen.png)

## TM2O Configuration ##

In the section "Available Services" you will now read

**No topic maps created yet, go to administration interface to upload a new file!** or **The MaJorToM server does not response to any request.**

Before your get a list of OData services you have to configure the connection to your data sources. Follow the link "here" in the section "TM2O Configuration" to access the configuration interface.

In the configuration interface you have to specify some basic characteristics (use Namespace "OData" and "FLAT" association mode to get started) and the data provider for the TM2O service. Full details about the configuration are available [here](http://code.google.com/p/tm2o/wiki/Configuration).

![http://tm2o.googlecode.com/files/figure-tm2o-configuration.png](http://tm2o.googlecode.com/files/figure-tm2o-configuration.png)

To get started with the TM2O service you simply choose the URL of a running majortom server or you upload a topic map to the server. In the section [Provider](http://code.google.com/p/tm2o/wiki/Provider) you get full information about the provider concept in tm2o.

  * If a running instance of a majrotom server is available chhose the Option "Remote Content Provider" and enter the URL of the server, like _http://nemo.tm.informatik.uni-leipzig.de:8080/majortom-server/_.

  * If no running instance of a majortom server is available choose the Option "Local Content Provider" and upload the [ToyTM](http://tm2o.googlecode.com/files/download%5B2%5D.xtm).

After pressing the button "Set" the configuration of the TM2O service is changed. Now follow the link "available services listed here" at the bottom of the page.

## Create a OData service ##

Now you can see a list of topic maps which are available by the remote majortom server (or locally stored by your tm2o service).

To create a OData service for one of this maps you have to press at the according topic map. With this click you will trigger the autogeneration of the OData Schema based on the topic map. Dependent on the complexity of the underlying topic map this process might take a while.

After the successful creation of the OData service you will see the service description delivered by the service URL, like

_http://localhost:8080/tm2o/odata.svc/3d46a9abbb1b6f98ddd4c0c7dc2c671_

The seervice description delivers you a number of references to collections. If you want the data for a collection - let's say "Park" - packed in JSON you have to specify the following URL

_http://localhost:8080/tm2o/odata.svc/3d46a9abbb1b6f98ddd4c0c7dc2c671/Park?$format=json_

## Using the data ##

Once you have created your OData service out of a topic map, you can use the data in the existing OData infrastructure. [Here](http://www.odata.org/consumers) you will find a list of existing OData consumers.

One of these consumers is the [Sesame Data Browser](http://metasapiens.com/sesame/data-browser/).

![http://tm2o.googlecode.com/files/sesame%20data%20browser.png](http://tm2o.googlecode.com/files/sesame%20data%20browser.png)

Download and install the data browser locally. After starting the data browser you can open a new connection. To open a connection with the OData service you have to enter the URL of the service description, like:

_http://localhost:8080/tm2o/odata.svc/3d46a9abbb1b6f98ddd4c0c7dc2c671_

If your OData service (and the data provider behind the OData service) is running correctly, you can start to browse in your data as it is depicted in the figure above.