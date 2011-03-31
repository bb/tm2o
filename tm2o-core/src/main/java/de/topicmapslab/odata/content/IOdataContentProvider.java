package de.topicmapslab.odata.content;

import java.util.List;
import java.util.Properties;

import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.producer.QueryInfo;

import de.topicmapslab.odata.TopicMapMetadata;
import de.topicmapslab.odata.config.EContentProviderConfiguration;
import de.topicmapslab.odata.edm.IEdmPropertyIdentifier;
import de.topicmapslab.odata.exception.TopicMapsODataException;

/**
 * Interface definition of an OData content provider accessing the topic maps source
 * 
 * @author Sven Krosse
 */
public interface IOdataContentProvider {

	/**
	 * token for combination of relationship and counter player type
	 */
	public static final String RELATIONSHIP_DELIMER = "-";
	/**
	 * key constant
	 */
	public static final String ID_PROPNAME = "id";

	/**
	 * Returns all entity sets of the content provider
	 * 
	 * @return the entity sets
	 */
	public List<EdmEntitySet> getEntitySets();

	/**
	 * Returns all EDM associations of the content provider
	 * 
	 * @return the associations
	 */
	public List<EdmAssociation> getAssociations();

	/**
	 * Register the service
	 * 
	 * @param service
	 *            the service
	 */
	public void setService(TopicMapMetadata service);

	/**
	 * Method called to initialize the content provider.
	 * 
	 * @param namespace
	 *            the namespace of this service
	 * @param properties
	 *            properties containing all arguments necessary to initialize the content provider
	 * @throws TopicMapsODataException
	 *             thrown if the initialization failed
	 */
	public void initialize(String namespace, Properties properties) throws TopicMapsODataException;

	/**
	 * Returns all topic types of the topic map.
	 * 
	 * @return the list of EDM entity types
	 */
	public List<EdmEntityType> getEntityTypes();

	/**
	 * Return the entity for the given id and entity set
	 * 
	 * @param entitySet
	 *            the entity set
	 * @param entityType
	 *            the name of the entity type
	 * @param id
	 *            the id of the entity
	 * @return the created entity
	 */
	public OEntity getEntity(final EdmEntitySet entitySet, final String entityType, final String id) throws TopicMapsODataException;

	/**
	 * Returns all entities of the given entity set
	 * 
	 * @param entitySet
	 *            the entity set
	 * @param queryInfo
	 *            the query info
	 * @return a possible empty list of entities
	 */
	public List<OEntity> getEntities(final EdmEntitySet entitySet, final QueryInfo queryInfo);

	/**
	 * Returns the namespace of this OData service
	 * 
	 * @return the namespace
	 */
	public String getNamespace();

	/**
	 * Returns the entity id of an entity
	 * 
	 * @param entityName
	 *            the entity name
	 * @return the id and never <code>null</code>
	 * @throws TopicMapsODataException
	 *             thrown if id is missed
	 */
	public String getEntityId(final String entityName) throws TopicMapsODataException;

	/**
	 * Returns the property identifier of an property
	 * 
	 * @param property
	 *            the property
	 * @return the identifier and never <code>null</code>
	 * @throws TopicMapsODataException
	 *             thrown if property is missed
	 */
	public IEdmPropertyIdentifier getPropertyType(final String property) throws TopicMapsODataException;

	/**
	 * Returns all entities for the given entity set and the navigation property
	 * 
	 * @param entitySet
	 *            the entity set
	 * @param entityId
	 *            the Id of left hand navigation node
	 * @param navigationPropertyId
	 *            the association type
	 * @param info
	 *            the query info
	 * @return the list of matching entities
	 */
	public List<OEntity> getEntities(final EdmEntitySet entitySet, final String entityId, final String navigationProperty, final QueryInfo info);

	/**
	 * Modify the mode of internal association mapping to {@link EContentProviderConfiguration#FLAT_ASSOCIATION} or
	 * {@link EContentProviderConfiguration#STRONG_ASSOCIATION}
	 * 
	 * @param cfg
	 *            the new mode {@link EContentProviderConfiguration#FLAT_ASSOCIATION} or
	 *            {@link EContentProviderConfiguration#STRONG_ASSOCIATION}
	 */
	public void configAssociationHandling(EContentProviderConfiguration cfg);
}
