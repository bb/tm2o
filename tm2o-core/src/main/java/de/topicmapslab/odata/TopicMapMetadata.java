package de.topicmapslab.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.odata4j.core.ODataConstants;
import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmSchema;
import org.odata4j.producer.QueryInfo;

import de.topicmapslab.odata.content.IOdataContentProvider;
import de.topicmapslab.odata.content.empty.EmptyOdataContentProvider;

/**
 * the topic map meta data provider
 * 
 * @author Sven Krosse
 */
public class TopicMapMetadata {
	private static final String CONTAINER_NAME = "Container";
	private Class<? extends IOdataContentProvider> contentProviderClass;
	private Properties properties;
	private String namespace;
	private final Map<String, IOdataContentProvider> contentProviders = new HashMap<String, IOdataContentProvider>();
	private final Map<String, EdmDataServices> services = new HashMap<String, EdmDataServices>();

	/**
	 * constructor
	 * 
	 * @param contentProvider
	 *            the contentProvider
	 * @param namespace
	 *            the namespace
	 */
	public TopicMapMetadata(final Class<? extends IOdataContentProvider> contentProviderClazz, final Properties properties, final String namespace) {
		this.contentProviderClass = contentProviderClazz;
		this.properties = properties;
		this.namespace = namespace;
	}

	/**
	 * Reload the configuration of this OData service
	 * 
	 * @param contentProviderClazz
	 *            the provider class
	 * @param properties
	 *            the properties
	 * @param namespace
	 *            the namespace
	 */
	public void reload(final Class<? extends IOdataContentProvider> contentProviderClazz, final Properties properties, final String namespace) {
		this.contentProviderClass = contentProviderClazz;
		this.properties = properties;
		this.namespace = namespace;
		/*
		 * clear existing ones
		 */
		contentProviders.clear();
		services.clear();
	}

	/**
	 * Get internal properties
	 * 
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Returns the interal set namespace
	 * 
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Returns the content provider class
	 * 
	 * @return the class
	 */
	public Class<? extends IOdataContentProvider> getContentProviderClass() {
		return contentProviderClass;
	}

	/**
	 * Utility method to build OData service schema from topic map
	 * 
	 * @return the
	 */
	private EdmDataServices buildDataService(final String topicMapId) {
		IOdataContentProvider contentProvider = getContentProvider(topicMapId);
		List<EdmSchema> schemas = new ArrayList<EdmSchema>();
		List<EdmEntityContainer> containers = new ArrayList<EdmEntityContainer>();
		List<EdmEntitySet> entitySets = contentProvider.getEntitySets();
		List<EdmEntityType> entityTypes = contentProvider.getEntityTypes();
		List<EdmAssociation> associations = contentProvider.getAssociations();
		/*
		 * create container
		 */
		EdmEntityContainer container = new EdmEntityContainer(CONTAINER_NAME, true, null, entitySets, null, null);
		containers.add(container);
		/*
		 * create schema
		 */
		EdmSchema schema = new EdmSchema(namespace, null, entityTypes, null, associations, containers);
		schemas.add(schema);
		return new EdmDataServices(ODataConstants.DATA_SERVICE_VERSION, schemas);
	}

	/**
	 * Checks if the service for the given topic map id is loaded
	 * 
	 * @param topicMapId
	 *            the topic map id
	 * @return <code>true</code> if the service is loaded, <code>false</code> otherwise.
	 */
	public boolean isLoadedService(final String topicMapId) {
		return services.containsKey(topicMapId);
	}

	/**
	 * Returns the EDM Data service representing the meta model
	 * 
	 * @param topicMapId
	 *            the topic map Id
	 * @return the meta model
	 */
	public EdmDataServices getService(final String topicMapId) {
		/*
		 * check if topic map id already bound to a EDM service
		 */
		if (!services.containsKey(topicMapId)) {
			/*
			 * build new service
			 */
			services.put(topicMapId, buildDataService(topicMapId));
		}
		/*
		 * return service
		 */
		return services.get(topicMapId);
	}

	/**
	 * Returns the entity for the given entity id
	 * 
	 * @param topicMapId
	 *            the topic map id
	 * @param entitySetName
	 *            the name of the entity set name
	 * @param entityId
	 *            the entity id
	 * @return the entity and never <code>null</code>
	 */
	public OEntity getEntity(final String topicMapId, final String entitySetName, final String entityId) {
		/*
		 * get entity set
		 */
		EdmEntitySet entitySet = getService(topicMapId).getEdmEntitySet(entitySetName);
		if (entitySet == null) {
			throw new RuntimeException("Entity set for name '" + entitySetName + "' not found!");
		}
		return getContentProvider(topicMapId).getEntity(entitySet, entitySet.type.name, entityId);
	}

	/**
	 * Returns all entities of the given entity set
	 * 
	 * @param topicMapId
	 *            the topic map id
	 * @param entitySetName
	 *            the name of the entity set name
	 * @param queryInfo
	 *            the query info
	 * @return a possible empty list of entities
	 */
	public List<OEntity> getEntities(final String topicMapId, final String entitySetName, final QueryInfo queryInfo) {
		/*
		 * get entity set
		 */
		EdmEntitySet entitySet = getService(topicMapId).getEdmEntitySet(entitySetName);
		if (entitySet == null) {
			throw new RuntimeException("Entity set for name '" + entitySetName + "' not found!");
		}
		return getContentProvider(topicMapId).getEntities(entitySet, queryInfo);
	}

	/**
	 * Returns all counter entities of the given entity set
	 * 
	 * @param entitySetName
	 *            the name of the entity set name
	 * @param entityId
	 *            the Id of left hand navigation node
	 * @param navProperty
	 *            the navigation property
	 * @param counterType
	 *            the counter type
	 * @param queryInfo
	 *            the query info
	 * @return a possible empty list of entities
	 */
	public List<OEntity> getEntities(final String topicMapId, final String entitySetName, final String entityId, final String navProperty,
			final QueryInfo queryInfo) {
		/*
		 * get entity set
		 */
		EdmEntitySet entitySet = getService(topicMapId).getEdmEntitySet(entitySetName);
		if (entitySet == null) {
			throw new RuntimeException("Entity set for name '" + entitySetName + "' not found!");
		}
		return getContentProvider(topicMapId).getEntities(entitySet, entityId, navProperty, queryInfo);
	}

	/**
	 * Internal method to get the content provider of the given topic map id
	 * 
	 * @param topicMapId
	 *            the topic map id
	 * @return the content provider and never <code>null</code>
	 */
	private IOdataContentProvider getContentProvider(final String topicMapId) {
		if (contentProviders.containsKey(topicMapId)) {
			return contentProviders.get(topicMapId);
		}
		try {
			IOdataContentProvider contentProvider = contentProviderClass.getConstructor(String.class).newInstance(topicMapId);
			contentProvider.initialize(namespace, properties);
			contentProvider.setService(this);
			contentProviders.put(topicMapId, contentProvider);
			return contentProvider;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return new EmptyOdataContentProvider();
		}
	}
}
