package de.topicmapslab.odata.web;

import java.util.Properties;

import de.topicmapslab.odata.content.IOdataContentProvider;

public class ODataConfiguration {

	private final String namespace;
	private final Class<? extends IOdataContentProvider> clazz;
	private final Properties properties;

	/**
	 * @param clazz
	 *            the class
	 * @param properties
	 *            the properties
	 * @param namespace
	 *            the namespace
	 */
	public ODataConfiguration(Class<? extends IOdataContentProvider> clazz, Properties properties, String namespace) {
		this.namespace = namespace;
		this.clazz = clazz;
		this.properties = properties;
	}

	/**
	 * Returns the properties
	 * 
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Returns the content provider class
	 * 
	 * @return the class
	 */
	public Class<? extends IOdataContentProvider> getClazz() {
		return clazz;
	}

	/**
	 * Returns the namespace
	 * 
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

}
