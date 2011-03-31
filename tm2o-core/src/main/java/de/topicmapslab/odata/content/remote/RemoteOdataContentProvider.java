package de.topicmapslab.odata.content.remote;

import java.util.Properties;

import de.topicmapslab.odata.content.TmqlOdataContentProviderImpl;
import de.topicmapslab.odata.exception.TopicMapsODataException;

/**
 * Content provider using a remote topic map data source running in a MaJorToM server
 * 
 * @author Sven Krosse
 */
public class RemoteOdataContentProvider extends TmqlOdataContentProviderImpl<RemoteTmqlHelper> {

	private RemoteTmqlHelper helper;
	private String server;

	/**
	 * constructor
	 * 
	 * @param topicMapId
	 *            the topic map id
	 */
	public RemoteOdataContentProvider(String topicMapId) {
		super(topicMapId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(String namespace, Properties properties) throws TopicMapsODataException {
		/*
		 * load properties
		 */
		Object oServer = properties.get("server");
		if (oServer == null) {
			throw new TopicMapsODataException("Missing required property of server URL");
		}
		server = oServer.toString();
		helper = new RemoteTmqlHelper(getServerUrl(), getTopicMapId());
		super.initialize(namespace, properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RemoteTmqlHelper getTmqlHelper() {
		return helper;
	}

	/**
	 * Returns the server URL
	 * 
	 * @return the server URL
	 */
	protected String getServerUrl() {
		return server;
	}
}
