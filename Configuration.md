# Configuration #

To configure your TM2O web service you can use a property file or the web interface to do so.

## Using the Property File ##

If you want to use a property file to configure the TM2O service, the following steps are necessary:

# Open your home directory.
# Create a new folder with the name '.tm2o' and open it.
# Create a new file with name 'tm2o.properties'.
# Edit the file with a text editor.

The following properties are supported:

|**property name**|**description**|**values**|**mandatory**|
|:----------------|:--------------|:---------|:------------|
|namespace        |The namespace of your OData service|Any string|yes          |
|association-mode |The mode of mapping associations.|FLAT\_ASSOCIATION or STRONG\_ASSOCIATION|yes          |
|content-provider |The class name of used content provider|de.topicmapslab.odata.content.memory.MemoryOdataContentProvider or de.topicmapslab.odata.content.remote.RemoteOdataContentProvider|yes          |
|path             |The file to a topic map file(.ctm,.xtm,.ltm)|The absolute path| yes (in case of memory content provider)|
|server           |The address of the remote server|a valide URL|yes (in case of remote content provider)|
|api-key          |The API key of the user of the remote server|a valide API key|yes (in case of remote content provider)|

## Using the web Interface ##

After the deployment of your TM2O service, a configuration interface is avalaible under the service root: _your-webserver-root/tm2o_

You can edit the namespace of your TM2O webservice using any non empty string, which does not contain any whitespaces.

For association mode you can choose FLAT or STRONG mode.

The content provider can be choosed by select the Remote Content Provider or Local Content Provider.

For local content providers you should be upload any topic map file to the server. If you choose the remote provider you must be enter a valid address of a MaJorToM server. If the URL is invalid, it will be colored red.

![http://tm2o.googlecode.com/files/figure-tm2o-configuration.png](http://tm2o.googlecode.com/files/figure-tm2o-configuration.png)