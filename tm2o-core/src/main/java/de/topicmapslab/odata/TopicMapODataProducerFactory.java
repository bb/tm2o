package de.topicmapslab.odata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.ODataProducerFactory;

import de.topicmapslab.odata.content.IOdataContentProvider;
import de.topicmapslab.odata.exception.TopicMapsODataException;

public class TopicMapODataProducerFactory implements ODataProducerFactory {

	private static TopicMapODataProducer producer;

	/**
	 * Returns the internal producer
	 * 
	 * @return the producer
	 */
	public static TopicMapODataProducer getProducerInstance() {
		if (producer == null) {
			producer = createStatic();
		}
		return producer;
	}

	/**
	 * Static method to load a new data producer instance
	 * 
	 * @param properties
	 *            the properties
	 * @return the new data producer
	 */
	@SuppressWarnings("unchecked")
	public static TopicMapODataProducer createStatic() {
		Properties properties = loadProperties();
		Object oContentProviderClass = properties.get("contentProvider");
		if (oContentProviderClass == null) {
			throw new RuntimeException("Missing required properties 'contentProvider'");
		}
		/*
		 * load properties
		 */
		Object oNamespace = properties.get("namespace");
		if (oNamespace == null) {
			throw new TopicMapsODataException("Missing required properties 'namespace'");
		}
		try {
			Class<IOdataContentProvider> clazz = (Class<IOdataContentProvider>) Class.forName(oContentProviderClass.toString());
			String namespace = oNamespace.toString();
			/*
			 * create content provider
			 */
			producer = new TopicMapODataProducer(clazz, properties, namespace);
			return producer;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ODataProducer create(Properties properties) {
		return getProducerInstance();
	}

	public static final String PATH = System.getProperty("user.home") + "/.tm2o";
	private static final String FILE = PATH + "/tm2o.properties";

	/**
	 * Writes the given properties to user dir
	 * 
	 * @param clazz
	 *            the class
	 * @param properties
	 *            the properties
	 * @param namespace
	 *            the namespace
	 */
	public static void writeProperties(Class<? extends IOdataContentProvider> clazz, Properties properties, String namespace) {
		File file = new File(FILE);
		if (!file.exists()) {
			try {
				new File(PATH).mkdirs();
				file.createNewFile();
			} catch (Exception e) {
				System.err.println("Cannot create properties file");
				return;
			}
		}
		try {
			Properties toPersist = new Properties();
			toPersist.putAll(properties);
			toPersist.remove("file");
			toPersist.put("namespace", namespace);
			toPersist.put("contentProvider", clazz.getName());
			toPersist.store(new FileOutputStream(file), "TM2O");
		} catch (Exception e) {
			System.err.println("Cannot write properties to file");
			return;
		}
	}

	/**
	 * Loads the properties from file
	 * 
	 * @return the properties
	 */
	public static Properties loadProperties() {
		File file = new File(FILE);
		InputStream is;
		if (file.exists()) {
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				is = TopicMapODataProducerFactory.class.getResourceAsStream("tm2o.properties");
			}
		} else {
			is = TopicMapODataProducerFactory.class.getResourceAsStream("tm2o.properties");
		}
		Properties p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load properties from file!", e);
		}
		return p;
	}
}
